package com.example.mynotes.data.repository

import com.example.mynotes.data.dao.NoteDao
import com.example.mynotes.model.NoteModel
import kotlinx.coroutines.flow.Flow

class NoteRepository(
    private val noteDao: NoteDao
) {
    suspend fun insertNote(note: NoteModel): String{
        noteDao.insertNote(note)
        return note.id
    }

    fun getNotesByFolderId(folderId: String) : Flow<List<NoteModel>>{
        return noteDao.getNotesByFolderId(folderId)
    }

    fun getAllNotes() : Flow<List<NoteModel>>{
        return noteDao.getAllNotes()
    }

    suspend fun getNoteById(id: String) : NoteModel{
        return noteDao.getNoteById(id)
    }

    suspend fun deleteNote(note: NoteModel){
        noteDao.deleteNote(note)
    }

    suspend fun updateNote(note: NoteModel){
        noteDao.updateNote(note)
    }
}