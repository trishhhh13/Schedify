package com.trishala13kohad.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(private val context: Context, private val listener: TaskInterface): RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val allTask = ArrayList<Task>()
    inner class TaskViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val textView: TextView = itemView.findViewById(R.id.textList)
        val delButton: ImageView = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val viewHolder= TaskViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item, parent, false))
        viewHolder.delButton.setOnClickListener {
            listener.onItemClicked(allTask[viewHolder.adapterPosition])
        }
        viewHolder.textView.setOnClickListener {
            listener.clickedMe(allTask[viewHolder.adapterPosition], viewHolder.adapterPosition)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentNote = allTask[position]
        if(currentNote.title != "")
            {holder.textView.text = currentNote.title}
        else
        {holder.textView.text = currentNote.name}
    }

    override fun getItemCount(): Int {
        return allTask.size
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(taskList: List<Task>){
        allTask.clear()
        allTask.addAll(taskList)

        notifyDataSetChanged()
    }
    fun openMeeting(task: List<Task>){
        val trying = Intent(context, MeetingActivity::class.java)
        trying.putExtra("title", task[0].title)
        trying.putExtra("url", task[0].url)
        trying.putExtra("date", task[0].date)
        trying.putExtra("time", task[0].time)
        startActivity(context, trying, null)

        notifyDataSetChanged()

    }
    fun openMessage(task: List<Task>){
        val trying = Intent(context, MessageActivity::class.java)
        trying.putExtra("name", task[0].name)
        trying.putExtra("message", task[0].message)
        trying.putExtra("date", task[0].date)
        trying.putExtra("time", task[0].time)
        startActivity(context, trying, null)

        notifyDataSetChanged()
    }
}

interface TaskInterface{
    fun onItemClicked(task: Task)
    fun clickedMe(task:Task, position: Int)
}