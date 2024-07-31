package com.example.journalapp

//Import
import com.example.journalapp.model.Note

//This Constants.kt file contains the constants used in the app. It contains important constants,
// and helper functions, help promotes code reuse and reducing the chances of errors from hardcoding values in the app.


object Constants {
    //Database Configuration
    // -> Store the name of the database and tables used in the app
    const val TABLE_NAME = "notes" //Name of the table
    const val DATABASE_NAME = "notesDatabase" //Name of the database

    //Provides a default Note object with empty fields when there is no notes.
    //Mainly used for initializing UI components and providing default values.
    val noteDetailPlaceHolder = Note(
        note = "",
        id = 0,
        title = "",
        date = "",
        time = "",
        description = "",
        location = "",
        imageUri = "",
        dateUpdated = ""
    )

    //Navigation Constants, used in MainActivity.kt to navigate between screens
    //Below are string constants representing different navigation routes for the app
    const val NAVIGATION_NOTES_CREATE = "notesCreate" //CreateNoteScreen
    const val NAVIGATION_NOTES_LIST = "notesList" //Displays the notes (MAIN SCREEN)
    const val NAVIGATION_NOTES_DETAIL = "noteDetail/{noteId}" //NoteDetailScreen -> Shows you all the details regarding that specific note you have created
    const val NAVIGATION_NOTES_EDIT = "noteEdit/{noteId}" //EditNoteScreen -> Let's you edit the notes you have created
    const val NAVIGATION_NOTES_ID_ARGUMENT = "noteId" //ARGUMENT to bring you to that specific Note you have clicked on

    //Helper function -> They take in a noteId and return corresponding navigation path as a string.
    //Helps generate the navigation paths for each specific action. 1) View Details 2) Edit Note

    //Brings you to Note Details Screen -> To view the notes
    fun noteDetailNavigation(noteId: Int) = "noteDetail/$noteId"
    //Brings you to Edit Notes Screen -> To edit the notes.
    fun noteEditNavigation(noteId: Int) = "noteEdit/$noteId"
}
