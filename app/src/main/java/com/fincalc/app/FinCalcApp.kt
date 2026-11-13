package com.fincalc.app

import android.app.Application
import com.fincalc.app.ads.AdMobManager

/** Application class: initialises AdMob (and preloads the first ads). */
class FinCalcApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AdMobManager.init(this)
    }
}
