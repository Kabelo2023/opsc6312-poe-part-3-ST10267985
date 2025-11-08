package com.example.smartplanner.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartplanner.databinding.ActivityLoginBinding
import com.example.smartplanner.ui.home.HomeActivity
import com.example.smartplanner.ui.register.RegisterActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
            }

            binding = ActivityLoginBinding.inflate(layoutInflater)
            setContentView(binding.root)

            // Login button
            binding.btnLogin.setOnClickListener {
                val email = binding.etEmail.text.toString().trim()
                val password = binding.etPassword.text.toString()

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "Email and password required", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        Log.i("Login", "signIn success: ${it.user?.uid}")
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        Log.e("Login", "signIn failed", it)
                        Toast.makeText(this, it.localizedMessage ?: "Login failed", Toast.LENGTH_SHORT).show()
                    }
            }

            // Navigate to Register
            binding.tvGoRegister.setOnClickListener {
                startActivity(Intent(this, RegisterActivity::class.java))
            }
        } catch (e: Throwable) {
            Log.e("Login", "onCreate crash", e)
            Toast.makeText(this, e.message ?: "Login crashed", Toast.LENGTH_LONG).show()
        }
    }
}
