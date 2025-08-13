package com.nyasai.traintimer.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nyasai.traintimer.commonparts.CommonLoadingCompose
import com.nyasai.traintimer.commonparts.CommonLoadingViewModel
import com.nyasai.traintimer.database.RouteDetail
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.routeinfo.RouteInfoItemCompose
import com.nyasai.traintimer.routeinfo.RouteInfoTitleCompose
import com.nyasai.traintimer.routelist.RouteListItemCompose
import com.nyasai.traintimer.util.YahooRouteInfoGetter

/**
 * Composeレイアウトコンポーネントの使用例
 */

/**
 * CommonLoadingCompose の使用例
 */
@Composable
fun CommonLoadingUsageExample() {
    val viewModel: CommonLoadingViewModel = viewModel()
    
    Box(modifier = Modifier.fillMaxSize()) {
        // メインコンテンツ
        Column {
            Text("メインコンテンツ")
            Button(onClick = { 
                viewModel.showLoading("データ読み込み中")
            }) {
                Text("ローディング開始")
            }
            Button(onClick = { 
                viewModel.closeLoading()
            }) {
                Text("ローディング終了")
            }
        }
        
        // ローディングオーバーレイ
        CommonLoadingCompose(viewModel = viewModel)
    }
}

/**
 * RouteListItemCompose の使用例
 */
@Composable
fun RouteListItemUsageExample() {
    val sampleRouteListItem = RouteListItem().apply {
        routeName = "JR山手線"
        stationName = "新宿駅"
        destination = "渋谷・品川方面"
    }
    
    LazyColumn {
        items(listOf(sampleRouteListItem, sampleRouteListItem)) { item ->
            RouteListItemCompose(
                routeListItem = item,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * RouteInfoItemCompose の使用例
 */
@Composable
fun RouteInfoItemUsageExample() {
    val sampleRouteDetail = RouteDetail().apply {
        departureTime = "12:34"
        trainType = "快速"
        destination = "新宿"
    }
    
    LazyColumn {
        items(listOf(sampleRouteDetail, sampleRouteDetail)) { item ->
            RouteInfoItemCompose(
                routeDetail = item,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * RouteInfoTitleCompose の使用例
 */
@Composable
fun RouteInfoTitleUsageExample() {
    val sampleRouteListItem = RouteListItem().apply {
        routeName = "JR山手線"
        stationName = "新宿駅"
        destination = "渋谷・品川方面"
    }
    
    Column {
        RouteInfoTitleCompose(
            routeListItem = sampleRouteListItem,
            currentDiagramType = YahooRouteInfoGetter.Companion.DiagramType.Weekday,
            onTitleClick = { 
                // タイトルクリック時の処理
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        RouteInfoTitleCompose(
            routeListItem = sampleRouteListItem,
            currentDiagramType = YahooRouteInfoGetter.Companion.DiagramType.Saturday,
            onTitleClick = { 
                // タイトルクリック時の処理
            }
        )
    }
}

/**
 * 全コンポーネントの統合例
 */
@Composable
fun IntegratedLayoutExample() {
    val commonLoadingViewModel: CommonLoadingViewModel = viewModel()
    
    val sampleRouteListItem = RouteListItem().apply {
        routeName = "JR山手線"
        stationName = "新宿駅"
        destination = "渋谷・品川方面"
    }
    
    val sampleRouteDetails = listOf(
        RouteDetail().apply {
            departureTime = "12:34"
            trainType = "快速"
            destination = "新宿"
        },
        RouteDetail().apply {
            departureTime = "12:45"
            trainType = "普通"
            destination = "池袋"
        }
    )
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            // タイトル
            RouteInfoTitleCompose(
                routeListItem = sampleRouteListItem,
                currentDiagramType = YahooRouteInfoGetter.Companion.DiagramType.Weekday,
                onTitleClick = { 
                    commonLoadingViewModel.showLoading("データ更新中")
                }
            )
            
            // 詳細リスト
            LazyColumn {
                items(sampleRouteDetails) { detail ->
                    RouteInfoItemCompose(
                        routeDetail = detail,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        
        // ローディング
        CommonLoadingCompose(viewModel = commonLoadingViewModel)
    }
}