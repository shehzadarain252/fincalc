package com.fincalc.app

import android.app.Application

/**
 * Application class. AdMob is NOT initialised here: MainActivity requests
 * UMP consent first (AdMobManager.initializeAfterConsent) and MobileAds is
 * only initialised once consent is determined — no ad request may fire
 * before that (v1.0.2 fix, 2026-11-16).
 */
class FinCalcApp : Application()
