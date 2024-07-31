package com.example.journalapp.view.CreateNote

//Import
import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.journalapp.JournalNotesApp
import com.example.journalapp.R
import com.example.journalapp.view.NotesList.NotesFab
import com.example.journalapp.view.SharedComponents.DatePickerComponent
import com.example.journalapp.view.SharedComponents.DatePickerDialog
import com.example.journalapp.view.SharedComponents.GenericAppBar
import com.example.journalapp.view.SharedComponents.TimePickerComponent
import com.example.journalapp.view.SharedComponents.TimePickerDialog
import com.example.journalapp.view.theme.AppTheme
import com.example.journalapp.viewModel.NotesViewModel

//To handle the mutableStateOf each variable: Photos, Title, Note, Description, Time, Date, Location,
//Navigation icon and Save icon
class CreateNoteViewModel : ViewModel() {
    val currentPhotos = mutableStateOf("")
    val currentTitle = mutableStateOf("")
    val currentNote = mutableStateOf("")
    val currentDescription = mutableStateOf("")
    val currentTime = mutableStateOf("")
    val currentDate = mutableStateOf("")
    val currentLocation = mutableStateOf("")
    val navIconState = mutableStateOf(true)
    val saveButtonState = mutableStateOf(false)
}

//Composable for Create Note Screen. This is the Create Note page users see after pressing the FAB "+" in the main page.
@SuppressLint("DefaultLocale")
@Composable
fun CreateNoteScreen(
    navController: NavController,
    viewModel: NotesViewModel
) {
    //To preserve the state of the viewModel
    val createNoteViewModel: CreateNoteViewModel = viewModel()

    //Get the current values of each variable. Helps ensure that the values are preserved even after recompositions and screen rotations.
    //Each variable considered in order:
    //1) Photos, 2) Title, 3) Note, 4) Description, 5) Time, 6) Date, 7) Location, 8) Navigation icon, 9) Save icon
    val currentPhotos = createNoteViewModel.currentPhotos
    val currentTitle = createNoteViewModel.currentTitle
    val currentNote = createNoteViewModel.currentNote
    val currentDescription = createNoteViewModel.currentDescription
    val currentTime = createNoteViewModel.currentTime
    val currentDate = createNoteViewModel.currentDate
    val currentLocation = createNoteViewModel.currentLocation

    //These are states of the TimePicker and DatePickers so that even after rotation the values are preserved.
    val showTimePickerDialog = remember { mutableStateOf(false) }
    val showDatePickerDialog = remember { mutableStateOf(false) }
    val selectedHour = remember { mutableIntStateOf(0) }
    val selectedMinute = remember { mutableIntStateOf(0) }
    val selectedDay = remember { mutableIntStateOf(0) }
    val selectedMonth = remember { mutableIntStateOf(0) }
    val selectedYear = remember { mutableIntStateOf(0) }

    //States of Navigation icon (Back button) and Save icon (Save button)
    val navIconState = createNoteViewModel.navIconState
    val saveButtonState = createNoteViewModel.saveButtonState

    //Get the image from the gallery using the Coil library
    val getImageRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            if (uri != null) {
                JournalNotesApp.getUriPermission(uri)
                currentPhotos.value = uri.toString()
            }
        })
    //Main Content
    AppTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.primary) {
            Scaffold(
                topBar = {
                    //Uses a GenericAppBar composable defined in the file: Shared.kt
                    GenericAppBar(
                        title = "Create",
                        //For navigation, once clicked, goes back to the previous screen.
                        navIcon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.nav_arrow),
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        },
                        onBackIconClick = {
                            //Goes back to the previous screen.
                            //Also implemented it in a way where it will not cause bugs when pressing the back button too quick
                            if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                                navController.popBackStack()
                            }
                        },
                        //For saving the notes after creation, one clicked, saves the note and present it in the NoteListScreen.
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.save),
                                contentDescription = "Save Note",
                                tint = MaterialTheme.colorScheme.onPrimary,
                            )
                        },
                        onIconClick = {
                            viewModel.createNote(
                                currentTitle.value,
                                currentNote.value,
                                currentDescription.value,
                                currentTime.value,
                                currentDate.value,
                                currentLocation.value,
                                currentPhotos.value
                            )
                            //Goes back to the previous screen
                            if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                                navController.popBackStack()
                            }
                        },
                        //Navigation Icon state
                        navIconState = navIconState,
                        //Save Button Icon state
                        iconState = saveButtonState,
                    )
                },
                //The Floating Action Bar, allows users to select an image from their gallery. Indicated by a camera
                floatingActionButton = {
                    NotesFab(
                        contentDescription = "Add Image",
                        action = {
                            getImageRequest.launch(arrayOf("image/*"))
                        },
                        icon = R.drawable.camera,
                    )
                },
                //Display of the elements such as Image, Title, Note, Description, Time, Date, Location
                content = { innerPadding ->
                    //Use LazyColumns to display the elements. Similar to RecyclerView, but more lightweight.
                    LazyColumn(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                    ) {
                        //Display the elements -> Each are labeled with their corresponding names.
                        items(listOf("photo", "title", "note", "description", "time_date", "location")) { item ->
                            when (item) {
                                //Image
                                "photo" -> Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(270.dp)
                                ) {
                                    //Displays the picture selected by the user.
                                    if (currentPhotos.value.isNotEmpty()) {
                                        Image(
                                            painter = rememberAsyncImagePainter(
                                                ImageRequest.Builder(LocalContext.current)
                                                    .data(data = Uri.parse(currentPhotos.value))
                                                    .build()
                                            ),
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop,
                                        )
                                    } else {
                                        //If no image is selected, show a placeholder instead
                                        Text(
                                            text = "Image Placeholder",
                                            modifier = Modifier.align(Alignment.Center),
                                            style = MaterialTheme.typography.headlineMedium,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                                //Title
                                "title" -> OutlinedTextField(
                                    value = currentTitle.value,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 5.dp, start = 15.dp, end = 15.dp),
                                    maxLines = 1,
                                    onValueChange = { value ->
                                        currentTitle.value = value
                                        saveButtonState.value =
                                            (currentTitle.value.isNotEmpty() && currentNote.value.isNotEmpty())
                                    },
                                    label = { Text("Title") }
                                )
                                //Note
                                "note" -> OutlinedTextField(
                                    value = currentNote.value,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 5.dp, start = 15.dp, end = 15.dp),
                                    maxLines = 1,
                                    onValueChange = { value ->
                                        currentNote.value = value
                                        saveButtonState.value =
                                            currentTitle.value.isNotEmpty() && currentNote.value.isNotEmpty()
                                    },
                                    label = { Text("Note") },
                                )
                                //Description
                                "description" -> OutlinedTextField(
                                    value = currentDescription.value,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 5.dp, start = 15.dp, end = 15.dp)
                                        .height(150.dp),
                                    maxLines = 7,
                                    onValueChange = { value ->
                                        currentDescription.value = value
                                    },
                                    label = { Text("Description") },
                                )
                                //Time and Date, displayed as a row because I like the design and it is intuitive for users
                                "time_date" -> Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 5.dp)
                                ) {
                                    //Time (TextField to display the time selected by the user)
                                    OutlinedTextField(
                                        value = currentTime.value,
                                        onValueChange = { value ->
                                            currentTime.value = value
                                        },
                                        label = { Text("Time") },
                                        modifier = Modifier
                                            .padding(start = 15.dp)
                                            .weight(3f)
                                    )
                                    TextButton(onClick = { showTimePickerDialog.value = true }) {
                                        Text("Time")
                                    }
                                    //Date (TextField to display the date selected by the user)
                                    OutlinedTextField(
                                        value = currentDate.value,
                                        onValueChange = { value ->
                                            currentDate.value = value
                                        },
                                        label = { Text("Date") },
                                        modifier = Modifier.weight(3f)
                                    )
                                    TextButton(onClick = { showDatePickerDialog.value = true }) {
                                        Text("Date")
                                    }
                                }
                                //Location, users types this out themselves.
                                "location" -> OutlinedTextField(
                                    value = currentLocation.value,
                                    onValueChange = { value ->
                                        currentLocation.value = value
                                    },
                                    label = { Text("Location") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 5.dp, start = 15.dp, end = 15.dp)
                                )
                            }
                        }
                    }
                    //When the TimePickerDialog is clicked, on the UI it is shown as "Time"
                    if (showTimePickerDialog.value) {
                        TimePickerDialog(
                            title = "Select Time",
                            onConfirm = {
                                showTimePickerDialog.value = false
                                currentTime.value = String.format("%02d:%02d", selectedHour.intValue, selectedMinute.intValue)
                            },
                            onCancel = { showTimePickerDialog.value = false },
                            content = {
                                TimePickerComponent(selectedHour, selectedMinute)
                            }
                        )
                    }
                    //When the DatePickerDialog is clicked, on the UI it is shown as "Date"
                    if (showDatePickerDialog.value) {
                        DatePickerDialog(
                            title = "Select Date",
                            onConfirm = {
                                showDatePickerDialog.value = false
                                currentDate.value = String.format("%02d-%02d-%04d", selectedDay.intValue, selectedMonth.intValue + 1, selectedYear.intValue)
                            },
                            onCancel = { showDatePickerDialog.value = false },
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

