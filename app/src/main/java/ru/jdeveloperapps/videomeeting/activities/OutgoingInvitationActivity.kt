package ru.jdeveloperapps.videomeeting.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import ru.jdeveloperapps.videomeeting.R
import ru.jdeveloperapps.videomeeting.databinding.ActivityOutgpingInvitationBinding
import ru.jdeveloperapps.videomeeting.models.User
import ru.jdeveloperapps.videomeeting.network.RetrofitInstance
import ru.jdeveloperapps.videomeeting.utilites.Constants
import ru.jdeveloperapps.videomeeting.utilites.PreferenceManager

class OutgoingInvitationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOutgpingInvitationBinding
    private lateinit var preferenceManager: PreferenceManager
    private var inviterToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOutgpingInvitationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferenceManager = PreferenceManager(applicationContext)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }

            // Get new FCM registration token
            inviterToken = task.result
        })

        val meetingType = intent.getStringExtra("type")

        meetingType?.let { type ->
            if (type == "video") {
                binding.imageMeetingType.setImageResource(R.drawable.ic_video)
            }
        }

        val user = intent.getSerializableExtra("user") as User
        user.let {
            binding.textFirsChar.text = String.format("%s", it.firstName.substring(0, 1))
            binding.textUserName.text = String.format("%s %s", it.firstName, it.lastName)
            binding.textEmail.text = it.email
        }

        binding.imageStopInvitation.setOnClickListener {
            onBackPressed()
        }

        meetingType?.let {
            initiateMeeting(it, user.token)
        }

    }

    private fun initiateMeeting(meetingType: String, receiverToken: String) {
        try {
            val tokens = JSONArray()
            tokens.put(receiverToken)

            val body = JSONObject()
            val data = JSONObject()

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION)
            data.put(Constants.REMOTE_MSG_MEETING_TYPE, meetingType)
            data.put(
                Constants.KEY_FIRST_NAME,
                preferenceManager.getString(Constants.KEY_FIRST_NAME)
            )
            data.put(Constants.KEY_LAST_NAME, preferenceManager.getString(Constants.KEY_LAST_NAME))
            data.put(Constants.KEY_EMAIL, preferenceManager.getString(Constants.KEY_EMAIL))
            data.put(Constants.REMOTE_MSG_INVITER_TOKEN, inviterToken)

            body.put(Constants.REMOTE_MSG_DATA, data)
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens)

            sendingRemoteMessage(body.toString(), Constants.REMOTE_MSG_INVITATION)

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
                if (type == Constants.REMOTE_MSG_INVITATION) {
                    Toast.makeText(
                        applicationContext,
                        "Invitation send successfully",
                        Toast.LENGTH_SHORT
                    ).show()
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