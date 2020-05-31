package com.iseasoft.iseaiptv.services

import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.text.TextUtils
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.iseasoft.iseaiptv.Constants
import com.iseasoft.iseaiptv.R
import com.iseasoft.iseaiptv.ui.activity.SplashActivity
import java.text.SimpleDateFormat
import java.util.*

class IndiaTvFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Handle data payload of FCM messages.
        Log.d(TAG, "FCM Message Id: " + remoteMessage.messageId!!)
        Log.d(TAG, "FCM Notification Message: " + remoteMessage.notification!!)
        Log.d(TAG, "FCM Data Message: " + remoteMessage.data)

        try {
            val data = remoteMessage.data
            val intent = Intent(this, SplashActivity::class.java)

            val url = data[Constants.PUSH_URL_KEY]
            val message = remoteMessage.notification!!.body

            if (!TextUtils.isEmpty(url)) {
                intent.putExtra(Constants.PUSH_URL_KEY, url)
                intent.putExtra(Constants.PUSH_MESSAGE, message)
            }
            val contentIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            var title: String? = getString(R.string.app_name)
            if (!TextUtils.isEmpty(remoteMessage.notification!!.title)) {
                title = remoteMessage.notification!!.title
            }

            val notification = NotificationCompat.Builder(applicationContext)
                    .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_push))
                    .setSmallIcon(R.drawable.ic_push)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true).build()

            val now = Date()
            val id = Integer.parseInt(SimpleDateFormat("ddHHmmss", Locale.US).format(now))
            val manager = NotificationManagerCompat.from(applicationContext)
            manager.notify(id, notification)
        } catch (e: Exception) {

        }

    }

    companion object {

        private val TAG = "MessagingService"
    }
}
