package com.trishala13kohad.myapplication

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import java.util.*

class ScheduleMeeting : BroadcastReceiver() {
    @SuppressLint("UnspecifiedImmutableFlag")
    @RequiresApi(Build.VERSION_CODES.M)
    fun scheduleMeeting(context: Context, time: Long, url: String?){
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        val pending =
            PendingIntent.getBroadcast(context,
                Calendar.getInstance().timeInMillis.toInt(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        // Schedule notification
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pending)
    }
    @SuppressLint("UnspecifiedImmutableFlag")
    fun cancelMeeting(context: Context, time: Long, url: String?) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.putExtra("url", url)
        val pending =
            PendingIntent.getBroadcast(context, Calendar.getInstance().
            timeInMillis.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
        // Cancel notification
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.cancel(pending)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onReceive(context: Context, intent: Intent) {
        PendingIntent.getActivity(context,Calendar.getInstance().
        timeInMillis.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}