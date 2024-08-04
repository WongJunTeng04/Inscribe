package com.example.journalapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.journalapp.model.Note
import com.example.journalapp.persistence.NotesDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//The app's ViewModel, which is responsible for preparing and managing the data for the UI.
class NotesViewModel(
    private val db: NotesDao
) : ViewModel() {

    // LiveData to observe the list of notes
    val notes: LiveData<List<Note>> = db.getNotes()

    // Function to delete a note
    fun deleteNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            db.deleteNote(note)
        }
    }

    // Function to update a note
    fun updateNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            db.updateNote(note)
        }
    }

    // Function to create a new note
    fun createNote(
        title: String,
        note: String,
        description: String,
        time: String,
        date: String,
        location: String,
        image: String? = null
    ) {
        // Create a new Note object
        val newNote = Note(
            title = title,
            note = note,
            description = description,
            time = time,
            date = date,
            location = location,
            imageUri = image
        )
        // Insert the new note into the database
        viewModelScope.launch(Dispatchers.IO) {
            db.insertNote(newNote)
        }
    }

    // Function to retrieve a note by its ID
    suspend fun getNote(id: Int): Note? {
        return db.getNoteById(id)
    }
}

// Factory class to create an instance of NotesViewModel
class NoteViewModelFactory(
    private val db: NotesDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotesViewModel(db) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
