package com.example.prello.activities
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.prello.R
import com.example.prello.firebase.FireStoreClass
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val typeface : Typeface = Typeface.createFromAsset(assets,"RobotoMono-Bold.ttf")
        findViewById<TextView>(R.id.tv_app_name).typeface = typeface
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        Handler(Looper.getMainLooper()).postDelayed({
            val currentUserID = FireStoreClass.getCurrentUserID()
            if (currentUserID.isNotEmpty()) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
            }else{
                startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
                overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
            }
            finish()
        },1000)
    }
}