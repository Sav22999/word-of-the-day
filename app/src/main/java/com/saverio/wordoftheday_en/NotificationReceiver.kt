package com.saverio.wordoftheday_en

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import java.util.*


class NotificationReceiver : BroadcastReceiver() {

    lateinit var title: String
    lateinit var text: String
    var notificationNumberParameter: Int = 0

    override fun onReceive(context: Context, intent: Intent) {
        title = intent.getStringExtra("title").toString()
        text = intent.getStringExtra("text").toString()
        notificationNumberParameter = intent.getIntExtra("notificationNumber", 0)
        sendNotification(context, title, text, true)
    }

    fun sendNotification(
        context: Context,
        title: String,
        message: String,
        autoCancel: Boolean = true
    ) {
        var notificationNumber = notificationNumberParameter
        var NOTIFICATION_CHANNEL_ID =
            "${context.packageName.replace(".", "_")}_notification_${notificationNumber}"
        var NOTIFICATION_CHANNEL_NAME = "${context.packageName}_notification".replace(".", "_")
        val notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                importance
            )
            notificationManager?.createNotificationChannel(notificationChannel)
        }

        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder =
            NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(autoCancel) //.setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

        val savedDate = context.getSharedPreferences(
            "lastNotificationDate",
            Context.MODE_PRIVATE
        ).getString("lastNotificationDate", "")

        val c = Calendar.getInstance()

        val currentDate =
            "${c.get(Calendar.YEAR)}-${c.get(Calendar.MONTH + 1)}-${c.get(Calendar.DAY_OF_MONTH)}"

        if (getPushNotifications(context)) {
            if (currentDate != savedDate) {
                notificationManager!!.notify(
                    notificationNumber,
                    notificationBuilder.build()
                )
            } else {
                //Notification already sent
            }
        } else {
            //Notifications disabled
        }

        setWordSawToday(context, currentDate)
    }

    fun getPushNotifications(context: Context): Boolean {
        return context.getSharedPreferences(
            "pushNotifications",
            Context.MODE_PRIVATE
        ).getBoolean("pushNotifications", true)
    }

    fun setWordSawToday(context: Context, currentDate: String) {
        context.getSharedPreferences("lastNotificationDate", Context.MODE_PRIVATE).edit()
            .putString("lastNotificationDate", currentDate).apply()
    }
}