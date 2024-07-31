package com.example.journalapp.view.NoteDetail

//Imports
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
import com.example.journalapp.viewModel.NotesViewModel
import com.example.journalapp.view.theme.AppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//Composable for displaying Note Detail. Used in NotesList.kt
@Composable
fun NoteDetailPage(noteId: Int, navController: NavController, viewModel: NotesViewModel) {
    val scope = rememberCoroutineScope() // Collect note details
    val note = remember { mutableStateOf(noteDetailPlaceHolder) }

    //Show navIcon, mutableStateOf(true)
    val navIconState = remember { mutableStateOf(true) }

    //Collects note details
    LaunchedEffect(true) {
        scope.launch(Dispatchers.IO) {
            note.value = viewModel.getNote(noteId) ?: noteDetailPlaceHolder
        }
    }
    //Main Theme
    AppTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Scaffold(
                topBar = {
                    GenericAppBar(
                        title = "Details", //Title of screen
                        //For navigation, using the nav_arrow, navIcon and onNavIconClick are related.
                        navIcon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.nav_arrow),
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        },
                        onBackIconClick = {
                            if (navController.currentBackStackEntry?.lifecycle?.currentState
                                == Lifecycle.State.RESUMED
                            ) {
                                navController.popBackStack()
                            }
                        },
                        //For editing, using the edit icon, icon and onIconClick are related.
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.edit),
                                contentDescription = "Edit Note",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        },
                        onIconClick = {
                            navController.navigate(Constants.noteEditNavigation(note.value.id ?: 0))
                        },
                        navIconState = navIconState,
                        iconState = remember { mutableStateOf(true) }
                    )
                },
                //Main Content of Note Detail Page
                content = { innerPadding ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    ) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(370.dp)
                            ) {
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
                                } else {
                                    Text(
                                        text = "Image",
                                        modifier = Modifier.align(Alignment.Center),
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                        item {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = note.value.title,
                                    modifier = Modifier
                                        .padding(top = 10.dp),
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = note.value.note,
                                    modifier = Modifier
                                        .padding(top = 4.dp),
                                    fontSize = 20.sp
                                )
                                Text(
                                    text = note.value.dateUpdated,
                                    modifier = Modifier
                                        .padding(top = 5.dp),
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                                HorizontalDivider(
                                    modifier = Modifier.padding(
                                        start = 10.dp,
                                        end = 10.dp,
                                        top = 10.dp
                                    )
                                )
                            }
                        }
                        item {
                            Column(
                                modifier = Modifier.padding(
                                    start = 20.dp,
                                    end = 20.dp,
                                    top = 10.dp,
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp)
                                ) {
                                    Text(
                                        text = "Description:",
                                        fontSize = 20.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = note.value.description,
                                        fontSize = 20.sp,
                                        modifier = Modifier.weight(2f).padding(start = 10.dp)
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp)
                                ) {
                                    Text(
                                        text = "Time:",
                                        fontSize = 20.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = note.value.time,
                                        fontSize = 20.sp,
                                        modifier = Modifier.weight(2f).padding(start = 10.dp)
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp)
                                ) {
                                    Text(
                                        text = "Date:",
                                        fontSize = 20.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = note.value.date,
                                        fontSize = 20.sp,
                                        modifier = Modifier.weight(2f).padding(start = 10.dp)
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp, bottom = 10.dp)
                                ) {
                                    Text(
                                        text = "Location:",
                                        fontSize = 20.sp,
                                        modifier = Modifier.weight(1f)
                                    )
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

