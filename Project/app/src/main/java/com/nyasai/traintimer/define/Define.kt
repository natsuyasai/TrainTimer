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

    /**
     * 定数一覧
     */
    companion object {
        // 路線リスト削除確認ダイアログ引数(データID)
        const val RouteListDeleteConfirmArgentDataId = "DATA_ID"

        // 路線リストアイテム編集種別結果o
        const val RouteListItemEditType = "ITEM_EDIT_TYPE"
    }
}