package com.example.journalapp.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.journalapp.model.Note
import com.example.journalapp.persistence.NotesDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotesViewModel(
    private val db: NotesDao
) : ViewModel() {

    val notes: LiveData<List<Note>> = db.getNotes().also {
        it.observeForever { noteList ->
            Log.d("NotesViewModel", "Notes retrieved: ${noteList.size}")
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            db.deleteNote(note)
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            db.updateNote(note)
        }
    }

    fun createNote(
        title: String,
        note: String,
        description: String,
        time: String,
        date: String,
        location: String,
        image: String? = null
    ) {
        val newNote = Note(
            title = title,
            note = note,
            description = description,
            time = time,
            date = date,
            location = location,
            imageUri = image
        )
        viewModelScope.launch(Dispatchers.IO) {
            db.insertNote(newNote)
        }
    }

    suspend fun getNote(id: Int): Note? {
        return db.getNoteById(id)
    }


}

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
