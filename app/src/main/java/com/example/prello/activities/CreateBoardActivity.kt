package com.example.prello.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.marginBottom
import com.bumptech.glide.Glide
import com.example.prello.R
import com.example.prello.databinding.ActivityCreateBoardBinding
import com.example.prello.firebase.FireStoreClass
import com.example.prello.models.Board
import com.example.prello.utils.Constants
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.IOException

class CreateBoardActivity : BaseActivity() {
    private lateinit var binding: ActivityCreateBoardBinding
    private var mSelectedImageFileUri : Uri? = null
    private lateinit var mUserName: String
    private var mBoardImageURL : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        setupActionBar()

        if(intent.hasExtra(Constants.NAME)){
            mUserName = intent.getStringExtra(Constants.NAME).toString()
        }
        binding.ivBoardImage.setOnClickListener{
            if(ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }else{
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        binding.btnCreate.setOnClickListener {
            if(mSelectedImageFileUri != null){
                uploadBoardImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                createBoard()
            }
        }
    }

    private fun createBoard(){
        val assignedUsersArrayList: ArrayList<String> = ArrayList()
        assignedUsersArrayList.add(getCurrentUserID())

        val board = Board(
            binding.etBoardName.text.toString(),
            mBoardImageURL,
            mUserName,
            assignedUsersArrayList
        )

        FireStoreClass().createBoard(this,board)
    }

    private fun setupActionBar(){
        setSupportActionBar(binding.toolbarCreateBoardActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        supportActionBar?.title = resources.getString(R.string.create_board_title)
        binding.toolbarCreateBoardActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }else{
                val snackbar = Snackbar.make(
                    binding.root,
                    "Oops, you have denied the permission for storage. You can allow it from settings.",
                    Snackbar.LENGTH_SHORT
                )
                snackbar.animationMode = Snackbar.ANIMATION_MODE_SLIDE
//                snackbar.view.setBackgroundColor(Color.parseColor("#0C90F1"))
                snackbar.show()
//                Toast.makeText(
//                    this,
//                    "Oops, you have denied the permission for storage. You can allow it from settings.",
//                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data!!.data != null){
            mSelectedImageFileUri = data.data
            try{
                Glide
                    .with(this)
                    .load(mSelectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_board_place_holder)
                    .into(binding.ivBoardImage)
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

    private fun uploadBoardImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        if(mSelectedImageFileUri != null){
            val sRef: StorageReference =
                FirebaseStorage
                    .getInstance()
                    .reference
                    .child(
                        "BOARD_IMAGE" + System.currentTimeMillis()
                                + "." + Constants.getFileExtension(this,mSelectedImageFileUri))
            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                    taskSnapshot ->
                Log.e(
                    "Firebase Image URI",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        uri ->
                    Log.e("Downloadable Image Uri", uri.toString())
                    mBoardImageURL = uri.toString()
                    createBoard()
                }
            }.addOnFailureListener {
                    exception ->
                Toast.makeText(this,exception.message,Toast.LENGTH_SHORT).show()
                hideProgressDialog()
            }
        }
    }
    fun boardCreatedSuccessfully(){
        hideProgressDialog()
        finish()
    }
}