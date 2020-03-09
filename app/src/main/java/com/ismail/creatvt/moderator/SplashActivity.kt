package com.ismail.creatvt.moderator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import com.ismail.creatvt.moderator.home.HomeActivity
import com.ismail.creatvt.moderator.login.LoginActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Handler().postDelayed({
            if(FirebaseAuth.getInstance().currentUser == null){
                startActivity(Intent(this, LoginActivity::class.java))
            } else{
                startActivity(Intent(this, HomeActivity::class.java))
            }
        },3000)
    }

}
