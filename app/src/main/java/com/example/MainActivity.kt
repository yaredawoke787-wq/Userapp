package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.local.AppDatabase
import com.example.data.repository.GiftRepository
import com.example.ui.components.PremiumBottomBar
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.GiftViewModel
import com.example.ui.viewmodel.GiftViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Setup local SQLite database & Repository
        val database = AppDatabase.getDatabase(this)
        val repository = GiftRepository(database.giftDao())
        
        // Instantiate the centralized MVVM ViewModel with dynamic factory
        val viewModel = ViewModelProvider(
            this,
            GiftViewModelFactory(repository)
        )[GiftViewModel::class.java]

        // Auto sync with cloud database on startup
        viewModel.syncWithCloud(this)

        enableEdgeToEdge()
        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()
            val currentLanguage by viewModel.currentLanguage.collectAsState()
            MyApplicationTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: "splash"

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        // Only display floating bottom navigation bar on primary screens
                        val showBottomBar = currentRoute in listOf("home", "favorites", "cart", "contact", "settings")
                        if (showBottomBar) {
                            PremiumBottomBar(
                                currentRoute = currentRoute,
                                currentLanguage = currentLanguage,
                                onNavigate = { route ->
                                    navController.navigate(route) {
                                        // Avoid building duplicate task stacks on rapid selection
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                ) { _ ->
                    NavHost(
                        navController = navController,
                        startDestination = "splash",
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable("splash") {
                            SplashScreen(onSplashComplete = {
                                val sharedPrefs = this@MainActivity.getSharedPreferences("teke_prefs", android.content.Context.MODE_PRIVATE)
                                val completed = sharedPrefs.getBoolean("onboarding_completed", false)
                                val startRoute = if (completed) "home" else "onboarding"
                                navController.navigate(startRoute) {
                                    popUpTo("splash") { inclusive = true }
                                }
                            })
                        }
                        
                        composable("onboarding") {
                            OnboardingScreen(
                                viewModel = viewModel,
                                onOnboardingComplete = {
                                    navController.navigate("home") {
                                        popUpTo("onboarding") { inclusive = true }
                                    }
                                }
                            )
                        }
                        
                        composable("home") {
                            HomeScreen(
                                viewModel = viewModel,
                                onProductClick = { productId ->
                                    navController.navigate("product_details/$productId")
                                },
                                onNavigateToFavorites = {
                                    navController.navigate("favorites")
                                },
                                onNavigateToCart = {
                                    navController.navigate("cart")
                                },
                                onNavigateToOnboarding = {
                                    val sharedPrefs = this@MainActivity.getSharedPreferences("teke_prefs", android.content.Context.MODE_PRIVATE)
                                    sharedPrefs.edit().putBoolean("onboarding_completed", false).apply()
                                    navController.navigate("onboarding") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                            )
                        }
                        
                        composable(
                            route = "product_details/{productId}",
                            arguments = listOf(navArgument("productId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val productId = backStackEntry.arguments?.getInt("productId") ?: 1
                            ProductDetailsScreen(
                                viewModel = viewModel,
                                productId = productId,
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                        
                        composable("favorites") {
                            WishlistScreen(
                                viewModel = viewModel,
                                onProductClick = { productId ->
                                    navController.navigate("product_details/$productId")
                                }
                            )
                        }
                        
                        composable("cart") {
                            CartScreen(viewModel = viewModel)
                        }
                        
                        composable("contact") {
                            ContactScreen(
                                viewModel = viewModel,
                                onProductClick = { productId ->
                                    navController.navigate("product_details/$productId")
                                }
                            )
                        }
                        
                        composable("settings") {
                            SettingsScreen(
                                viewModel = viewModel,
                                onNavigateToAdmin = { navController.navigate("admin") }
                            )
                        }

                        composable("admin") {
                            AdminScreen(
                                viewModel = viewModel,
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
