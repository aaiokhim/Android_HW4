package com.example.android_hw4

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


class NotificationWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        val title = inputData.getString("notification_title") ?: "Notification"
        val delay = inputData.getInt("notification_time", 5)

        if (!NotificationManagerCompat.from(applicationContext).areNotificationsEnabled()) {
            android.util.Log.d("NotificationWorker", "No notification permission")
        } else {
            notificationChannel()

            val notification = NotificationCompat.Builder(applicationContext, "reminder_channel")
                .setContentTitle(title)
                .setContentText("Delay: $delay seconds")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setAutoCancel(true)
                .build()

            with(NotificationManagerCompat.from(applicationContext)) {
                notify(NOTIFICATION_ID, notification)
            }
        }

        return Result.success()
    }

    private fun notificationChannel() {

        val channel = android.app.NotificationChannel(
            CHANNEL_ID,
            "Notification channel",
            android.app.NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Channel for reminder"
        }

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        notificationManager.createNotificationChannel(channel)

    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "reminder_channel"
        const val KEY_TITLE = "title"
        const val KEY_DELAY_SECONDS = "delay_seconds"
    }

}