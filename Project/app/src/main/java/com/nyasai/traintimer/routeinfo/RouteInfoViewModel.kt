package com.nyasai.traintimer.routeinfo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nyasai.traintimer.database.FilterInfo
import com.nyasai.traintimer.database.RouteDatabaseDao
import com.nyasai.traintimer.database.RouteDetail
import com.nyasai.traintimer.define.Define
import com.nyasai.traintimer.util.YahooRouteInfoGetter
import kotlinx.coroutines.Job
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.*

/**
 * 路線詳細情報表示用ViewModel
 */
class RouteInfoViewModel(
    val database: RouteDatabaseDao,
    application: Application,
    parentId: Long
) : AndroidViewModel(application) {

    // 本VM用job
    private val _job = Job()

    // 路線情報
    val routeInfo = database.getRouteListItemWithId(parentId)

    // 路線詳細
    val routeItems = database.getRouteDetailItemsWithParentId(parentId)

    // 現在カウント中のアイテム
    private var _currentCountItem: MutableLiveData<RouteDetail> = MutableLiveData()
    var currentCountItem: LiveData<RouteDetail> = _currentCountItem

    // 現在の表示ダイア種別
    private var _currentDiagramType: MutableLiveData<YahooRouteInfoGetter.Companion.DiagramType> = MutableLiveData()
    var currentDiagramType: LiveData<YahooRouteInfoGetter.Companion.DiagramType> = _currentDiagramType

    // フィルタ情報
    val filterInfo = database.getFilterInfoItemWithParentId(parentId)

    // 表示アイテムキャッシュ
    private var _displayRouteDetailItemCache: List<RouteDetail>? = null

    // 親データID
    private val _parentDataId: Long = parentId

    // ダイア種別用モデルクラス
    private val _diagramTypeModel: DiagramTypeModel = DiagramTypeModel(Calendar.getInstance())

    init {
        _currentDiagramType.value = _diagramTypeModel.getTodayDiagramType(false)
        _currentCountItem.value = getNearTimeItem()
    }

    /**
     * 初期化
     * インスタンス生成以降に初期化したいものの初期化を行う
     */
    fun initializeAsync() {
        trySetPublicHolidayDiagramTypeAsync()
    }

    /**
     * onClearedフック
     */
    override fun onCleared() {
        _job.cancel()
        super.onCleared()
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
        // タイマ表示用に対象データを更新しておく
        updateCurrentCountItem()
    }

    /**
     * カウントダウン中アイテム設定
     */
    fun updateCurrentCountItem(useCache: Boolean = false) {
        _currentCountItem.value = getNearTimeItem(useCache)
    }

    /**
     * 表示用路線詳細アイテム取得
     */
    fun getDisplayRouteDetailItems(useCache: Boolean = false): List<RouteDetail> {
        if (useCache && _displayRouteDetailItemCache != null) {
            return _displayRouteDetailItemCache!!
        }
        return if (routeItems.value == null) {
            listOf()
        } else {
            val filter = routeItems.value?.filter { routeItem ->
                // 表示中のダイア種別かつフィルタONのものだけ抽出
                routeItem.diagramType == currentDiagramType.value?.ordinal
                        && filterInfo.value?.any {
                    it.trainTypeAndDestination == FilterInfo.createFilterKey(
                        routeItem.trainType,
                        routeItem.destination
                    ) && it.isShow
                } ?: true
            }
            // 時刻順ソート
            _displayRouteDetailItemCache = filter?.sortedWith { v1, v2 ->
                val correctedV1 = correctDepartureTimeForSort(v1.departureTime)
                val correctedV2 = correctDepartureTimeForSort(v2.departureTime)
                val diffHour = correctedV1.first - correctedV2.first
                if (diffHour != 0) {
                    diffHour
                } else {
                    correctedV1.second - correctedV2.second
                }
            }
            _displayRouteDetailItemCache ?: listOf()
        }
    }

    /**
     * 次に表示するアイテムの現在時刻からの差分時間取得
     */
    fun getNextDiffTime(): Long {
        // 画面に一番近いデータへの残り時間を設定する(1秒毎)
        if (currentCountItem.value == null) {
            updateCurrentCountItem()
        }
        // データが取得できなければ，ハイフン表示とするために-1を設定
        return when {
            currentCountItem.value != null -> ChronoUnit.SECONDS.between(
                LocalTime.now(), LocalTime.parse(
                    currentCountItem.value?.departureTime
                )
            )
            else -> -1L
        }
    }

    /**
     * ソート用に時刻情報を補正
     * @param departureTime 補正前文字列
     * @return 補正後文字列
     */
    private fun correctDepartureTimeForSort(departureTime: String): Pair<Int, Int> {
        val hour = Integer.parseInt(departureTime.substring(0, 2))
        val minutes = Integer.parseInt(departureTime.substring(3))
        // 一番遅い終電が2時前かつ一番早い始発が4時台のため，間の3時を区切りとする
        if (hour in 0..3) {
            // 0時～3時は24時間表記の24時～27時に変換する
            return Pair(hour + 24, minutes)
        }
        return Pair(hour, minutes)
    }

    /**
     * 直近の時刻のアイテムを取得する
     */
    private fun getNearTimeItem(useCache: Boolean = false): RouteDetail? {
        val now = LocalTime.now()
        for (item in getDisplayRouteDetailItems(useCache)) {
            if (LocalTime.parse(item.departureTime) > now) {
                return item
            }
        }
        return null
    }

    /**
     * フィルタ情報取得(同期)
     */
    fun getFilterInfoItemWithParentIdSync(): List<FilterInfo> {
        return database.getFilterInfoItemWithParentIdSync(_parentDataId)
    }

    /**
     * フィルタ情報更新
     */
    fun updateFilterInfoListItem(data: List<FilterInfo>) {
        database.updateFilterInfoListItem(data)
    }
}