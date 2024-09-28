package com.example.haechorom.mode1

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.haechorom.R
//import net.daum.mf.map.api.KakaoMapSdk

class Mode1Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // Kakao SDK 초기화
//        KakaoSdk.init(this, "{54fc29e3b7f909c70d60a15c5ad4897c}")
//
//        KakaoMapSdk.init(this, "54fc29e3b7f909c70d60a15c5ad4897c");

        enableEdgeToEdge()
        setContentView(R.layout.activity_mode1)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    // 뒤로가기를 눌렀을 때 앱 종료
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity() // 모든 액티비티 종료 (앱 종료)
    }
}