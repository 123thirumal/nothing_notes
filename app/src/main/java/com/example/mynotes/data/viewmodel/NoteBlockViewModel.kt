package com.example.mynotes.data.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.data.Graph
import com.example.mynotes.data.repository.NoteBlockRepository
import com.example.mynotes.model.NoteBlockModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NoteBlockViewModel(
    private val noteBlockRepository: NoteBlockRepository = Graph.noteBlockRepository
) : ViewModel() {


    suspend fun insertNoteBlock(noteBlock: NoteBlockModel) {
        noteBlockRepository.insertNoteBlock(noteBlock)
    }

    suspend fun deleteNoteBlock(noteBlock: NoteBlockModel) {
        noteBlockRepository.deleteNoteBlock(noteBlock)
    }

    suspend fun deleteBlocksByNoteId(noteId: String) {
        noteBlockRepository.deleteBlocksByNoteId(noteId)
    }

    suspend fun getBlocksByNoteId(noteId: String): List<NoteBlockModel> {
        return noteBlockRepository.getBlocksByNoteId(noteId)
    }
}