package com.example.sosotalot.data.firebase

import android.app.Activity
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

object FirebaseAuthManager {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signInAnonymously(onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        auth.signInAnonymously()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result?.user?.uid?.let { onSuccess(it) }
                } else {
                    onFailure(task.exception ?: Exception("匿名登录失败"))
                }
            }
    }

    fun getGoogleSignInClient(activity: Activity): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("YOUR_FIREBASE_WEB_CLIENT_ID")
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(activity, gso)
    }

    fun firebaseAuthWithGoogle(idToken: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result?.user?.uid?.let { onSuccess(it) }
                } else {
                    onFailure(task.exception ?: Exception("Google 登录失败"))
                }
            }
    }
}
