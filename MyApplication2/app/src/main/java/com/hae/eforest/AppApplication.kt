package com.hae.eforest

import android.app.Application
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AppApplication: Application() {

    companion object {
        var ctx: AppApplication? = null
        var token: String = ""
    }

    override fun onCreate() {
        super.onCreate()
        ctx = this

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("AppApplication", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.w("AppApplication", "Fetching FCM token : ${token}", task.exception)

        })
    }
}