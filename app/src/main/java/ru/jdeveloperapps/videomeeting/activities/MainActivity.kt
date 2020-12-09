package ru.jdeveloperapps.videomeeting.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import ru.jdeveloperapps.videomeeting.adapters.UsersAdapter
import ru.jdeveloperapps.videomeeting.databinding.ActivityMainBinding
import ru.jdeveloperapps.videomeeting.models.User
import ru.jdeveloperapps.videomeeting.utilites.Constants
import ru.jdeveloperapps.videomeeting.utilites.PreferenceManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var preferenceManager: PreferenceManager
    private val usersAdapter = UsersAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferenceManager = PreferenceManager(applicationContext)

        binding.textTitle.text = String.format(
            "%s %s",
            preferenceManager.getString(Constants.KEY_FIRST_NAME),
            preferenceManager.getString(Constants.KEY_LAST_NAME)
        )

        binding.textSignOut.setOnClickListener {
            signOut()
        }

        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
            if (task.isSuccessful && task.result != null) {
                sendFCMTokenToDatabase(task.result!!.token)
            }
        }

        binding.usersRecyclerView.adapter = usersAdapter
        getUsers()
    }

    private fun getUsers() {
        binding.userProgressBar.visibility = View.VISIBLE
        val database = FirebaseFirestore.getInstance()
        database.collection(Constants.KEY_COLLECTIONS_USERS)
            .get()
            .addOnCompleteListener { task ->
                binding.userProgressBar.visibility = View.GONE
                val myUserId = preferenceManager.getString(Constants.KEY_USER_ID)
                if (task.isSuccessful && task.getResult() != null) {
                    task.getResult()?.let {
                        val listUsers = mutableListOf<User>()
                        it.forEach { documentSnapshot ->
                            if (!myUserId.equals(documentSnapshot.id)) {
                                val user = User(
                                    firstName = documentSnapshot.getString(Constants.KEY_FIRST_NAME)!!,
                                    lastName = documentSnapshot.getString(Constants.KEY_LAST_NAME)!!,
                                    email = documentSnapshot.getString(Constants.KEY_EMAIL)!!,
                                    token = documentSnapshot.id
                                )
                                listUsers.add(user)
                            }
                        }
                        usersAdapter.differ.submitList(listUsers)
                    }
                } else {
                    binding.textErrorMessage.text = "No users avaiable"
                    binding.textErrorMessage.visibility = View.VISIBLE
                }
            }
    }

    private fun sendFCMTokenToDatabase(token: String) {
        val database = FirebaseFirestore.getInstance()
        val documentReference = database.collection(Constants.KEY_COLLECTIONS_USERS).document(
            preferenceManager.getString(Constants.KEY_USER_ID)!!
        )
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
            .addOnSuccessListener {
                Toast.makeText(applicationContext, "Token updated successfuly", Toast.LENGTH_SHORT)
                    .show()
            }
            .addOnFailureListener {
                Toast.makeText(
                    applicationContext,
                    "error update token: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun signOut() {
        Toast.makeText(applicationContext, "Sign out...", Toast.LENGTH_SHORT).show()
        val database = FirebaseFirestore.getInstance()
        val documentReference = database.collection(
            Constants.KEY_COLLECTIONS_USERS
        ).document(
            preferenceManager.getString(Constants.KEY_USER_ID)!!
        )
        val updates = hashMapOf<String, Any>(Pair(Constants.KEY_FCM_TOKEN, FieldValue.delete()))
        documentReference.update(updates)
            .addOnSuccessListener {
                preferenceManager.clearPreference()
                startActivity(Intent(applicationContext, SignInActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext, "Failure sign out", Toast.LENGTH_SHORT).show()
            }
    }
}