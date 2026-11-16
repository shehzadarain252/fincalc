package com.fincalc.app.ads

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import kotlinx.coroutines.delay

private const val TAG = "FinCalcAds"
private const val MAX_BANNER_RETRIES = 5

/**
 * LIVE banner ad (added 2026-11-14 at the user's request), shown above the
 * bottom navigation on the tab screens. Anchored adaptive banner sized to
 * the current screen width.
 *
 * v1.0.2 (2026-11-16): a failed load is now logged (filter logcat for
 * FinCalcAds) and retried with backoff instead of leaving the space empty
 * for the whole session. If the failure is a persistent no-fill (for
 * example, the unit belonging to a different AdMob app entry than the
 * manifest App ID), retries stop after [MAX_BANNER_RETRIES] attempts and
 * the banner stays hidden — the app keeps working.
 */
@Composable
fun BannerAd(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val bannerUnitId = AdMobManager.bannerAdUnitId

    val adSize = remember(configuration.screenWidthDp) {
        val width = configuration.screenWidthDp
        AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, width)
    }

    // Retry counter: bumped by the failure listener; the LaunchedEffect
    // below reloads the ad after a backoff delay. Reset on a successful
    // load so a later failure gets a fresh retry budget.
    var retryCount by remember { mutableIntStateOf(0) }

    val adView = remember(bannerUnitId, adSize) {
        AdView(context).apply {
            setAdSize(adSize)
            // NOTE: the local must NOT be named adUnitId — inside apply{} a
            // local val would shadow the AdView property and the assignment
            // would fail to compile ("Val cannot be reassigned").
            adUnitId = bannerUnitId
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    retryCount = 0
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.w(TAG, "Banner failed to load: code=${error.code} " +
                        "message=${error.message} domain=${error.domain}")
                    if (retryCount < MAX_BANNER_RETRIES) {
                        retryCount++
                    } else {
                        Log.w(TAG, "Banner retry limit reached ($MAX_BANNER_RETRIES attempts); " +
                            "banner stays hidden for this session")
                    }
                }
            }
            loadAd(AdRequest.Builder().build())
        }
    }

    // Retry with backoff: 10s, 20s, 40s, 80s, 120s (capped).
    LaunchedEffect(retryCount) {
        if (retryCount > 0) {
            val delayMs = minOf(10_000L * (1L shl (retryCount - 1)), 120_000L)
            Log.i(TAG, "Retrying banner load #$retryCount in ${delayMs}ms")
            delay(delayMs)
            adView.loadAd(AdRequest.Builder().build())
        }
    }

    DisposableEffect(adView) {
        adView.resume()
        onDispose {
            adView.pause()
            adView.destroy()
        }
    }

    AndroidView(
        factory = { adView },
        modifier = modifier.fillMaxWidth(),
    )
}
