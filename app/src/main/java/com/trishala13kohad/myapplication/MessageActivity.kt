package com.trishala13kohad.myapplication

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils.SimpleStringSplitter
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.vdurmont.emoji.EmojiParser
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

//Message activity to take input details from the user
class MessageActivity : AppCompatActivity() {

    private lateinit var viewModel: TaskViewModel
    private lateinit var editTextDate: EditText
    private lateinit var editTextTime: EditText
    private lateinit var editTextName: TextInputEditText
    private lateinit var editTextMessage: TextInputEditText

    private var previousName :String? = null
    private var previousMessage:String? = null
    private var previousTime :String? = null
    private var previousDate :String? = null
    private var previousEventId by Delegates.notNull<Int>()

    private var isEditing = false
    private var toDelete = false

    private var cal: Calendar = Calendar.getInstance()
    private var cali: Calendar = Calendar.getInstance()

    private lateinit var preferenceManager :PreferenceManager

    @DelicateCoroutinesApi
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

//        val resultLauncher =
//            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//                //register for result
//                if (result.resultCode == Activity.RESULT_OK) {
//                    // getting data from the contact intent
//                    val data: Intent? = result.data
//
//                    val contactUri = data?.data ?: return@registerForActivityResult
//                    val cols = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER,
//                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
//
//                    val rs = contentResolver.query(contactUri, cols, null,
//                        null, null)
//                    if (rs?.moveToFirst()!!) {
//                        editTextName.setText(rs.getString(1))
//                    }
//                    rs.close()
//                }
//            }

        //getting values if editing previous task
        previousEventId= intent.getIntExtra("eventId", 0)
        previousName = intent.getStringExtra("name")
        previousMessage = intent.getStringExtra("message")
        previousDate = intent.getStringExtra("date")
        previousTime = intent.getStringExtra("time")
        toDelete = intent.getBooleanExtra("toDelete", false)


        preferenceManager = PreferenceManager(applicationContext)

        //getting all input field edittext
        editTextTime= findViewById(R.id.timeInput)
        editTextDate = findViewById(R.id.dateInput)
        editTextName = findViewById(R.id.nameInputET)
        editTextMessage = findViewById(R.id.messageInput)

        if (previousName != null) {
            if(!toDelete) {isEditing = true}
            //when editing a previous task
            editTextTime.setText(previousTime)
            editTextName.setText(previousName)
            editTextDate.setText(previousDate)
            editTextMessage.setText(previousMessage)
        }
        if(toDelete){
            deleteAlertDialog()
        }

        viewModel = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)).
        get(TaskViewModel::class.java)

//        editTextName.setOnClickListener{
////            if(!hasPermission()){
////                requestPermission()
////            }
////            else {
////                val i = Intent(Intent.ACTION_PICK)
////                i.setPackage("com.whatsapp")
////                GlobalScope.launch(Dispatchers.IO) {
////                    resultLauncher.launch(i)
////                }
////            }
//        }

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
            val datesPicker = DatePickerDialog(this, dateSetListener,
                // set DatePickerDialog to point to today's date when it loads upQ1
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH))

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
            TimePickerDialog(this,timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE), false).show()
        }
    }

    @DelicateCoroutinesApi
    private fun deleteAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to Delete?")
            .setCancelable(false)
            .setPositiveButton("DELETE") { dialog, id ->
                // Delete selected note from database
                cancelMessage(previousName!!, previousMessage!!, previousEventId)
                dialog.dismiss()
                GlobalScope.launch {
                    val task: List<Task> = viewModel.taskByMessage(previousName!!, previousMessage!!)
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

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun cancelMessage(previousName: String, previousMessage: String, previousEventId: Int) {
        preferenceManager.setISON(false)
        //intent to open whatsapp intent
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        share.setPackage("com.whatsapp")
        share.putExtra(Intent.EXTRA_TEXT, previousMessage)

        //explicit intent to start service
        val serviceIntent =Intent(this, WAccessibility::class.java)
        serviceIntent.putExtra("UserID", previousName)
        serviceIntent.putExtra("SendMessage", previousMessage)
        serviceIntent.putExtra("eventId", previousEventId)

        //initialising pending intent for accessibility service
        val pendingIntentService = PendingIntent.getService(this,
            previousEventId, serviceIntent, PendingIntent.FLAG_ONE_SHOT)
        (getSystemService(ALARM_SERVICE) as AlarmManager)[AlarmManager.RTC_WAKEUP,
                cal.timeInMillis-10] = pendingIntentService

        pendingIntentService.cancel()

        //initialising pending intent to open whatsapp
        val pendingIntent = PendingIntent.getActivity(
            this, previousEventId, share, PendingIntent.FLAG_ONE_SHOT)
        (getSystemService(ALARM_SERVICE) as AlarmManager)[AlarmManager.RTC_WAKEUP,
                cal.timeInMillis] = pendingIntent

        pendingIntent.cancel()
    }

    private fun updateDateInView() {
        //setting date in the edittext
        if(cal.timeInMillis >= cali.timeInMillis) {
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            editTextDate.setText(sdf.format(cal.time))
        } else {
            Toast.makeText(this, "Invalid time", Toast.LENGTH_SHORT).show()
        }
    }
    private fun updateTimeInView() {
        //setting time in the edittext
        if(cal.timeInMillis >= cali.timeInMillis) {
            val myFormat = "hh:mm aa"
            val stf = SimpleDateFormat(myFormat, Locale.US)
            editTextTime.setText((stf.format(cal.time)))
        } else {
            Toast.makeText(this, "Invalid time", Toast.LENGTH_SHORT).show()
        }
    }

//    private fun hasPermission(): Boolean{
//        //getting permission to read contact
//        return ActivityCompat.checkSelfPermission(
//                this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
//    }

//    @RequiresApi(Build.VERSION_CODES.M)
//    private fun requestPermission(){
//        //requesting permission
//        val permission = mutableListOf<String>()
//        if(!shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)){
//            Toast.makeText(this, "Contact permission is required to send message."
//                ,Toast.LENGTH_SHORT).show()
//        }
//        if (!hasPermission()) {
//            permission.add(Manifest.permission.READ_CONTACTS)
//        }
//        if (permission.isNotEmpty()) {
//            ActivityCompat.requestPermissions(this, permission.toTypedArray(), 0)
//        }
//    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.act_menu, menu)
        return true
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        //setting eventId
        val eventId = Calendar.getInstance().timeInMillis.toInt()%10000

        var nameInput = editTextName.text.toString()
        val dateInput = editTextDate.text.toString()
        val timeInput = editTextTime.text.toString()
        val messageInput = editTextMessage.text.toString()


        if (id == R.id.action_favorite && !isEditing && !toDelete) {
            //When a new task is inserted and not edited the previously existing
            if(nameInput.isNotEmpty() && messageInput.isNotEmpty() && dateInput.isNotEmpty()
                && timeInput.isNotEmpty()){

                    nameInput = EmojiParser.removeAllEmojis(nameInput)

                //inserting task in db when fields are not empty and provided link is valid
                viewModel.insertTask(Task("", nameInput,"",
                    messageInput, dateInput, timeInput,eventId))

                if(!isAccessibilityOn(applicationContext)){
                    //Checking accessibility for automation
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)

                    Thread.sleep(5000)

                }
                //call function to schedule message
                startWhatsapp(nameInput, messageInput, eventId)
                //finish and back to main activity
                finish()
            }
            else
            //showing toast message if the fields are incompletely filled
                Toast.makeText(this, "Insert all the details", Toast.LENGTH_SHORT).show()
            return true
        }
        //When editing the previously existing task
        else if(id == R.id.action_favorite && isEditing && !toDelete){

            //when the fields are not empty
            if(nameInput.isNotEmpty() && messageInput.isNotEmpty() && dateInput.isNotEmpty()
                && timeInput.isNotEmpty()){
                nameInput = EmojiParser.removeAllEmojis(nameInput)
                //updating the task details
                viewModel.updateTaskByMessage("",  nameInput,"",
                    messageInput, dateInput, timeInput, previousName, previousMessage, eventId)

                if(!isAccessibilityOn(applicationContext)){
                    //if the accessibility is on for automation
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)

                    Thread.sleep(5000)

                }
                if (timeInput != previousTime && dateInput != previousDate) {
                    cancelMessage(previousName!!, previousMessage!!, previousEventId)
                    //call function to schedule message
                    startWhatsapp(nameInput, messageInput, eventId)
                }
                //finish and back to main activity
                finish()
            }
              else
            //showing toast message if the fields are incompletely filled
                Toast.makeText(this, "Insert all the details", Toast.LENGTH_SHORT).show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun startWhatsapp(name: String, message: String, eventId: Int) {

        //setting preference manager isOn true
        preferenceManager.setISON(true)

        //intent to open whatsapp intent
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        share.setPackage("com.whatsapp")
        share.putExtra(Intent.EXTRA_TEXT, message)

        //explicit intent to start service
        val serviceIntent =Intent(this, WAccessibility::class.java)
        serviceIntent.putExtra("UserID", name)
        serviceIntent.putExtra("SendMessage", message)
        serviceIntent.putExtra("eventId", eventId)

        //initialising pending intent for accessibility service
        val pendingIntentService = PendingIntent.getService(this,
            eventId, serviceIntent, PendingIntent.FLAG_ONE_SHOT)
        (getSystemService(ALARM_SERVICE) as AlarmManager)[AlarmManager.RTC_WAKEUP,
                cal.timeInMillis-10] = pendingIntentService

        //initialising pending intent to open whatsapp
        val pendingIntent = PendingIntent.getActivity(
            this, eventId, share, PendingIntent.FLAG_ONE_SHOT)
        (getSystemService(ALARM_SERVICE) as AlarmManager)[AlarmManager.RTC_WAKEUP,
                cal.timeInMillis] = pendingIntent

    }

    //function to check if the accessibility service is on for the app
    private fun isAccessibilityOn(context: Context): Boolean {

        var accessibilityEnabled = 0

        val service = context.packageName+"/"+WAccessibility::class.java.canonicalName

        try {
            accessibilityEnabled = Settings.Secure.getInt(context.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED)
        }

        catch(e: Exception){
            e.printStackTrace()
        }

        val colonSplitter = SimpleStringSplitter(':')

        if(accessibilityEnabled == 1){

            val settingValue = Settings.Secure.getString(context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)

            if (settingValue != null) {

                colonSplitter.setString(settingValue)

                while (colonSplitter.hasNext()) {

                    val accessibilityService = colonSplitter.next()

                    if (accessibilityService.equals(service, ignoreCase = true)) {
                        return true
                    }

                }

            }

        }

        return false

    }

}