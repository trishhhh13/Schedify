package com.trishala13kohad.myapplication

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import java.util.*


class NotificationReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        val channelId = "${context.packageName}-${context.getString(R.string.app_name)}"

        val name = "my_package_channel"
        val description = "my_package_first_channel"// The user-visible description of the channel.
        val builder: NotificationCompat.Builder
        val notifManager =  context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            builder = NotificationCompat.Builder(context, channelId)
            val mChannel = NotificationChannel(channelId, name, importance)
                mChannel.description = description
                mChannel.enableVibration(true)
                mChannel.lightColor = Color.GREEN
                mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            notifManager.createNotificationChannel(mChannel)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            builder.setContentTitle(intent.getStringExtra("title")) // required
                .setSmallIcon(R.drawable.notification_icon) // required
                .setContentText(intent.getStringExtra("text")) // required
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                .setAutoCancel(true)
                .build()
        } else {
            builder = NotificationCompat.Builder(context, channelId)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            builder.setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(intent.getStringExtra("title"))
                .setContentText(intent.getStringExtra("text"))
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                .build() // required

        }
        // Show notification
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        manager.notify((Calendar.getInstance().timeInMillis%10000).toInt(), builder.build())

    }

    @SuppressLint("UnspecifiedImmutableFlag")
    @RequiresApi(Build.VERSION_CODES.M)
    fun scheduleNotification(context: Context, time: Long, title: String?,
                             text: String?, eventId: Int) {
        val intent = Intent(context, NotificationReceiver::class.java)
        intent.putExtra("title", title)
        intent.putExtra("text", text)
        val pending =
            PendingIntent.getBroadcast(context,
                eventId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        // Schedule notification
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pending)
    }
    @SuppressLint("UnspecifiedImmutableFlag")
    fun cancelNotification(context: Context, title: String?, text: String?, eventId: Int) {
        val intent = Intent(context, NotificationReceiver::class.java)
        intent.putExtra("title", title)
        intent.putExtra("text", text)
        intent.putExtra("eventId", eventId)
        val pending =
            PendingIntent.getBroadcast(context, eventId,
                intent, PendingIntent.FLAG_UPDATE_CURRENT)
        // Cancel notification
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.cancel(pending)
    }
}