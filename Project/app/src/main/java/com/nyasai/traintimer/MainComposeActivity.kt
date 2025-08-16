package com.nyasai.traintimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nyasai.traintimer.routeinfo.RouteInfoScreenRefactored
import com.nyasai.traintimer.routelist.RouteListScreenRefactored
import com.nyasai.traintimer.setting.PreferenceScreen
import com.nyasai.traintimer.ui.theme.TrainTimerTheme

/**
 * メインアクティビティ（Compose版）
 */
class MainComposeActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            TrainTimerTheme {
                TrainTimerApp()
            }
        }
    }
}

/**
 * TrainTimerアプリケーションのメインコンポーネント
 */
@Composable
fun TrainTimerApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        TrainTimerNavigation(
            navController = navController
        )
    }
}

/**
 * ナビゲーション設定
 */
@Composable
fun TrainTimerNavigation(
    navController: NavHostController
) {
    val context = LocalContext.current
    
    NavHost(
        navController = navController,
        startDestination = "route_list"
    ) {
        // 路線一覧画面
        composable("route_list") {
            RouteListScreenRefactored(
                onRouteItemClick = { dataId ->
                    navController.navigate("route_info/$dataId")
                },
                onSettingsClick = {
                    navController.navigate("settings")
                }
            )
        }
        
        // 路線詳細画面
        composable("route_info/{parentDataId}") { backStackEntry ->
            val parentDataId = backStackEntry.arguments?.getString("parentDataId")?.toLongOrNull() ?: 0L
            
            RouteInfoScreenRefactored(
                parentDataId = parentDataId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        // 設定画面
        composable("settings") {
            PreferenceScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}