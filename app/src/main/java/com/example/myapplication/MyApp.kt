package com.example.myapplication

import android.app.Application
import com.yandex.mapkit.MapKitFactory

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Инициализация MapKit
        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
        MapKitFactory.initialize(this)
    }
}
