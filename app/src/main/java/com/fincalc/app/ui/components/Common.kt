package com.fincalc.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.fincalc.app.util.Format
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fincalc.app.data.CalcInput
import com.fincalc.app.data.CalcResult
import com.fincalc.app.data.CalculatorDef
import com.fincalc.app.data.Category
import com.fincalc.app.data.ChartBar
import com.fincalc.app.ui.iconFor
import com.fincalc.app.ui.theme.FinCalcBlue
import com.fincalc.app.ui.theme.FinCalcPurple

// ---------- Input fields ----------

@Composable
fun NumberField(input: CalcInput, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = { v ->
            // Allow digits, one dot, one leading minus.
            val clean = v.filter { it.isDigit() || it == '.' || it == '-' }
            onChange(clean)
        },
        label = { Text(input.label) },
        placeholder = { Text(input.hint.ifBlank { "0" }) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectField(input: CalcInput, value: String, onChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(input.label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            input.options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = { onChange(option); expanded = false }
                )
            }
        }
    }
}

@Composable
fun InputField(input: CalcInput, value: String, onChange: (String) -> Unit) {
    when (input.type) {
        com.fincalc.app.data.InputType.SELECT -> SelectField(input, value, onChange)
        else -> NumberField(input, value, onChange)
    }
}

// ---------- Buttons ----------

@Composable
fun PrimaryButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(12.dp)
    ) { Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold) }
}

@Composable
fun ActionButton(text: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.height(44.dp)
    ) { Text(text) }
}

// ---------- Result display ----------

@Composable
fun ResultCard(
    result: CalcResult,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onSavePdf: () -> Unit,
    onSaveHistory: () -> Unit,
    /** When the calculation was performed — shown under the headline (§10). */
    timestamp: Long? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                result.subtitle,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                result.title,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = FinCalcBlue,
                modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
            )
            if (timestamp != null) {
                Text(
                    "Calculated: ${Format.dateTime(timestamp)}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            result.rows.forEach { row ->
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(row.label, fontSize = 14.sp, modifier = Modifier.weight(1f))
                    Text(
                        row.value, fontSize = 14.sp,
                        fontWeight = if (row.highlight) FontWeight.Bold else FontWeight.Normal,
                        textAlign = TextAlign.End
                    )
                }
            }
            if (result.chart.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                BarChart(bars = result.chart)
            }
            if (result.formula.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                Text("Formula", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(result.formula, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (result.breakdown.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Text("Breakdown", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                result.breakdown.forEach { line ->
                    Text("• $line", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp))
                }
            }
            if (result.tip != null) {
                Spacer(Modifier.height(12.dp))
                Box(
                    Modifier.fillMaxWidth().background(
                        MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(10.dp)
                    ).padding(12.dp)
                ) {
                    Text("💡 ${result.tip}", fontSize = 13.sp)
                }
            }
            if (result.disclaimer != null) {
                Spacer(Modifier.height(8.dp))
                Text(result.disclaimer, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ActionButton("Copy", onCopy)
                ActionButton("Share", onShare)
                ActionButton("PDF", onSavePdf)
                ActionButton("Save", onSaveHistory)
            }
        }
    }
}

@Composable
fun BarChart(bars: List<ChartBar>, modifier: Modifier = Modifier) {
    val max = (bars.maxOfOrNull { it.value } ?: 0.0).coerceAtLeast(1.0)
    val colors = listOf(FinCalcBlue, FinCalcPurple, Color(0xFF16A34A), Color(0xFFF59E0B), Color(0xFF06B6D4), Color(0xFFEC4899))
    Column(modifier.fillMaxWidth()) {
        Canvas(Modifier.fillMaxWidth().height((bars.size * 34).dp)) {
            val barH = 22.dp.toPx()
            val gap = 12.dp.toPx()
            bars.forEachIndexed { i, bar ->
                val y = i * (barH + gap)
                val w = (size.width * 0.62f * (bar.value / max)).toFloat().coerceAtLeast(4f)
                drawRoundRect(
                    color = colors[i % colors.size],
                    topLeft = Offset(0f, y),
                    size = Size(w, barH),
                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                )
            }
        }
        bars.forEachIndexed { i, bar ->
            Row(Modifier.fillMaxWidth().padding(top = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(bar.label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    if (bar.value >= 1000) "%,.0f".format(bar.value) else "%.2f".format(bar.value),
                    fontSize = 12.sp, fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ---------- Cards & rows ----------

@Composable
fun CalculatorGridCard(def: CalculatorDef, onClick: () -> Unit) {
    Card(
        modifier = Modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier.size(48.dp).background(
                    MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp)
                ),
                contentAlignment = Alignment.Center
            ) {
                Icon(iconFor(def.icon), contentDescription = def.title, tint = FinCalcBlue,
                    modifier = Modifier.size(26.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(
                def.title.replace(" Calculator", ""), fontSize = 12.sp,
                fontWeight = FontWeight.Medium, textAlign = TextAlign.Center,
                maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 15.sp
            )
        }
    }
}

@Composable
fun CalculatorRow(
    def: CalculatorDef,
    onClick: () -> Unit,
    isFavorite: Boolean = false,
    onFavorite: ((CalculatorDef) -> Unit)? = null
) {
    Row(
        Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(48.dp).background(
                MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp)
            ),
            contentAlignment = Alignment.Center
        ) {
            Icon(iconFor(def.icon), contentDescription = def.title, tint = FinCalcBlue,
                modifier = Modifier.size(26.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(def.title, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text(def.description, fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        if (onFavorite != null) {
            IconButton(onClick = { onFavorite(def) }) {
                Icon(
                    if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color(0xFFDC2626) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Icon(Icons.Filled.ChevronRight, contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun CategoryCard(category: Category, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(56.dp).background(
                    MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(14.dp)
                ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    iconFor(category.id), contentDescription = category.title,
                    tint = FinCalcBlue, modifier = Modifier.size(30.dp)
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(category.title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(category.subtitle, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun SectionHeader(title: String, onSeeAll: (() -> Unit)? = null) {
    Row(
        Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        if (onSeeAll != null) {
            Text("See All >", fontSize = 13.sp, color = FinCalcBlue,
                modifier = Modifier.clickable { onSeeAll() })
        }
    }
}
