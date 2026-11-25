package com.fincalc.app.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

/**
 * Currency exchange rates.
 *
 * Primary source: ExchangeRate-API open endpoint — free, no API key,
 * 160+ currencies, daily snapshot, attribution required.
 *   https://open.er-api.com/v6/latest/USD
 * Fallback: Frankfurter (ECB reference rates, free, no key).
 *   https://api.frankfurter.dev/v1/latest?base=USD
 *
 * Rules from the master prompt (§8): never display invented live rates.
 * If both sources fail, the converter shows "rates unavailable" and offers
 * the last cached snapshot (clearly labelled with its update time) — it
 * never makes up a rate.
 */
data class RatesResponse(
    @SerializedName("result") val result: String? = null,
    @SerializedName("base_code") val baseCode: String? = null,
    @SerializedName("base") val baseAlt: String? = null,
    @SerializedName("time_last_update_utc") val updatedUtc: String? = null,
    @SerializedName("date") val date: String? = null,
    @SerializedName("rates") val rates: Map<String, Double> = emptyMap()
)

interface ErApiService {
    @GET("v6/latest/{base}")
    suspend fun latest(@Path("base") base: String): RatesResponse
}

interface FrankfurterService {
    @GET("v1/latest")
    suspend fun latest(@Query("base") base: String): RatesResponse
}

sealed class RatesState {
    object Loading : RatesState()
    data class Live(val rates: Map<String, Double>, val updatedAt: String, val source: String) : RatesState()
    data class Cached(val rates: Map<String, Double>, val updatedAt: String) : RatesState()
    data class Unavailable(val message: String) : RatesState()
}

class RatesRepository(private val context: Context) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val erApi = Retrofit.Builder()
        .baseUrl("https://open.er-api.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(ErApiService::class.java)

    private val frankfurter = Retrofit.Builder()
        .baseUrl("https://api.frankfurter.dev/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(FrankfurterService::class.java)

    private val store = SettingsStore(context)
    private val gson = Gson()

    /** Fetch fresh rates for [base]; fall back to the cached snapshot. */
    suspend fun getRates(base: String): RatesState = withContext(Dispatchers.IO) {
        // Fresh from primary source.
        try {
            val res = erApi.latest(base)
            if (res.rates.isNotEmpty()) {
                val stamp = res.updatedUtc ?: res.date ?: ""
                store.saveRates(base, gson.toJson(res.rates), stamp)
                return@withContext RatesState.Live(res.rates, stamp, "ExchangeRate-API (open.er-api.com)")
            }
        } catch (_: Exception) { /* try fallback */ }

        // Fallback source (ECB rates).
        try {
            val res = frankfurter.latest(base)
            if (res.rates.isNotEmpty()) {
                val stamp = res.date ?: ""
                store.saveRates(base, gson.toJson(res.rates), stamp)
                return@withContext RatesState.Live(res.rates, stamp, "Frankfurter (ECB reference rates)")
            }
        } catch (_: Exception) { /* fall through to cache */ }

        // Last cached snapshot, clearly labelled.
        val (cachedBase, json, stamp) = store.cachedRates()
        if (json.isNotBlank() && cachedBase == base) {
            @Suppress("UNCHECKED_CAST")
            val rates = gson.fromJson(json, Map::class.java) as? Map<String, Double>
            if (!rates.isNullOrEmpty()) return@withContext RatesState.Cached(rates, stamp)
        }
        RatesState.Unavailable(
            "Live exchange rates are unavailable right now. Check your internet " +
                "connection and try again — no estimated rate is shown."
        )
    }

    companion object {
        /** Major currencies offered in the converter (plus USD). */
        val SUPPORTED = listOf(
            "USD", "EUR", "GBP", "PKR", "INR", "CAD", "AUD", "AED", "JPY", "CNY",
            "CHF", "NZD", "SGD", "SAR", "QAR", "KWD", "BHD", "OMR", "TRY", "BRL",
            "ZAR", "KRW", "MYR", "IDR", "THB", "PHP", "BDT", "NPR", "LKR", "AFN"
        )
        val NAMES = mapOf(
            "USD" to "US Dollar", "EUR" to "Euro", "GBP" to "British Pound",
            "PKR" to "Pakistani Rupee", "INR" to "Indian Rupee", "CAD" to "Canadian Dollar",
            "AUD" to "Australian Dollar", "AED" to "UAE Dirham", "JPY" to "Japanese Yen",
            "CNY" to "Chinese Yuan", "CHF" to "Swiss Franc", "NZD" to "New Zealand Dollar",
            "SGD" to "Singapore Dollar", "SAR" to "Saudi Riyal", "QAR" to "Qatari Riyal",
            "KWD" to "Kuwaiti Dinar", "BHD" to "Bahraini Dinar", "OMR" to "Omani Rial",
            "TRY" to "Turkish Lira", "BRL" to "Brazilian Real", "ZAR" to "South African Rand",
            "KRW" to "South Korean Won", "MYR" to "Malaysian Ringgit", "IDR" to "Indonesian Rupiah",
            "THB" to "Thai Baht", "PHP" to "Philippine Peso", "BDT" to "Bangladeshi Taka",
            "NPR" to "Nepalese Rupee", "LKR" to "Sri Lankan Rupee", "AFN" to "Afghan Afghani"
        )
    }
}
