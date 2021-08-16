package com.trishala13kohad.myapplication

import android.app.*
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.RemoteViews
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModelProvider
import java.text.SimpleDateFormat
import java.util.*


class MeetingActivity : AppCompatActivity() {
    private lateinit var viewModel: TaskViewModel
    private lateinit var dateInput: EditText
    private lateinit var timeInput: EditText
    private lateinit var title: EditText
    private lateinit var link: EditText
    private var channelID = "Your_Channel_ID"
    private var titlei :String? = null
    private var edit = false
    private lateinit var builder: NotificationCompat.Builder
    private var cal: Calendar = Calendar.getInstance()
    private var cali: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meeting)

        val intent = intent
        titlei = intent.getStringExtra("title")
        val urli = intent.getStringExtra("url")
        val datei = intent.getStringExtra("date")
        val timei = intent.getStringExtra("time")
        if (titlei != null && urli != null && datei != null) {
            edit = true
            title= findViewById(R.id.titleInput)
            title.setText(titlei)
            link= findViewById(R.id.linkInput)
            link.setText(urli)
            dateInput =  findViewById(R.id.dateInputM)
            dateInput.setText(datei)
            timeInput = findViewById(R.id.timeInputM)
            timeInput.setText(timei)
        }
            viewModel = ViewModelProvider(
                this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            ).get(TaskViewModel::class.java)
            dateInput = findViewById(R.id.dateInputM)
            timeInput = findViewById(R.id.timeInputM)
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
                    cal.get(Calendar.DAY_OF_MONTH)
                )
                bro.datePicker.minDate = cali.timeInMillis
                bro.show()
            }
            timeInput.setOnClickListener {
                TimePickerDialog(
                    this@MeetingActivity, timeSetListener, cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE), false
                ).show()
            }

        createNotificationChannel()
        val notificationLayout = RemoteViews(packageName, R.layout.activity_notification)
         builder = NotificationCompat.Builder(this, channelID)
            .setContentTitle("TItle").setSmallIcon(R.drawable.notification_icon)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)
             .setDefaults(Notification.DEFAULT_ALL)
             .setPriority(NotificationCompat.PRIORITY_HIGH)

    }
    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            val name = "App notification"
            val descriptionText = "This is test notification"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
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
        title = findViewById(R.id.titleInput)
        link = findViewById(R.id.linkInput)
        dateInput = findViewById(R.id.dateInputM)
        timeInput = findViewById(R.id.timeInputM)
            if (id == R.id.action_favorite && !edit) {
                val titleInput = title.text.toString()
                val linkInput = link.text.toString()
                val dateInpu = dateInput.text.toString()
                val timeInpu = timeInput.text.toString()
                if (titleInput.isNotEmpty() && linkInput.isNotEmpty() && dateInpu.isNotEmpty()
                    && timeInpu.isNotEmpty()
                ) {
                    viewModel.insertTask(
                        Task(
                            titleInput, "", linkInput, "",
                            dateInpu, timeInpu
                        )
                    )
                    finish()
                }
                else {
                    Toast.makeText(this, "Insert all the details", Toast.LENGTH_SHORT).show()
//                with(NotificationManagerCompat.from(this)){
//                    notify(0,builder.build())
//                }
                }
            }

                else if(id == R.id.action_favorite && edit) {
                     val titleIn = title.text.toString()
                    val linkIn = link.text.toString()
                    val dateIn = dateInput.text.toString()
                    val timeIn = timeInput.text.toString()
                    if (titleIn.isNotEmpty() && linkIn.isNotEmpty() && dateIn.isNotEmpty()
                        && timeIn.isNotEmpty()) {
                        viewModel.updateTaskByTitle(titleIn, "", linkIn, "",
                                dateIn, timeIn, titlei)
                        finish()
                    }
                else
                    Toast.makeText(this,"Fill the details", Toast.LENGTH_SHORT).show()
                return true
                }

            return super.onOptionsItemSelected(item)
        }
}


