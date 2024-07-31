package com.example.journalapp

//Imports
import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.room.Room
import com.example.journalapp.persistence.NotesDao
import com.example.journalapp.persistence.NotesDatabase

// In an MVVM architecture, the JournalNotesApp class,
// which is responsible for application-wide setup and management,
// does not strictly belong to the Model, View, or ViewModel layers. Instead,
// it serves as a foundational component of your application. So this file is not in any of the MVVM packages.


//This class JournalNotesApp extends Application, which means it gets initialised when the app starts.
class JournalNotesApp : Application() {
    //Declare database
    private var db: NotesDatabase? = null

    //Static instance
    init {
        instance = this
    }

    //Initializes the getDb() method and returns and instance of "NotesDatabase"
    private fun getDb() : NotesDatabase {
        if (db != null) {
            return db!!
        } else {
            //Uses ROOM's databaseBuilder to create the database.
            db = Room.databaseBuilder(
                instance!!.applicationContext,
                NotesDatabase::class.java, Constants.DATABASE_NAME
            ).fallbackToDestructiveMigration().build() //Fallback to destructive migration for schema changes.

            return db!!
        }
    }

    //Static instance
    companion object {
        private var instance: JournalNotesApp? = null

        //getDao() method returns the NotesDao instance from the database.
        fun getDao() : NotesDao { //Have functions for retrieving, updating, and deleting the notes user have created
            return instance!!.getDb().NotesDao()
        }
        //getUriPermission() method takes a Uri and grants persistent read permission to it.
        fun getUriPermission(uri: Uri) {
            instance!!.applicationContext.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
    }
}