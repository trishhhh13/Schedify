package com.trishala13kohad.myapplication

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
data class Task(val title: String, val name: String, val url: String, val message: String,
                val date: String, val time: String){

    @PrimaryKey (autoGenerate = true) var id = 0

}
