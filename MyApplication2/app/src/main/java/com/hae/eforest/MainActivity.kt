package com.hae.eforest

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.Constants.TAG
import com.google.firebase.messaging.FirebaseMessaging
import com.hae.eforest.databinding.ActivityMainBinding
import com.hae.eforest.java.AppTokenJavaExample
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var repository: RemoteRepository

    @Inject
    lateinit var sumSubrepository: SumSubRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        getToken()
    }

    fun getToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(object : OnCompleteListener<String?> {
                override fun onComplete(task: Task<String?>) {
                    if (!task.isSuccessful) {
                        Log.w(TAG, "토큰 생성 실패", task.exception)
                        return
                    }
                    // 새로운 토큰 생성 성공 시
                    task.result?.let {
                        Log.d("MessageToken", it)
                        AppApplication.token = it
                        binding.etToken.setText(it)
                    }
                }
            })

        binding.etTitle.setText("테스트타이틀")
        binding.etContent.setText("테스트본문")
        binding.btnSend.setOnClickListener {
            sendFCM()
        }
    }

    fun sendFCM() {
        lifecycle.coroutineScope.launch(Dispatchers.IO) {
            sumSubrepository.accessToken(
                RequestAccessTokenParam(
                    "basic-kyc-level",
                    "msdk2-demo-android-436b6959-923f-4ad4-9288-abf22850a175"
                )
            )
            AppTokenJavaExample.getToken()
//            repository.sendFCM(
//                RequestFCMParam(binding.etToken.text.toString(), RequestFCMParam.FCMData(
//                    binding.etTitle.text.toString(),
//                    binding.etContent.text.toString()
//
//                ))
//            )
        }
    }
}