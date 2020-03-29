package com.ismail.creatvt.moderator.login

import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.ismail.creatvt.moderator.R


class AuthManager {

    companion object{
        private val TAG = AuthManager::class.java.simpleName

        private const val GOOGLE_SIGN_IN = 21

        fun startGoogleLogin(activity:AppCompatActivity){
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build()
            val googleSignInClient = GoogleSignIn.getClient(activity, gso)
            activity.startActivityForResult(googleSignInClient.signInIntent, GOOGLE_SIGN_IN)
        }

        fun startFacebookLogin(
            activity: AppCompatActivity,
            callbackManager: CallbackManager,
            loginCallback: LoginCallback
        ){
            val loginManager = LoginManager.getInstance()
            loginManager.logInWithReadPermissions(activity, listOf("email","public_profile"))

            loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    handleFacebookAccessToken(activity, loginResult.accessToken, loginCallback)
                }

                override fun onCancel() {
                    loginCallback.onLoginFailed()
                }

                override fun onError(exception: FacebookException) {
                    loginCallback.onLoginFailed()
                }
            })
        }

        fun handleResult(activity: AppCompatActivity, requestCode: Int, data: Intent?, loginCallback:LoginCallback) {
            if(requestCode == GOOGLE_SIGN_IN){
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    if(account != null){
                        firebaseAuthWithGoogle(activity, account, loginCallback)
                    } else{
                        loginCallback.onLoginFailed()
                    }
                } catch (e: ApiException) {
                    Log.w(TAG, "Google sign in failed", e)
                    loginCallback.onLoginFailed()
                }
            }
        }

        private fun firebaseAuthWithGoogle(activity: AppCompatActivity, acct: GoogleSignInAccount, callback: LoginCallback) {
            Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)
            val auth = FirebaseAuth.getInstance()
            val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener(activity) { task: Task<AuthResult> ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInWithCredential:success")
                        auth.currentUser
                        callback.onLoginSuccess()
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        callback.onLoginFailed()
                    }

                }
        }

        private fun handleFacebookAccessToken(activity: AppCompatActivity, token: AccessToken, loginCallback: LoginCallback) {
            Log.d(TAG, "handleFacebookAccessToken:$token")

            val auth = FirebaseAuth.getInstance()
            val credential = FacebookAuthProvider.getCredential(token.token)
            auth.signInWithCredential(credential)
                .addOnCompleteListener(activity) { task:Task<AuthResult> ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        auth.currentUser?.uid
                        loginCallback.onLoginSuccess()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        loginCallback.onLoginFailed()
                    }
                }
        }
    }

    interface LoginCallback{
        fun onLoginSuccess()
        fun onLoginFailed()
    }
}