package ru.jdeveloperapps.videomeeting.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import ru.jdeveloperapps.videomeeting.databinding.ActivitySignUpBinding
import ru.jdeveloperapps.videomeeting.utilites.Constants
import ru.jdeveloperapps.videomeeting.utilites.PreferenceManager

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferenceManager = PreferenceManager(applicationContext)

        binding.imageBack.setOnClickListener { onBackPressed() }
        binding.textSignIn.setOnClickListener { onBackPressed() }

        binding.buttonSignUp.setOnClickListener {
            if (binding.inputFirstName.text.toString().trim().isEmpty()) {
                Toast.makeText(this, "Enter first name", Toast.LENGTH_SHORT).show()
            } else if (binding.inputLastName.text.trim().isEmpty()) {
                Toast.makeText(this, "Enter last name", Toast.LENGTH_SHORT).show()
            } else if (binding.inputEmail.text.trim().isEmpty()) {
                Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString())
                    .matches()
            ) {
                Toast.makeText(this, "Enter valid email", Toast.LENGTH_SHORT).show()
            } else if (binding.inputPassword.text.toString().trim().isEmpty()) {
                Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show()
            } else if (binding.inputConfirmPassword.text.toString().trim().isEmpty()) {
                Toast.makeText(this, "Confirm your password", Toast.LENGTH_SHORT).show()
            } else if (binding.inputPassword.text.toString().trim() != binding.inputConfirmPassword.text.toString()
                    .trim()
            ) {
                Toast.makeText(this, "Password & confirm password most be same", Toast.LENGTH_SHORT)
                    .show()
            } else {
                signUp()
            }
        }
    }

    private fun signUp() {
        binding.signUpProgressBar.visibility = View.VISIBLE
        binding.buttonSignUp.visibility = View.INVISIBLE

        val database = FirebaseFirestore.getInstance()
        val user = mapOf<String, Any>(
            Pair(Constants.KEY_FIRST_NAME, binding.inputFirstName.text.toString()),
            Pair(Constants.KEY_LAST_NAME, binding.inputLastName.text.toString()),
            Pair(Constants.KEY_EMAIL, binding.inputEmail.text.toString()),
            Pair(Constants.KEY_PASSWORD, binding.inputPassword.text.toString()),
        )

        database.collection(Constants.KEY_COLLECTIONS_USERS)
            .add(user)
            .addOnSuccessListener { documentPreference ->
                preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true)
                preferenceManager.putString(Constants.KEY_USER_ID, documentPreference.id)
                preferenceManager.putString(
                    Constants.KEY_FIRST_NAME,
                    binding.inputFirstName.text.toString()
                )
                preferenceManager.putString(
                    Constants.KEY_LAST_NAME,
                    binding.inputLastName.text.toString()
                )
                preferenceManager.putString(Constants.KEY_EMAIL, binding.inputEmail.text.toString())
                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                binding.buttonSignUp.visibility = View.VISIBLE
                binding.signUpProgressBar.visibility = View.INVISIBLE
                Toast.makeText(this, "error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}