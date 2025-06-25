package com.example.mynotes.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mynotes.model.FolderModel
import com.example.mynotes.model.NoteModel
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDao{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFolder(folder: FolderModel): Long

    @Delete
    suspend fun deleteFolder(folder: FolderModel)

    @Query("SELECT * FROM `folders_table` WHERE id =:id")
    suspend fun getFolderById(id: Long) :FolderModel

    @Update
    suspend fun updateFolder(folder: FolderModel)

    @Query("SELECT * FROM `folders_table`")
    fun getAllFolders(): Flow<List<FolderModel>>
}