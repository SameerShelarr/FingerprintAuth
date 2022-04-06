package com.example.fingerprintauth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fingerprintauth.databinding.ActivityAuthenticationCompleteBinding
import kotlin.jvm.java as java1

class AuthenticationCompleteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticationCompleteBinding
    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationCompleteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backToAuthActivityButton.setOnClickListener {
            startActivity(
                Intent(
                    this@AuthenticationCompleteActivity,
                    MainActivity::class.java1
                )
            )
            finish()
        }
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(
            this,
            "Please click BACK again to exit",
            Toast.LENGTH_SHORT
        ).show()
        Handler(Looper.getMainLooper()).postDelayed({
            doubleBackToExitPressedOnce = false
        }, 2000)
    }
}