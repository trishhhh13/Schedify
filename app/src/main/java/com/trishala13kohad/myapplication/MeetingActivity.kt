package com.trishala13kohad.myapplication

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import java.text.SimpleDateFormat
import java.util.*


class MeetingActivity : AppCompatActivity() {
    private lateinit var viewModel: TaskViewModel
    private lateinit var dateInput: EditText
    private lateinit var timeInput: EditText
    private var cal: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meeting)
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(TaskViewModel::class.java)
        dateInput = findViewById(R.id.dateInput)
        timeInput = findViewById(R.id.timeInput)
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        val timeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
            cal.set(Calendar.MINUTE, minute)
            updateTimeInView()
        }
        dateInput.setOnClickListener {
            val bro = DatePickerDialog(
                this@MeetingActivity,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH))
            bro.datePicker.minDate = cal.timeInMillis
            bro.show()
        }
        timeInput.setOnClickListener {
            TimePickerDialog(this,timeSetListener, cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE), true).show()
        }
    }

    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        dateInput.setText(sdf.format(cal.time))
    }
    private fun updateTimeInView() {
        val myFormat = "HH:MM"
        val stf = SimpleDateFormat(myFormat, Locale.US)
        timeInput.setText((stf.format(cal.time)))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.act_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

            if (id == R.id.action_favorite) {
                val title: EditText = findViewById(R.id.nameInput)
                val titleInput = title.text.toString()
                val link: EditText = findViewById(R.id.messageInput)
                val linkInput = link.text.toString()
                val date: EditText = findViewById(R.id.dateInput)
                val dateInpu = date.text.toString()
                val time: EditText = findViewById(R.id.timeInput)
                val timeInput = time.text.toString()
                if (titleInput.isNotEmpty() && linkInput.isNotEmpty() && dateInpu.isNotEmpty()
                    && timeInput.isNotEmpty()) {
                    viewModel.insertTask(Task(titleInput, "", linkInput, "",
                            dateInpu, timeInput))
                    finish()
                    Toast.makeText(this, "Action clicked", Toast.LENGTH_SHORT).show()
                } else
                    Toast.makeText(this, "Insert all the details", Toast.LENGTH_SHORT).show()
                return true
            }
            return super.onOptionsItemSelected(item)
        }
}


