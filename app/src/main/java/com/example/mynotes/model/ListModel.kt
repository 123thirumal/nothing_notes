package com.example.mynotes.model
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import java.util.UUID

data class ListModel(
    val id:String = UUID.randomUUID().toString(),
    var description: String="",
) {}