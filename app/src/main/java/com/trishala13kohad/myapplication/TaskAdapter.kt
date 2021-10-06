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
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(private val context: Context, private val listener: TaskInterface): RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val allTask = ArrayList<Task>()

    inner class TaskViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        //task items in recycler view
        val textView: TextView = itemView.findViewById(R.id.taskList)
        val delButton: ImageView = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {

        //creating view holder
        val viewHolder= TaskViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item,
            parent, false))

        //setting onClickListener to delete the task
        viewHolder.delButton.setOnClickListener {
            listener.onDeleteClicked(allTask[viewHolder.adapterPosition])
        }

        //setting onClickListener to open the task and view in detail
        viewHolder.textView.setOnClickListener {
            listener.clickedListItem(allTask[viewHolder.adapterPosition], viewHolder.adapterPosition)
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        //binding view holder
        //setting the inserted task name to be displayed in the list
        val currentTask = allTask[position]
        lateinit var currentTaskText: String
        if(currentTask.title != "")
            { currentTaskText = "${currentTask.title} - ${currentTask.url}" }
        else
        { currentTaskText = "${currentTask.name} - ${currentTask.message}" }
        holder.textView.text = currentTaskText
    }

    override fun getItemCount(): Int {
        //getting the length of the task
        return allTask.size

    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(taskList: List<Task>){
        allTask.clear()
        allTask.addAll(taskList)

        notifyDataSetChanged()
    }
    fun openMeeting(task: List<Task>){

        //open meeting intent with details to edit
        val openMeetingIntent = Intent(context, MeetingActivity::class.java)
        openMeetingIntent.putExtra("title", task[0].title)
        openMeetingIntent.putExtra("url", task[0].url)
        openMeetingIntent.putExtra("date", task[0].date)
        openMeetingIntent.putExtra("time", task[0].time)
        openMeetingIntent.putExtra("eventId", task[0].eventId)
        startActivity(context, openMeetingIntent, null)

        //updating changes being made
        notifyDataSetChanged()

    }
    fun openMessage(task: List<Task>){

        //open message intent with details to edit
        val openMessageIntent = Intent(context, MessageActivity::class.java)
        openMessageIntent.putExtra("name", task[0].name)
        openMessageIntent.putExtra("message", task[0].message)
        openMessageIntent.putExtra("date", task[0].date)
        openMessageIntent.putExtra("time", task[0].time)
        openMessageIntent.putExtra("eventId", task[0].eventId)
        startActivity(context, openMessageIntent, null)

        //updating changes being made
        notifyDataSetChanged()
    }

    fun openMeetingToDelete(task: List<Task>) {

        //open meeting intent with details to delete
        val openMeetingIntent = Intent(context, MeetingActivity::class.java)
        openMeetingIntent.putExtra("title", task[0].title)
        openMeetingIntent.putExtra("url", task[0].url)
        openMeetingIntent.putExtra("date", task[0].date)
        openMeetingIntent.putExtra("time", task[0].time)
        openMeetingIntent.putExtra("eventId", task[0].eventId)
        openMeetingIntent.putExtra("toDelete", true)
        startActivity(context, openMeetingIntent, null)

        //updating changes being made
        notifyDataSetChanged()
    }
    fun openMessageToDelete(task: List<Task>) {

        //open message intent with details to delete
        val openMessageIntent = Intent(context, MessageActivity::class.java)
        openMessageIntent.putExtra("name", task[0].name)
        openMessageIntent.putExtra("message", task[0].message)
        openMessageIntent.putExtra("date", task[0].date)
        openMessageIntent.putExtra("time", task[0].time)
        openMessageIntent.putExtra("eventId", task[0].eventId)
        openMessageIntent.putExtra("toDelete", true)
        startActivity(context, openMessageIntent, null)

        //updating changes being made
        notifyDataSetChanged()
    }
}

interface TaskInterface{
    //overridden in the main activity
    fun onDeleteClicked(task: Task)
    fun clickedListItem(task:Task, position: Int)
}