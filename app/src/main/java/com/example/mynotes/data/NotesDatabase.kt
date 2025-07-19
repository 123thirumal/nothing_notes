package com.example.mynotes.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mynotes.data.dao.FolderDao
import com.example.mynotes.data.dao.ImageDemoDao
import com.example.mynotes.data.dao.LockDao
import com.example.mynotes.data.dao.NoteBlockDao
import com.example.mynotes.data.dao.NoteDao
import com.example.mynotes.model.FolderModel
import com.example.mynotes.model.ImageDemoModel
import com.example.mynotes.model.LockModel
import com.example.mynotes.model.NoteBlockModel
import com.example.mynotes.model.NoteModel


@Database(
    entities = [NoteModel::class, FolderModel::class, LockModel::class, NoteBlockModel::class, ImageDemoModel::class],
    version = 15,
    exportSchema = false
)
abstract class NotesDatabase: RoomDatabase(){
    abstract val noteDao: NoteDao
    abstract val folderDao: FolderDao
    abstract val lockDao: LockDao
    abstract val noteBlockDao: NoteBlockDao
    abstract val imageDemoDao: ImageDemoDao
}