package com.fincalc.app.ui.screens

import android.content.Intent
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.fincalc.app.data.CalculatorRegistry
import com.fincalc.app.data.FinCalcDao
import com.fincalc.app.ui.components.CalculatorRow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun formatTime(ts: Long): String =
    SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.US).format(Date(ts))

// ---------- Favorites ----------

@Composable
fun FavoritesScreen(navController: NavController, dao: FinCalcDao) {
    val favorites by dao.favorites().collectAsState(initial = emptyList())
    val defs = remember(favorites) { favorites.mapNotNull { CalculatorRegistry.byId(it) } }
    val scope = rememberCoroutineScope()
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)
    ) {
        Text("Your favorite calculators", fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp))
        if (defs.isEmpty()) {
            Text("No favorites yet. Tap the heart on any calculator to add it here.",
                fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 16.dp))
        } else {
            defs.forEach { def ->
                CalculatorRow(
                    def = def,
                    onClick = { navController.navigate("calculator/${def.id}") },
                    isFavorite = true,
                    onFavorite = { scope.launch { dao.removeFavorite(def.id) } }
                )
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

// ---------- History ----------

@Composable
fun HistoryScreen(navController: NavController, dao: FinCalcDao) {
    val history by dao.history().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var confirmClear by remember { mutableStateOf(false) }
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Calculation History", fontSize = 16.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f))
            if (history.isNotEmpty()) {
                OutlinedButton(onClick = { confirmClear = true }) { Text("Clear All") }
            }
        }
        if (history.isEmpty()) {
            Text("No calculations yet. Results you save will appear here — tap one to reopen its calculator.",
                fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 16.dp))
        } else {
            history.forEach { item ->
                Row(
                    Modifier.fillMaxWidth().clickable {
                        navController.navigate("calculator/${item.calcId}")
                    }.padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(item.calcTitle, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                        Text(item.summary, fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(formatTime(item.timestamp), fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "${item.calcTitle}\n${item.summary}\n${formatTime(item.timestamp)}\n— Calculated with FinCalc"
                            )
                        }
                        context.startActivity(Intent.createChooser(intent, "Share calculation"))
                    }) {
                        Icon(Icons.Filled.Share, contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = { scope.launch { dao.deleteHistory(item.id) } }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
    }
    if (confirmClear) {
        AlertDialog(
            onDismissRequest = { confirmClear = false },
            title = { Text("Clear history?") },
            text = { Text("This deletes all saved calculations. This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmClear = false
                    scope.launch { dao.clearHistory() }
                }) { Text("Clear") }
            },
            dismissButton = {
                TextButton(onClick = { confirmClear = false }) { Text("Cancel") }
            }
        )
    }
}
