package com.example.mynotes.data

import android.app.Application

class NotesApp: Application() {
    override fun onCreate() {
        super.onCreate();
        Graph.provide(this)
    }
}