package com.example.mynotes.data

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mynotes.data.repository.FolderRepository
import com.example.mynotes.data.repository.ImageDemoRepository
import com.example.mynotes.data.repository.LockRepository
import com.example.mynotes.data.repository.NoteBlockRepository
import com.example.mynotes.data.repository.NoteRepository
import com.example.mynotes.model.ImageDemoModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object Graph {
    lateinit var database: NotesDatabase

    val noteRepository by lazy{ NoteRepository(database.noteDao) }

    val folderRepository by lazy{ FolderRepository(database.folderDao) }

    val lockRepository by lazy{ LockRepository(database.lockDao) }

    val noteBlockRepository by lazy{ NoteBlockRepository(database.noteBlockDao) }

    val imageDemoRepository by lazy{ ImageDemoRepository(database.imageDemoDao) }


    fun provide(context: Context){
        database = Room.databaseBuilder(context, NotesDatabase::class.java, "my_notes.db")
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }
}