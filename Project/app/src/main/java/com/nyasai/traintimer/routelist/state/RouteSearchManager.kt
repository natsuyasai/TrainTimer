package com.nyasai.traintimer.routelist.state

import androidx.compose.runtime.Stable
import com.nyasai.traintimer.util.YahooRouteInfoGetter

/**
 * Yahoo路線情報検索機能を管理するクラス
 */
@Stable
class RouteSearchManager {
    
    // Yahoo路線情報取得用
    private val _yahooRouteInfoGetter = YahooRouteInfoGetter()
    
    /**
     * リソースのクリーンアップ
     */
    fun dispose() {
        _yahooRouteInfoGetter.dispose()
    }
    
    /**
     * 駅リスト取得
     */
    fun getStationList(stationName: String) = _yahooRouteInfoGetter.getStationList(stationName)
    
    /**
     * 行先取得(駅名)
     */
    fun getDestinationFromStationName(stationName: String) =
        _yahooRouteInfoGetter.getDestinationFromStationName(stationName)
    
    /**
     * 行先取得(URL)
     */
    fun getDestinationFromUrl(stationUrl: String) =
        _yahooRouteInfoGetter.getDestinationFromUrl(stationUrl)
    
    /**
     * 行先キー分割
     */
    fun splitDestinationKey(keyString: String) =
        _yahooRouteInfoGetter.splitDestinationKey(keyString)
    
    /**
     * 時刻表情報取得
     * @param timeTableUrl 時刻表情報ページURL
     * @param notifyMaxCountCallback 最大カウント値通知コールバック関数
     * @param notifyCountCallback カウント通知コールバック関数
     */
    suspend fun getTimeTableInfo(
        timeTableUrl: String,
        notifyMaxCountCallback: ((Int) -> Unit),
        notifyCountCallback: (() -> Unit)
    ) = _yahooRouteInfoGetter.getTimeTableInfo(
        timeTableUrl,
        notifyMaxCountCallback,
        notifyCountCallback
    )
}