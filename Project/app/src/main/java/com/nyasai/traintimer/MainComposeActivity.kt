package com.nyasai.traintimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nyasai.traintimer.commonparts.CommonLoadingViewModel
import com.nyasai.traintimer.commonparts.CommonLoadingViewModelFactory
import com.nyasai.traintimer.database.RouteDatabase
import com.nyasai.traintimer.routelist.RouteListScreen
import com.nyasai.traintimer.routelist.RouteListViewModel
import com.nyasai.traintimer.routelist.RouteListViewModelFactory
import com.nyasai.traintimer.routeinfo.RouteInfoScreen
import com.nyasai.traintimer.routeinfo.RouteInfoViewModel
import com.nyasai.traintimer.routeinfo.RouteInfoViewModelFactory
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
    
    // 共通ViewModelインスタンス
    val commonLoadingViewModel = remember {
        ViewModelProvider(
            context as ComponentActivity,
            CommonLoadingViewModelFactory()
        )[CommonLoadingViewModel::class.java]
    }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        TrainTimerNavigation(
            navController = navController,
            commonLoadingViewModel = commonLoadingViewModel
        )
    }
}

/**
 * ナビゲーション設定
 */
@Composable
fun TrainTimerNavigation(
    navController: NavHostController,
    commonLoadingViewModel: CommonLoadingViewModel
) {
    val context = LocalContext.current
    
    NavHost(
        navController = navController,
        startDestination = "route_list"
    ) {
        // 路線一覧画面
        composable("route_list") {
            val routeListViewModel = remember {
                val application = (context as ComponentActivity).application
                ViewModelProvider(
                    context,
                    RouteListViewModelFactory(
                        RouteDatabase.getInstance(application).routeDatabaseDao,
                        application
                    )
                )[RouteListViewModel::class.java]
            }
            
            RouteListScreen(
                onRouteItemClick = { dataId ->
                    navController.navigate("route_info/$dataId")
                },
                onSettingsClick = {
                    navController.navigate("settings")
                },
                routeListViewModel = routeListViewModel,
                commonLoadingViewModel = commonLoadingViewModel
            )
        }
        
        // 路線詳細画面
        composable("route_info/{parentDataId}") { backStackEntry ->
            val parentDataId = backStackEntry.arguments?.getString("parentDataId")?.toLongOrNull() ?: 0L
            
            val routeInfoViewModel = remember(parentDataId) {
                val application = (context as ComponentActivity).application
                ViewModelProvider(
                    context,
                    RouteInfoViewModelFactory(
                        RouteDatabase.getInstance(application).routeDatabaseDao,
                        application,
                        parentDataId
                    )
                )[RouteInfoViewModel::class.java]
            }
            
            RouteInfoScreen(
                parentDataId = parentDataId,
                onBackClick = {
                    navController.popBackStack()
                },
                routeInfoViewModel = routeInfoViewModel
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