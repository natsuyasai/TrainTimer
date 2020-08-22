package com.nyasai.traintimer.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.nyasai.traintimer.define.Define

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
    @Query("SELECT * from route_list_item_table ORDER BY dataId")
    fun getAllRouteListItems(): LiveData<List<RouteListItem>>

    /**
     * 路線アイテム一覧取得(同期)
     */
    @Query("SELECT * from route_list_item_table ORDER BY dataId")
    fun getAllRouteListItemsSync(): List<RouteListItem>

    // endregion 路線リストアイテム操作


    // region 路線情報詳細操作

    /**
     * 路線詳細アイテム追加
     * @param routeDetails 追加アイテム
     */
    @Insert
    fun insertRouteDetailsItem(routeDetails: RouteDetails)

    /**
     * 路線詳細アイテム更新
     * @param routeDetails 更新アイテム
     */
    @Update
    fun updateRouteDetailsItem(routeDetails: RouteDetails)
    @Update
    fun updateRouteDetailsItems(routeDetails: List<RouteDetails>)

    /**
     * 路線詳細アイテム削除
     */
    @Query("DELETE FROM route_details_table WHERE dataId = :id")
    fun deleteRouteDetailsItem(id: Long)

    /**
     * 路線詳細アイテム全クリア
     */
    @Query("DELETE FROM route_details_table")
    fun clearAllRouteDetailsItem()

    /**
     * 路線詳細アイテム取得
     * @param id 路線データID
     */
    @Query("SELECT * from route_details_table WHERE dataId = :id")
    fun getRouteDetailsItemWithId(id: Long): RouteDetails?

    /**
     * 路線詳細アイテム一覧取得(親アイテムID指定)
     * @param parentId 親アイテムID
     */
    @Query("SELECT * from route_details_table WHERE parent_row_id = :parentId ORDER BY dataId")
    fun getRouteDetailsItemsWithParentId(parentId: Long): LiveData<List<RouteDetails>>

    /**
     * 路線詳細アイテム一覧取得(親アイテムID指定)
     * @param parentId 親アイテムID
     */
    @Query("SELECT * from route_details_table WHERE parent_row_id = :parentId AND diagram_type = :diagramType ORDER BY dataId")
    fun getCurrentDiagramRouteDetailsItemsWithParentId(parentId: Long, diagramType: Int): LiveData<List<RouteDetails>>

    /**
     * 路線詳細アイテム一覧取得(親アイテムID指定)同期
     * @param parentId 親アイテムID
     */
    @Query("SELECT * from route_details_table WHERE parent_row_id = :parentId ORDER BY dataId")
    fun getRouteDetailsItemsWithParentIdSync(parentId: Long): List<RouteDetails>

    /**
     * 路線詳細アイテム一覧取得
     */
    @Query("SELECT * from route_details_table ORDER BY dataId")
    fun getAllRouteDetailsItems(): LiveData<List<RouteDetails>>

    /**
     * 路線詳細アイテム一覧取得(同期)
     */
    @Query("SELECT * from route_details_table ORDER BY dataId")
    fun getAllRouteDetailsItemsSync(): List<RouteDetails>

    // endregion 路線情報詳細操作
}