package com.kuroakevizago.aira.data.local.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kuroakevizago.aira.data.local.entity.MusicEntity

@Dao
interface IBookmarkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMusic(music: MusicEntity)

    @Query("DELETE FROM bookmarked_music WHERE id = :musicId")
    suspend fun removeMusic(musicId: String)

    @Query("SELECT COUNT(*) > 0 FROM bookmarked_music WHERE id = :musicId AND userId = :userId")
    suspend fun isMusicBookmarked(musicId: String, userId: String): Boolean

    @Query("SELECT * FROM bookmarked_music WHERE userId = :userId")
    fun getBookmarksForUser(userId: String): LiveData<List<MusicEntity>>
}