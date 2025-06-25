package com.example.mynotes.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.room.PrimaryKey
import java.util.UUID

data class CheckListModel (
    val id:String = UUID.randomUUID().toString(),
    var description: String="",
    var isChecked: Boolean = false
){}