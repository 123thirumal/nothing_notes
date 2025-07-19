package com.example.mynotes.data.viewmodel

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import com.example.mynotes.model.NoteBlockEntityModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class CommonViewModel : ViewModel() {
    val isPrivateFilesUnlocked = mutableStateOf(false)
    val headSelect = mutableStateOf("ALL")
    val lockViewModel = LockViewModel()
    val folderViewModel = FolderViewModel()
    val noteViewModel = NoteViewModel()
    val noteBlockViewModel = NoteBlockViewModel()
    val imageDemoViewModel = ImageDemoViewModel()

    var currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    fun removeUser(){
        currentUser = null
    }

    fun setUser(){
        currentUser = FirebaseAuth.getInstance().currentUser
    }
}