package com.fincalc.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fincalc.app.ads.AdMobManager
import com.fincalc.app.data.Category
import com.fincalc.app.data.FinCalcDatabase
import com.fincalc.app.data.SettingsStore
import com.fincalc.app.ui.screens.AboutScreen
import com.fincalc.app.ui.screens.AllCategoriesScreen
import com.fincalc.app.ui.screens.CalculatorScreen
import com.fincalc.app.ui.screens.CategoryScreen
import com.fincalc.app.ui.screens.ContactScreen
import com.fincalc.app.ui.screens.DisclaimerScreen
import com.fincalc.app.ui.screens.FaqScreen
import com.fincalc.app.ui.screens.FavoritesScreen
import com.fincalc.app.ui.screens.HistoryScreen
import com.fincalc.app.ui.screens.HomeScreen
import com.fincalc.app.ui.screens.PrivacyScreen
import com.fincalc.app.ui.screens.SearchScreen
import com.fincalc.app.ui.screens.SettingsScreen
import com.fincalc.app.ui.screens.TermsScreen
import com.fincalc.app.ui.theme.FinCalcTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Privacy consent (UMP) on first launch — required before ads.
        AdMobManager.requestConsent(this)
        setContent {
            val store = remember { SettingsStore(this) }
            val darkMode by store.darkMode.collectAsState(initial = false)
            val dao = remember { FinCalcDatabase.get(this).dao() }
            FinCalcTheme(darkTheme = darkMode) {
                FinCalcNav(store, dao)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinCalcNav(store: SettingsStore, dao: com.fincalc.app.data.FinCalcDao) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val backStack by navController.currentBackStackEntryAsState()
    val route = backStack?.destination?.route ?: "home"
    val darkMode by store.darkMode.collectAsState(initial = false)

    val title = when {
        route == "home" -> "FinCalc"
        route == "categories" -> "All Categories"
        route.startsWith("category/") -> {
            val id = route.substringAfter("category/")
            Category.values().find { it.id == id }?.title ?: "Calculators"
        }
        route.startsWith("calculator/") -> "Calculator"
        route == "search" -> "Search"
        route == "favorites" -> "Favorites"
        route == "history" -> "History"
        route == "settings" -> "Settings"
        route == "about" -> "About Us"
        route == "privacy" -> "Privacy Policy"
        route == "terms" -> "Terms & Conditions"
        route == "disclaimer" -> "Financial Disclaimer"
        route == "contact" -> "Contact Us"
        route == "faq" -> "FAQ"
        else -> "FinCalc"
    }
    val showBottomBar = route in listOf("home", "favorites", "history", "settings")
    val isHome = route == "home"

    fun navigateAndCloseDrawer(destination: String) {
        scope.launch { drawerState.close() }
        navController.navigate(destination) {
            popUpTo("home") { saveState = true }
            launchSingleTop = true
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(Modifier.padding(16.dp)) {
                    Text("FinCalc", fontSize = 24.sp, fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary)
                    Text("Smart Financial Tools", fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(16.dp))
                    val items = listOf(
                        "Home" to "home" to Icons.Filled.Home,
                        "Favorites" to "favorites" to Icons.Filled.Favorite,
                        "History" to "history" to Icons.Filled.History,
                        "Settings" to "settings" to Icons.Filled.Settings,
                        "About Us" to "about" to Icons.Filled.Info,
                        "Privacy Policy" to "privacy" to Icons.Filled.PrivacyTip,
                        "Terms & Conditions" to "terms" to Icons.Filled.Description,
                        "Financial Disclaimer" to "disclaimer" to Icons.Filled.Warning,
                        "Contact Us" to "contact" to Icons.Filled.Mail,
                        "FAQ" to "faq" to Icons.Filled.Help
                    )
                    items.forEach { (labelDest, icon) ->
                        val (label, dest) = labelDest
                        NavigationDrawerItem(
                            label = { Text(label) },
                            icon = { Icon(icon, contentDescription = null) },
                            selected = route == dest,
                            onClick = { navigateAndCloseDrawer(dest) }
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    androidx.compose.foundation.layout.Row(
                        Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Text("Dark Mode", modifier = Modifier.weight(1f))
                        Switch(checked = darkMode, onCheckedChange = {
                            scope.launch { store.setDarkMode(it) }
                        })
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            title, fontStyle = if (isHome) FontStyle.Italic else FontStyle.Normal,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        if (isHome) {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Filled.Menu, contentDescription = "Menu")
                            }
                        } else {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                            }
                        }
                    },
                    actions = {
                        if (isHome) {
                            // Dark/Light toggle on the home dashboard (master prompt §2).
                            IconButton(onClick = { scope.launch { store.setDarkMode(!darkMode) } }) {
                                Icon(
                                    if (darkMode) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                                    contentDescription = "Toggle dark mode"
                                )
                            }
                            IconButton(onClick = { navController.navigate("settings") }) {
                                Icon(Icons.Filled.Settings, contentDescription = "Settings")
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
            },
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar {
                        val tabs = listOf(
                            "Home" to "home" to Icons.Filled.Home,
                            "Favorites" to "favorites" to Icons.Filled.Favorite,
                            "History" to "history" to Icons.Filled.History,
                            "Settings" to "settings" to Icons.Filled.Settings
                        )
                        tabs.forEach { (labelDest, icon) ->
                            val (label, dest) = labelDest
                            NavigationBarItem(
                                selected = route == dest,
                                onClick = {
                                    navController.navigate(dest) {
                                        popUpTo("home") { saveState = true }
                                        launchSingleTop = true
                                    }
                                },
                                icon = { Icon(icon, contentDescription = label) },
                                label = { Text(label) }
                            )
                        }
                    }
                }
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.fillMaxSize().padding(padding)
            ) {
                composable("home") { HomeScreen(navController, dao) }
                composable("categories") { AllCategoriesScreen(navController) }
                composable(
                    "category/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.StringType })
                ) { entry ->
                    CategoryScreen(navController, dao, entry.arguments?.getString("id") ?: "loan")
                }
                composable(
                    "calculator/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.StringType })
                ) { entry ->
                    CalculatorScreen(navController, dao, store, entry.arguments?.getString("id") ?: "")
                }
                composable("search") { SearchScreen(navController) }
                composable("favorites") { FavoritesScreen(navController, dao) }
                composable("history") { HistoryScreen(navController, dao) }
                composable("settings") { SettingsScreen(navController, dao, store) }
                composable("about") { AboutScreen() }
                composable("privacy") { PrivacyScreen() }
                composable("terms") { TermsScreen() }
                composable("disclaimer") { DisclaimerScreen() }
                composable("contact") { ContactScreen() }
                composable("faq") { FaqScreen() }
            }
        }
    }
}
