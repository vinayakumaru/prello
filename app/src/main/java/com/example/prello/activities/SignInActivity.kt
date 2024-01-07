package com.example.prello.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.core.view.WindowInsetsControllerCompat
import com.example.prello.R
import com.example.prello.databinding.ActivitySignInBinding
import com.example.prello.firebase.FireStoreClass
import com.example.prello.models.User
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : BaseActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        setupActionBar()

        auth = FirebaseAuth.getInstance()

        binding.btnSignIn.setOnClickListener {
            signInRegisteredUser()
        }
    }

    private fun setupActionBar(){
        setSupportActionBar(binding.toolbarSignInActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        binding.toolbarSignInActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun signInRegisteredUser(){
        val email: String = binding.etEmail.text.toString().trim{ it <= ' ' }
        val password: String = binding.etPassword.text.toString().trim{ it <= ' ' }
        if(validateForm(email, password)){
            showProgressDialog(resources.getString(R.string.please_wait))
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        FireStoreClass().loadUserData(this)
                    } else {
                        hideProgressDialog()
                        Log.w("Sign In", "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
    private fun validateForm(email: String, password: String) : Boolean{
        return when {
            TextUtils.isEmpty(email)->{
                showErrorSnackBar("Please enter a email")
                false
            }
            TextUtils.isEmpty(password)->{
                showErrorSnackBar("Please enter a password")
                false
            }
            else->{
                true
            }
        }
    }

    fun signInSuccess(user: User) {
        hideProgressDialog()
//        Toast.makeText(this, user.toString(), Toast.LENGTH_SHORT).show()
        val intent = Intent(this,MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}