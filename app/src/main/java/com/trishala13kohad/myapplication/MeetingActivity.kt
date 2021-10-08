package com.trishala13kohad.myapplication

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates
import android.app.PendingIntent
import kotlinx.coroutines.DelicateCoroutinesApi


//Meeting activity to take input details from the user
class MeetingActivity : AppCompatActivity() {

    private lateinit var viewModel: TaskViewModel

    private lateinit var editTextDate: EditText
    private lateinit var editTextTime: EditText
    private lateinit var editTextTitle: EditText
    private lateinit var editTextLink: EditText

    private var previousLink: String? = null
    private var previousTitle: String? = null
    private var previousDate: String? = null
    private var previousTime: String? = null
    private var previousEventId by Delegates.notNull<Int>()

    private var isEditing = false
    private var toDelete = false

    private var cal: Calendar = Calendar.getInstance()
    private var cali: Calendar = Calendar.getInstance()

    @DelicateCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meeting)

        previousTitle = intent.getStringExtra("title")
        previousLink = intent.getStringExtra("url")
        previousDate = intent.getStringExtra("date")
        previousTime = intent.getStringExtra("time")
        previousEventId = intent.getIntExtra("eventId", 0)
        toDelete = intent.getBooleanExtra("toDelete", false)


        //getting all input fields edittext
        editTextLink = findViewById(R.id.linkInput)
        editTextTitle = findViewById(R.id.titleInput)
        editTextDate = findViewById(R.id.dateInputM)
        editTextTime = findViewById(R.id.timeInputM)

        if (previousTitle != null) {
            //when editing a previous task
                if(!toDelete) {isEditing = true}
            editTextLink.setText(previousLink)
            editTextTitle.setText(previousTitle)
            editTextTime.setText(previousTime)
            editTextDate.setText(previousDate)
        }
        if(toDelete){
            deleteAlertDialog()
        }

        viewModel = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application))
            .get(TaskViewModel::class.java)


        //setting date in date picker dialog
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }

        //called when user wants to select date
        editTextDate.setOnClickListener {
            val datesPicker = DatePickerDialog(
                this@MeetingActivity, dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )

            //setting the minimum date as today
            datesPicker.datePicker.minDate = cali.timeInMillis
            datesPicker.show()
        }

        //setting time in time picker dialog
        val timeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
            cal.set(Calendar.MINUTE, minute)
            updateTimeInView()
        }

        //called when user wants to select time
        editTextTime.setOnClickListener {
            TimePickerDialog(this@MeetingActivity,
                timeSetListener, cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE), false).show()
        }

        //registering broadcast receiver
        val broadCastReceiver = NotificationReceiver()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(broadCastReceiver, IntentFilter(NOTIFICATION_SERVICE))
    }

    @DelicateCoroutinesApi
    private fun deleteAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to Delete?")
            .setCancelable(false)
            .setPositiveButton("DELETE") { dialog, id ->
                // Delete selected note from database
                cancelMeetingAndNotification(previousTitle!!, previousLink!!, previousEventId)
                dialog.dismiss()
                GlobalScope.launch {
                    val task: List<Task> = viewModel.taskByTitle(previousTitle!!, previousLink!!)
                    viewModel.deleteTask(task[0])
                }
                finish()
            }
            .setNegativeButton("CANCEL") { dialog, id ->
                dialog.dismiss()
                finish()
            }
        val alert = builder.create()
        alert.show()
        }

    private fun updateDateInView() {
        //setting date in the edittext
        if (cal.timeInMillis >= cali.timeInMillis) {
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            editTextDate.setText(sdf.format(cal.time))
        } else {
            Toast.makeText(this, "Invalid time", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateTimeInView() {
        //setting time in the edittext
        if (cal.timeInMillis >= cali.timeInMillis) {
            val myFormat = "hh:mm aa"
            val stf = SimpleDateFormat(myFormat, Locale.US)
            editTextTime.setText((stf.format(cal.time)))
        } else {
            Toast.makeText(this, "Invalid time", Toast.LENGTH_SHORT).show()
        }
    }

    //called when clicked on done icon
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.act_menu, menu)
        return true
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        //setting eventId
        val eventId = Calendar.getInstance().timeInMillis.toInt() % 10000

        val titleInput = editTextTitle.text.toString()
        val linkInput = editTextLink.text.toString()
        val dateInput = editTextDate.text.toString()
        val timeInput = editTextTime.text.toString()

        if (id == R.id.action_favorite && !isEditing && !toDelete) {

            //When a new task is inserted and not edited the previously existing
            if (titleInput.isNotEmpty() && linkInput.isNotEmpty() && dateInput.isNotEmpty()
                && timeInput.isNotEmpty()) {

                //when the fields are not empty
                if (linkInput.isValidUrl()) {

                    //inserting task in db when fields are not empty and provided link is valid
                    viewModel.insertTask(Task(titleInput, "", linkInput, "",
                            dateInput, timeInput, eventId))

                    //scheduling notification few minutes before the scheduled meeting
                    NotificationReceiver().scheduleNotification(this, cal.timeInMillis -
                            300000, "$titleInput - in few minutes", linkInput, eventId)

                    //scheduling meeting intent on time as provided
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(linkInput))
                    val pendingIntent = PendingIntent.getActivity(this,
                        eventId, intent, PendingIntent.FLAG_ONE_SHOT)
                    (getSystemService(ALARM_SERVICE) as AlarmManager)[AlarmManager.RTC_WAKEUP,
                            cal.timeInMillis] = pendingIntent

                    //finish and back to main activity
                    finish()

                } else {
                    //showing toast message if the link isn't valid
                    Toast.makeText(this, "Provide a valid meeting link",
                        Toast.LENGTH_SHORT).show()
                }
            } else {
                //showing toast message if the fields are incompletely filled
                Toast.makeText(this, "Insert all the details", Toast.LENGTH_SHORT).show()
            }
        }
        //When editing the previously existing task
        else if (id == R.id.action_favorite && isEditing && !toDelete) {

            //when the fields are not empty
            if (titleInput.isNotEmpty() && linkInput.isNotEmpty() && dateInput.isNotEmpty()
                && timeInput.isNotEmpty()) {

                //when fields are not empty and the link is valid
                if (linkInput.isValidUrl()) {

                    //updating the task details
                    viewModel.updateTaskByTitle(titleInput, "", linkInput, "",
                        dateInput, timeInput, previousTitle, previousLink, eventId)

                    //when date and time is updated
                    if (timeInput != previousTime || dateInput != previousDate) {

                        //Cancel previously scheduled meeting and notification
                        cancelMeetingAndNotification(previousTitle!!, previousLink!!, eventId)

                        //Schedule new notification
                        NotificationReceiver().scheduleNotification(this,
                            cal.timeInMillis - 400000, "$titleInput - in few minutes",
                            linkInput, eventId)

                        //Scheduling new intent with updated date and time
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(linkInput))
                        val pendingIntent = PendingIntent.getActivity(
                            this, eventId, intent, PendingIntent.FLAG_ONE_SHOT)
                        (getSystemService(ALARM_SERVICE) as AlarmManager)[AlarmManager.RTC_WAKEUP,
                                cal.timeInMillis] = pendingIntent

                    }
                    //Back to main activity
                    finish()
                }
            } else
                //Show toast when details are incompletely filled
                Toast.makeText(this, "Fill the details", Toast.LENGTH_SHORT).show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun String.isValidUrl(): Boolean = Patterns.WEB_URL.matcher(this).matches()

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun cancelMeetingAndNotification(title: String, link: String, eventId: Int) {
        //Cancel few minutes prior meeting alert notification
        NotificationReceiver().cancelNotification(this, title, link, eventId)

        //Cancel meeting intent when deleted task
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(previousLink))
        val pendingIntent = PendingIntent.getActivity(
            this, eventId, intent, PendingIntent.FLAG_ONE_SHOT)
        (getSystemService(ALARM_SERVICE) as AlarmManager)[AlarmManager.RTC_WAKEUP,
                cal.timeInMillis] = pendingIntent

        //Cancel intent
        pendingIntent.cancel()
    }

}


