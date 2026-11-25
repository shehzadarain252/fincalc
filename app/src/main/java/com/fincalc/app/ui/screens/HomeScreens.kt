package com.fincalc.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fincalc.app.data.CalculatorRegistry
import com.fincalc.app.data.Category
import com.fincalc.app.data.FinCalcDao
import com.fincalc.app.ui.components.CalculatorGridCard
import com.fincalc.app.ui.components.CalculatorRow
import com.fincalc.app.ui.components.CategoryCard
import com.fincalc.app.ui.components.SectionHeader
import com.fincalc.app.ui.theme.FinCalcBlue
import com.fincalc.app.ui.theme.FinCalcPurple
import kotlinx.coroutines.launch

// ---------- Home ----------

@Composable
fun HomeScreen(navController: NavController, dao: FinCalcDao) {
    val recent by dao.recentHistory(10).collectAsState(initial = emptyList())
    val recentDefs = remember(recent) {
        recent.mapNotNull { CalculatorRegistry.byId(it.calcId) }.distinctBy { it.id }.take(4)
    }
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)
    ) {
        // Welcome banner — matches the design screenshots.
        Box(
            Modifier.fillMaxWidth().padding(top = 12.dp)
                .background(
                    Brush.linearGradient(listOf(FinCalcBlue, FinCalcPurple)),
                    RoundedCornerShape(18.dp)
                ).padding(20.dp)
        ) {
            Column {
                Text("Your Smart", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Financial Assistant", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Plan Better. Live Smarter.", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp,
                    modifier = Modifier.padding(top = 4.dp))
            }
        }
        Spacer(Modifier.height(12.dp))
        // Search bar
        Box(
            Modifier.fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                .clickable { navController.navigate("search") }
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Search, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("  Search calculators...", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
            }
        }
        // Popular calculators
        SectionHeader("Popular Calculators") { navController.navigate("categories") }
        val popular = CalculatorRegistry.popular().take(8)
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.fillMaxWidth().height(210.dp),
            contentPadding = PaddingValues(0.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            userScrollEnabled = false
        ) {
            items(popular) { def ->
                CalculatorGridCard(def) { navController.navigate("calculator/${def.id}") }
            }
        }
        // Quick tools
        SectionHeader("Quick Tools") { navController.navigate("category/daily") }
        val quick = CalculatorRegistry.quickTools()
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.fillMaxWidth().height(105.dp),
            contentPadding = PaddingValues(0.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            userScrollEnabled = false
        ) {
            items(quick) { def ->
                CalculatorGridCard(def) { navController.navigate("calculator/${def.id}") }
            }
        }
        // Favorite calculators (master prompt §2)
        val favoriteIds by dao.favorites().collectAsState(initial = emptyList())
        val favDefs = remember(favoriteIds) {
            favoriteIds.mapNotNull { CalculatorRegistry.byId(it) }.take(4)
        }
        val favScope = rememberCoroutineScope()
        if (favDefs.isNotEmpty()) {
            SectionHeader("Favorite Calculators") { navController.navigate("favorites") }
            favDefs.forEach { def ->
                CalculatorRow(
                    def,
                    onClick = { navController.navigate("calculator/${def.id}") },
                    isFavorite = true,
                    onFavorite = { d -> favScope.launch { dao.removeFavorite(d.id) } }
                )
            }
        }
        // Recently used
        if (recentDefs.isNotEmpty()) {
            SectionHeader("Recently Used") { navController.navigate("history") }
            recentDefs.forEach { def ->
                CalculatorRow(def, onClick = { navController.navigate("calculator/${def.id}") })
            }
        }
        // All categories (master prompt §2)
        SectionHeader("All Categories") { navController.navigate("categories") }
        Category.values().forEach { category ->
            CategoryCard(category) { navController.navigate("category/${category.id}") }
        }
        // Promo banner
        Card(
            Modifier.fillMaxWidth().padding(vertical = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(Modifier.padding(18.dp)) {
                Text("Build a Secure", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("Financial Future", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("90 calculators for loans, taxes, investments and more — all in one place.",
                    fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp))
                Button(onClick = { navController.navigate("categories") },
                    shape = RoundedCornerShape(10.dp)) {
                    Text("Get Started →")
                }
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

// ---------- All categories ----------

@Composable
fun AllCategoriesScreen(navController: NavController) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        Category.values().forEach { category ->
            CategoryCard(category) { navController.navigate("category/${category.id}") }
        }
        Spacer(Modifier.height(16.dp))
    }
}

// ---------- One category ----------

@Composable
fun CategoryScreen(navController: NavController, dao: FinCalcDao, categoryId: String) {
    val category = Category.values().find { it.id == categoryId } ?: Category.LOAN
    val defs = CalculatorRegistry.byCategory(category)
    val favorites by dao.favorites().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)
    ) {
        Text(category.title, fontSize = 20.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 12.dp, bottom = 4.dp))
        Text("${defs.size} calculators — tap one to open it.", fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(8.dp))
        defs.forEach { def ->
            CalculatorRow(
                def = def,
                onClick = { navController.navigate("calculator/${def.id}") },
                isFavorite = favorites.contains(def.id),
                onFavorite = { d ->
                    scope.launch {
                        if (favorites.contains(d.id)) dao.removeFavorite(d.id)
                        else dao.addFavorite(com.fincalc.app.data.Favorite(d.id))
                    }
                }
            )
        }
        Spacer(Modifier.height(16.dp))
    }
}

// ---------- Search ----------

@Composable
fun SearchScreen(navController: NavController) {
    var query by remember { mutableStateOf("") }
    val results = remember(query) { CalculatorRegistry.search(query) }
    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search calculators...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(Modifier.height(8.dp))
        if (query.isBlank()) {
            Text("Try “loan”, “tax”, “compound”, “tip” or “currency”.",
                fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))
            Text("All categories", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Category.values().forEach { category ->
                CategoryCard(category) { navController.navigate("category/${category.id}") }
            }
        } else if (results.isEmpty()) {
            Text("No calculators found for “$query”.",
                fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 12.dp))
        } else {
            Text("${results.size} result${if (results.size == 1) "" else "s"}",
                fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Column(Modifier.verticalScroll(rememberScrollState())) {
                results.forEach { def ->
                    CalculatorRow(def, onClick = { navController.navigate("calculator/${def.id}") })
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
