package com.fincalc.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fincalc.app.ads.AdMobManager
import com.fincalc.app.data.CalcResult
import com.fincalc.app.data.CalculatorDef
import com.fincalc.app.data.CalculatorRegistry
import com.fincalc.app.data.Favorite
import com.fincalc.app.data.FinCalcDao
import com.fincalc.app.data.HistoryItem
import com.fincalc.app.data.RatesHolder
import com.fincalc.app.data.RatesRepository
import com.fincalc.app.data.RatesState
import com.fincalc.app.data.SettingsStore
import com.fincalc.app.ui.components.InputField
import com.fincalc.app.ui.components.PrimaryButton
import com.fincalc.app.ui.components.ResultCard
import com.fincalc.app.util.Format
import com.fincalc.app.util.PdfExport
import kotlinx.coroutines.launch

val CURRENCY_SYMBOLS = mapOf(
    "USD" to "$", "EUR" to "€", "GBP" to "£", "PKR" to "Rs ", "INR" to "₹",
    "CAD" to "$", "AUD" to "$", "AED" to "AED ", "JPY" to "¥", "CNY" to "¥",
    "CHF" to "CHF ", "NZD" to "$", "SGD" to "$", "SAR" to "SAR ", "QAR" to "QAR ",
    "KWD" to "KWD ", "BHD" to "BHD ", "OMR" to "OMR ", "TRY" to "₺", "BRL" to "R$",
    "ZAR" to "R ", "KRW" to "₩", "MYR" to "RM ", "IDR" to "Rp ", "THB" to "฿",
    "PHP" to "₱", "BDT" to "৳", "NPR" to "Rs ", "LKR" to "Rs ", "AFN" to "؋"
)

// ---------- Generic calculator ----------

@Composable
fun CalculatorScreen(navController: NavController, dao: FinCalcDao, store: SettingsStore, calcId: String) {
    val def = CalculatorRegistry.byId(calcId)
    if (def == null) {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Text("Calculator not found.", fontSize = 16.sp)
        }
        return
    }
    if (def.id == "currency-converter") {
        CurrencyConverterScreen(navController, dao, store, def)
        return
    }
    CalculatorContent(navController, dao, store, def)
}

@Composable
fun CalculatorContent(
    navController: NavController,
    dao: FinCalcDao,
    store: SettingsStore,
    def: CalculatorDef
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currency by store.currency.collectAsState(initial = "USD")
    val decimals by store.decimals.collectAsState(initial = 2)
    val symbol = CURRENCY_SYMBOLS[currency] ?: "$"

    val values = remember(def.id) {
        mutableStateMapOf<String, String>().apply {
            def.inputs.forEach { put(it.id, it.defaultValue) }
        }
    }
    var result by remember(def.id) { mutableStateOf<CalcResult?>(null) }
    var error by remember(def.id) { mutableStateOf<String?>(null) }
    var showPdfDialog by remember { mutableStateOf(false) }
    var calcTime by remember(def.id) { mutableStateOf<Long?>(null) }
    val favorites by dao.favorites().collectAsState(initial = emptyList())
    val isFavorite = favorites.contains(def.id)

    fun handleBack() {
        if (result != null) {
            AdMobManager.maybeShowInterstitial(context as android.app.Activity) {
                navController.navigateUp()
            }
        } else {
            navController.navigateUp()
        }
    }
    BackHandler(enabled = true) { handleBack() }

    fun resultText(r: CalcResult): String = buildString {
        appendLine("${def.title}")
        appendLine("${r.subtitle}: ${r.title}")
        r.rows.forEach { appendLine("${it.label}: ${it.value}") }
        if (r.formula.isNotBlank()) appendLine("Formula: ${r.formula}")
        calcTime?.let { appendLine("Calculated: ${Format.dateTime(it)}") }
    }

    fun exportPdf(r: CalcResult) {
        val uri = PdfExport.export(
            context, def.title, "${r.subtitle}: ${r.title}",
            r.rows.map { PdfExport.PdfLine(it.label, it.value) }, r.formula
        )
        if (uri != null) {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "${def.title} — FinCalc")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Export PDF"))
        } else {
            Toast.makeText(context, "PDF export failed, please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)
    ) {
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(def.title, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            IconButton(onClick = {
                scope.launch {
                    if (isFavorite) dao.removeFavorite(def.id) else dao.addFavorite(Favorite(def.id))
                }
            }) {
                Icon(
                    if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) androidx.compose.ui.graphics.Color(0xFFDC2626)
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Text(def.description, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(8.dp))
        def.inputs.forEach { input ->
            InputField(input, values[input.id] ?: "", onChange = { values[input.id] = it })
        }
        if (error != null) {
            Text(error!!, color = MaterialTheme.colorScheme.error, fontSize = 13.sp,
                modifier = Modifier.padding(vertical = 4.dp))
        }
        Spacer(Modifier.height(8.dp))
        PrimaryButton("Calculate") {
            val missing = def.inputs.filter { it.required && (values[it.id].isNullOrBlank()) }
            val invalid = def.inputs.filter {
                it.type == com.fincalc.app.data.InputType.NUMBER &&
                    (values[it.id]?.toDoubleOrNull() == null)
            }
            when {
                missing.isNotEmpty() -> {
                    error = "Please fill in: ${missing.joinToString(", ") { it.label }}"
                    result = null
                }
                invalid.isNotEmpty() -> {
                    error = "Please enter valid numbers for: ${invalid.joinToString(", ") { it.label }}"
                    result = null
                }
                else -> {
                    error = null
                    result = try {
                        def.compute(values.toMap(), symbol, decimals)
                    } catch (e: Exception) {
                        error = "Could not calculate — please check your inputs."
                        null
                    }
                    if (result != null) {
                        calcTime = System.currentTimeMillis()
                        AdMobManager.onCalculationCompleted(context)
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = {
                def.inputs.forEach { values[it.id] = it.defaultValue }
                result = null
                error = null
            },
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) { Text("Reset") }

        result?.let { r ->
            ResultCard(
                result = r,
                onCopy = {
                    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    cm.setPrimaryClip(ClipData.newPlainText("FinCalc result", resultText(r)))
                    Toast.makeText(context, "Result copied.", Toast.LENGTH_SHORT).show()
                },
                onShare = {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, resultText(r) + "\n— Calculated with FinCalc")
                    }
                    context.startActivity(Intent.createChooser(intent, "Share result"))
                },
                onSavePdf = {
                    // PDF export is the rewarded-ad unlock: watch an ad to unlock
                    // it — or export directly when no ad is loaded (never block).
                    if (AdMobManager.isRewardedReady()) showPdfDialog = true else exportPdf(r)
                },
                onSaveHistory = {
                    scope.launch {
                        dao.insertHistory(
                            HistoryItem(
                                calcId = def.id, calcTitle = def.title,
                                summary = "${r.subtitle}: ${r.title}",
                                inputsJson = values.toMap().toString(),
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    }
                    Toast.makeText(context, "Saved to History.", Toast.LENGTH_SHORT).show()
                },
                timestamp = calcTime
            )
            if (showPdfDialog) {
                AlertDialog(
                    onDismissRequest = { showPdfDialog = false },
                    title = { Text("Export as PDF") },
                    text = { Text("Watch a short ad to unlock the PDF export for this calculation. Basic calculations stay free — watching is always optional.") },
                    confirmButton = {
                        TextButton(onClick = {
                            showPdfDialog = false
                            AdMobManager.showRewarded(
                                context as android.app.Activity,
                                onReward = { exportPdf(r) },
                                onClosed = {}
                            )
                        }) { Text("Watch Ad to Unlock") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showPdfDialog = false }) { Text("Cancel") }
                    }
                )
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

// ---------- Currency converter (live rates) ----------

@Composable
fun CurrencyConverterScreen(
    navController: NavController,
    dao: FinCalcDao,
    store: SettingsStore,
    def: CalculatorDef
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val decimals by store.decimals.collectAsState(initial = 2)
    val repo = remember { RatesRepository(context) }
    var ratesState by remember { mutableStateOf<RatesState>(RatesState.Loading) }
    var result by remember { mutableStateOf<CalcResult?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var showPdfDialog by remember { mutableStateOf(false) }
    var calcTime by remember { mutableStateOf<Long?>(null) }
    val values = remember {
        mutableStateMapOf("amount" to "100", "from" to "USD", "to" to "EUR")
    }

    fun loadRates() {
        scope.launch {
            ratesState = RatesState.Loading
            val state = repo.getRates("USD")
            when (state) {
                is RatesState.Live -> {
                    RatesHolder.rates = state.rates
                    RatesHolder.updatedAt = state.updatedAt
                    RatesHolder.source = state.source
                }
                is RatesState.Cached -> {
                    RatesHolder.rates = state.rates
                    RatesHolder.updatedAt = state.updatedAt + " (cached)"
                    RatesHolder.source = "Last saved rates"
                }
                else -> { /* leave holder as-is */ }
            }
            ratesState = state
        }
    }
    LaunchedEffect(Unit) { loadRates() }

    fun exportPdf(r: CalcResult) {
        val uri = PdfExport.export(
            context, def.title, "${r.subtitle}: ${r.title}",
            r.rows.map { PdfExport.PdfLine(it.label, it.value) }, r.formula
        )
        if (uri != null) {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Export PDF"))
        } else {
            Toast.makeText(context, "PDF export failed, please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)
    ) {
        Text(def.title, fontSize = 20.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 12.dp))
        Text(def.description, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(8.dp))
        // Rate status line
        when (val st = ratesState) {
            is RatesState.Loading -> Text("Loading live rates…", fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            is RatesState.Live -> Text("Live rates • Updated: ${st.updatedAt} • Source: ${st.source}",
                fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            is RatesState.Cached -> Text("Showing last saved rates (${st.updatedAt}) — connect to refresh.",
                fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
            is RatesState.Unavailable -> Text(st.message, fontSize = 13.sp,
                color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(8.dp))
        InputField(
            com.fincalc.app.data.CalcInput("amount", "Amount"),
            values["amount"] ?: "", onChange = { values["amount"] = it }
        )
        InputField(
            com.fincalc.app.data.CalcInput("from", "From", type = com.fincalc.app.data.InputType.SELECT,
                options = RatesRepository.SUPPORTED, defaultValue = "USD"),
            values["from"] ?: "USD", onChange = { values["from"] = it }
        )
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {
                val f = values["from"]; values["from"] = values["to"] ?: "EUR"; values["to"] = f ?: "USD"
            }) { Icon(Icons.Filled.SwapHoriz, contentDescription = "Swap currencies") }
            Text("Swap", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.weight(1f))
            IconButton(onClick = { loadRates() }) { Icon(Icons.Filled.Refresh, contentDescription = "Refresh rates") }
        }
        InputField(
            com.fincalc.app.data.CalcInput("to", "To", type = com.fincalc.app.data.InputType.SELECT,
                options = RatesRepository.SUPPORTED, defaultValue = "EUR"),
            values["to"] ?: "EUR", onChange = { values["to"] = it }
        )
        if (error != null) {
            Text(error!!, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
        }
        Spacer(Modifier.height(8.dp))
        PrimaryButton("Convert") {
            val amount = values["amount"]?.toDoubleOrNull()
            if (amount == null) {
                error = "Please enter a valid amount."
                result = null
            } else {
                error = null
                result = def.compute(values.toMap(), "$", decimals)
                calcTime = System.currentTimeMillis()
                AdMobManager.onCalculationCompleted(context)
            }
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = { values["amount"] = "100"; values["from"] = "USD"; values["to"] = "EUR"; result = null },
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) { Text("Reset") }
        result?.let { r ->
            ResultCard(
                result = r,
                onCopy = {
                    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    cm.setPrimaryClip(
                        ClipData.newPlainText(
                            "FinCalc result",
                            "${r.subtitle}: ${r.title}" +
                                (calcTime?.let { "\nCalculated: ${Format.dateTime(it)}" } ?: "")
                        )
                    )
                    Toast.makeText(context, "Result copied.", Toast.LENGTH_SHORT).show()
                },
                onShare = {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "${r.subtitle}: ${r.title}" +
                                (calcTime?.let { "\nCalculated: ${Format.dateTime(it)}" } ?: "") +
                                "\n— Converted with FinCalc"
                        )
                    }
                    context.startActivity(Intent.createChooser(intent, "Share result"))
                },
                onSavePdf = {
                    // Same rewarded unlock as every other calculator (§11E).
                    if (AdMobManager.isRewardedReady()) showPdfDialog = true else exportPdf(r)
                },
                onSaveHistory = {
                    scope.launch {
                        dao.insertHistory(
                            HistoryItem(
                                calcId = def.id, calcTitle = def.title,
                                summary = "${r.subtitle}: ${r.title}",
                                inputsJson = values.toMap().toString(),
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    }
                    Toast.makeText(context, "Saved to History.", Toast.LENGTH_SHORT).show()
                },
                timestamp = calcTime
            )
            if (showPdfDialog) {
                AlertDialog(
                    onDismissRequest = { showPdfDialog = false },
                    title = { Text("Export as PDF") },
                    text = { Text("Watch a short ad to unlock the PDF export for this calculation. Basic calculations stay free — watching is always optional.") },
                    confirmButton = {
                        TextButton(onClick = {
                            showPdfDialog = false
                            AdMobManager.showRewarded(
                                context as android.app.Activity,
                                onReward = { exportPdf(r) },
                                onClosed = {}
                            )
                        }) { Text("Watch Ad to Unlock") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showPdfDialog = false }) { Text("Cancel") }
                    }
                )
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}
