package com.trishala13kohad.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast

class MeetingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meeting)
    }
//    fun submitData(view: View) {
//        val input : EditText = findViewById(R.id.input)
//        val data = input.text.toString()
//        if(data.isNotEmpty()){
//            viewModel.insertTask(Task(data,"hekk","","","ee","b"))
//            input.setText("")
//            Toast.makeText(this, "Inserted the note", Toast.LENGTH_SHORT).show()
//        }
//        else
//            Toast.makeText(this, "Insert a note", Toast.LENGTH_SHORT).show()
//    }
}