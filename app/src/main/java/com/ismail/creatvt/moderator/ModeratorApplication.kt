package com.ismail.creatvt.moderator

import android.app.Application
import com.google.firebase.FirebaseApp

class ModeratorApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
    }
}