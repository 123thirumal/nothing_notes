package com.example.mynotes.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mynotes.model.ImageDemoModel

@Dao
interface ImageDemoDao {
    @Query("SELECT * FROM image_demo_table WHERE id = 0 LIMIT 1")
    suspend fun getImageDemo(): ImageDemoModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateImageDemo(imageDemo: ImageDemoModel)
}
