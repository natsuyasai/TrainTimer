package com.nyasai.traintimer.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        RouteListItem::class,
        RouteDetail::class,
        FilterInfo::class
    ],
    version = 1, exportSchema = false)
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
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    // DBインスタンス作成
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        RouteDatabase::class.java,
                        "route_database")
                        .build()
                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}