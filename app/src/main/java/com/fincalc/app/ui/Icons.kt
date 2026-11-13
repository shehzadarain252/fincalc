package com.fincalc.app.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.ui.graphics.vector.ImageVector

/** Icon keys used by CalculatorDef.icon — one place, so the UI stays consistent. */
fun iconFor(key: String): ImageVector = when (key) {
    "loan", "emi" -> Icons.Filled.Home
    "home" -> Icons.Filled.Home
    "mortgage" -> Icons.Filled.AccountBalance
    "car" -> Icons.Filled.ShoppingCart
    "interest" -> Icons.Filled.Percent
    "payoff" -> Icons.Filled.Schedule
    "dti" -> Icons.Filled.PieChart
    "compare" -> Icons.Filled.BarChart
    "schedule" -> Icons.Filled.CalendarMonth
    "tax", "federal", "state" -> Icons.Filled.Receipt
    "paycheck", "salary", "hourly" -> Icons.Filled.AttachMoney
    "overtime" -> Icons.Filled.Timelapse
    "sales" -> Icons.Filled.Store
    "payroll" -> Icons.Filled.Wallet
    "takehome" -> Icons.Filled.MonetizationOn
    "compound", "savings" -> Icons.Filled.TrendingUp
    "simple" -> Icons.Filled.Calculate
    "return", "roi" -> Icons.Filled.ShowChart
    "stock" -> Icons.Filled.BarChart
    "dividend" -> Icons.Filled.CardGiftcard
    "retirement" -> Icons.Filled.School
    "401k" -> Icons.Filled.Savings
    "roth" -> Icons.Filled.Savings
    "inflation" -> Icons.Filled.TrendingUp
    "budget", "planner", "expense" -> Icons.Filled.Wallet
    "goal", "flag" -> Icons.Filled.Flag
    "emergency" -> Icons.Filled.Savings
    "card", "cardinterest" -> Icons.Filled.CreditCard
    "debt" -> Icons.Filled.CreditCard
    "networth" -> Icons.Filled.AccountBalance
    "currency" -> Icons.Filled.CurrencyExchange
    "percent" -> Icons.Filled.Percent
    "discount" -> Icons.Filled.ShoppingCart
    "tip" -> Icons.Filled.AttachMoney
    "age" -> Icons.Filled.CalendarMonth
    "date" -> Icons.Filled.DateRange
    "time" -> Icons.Filled.Schedule
    "unit" -> Icons.Filled.Calculate
    "gst", "vat" -> Icons.Filled.Receipt
    "margin", "markup", "gross", "netprofit" -> Icons.Filled.TrendingUp
    "breakeven" -> Icons.Filled.BarChart
    "businessloan" -> Icons.Filled.AccountBalance
    "commission" -> Icons.Filled.AttachMoney
    "cashflow" -> Icons.Filled.Wallet
    "revenue" -> Icons.Filled.MonetizationOn
    else -> Icons.Filled.Calculate
}
