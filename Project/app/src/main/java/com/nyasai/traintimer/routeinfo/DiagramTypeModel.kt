package com.nyasai.traintimer.routeinfo

import com.nyasai.traintimer.define.Define
import java.util.*
import com.github.kittinunf.fuel.httpGet

class DiagramTypeModel {

    // 祝日判定用APIURL
    private val PublicHolidayJudgeAPIUrl: String =
        "http://s-proj.com/utils/checkHoliday.php?kind=ph"

    /**
     * 次のダイア種別を取得
     * @param current 現在の種別
     */
    fun getNextDiagramType(current: Define.DiagramType?): Define.DiagramType {
        // 平日 ⇒ 土曜 ⇒ 日曜・祝日 ⇒ 平日
        return when (current) {
            Define.DiagramType.Weekday -> Define.DiagramType.Saturday
            Define.DiagramType.Saturday -> Define.DiagramType.Sunday
            Define.DiagramType.Sunday -> Define.DiagramType.Weekday
            else -> Define.DiagramType.Weekday
        }
    }

    /**
     * 今日のダイア種別を取得
     * @param judgePublicHoliday 祝日の判定を行うか
     */
    fun getTodayDiagramType(judgePublicHoliday: Boolean): Define.DiagramType {
        var type = when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            1 -> Define.DiagramType.Sunday
            7 -> Define.DiagramType.Saturday
            else -> Define.DiagramType.Weekday
        }
        if (judgePublicHoliday) {

            val response = PublicHolidayJudgeAPIUrl.httpGet().response()
            if (response.second.responseMessage == "holiday") {
                type = Define.DiagramType.Sunday
            }
        }
        return type
    }
}