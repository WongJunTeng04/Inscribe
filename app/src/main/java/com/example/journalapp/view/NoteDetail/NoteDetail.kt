package com.example.journalapp.view.NoteDetail

// Imports
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.journalapp.Constants
import com.example.journalapp.Constants.noteDetailPlaceHolder
import com.example.journalapp.R
import com.example.journalapp.view.SharedComponents.GenericAppBar
import com.example.journalapp.view.theme.AppTheme
import com.example.journalapp.viewModel.NotesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// This NoteDetail.kt file is basically a detailed view for the note. When users click on the note in the
// NoteListScreen (or main page), they are brought to the NoteDetailPage.

// Composable for displaying Note Detail. // This shows the note details when user clicks on it.
@Composable
fun NoteDetailPage(
    noteId: Int, navController: NavController,
    viewModel: NotesViewModel ) {

    // Collect note details
    val scope = rememberCoroutineScope()
    // Manage state within a composable function and ensures the state is retained across recompositions, so the value persists even if the UI is re-rendered.
    val note = remember { mutableStateOf(noteDetailPlaceHolder) }

    // Show navIcon, mutableStateOf(true)
    val navIconState = remember { mutableStateOf(true) }

    // Collects note details
    LaunchedEffect(true) {
        scope.launch(Dispatchers.IO) {
            note.value = viewModel.getNote(noteId) ?: noteDetailPlaceHolder
        }
    }
    // Main Content Area
    AppTheme {
        //Explained once: This surface on top of a scaffold is to allow easier customizability of the screens.
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            //Puts all elements into a scaffold so that they can be displayed
            Scaffold(
                //The Top App Bar
                topBar = {
                    // Uses the GenericAppBar composable that is created in the Shared.kt file
                    GenericAppBar(
                        title = "Details", // Title of screen
                        // For navigation, using the nav_arrow to go back to the previous screen
                        navIcon = {
                            // For icon to go back
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.nav_arrow),
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        },
                        // Goes back to the previous screen
                        onBackIconClick = {
                            if (navController.currentBackStackEntry?.lifecycle?.currentState
                                == Lifecycle.State.RESUMED
                            ) {
                                navController.popBackStack()
                            }
                        },
                        // For editing, using the edit icon, to edit a note
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.edit),
                                contentDescription = "Edit Note",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        },
                        // Navigates you to the  note edit page where you can edit the specific note
                        onIconClick = {
                            navController.navigate(Constants.noteEditNavigation(note.value.id ?: 0))
                        },
                        // Navigation Icon state
                        navIconState = navIconState,
                        iconState = remember { mutableStateOf(true) }
                    )
                },
                // Main Content of Note Detail Page
                // This LazyColumn is done is different from CreateNoteScreen because it allows for more customizability for the UI
                content = { innerPadding ->
                    // The LazyColumn is similar to the RecyclerView in Android. It is more lightweight but provides the same functionality as
                    // a RecyclerView
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    ) {
                        // LazyColumn item 1: Image
                        item {
                            // Displays the image to the user. If there is an image, display the image. If there is no image, display a placeholder text instead.
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(370.dp)
                            ) {
                                // If there is an image, display the image
                                if (note.value.imageUri != null && note.value.imageUri!!.isNotEmpty()) {
                                    Image(
                                        painter = rememberAsyncImagePainter(
                                            ImageRequest.Builder(LocalContext.current)
                                                .data(data = Uri.parse(note.value.imageUri))
                                                .build()
                                        ),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else { // If there is no image, display an Image placeholder instead.
                                    Text(
                                        text = "Image Placeholder",
                                        modifier = Modifier.align(Alignment.Center),
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                        // LazyColumn item 2: Title, Note, and Date updated
                        // Displays the title of the entry
                        item {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = note.value.title, // Displays the value of the title
                                    modifier = Modifier
                                        .padding(top = 10.dp),
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                // Displays a short note to allow users to remember what that entry is for
                                Text(
                                    text = note.value.note, // Displays the value of the note
                                    modifier = Modifier
                                        .padding(top = 4.dp),
                                    fontSize = 20.sp
                                )
                                // Displays when the note was last modified
                                Text(
                                    text = note.value.dateUpdated, // Displays the value of the date the entry was updated
                                    modifier = Modifier
                                        .padding(top = 5.dp),
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                                //A horizontal divider just to make it look good for aesthetic purposes.
                                HorizontalDivider(
                                    modifier = Modifier.padding(
                                        start = 10.dp,
                                        end = 10.dp,
                                        top = 10.dp
                                    )
                                )
                            }
                        }
                        // LazyColumn item 3: Description, Time, Date, Location
                        item {
                            // Column for showing the other details such as Description, Time, Date, Location
                            Column(
                                modifier = Modifier.padding(
                                    start = 20.dp,
                                    end = 20.dp,
                                    top = 10.dp,
                                )
                            ) {
                                // 1) Description
                                // Each of the element is displayed in a Row to make it look nice.
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp)
                                ) {
                                    // Description label
                                    Text(
                                        text = "Description:",
                                        fontSize = 20.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    // Description's actual value
                                    Text(
                                        text = note.value.description,
                                        fontSize = 20.sp,
                                        modifier = Modifier.weight(2f).padding(start = 10.dp)
                                    )
                                }
                                // 2) Time
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp)
                                ) {
                                    // Time label
                                    Text(
                                        text = "Time:",
                                        fontSize = 20.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    // Time's actual value (the time the user has picked)
                                    Text(
                                        text = note.value.time,
                                        fontSize = 20.sp,
                                        modifier = Modifier.weight(2f).padding(start = 10.dp)
                                    )
                                }
                                // 3) Date
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp)
                                ) {
                                    // Date label
                                    Text(
                                        text = "Date:",
                                        fontSize = 20.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    // Date actual value (the date the user has picked)
                                    Text(
                                        text = note.value.date,
                                        fontSize = 20.sp,
                                        modifier = Modifier.weight(2f).padding(start = 10.dp)
                                    )
                                }
                                // 4) Location
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp, bottom = 10.dp)
                                ) {
                                    // Location label
                                    Text(
                                        text = "Location:",
                                        fontSize = 20.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    // Location actual value
                                    Text(
                                        text = note.value.location,
                                        fontSize = 20.sp,
                                        modifier = Modifier.weight(2f).padding(start = 10.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}

