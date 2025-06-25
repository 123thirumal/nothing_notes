package com.example.mynotes.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "folders_table")
data class FolderModel (
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    var title: String="",
    val createdAt: Long = System.currentTimeMillis()
){}