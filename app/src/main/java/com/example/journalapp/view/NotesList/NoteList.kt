package com.example.journalapp.view.NotesList

//Import list
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.journalapp.Constants
import com.example.journalapp.R
import com.example.journalapp.model.Note
import com.example.journalapp.view.theme.AppTheme
import com.example.journalapp.viewModel.NotesViewModel

//Main page, main entry point for the app.
//Main Composable. Shows the notes made by users. Connects all the different Composable to make the UI.
@OptIn(ExperimentalMaterial3Api::class) //Experimental class because I used materials 3.
@Composable
fun NoteListScreen(
    navController: NavController,
    viewModel: NotesViewModel
) {
    //Remember the mutable state of each variable to operate on them later on in different places. Crucial
    val openDialog = remember { mutableStateOf(false) }
    val deleteText = remember { mutableStateOf("") }
    val noteQuery = remember { mutableStateOf("") }
    val notesToDelete = remember { mutableStateOf(listOf<Note>()) }
    val notes = viewModel.notes.observeAsState()
    val context = LocalContext.current

    // State to track the visibility of the search bar
    val isSearchBarVisible = remember { mutableStateOf(false) }
    // State to track the visibility of the tooltip
    val toolTipShow = remember { mutableStateOf(false) }

    //Content area
    AppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                //Top App Bar that has the search and delete buttons; Title as well
                //Does not use the (GenericAppBar in Shared.kt) because I want to customize it.
                topBar = {
                    CenterAlignedTopAppBar(
                        //Colors for the Top App Bar
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        //Title
                        title = {
                            Text(
                                "INFOPAL",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        //Tool tip, when user's press on it, a tool tip is shown, does not navigate users anywhere.
                        navigationIcon = {
                            IconButton(onClick = {
                                toolTipShow.value = !toolTipShow.value
                            }) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = "Menu",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        },
                        //The actions are Search and Delete All Notes
                        actions = {
                            //Search and query notes
                            IconButton(onClick = {
                                isSearchBarVisible.value = !isSearchBarVisible.value
                            }) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(id = R.drawable.search),
                                    contentDescription = "Search for notes",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            //Delete all notes.
                            IconButton(onClick = {
                                if (notes.value?.isNotEmpty() == true) {
                                    openDialog.value = true
                                    deleteText.value = "Are you sure you want to delete all notes?"
                                    notesToDelete.value = notes.value ?: emptyList()
                                } else {
                                    Toast.makeText(context,
                                        "No notes to delete", Toast.LENGTH_SHORT).show()
                                }
                            }) {
                                //Delete icon
                                Icon(
                                    imageVector = ImageVector.vectorResource(id = R.drawable.delete),
                                    contentDescription = "Delete all notes",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    )
                },
                //Floating action Bar to let users create the note, when pressed, brings you to the CreateNoteScreen
                floatingActionButton = {
                    NotesFab(
                        contentDescription = "Create Note",
                        action = { navController.navigate(Constants.NAVIGATION_NOTES_CREATE) },
                        icon = R.drawable.add
                    )
                }
            ) { paddingValues ->
                //Displays the notes.
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    //Search Bar, Hide and Show with some simple animations
                    AnimatedVisibility(
                        visible = isSearchBarVisible.value,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        //Allows the search bar to filter notes based on what users have typed in the search
                        SearchBar(query = noteQuery, isSearchBarVisible = isSearchBarVisible)
                    }
                    //The notes items displayed
                    notes.value?.let {
                        if (it.isNotEmpty()) {
                            NotesList(
                                notes = it,
                                openDialog = openDialog,
                                query = noteQuery,
                                deleteText = deleteText,
                                navController = navController,
                                notesToDelete = notesToDelete
                            )
                        }
                    }
                }
                //Delete dialog shows the confirmation dialog when users want to delete notes. Prevents accidental deletions.
                DeleteDialog(
                    openDialog = openDialog,
                    text = deleteText,
                    action = {
                        notesToDelete.value.forEach {
                            viewModel.deleteNote(it)
                        }
                        Toast.makeText(context, "Notes Deleted", Toast.LENGTH_SHORT).show()
                        notesToDelete.value = mutableListOf()
                    }
                )
                // Show Tool Tip when users click on the Info icon
                ToolTip(toolTipShow)
            }
        }
    }
}

//Composable for Search Bar, allows users to search for notes based on:
// title, note, description, location, date, and time
@Composable
fun SearchBar(query: MutableState<String>, isSearchBarVisible: MutableState<Boolean>) {
    Column(Modifier.padding()) {
        OutlinedTextField(
            label = { Text("Search...") },
            value = query.value,
            onValueChange = { query.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 5.dp, end = 5.dp),
            maxLines = 1,
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search Button"
                )
            },
            trailingIcon = {
                IconButton(onClick = {
                    //Clears the search bar if there is text in the search bar.
                    if (query.value.isNotEmpty()) {
                        query.value = ""
                    } else {
                        //If there is nothing in the search bar, pressing on the "X" icon will close the search bar.
                        isSearchBarVisible.value = false
                    }
                }) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "Clear Search"
                    )
                }
            }
        )
    }
}

//Composable for Notes, the individual notes are combined into one body to be displayed.
@Composable
fun NotesList(
    notes: List<Note>,
    openDialog: MutableState<Boolean>,
    query: MutableState<String>,
    deleteText: MutableState<String>,
    navController: NavController,
    notesToDelete: MutableState<List<Note>>
) {
    //Track the previous header, the header is the Date that is displayed on top of the notes (The date that the note is created or updated)
    var previousHeader = ""
    LazyColumn(
        contentPadding = PaddingValues(12.dp),
        modifier = Modifier
            .fillMaxSize()
    ) {
        //Search for notes
        val queriedNotes = if (query.value.isEmpty()) {
            notes
        } else {
            //Filters the notes based on the query, if it has the properties, then the search will be displayed
            //Properties: note, title, description, location, date, and time
            notes.filter { it.note.contains(query.value, true) ||
                        it.title.contains(query.value, true) ||
                        it.description.contains(query.value, true)||
                        it.location.contains(query.value, true)||
                        it.date.contains(query.value, true) ||
                        it.time.contains(query.value, true)
            }
        }
        //Displays the journal notes that the user have made
        itemsIndexed(queriedNotes) { _, note ->
            //The logic is: If the note's date created is not the same as the Header's date, then that means
            //it is a new day and a section for that date will be created for the note.
            if (note.getDay() != previousHeader) {
                Column(
                    modifier = Modifier
                        .padding(6.dp)
                        .fillMaxWidth()
                ) {
                    //Gets the day for the header
                    Text(
                        text = note.getDay(),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                )
                previousHeader = note.getDay()
                HorizontalDivider(modifier = Modifier.padding(bottom = 10.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            //Displays the notes under the previousHeader element.
            NoteListItem(
                note,
                openDialog,
                deleteText = deleteText,
                navController,
                notesToDelete = notesToDelete,
                noteBackground = MaterialTheme.colorScheme.primaryContainer
            )
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
            )

        }
    }
}

//Composable for each individual note. Separated to make it easier to understand for people.
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteListItem(
    note: Note,
    openDialog: MutableState<Boolean>,
    deleteText: MutableState<String>,
    navController: NavController,
    noteBackground: Color,
    notesToDelete: MutableState<List<Note>>
) {
    // State to manage the visibility of the additional information, when users click on the hamburger menu
    val isExpanded = remember { mutableStateOf(false) }

    //Returns the notes in a Box container
    return Box(modifier = Modifier.clip(RoundedCornerShape(12.dp))) {
        //Creates a clickable note
        Column(
            modifier = Modifier
                .background(noteBackground)
                .fillMaxWidth()
                //These 2 modifiers allow the list to take as much space as they need.
                //Allows dynamic sizing according to what the user has entered.
                .wrapContentWidth() // Let width be determined by content
                .wrapContentHeight() // Let height be determined by content
                .padding(12.dp)

                //Allows users to navigate or delete each individual note. A short click and a long click
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false),
                    //Press once to go the note (short click)
                    onClick = {
                        if (note.id != 0) {
                            navController.navigate(
                                Constants.noteDetailNavigation(noteId = note.id ?: 0)
                            )
                        }
                    },
                    //Press on the note for a long time to delete that individual note.
                    onLongClick = {
                        if (note.id != 0) {
                            openDialog.value = true
                            deleteText.value = "Are you sure you want to delete this note?"
                            notesToDelete.value = mutableListOf(note)
                        }
                    }
                )
        ) {
            //Title for the note
            Text(
                text = note.title,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontSize = 20.sp,
            )
            //Row for location and dropdown for notes-(To display more info)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                //Location
                Icon(Icons.Outlined.Place, contentDescription = null)
                Text(
                    text = "Location: " + note.location,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                // Add an IconButton for the dropdown arrow
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { isExpanded.value = !isExpanded.value }) {
                    Icon(
                        imageVector = if (isExpanded.value) Icons.Filled.ArrowDropDown else Icons.Filled.Menu,
                        contentDescription = if (isExpanded.value) "Show less" else "Show more"
                    )
                }
            }
            // Use AnimatedVisibility to show/hide additional information
            AnimatedVisibility(visible = isExpanded.value) {
                Column(
                    modifier = Modifier.padding(start = 8.dp, top = 8.dp)
                ) {
                    if (!note.imageUri.isNullOrEmpty()) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                ImageRequest
                                    .Builder(LocalContext.current)
                                    .data(data = android.net.Uri.parse(note.imageUri))
                                    .build()
                            ),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                    //Short note
                    Text(
                        text = "Note: " + note.note,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 15.dp),
                    )
                    //Description
                    Text(
                        text = "Description: " + note.description,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 15.dp),
                    )
                    //Time
                    Text(
                        text = "Time: " + note.time,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 15.dp),
                    )
                    //Date
                    Text(
                        text = "Date: " + note.date,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 15.dp),
                    )
                    //Date updated which is also Last Modified
                    Text(
                        text = "Last Modified: " + note.dateUpdated,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .padding(top = 15.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}

//Delete dialog used as  confirmation dialogs for deletion of notes.
@Composable
fun DeleteDialog(
    openDialog: MutableState<Boolean>,
    text: MutableState<String>,
    action: () -> Unit
) {
    //If the openDialog is true, show the dialog. It must be triggered either by pressing the "Delete" button or long pressing on a note (Long-press to delete a note)
    if (openDialog.value) {
        //Shows the AlertDialog which is similar to a confirmation box.
        AlertDialog(
            icon = {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.delete),
                    contentDescription = "Delete Note"
                )
            },
            //Title
            title = {
                Text(
                    text = "DELETE",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold)
            },
            //Shows the different dialogs, one is for deletion of all notes, and another is for deletion of one note.
            text = {
                Column {
                    Text(text = text.value)
                }
            },
            //Confirm button. Invoke() the actions which is deleting the note(s)
            confirmButton = {
                TextButton(
                    onClick = {
                        action.invoke()
                        openDialog.value = false
                    },
                ) {
                    Text(text = "Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                    }
                ) {
                    Text(text = "CANCEL",
                        fontWeight = FontWeight.Bold)
                }
            },
            //Closes the AlertDialog
            onDismissRequest = {
                openDialog.value = false
            }
        )
    }
}

//Composable for FAB (Floating Action Bar), to allow users to create notes.
@Composable
fun NotesFab(contentDescription: String, icon: Int, action: () -> Unit) {
    FloatingActionButton(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        onClick = { action.invoke() },
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = icon),
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

//This is a tooltip composable to allow users to read about tips.eee
@Composable
fun ToolTip(toolTipShow: MutableState<Boolean>) {
    AnimatedVisibility(
        visible = toolTipShow.value,
        enter = fadeIn(animationSpec = tween(durationMillis = 300)),
        exit = fadeOut(animationSpec = tween(durationMillis = 300))
    ) {
        if (toolTipShow.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .background(color = Color.White, shape = MaterialTheme.shapes.medium)
                        .padding(10.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "TIPS OF INFOPAL",
                            style = MaterialTheme.typography.headlineLarge
                        )
                        HorizontalDivider(modifier = Modifier.padding(10.dp))
                        Spacer(modifier = Modifier.height(15.dp))
                        Text(
                            text = "Welcome to INFOPAL"
                        )
                        Spacer(modifier = Modifier.height(30.dp))
                        Text(
                            text = "INFOPAL is your daily journal, where you can store all your thoughts and experiences."
                        )
                        Spacer(modifier = Modifier.height(15.dp))
                        Text(
                            text = "Tips:"
                        )
                        Spacer(modifier = Modifier.height(15.dp))
                        Text(
                            text = "1) Press once on a note to view the details of the note."
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "2) Long press/click on a note to delete the note."
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "3) Press the FAB, which is the, + icon, to create a note."
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "4) Press on the Hamburger menu on the note to view more details."
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "That's it! Happy Journaling Everyone!"
                        )
                        TextButton(onClick = { toolTipShow.value = false }) {
                            Text(
                                text = "Dismiss",
                                modifier = Modifier.padding(top = 30.dp),
                                fontSize = 20.sp
                            )
                        }
                    }
                }
            }
        }
    }
}