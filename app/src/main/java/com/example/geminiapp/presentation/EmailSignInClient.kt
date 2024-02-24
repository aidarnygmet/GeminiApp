package com.example.geminiapp.presentation

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class EmailSignInClient {
    private val auth = Firebase.auth
    fun signIn(email: String, password: String, onSuccess: (SignInResult)->Unit, onFailure: (Exception?)->Unit){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("check", "signInWithEmail:success")
                    val user = auth.currentUser
                    val res = SignInResult(data = user?.run {
                        UserData(
                            userId = uid,
                            username = displayName,
                            profilePictureId = photoUrl?.toString()
                        )
                    }, errorMessage = null)
                    onSuccess(res)
                } else {
                    Log.w("check", "signInWithEmail:failure", task.exception)
                    onFailure(task.exception)
                }
            }
    }
    fun signUp(email: String, username: String, password: String, onSuccess: (SignInResult)->Unit, onFailure: (Exception?)->Unit){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("check", "createUserWithEmail:success")
                    val user = auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build()
                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val res = SignInResult(data = user?.run {
                                    UserData(
                                        userId = uid,
                                        username = displayName,
                                        profilePictureId = photoUrl?.toString()
                                    )
                                }, errorMessage = null)
                                onSuccess(res)
                            } else {
                                onFailure(task.exception)
                            }
                        }
                } else {
                    Log.w("check", "createUserWithEmail:failure", task.exception)
                    onFailure(task.exception)
                }
            }
    }
}