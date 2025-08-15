package com.nyasai.traintimer.routeinfo

import com.nyasai.traintimer.database.RouteDetail

/**
 * ユーザーインタラクション管理機能を管理するクラス
 */
class InteractionManager {
    
    /**
     * タイトルクリック処理
     */
    fun handleTitleClick(
        routeInfoViewModel: RouteInfoViewModel,
        triggerUpdate: () -> Unit
    ) {
        // ダイヤ種別を切り替え
        routeInfoViewModel.setNextDiagramType()
        // フィルター更新トリガーを増加させて表示を更新
        triggerUpdate()
    }
    
    /**
     * アイテムクリック処理
     */
    fun handleItemClick(
        routeInfoViewModel: RouteInfoViewModel,
        selectedRouteDetail: RouteDetail
    ) {
        routeInfoViewModel.setCurrentCountItem(selectedRouteDetail)
    }
}