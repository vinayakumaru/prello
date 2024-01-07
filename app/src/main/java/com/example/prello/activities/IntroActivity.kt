package com.example.prello.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.prello.R
import com.google.firebase.auth.FirebaseAuth

class IntroActivity : BaseActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        findViewById<Button>(R.id.btn_sign_up_intro).setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        findViewById<Button>(R.id.btn_sign_in_intro).setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }
}