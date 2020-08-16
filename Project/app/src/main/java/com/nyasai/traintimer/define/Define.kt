package com.nyasai.traintimer.define

class Define {

    // ダイヤ種別
    enum class DiagramType {
        // 平日
        Weekday,
        // 土曜
        Saturday,
        // 日曜祝日
        Sunday
    }

    companion object{
        // 路線リスト削除確認ダイアログ引数(データID)
        const val ROUTE_LIST_DELETE_CONFIRM_ARGMENT_DATAID = "DATA_ID"
    }
}