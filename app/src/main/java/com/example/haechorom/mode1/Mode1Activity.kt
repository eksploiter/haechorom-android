package com.example.haechorom.mode1

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.haechorom.R
import com.example.haechorom.databinding.ActivityMode1Binding
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.camera.CameraPosition
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.camera.CameraAnimation
import android.location.LocationManager
import android.content.Context

class Mode1Activity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private var kakaoMap: KakaoMap? = null
    private lateinit var binding: ActivityMode1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mode1)

        // MapView 초기화
        mapView = findViewById(R.id.map_view)

        // ViewBinding 초기화
        binding = ActivityMode1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // 버튼 클릭 시 Invest1Activity로 이동
        binding.button.setOnClickListener {
            val intent = Intent(this@Mode1Activity, Invest1Activity::class.java)
            startActivity(intent)
        }

        // 위치 권한 요청
        checkLocationPermission()

        // KakaoMapReadyCallback 설정
        binding.mapView.start(object : KakaoMapReadyCallback() {
            override fun onMapReady(kakaoMap: KakaoMap) {
                // KakaoMap 객체 저장
                this@Mode1Activity.kakaoMap = kakaoMap

                // 내 위치를 기반으로 카메라를 이동시킴
                moveToCurrentLocation(kakaoMap)

                // 줌 레벨을 조정해 초기 줌이 너무 크지 않도록 설정
                val cameraUpdate = CameraUpdateFactory.zoomTo(10) // 적절한 줌 레벨로 설정
                kakaoMap.moveCamera(cameraUpdate)

                // 카메라 이동 애니메이션
                kakaoMap.moveCamera(CameraUpdateFactory.tiltTo(Math.toRadians(30.0)), CameraAnimation.from(500, true, true))
                kakaoMap.moveCamera(CameraUpdateFactory.rotateTo(Math.toRadians(45.0)), CameraAnimation.from(500, true, true))

                // 카메라 이동 완료 후 처리
                kakaoMap.setOnCameraMoveEndListener { _, cameraPosition, _ ->
                    Log.d(TAG, "Camera Position after move: $cameraPosition")
                }
            }
        })
    }

    // 위치 권한 확인
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1000)
        }
    }

    // 내 위치로 카메라 이동
    private fun moveToCurrentLocation(kakaoMap: KakaoMap) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val location: Location? = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

            location?.let {
                val myLocation = LatLng.from(it.latitude, it.longitude)
                val cameraPosition = CameraPosition.from(
                    myLocation.latitude,
                    myLocation.longitude,
                    10, // 적당한 줌 레벨
                    0.0, // 기울기 없음
                    0.0, // 회전 없음
                    0.0 // 높이
                )
                kakaoMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.resume() // 지도 라이프사이클 resume() 호출
        mapView.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                Log.e(TAG, "onMapDestroy")
            }

            override fun onMapError(error: Exception?) {
                Log.e(TAG, "onMapError", error)
            }

        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(kakaoMap: KakaoMap) {
                this@Mode1Activity.kakaoMap = kakaoMap
                Log.e(TAG, "onMapReady")

                // 내 위치로 카메라 이동
                moveToCurrentLocation(kakaoMap)
            }
        })
    }

    override fun onPause() {
        super.onPause()
        mapView.pause() // 지도 라이프사이클 pause() 호출
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.finish() // 명시적으로 지도 종료
    }

    // 뒤로가기를 눌렀을 때 앱 종료
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity() // 모든 액티비티 종료 (앱 종료)
    }
}
