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
        // Build notification based on Intent

        val notification: Notification = NotificationCompat
            .Builder(context, channelId)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(intent.getStringExtra("title"))
            .setContentText(intent.getStringExtra("text"))
            .setDefaults(NotificationCompat.DEFAULT_SOUND)
            .build()
        // Show notification
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
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
            PendingIntent.getBroadcast(context, Calendar.getInstance().
            timeInMillis.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
        // Cancel notification
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.cancel(pending)
    }
//    fun createNotification(title: String?, body: String?) {
//        val NOTIFY_ID = 1002
//
//        // There are hardcoding only for show it's just strings
//        val name = "my_package_channel"
//        val id = "my_package_channel_1" // The user-visible name of the channel.
//        val description = "my_package_first_channel" // The user-visible description of the channel.
//        val intent: Intent
//        val pendingIntent: PendingIntent
//        val builder: NotificationCompat.Builder
//        var notifManager: NotificationManager? = null
//        if (notifManager == null) {
//            notifManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val importance = NotificationManager.IMPORTANCE_HIGH
//            var mChannel: NotificationChannel = notifManager.getNotificationChannel(id)
//            if (mChannel == null) {
//                mChannel = NotificationChannel(id, name, importance)
//                mChannel.description = description
//                mChannel.enableVibration(true)
//                mChannel.lightColor = Color.GREEN
//                mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
//                notifManager.createNotificationChannel(mChannel)
//            }
//            builder = NotificationCompat.Builder(this, id)
//            intent = Intent(this, MainActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
//            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
//            builder.setContentTitle(title) // required
//                .setSmallIcon(android.R.drawable.ic_popup_reminder) // required
//                .setContentText(body) // required
//                .setDefaults(Notification.DEFAULT_ALL)
//                .setAutoCancel(true)
//                .setContentIntent(pendingIntent)
//                .setTicker(title)
//                .setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
//        } else {
//            builder = NotificationCompat.Builder(this)
//            intent = Intent(this, MainActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
//            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
//            builder.setContentTitle(title) // required
//                .setSmallIcon(android.R.drawable.ic_popup_reminder) // required
//                .setContentText(body) // required
//                .setDefaults(Notification.DEFAULT_ALL)
//                .setAutoCancel(true)
//                .setContentIntent(pendingIntent)
//                .setTicker(title)
//                .setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)).priority =
//                Notification.PRIORITY_HIGH
//        }
//        val notification = builder.build()
//        notifManager.notify(NOTIFY_ID, notification)
//    }
}