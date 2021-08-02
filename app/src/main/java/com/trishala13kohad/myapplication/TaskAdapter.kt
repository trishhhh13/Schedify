package com.trishala13kohad.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(private val context: Context, private val listener: TaskInterface): RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val allTask = ArrayList<Task>()
    inner class TaskViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val textView: TextView = itemView.findViewById(R.id.text)
        val delButton: ImageView = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val viewHolder= TaskViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item, parent, false))
        viewHolder.delButton.setOnClickListener {
            listener.onItemClicked(allTask[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentNote = allTask[position]
        holder.textView.text = currentNote.title
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
}
interface TaskInterface{
    fun onItemClicked(task: Task)
}