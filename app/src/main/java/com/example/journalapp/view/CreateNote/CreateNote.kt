package com.example.journalapp.view.CreateNote

// Imports
import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.journalapp.view.SharedComponents.DatePickerComponent
import com.example.journalapp.view.SharedComponents.DatePickerDialog
import com.example.journalapp.view.SharedComponents.GenericAppBar
import com.example.journalapp.view.SharedComponents.TimePickerComponent
import com.example.journalapp.view.SharedComponents.TimePickerDialog
import com.example.journalapp.view.SharedComponents.saveBitmapToMediaStore
import com.example.journalapp.view.theme.AppTheme
import com.example.journalapp.viewModel.NotesViewModel

// To handle the mutableStateOf each variable: Photos, Title, Note, Description, Time, Date, Location, Navigation icon and Save icon
// Provides a way to reactively update and observe the state of the variables, responding to changes to the fields.
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

// Composable for Create Note Screen. This is the Create Note page users see after pressing the FAB "+" in the main page.
@SuppressLint("DefaultLocale")
@Composable
fun CreateNoteScreen(
    navController: NavController,
    viewModel: NotesViewModel
) {
    // To preserve the state of the viewModel
    val createNoteViewModel: CreateNoteViewModel = viewModel()

    // Get the current values of each variable. Helps ensure that the values are preserved even after recompositions and screen rotations.
    // Each variable considered in order (it is also displayed in this order in the UI):
    // 1) Photos, 2) Title, 3) Note, 4) Description, 5) Time, 6) Date, 7) Location, 8) Navigation icon, 9) Save icon
    val currentPhotos = createNoteViewModel.currentPhotos
    val currentTitle = createNoteViewModel.currentTitle
    val currentNote = createNoteViewModel.currentNote
    val currentDescription = createNoteViewModel.currentDescription
    val currentTime = createNoteViewModel.currentTime
    val currentDate = createNoteViewModel.currentDate
    val currentLocation = createNoteViewModel.currentLocation

    // These are states of the TimePicker and DatePickers so that even after rotation the values are preserved.
    val showTimePickerDialog = remember { mutableStateOf(false) }
    val showDatePickerDialog = remember { mutableStateOf(false) }
    val selectedHour = remember { mutableIntStateOf(0) }
    val selectedMinute = remember { mutableIntStateOf(0) }
    val selectedDay = remember { mutableIntStateOf(0) }
    val selectedMonth = remember { mutableIntStateOf(0) }
    val selectedYear = remember { mutableIntStateOf(0) }

    // States of Navigation icon (Back button) and Save icon (Save button)
    val navIconState = createNoteViewModel.navIconState
    val saveButtonState = createNoteViewModel.saveButtonState

    // Get the image from the gallery using the Coil library
    val getImageRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            if (uri != null) {
                JournalNotesApp.getUriPermission(uri)
                currentPhotos.value = uri.toString()
            }
        }
    )

    // Launch camera to take a picture
    val context = LocalContext.current
    val takePictureRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bitmap ->
            if (bitmap != null) {
                val uri = saveBitmapToMediaStore(context, bitmap)
                if (uri != null) {
                    currentPhotos.value = uri.toString()
                }
            }
        }
    )

    // Request camera permission to use the camera for taking a picture
    val requestCameraPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                takePictureRequest.launch()
            } else {
                //Shows a toast that explains to user why the camera permission is needed
                Toast.makeText(context, "Camera permission is required to take photos.", Toast.LENGTH_SHORT).show()
            }
        }
    )

    // Function to check if any field has value, used for logic of saving the note
    fun hasValues(): Boolean {
        return currentPhotos.value.isNotBlank() ||
                currentTitle.value.isNotBlank() ||
                currentNote.value.isNotBlank() ||
                currentDescription.value.isNotBlank() ||
                currentTime.value.isNotBlank() ||
                currentDate.value.isNotBlank() ||
                currentLocation.value.isNotBlank()
    }
    //Main Content Area
    AppTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.primary) {
            //Puts all elements into a scaffold so that they can be displayed
            Scaffold(
                topBar = {
                    // Uses a GenericAppBar composable defined in the file: Shared.kt
                    GenericAppBar(
                        title = "Create",
                        // For navigation, once clicked, goes back to the previous screen.
                        navIcon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.nav_arrow),
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        },
                        onBackIconClick = {
                            // Goes back to the previous screen.
                            // Also implemented it in a way where it will not cause bugs when pressing the back button too quick
                            if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                                navController.popBackStack()
                            }
                        },
                        // For saving the notes after creation, once clicked, saves the note and present it in the NoteListScreen.
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.save),
                                contentDescription = "Save Note",
                                tint = MaterialTheme.colorScheme.onPrimary,
                            )
                        },
                        // When the save button is clicked, the note is created and stored in the database. The title, note, description, time, date, location
                        // and photos are stored in the database.
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
                            // After saving, goes back to the previous screen
                            if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                                navController.popBackStack()
                            }
                        },
                        // Navigation Icon state
                        navIconState = navIconState,
                        // Save Button Icon state
                        iconState = saveButtonState,
                    )
                },
                // The Bottom Bar.
                // It allows users to clear all the TextFields including the Picture
                bottomBar = {
                    // Start of Bottom App Bar
                    BottomAppBar(
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        // Clear function, this allows the users to clear all the TextFields, including the Picture
                        IconButton(onClick = {
                            currentPhotos.value = ""
                            currentTitle.value = ""
                            currentNote.value = ""
                            currentDescription.value = ""
                            currentTime.value = ""
                            currentDate.value = ""
                            currentLocation.value = ""
                            saveButtonState.value = hasValues()
                        }) {
                            // Icon for clear, clear the entry for all the text fields and also the picture
                            Icon(
                                Icons.Filled.Clear,
                                contentDescription = "Clear Note",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        // Remove Image button
                        IconButton(onClick = {
                            currentPhotos.value = ""
                            saveButtonState.value = hasValues()
                        }) {
                            // Icon for removing the image. It is in the shape of a camera but crossed out. Intuitive and easy for users to understand.
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.remove_picture),
                                contentDescription = "Remove Image",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        // Allows users to confirm their changes. (It is shown as a tick)
                        // Once clicked, and conditions are met (at least one field has values) then go back to the previous screen.
                        IconButton(onClick = {
                            if (hasValues()) {
                                viewModel.createNote(
                                    currentTitle.value,
                                    currentNote.value,
                                    currentDescription.value,
                                    currentTime.value,
                                    currentDate.value,
                                    currentLocation.value,
                                    currentPhotos.value
                                )
                                // Save then go back to the previous screen.
                                if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                                    navController.popBackStack()
                                }
                            }
                        }) {
                            // Icon for confirming the changes. If no changes are made, just goes back to the previous screen, If there are changes, save it to the database.
                            Icon(
                                Icons.Filled.Done,
                                contentDescription = "Save Note",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        // For users to access their galleries and select an image to be placed in the entry
                        IconButton(onClick = {
                            getImageRequest.launch(arrayOf("image/*"))
                        }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.image),
                                contentDescription = "Add Image",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        // Spacer just makes space between elements
                        Spacer(Modifier.weight(1f, true))
                        // Allows users to take a picture, and use it for the entry.
                        IconButton(onClick = {
                            requestCameraPermission.launch(android.Manifest.permission.CAMERA)
                        }) {
                            // Image for the camera function.
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.camera),
                                contentDescription = "Take Picture",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                },
                // Display of the elements such as Image, Title, Note, Description, Time, Date, Location
                content = { innerPadding ->
                    // Use LazyColumns to display the elements. Similar to RecyclerView, but more lightweight.
                    LazyColumn(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                    ) {
                        // Display the elements -> Each are labeled with their corresponding names.
                        items(listOf("photo", "title", "note", "description", "time_date", "location")) { item ->
                            when (item) {
                                // Image (Is displayed in a Box compose element)
                                "photo" -> Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(260.dp)
                                ) {
                                    // Displays the picture selected by the user.
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
                                        // If no image is selected, show a placeholder instead
                                        Text(
                                            text = "Image Placeholder",
                                            modifier = Modifier.align(Alignment.Center),
                                            style = MaterialTheme.typography.headlineMedium,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                                // Title (OutlinedTextField for users to enter the title)
                                "title" -> OutlinedTextField(
                                    value = currentTitle.value, //Value for the title field
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 5.dp, start = 15.dp, end = 15.dp),
                                    maxLines = 1,
                                    onValueChange = { value ->
                                        currentTitle.value = value //Changes the value based on what the user has typed
                                        saveButtonState.value = (currentTitle.value.isNotEmpty() && currentNote.value.isNotEmpty())
                                    },
                                    label = { Text("Title") }
                                )
                                // Note (OutlinedTextField for users to enter a short note to relate it to what the note is about)
                                "note" -> OutlinedTextField(
                                    value = currentNote.value, // Value for the note field
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 5.dp, start = 15.dp, end = 15.dp),
                                    maxLines = 1,
                                    onValueChange = { value -> // Changes the value based on what the user has typed
                                        currentNote.value = value
                                        saveButtonState.value = (currentTitle.value.isNotEmpty() && currentNote.value.isNotEmpty())
                                    },
                                    label = { Text("Note") },
                                )
                                // Description (OutlinedTextField for users to enter a description for the note)
                                "description" -> OutlinedTextField(
                                    value = currentDescription.value, // Value for the description field
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 5.dp, start = 15.dp, end = 15.dp)
                                        .height(150.dp),
                                    maxLines = 7,
                                    onValueChange = { value ->
                                        currentDescription.value = value //Changes the value based on what the user has typed
                                    },
                                    label = { Text("Description") },
                                )
                                // Time and Date, displayed as a row because I like the design and it is intuitive for users
                                "time_date" -> Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 5.dp)
                                ) {
                                    // Time (TextField to display the time selected by the user)
                                    OutlinedTextField(
                                        value = currentTime.value, // Value for the time field
                                        onValueChange = { value ->
                                            currentTime.value = value // Changes the value based on what the user has typed or selected using the time picker
                                        },
                                        label = { Text("Time") },
                                        modifier = Modifier
                                            .padding(start = 15.dp)
                                            .weight(3f)
                                    )
                                    // Shows the TimePicker to allow users to choose the time (in the form of a clock)
                                    TextButton(onClick = { showTimePickerDialog.value = true }) {
                                        Text("Time")
                                    }
                                    // Date (TextField to display the date selected by the user)
                                    OutlinedTextField(
                                        value = currentDate.value, // Value for the date field
                                        onValueChange = { value ->
                                            currentDate.value = value // Changes the value based on what the user has typed or selected using the date picker
                                        },
                                        label = { Text("Date") },
                                        modifier = Modifier.weight(3f)
                                    )
                                    // Shows the DatePicker to allow users to choose the date (in the form of a calendar)
                                    TextButton(onClick = { showDatePickerDialog.value = true }) {
                                        Text("Date")
                                    }
                                }
                                // Location, users type this out themselves.
                                "location" -> OutlinedTextField(
                                    value = currentLocation.value, // Value for the location field
                                    onValueChange = { value ->
                                        currentLocation.value = value // Changes the value based on what the user has typed or selected using the location
                                    },
                                    label = { Text("Location") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 5.dp, start = 15.dp, end = 15.dp, bottom = 10.dp)
                                )
                            }
                        }
                    }
                    // When the TimePickerDialog is clicked it shows the Time Picker, on the UI it is shown as "Time"
                    // (If confirm, it stores the time the user has selected), (If cancel, it hides the TimePicker)
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
                    // When the DatePickerDialog is clicked, on the UI it is shown as "Date"
                    // (If confirm, it stores the date the user has selected), (If cancel, it hides the DatePicker)
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