package com.example.prello.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.prello.activities.*
import com.example.prello.models.Board
import com.example.prello.models.User
import com.example.prello.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FireStoreClass {
    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: User){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID()).set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }.addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error writing document", e)
            }
    }

    fun createBoard(activity: CreateBoardActivity,board: Board){
        mFireStore.collection(Constants.BOARDS)
            .document()
            .set(board, SetOptions.merge())
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName,"Board Created successfully.")
                Toast.makeText(activity,"Board Created successfully.",Toast.LENGTH_SHORT).show()
                activity.boardCreatedSuccessfully()
            }.addOnFailureListener{
                exception ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating a board.",
                    exception
                )
            }
    }

    fun loadUserData(activity: Activity){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener {
                val loggedInUser = it?.toObject(User::class.java)
                when(activity){
                    is SignInActivity -> {
                        activity.signInSuccess(loggedInUser ?: User())
                    }
                    is MainActivity -> {
                        activity.updateNavigationUserDetails(loggedInUser ?: User())
                    }
                    is MyProfileActivity -> {
                        activity.setUserDataInUI(loggedInUser ?: User())
                    }
                }
            }.addOnFailureListener { e ->
                if (activity is SignInActivity)
                    activity.hideProgressDialog()
                Log.e("SignInUser","Error reading document", e)
            }
    }

    fun updateUserProfileData(activity: MyProfileActivity,userHashMap: HashMap<String,Any>){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName,"document updated successfully")
                Toast.makeText(activity,"Profile updated successfully",Toast.LENGTH_SHORT).show()
                activity.profileUpdateSuccess()
            }.addOnFailureListener {
                e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error updating document", e)
            }

    }
    companion object {
        fun getCurrentUserID(): String{
            val currentUser = FirebaseAuth.getInstance().currentUser
            var currentUserID = ""
            if(currentUser != null) {
                currentUserID = currentUser.uid
            }
            return currentUserID
        }
    }
}