package com.nyasai.traintimer.routeinfo

import com.github.kittinunf.fuel.core.isSuccessful
import com.github.kittinunf.fuel.httpGet
import com.nyasai.traintimer.define.Define
import com.nyasai.traintimer.http.IHttpClient
import com.nyasai.traintimer.util.YahooRouteInfoGetter
import java.lang.Exception
import java.util.*

open class DiagramTypeModel(calendar: Calendar, httpClient: IHttpClient) {

    // 祝日判定用APIのURL
    private val _publicHolidayJudgeAPIUrl: String =
        "http://s-proj.com/utils/checkHoliday.php?kind=ph"

    private val _calendar: Calendar

    private val _httpClient: IHttpClient

    init {
        _calendar = calendar
        _httpClient = httpClient
    }

    /**
     * 次のダイア種別を取得
     * @param current 現在の種別
     */
    fun getNextDiagramType(current: YahooRouteInfoGetter.Companion.DiagramType?): YahooRouteInfoGetter.Companion.DiagramType {
        // 平日 ⇒ 土曜 ⇒ 日曜・祝日 ⇒ 平日
        return when (current) {
            YahooRouteInfoGetter.Companion.DiagramType.Weekday -> YahooRouteInfoGetter.Companion.DiagramType.Saturday
            YahooRouteInfoGetter.Companion.DiagramType.Saturday -> YahooRouteInfoGetter.Companion.DiagramType.Holiday
            YahooRouteInfoGetter.Companion.DiagramType.Holiday -> YahooRouteInfoGetter.Companion.DiagramType.Weekday
            else -> YahooRouteInfoGetter.Companion.DiagramType.Weekday
        }
    }

    /**
     * 今日のダイア種別を取得
     * @param judgePublicHoliday 祝日の判定を行うか
     */
    fun getTodayDiagramType(judgePublicHoliday: Boolean): YahooRouteInfoGetter.Companion.DiagramType {
        var type = when (_calendar.get(Calendar.DAY_OF_WEEK)) {
            1 -> YahooRouteInfoGetter.Companion.DiagramType.Holiday
            7 -> YahooRouteInfoGetter.Companion.DiagramType.Saturday
            else -> YahooRouteInfoGetter.Companion.DiagramType.Weekday
        }
        if (judgePublicHoliday) {
            try {
                if (isHoliday()) {
                    type = YahooRouteInfoGetter.Companion.DiagramType.Holiday
                }
            }
            catch (e: Exception) {

            }
        }
        return type
    }

    /**
     * 休日か
     */
    protected open fun isHoliday(): Boolean {
        val response = _httpClient.httpGet(_publicHolidayJudgeAPIUrl)
        if (response.isSuccessful && String(response.data) == "holiday") {
            return true
        }
        return false
    }
}