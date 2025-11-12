package com.example.smartplanner.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.smartplanner.databinding.ActivityLoginBinding
import com.example.smartplanner.ui.register.RegisterActivity
import com.example.smartplanner.ui.home.HomeActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val auth by lazy { FirebaseAuth.getInstance() }
    private lateinit var googleClient: GoogleSignInClient

    private val googleLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        runCatching { task.result }.onSuccess { account ->
            val token = account.idToken
            if (token == null) {
                toast("Google token missing")
                return@onSuccess
            }
            val cred = GoogleAuthProvider.getCredential(token, null)
            auth.signInWithCredential(cred)
                .addOnSuccessListener { goHome() }
                .addOnFailureListener { toast(it.localizedMessage ?: "Google sign-in failed") }
        }.onFailure {
            toast("Google sign-in cancelled")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // If already logged in, skip
        auth.currentUser?.let { goHome(); return }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Email / password login
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPassword.text.toString()
            if (email.isEmpty() || pass.isEmpty()) {
                toast("Email & password required"); return@setOnClickListener
            }
            auth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener { goHome() }
                .addOnFailureListener { toast(it.localizedMessage ?: "Login failed") }
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Google SSO
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(com.example.smartplanner.R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleClient = GoogleSignIn.getClient(this, gso)

        binding.btnGoogle.setOnClickListener {
            googleLauncher.launch(googleClient.signInIntent)
        }
    }

    private fun goHome() {
        Log.i("Login", "Signed in as ${auth.currentUser?.uid}")
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
