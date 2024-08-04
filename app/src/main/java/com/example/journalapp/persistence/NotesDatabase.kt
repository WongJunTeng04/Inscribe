package com.example.journalapp.persistence

//Imports
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.journalapp.model.Note

//Our app's database
@Database(version = 1, entities = [Note::class])
abstract class NotesDatabase : RoomDatabase() {
    abstract fun NotesDao(): NotesDao
}
