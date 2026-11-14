package com.fincalc.app.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Money / number formatting. All amounts use US grouping (1,000.00) and the
 * user's chosen decimal precision (default 2, from Settings).
 */
object Format {

    fun money(value: Double, currencySymbol: String = "$", decimals: Int = 2): String {
        if (value.isNaN() || value.isInfinite()) return currencySymbol + "0.00"
        val nf = NumberFormat.getNumberInstance(Locale.US).apply {
            minimumFractionDigits = decimals
            maximumFractionDigits = decimals
        }
        val sign = if (value < 0) "-" else ""
        return sign + currencySymbol + nf.format(kotlin.math.abs(value))
    }

    fun num(value: Double, decimals: Int = 2): String {
        if (value.isNaN() || value.isInfinite()) return "0"
        val nf = NumberFormat.getNumberInstance(Locale.US).apply {
            minimumFractionDigits = decimals
            maximumFractionDigits = decimals
        }
        return nf.format(value)
    }

    fun pct(value: Double, decimals: Int = 2): String = num(value, decimals) + "%"

    fun whole(value: Double): String {
        val nf = NumberFormat.getIntegerInstance(Locale.US)
        return nf.format(kotlin.math.round(value).toLong())
    }

    /** "14 Nov 2026, 12:07 AM" — shown on results and in shared text. */
    fun dateTime(timestamp: Long): String =
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(timestamp))
}
