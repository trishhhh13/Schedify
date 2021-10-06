package com.trishala13kohad.myapplication

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//Holds all the data needed for UI
class TaskViewModel(application: Application): AndroidViewModel(application) {

    val allTask: LiveData<List<Task>>


    private val repository: TaskRepository
    init {
        val dao = TaskDatabase.getDatabase(application).getTaskDao()
        repository = TaskRepository(dao)
        allTask = repository.allTask
    }

    //function to get task by title and link
    fun taskByTitle(title: String, link: String): List<Task> {
        return repository.taskByTitle(title, link)
    }

    //function to get task by name and message
    fun taskByMessage(name: String, message: String): List<Task> {
        return repository.taskByMessage(name, message)
    }
    //function called when task is deleted
    fun deleteTask(task:Task){
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(task)
        }
    }
    //function called when inserting a new task
    fun insertTask(task:Task){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(task)
        }
    }

    //function called when user update the meeting details
    fun updateTaskByTitle(title: String, name: String, url: String, message:
    String, date: String, time: String, oldTitle: String?,oldLink:String?, eventId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.taskTitleUpdate(title, name, url, message, date, time,
                oldTitle, oldLink, eventId)
        }
    }

    //function called when user update the message details
    fun updateTaskByMessage(title: String, name: String, url: String, message:
    String, date: String, time: String, oldName: String?,oldMsg: String?, eventId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.taskMessageUpdate(title, name, url, message, date,
                time, oldName, oldMsg, eventId)
        }
    }


}