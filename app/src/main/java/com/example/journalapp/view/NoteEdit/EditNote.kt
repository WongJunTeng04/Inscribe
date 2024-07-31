package com.example.journalapp.view.NoteEdit

//Imports
import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.journalapp.Constants
import com.example.journalapp.JournalNotesApp
import com.example.journalapp.R
import com.example.journalapp.model.Note
import com.example.journalapp.view.SharedComponents.DatePickerComponent
import com.example.journalapp.view.SharedComponents.DatePickerDialog
import com.example.journalapp.view.SharedComponents.GenericAppBar
import com.example.journalapp.view.SharedComponents.TimePickerComponent
import com.example.journalapp.view.SharedComponents.TimePickerDialog
import com.example.journalapp.view.theme.AppTheme
import com.example.journalapp.viewModel.NotesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//Keep in mind, although the CreateNoteScreen and NoteEditScreen is extremely similar, a lot of the logic in the
//CreateNoteScreen is not the same as the NoteEditScreen as there are more things to consider in the NoteEditScreen.

//This page is accessed when users press on the edit icon in the NavBar in the NoteDetailPage. Here they can edit
//the note details and update it, storing it in the database.

//Note Edit Screen
@SuppressLint("DefaultLocale")
@Composable
fun NoteEditScreen(
    noteId: Int,
    navController: NavController,
    viewModel: NotesViewModel
) {
    //To handle database operations and creates a CoroutineScope that is tied to the lifecycle of the composable
    //The database operation here is to update the notes with the values changed
    val scope = rememberCoroutineScope()
    val note = remember { mutableStateOf(Constants.noteDetailPlaceHolder) }

    // Find properties for each individual object within the note
    // React to changes in the composable. Also, this is done this way to retain information in an event of screen rotation and recomposition.
    //Below, Information is stored according to:
    // 1) Photos, 2) Title, 3) Note, 4) Description, 5) Time, 6) Date, 7) Location
    val currentPhotos = rememberSaveable { mutableStateOf(note.value.imageUri) }
    val currentTitle = rememberSaveable { mutableStateOf(note.value.title) }
    val currentNote = rememberSaveable { mutableStateOf(note.value.note) }
    val currentDescription = rememberSaveable { mutableStateOf(note.value.description) }
    val currentTime = rememberSaveable { mutableStateOf(note.value.time) }
    val currentDate = rememberSaveable { mutableStateOf(note.value.date) }
    val currentLocation = rememberSaveable { mutableStateOf(note.value.location) }

    //Save button
    val saveButtonState = rememberSaveable { mutableStateOf(false) }
    //Navigation button
    val navIconState = rememberSaveable { mutableStateOf(true) }

    //For TimePicker and DatePicker
    val showTimePickerDialog = rememberSaveable { mutableStateOf(false) }
    val showDatePickerDialog = rememberSaveable { mutableStateOf(false) }
    val selectedHour = rememberSaveable { mutableIntStateOf(0) }
    val selectedMinute = rememberSaveable { mutableIntStateOf(0) }
    val selectedDay = rememberSaveable { mutableIntStateOf(0) }
    val selectedMonth = rememberSaveable { mutableIntStateOf(0) }
    val selectedYear = rememberSaveable { mutableIntStateOf(0) }

    //Get image from gallery
    val getImageRequest = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            JournalNotesApp.getUriPermission(uri)
            currentPhotos.value = uri.toString()
        } else {
            currentPhotos.value = null
        }
        saveButtonState.value = false
    }

    //Get note from database for that specific note
    //The scope.launch(Dispatchers.IO) block is used to fetch the note from the database in the background.
    LaunchedEffect(true) {
        scope.launch(Dispatchers.IO) {
            note.value = viewModel.getNote(noteId) ?: Constants.noteDetailPlaceHolder
            currentPhotos.value = note.value.imageUri
            currentNote.value = note.value.note
            currentTitle.value = note.value.title
            currentDescription.value = note.value.description
            currentTime.value = note.value.time
            currentDate.value = note.value.date
            currentLocation.value = note.value.location
        }
    }

    // Function to check if any field has value //This prevents the users from saving a note which has no values during editing (VERY IMPORTANT)
    fun hasValues(): Boolean {
        return currentPhotos.value != null ||
                currentTitle.value.isNotBlank() ||
                currentNote.value.isNotBlank() ||
                currentDescription.value.isNotBlank() ||
                currentTime.value.isNotBlank() ||
                currentDate.value.isNotBlank() ||
                currentLocation.value.isNotBlank()
    }

    //Main content area
    AppTheme {
        //A surface container using the colorScheme.primary
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.primary) {
            Scaffold(
                topBar = {
                    //Uses the GenericAppBar composable created in the Shared.kt file
                    GenericAppBar(
                        //Title
                        title = "Edit",
                        //Navigation Icon, allows users to go back to the previous screen they came from
                        navIcon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.nav_arrow),
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        },
                        //Once the user presses on the navigation icon, they are led back to the previous screen
                        onBackIconClick = {
                            if (navController.currentBackStackEntry?.lifecycle?.currentState
                                == Lifecycle.State.RESUMED
                            ) {
                                navController.popBackStack()
                            }
                        },
                        //Save Icon, allows users to save their edits and register it in the database once they are done editing
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.save),
                                contentDescription = "Save Note",
                                tint = MaterialTheme.colorScheme.onPrimary,
                            )
                        },
                        // Once clicked, users can save their edits
                        onIconClick = {
                            //Only allows users to save if at least one of the fields have values.
                            if (hasValues()) {
                                //Updates the notes.
                                viewModel.updateNote(
                                    Note(
                                        id = note.value.id,
                                        imageUri = currentPhotos.value,
                                        title = currentTitle.value,
                                        note = currentNote.value,
                                        description = currentDescription.value,
                                        time = currentTime.value,
                                        date = currentDate.value,
                                        location = currentLocation.value
                                    )
                                )
                                if (navController.currentBackStackEntry?.lifecycle?.currentState
                                    == Lifecycle.State.RESUMED
                                ) {
                                    navController.popBackStack()
                                }
                            }
                        },
                        //NavIcon and SaveButton state
                        navIconState = navIconState,
                        iconState = saveButtonState,
                    )
                },
                //The Bottom Bar is a distinct feature that is only found in the edit page.
                //It allows users to clear all the TextFields including the Picture
                bottomBar = {
                    //Start of Bottom App Bar
                    BottomAppBar(
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ){
                        //Clear function, this allows the users to clear all the TextFields, including the Picture
                        IconButton(onClick = {
                            currentPhotos.value = null
                            currentTitle.value = ""
                            currentNote.value = ""
                            currentDescription.value = ""
                            currentTime.value = ""
                            currentDate.value = ""
                            currentLocation.value = ""
                            saveButtonState.value = hasValues()
                        }) {
                            //Icon for clear
                            Icon(
                                Icons.Filled.Clear,
                                contentDescription = "Clear Note",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        // Remove Image button
                        IconButton(onClick = {
                            currentPhotos.value = null
                            saveButtonState.value = hasValues()
                        }) {
                            //Icon for removing the image. It is in the shape of a camera but crossed out. Intuitive and easy for users to understand.
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.remove_picture),
                                contentDescription = "Remove Image",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        //Allows users to confirm their changes. (It is shown as a tick)
                        IconButton(onClick = {
                            if (hasValues()) {
                                viewModel.updateNote(
                                    Note(
                                        id = note.value.id,
                                        imageUri = currentPhotos.value,
                                        title = currentTitle.value,
                                        note = currentNote.value,
                                        description = currentDescription.value,
                                        time = currentTime.value,
                                        date = currentDate.value,
                                        location = currentLocation.value
                                    )
                                )
                                //Once clicked, and conditions are met (at least one field have values) then go back to the previous screen.
                                //Save then go back to the previous screen.
                                if (navController.currentBackStackEntry?.lifecycle?.currentState
                                    == Lifecycle.State.RESUMED
                                ) {
                                    navController.popBackStack()
                                }
                            }
                        }) {
                            //Icon for confirming the changes. If no changes are made, just goes back to the previous screen.
                            Icon(
                                Icons.Filled.Done,
                                contentDescription = "Save Note",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        //Spacer just makes space between elements
                        Spacer(Modifier.weight(1f, true))
                        //Allows users to take a picture, and display it to the user.
                        IconButton(onClick = {
                            getImageRequest.launch(arrayOf("image/*"))
                        }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.image),
                                contentDescription = "Add Image",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                },
                //Main Content
                content = { innerPadding ->
                    LazyColumn( //Used LazyColumn to display the elements, similar to recyclerview, but more lightweight and for compose
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        //Displays the image
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(260.dp)
                            ) {
                                //If there is a picture associated with the note, then show the picture
                                if (currentPhotos.value != null && currentPhotos.value!!.isNotEmpty()) {
                                    Image(
                                        painter = rememberAsyncImagePainter(
                                            ImageRequest.Builder(LocalContext.current)
                                                .data(data = Uri.parse(currentPhotos.value))
                                                .build()
                                        ),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    //If there is NO picture, then a placeholder will be shown
                                } else {
                                    Text(
                                        text = "Image placeholder",
                                        modifier = Modifier.align(Alignment.Center),
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                        //Displays the title
                        item {
                            OutlinedTextField(
                                value = currentTitle.value,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 5.dp, start = 15.dp, end = 15.dp),
                                maxLines = 1,
                                //Changes the value of the Text Field for title and updates the saveButtonState
                                onValueChange = { value ->
                                    currentTitle.value = value
                                    if (currentTitle.value != note.value.title) {
                                        saveButtonState.value = true
                                    } else if (currentNote.value == note.value.note && currentTitle.value == note.value.title) {
                                        saveButtonState.value = false
                                    }
                                },
                                label = { Text("Title") }
                            )
                        }
                        //Displays the "note" which is a short description for the note that they are creating
                        item {
                            OutlinedTextField(
                                value = currentNote.value,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 5.dp, start = 15.dp, end = 15.dp),
                                maxLines = 1,
                                //Changes the value of the Text Field for note field and updates the saveButtonState
                                onValueChange = { value ->
                                    currentNote.value = value
                                    if (currentNote.value != note.value.note) {
                                        saveButtonState.value = true
                                    } else if (currentNote.value == note.value.note && currentTitle.value == note.value.title) {
                                        saveButtonState.value = false
                                    }
                                },
                                label = { Text("Note") },
                            )
                        }
                        //Displays the description of the note
                        item {
                            OutlinedTextField(
                                value = currentDescription.value,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .padding(top = 5.dp, start = 15.dp, end = 15.dp),
                                maxLines = 7,
                                //Changes the value of the Text Field for description field and updates the saveButtonState
                                onValueChange = { value ->
                                    currentDescription.value = value
                                    if (currentDescription.value != note.value.description) {
                                        saveButtonState.value = true
                                    } else if (currentDescription.value == note.value.description && currentTitle.value == note.value.title) {
                                        saveButtonState.value = false
                                    }
                                },
                                label = { Text("Description") },
                            )
                        }
                        //Displays the time and date of the note. Shown in a row to make the elements side by side.
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(1.dp)
                            ) {
                                //Displays the time
                                OutlinedTextField(
                                    value = currentTime.value,
                                    //Changes the value of the Text Field for time field and updates the saveButtonState
                                    onValueChange = { value ->
                                        currentTime.value = value
                                        if (currentTime.value != note.value.time) {
                                            saveButtonState.value = true
                                        } else if (currentTime.value == note.value.time && currentTitle.value == note.value.title) {
                                            saveButtonState.value = false
                                        }
                                    },
                                    label = { Text("Time") },
                                    modifier = Modifier
                                        .weight(2f)
                                        .padding(top = 5.dp, start = 15.dp)
                                )
                                TextButton(onClick = { showTimePickerDialog.value = true }) {
                                    Text("Time")
                                }
                                //Displays the date
                                OutlinedTextField(
                                    value = currentDate.value,
                                    //Changes the value of the Text Field for date field and updates the saveButtonState
                                    onValueChange = { value ->
                                        currentDate.value = value
                                        if (currentDate.value != note.value.date) {
                                            saveButtonState.value = true
                                        } else if (currentDate.value == note.value.date && currentTitle.value == note.value.title) {
                                            saveButtonState.value = false
                                        }
                                    },
                                    label = { Text("Date") },
                                    modifier = Modifier.weight(3f)
                                )
                                TextButton(onClick = { showDatePickerDialog.value = true }) {
                                    Text("Date")
                                }
                            }
                        }
                        //Displays the location of the note
                        item {
                            OutlinedTextField(
                                value = currentLocation.value,
                                //Changes the value of the Text Field for location field and updates the saveButtonState
                                onValueChange = { value ->
                                    currentLocation.value = value
                                    if (currentLocation.value != note.value.location) {
                                        saveButtonState.value = true
                                    } else if (currentLocation.value == note.value.location && currentTitle.value == note.value.title) {
                                        saveButtonState.value = false
                                    }
                                },
                                label = { Text("Location") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 5.dp, start = 15.dp, end = 15.dp)
                            )
                        }
                    }
                    //Show time picker component to select time and then update the time

                    if (showTimePickerDialog.value) {
                        TimePickerDialog(
                            title = "Select Time",
                            onCancel = { showTimePickerDialog.value = false },
                            onConfirm = {
                                showTimePickerDialog.value = false
                                currentTime.value = String.format("%02d:%02d", selectedHour.intValue, selectedMinute.intValue)
                            },
                            content = {
                                TimePickerComponent(selectedHour, selectedMinute)
                            }
                        )
                    }
                    //Show the date picker component to select date and then update the date
                    if (showDatePickerDialog.value) {
                        DatePickerDialog(
                            title = "Select Date",
                            onCancel = { showDatePickerDialog.value = false },
                            onConfirm = {
                                showDatePickerDialog.value = false
                                currentDate.value = String.format("%02d-%02d-%04d", selectedDay.intValue, selectedMonth.intValue + 1, selectedYear.intValue)
                            },
                            content = {
                                DatePickerComponent(selectedDay, selectedMonth, selectedYear)
                            }
                        )
                    }
                }
            )
        }
    }
}