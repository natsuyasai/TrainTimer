package com.nyasai.traintimer.routeinfo

import com.github.kittinunf.fuel.core.isSuccessful
import com.github.kittinunf.fuel.httpGet
import com.nyasai.traintimer.define.Define
import com.nyasai.traintimer.util.YahooRouteInfoGetter
import java.lang.Exception
import java.util.*

class DiagramTypeModel {

    // 祝日判定用APIのURL
    private val _publicHolidayJudgeAPIUrl: String =
        "http://s-proj.com/utils/checkHoliday.php?kind=ph"

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
        var type = when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            1 -> YahooRouteInfoGetter.Companion.DiagramType.Holiday
            7 -> YahooRouteInfoGetter.Companion.DiagramType.Saturday
            else -> YahooRouteInfoGetter.Companion.DiagramType.Weekday
        }
        if (judgePublicHoliday) {
            try {
                val response = _publicHolidayJudgeAPIUrl.httpGet().response()
                if (response.second.isSuccessful && String(response.second.data) == "holiday") {
                    type = YahooRouteInfoGetter.Companion.DiagramType.Holiday
                }
            }
            catch (e: Exception) {

            }
        }
        return type
    }
}