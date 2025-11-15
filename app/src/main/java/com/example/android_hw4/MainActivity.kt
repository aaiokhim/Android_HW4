package com.example.android_hw4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import android.widget.Button
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit
import com.example.android_hw4.NotificationWorker
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.android_hw4.ui.theme.Android_HW4Theme

class MainActivity : ComponentActivity() {
    private lateinit var workManager: WorkManager
    private var workId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnCreateSchedule = findViewById<Button>(R.id.btn_create_schedule)
        val btnDeleteSchedule = findViewById<Button>(R.id.btn_delete_schedule)
        val inputReminder = findViewById<Button>(R.id.input_text_reminder)
        val inputDelay = findViewById<Button>(R.id.input_delay_time)

        workManager = WorkManager.getInstance(this)


        btnCreateSchedule.setOnClickListener {
            val title = inputReminder.text.toString().takeIf { it.isNotBlank() } ?: "Reminder"
            val delay = inputDelay.text.toString().toLongOrNull() ?: 10L

            createNotification(title, delay)
        }

        btnDeleteSchedule.setOnClickListener {
            deleteNotification()
        }
    }

    private fun createNotification(title : String, delay : Long) {
        val notificationRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delay, TimeUnit.SECONDS)
            .setInputData(workDataOf(
                "title" to title,
                "message" to title
            ))
            .addTag("reminder_tag")
            .build()
        workId = notificationRequest.id.toString()
        workManager.enqueue(notificationRequest)


    }

    private fun deleteNotification() {

    }
}


