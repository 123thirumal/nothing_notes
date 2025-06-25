package com.example.mynotes.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mynotes.model.LockModel
import com.example.mynotes.model.NoteModel


@Dao
interface LockDao {
    @Query("SELECT * FROM lock_table WHERE id = 0 LIMIT 1")
    suspend fun getLock(): LockModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(lock: LockModel)

    @Query("DELETE FROM lock_table WHERE id = 0")
    suspend fun deleteLock()

}