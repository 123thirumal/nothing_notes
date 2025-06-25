package com.example.mynotes.data.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.data.Graph
import com.example.mynotes.model.NoteModel
import com.example.mynotes.data.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NoteViewModel( private val noteRepository: NoteRepository= Graph.noteRepository): ViewModel() {

    val allNotes: StateFlow<List<NoteModel>> = noteRepository.getAllNotes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val nonPrivateNotes: StateFlow<List<NoteModel>> = allNotes
        .map { list -> list.filter { !it.isPrivate } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val folderId = MutableStateFlow<Long?>(null)

    val folderNotes: StateFlow<List<NoteModel>> = folderId
        .filterNotNull()
        .flatMapLatest { id ->
            noteRepository.getNotesByFolderId(id)
                .map { notes -> notes.filter { !it.isPrivate } } // <-- Filter here
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    val privateNotes: StateFlow<List<NoteModel>> = allNotes
        .map { notes -> notes.filter { it.isPrivate == true } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    suspend fun insertNote(note: NoteModel): NoteModel {
        val noteId= noteRepository.insertNote(note)
        return noteRepository.getNoteById(noteId)
    }

    suspend fun getNoteById(id: Long): NoteModel {
        return noteRepository.getNoteById(id)
    }

    private val _isUpdateDone = mutableStateOf(false)
    val isUpdateDone: MutableState<Boolean> = _isUpdateDone
    suspend fun updateNote(note: NoteModel){
        noteRepository.updateNote(note) //awaits
        _isUpdateDone.value = true
    }

    fun setIsUpdateDone(bool: Boolean){
        _isUpdateDone.value=bool
    }


    suspend fun deleteNote(note: NoteModel){
        noteRepository.deleteNote(note)
    }

    fun setFolderId(id: Long) { //for assigning the folderid variable for fetching notes
                                //Not for assigning the folder to which the notes belong, which can be done in updateNote function
        folderId.value = id
    }

}