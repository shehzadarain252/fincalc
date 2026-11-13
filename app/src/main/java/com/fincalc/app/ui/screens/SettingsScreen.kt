package com.fincalc.app.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.fincalc.app.data.FinCalcDao
import com.fincalc.app.data.SettingsStore
import kotlinx.coroutines.launch

/** Support contact for FinCalc — the user's own account email (changeable). */
const val SUPPORT_EMAIL = "shehzadarain261@gmail.com"

@Composable
fun SettingsScreen(navController: NavController, dao: FinCalcDao, store: SettingsStore) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val darkMode by store.darkMode.collectAsState(initial = false)
    val currency by store.currency.collectAsState(initial = "USD")
    val decimals by store.decimals.collectAsState(initial = 2)
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showDecimalsDialog by remember { mutableStateOf(false) }
    var confirmClear by remember { mutableStateOf(false) }
    val favorites by dao.favorites().collectAsState(initial = emptyList())

    @Composable
    fun RowItem(title: String, subtitle: String? = null, onClick: (() -> Unit)? = null, trailing: @Composable (() -> Unit)? = null) {
        Row(
            Modifier.fillMaxWidth().clickable(enabled = onClick != null) { onClick?.invoke() }
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(title, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                if (subtitle != null) {
                    Text(subtitle, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            trailing?.invoke() ?: Icon(Icons.Filled.ChevronRight, contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        Text("Appearance", fontSize = 13.sp, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary)
        RowItem("Dark Mode", "Easier on the eyes at night") {
            Switch(checked = darkMode, onCheckedChange = { scope.launch { store.setDarkMode(it) } })
        }
        Spacer(Modifier.height(8.dp))
        Text("Calculations", fontSize = 13.sp, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary)
        RowItem("Default Currency", currency, onClick = { showCurrencyDialog = true })
        RowItem("Decimal Precision", "$decimals digits", onClick = { showDecimalsDialog = true })
        RowItem("Calculation History", "View, reopen and delete past calculations",
            onClick = { navController.navigate("history") })
        RowItem("Favorites", "${favorites.size} saved",
            onClick = { navController.navigate("favorites") })
        Spacer(Modifier.height(8.dp))
        Text("Privacy & Data", fontSize = 13.sp, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary)
        RowItem("Privacy Settings", "Advertising consent and data choices",
            onClick = {
                AdMobManager.requestConsent(context as android.app.Activity) {
                    Toast.makeText(context, "Privacy choices updated.", Toast.LENGTH_SHORT).show()
                }
            })
        RowItem("Clear App Data", "Delete history, favorites and settings",
            onClick = { confirmClear = true })
        Spacer(Modifier.height(8.dp))
        Text("About", fontSize = 13.sp, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary)
        RowItem("About App", "Version 1.0.0", onClick = { navController.navigate("about") })
        RowItem("Contact Support", SUPPORT_EMAIL, onClick = {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$SUPPORT_EMAIL")
                putExtra(Intent.EXTRA_SUBJECT, "FinCalc Support")
            }
            context.startActivity(intent)
        })
        Spacer(Modifier.height(24.dp))
    }

    if (showCurrencyDialog) {
        AlertDialog(
            onDismissRequest = { showCurrencyDialog = false },
            title = { Text("Default Currency") },
            text = {
                Column {
                    listOf("USD", "EUR", "GBP", "PKR", "INR", "CAD", "AUD", "AED", "JPY", "CNY", "CHF").forEach { code ->
                        Text(
                            "$code ${CURRENCY_SYMBOLS[code] ?: ""}",
                            fontSize = 15.sp,
                            modifier = Modifier.fillMaxWidth().clickable {
                                scope.launch { store.setCurrency(code) }
                                showCurrencyDialog = false
                            }.padding(vertical = 8.dp)
                        )
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showCurrencyDialog = false }) { Text("Cancel") }
            }
        )
    }
    if (showDecimalsDialog) {
        AlertDialog(
            onDismissRequest = { showDecimalsDialog = false },
            title = { Text("Decimal Precision") },
            text = {
                Column {
                    listOf(0, 1, 2, 3, 4).forEach { n ->
                        Text(
                            "$n digit${if (n == 1) "" else "s"}",
                            fontSize = 15.sp,
                            modifier = Modifier.fillMaxWidth().clickable {
                                scope.launch { store.setDecimals(n) }
                                showDecimalsDialog = false
                            }.padding(vertical = 8.dp)
                        )
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showDecimalsDialog = false }) { Text("Cancel") }
            }
        )
    }
    if (confirmClear) {
        AlertDialog(
            onDismissRequest = { confirmClear = false },
            title = { Text("Clear all app data?") },
            text = { Text("This deletes your history, favorites and settings. This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmClear = false
                    scope.launch {
                        dao.clearHistory()
                        favorites.forEach { dao.removeFavorite(it) }
                        store.clearAll()
                    }
                    Toast.makeText(context, "App data cleared.", Toast.LENGTH_SHORT).show()
                }) { Text("Clear") }
            },
            dismissButton = {
                TextButton(onClick = { confirmClear = false }) { Text("Cancel") }
            }
        )
    }
}
