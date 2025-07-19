package com.example.mynotes.model

import androidx.compose.ui.unit.Dp
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "note_blocks",
    foreignKeys = [ForeignKey(
        entity = NoteModel::class,
        parentColumns = ["id"],
        childColumns = ["noteId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class NoteBlockModel(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),

    val noteId: String="", // Foreign key to NoteModel

    val type: String="", // "text", "image", "bulleted_list", "numbered_list", "check_list", "voice_note"

    //for text
    val description: String? = null,

    //for image
    val imageUri: String? = null,
    val isImgResize: Boolean? = null,
    val isImgDropdown: Boolean? = null,
    val imgWidth: Float? = null,
    val imgHeight: Float? = null,

    //for list
    val checklistItems: String? = null,
    val numberedItems: String? = null,
    val bulletedItems: String? = null,


    //for voice_note
    val audioPath: String? = null,

    val blockOrder: Int = 0 // Optional: for keeping order inside the note
){}