package com.nyasai.traintimer.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

/**
 * DBDao
 */
@Dao
interface RouteDatabaseDao {

    // region 路線リストアイテム操作

    /**
     * 路線アイテム追加
     * @param routeListItem 追加アイテム
     */
    @Insert
    fun insertRouteListItem(routeListItem: RouteListItem)

    /**
     * 路線アイテム更新
     * @param routeListItem 更新アイテム
     */
    @Update
    fun updateRouteListItem(routeListItem: RouteListItem)

    @Update
    fun updateRouteListItems(routeListItem: List<RouteListItem>)

    /**
     * 路線アイテム削除
     */
    @Query("DELETE FROM route_list_item_table WHERE dataId = :id")
    fun deleteRouteListItem(id: Long)

    /**
     * 路線アイテム全クリア
     */
    @Query("DELETE FROM route_list_item_table")
    fun clearAllRouteListItem()

    /**
     * 路線アイテム取得
     * @param id 路線データID
     */
    @Query("SELECT * from route_list_item_table WHERE dataId = :id")
    fun getRouteListItemWithId(id: Long): LiveData<RouteListItem>

    /**
     * 路線アイテム取得(同期)
     * @param id 路線データID
     */
    @Query("SELECT * from route_list_item_table WHERE dataId = :id")
    fun getRouteListItemWithIdSync(id: Long): RouteListItem?

    /**
     * 路線アイテム一覧取得
     */
    @Query("SELECT * from route_list_item_table ORDER BY sort_index")
    fun getAllRouteListItems(): LiveData<List<RouteListItem>>

    /**
     * 路線アイテム一覧取得(同期)
     */
    @Query("SELECT * from route_list_item_table ORDER BY sort_index")
    fun getAllRouteListItemsSync(): List<RouteListItem>

    /**
     * 路線アイテム一覧降順取得(同期)
     */
    @Query("SELECT * from route_list_item_table ORDER BY sort_index DESC")
    fun getDestAllRouteListItemsSync(): List<RouteListItem>

    @Query("SELECT MAX(sort_index) FROM route_list_item_table")
    fun getMaxSortIndex(): Long

    // endregion 路線リストアイテム操作

    // region 路線情報詳細操作

    /**
     * 路線詳細アイテム追加
     * @param routeDetail 追加アイテム
     */
    @Insert
    fun insertRouteDetailItem(routeDetail: RouteDetail)

    /**
     * 路線詳細アイテム追加
     * @param routeDetail 追加アイテム一覧
     */
    @Insert
    fun insertRouteDetailItems(routeDetail: List<RouteDetail>)

    /**
     * 路線詳細アイテム更新
     * @param routeDetail 更新アイテム
     */
    @Update
    fun updateRouteDetailItem(routeDetail: RouteDetail)

    /**
     * 路線詳細アイテム更新
     * @param routeDetail 更新アイテム一覧
     */
    @Update
    fun updateRouteDetailItems(routeDetail: List<RouteDetail>)

    /**
     * 路線詳細アイテム削除
     */
    @Query("DELETE FROM route_details_table WHERE dataId = :id")
    fun deleteRouteDetailItem(id: Long)

    /**
     * 路線詳細アイテム削除
     */
    @Query("DELETE FROM route_details_table WHERE parent_row_id = :id")
    fun deleteRouteDetailItemWithParentId(id: Long)

    /**
     * 路線詳細アイテム全クリア
     */
    @Query("DELETE FROM route_details_table")
    fun clearAllRouteDetailItem()

    /**
     * 路線詳細アイテム取得
     * @param id 路線データID
     */
    @Query("SELECT * from route_details_table WHERE dataId = :id")
    fun getRouteDetailItemWithId(id: Long): RouteDetail?

    /**
     * 路線詳細アイテム一覧取得(親アイテムID指定)
     * @param parentId 親アイテムID
     */
    @Query("SELECT * from route_details_table WHERE parent_row_id = :parentId ORDER BY dataId")
    fun getRouteDetailItemsWithParentId(parentId: Long): LiveData<List<RouteDetail>>

    /**
     * 路線詳細アイテム一覧取得(親アイテムID指定)同期
     * @param parentId 親アイテムID
     */
    @Query("SELECT * from route_details_table WHERE parent_row_id = :parentId ORDER BY dataId")
    fun getRouteDetailItemsWithParentIdSync(parentId: Long): List<RouteDetail>

    /**
     * 路線詳細アイテム一覧取得(親アイテムID指定)
     * @param parentId 親アイテムID
     */
    @Query("SELECT * from route_details_table WHERE parent_row_id = :parentId AND diagram_type = :diagramType ORDER BY dataId")
    fun getCurrentDiagramRouteDetailItemsWithParentId(
        parentId: Long,
        diagramType: Int
    ): LiveData<List<RouteDetail>>

    @Query("SELECT * from route_details_table WHERE parent_row_id = :parentId AND diagram_type = :diagramType ORDER BY dataId")
    fun getCurrentDiagramRouteDetailItemsWithParentIdSync(
        parentId: Long,
        diagramType: Int
    ): List<RouteDetail>

    /**
     * 路線詳細アイテム一覧取得
     */
    @Query("SELECT * from route_details_table ORDER BY dataId")
    fun getAllRouteDetailItems(): LiveData<List<RouteDetail>>

    /**
     * 路線詳細アイテム一覧取得(同期)
     */
    @Query("SELECT * from route_details_table ORDER BY dataId")
    fun getAllRouteDetailItemsSync(): List<RouteDetail>

    // endregion 路線情報詳細操作

    // region フィルタ情報操作

    /**
     * フィルタ情報アイテム追加
     * @param filterInfo 追加アイテム
     */
    @Insert
    fun insertFilterInfoItem(filterInfo: FilterInfo)

    /**
     * フィルタ情報アイテム追加
     * @param filterInfoList 追加アイテム一覧
     */
    @Insert
    fun insertFilterInfoItems(filterInfoList: List<FilterInfo>)

    /**
     * フィルタ情報アイテム更新
     * @param filterInfo 更新アイテム
     */
    @Update
    fun updateFilterInfoItem(filterInfo: FilterInfo)

    @Update
    fun updateFilterInfoListItem(filterInfoList: List<FilterInfo>)

    /**
     * フィルタ情報アイテム削除
     */
    @Query("DELETE FROM filter_info_table WHERE dataId = :id")
    fun deleteFilterInfoItem(id: Long)

    /**
     * フィルタ情報アイテム削除
     */
    @Query("DELETE FROM filter_info_table WHERE parent_row_id = :id")
    fun deleteFilterInfoItemWithParentId(id: Long)

    /**
     * フィルタ情報アイテム一覧取得(親アイテムID指定)
     * @param parentId 親アイテムID
     */
    @Query("SELECT * from filter_info_table WHERE parent_row_id = :parentId ORDER BY dataId")
    fun getFilterInfoItemWithParentId(parentId: Long): LiveData<List<FilterInfo>>

    /**
     * フィルタ情報アイテム一覧取得(親アイテムID指定)同期
     * @param parentId 親アイテムID
     */
    @Query("SELECT * from filter_info_table WHERE parent_row_id = :parentId ORDER BY dataId")
    fun getFilterInfoItemWithParentIdSync(parentId: Long): List<FilterInfo>

    /**
     * フィルタ情報アイテム一覧取得(同期)
     */
    @Query("SELECT * from filter_info_table ORDER BY dataId")
    fun getAllFilterInfoItemSync(): List<FilterInfo>

    @Query("DELETE FROM filter_info_table")
    fun clearAllFilterInfo()

    // endregion フィルタ情報操作
}