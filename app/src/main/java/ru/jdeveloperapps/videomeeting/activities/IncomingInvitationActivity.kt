package ru.jdeveloperapps.videomeeting.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.json.JSONArray
import org.json.JSONObject
import ru.jdeveloperapps.videomeeting.R
import ru.jdeveloperapps.videomeeting.databinding.ActivityIncomingInvitationBinding
import ru.jdeveloperapps.videomeeting.network.RetrofitInstance
import ru.jdeveloperapps.videomeeting.utilites.Constants
import java.net.URL

class IncomingInvitationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIncomingInvitationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIncomingInvitationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val meetingType = intent.getStringExtra(Constants.REMOTE_MSG_MEETING_TYPE)

        meetingType?.let {
            if (it == "video") {
                binding.imageMeetingType.setImageDrawable(getDrawable(R.drawable.ic_video))
            }
        }

        val firstName = intent.getStringExtra(Constants.KEY_FIRST_NAME)

        firstName?.let {
            binding.textFirsChar.text = it.substring(0, 1)
        }

        binding.textUserName.text = String.format(
            "%s %s",
            firstName,
            intent.getStringExtra(Constants.KEY_LAST_NAME)
        )

        binding.textEmail.text = intent.getStringExtra(Constants.KEY_EMAIL)

        binding.imageAcceptInvitation.setOnClickListener {
            sendInvitationResponse(
                Constants.REMOTE_MSG_INVITATION_ACCEPTED,
                intent.getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN) ?: ""
            )
        }

        binding.imageCancelInvitation.setOnClickListener {
            sendInvitationResponse(
                Constants.REMOTE_MSG_INVITATION_REJECTED,
                intent.getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN) ?: ""
            )
        }

    }

    private fun sendInvitationResponse(type: String, receiverToken: String) {
        try {
            val tokens = JSONArray()
            tokens.put(receiverToken)

            val body = JSONObject()
            val data = JSONObject()

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION_RESPONSE)
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE, type)

            body.put(Constants.REMOTE_MSG_DATA, data)
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens)

            sendingRemoteMessage(body.toString(), type)
        } catch (e: Exception) {
            Toast.makeText(
                applicationContext,
                "${e.message} что то с отправкой",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
    }

    private fun sendingRemoteMessage(remoteMessageBody: String, type: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val response = RetrofitInstance.api.sendRemoteMessage(
                Constants.getRemoteMessageHeaders(),
                remoteMessageBody
            )
            if (response.isSuccessful) {
                if (type == Constants.REMOTE_MSG_INVITATION_ACCEPTED) {
                    try {
                        val serverURL = URL("https://meet.jit.si")
                        val conferenceOptions = JitsiMeetConferenceOptions.Builder()
                            .setServerURL(serverURL)
                            .setWelcomePageEnabled(false)
                            .setRoom(intent.getStringExtra(Constants.REMOTE_MSG_MEETING_ROOM))
                            .build()
                        JitsiMeetActivity.launch(applicationContext, conferenceOptions)
                        finish()
                    } catch (e: Exception) {
                        Toast.makeText(
                            applicationContext,
                            e.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Invitation rejected",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    response.message(),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }
}