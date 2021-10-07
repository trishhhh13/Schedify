package com.trishala13kohad.myapplication

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

//This class contains recycler view containing scheduled meetings and messages
class MainActivity : AppCompatActivity(), TaskInterface {

    private lateinit var viewModel: TaskViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var rootView: View
    var del = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Getting views from the activity
        rootView = findViewById(R.id.rootView)
        recyclerView = (findViewById(R.id.recyclerView))
        fab = findViewById(R.id.addButton)

        //Setting up recycler view with the adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = TaskAdapter(this, this)
        recyclerView.adapter = adapter

        //Changing visibility of floating action button while scrolling recycler view
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val height = recyclerView.height
                if (dy > 0 || height > rootView.height) {
                    fab.hide()
                    return
                }
                if (dy < 0 || height <= rootView.height) {
                    fab.show()
                }
            }
        })

        //setting up ViewModel
        viewModel = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application))
            .get(TaskViewModel::class.java)

        //Only visible when recycler view is empty
        val noTaskView: TextView = findViewById(R.id.noTaskView)

        viewModel.allTask.observe(this, { list ->
            list?.let {
                adapter.updateList(it)
                if (adapter.itemCount != 0) {
                    noTaskView.visibility = View.GONE
                } else {
                    noTaskView.visibility = View.VISIBLE
                }
            }
        })
    }

    //handling clicks on the items in the recycler view
    override fun clickedListItem(task: Task, position: Int) {
        val meetingTitle = task.title
        val meetingLink = task.url
        val nameOfReceiver = task.name
        val textMessage = task.message

        val thread = Thread {
            //get task by title and link
            val checkByTitle: List<Task> = viewModel.taskByTitle(meetingTitle, meetingLink)
            //get message by message and name
            val checkByMessage: List<Task> = viewModel.taskByMessage(nameOfReceiver, textMessage)

            val taskAdapter = TaskAdapter(this, this)
            if (meetingTitle.isNotEmpty())
                //if the item selected is a scheduled meeting
                taskAdapter.openMeeting(checkByTitle)
            else
                //if the item is a scheduled message
                taskAdapter.openMessage(checkByMessage)
        }
        thread.start()
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onDeleteClicked(task: Task) {

        val meetingTitle = task.title
        val meetingLink = task.url
        val nameOfReceiver = task.name
        val textMessage = task.message

        GlobalScope.launch(Dispatchers.IO) {
            //get task by title and link
            val checkByTitle: List<Task> = viewModel.taskByTitle(meetingTitle, meetingLink)
            //get message by message and name
            val checkByMessage: List<Task> = viewModel.taskByMessage(nameOfReceiver, textMessage)

            val taskAdapter = TaskAdapter(this@MainActivity, this@MainActivity)
            if (meetingTitle.isNotEmpty())
            //if the task to be deleted is a scheduled meeting
                taskAdapter.openMeetingToDelete(checkByTitle)
            else
            //if the task to be deleted is a scheduled message
                taskAdapter.openMessageToDelete(checkByMessage)
        }


        recyclerView = findViewById(R.id.recyclerView)
        rootView = findViewById(R.id.rootView)
        fab = findViewById(R.id.addButton)

        //Changing visibility of floating action button while scrolling recycler view
        val height = recyclerView.height
        if (height > rootView.height) {
            fab.hide()
            return
        }
        if (height <= rootView.height) {
            fab.show()
        }


    }

    fun chooseTaskToSchedule(view: View) {
        //called when fab is clicked
        alertDialog()
    }

    private fun alertDialog() {

        val message = Intent(this, MessageActivity::class.java)
        val meeting = Intent(this, MeetingActivity::class.java)

        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Choose task to schedule")
        val choice = arrayOf("Message", "Meeting")

        dialog.setItems(choice) { dialog, which ->

            if (which == 0) {
                startActivity(message)
            } else {
                startActivity(meeting)
            }
        }

        val alertDialog = dialog.create()
        alertDialog.show()

    }

    fun clickedListItem(view: View) {}

}