package com.fincalc.app.ads

import android.app.Activity
import android.content.Context
import android.util.Log
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
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
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
 * App ID note (v1.0.3, 2026-11-16): the AndroidManifest uses the App ID
 * of the FinCalc app entry created in AdMob on asadaliarain081@gmail.com,
 * and the three unit IDs below belong to that same app entry. The
 * EduMind AI App ID candidate is recorded below as reference only.
 *
 * Ad-load failures (v1.0.2, 2026-11-16): every load failure is now logged
 * with its error code/message (filter logcat for FinCalcAds) and retried
 * with backoff — a single failed load no longer means no ads for the
 * whole session. Consent is determined BEFORE MobileAds is initialised.
 */
object AdMobManager {

    private const val TAG = "FinCalcAds"

    // Production Ad Unit IDs — created fresh in AdMob under the FinCalc
    // app (asadaliarain081@gmail.com), 2026-11-16 (v1.0.3). All three
    // belong to the same FinCalc app entry as the App ID in the manifest.
    private const val INTERSTITIAL_ID = "ca-app-pub-4510714397469784/8671860101"
    private const val REWARDED_ID = "ca-app-pub-4510714397469784/4807977573"

    // Banner Ad Unit ID — created fresh in AdMob under the FinCalc app,
    // 2026-11-16 (v1.0.3). Replaces the banner unit supplied by the user
    // on 2026-11-16, which belonged to a different app entry.
    private const val BANNER_ID = "ca-app-pub-4510714397469784/3059781636"

    /** Banner unit for the BannerAd composable. */
    val bannerAdUnitId: String get() = BANNER_ID

    // Reference only: the EduMind AI App ID from the master prompt.
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

    // Retry with backoff after a load failure (v1.0.2): delays in ms.
    private val RETRY_DELAYS_MS = longArrayOf(5_000L, 15_000L, 30_000L, 60_000L, 120_000L)
    private const val MAX_RETRY_ATTEMPTS = 5

    private var interstitial: InterstitialAd? = null
    private var rewarded: RewardedAd? = null
    private var initialized = false
    private var appContext: Context? = null
    private var interstitialRetries = 0
    private var rewardedRetries = 0
    private var interstitialRetryPending = false
    private var rewardedRetryPending = false
    private val retryScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    /**
     * Consent-first entry point (v1.0.2). Call this from MainActivity
     * onCreate INSTEAD of init(): it requests the UMP consent info update,
     * shows the consent form if required, and only then initialises
     * MobileAds when ads may be requested. No ad request fires before
     * consent is determined. [onDone] always runs.
     */
    fun initializeAfterConsent(activity: Activity, onDone: () -> Unit = {}) {
        val params = ConsentRequestParameters.Builder().build()
        val consentInfo = UserMessagingPlatform.getConsentInformation(activity)
        consentInfo.requestConsentInfoUpdate(
            activity, params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { _ ->
                    if (consentInfo.canRequestAds()) {
                        Log.i(TAG, "Consent determined: ads allowed, initialising MobileAds")
                        init(activity)
                    } else {
                        Log.i(TAG, "Consent determined: ads NOT allowed, skipping MobileAds init")
                    }
                    onDone()
                }
            },
            { error ->
                // Consent info update failed (e.g. no network). Fall back to
                // the stored consent state: initialise only if ads may be
                // requested, and log the failure for diagnosis.
                Log.w(TAG, "Consent info update failed: code=${error.errorCode} message=${error.message}")
                if (consentInfo.canRequestAds()) init(activity)
                onDone()
            }
        )
    }

    fun init(context: Context) {
        if (initialized) return
        initialized = true
        appContext = context.applicationContext
        MobileAds.initialize(context) {
            Log.i(TAG, "MobileAds initialised")
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
        val ctx = appContext ?: context.applicationContext
        InterstitialAd.load(
            ctx, interstitialId, AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitial = ad
                    interstitialRetries = 0
                    Log.i(TAG, "Interstitial loaded")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitial = null
                    Log.w(TAG, "Interstitial failed to load: code=${error.code} " +
                        "message=${error.message} domain=${error.domain}")
                    scheduleInterstitialRetry(ctx)
                }
            }
        )
    }

    private fun scheduleInterstitialRetry(context: Context) {
        if (interstitialRetryPending) return
        if (interstitialRetries >= MAX_RETRY_ATTEMPTS) {
            Log.w(TAG, "Interstitial retry limit reached ($MAX_RETRY_ATTEMPTS attempts); " +
                "no further retries this session")
            return
        }
        interstitialRetryPending = true
        val delayMs = RETRY_DELAYS_MS[interstitialRetries.coerceIn(0, RETRY_DELAYS_MS.lastIndex)]
        interstitialRetries++
        Log.i(TAG, "Scheduling interstitial retry #${interstitialRetries} in ${delayMs}ms")
        retryScope.launch {
            delay(delayMs)
            interstitialRetryPending = false
            loadInterstitial(context)
        }
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
            if (!eligible) {
                Log.d(TAG, "Interstitial not eligible: calcs=$count lastShown=${now - lastShown}ms ago window=$windowCountNow")
                activity.runOnUiThread { onFinished() }
                return@launch
            }
            if (ad == null) {
                Log.w(TAG, "Interstitial eligible but no ad loaded (calcs=$count); retry is scheduled")
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
                    Log.w(TAG, "Interstitial failed to show: code=${error.code} message=${error.message}")
                    loadInterstitial(activity)
                    onFinished()
                }
            }
            activity.runOnUiThread { ad.show(activity) }
        }
    }

    // ---------- Rewarded ----------

    private fun loadRewarded(context: Context) {
        val ctx = appContext ?: context.applicationContext
        RewardedAd.load(
            ctx, rewardedId, AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewarded = ad
                    rewardedRetries = 0
                    Log.i(TAG, "Rewarded ad loaded")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewarded = null
                    Log.w(TAG, "Rewarded ad failed to load: code=${error.code} " +
                        "message=${error.message} domain=${error.domain}")
                    scheduleRewardedRetry(ctx)
                }
            }
        )
    }

    private fun scheduleRewardedRetry(context: Context) {
        if (rewardedRetryPending) return
        if (rewardedRetries >= MAX_RETRY_ATTEMPTS) {
            Log.w(TAG, "Rewarded retry limit reached ($MAX_RETRY_ATTEMPTS attempts); " +
                "no further retries this session")
            return
        }
        rewardedRetryPending = true
        val delayMs = RETRY_DELAYS_MS[rewardedRetries.coerceIn(0, RETRY_DELAYS_MS.lastIndex)]
        rewardedRetries++
        Log.i(TAG, "Scheduling rewarded retry #${rewardedRetries} in ${delayMs}ms")
        retryScope.launch {
            delay(delayMs)
            rewardedRetryPending = false
            loadRewarded(context)
        }
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
            Log.i(TAG, "Rewarded requested but no ad loaded; exporting without an ad")
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
                Log.w(TAG, "Rewarded ad failed to show: code=${error.code} message=${error.message}")
                loadRewarded(activity)
                onClosed()
            }
        }
        ad.show(activity) { _ -> onReward() }
    }
}
