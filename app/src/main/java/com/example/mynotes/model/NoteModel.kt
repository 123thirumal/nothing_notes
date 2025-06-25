package com.example.mynotes.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    tableName = "notes_table",
    foreignKeys = [ForeignKey(
        entity = FolderModel::class,
        parentColumns = ["id"],
        childColumns = ["folderId"],
        onDelete = ForeignKey.SET_NULL
    )]
)
data class NoteModel(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    var title: String?="",
    val createdAt: Long = 0L,
    var updatedAt: Long = System.currentTimeMillis(),
    var folderId: Long?=null,
    var isPrivate: Boolean = false,
) {}