package ru.jdeveloperapps.videomeeting.firebase

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ru.jdeveloperapps.videomeeting.activities.IncomingInvitationActivity
import ru.jdeveloperapps.videomeeting.utilites.Constants

class MessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val type = remoteMessage.data.get(Constants.REMOTE_MSG_TYPE)

        type?.let {
            if (it == Constants.REMOTE_MSG_INVITATION) {
                val intent = Intent(applicationContext, IncomingInvitationActivity::class.java)
                intent.apply {
                    putExtra(
                        Constants.REMOTE_MSG_MEETING_TYPE,
                        remoteMessage.data.get(Constants.REMOTE_MSG_MEETING_TYPE)
                    )
                    putExtra(
                        Constants.KEY_FIRST_NAME,
                        remoteMessage.data.get(Constants.KEY_FIRST_NAME)
                    )
                    putExtra(
                        Constants.KEY_LAST_NAME,
                        remoteMessage.data.get(Constants.KEY_LAST_NAME)
                    )
                    putExtra(
                        Constants.KEY_EMAIL,
                        remoteMessage.data.get(Constants.KEY_EMAIL)
                    )
                    putExtra(
                        Constants.REMOTE_MSG_INVITER_TOKEN,
                        remoteMessage.data.get(Constants.REMOTE_MSG_INVITER_TOKEN)
                    )
                    putExtra(
                        Constants.REMOTE_MSG_MEETING_ROOM,
                        remoteMessage.data.get(Constants.REMOTE_MSG_MEETING_ROOM)
                    )
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(intent)
            }
            if (it == Constants.REMOTE_MSG_INVITATION_RESPONSE) {
                val intent = Intent(Constants.REMOTE_MSG_INVITATION_RESPONSE)
                intent.putExtra(
                    Constants.REMOTE_MSG_INVITATION_RESPONSE,
                    remoteMessage.data.get(Constants.REMOTE_MSG_INVITATION_RESPONSE)
                )
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
            }
        }
    }

}