package com.trishala13kohad.myapplication
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


class MainActivity : AppCompatActivity(), TaskInterface {

    private lateinit var viewModel: TaskViewModel
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = (findViewById(R.id.recyclerView))
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = TaskAdapter(this, this)
        recyclerView.adapter = adapter

        viewModel = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)).
        get(TaskViewModel::class.java)
        val textView: TextView = findViewById(R.id.input)
        viewModel.allTask.observe(this, {list ->
            list?.let {
                adapter.updateList(it)
                if(adapter.itemCount != 0){
                    textView.visibility = View.GONE
                }
                else{
                    textView.visibility = View.VISIBLE
                }
            }
        })
    }

    override fun clickedMe(task: Task, position: Int) {
        val text= task.title
        val textM = task.message
        val thread = Thread {
                    val checkByTitle: List<Task> = viewModel.taskByTitle(text)
                    val checkByMessage: List<Task> = viewModel.taskByMessage(textM)
                    val taskAdapter = TaskAdapter(this, this)
            if(text.isNotEmpty())
                    taskAdapter.openMeeting(checkByTitle)
            else
                taskAdapter.openMessage(checkByMessage)

        }
//        taskAdapter.openMessage(checkByMessage)
        thread.start()
//        val openMeeting = Intent(this, MeetingActivity::class.java)
    }

    override fun onItemClicked(task: Task) {
        viewModel.deleteTask(task)
        Toast.makeText(this, "Deleted the task", Toast.LENGTH_SHORT).show()
    }

    fun chooseType(view: View) {
         alertDialog()
    }
    private fun alertDialog() {
        val message = Intent(this, MessageActivity::class.java)
        val meeting = Intent(this, MeetingActivity::class.java)
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Task")
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

}