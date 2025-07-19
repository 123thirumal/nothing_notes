package com.example.mynotes.data.repository

import com.example.mynotes.data.dao.NoteBlockDao
import com.example.mynotes.model.NoteBlockModel
import kotlinx.coroutines.flow.Flow

class NoteBlockRepository(
    private val noteBlockDao: NoteBlockDao
) {
    suspend fun insertNoteBlock(noteBlock: NoteBlockModel) : String{
        noteBlockDao.insertNoteBlock(noteBlock)
        return noteBlock.id
    }

    suspend fun getBlocksByNoteId(noteId: String) : List<NoteBlockModel> {
        return noteBlockDao.getBlocksByNoteId(noteId)
    }

    suspend fun deleteNoteBlock(noteBlock: NoteBlockModel){
        noteBlockDao.deleteNoteBlock(noteBlock)
    }

    suspend fun deleteBlocksByNoteId(noteId: String){
        noteBlockDao.deleteBlocksByNoteId(noteId)
    }

}