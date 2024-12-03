package com.kuroakevizago.aira.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarked_music")
class MusicEntity (
    @PrimaryKey
    val id: String,
    val name: String,
    val userId: String,
    val difficulty: String? = null,
    val musicDescription: String? = null,
    val author: String? = null,
)