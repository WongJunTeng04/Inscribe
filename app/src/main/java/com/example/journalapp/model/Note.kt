package com.example.journalapp.model

//Imports
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.journalapp.Constants
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//Represents a table for an individual NOTE, in the database
@Entity(tableName = Constants.TABLE_NAME, indices = [Index(value = ["id"], unique = true)])
data class Note(
    //1) Unique for each note (Primary Key) It is auto-generated
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    //2) The actual note
    @ColumnInfo(name = "note") val note: String,
    //3) The image URI
    @ColumnInfo(name = "imageUri") val imageUri: String? = null,
    //4) The title
    @ColumnInfo(name = "title") val title: String,
    //5) The description
    @ColumnInfo(name = "description") val description: String,
    //6) The time
    @ColumnInfo(name = "time") val time: String,
    //7) The date
    @ColumnInfo(name = "date") val date: String,
    //8) The location
    @ColumnInfo(name = "location") val location: String,
    //9) The date updated (When users create or edit a note, this gets updated as well)
    @ColumnInfo(name = "dateUpdated") val dateUpdated: String = getDateCreated(),
) {
    //Companion object for getting the Date created and updated (For Column 9 in the database)
    companion object {
        fun getDateCreated(): String {
            val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
            return dateFormat.format(Date())
        }
    }

    //Get the day when the note was created. Using Locale
    fun getDay(): String {
        if (this.dateUpdated.isEmpty()) return ""

        val inputFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        return try {
            val date: Date? = inputFormat.parse(this.dateUpdated)
            date?.let { outputFormat.format(it) } ?: ""
        } catch (e: ParseException) {
            ""
        }
    }
}
