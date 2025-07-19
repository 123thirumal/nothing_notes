package com.example.mynotes.utils

import android.util.Log
import com.example.mynotes.model.NoteBlockEntityModel
import com.example.mynotes.model.NoteBlockModel
import com.google.gson.Gson

fun convertToNoteBlockModel(
    block: NoteBlockEntityModel,
    noteId: String,
    order: Int
): NoteBlockModel {
    when (block) {
        is NoteBlockEntityModel.TextBlock -> {
            return NoteBlockModel(
                noteId = noteId,
                type = "text",
                description = block.description.value,
                blockOrder = order,
            )
        }

        is NoteBlockEntityModel.ImageBlock ->{
            return NoteBlockModel(
                noteId = noteId,
                type = "image",
                imageUri = block.uri.value?.toString(),
                isImgResize = block.isImgResize.value,
                isImgDropdown = block.isImgDropdown.value,
                imgWidth = block.imgWidth.value.value,
                imgHeight = block.imgHeight.value.value,
                blockOrder = order
            )
        }

        is NoteBlockEntityModel.BulletedListBlock ->{
            return NoteBlockModel(
                noteId = noteId,
                type = "bulleted_list",
                bulletedItems = Gson().toJson(block.items),
                blockOrder = order
            )
        }

        is NoteBlockEntityModel.CheckListBlock -> {
            return NoteBlockModel(
                noteId = noteId,
                type = "check_list",
                checklistItems = Gson().toJson(block.items),
                blockOrder = order
            )
        }

        is NoteBlockEntityModel.NumberedListBlock -> {
            return NoteBlockModel(
                noteId = noteId,
                type = "numbered_list",
                numberedItems = Gson().toJson(block.items),
                blockOrder = order
            )
        }

        is NoteBlockEntityModel.VoiceBlock -> {
            return NoteBlockModel(
                noteId = noteId,
                type = "voice_note",
                audioPath = block.uri.value?.toString(),
                blockOrder = order
            )
        }
    }
}