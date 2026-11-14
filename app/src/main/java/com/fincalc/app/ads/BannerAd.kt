package com.fincalc.app.ads

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

/**
 * LIVE banner ad (added 2026-11-14 at the user's request), shown above the
 * bottom navigation on the tab screens. Anchored adaptive banner sized to
 * the current screen width. If the banner fails to load (no fill, or the
 * unit belongs to a different AdMob app entry than the manifest App ID),
 * the space simply stays empty and the app keeps working.
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

    val adView = remember(bannerUnitId, adSize) {
        AdView(context).apply {
            setAdSize(adSize)
            // NOTE: the local must NOT be named adUnitId — inside apply{} a
            // local val would shadow the AdView property and the assignment
            // would fail to compile ("Val cannot be reassigned").
            adUnitId = bannerUnitId
            adListener = object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    // Banner stays hidden; retry on next composition.
                }
            }
            loadAd(AdRequest.Builder().build())
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
