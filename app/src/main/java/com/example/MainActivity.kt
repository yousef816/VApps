package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.RecordingRepository
import com.example.media.AudioPlayerManager
import com.example.media.AudioRecorderManager
import com.example.ui.screens.EditorScreen
import com.example.ui.screens.UpdateScreen
import com.example.ui.screens.PlayerScreen
import com.example.ui.screens.RecordScreen
import com.example.ui.screens.RecordingsListScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.MainViewModelFactory
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {

    private lateinit var database: AppDatabase
    private lateinit var repository: RecordingRepository
    private lateinit var recorderManager: AudioRecorderManager
    private lateinit var playerManager: AudioPlayerManager
    private lateinit var viewModelFactory: MainViewModelFactory
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Initialize dependencies
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "recording-database"
        ).build()
        repository = RecordingRepository(database.recordingDao())
        recorderManager = AudioRecorderManager(applicationContext)
        playerManager = AudioPlayerManager()

        viewModelFactory = MainViewModelFactory(application, repository, recorderManager, playerManager)
        mainViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(mainViewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in listOf("record", "list", "editor", "cloud")) {
                NavigationBar(
                    containerColor = Color(0xFF000000).copy(alpha = 0.4f),
                    contentColor = Color.White,
                    tonalElevation = 0.dp
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Outlined.Mic, contentDescription = "تسجيل") },
                        label = { Text("تسجيل", fontSize = 10.sp) },
                        selected = currentRoute == "record",
                        onClick = { navController.navigate("record") { launchSingleTop = true; restoreState = true } },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFEF4444),
                            selectedTextColor = Color(0xFFEF4444),
                            unselectedIconColor = Color.White.copy(alpha = 0.3f),
                            unselectedTextColor = Color.White.copy(alpha = 0.3f),
                            indicatorColor = Color.Transparent
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Outlined.LibraryMusic, contentDescription = "المكتبة") },
                        label = { Text("المكتبة", fontSize = 10.sp) },
                        selected = currentRoute == "list",
                        onClick = { navController.navigate("list") { launchSingleTop = true; restoreState = true } },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFEF4444),
                            selectedTextColor = Color(0xFFEF4444),
                            unselectedIconColor = Color.White.copy(alpha = 0.3f),
                            unselectedTextColor = Color.White.copy(alpha = 0.3f),
                            indicatorColor = Color.Transparent
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Outlined.ContentCut, contentDescription = "محرر") },
                        label = { Text("محرر", fontSize = 10.sp) },
                        selected = currentRoute == "editor",
                        onClick = { navController.navigate("editor") { launchSingleTop = true; restoreState = true } },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFEF4444),
                            selectedTextColor = Color(0xFFEF4444),
                            unselectedIconColor = Color.White.copy(alpha = 0.3f),
                            unselectedTextColor = Color.White.copy(alpha = 0.3f),
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController, 
            startDestination = "record", 
            modifier = Modifier.padding(padding),
            enterTransition = { androidx.compose.animation.slideInHorizontally { it / 2 } + androidx.compose.animation.fadeIn() },
            exitTransition = { androidx.compose.animation.slideOutHorizontally { -it / 2 } + androidx.compose.animation.fadeOut() },
            popEnterTransition = { androidx.compose.animation.slideInHorizontally { -it / 2 } + androidx.compose.animation.fadeIn() },
            popExitTransition = { androidx.compose.animation.slideOutHorizontally { it / 2 } + androidx.compose.animation.fadeOut() }
        ) {
            composable("record") {
                RecordScreen(
                    viewModel = viewModel,
                    onNavigateToList = { navController.navigate("list") },
                    onNavigateToSettings = { navController.navigate("settings") }
                )
            }
            composable("list") {
                RecordingsListScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToPlayer = { path ->
                        val encoded = URLEncoder.encode(path, StandardCharsets.UTF_8.toString())
                        navController.navigate("player/$encoded")
                    }
                )
            }
            composable("editor") {
                EditorScreen(
                    viewModel = viewModel
                )
            }
            composable(
                "player/{path}",
                arguments = listOf(navArgument("path") { type = NavType.StringType })
            ) { backStackEntry ->
                val path = backStackEntry.arguments?.getString("path") ?: ""
                val decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8.toString())
                PlayerScreen(
                    viewModel = viewModel,
                    path = decodedPath,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("settings") {
                SettingsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToUpdates = { navController.navigate("updates") }
                )
            }
            composable("updates") {
                UpdateScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
