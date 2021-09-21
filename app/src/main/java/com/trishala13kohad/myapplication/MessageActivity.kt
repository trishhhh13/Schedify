package com.trishala13kohad.myapplication

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates


class MessageActivity : AppCompatActivity() {
    private lateinit var viewModel: TaskViewModel
    private lateinit var dateInput: EditText
    private lateinit var timeInput: EditText
    private lateinit var nameInput: TextInputEditText
    private var namei :String? = null
    private var messagei :String? = null
    private var eventI by Delegates.notNull<Int>()
    private var edit = false
    private var check = false
    private var cal: Calendar = Calendar.getInstance()
    private var cali: Calendar = Calendar.getInstance()

    @DelicateCoroutinesApi
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // There are no request codes
                    val data: Intent? = result.data
                    val contactUri = data?.data ?: return@registerForActivityResult
                    val cols = arrayOf(
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                    )

                    val rs = contentResolver.query(contactUri, cols, null, null, null)
                    if (rs?.moveToFirst()!!) {
                        nameInput.setText(rs.getString(1))

                    }
                    rs.close()
                }
            }

        val intent = intent
        eventI= intent.getIntExtra("eventId", 0)
        namei = intent.getStringExtra("name")
        messagei = intent.getStringExtra("message")
        val datei = intent.getStringExtra("date")
        val timei = intent.getStringExtra("time")

        if (namei != null && messagei != null && datei != null && timei != null) {
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
        nameInput = findViewById(R.id.nameInputET)
        nameInput.setOnClickListener{
            if(!hasPermission()){
                requestPermission()
            }
            else {
                val i = Intent(Intent.ACTION_PICK)
                i.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
                GlobalScope.launch(Dispatchers.IO) {
                    resultLauncher.launch(i)
                }
            }
        }
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
            val bro = DatePickerDialog(this,
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH))

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
        if(cal.timeInMillis >= cali.timeInMillis) {
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            dateInput.setText(sdf.format(cal.time))
        }
        else
        {
            Toast.makeText(this, "Invalid time", Toast.LENGTH_SHORT).show()
        }
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

    private fun hasPermission(): Boolean{
        return ActivityCompat
            .checkSelfPermission(
                this, Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED

    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestPermission(){
        val permission = mutableListOf<String>()
        if(!shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)){
            Toast.makeText(this, "Contact permission is required to send message."
                ,Toast.LENGTH_SHORT).show()
        }
        if (!hasPermission()) {
            permission.add(Manifest.permission.READ_CONTACTS)
        }
        if (permission.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permission.toTypedArray(), 0)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray) {
        for(permission in permissions){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                check = true
            }
        }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.act_menu, menu)
        return true
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if(namei != null) { edit = true }
        val eventId = Calendar.getInstance().timeInMillis.toInt()%10000

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
            if(nameInput.isNotEmpty() && messageInput.isNotEmpty() && dateInput.isNotEmpty()
                && timeInput.isNotEmpty()){
                viewModel.insertTask(Task("", nameInput,"",
                    messageInput, dateInput, timeInput,eventId))
                NotificationReceiver().scheduleNotification(this, cal.timeInMillis,
                    "Message sent to $nameInput", messageInput,
                    eventId)
                finish()
            }
            else
                Toast.makeText(this, "Insert all the details", Toast.LENGTH_SHORT).show()
            return true
        }
        else if(id == R.id.action_favorite && edit){
            val name: TextInputEditText = findViewById(R.id.nameInputET)
            val nameInput = name.text.toString()
            val message: TextInputEditText = findViewById(R.id.messageInput)
            val messageInput = message.text.toString()
            val date: EditText = findViewById(R.id.dateInput)
            val dateInput = date.text.toString()
            val time: EditText = findViewById(R.id.timeInput)
            val timeInput = time.text.toString()
            if(nameInput.isNotEmpty() && messageInput.isNotEmpty() && dateInput.isNotEmpty()
                && timeInput.isNotEmpty()){
                viewModel.updateTaskByMessage("",  nameInput,"",
                    messageInput, dateInput, timeInput, namei, eventId)
                NotificationReceiver().cancelNotification(this,  namei, messagei,
                    eventI)
                NotificationReceiver()
                .scheduleNotification(this,
                    cal.timeInMillis , "Message sent to $nameInput", messageInput, eventId)
                finish()
            }
              else
                Toast.makeText(this, "Insert all the details", Toast.LENGTH_SHORT).show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}