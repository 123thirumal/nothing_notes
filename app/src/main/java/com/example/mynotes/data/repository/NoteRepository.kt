package com.example.mynotes.data.repository

import com.example.mynotes.data.dao.NoteDao
import com.example.mynotes.model.NoteModel
import kotlinx.coroutines.flow.Flow

class NoteRepository(
    private val noteDao: NoteDao
) {
    suspend fun insertNote(note: NoteModel): Long{
        return noteDao.insertNote(note)
    }

    fun getNotesByFolderId(folderId: Long) : Flow<List<NoteModel>>{
        return noteDao.getNotesByFolderId(folderId)
    }

    fun getAllNotes() : Flow<List<NoteModel>>{
        return noteDao.getAllNotes()
    }

    suspend fun getNoteById(id: Long) : NoteModel{
        return noteDao.getNoteById(id)
    }

    suspend fun deleteNote(note: NoteModel){
        noteDao.deleteNote(note)
    }

    suspend fun updateNote(note: NoteModel){
        noteDao.updateNote(note)
    }
}