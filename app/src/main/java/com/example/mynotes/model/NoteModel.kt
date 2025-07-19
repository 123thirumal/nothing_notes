package com.example.mynotes.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID


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
    @PrimaryKey val id: String =  UUID.randomUUID().toString(),
    var title: String?="",
    val createdAt: Long = 0L,
    var updatedAt: Long = System.currentTimeMillis(),
    var folderId: String?=null,
    var Private: Boolean = false,
    val SavedInCloud: Boolean = false,
    val Synced: Boolean = false
) {}