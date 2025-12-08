package com.example.android_hw4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import android.widget.Button
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit
import android.widget.EditText
import android.widget.TextView
import java.util.UUID
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.os.Build



class MainActivity : ComponentActivity() {
    private lateinit var workManager: WorkManager
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private var workRequestId: UUID? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnCreateSchedule = findViewById<Button>(R.id.btn_create_schedule)
        val btnDeleteSchedule = findViewById<Button>(R.id.btn_delete_schedule)
        val inputReminder = findViewById<EditText>(R.id.input_text_reminder)
        val inputDelay = findViewById<EditText>(R.id.input_delay_time)
        val stateReminder = findViewById<TextView>(R.id.state_reminder)

        workManager = WorkManager.getInstance(this)

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                findViewById<TextView>(R.id.state_reminder)?.text = "State: There is permission"
            } else {
                findViewById<TextView>(R.id.state_reminder)?.text = "State: There is no permission"
            }
        }

        if (!checkPermission()) {
            requestPermission()
        }

        btnCreateSchedule.setOnClickListener {
            val title = inputReminder.text.toString().takeIf { it.isNotBlank() } ?: "Reminder"
            val delay = inputDelay.text.toString().toLongOrNull() ?: 10L

            if (checkPermission()) {
                createNotification(title, delay)
            }
        }

        btnDeleteSchedule.setOnClickListener {
            deleteNotification()
        }
    }

    private fun checkPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true
        }

        val state = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED

        return state
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
                findViewById<TextView>(R.id.state_reminder)?.text = "State: Permission is required to display notifications"
            }
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun createNotification(title : String, delay : Long) {
        if (!checkPermission()) {
            requestPermission()
            findViewById<TextView>(R.id.state_reminder)?.text = "State: Need permission first"
            return
        }

        val notificationRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delay, TimeUnit.SECONDS)
            .setInputData(workDataOf(
                "notification_title" to title,
                "notification_time" to delay.toInt()
            ))
            .addTag("reminder_tag")
            .build()

        workRequestId = notificationRequest.id
        workManager.enqueue(notificationRequest)

        findViewById<TextView>(R.id.state_reminder)?.text = "State: Scheduled for $delay seconds"
    }

    private fun deleteNotification() {
        (workRequestId)?.let { id ->
            workManager.cancelWorkById(id)
            workRequestId = null
            findViewById<TextView>(R.id.state_reminder)?.text = "State: cancle"
        } ?: run {
            findViewById<TextView>(R.id.state_reminder)?.text = "State: no notifications"
        }
    }
}


