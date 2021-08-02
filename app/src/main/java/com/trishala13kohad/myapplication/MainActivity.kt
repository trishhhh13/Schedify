package com.trishala13kohad.myapplication
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity(), TaskInterface {

    private lateinit var viewModel: TaskViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = (findViewById(R.id.recyclerView))
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = TaskAdapter(this, this)
        recyclerView.adapter = adapter

        viewModel = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(TaskViewModel::class.java)

        viewModel.allTask.observe(this, {list ->
            list?.let {
                adapter.updateList(it)
            }

        })
    }

    override fun onItemClicked(task: Task) {

        viewModel.deleteTask(task)
        Toast.makeText(this, "Deleted the note", Toast.LENGTH_SHORT).show()
    }

    fun chooseType(view: View) {
         alertDialog()
    }
    private fun alertDialog() {
        val dialog = AlertDialog.Builder(this)
        dialog.setMessage("Choose the type of task:-")
        dialog.setTitle("Task")
        dialog.setItems(arrayOf("Message", "Meeting")) { dialog, which ->
            if (which == 0) {
                val message: Intent = Intent(this, MessageActivity::class.java)
                startActivity(message)
            } else {
                val meeting: Intent = Intent(this, MeetingActivity::class.java)
                startActivity(meeting)
            }
        }
        val alertDialog = dialog.create()
        alertDialog.show()
    }

}