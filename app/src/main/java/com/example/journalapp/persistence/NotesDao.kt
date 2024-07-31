package com.example.journalapp.persistence

//Imports
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.journalapp.model.Note

//Gives us an interface to interact with our database
@Dao
interface NotesDao {
    //Allows us to see the individual notes and all the information related to it
    @Query("SELECT * FROM notes WHERE notes.id=:id")
    suspend fun getNoteById(id: Int): Note?

    //Allows us to get all the notes stored in the database and displays it, in descending order of date updated
    @Query("SELECT * FROM Notes ORDER BY dateUpdated DESC")
    fun getNotes(): LiveData<List<Note>>

    //Delete a note
    @Delete
    fun deleteNote(note: Note)

    //Update the note
    @Update
    fun updateNote(note:Note)

    //Insert a new note
    @Insert
    fun insertNote(note: Note)
}