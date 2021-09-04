package com.trishala13kohad.myapplication

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import java.util.*


class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val channelId = "${context.packageName}-${context.getString(R.string.app_name)}"
        // Build notification based on Intent
        val notification: Notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(intent.getStringExtra("title"))
            .setContentText(intent.getStringExtra("text"))
            .setDefaults(NotificationCompat.DEFAULT_SOUND)
            .build()
        // Show notification
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(Calendar.getInstance().timeInMillis.toInt(), notification)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    @RequiresApi(Build.VERSION_CODES.M)
    fun scheduleNotification(context: Context, time: Long, title: String?, text: String?) {
        val intent = Intent(context, NotificationReceiver::class.java)
        intent.putExtra("title", title)
        intent.putExtra("text", text)
        val pending =
            PendingIntent.getBroadcast(context,
                Calendar.getInstance().timeInMillis.toInt(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        // Schedule notification
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pending)
    }
    @SuppressLint("UnspecifiedImmutableFlag")
    fun cancelNotification(context: Context, title: String?, text: String?) {
        val intent = Intent(context, NotificationReceiver::class.java)
        intent.putExtra("title", title)
        intent.putExtra("text", text)
        val pending =
            PendingIntent.getBroadcast(context, Calendar.getInstance().timeInMillis.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
        // Cancel notification
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.cancel(pending)
    }
}