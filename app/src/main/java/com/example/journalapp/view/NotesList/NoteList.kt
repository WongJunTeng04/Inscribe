package com.example.journalapp.view.NotesList

//Imports
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.journalapp.view.SharedComponents.NotesFab
import com.example.journalapp.view.theme.AppTheme
import com.example.journalapp.viewModel.NotesViewModel

//NoteListScreen is the screen that displays the journal entries (notes) in a list and acts as the main page.
//When users start the app, the first composable they will see is this composable.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(
    navController: NavController,
    viewModel: NotesViewModel
) {
    //Variables
    val openDialog = remember { mutableStateOf(false) }
    val deleteText = remember { mutableStateOf("") }
    val noteQuery = remember { mutableStateOf("") }
    val notesToDelete = remember { mutableStateOf(listOf<Note>()) }
    val notes = viewModel.notes.observeAsState()
    val context = LocalContext.current

    //Variables
    val isSearchBarVisible = remember { mutableStateOf(false) }
    val toolTipShow = remember { mutableStateOf(false) }

    AppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                topBar = {
                    //Top app bar
                    CenterAlignedTopAppBar(
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
                        //Navigation icon. In this navigation icon, it is different from other screens because it displays a ToolsTip instead of
                        // allowing users to navigate. This is done for the aesthetics of the app. It is just a design not a flaw in logic
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
                        //Actions. There are two actions here (Search and Delete)
                        //1) Search: Allows users to query their journal entries (notes) based on keywords. It queries the notes based on their title,
                        //note, description, time and date.
                        //2) Allows users to quickly delete all notes, done for easier access and deletion of all notes instead of deleting all notes one by one.

                        //1) Search (When users click on it, the search bar will appear, become visible.)
                        actions = {
                            IconButton(onClick = {
                                isSearchBarVisible.value = !isSearchBarVisible.value
                            }) {
                                //Search Icon
                                Icon(
                                    imageVector = ImageVector.vectorResource(id = R.drawable.search),
                                    contentDescription = "Search for notes",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            //2) Delete all notes (When users click on it, they can delete all the notes in an instant after going through the confirmation process by
                            // pressing confirm in the dialog box)
                            // If there are notes, then show the dialog and once confirmed, empties the note list.
                            IconButton(onClick = {
                                if (notes.value?.isNotEmpty() == true) {
                                    openDialog.value = true
                                    deleteText.value = "Are you sure you want to delete all notes?"
                                    notesToDelete.value = notes.value ?: emptyList()
                                } else { //If there are no notes, shows a toast message telling users that there are no notes to delete.
                                    Toast.makeText(context,
                                        "No notes to delete", Toast.LENGTH_SHORT).show()
                                }
                            }) {
                                //Icon
                                Icon(
                                    imageVector = ImageVector.vectorResource(id = R.drawable.delete),
                                    contentDescription = "Delete all notes",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    )
                },
                //The floating action bar allows users to create a note by pressing on the "+" icon which brings them to the CreateNoteScreen.kt
                floatingActionButton = {
                    NotesFab(
                        contentDescription = "Create Note",
                        action = { navController.navigate(Constants.NAVIGATION_NOTES_CREATE) }, //Navigates them to the create screen
                        icon = R.drawable.add
                    )
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    //Allows the search bar to be hidden when not in use.
                    AnimatedVisibility(
                        visible = isSearchBarVisible.value,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        //Searches the notes
                        SearchBar(query = noteQuery, isSearchBarVisible = isSearchBarVisible)
                    }
                    //Displays the notes
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
                //Opens the delete dialog for each individual deletion of a note.
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
                //Shows the tooltip, if users press on the information icon (located at the top left of the TopAppBar)
                ToolTip(toolTipShow)
            }
        }
    }
}

//Search Bar Component
@Composable
fun SearchBar(query: MutableState<String>, isSearchBarVisible: MutableState<Boolean>) {
    Column(Modifier.padding()) {
        OutlinedTextField(
            //Label for search bar
            label = { Text("Search...") },
            //The things that users type into the search bar is the value which is a query.value value in this case
            value = query.value,
            //Changes the value to what the users have types
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
            // Behaves differently when there is text and no text.
            //1) When there is text in the search bar, clears it
            //2) When there is no text in the search bar, hides the search bar
            trailingIcon = {
                //Clears the search bar
                IconButton(onClick = {
                    if (query.value.isNotEmpty()) {
                        query.value = ""
                    } else {
                        isSearchBarVisible.value = false //Hides the search bar
                    }
                }) {
                    //Icon is in the shape of an "x" to indicate clear.
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "Clear Search"
                    )
                }
            }
        )
    }
}

//Displays the notes, grouped NoteListItem
@Composable
fun NotesList(
    notes: List<Note>,
    openDialog: MutableState<Boolean>,
    query: MutableState<String>,
    deleteText: MutableState<String>,
    navController: NavController,
    notesToDelete: MutableState<List<Note>>
) {
    //Group the notes by day in Descending order
    val groupedNotes = if (query.value.isEmpty()) {
        notes.sortedByDescending { it.dateUpdated }.groupBy { it.getDay() }
        //Query for the search
    } else {
        notes.filter { it.note.contains(query.value, true) ||
                it.title.contains(query.value, true) ||
                it.description.contains(query.value, true) ||
                it.location.contains(query.value, true) ||
                it.date.contains(query.value, true) ||
                it.time.contains(query.value, true)
        }.sortedByDescending { it.dateUpdated }.groupBy { it.getDay() }
    }
    //Displays the note. Notes are grouped by their days.
    LazyColumn(
        contentPadding = PaddingValues(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        //Logic for grouping notes based on the days
        groupedNotes.forEach { (header, notesForHeader) ->
            item {
                Column(
                    modifier = Modifier
                        .padding(6.dp)
                        .fillMaxWidth()
                ) {
                    //Header
                    Text(
                        text = header,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                    )
                }
                //Spacer just places a space between elements
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                )
                //HorizontalDivider is for aesthetic purposes.
                HorizontalDivider(
                    modifier = Modifier.padding(bottom = 10.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            //Displays the notes
            itemsIndexed(notesForHeader, key = { index, note -> note.id ?: index }) { index, note ->
                NoteListItem(
                    note = note,
                    openDialog = openDialog,
                    deleteText = deleteText,
                    navController = navController,
                    notesToDelete = notesToDelete,
                    noteBackground = MaterialTheme.colorScheme.primaryContainer
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                )
            }
        }
    }
}

//This composable is for each individual notes, which is displayed in the NoteListScreen.kt
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
    //When users press on the hamburger menu, the list is expanded
    val isExpanded = remember { mutableStateOf(false) }

    //Displays the note
    Box(modifier = Modifier.clip(RoundedCornerShape(12.dp))) {
        Column(
            modifier = Modifier
                .background(noteBackground)
                .fillMaxWidth()
                //Allows the content to take as much width and height as they need
                .wrapContentWidth() //Takes up as much width as it needs
                .wrapContentHeight() //Takes up as much height as it needs
                .padding(12.dp)
                //Allows users to do two different things in 2 different ways: 1) Short click = Navigate to the note 2) Long click = Delete note
                .combinedClickable(
                    //Interaction source is for the ripple effect
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false),
                    //On short clicks, the users are redirected to the NoteDetails where they are shown the note's details in more detail.
                    onClick = {
                        if (note.id != 0) {
                            navController.navigate(
                                Constants.noteDetailNavigation(noteId = note.id ?: 0)
                            )
                        }
                    },
                    //On long clicks, the users can delete the individual notes they want.
                    onLongClick = {
                        if (note.id != 0) {
                            openDialog.value = true
                            deleteText.value = "Are you sure you want to delete this note?"
                            notesToDelete.value = mutableListOf(note)
                        }
                    }
                )
        ) {
            //Shows the title
            Text(
                text = note.title,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontSize = 20.sp,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                //Shows the location and hamburger menu icon in a row, for aesthetic purposes.
                Icon(Icons.Outlined.Place, contentDescription = null)
                Text(
                    text = "Location: " + note.location,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.weight(1f))
                //Hamburger menu, expands the note details
                IconButton(onClick = { isExpanded.value = !isExpanded.value }) {
                    Icon(
                        imageVector = if (isExpanded.value) Icons.Filled.ArrowDropDown else Icons.Filled.Menu,
                        contentDescription = if (isExpanded.value) "Show less" else "Show more"
                    )
                }
            }
            AnimatedVisibility(visible = isExpanded.value) {
                Column(
                    modifier = Modifier.padding(start = 8.dp, top = 8.dp)
                ) {
                    if (!note.imageUri.isNullOrEmpty()) {
                        //Displays the image
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
                    //Shows a short note that is associated with the journal entry (note) that gives a brief description to remind users what that note is about
                    Text(
                        text = "Note: " + note.note,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 15.dp),
                    )
                    //Shows the description of the note
                    Text(
                        text = "Description: " + note.description,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 15.dp),
                    )
                    //Shows the time of the note (the user sets this)
                    Text(
                        text = "Time: " + note.time,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 15.dp),
                    )
                    //Shows the date of the note (the user sets this)
                    Text(
                        text = "Date: " + note.date,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 15.dp),
                    )
                    //Shows the date the note was last updated
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

//Delete dialog composable acts as a confirmation box. This shows up when users are deleting all notes or deleting a single note.
@Composable
fun DeleteDialog(
    openDialog: MutableState<Boolean>,
    text: MutableState<String>,
    action: () -> Unit
) {
    if (openDialog.value) {
        AlertDialog(
            icon = {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.delete),
                    contentDescription = "Delete Note"
                )
            },
            title = {
                Text(
                    text = "DELETE",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(text = text.value)
                }
            },
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
            onDismissRequest = {
                openDialog.value = false
            }
        )
    }
}



//Tool tip to show the users some tips about how to use the app.
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
                    .clickable { /* No action, just to consume clicks so that the notes behind the Tool tips won't be pressed.*/ }
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
