package com.example.mynotes.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mynotes.model.NoteBlockModel
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteBlockDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNoteBlock(noteBlock: NoteBlockModel): Long


    @Query("SELECT * FROM `note_blocks` WHERE noteId =:noteId ORDER BY blockOrder ASC")
    suspend fun getBlocksByNoteId(noteId: Long): List<NoteBlockModel>

    @Delete
    suspend fun deleteNoteBlock(noteBlock: NoteBlockModel)

    @Query("DELETE FROM `note_blocks` WHERE noteId =:noteId")
    suspend fun deleteBlocksByNoteId(noteId: Long)

}