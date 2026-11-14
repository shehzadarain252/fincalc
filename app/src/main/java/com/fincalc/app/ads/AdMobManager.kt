package com.fincalc.app.ads

import android.app.Activity
import android.content.Context
import com.fincalc.app.data.SettingsStore
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Google AdMob — exact schedule from the master prompt (§11).
 *
 * - BANNER (added 2026-11-14 at the user's request): anchored adaptive
 *   banner above the bottom navigation on the tab screens.
 * - NO native ads (no Native Ad Unit ID was supplied; never reuse the
 *   interstitial/rewarded/banner IDs for native).
 * - INTERSTITIAL: only after FIVE completed calculations, shown at a natural
 *   navigation transition (never while the user is entering numbers or
 *   before a result is displayed). After a shown ad: counter resets,
 *   3-minute cooldown, then five more calculations before the next one.
 *   Maximum 3 interstitials per 30-minute session.
 * - REWARDED: only when the user voluntarily taps "Watch Ad to Unlock"
 *   (PDF export / advanced report). Reward is given ONLY after the SDK
 *   confirms successful completion.
 * - Ad Unit IDs live in this one central file. Never show them in the UI.
 * - LIVE ads in all builds (user request, 2026-11-14): the production
 *   interstitial/rewarded IDs are used even in debug builds. Do NOT tap
 *   your own live ads while testing — AdMob treats it as invalid traffic.
 *
 * App ID note: the AndroidManifest uses the App ID candidate that is NOT
 * the one registered for EduMind AI. The user must confirm in AdMob that
 * it belongs to FinCalc before a production release; the other candidate
 * is recorded below as reference only.
 */
object AdMobManager {

    // Production Ad Unit IDs (supplied in the FinCalc master prompt).
    private const val INTERSTITIAL_ID = "ca-app-pub-4510714397469784/7398560475"
    private const val REWARDED_ID = "ca-app-pub-4510714397469784/2417696533"

    // Banner Ad Unit ID (supplied by the user, 2026-11-14).
    private const val BANNER_ID = "ca-app-pub-4510714397469784/1975439878"

    /** Banner unit for the BannerAd composable. */
    val bannerAdUnitId: String get() = BANNER_ID

    // Reference only: the second App ID candidate from the master prompt.
    // It is NOT used anywhere — see the manifest note.
    @Suppress("unused")
    private const val OTHER_APP_ID_CANDIDATE = "ca-app-pub-4510714397469784~5758175741"

    // LIVE ads in every build (user request 2026-11-14) — no test units.
    private val interstitialId get() = INTERSTITIAL_ID
    private val rewardedId get() = REWARDED_ID

    private const val CALCS_BEFORE_AD = 5
    private const val COOLDOWN_MS = 3 * 60 * 1000L
    private const val WINDOW_MS = 30 * 60 * 1000L
    private const val MAX_PER_WINDOW = 3

    private var interstitial: InterstitialAd? = null
    private var rewarded: RewardedAd? = null
    private var initialized = false

    fun init(context: Context) {
        if (initialized) return
        initialized = true
        MobileAds.initialize(context) {
            loadInterstitial(context)
            loadRewarded(context)
        }
    }

    // ---------- UMP consent (privacy, §11H) ----------

    fun requestConsent(activity: Activity, onDone: () -> Unit = {}) {
        val params = ConsentRequestParameters.Builder().build()
        val consentInfo = UserMessagingPlatform.getConsentInformation(activity)
        consentInfo.requestConsentInfoUpdate(
            activity, params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { _ -> onDone() }
            },
            { _ -> onDone() }
        )
    }

    fun canRequestAds(activity: Activity): Boolean {
        val info: ConsentInformation = UserMessagingPlatform.getConsentInformation(activity)
        return info.canRequestAds()
    }

    // ---------- Interstitial ----------

    private fun loadInterstitial(context: Context) {
        InterstitialAd.load(
            context, interstitialId, AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) { interstitial = ad }
                override fun onAdFailedToLoad(error: LoadAdError) { interstitial = null }
            }
        )
    }

    /** Call after every completed calculation. */
    fun onCalculationCompleted(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            SettingsStore(context).incrementCalcsSinceAd()
        }
    }

    /**
     * Show an interstitial ONLY if the user has completed 5 calculations
     * since the last ad, the 3-minute cooldown has passed, and fewer than
     * 3 ads were shown in the last 30 minutes. Call this at a natural
     * navigation transition (e.g. leaving a result screen), never while
     * the user is working in a calculator. [onFinished] always runs.
     */
    fun maybeShowInterstitial(activity: Activity, onFinished: () -> Unit) {
        val store = SettingsStore(activity)
        CoroutineScope(Dispatchers.IO).launch {
            val count = store.calcsSinceAd()
            val lastShown = store.lastInterstitialAt()
            val (windowStart, windowCount) = store.adWindow()
            val now = System.currentTimeMillis()
            val windowCountNow =
                if (now - windowStart > WINDOW_MS) 0 else windowCount
            val eligible = count >= CALCS_BEFORE_AD &&
                now - lastShown >= COOLDOWN_MS &&
                windowCountNow < MAX_PER_WINDOW
            val ad = interstitial
            if (!eligible || ad == null) {
                activity.runOnUiThread { onFinished() }
                return@launch
            }
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    CoroutineScope(Dispatchers.IO).launch {
                        store.resetCalcsSinceAd()
                        store.setLastInterstitialAt(System.currentTimeMillis())
                        val (ws, wc) = store.adWindow()
                        val wStart = if (now - ws > WINDOW_MS) now else ws
                        val wCount = if (now - ws > WINDOW_MS) 1 else wc + 1
                        store.setAdWindow(wStart, wCount)
                    }
                    loadInterstitial(activity)
                    onFinished()
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    loadInterstitial(activity)
                    onFinished()
                }
            }
            activity.runOnUiThread { ad.show(activity) }
        }
    }

    // ---------- Rewarded ----------

    private fun loadRewarded(context: Context) {
        RewardedAd.load(
            context, rewardedId, AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) { rewarded = ad }
                override fun onAdFailedToLoad(error: LoadAdError) { rewarded = null }
            }
        )
    }

    fun isRewardedReady(): Boolean = rewarded != null

    /**
     * Show a rewarded ad the user voluntarily requested. [onReward] runs
     * ONLY when the SDK confirms the user earned the reward.
     * [onClosed] runs when the ad closes without a reward.
     */
    fun showRewarded(activity: Activity, onReward: () -> Unit, onClosed: () -> Unit) {
        val ad = rewarded
        if (ad == null) {
            onClosed()
            loadRewarded(activity)
            return
        }
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                loadRewarded(activity)
                onClosed()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                loadRewarded(activity)
                onClosed()
            }
        }
        ad.show(activity) { _ -> onReward() }
    }
}
