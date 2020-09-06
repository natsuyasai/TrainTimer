package com.nyasai.traintimer.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        RouteListItem::class,
        RouteDetail::class,
        FilterInfo::class
    ],
    version = 4,
    exportSchema = true)
abstract class RouteDatabase : RoomDatabase() {
    /**
     * Connects the database to the DAO.
     */
    abstract val routeDatabaseDao: RouteDatabaseDao

    /**
     * シングルトン
     */
    companion object {

        @Volatile
        private var INSTANCE: RouteDatabase? = null


        /**
         * インスタンス取得
         */
        fun getInstance(context: Context): RouteDatabase {
            // マイグレーション用
            val migration1to2 = object : Migration(1,2) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    // 路線一覧テーブル新規作成
                    database.execSQL("CREATE TABLE IF NOT EXISTS route_list_item_table (dataId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, route_name TEXT NOT NULL, station_name TEXT NOT NULL, destination TEXT NOT NULL, sort_index INTEGER NOT NULL)")

                    // 既にある場合の更新処理
                    database.execSQL("ALTER TABLE route_list_item_table ADD sort_index INTEGER NOT NULL DEFAULT 0")
                    database.execSQL("CREATE TABLE IF NOT EXISTS tmp_route_list_item_table (dataId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, route_name TEXT NOT NULL, station_name TEXT NOT NULL, destination TEXT NOT NULL, sort_index INTEGER NOT NULL)")
                    database.execSQL("INSERT OR REPLACE INTO tmp_route_list_item_table SELECT * FROM route_list_item_table")
                    database.execSQL("DROP TABLE route_list_item_table")
                    database.execSQL("ALTER TABLE tmp_route_list_item_table RENAME TO route_list_item_table")

                }
            }
            val migration2to3 = object : Migration(2,3) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    // 詳細テーブル新規作成
                    database.execSQL("CREATE TABLE IF NOT EXISTS route_details_table (dataId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, parent_row_id INTEGER NOT NULL, diagram_type INTEGER NOT NULL, departure_time TEXT NOT NULL, train_type TEXT NOT NULL, destination TEXT NOT NULL, FOREIGN KEY(parent_row_id) REFERENCES route_list_item_table(dataId) ON UPDATE NO ACTION ON DELETE CASCADE )")

                    // 既にある場合の更新処理
                    database.execSQL("CREATE TABLE IF NOT EXISTS tmp_route_details_table (dataId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, parent_row_id INTEGER NOT NULL, diagram_type INTEGER NOT NULL, departure_time TEXT NOT NULL, train_type TEXT NOT NULL, destination TEXT NOT NULL, FOREIGN KEY(parent_row_id) REFERENCES route_list_item_table(dataId) ON UPDATE NO ACTION ON DELETE CASCADE )")
                    database.execSQL("INSERT OR REPLACE INTO tmp_route_details_table SELECT * FROM route_details_table")
                    database.execSQL("DROP TABLE route_details_table")
                    database.execSQL("ALTER TABLE tmp_route_details_table RENAME TO route_details_table")
                }
            }
            val migration3to4 = object : Migration(3,4) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    // 詳細テーブル新規作成
                    database.execSQL("CREATE TABLE IF NOT EXISTS filter_info_table (dataId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, parent_row_id INTEGER NOT NULL, train_type_and_destination TEXT NOT NULL, is_show INTEGER NOT NULL, FOREIGN KEY(parent_row_id) REFERENCES route_list_item_table(dataId) ON UPDATE NO ACTION ON DELETE CASCADE )")

                    // 既にある場合の更新処理
                    database.execSQL("CREATE TABLE IF NOT EXISTS tmp_filter_info_table (dataId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, parent_row_id INTEGER NOT NULL, train_type_and_destination TEXT NOT NULL, is_show INTEGER NOT NULL, FOREIGN KEY(parent_row_id) REFERENCES route_list_item_table(dataId) ON UPDATE NO ACTION ON DELETE CASCADE )")
                    database.execSQL("INSERT OR REPLACE INTO tmp_filter_info_table SELECT * FROM filter_info_table")
                    database.execSQL("DROP TABLE filter_info_table")
                    database.execSQL("ALTER TABLE tmp_filter_info_table RENAME TO filter_info_table")
                }
            }
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    // DBインスタンス作成
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        RouteDatabase::class.java,
                        "route_database")
                        .addMigrations(migration1to2)
                        .addMigrations(migration2to3)
                        .addMigrations(migration3to4)
                        .build()
                    INSTANCE = instance
                }

                return instance
            }
        }


    }
}