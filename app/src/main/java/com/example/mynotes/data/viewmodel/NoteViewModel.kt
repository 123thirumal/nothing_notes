package com.example.mynotes.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.data.Graph
import com.example.mynotes.model.NoteModel
import com.example.mynotes.data.repository.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import android.util.Log
import androidx.compose.runtime.collectAsState


class NoteViewModel( private val noteRepository: NoteRepository= Graph.noteRepository): ViewModel() {

    val allNotes: StateFlow<List<NoteModel>> = noteRepository.getAllNotes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val nonPrivateNotes: StateFlow<List<NoteModel>> = allNotes
        .map { list -> list.filter {!it.Private }}
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val nonPrivateNotesInCloud: StateFlow<List<NoteModel>> = allNotes
        .map { list -> list.filter { !it.Private && it.SavedInCloud } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val nonPrivateNotesNotInCloud: StateFlow<List<NoteModel>> = allNotes
        .map { list -> list.filter { !it.Private && !it.SavedInCloud } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val folderId = MutableStateFlow<String?>(null)

    val folderNotes: StateFlow<List<NoteModel>> = folderId
        .filterNotNull()
        .flatMapLatest { id ->
            noteRepository.getNotesByFolderId(id)
                .map { notes -> notes.filter { !it.Private } } // <-- Filter here
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    val privateNotes: StateFlow<List<NoteModel>> = allNotes
        .map { notes -> notes.filter { it.Private == true } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val privateNotesInCloud: StateFlow<List<NoteModel>> = allNotes
        .map { notes -> notes.filter { it.Private == true && it.SavedInCloud } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val privateNotesNotInCloud: StateFlow<List<NoteModel>> = allNotes
        .map { notes -> notes.filter { it.Private == true && !it.SavedInCloud } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    suspend fun insertNote(note: NoteModel): NoteModel {
        val noteId= noteRepository.insertNote(note)
        return noteRepository.getNoteById(noteId)
    }

    suspend fun getNoteById(id: String): NoteModel {
        return noteRepository.getNoteById(id)
    }

    suspend fun updateNote(note: NoteModel){
        noteRepository.updateNote(note) //awaits
    }



    suspend fun deleteNote(note: NoteModel){
        noteRepository.deleteNote(note)
    }

    fun setFolderId(id: String) { //for assigning the folderid variable for fetching notes
                                //Not for assigning the folder to which the notes belong, which can be done in updateNote function
        folderId.value = id
    }


    suspend fun makeAllNotesNotSavedInCloud(){
        for(note in allNotes.value){
            noteRepository.updateNote(note.copy(SavedInCloud = false, Synced = false))
        }
    }

}