package com.fincalc.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * App settings + ad counters, stored locally with DataStore.
 * No account, no cloud sync — everything stays on the device.
 */
val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "fincalc_settings")

class SettingsStore(private val context: Context) {

    companion object {
        private val DARK_MODE = booleanPreferencesKey("dark_mode")
        private val CURRENCY = stringPreferencesKey("currency") // USD, EUR, GBP, PKR, INR...
        private val DECIMALS = intPreferencesKey("decimals") // 0..4
        private val CALCS_SINCE_AD = intPreferencesKey("calcs_since_ad")
        private val LAST_INTERSTITIAL_AT = longPreferencesKey("last_interstitial_at")
        private val AD_WINDOW_START = longPreferencesKey("ad_window_start")
        private val AD_WINDOW_COUNT = intPreferencesKey("ad_window_count")
        private val RATES_JSON = stringPreferencesKey("rates_json")
        private val RATES_UPDATED_AT = stringPreferencesKey("rates_updated_at")
        private val RATES_BASE = stringPreferencesKey("rates_base")
    }

    val darkMode: Flow<Boolean> = context.settingsDataStore.data.map { it[DARK_MODE] ?: false }
    val currency: Flow<String> = context.settingsDataStore.data.map { it[CURRENCY] ?: "USD" }
    val decimals: Flow<Int> = context.settingsDataStore.data.map { it[DECIMALS] ?: 2 }

    suspend fun setDarkMode(v: Boolean) = context.settingsDataStore.edit { it[DARK_MODE] = v }
    suspend fun setCurrency(v: String) = context.settingsDataStore.edit { it[CURRENCY] = v }
    suspend fun setDecimals(v: Int) = context.settingsDataStore.edit { it[DECIMALS] = v.coerceIn(0, 4) }

    // --- Interstitial ad pacing (master prompt §11D) ---
    suspend fun calcsSinceAd(): Int =
        context.settingsDataStore.data.map { it[CALCS_SINCE_AD] ?: 0 }.first()

    suspend fun incrementCalcsSinceAd() = context.settingsDataStore.edit {
        it[CALCS_SINCE_AD] = (it[CALCS_SINCE_AD] ?: 0) + 1
    }

    suspend fun resetCalcsSinceAd() = context.settingsDataStore.edit { it[CALCS_SINCE_AD] = 0 }

    suspend fun lastInterstitialAt(): Long =
        context.settingsDataStore.data.map { it[LAST_INTERSTITIAL_AT] ?: 0L }.first()

    suspend fun setLastInterstitialAt(ts: Long) =
        context.settingsDataStore.edit { it[LAST_INTERSTITIAL_AT] = ts }

    /** (windowStartMillis, shownCount) for the 30-minute / max-3-ads rule. */
    suspend fun adWindow(): Pair<Long, Int> =
        context.settingsDataStore.data.map {
            (it[AD_WINDOW_START] ?: 0L) to (it[AD_WINDOW_COUNT] ?: 0)
        }.first()

    suspend fun setAdWindow(start: Long, count: Int) = context.settingsDataStore.edit {
        it[AD_WINDOW_START] = start
        it[AD_WINDOW_COUNT] = count
    }

    // --- Cached exchange rates (for offline use of the converter) ---
    suspend fun saveRates(base: String, json: String, updatedAt: String) =
        context.settingsDataStore.edit {
            it[RATES_BASE] = base
            it[RATES_JSON] = json
            it[RATES_UPDATED_AT] = updatedAt
        }

    suspend fun cachedRates(): Triple<String, String, String> =
        context.settingsDataStore.data.map {
            Triple(it[RATES_BASE] ?: "", it[RATES_JSON] ?: "", it[RATES_UPDATED_AT] ?: "")
        }.first()

    suspend fun clearAll() = context.settingsDataStore.edit { it.clear() }
}
