package com.example.smartplanner.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.smartplanner.ui.home.HomeActivity
import com.example.smartplanner.ui.login.LoginActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: plug real auth state
        val loggedIn = true
        startActivity(Intent(this, if (loggedIn) HomeActivity::class.java else LoginActivity::class.java))
        finish()
    }
}
