package com.example.mynotes.model

import androidx.compose.runtime.MutableState
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


sealed class NoteBlockEntityModel {
    data class TextBlock(
        var description: MutableState<String>
    ) : NoteBlockEntityModel()


    data class ImageBlock(
        var uri: MutableState<Uri?>,
        var isImgResize: MutableState<Boolean> = mutableStateOf(false),
        var isImgDropdown: MutableState<Boolean> = mutableStateOf(false),
        val imgWidth: MutableState<Dp> = mutableStateOf(200.dp),
        val imgHeight: MutableState<Dp> = mutableStateOf(200.dp)
    ) : NoteBlockEntityModel()


    data class CheckListBlock(
        var items: SnapshotStateList<CheckListModel>
    ) : NoteBlockEntityModel()

    data class NumberedListBlock(
        var items: SnapshotStateList<ListModel>
    ) : NoteBlockEntityModel()

    data class BulletedListBlock(
        var items: SnapshotStateList<ListModel>
    ) : NoteBlockEntityModel()


    data class VoiceBlock(
        val uri: MutableState<Uri?>
    ) : NoteBlockEntityModel(){}


}
