package ru.jdeveloperapps.videomeeting.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import ru.jdeveloperapps.videomeeting.databinding.ActivitySignInBinding
import ru.jdeveloperapps.videomeeting.utilites.Constants
import ru.jdeveloperapps.videomeeting.utilites.PreferenceManager

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferenceManager = PreferenceManager(applicationContext)

        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.textSignUp.setOnClickListener {
            Log.d("FCM", "test logging")
            startActivity(Intent(applicationContext, SignUpActivity::class.java))
        }

        binding.buttonSignIn.setOnClickListener {
            if (binding.inputEmail.text.toString().trim().isEmpty()) {
                Toast.makeText(this, "Input email address", Toast.LENGTH_SHORT).show()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString())
                    .matches()) {
                Toast.makeText(this, "Input valid email address", Toast.LENGTH_SHORT).show()
            } else if (binding.inputPassword.text.toString().trim().isEmpty()) {
                Toast.makeText(this, "Input password", Toast.LENGTH_SHORT).show()
            } else {
                signIn()
            }
        }

    }

    private fun signIn() {
        binding.buttonSignIn.visibility = View.INVISIBLE
        binding.signInProgressBar.visibility = View.VISIBLE
        val database = FirebaseFirestore.getInstance()
        database.collection(Constants.KEY_COLLECTIONS_USERS)
            .whereEqualTo(Constants.KEY_EMAIL, binding.inputEmail.text.toString())
            .whereEqualTo(Constants.KEY_PASSWORD, binding.inputPassword.text.toString())
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null
                    && task.result?.documents?.size!! > 0) {
                    val document = task.getResult()!!.documents[0]
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true)
                    preferenceManager.putString(Constants.KEY_FIRST_NAME,
                        document.getString(Constants.KEY_FIRST_NAME)!!)
                    preferenceManager.putString(Constants.KEY_LAST_NAME,
                        document.getString(Constants.KEY_LAST_NAME)!!)
                    preferenceManager.putString(Constants.KEY_EMAIL,
                        document.getString(Constants.KEY_EMAIL)!!)
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                } else {
                    binding.buttonSignIn.visibility = View.VISIBLE
                    binding.signInProgressBar.visibility = View.INVISIBLE
                    Toast.makeText(this, "Incorrect", Toast.LENGTH_SHORT).show()
                }
            }
    }
}