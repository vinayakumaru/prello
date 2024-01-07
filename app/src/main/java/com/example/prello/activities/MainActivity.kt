package com.example.prello.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.bumptech.glide.Glide
import com.example.prello.R
import com.example.prello.databinding.ActivityMainBinding
import com.example.prello.firebase.FireStoreClass
import com.example.prello.models.User
import com.example.prello.utils.Constants
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener{

    companion object{
        const val MY_PROFILE_REQUEST_CODE : Int = 1
    }

    private lateinit var mUserName: String
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        setupActionBar()
        binding.navView.setNavigationItemSelectedListener(this)
        binding.drawerLayout.findViewById<FloatingActionButton>(R.id.fab_create_board).setOnClickListener{
            intent = Intent(this,CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME,mUserName)
            startActivity(intent)
        }

        FireStoreClass().loadUserData(this)
    }

    private fun setupActionBar(){
        val toolBar = binding.appBarMainIncluded.toolbarMainActivity
        setSupportActionBar(toolBar)
        toolBar.setNavigationIcon(R.drawable.ic_baseline_menu_24)
        toolBar.setNavigationOnClickListener {
            // Toggle Drawer
            toggleDrawer()
        }

    }

    private fun toggleDrawer(){
        val drawer = binding.drawerLayout
        val menu = binding.navView
        if(drawer.isDrawerOpen(menu)){
            drawer.closeDrawer(menu)
        }else{
            drawer.openDrawer(menu)
        }
//        alternative method
//        if(drawer.isDrawerOpen(GravityCompat.START)){
//            drawer.closeDrawer(GravityCompat.START)
//        }else{
//            drawer.openDrawer(GravityCompat.START)
//        }
    }

    override fun onBackPressed() {
        val drawer = binding.drawerLayout
        val menu = binding.navView
        if(drawer.isDrawerOpen(menu)){
            drawer.closeDrawer(menu)
        }else{
            doubleBackToExit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == MY_PROFILE_REQUEST_CODE){
            FireStoreClass().loadUserData(this)
        }else{
            Log.e("Cancelled","Cancelled")
        }
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_my_profile -> {
                startActivityForResult(Intent(this,MyProfileActivity::class.java),
                    MY_PROFILE_REQUEST_CODE)
            }
            R.id.nav_sign_out -> {
                signOut()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun updateNavigationUserDetails(user: User) {
        mUserName = user.name
        if(user == User()){
            signOut()
        }else{
            val navigationHeader = binding.navView.getHeaderView(0)
            Glide
                .with(this)
                .load(user.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(navigationHeader.findViewById(R.id.iv_user_image))
            navigationHeader.findViewById<TextView>(R.id.tv_username).text = user.name
        }
    }

    private fun signOut(){
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this@MainActivity, IntroActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}