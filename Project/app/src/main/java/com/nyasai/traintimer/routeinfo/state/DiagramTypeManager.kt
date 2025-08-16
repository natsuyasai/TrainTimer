package com.nyasai.traintimer.routeinfo.state

import androidx.compose.runtime.Stable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nyasai.traintimer.http.HttpClient
import com.nyasai.traintimer.routeinfo.logic.DiagramTypeModel
import com.nyasai.traintimer.util.YahooRouteInfoGetter
import java.util.Calendar

/**
 * ダイア種別管理を担当するクラス
 */
@Stable
class DiagramTypeManager {
    // 現在の表示ダイア種別
    private var _currentDiagramType: MutableLiveData<YahooRouteInfoGetter.Companion.DiagramType> =
        MutableLiveData()
    val currentDiagramType: LiveData<YahooRouteInfoGetter.Companion.DiagramType> =
        _currentDiagramType
    
    // ダイア種別用モデルクラス
    private val _diagramTypeModel: DiagramTypeModel =
        DiagramTypeModel(Calendar.getInstance(), HttpClient())
    
    init {
        _currentDiagramType.value = _diagramTypeModel.getTodayDiagramType(false)
    }
    
    /**
     * 初期化（祝日ダイア設定）
     */
    fun initializeAsync() {
        trySetPublicHolidayDiagramTypeAsync()
    }
    
    /**
     * 祝日ダイア設定
     */
    private fun trySetPublicHolidayDiagramTypeAsync() {
        _currentDiagramType.postValue(_diagramTypeModel.getTodayDiagramType(true))
    }
    
    /**
     * 次の表示ダイアに設定
     */
    fun setNextDiagramType() {
        _currentDiagramType.value = _diagramTypeModel.getNextDiagramType(_currentDiagramType.value)
    }
    
    /**
     * 現在のダイア種別を取得
     */
    fun getCurrentDiagramType(): YahooRouteInfoGetter.Companion.DiagramType? {
        return _currentDiagramType.value
    }
}