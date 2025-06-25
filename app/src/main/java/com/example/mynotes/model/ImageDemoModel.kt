package com.example.mynotes.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "image_demo_table")
class ImageDemoModel (
    @PrimaryKey val id: Int = 0, // Always 0 — only one row
    var showImageDemo: Boolean = true
)