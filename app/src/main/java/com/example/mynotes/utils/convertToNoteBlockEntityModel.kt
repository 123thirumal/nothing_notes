package com.example.mynotes.utils

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.unit.dp
import com.example.mynotes.data.viewmodel.NoteBlockViewModel
import com.example.mynotes.model.NoteBlockEntityModel
import com.example.mynotes.model.NoteBlockModel
import androidx.core.net.toUri
import com.example.mynotes.model.CheckListModel
import com.example.mynotes.model.ListModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


fun convertToNoteBlockEntityModel(
    block: NoteBlockModel,
): NoteBlockEntityModel {

    when(block.type){

        "text" -> {
            return NoteBlockEntityModel.TextBlock(description = mutableStateOf(block.description?:""))
        }


        "image" -> {
            return NoteBlockEntityModel.ImageBlock(
                uri = mutableStateOf(block.imageUri?.toUri()),
                isImgResize = mutableStateOf(block.isImgResize ?: false),
                isImgDropdown = mutableStateOf(block.isImgDropdown ?: false),
                imgWidth = mutableStateOf((block.imgWidth ?: 200f).dp),
                imgHeight = mutableStateOf((block.imgHeight ?: 200f).dp)
            )
        }


        "check_list" -> {
            val items: List<CheckListModel> = Gson().fromJson(
                block.checklistItems ?: "[]",
                object : TypeToken<List<CheckListModel>>() {}.type
            )
            return NoteBlockEntityModel.CheckListBlock(
                items = items.toMutableStateList()
            )
        }


        "numbered_list" -> {
            val items: List<ListModel> = Gson().fromJson(
                block.numberedItems ?: "[]",
                object : TypeToken<List<ListModel>>() {}.type
            )
            return NoteBlockEntityModel.NumberedListBlock(
                items = items.toMutableStateList()
            )
        }

        "bulleted_list" -> {
            val items: List<ListModel> = Gson().fromJson(
                block.bulletedItems ?: "[]",
                object : TypeToken<List<ListModel>>() {}.type
            )
            return NoteBlockEntityModel.BulletedListBlock(
                items = items.toMutableStateList()
            )
        }

        "voice_note" ->{
            return NoteBlockEntityModel.VoiceBlock(
                uri = mutableStateOf(block.audioPath?.toUri())
            )
        }

        else -> {
            throw IllegalArgumentException("Unsupported block type: ${block.type}")
        }
    }
}