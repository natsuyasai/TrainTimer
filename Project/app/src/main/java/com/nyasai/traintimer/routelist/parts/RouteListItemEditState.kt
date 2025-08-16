package com.nyasai.traintimer.routelist.parts

import androidx.compose.runtime.*

/**
 * 編集種別
 */
enum class EditType {
    None,
    Update,
    Delete,
    SetColor
}

/**
 * 路線一覧アイテム編集の状態
 */
@Stable
data class RouteListItemEditState(
    val selectedEditType: EditType = EditType.Update,
    val targetDataId: Long? = null
)

/**
 * 路線一覧アイテム編集のアクション
 */
interface RouteListItemEditActions {
    fun updateEditType(editType: EditType)
    fun setTargetDataId(dataId: Long?)
    fun clearUIData()
}

/**
 * 路線一覧アイテム編集のState Holder
 */
@Stable
class RouteListItemEditStateHolder {
    var state by mutableStateOf(RouteListItemEditState())
        private set
    
    val actions = object : RouteListItemEditActions {
        override fun updateEditType(editType: EditType) {
            state = state.copy(selectedEditType = editType)
        }
        
        override fun setTargetDataId(dataId: Long?) {
            state = state.copy(targetDataId = dataId)
        }
        
        override fun clearUIData() {
            state = RouteListItemEditState()
        }
    }
}

/**
 * RouteListItemEditStateHolderを作成するComposable関数
 */
@Composable
fun rememberRouteListItemEditState(): RouteListItemEditStateHolder {
    return remember { RouteListItemEditStateHolder() }
}