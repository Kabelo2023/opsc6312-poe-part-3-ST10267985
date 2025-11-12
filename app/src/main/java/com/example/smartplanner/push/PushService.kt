package com.example.smartplanner.push

import androidx.core.app.NotificationManagerCompat
import com.example.smartplanner.feature.Notifier
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PushService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        // You could POST this token to your backend for targeted pushes
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title ?: "SmartPlanner"
        val body = message.notification?.body ?: "Update"
        val note = Notifier.build(applicationContext, title, body)
        NotificationManagerCompat.from(applicationContext)
            .notify(title.hashCode(), note)
    }
}
