package com.example.mynotes

import android.app.Application
import com.example.mynotes.data.Graph
import com.google.firebase.Firebase
import com.google.firebase.initialize

class NotesApp: Application() {
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
        Firebase.initialize(this)
    }
}