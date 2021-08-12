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
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class MessageActivity : AppCompatActivity() {
    private lateinit var viewModel: TaskViewModel
    private lateinit var dateInput: EditText
    private lateinit var timeInput: EditText
    private lateinit var nameInput: TextInputEditText
    private var namei :String? = null
    private var edit = false
    private var cal: Calendar = Calendar.getInstance()
    private var cali: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        val intent = intent
        namei = intent.getStringExtra("name")
        val messagei = intent.getStringExtra("message")
        val datei = intent.getStringExtra("date")
        val timei = intent.getStringExtra("time")

        if ( messagei != null && datei != null) {
            edit = true
            nameInput = findViewById(R.id.nameInputET)
            nameInput.setText(namei)
            val message: TextInputEditText = findViewById(R.id.messageInput)
            message.setText(messagei)
            dateInput = findViewById(R.id.dateInput)
            dateInput.setText(datei)
            timeInput= findViewById(R.id.timeInput)
            timeInput.setText(timei)
        }
        viewModel = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)).
        get(TaskViewModel::class.java)
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
                this,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )

            bro.datePicker.minDate = cali.timeInMillis
            bro.show()


        }
        timeInput.setOnClickListener {

            TimePickerDialog(this,timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE), false).show()

        }
    }
    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        dateInput.setText(sdf.format(cal.time))
    }
    private fun updateTimeInView() {
        if(cal.timeInMillis >= cali.timeInMillis) {
            val myFormat = "hh:mm aa"
            val stf = SimpleDateFormat(myFormat, Locale.US)
            timeInput.setText((stf.format(cal.time)))
        }
        else
        {
            Toast.makeText(this, "Invalid time", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.act_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_favorite && !edit) {
            val name: TextInputEditText = findViewById(R.id.nameInputET)

            val nameInput = name.text.toString()
            val message: TextInputEditText = findViewById(R.id.messageInput)
            val messageInput = message.text.toString()
            val date: EditText = findViewById(R.id.dateInput)
            val dateInput = date.text.toString()
            val time: EditText = findViewById(R.id.timeInput)
            val timeInput = time.text.toString()
            if(messageInput.isNotEmpty() && dateInput.isNotEmpty()
                && timeInput.isNotEmpty()){
                viewModel.insertTask(Task("", nameInput,"",
                    messageInput, dateInput, timeInput))
                finish()
            }
            else
                Toast.makeText(this, "Insert all the details", Toast.LENGTH_SHORT).show()
            return true
        }
        else if(edit){
            val name: TextInputEditText = findViewById(R.id.nameInputET)
            val nameInput = name.text.toString()
            val message: TextInputEditText = findViewById(R.id.messageInput)
            val messageInput = message.text.toString()
            val date: EditText = findViewById(R.id.dateInput)
            val dateInput = date.text.toString()
            val time: EditText = findViewById(R.id.timeInput)
            val timeInput = time.text.toString()
            if( messageInput.isNotEmpty() && dateInput.isNotEmpty()
                && timeInput.isNotEmpty()){
                viewModel.updateTaskByMessage("",  nameInput,"",
                    messageInput, dateInput, timeInput, namei)
                finish()
            }
              else
                Toast.makeText(this, "Insert all the details", Toast.LENGTH_SHORT).show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}