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
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity(), TaskInterface {

    private lateinit var viewModel: TaskViewModel
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val rootView: View = findViewById(R.id.rootView)
        recyclerView = (findViewById(R.id.recyclerView))
        val fab = findViewById<FloatingActionButton>(R.id.addButton)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = TaskAdapter(this, this)
        recyclerView.adapter = adapter
        recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val height = recyclerView.height
                if (dy > 0|| height>rootView.height) {
                    fab.hide()
                    return
                }
                if (dy < 0 || height<=rootView.height) {
                    fab.show()
                }
            }
        })

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
        thread.start()
    }

    override fun onItemClicked(task: Task) {
        viewModel.deleteTask(task)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val height = recyclerView.height
        val rootView = findViewById<View>(R.id.rootView)
        val fab = findViewById<FloatingActionButton>(R.id.addButton)
        if (height>rootView.height) {
            fab.hide()
            return
        }
        if (height<=rootView.height) {
            fab.show()
        }
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