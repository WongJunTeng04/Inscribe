package com.example.journalapp

//Imports
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.journalapp.view.NoteDetail.NoteDetailPage
import com.example.journalapp.view.NoteEdit.NoteEditScreen
import com.example.journalapp.viewModel.NoteViewModelFactory
import com.example.journalapp.view.NotesList.NoteListScreen
import com.example.journalapp.viewModel.NotesViewModel
import com.example.journalapp.view.CreateNote.CreateNoteScreen

//Entry point for the app, run the app using MainActivity.kt
class MainActivity : ComponentActivity() {

    private lateinit var viewModel: NotesViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, NoteViewModelFactory(JournalNotesApp.getDao())).get(
            NotesViewModel::class.java)
        setContent {
            //Controller val to allow navigation throughout different screens.
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = Constants.NAVIGATION_NOTES_LIST) {
                // Goes to Notes List screen
                composable(Constants.NAVIGATION_NOTES_LIST) {
                    NoteListScreen(navController = navController, viewModel = viewModel)
                }

                // Goes to Note Detail screen
                composable(
                    Constants.NAVIGATION_NOTES_DETAIL,
                    arguments = listOf(navArgument(Constants.NAVIGATION_NOTES_ID_ARGUMENT) {
                        type = NavType.IntType
                    })
                ) { navBackStackEntry ->
                        navBackStackEntry.arguments?.getInt(Constants.NAVIGATION_NOTES_ID_ARGUMENT)?.let {
                        NoteDetailPage(noteId = it, navController = navController, viewModel = viewModel)
                    }
                }

                // Goes to Note Edit screen
                composable(
                    Constants.NAVIGATION_NOTES_EDIT,
                    arguments = listOf(navArgument(Constants.NAVIGATION_NOTES_ID_ARGUMENT) {
                        type = NavType.IntType
                    })
                ) { navBackStackEntry ->
                    navBackStackEntry.arguments?.getInt(Constants.NAVIGATION_NOTES_ID_ARGUMENT)?.let {
                        NoteEditScreen(noteId = it, navController = navController, viewModel = viewModel)
                    }
                }

                // Goes to Note Create screen
                composable(Constants.NAVIGATION_NOTES_CREATE) {
                    CreateNoteScreen(navController = navController, viewModel = viewModel)
                }
            }
        }
    }
}
