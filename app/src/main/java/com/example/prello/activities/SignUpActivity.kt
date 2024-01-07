package com.example.prello.activities

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.core.view.WindowInsetsControllerCompat
import com.example.prello.R
import com.example.prello.databinding.ActivitySignUpBinding
import com.example.prello.firebase.FireStoreClass
import com.example.prello.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase

class SignUpActivity : BaseActivity() {
    lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        setupActionBar()
    }

    private fun setupActionBar(){
        setSupportActionBar(binding.toolbarSignUpActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        binding.toolbarSignUpActivity.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.btnSignUp.setOnClickListener{
            registerUser()
        }
    }

    private fun registerUser(){
        val name: String = binding.etName.text.toString().trim{ it <= ' ' }
        val email: String = binding.etEmail.text.toString().trim{ it <= ' ' }
        val password: String = binding.etPassword.text.toString().trim{ it <= ' ' }

        if(validateForm(name,email,password)){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this) {
                    task ->
                    if(task.isSuccessful){
                        val firebaseUser : FirebaseUser = task.result!!.user!!
                        val registeredEmail  = firebaseUser.email!!
                        val user = User(firebaseUser.uid,name,registeredEmail)
                        FireStoreClass().registerUser(this,user)
                    }else{
                        hideProgressDialog()
                        Toast.makeText(
                            this,
                            "Registration failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
    private fun validateForm(name: String, email: String, password: String) : Boolean{
        return when {
            TextUtils.isEmpty(name)->{
                showErrorSnackBar("Please enter a name")
                false
            }
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

    fun userRegisteredSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this,
            "You have successfully registered",
            Toast.LENGTH_SHORT
        ).show()
        FirebaseAuth.getInstance().signOut()
        finish()
    }
}