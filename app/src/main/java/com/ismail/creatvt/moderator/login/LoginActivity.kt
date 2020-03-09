package com.ismail.creatvt.moderator.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.facebook.CallbackManager
import com.google.firebase.auth.FirebaseAuth
import com.ismail.creatvt.moderator.BaseActivity
import com.ismail.creatvt.moderator.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity(), AuthManager.LoginCallback {
    private var callbackManager: CallbackManager = CallbackManager.Factory.create()
    private var firebaseAuth:FirebaseAuth?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firebaseAuth = FirebaseAuth.getInstance()
        callbackManager = CallbackManager.Factory.create()

        google_login_button.setOnClickListener {
            AuthManager.startGoogleLogin(this)
        }

        facebook_login_button.setOnClickListener {
            AuthManager.startFacebookLogin(this, callbackManager, this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager.onActivityResult(requestCode, resultCode, data)
        AuthManager.handleResult(this, requestCode, data, this)

    }

    override fun onLoginSuccess() {
        Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()
    }

    override fun onLoginFailed() {
        Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
    }

}
