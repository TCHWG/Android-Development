package com.kuroakevizago.aira.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kuroakevizago.aira.data.local.entity.MusicEntity


@Database(entities = [MusicEntity::class], version = 1)
abstract class AiraRoomDatabase : RoomDatabase() {

    abstract fun bookmarkDao() : IBookmarkDao

    companion object {
        @Volatile
        private var INSTANCE: AiraRoomDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): AiraRoomDatabase {
            if (INSTANCE == null) {
                synchronized(AiraRoomDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        AiraRoomDatabase::class.java, "aira")
                        .build()
                }
            }
            return INSTANCE as AiraRoomDatabase
        }
    }
}