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
    @Insert(onConflict = OnConflictStrategy.IGNORE) //return -1L in case of error
    suspend fun insertNote(note: NoteModel): Long

    @Query("SELECT * FROM `notes_table` WHERE folderId =:folderId")
    fun getNotesByFolderId(folderId: Long): Flow<List<NoteModel>>

    @Query("SELECT * FROM `notes_table`")
    fun getAllNotes(): Flow<List<NoteModel>>

    @Query("SELECT * FROM `notes_table` WHERE id =:id")
    suspend fun getNoteById(id: Long) :NoteModel

    @Delete
    suspend fun deleteNote(note: NoteModel)

    @Update
    suspend fun updateNote(note: NoteModel)
}