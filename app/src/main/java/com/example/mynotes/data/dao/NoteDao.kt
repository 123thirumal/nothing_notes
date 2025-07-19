package com.example.mynotes.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mynotes.model.NoteModel
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE) //return -1L in case of error
    suspend fun insertNote(note: NoteModel)

    @Query("SELECT * FROM `notes_table` WHERE folderId =:folderId")
    fun getNotesByFolderId(folderId: String): Flow<List<NoteModel>>

    @Query("SELECT * FROM `notes_table`")
    fun getAllNotes(): Flow<List<NoteModel>>

    @Query("SELECT * FROM `notes_table` WHERE id =:id")
    suspend fun getNoteById(id: String) :NoteModel

    @Delete
    suspend fun deleteNote(note: NoteModel)

    @Update
    suspend fun updateNote(note: NoteModel)
}