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

//Class to schedule and cancel pending notifications
class NotificationReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        val channelId = "${context.packageName}-${context.getString(R.string.app_name)}"

        val name = "schedify_channel"
        // The user-visible description of the channel.
        val description = "schedify_notification_channel"

        val builder: NotificationCompat.Builder

        val notificationManager =  context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //setting the importance of the notification
            val importance = NotificationManager.IMPORTANCE_HIGH

            //setting the notification builder with channel ID
            builder = NotificationCompat.Builder(context, channelId)

            //defining notification channel
            val mChannel = NotificationChannel(channelId, name, importance)
                mChannel.description = description
                mChannel.enableVibration(true)
                mChannel.lightColor = Color.GREEN
                mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)

            //creating notification channel
            notificationManager.createNotificationChannel(mChannel)

            //initializing notification intent flags
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

            //setting up title, text and other details of notification while building
            builder.setContentTitle(intent.getStringExtra("title"))
                .setSmallIcon(R.drawable.notification_icon)
                .setContentText(intent.getStringExtra("text"))
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                .setAutoCancel(true)
                .build()

        } else {

            //setting the notification builder with channel ID
            builder = NotificationCompat.Builder(context, channelId)

            //initializing notification intent flags
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

            //setting up title, text and other details of notification while building
            builder.setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(intent.getStringExtra("title"))
                .setContentText(intent.getStringExtra("text"))
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                .build()

        }
        // Show notification
        notificationManager.notify((Calendar.getInstance().timeInMillis%10000).toInt(),
            builder.build())
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    @RequiresApi(Build.VERSION_CODES.M)
    fun scheduleNotification(context: Context, time: Long, title: String?, text: String?,
                             eventId: Int) {//eventId for unique request code

        //creating notification intent
        val intent = Intent(context, NotificationReceiver::class.java)
        intent.putExtra("title", title)
        intent.putExtra("text", text)

        //Scheduling the defined intent through pending intent
        val pending = PendingIntent.getBroadcast(context, eventId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        // Sync notification pending intent with time
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pending)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun cancelNotification(context: Context, title: String?, text: String?, eventId: Int) {

        val intent = Intent(context, NotificationReceiver::class.java)
        intent.putExtra("title", title)
        intent.putExtra("text", text)
        intent.putExtra("eventId", eventId)

        //getting the pending intent
        val pending = PendingIntent.getBroadcast(context, eventId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        // Cancel notification
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.cancel(pending)
    }
}