package com.example.fingerprintauth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.fingerprintauth.databinding.ActivityMainBinding
import java.util.concurrent.Executor

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Automatically opening auth dialog when app is open
        showBiometricAuthDialog()

        binding.apply {
            binding.fingerprintButton.setOnClickListener {
                showBiometricAuthDialog()
            }
        }
    }

    private fun showBiometricAuthDialog() {
        if (canAuthenticate()) {
            executor = ContextCompat.getMainExecutor(this)
            biometricPrompt = BiometricPrompt(this, executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(
                        errorCode: Int,
                        errString: CharSequence
                    ) {
                        super.onAuthenticationError(errorCode, errString)
                        Log.d(TAG, "onAuthenticationError: Authentication error: $errString")
                    }

                    override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult
                    ) {
                        super.onAuthenticationSucceeded(result)
                        //Starting the auth complete activity
                        startActivity(
                            Intent(
                                this@MainActivity,
                                AuthenticationCompleteActivity::class.java
                            )
                        )
                        finish()
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        Toast.makeText(
                            applicationContext, "Authentication failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })

            promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for my app")
                .setSubtitle("Log in using your biometric credential")
                .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
                .build()

            biometricPrompt.authenticate(promptInfo)
        } else {
            Toast.makeText(
                this@MainActivity,
                "Biometric Authentication not supported!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun canAuthenticate(): Boolean {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Log.d(TAG, "App can authenticate using biometrics.")
                return true
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Log.e(TAG, "No biometric features available on this device.")
                return false
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Log.e(TAG, "Biometric features are currently unavailable.")
                return false
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Log.e(TAG, "Biometric security not setup by user")
                return false
            }
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                Log.e(TAG, "Security update require for using biometric features")
                return false
            }
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                Log.e(TAG, "Biometric features are not supported")
                return false
            }
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                Log.e(TAG, "Biometric features are unavailable because of unknown status")
                return false
            }
            else -> {
                Log.e(TAG, "Biometric features are unavailable because of unknown reason")
                return false
            }
        }
    }
}