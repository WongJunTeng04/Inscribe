package com.example.journalapp.view.SharedComponents

//Imports
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.util.Calendar

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//!!!!!!!! THIS "Shared.kt" FILE IS FOR COMPONENT REUSE, TO PREVENT REPEATING CODE THROUGHOUT EACH FILE. !!!!!!!//////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//Composable for Top App Bar used in every .kt file in this project, unless stated otherwise
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenericAppBar(
    title: String,
    onBackIconClick: (() -> Unit)?,
    onIconClick: (() -> Unit)?,
    icon: @Composable (() -> Unit)?,
    navIcon: @Composable (() -> Unit)?,
    iconState: MutableState<Boolean>,
    navIconState: MutableState<Boolean>
) {
    //Materials 3 Top app bar
    CenterAlignedTopAppBar(
        //Title
        title = { Text(text = title) },
        //Colors
        colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        //The navigationIcon allows users to press on the back button to go back to the previous screen.
        //Except for in the main screen (The NoteListScreen), where it is used to show the tooltip instead (for aesthetics)
        navigationIcon = {
            IconButton(
                //Invokes the action when the icon is clicked
                onClick = {
                    onBackIconClick?.invoke()
                },
                content = {
                    if (navIconState.value){
                        navIcon?.invoke()
                    }
                }
            )
        },
        //The actions are like save and edit (Shown in the CreateNoteScreen and EditNoteScreen)
        actions ={
            IconButton(
                onClick = {
                    onIconClick?.invoke()
                },
                content = {
                    if (iconState.value){
                        icon?.invoke()
                    }
                }
            )
        }
    )
}

//Composable for TimePickerDialog (the small words at the side of the text field) used in CreateNote.kt and EditNote.kt
@Composable
fun TimePickerDialog(
    title: String = "Select Time",
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 5.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                item {
                    content()
                }
                item {
                    Row(
                        modifier = Modifier
                            .height(40.dp)
                            .fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(onClick = onCancel) {
                            Text("Cancel")
                        }
                        TextButton(onClick = onConfirm) {
                            Text("OK")
                        }
                    }
                }
            }
        }
    }
}


//Composable for DatePickerDialog (the small words at the side of the text field) used in CreateNote.kt and EditNote.kt
@Composable
fun DatePickerDialog(
    title: String = "Select Date",
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 5.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                item {
                    content()
                }
                item {
                    Row(
                        modifier = Modifier
                            .height(40.dp)
                            .fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(onClick = onCancel) {
                            Text("Cancel")
                        }
                        TextButton(onClick = onConfirm) {
                            Text("OK")
                        }
                    }
                }
            }
        }
    }
}
//Composable for TimePickerComponent (the pop up after pressing on the TimePickerDialog, allows you to choose time) used in CreateNote.kt and EditNote.kt
@Composable
fun TimePickerComponent(
    selectedHour: MutableState<Int>,
    selectedMinute: MutableState<Int>
) {
    //Uses AndroidView (legacy) since Materials 3 do not support DatePicker yet.
    AndroidView(
        factory = { ctx ->
            TimePicker(ctx).apply {
                setIs24HourView(true)
                setOnTimeChangedListener { _, hour, minute ->
                    selectedHour.value = hour
                    selectedMinute.value = minute
                }
            }
        },
        update = { view ->
            view.hour = selectedHour.value
            view.minute = selectedMinute.value
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    )
}

//Composable for DatePickerComponent (the pop up after pressing on the DatePickerDialog, allows you to choose time) used in CreateNote.kt and EditNote.kt
@Composable
fun DatePickerComponent(
    selectedDay: MutableState<Int>,
    selectedMonth: MutableState<Int>,
    selectedYear: MutableState<Int>
) {
    val calendar = Calendar.getInstance()
    calendar.apply {
        selectedDay.value = get(Calendar.DAY_OF_MONTH)
        selectedMonth.value = get(Calendar.MONTH)
        selectedYear.value = get(Calendar.YEAR)
    }
    //Uses AndroidView (legacy) since Materials 3 do not support DatePicker yet.
    AndroidView(
        factory = { ctx ->
            DatePicker(ctx).apply {
                init(
                    selectedYear.value,
                    selectedMonth.value,
                    selectedDay.value
                ) { _, year, month, day ->
                    selectedDay.value = day
                    selectedMonth.value = month
                    selectedYear.value = year
                }
            }
        },
        update = { view ->
            view.updateDate(selectedYear.value, selectedMonth.value, selectedDay.value)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    )
}

//Floating action Bar for multipurpose use in different screens and for convenience. This is shared between different screens so it's
//placed in the Shared.kt file.
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