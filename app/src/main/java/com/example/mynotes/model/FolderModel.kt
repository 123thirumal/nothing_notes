package com.example.mynotes.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID


@Entity(tableName = "folders_table")
data class FolderModel (
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    var title: String="",
    val createdAt: Long = System.currentTimeMillis()
){}