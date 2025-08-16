package com.nyasai.traintimer.routeinfo.logic

import com.nyasai.traintimer.database.RouteDetail
import com.nyasai.traintimer.routeinfo.RouteInfoScreenState

/**
 * ユーザーインタラクション管理機能を管理するクラス
 */
class InteractionManager {
    
    /**
     * タイトルクリック処理（State Holder版）
     */
    fun handleTitleClickWithState(
        screenState: RouteInfoScreenState,
        triggerUpdate: () -> Unit
    ) {
        // ダイヤ種別を切り替え
        screenState.setNextDiagramType()
        // フィルター更新トリガーを増加させて表示を更新
        triggerUpdate()
    }
    
    /**
     * アイテムクリック処理（State Holder版）
     */
    fun handleItemClickWithState(
        screenState: RouteInfoScreenState,
        selectedRouteDetail: RouteDetail
    ) {
        screenState.setCurrentCountItem(selectedRouteDetail)
    }
}