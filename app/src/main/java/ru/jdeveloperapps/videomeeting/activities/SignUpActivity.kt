package ru.jdeveloperapps.videomeeting.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.jdeveloperapps.videomeeting.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageBack.setOnClickListener { onBackPressed() }
        binding.textSignIn.setOnClickListener { onBackPressed() }
    }
}