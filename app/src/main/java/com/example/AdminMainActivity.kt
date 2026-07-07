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
import com.example.data.local.AppDatabase
import com.example.data.repository.GiftRepository
import com.example.ui.screens.AdminScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.GiftViewModel
import com.example.ui.viewmodel.GiftViewModelFactory

class AdminMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup local SQLite database & Repository (Shared with the main app)
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
            MyApplicationTheme(darkTheme = isDarkTheme) {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { _ ->
                    AdminScreen(
                        viewModel = viewModel,
                        onBackClick = {
                            // Since this is the main entry point for the Admin app, backing out closes the app
                            finish()
                        }
                    )
                }
            }
        }
    }
}
