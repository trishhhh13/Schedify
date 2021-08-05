package com.trishala13kohad.myapplication

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskViewModel(application: Application): AndroidViewModel(application) {

    val allTask: LiveData<List<Task>>


    private val repository: TaskRepository
    init {
        val dao = TaskDatabase.getDatabase(application).getTaskDao()
        repository = TaskRepository(dao)
        allTask = repository.allTask
    }
    fun taskByTitle(title: String): List<Task> {
        return repository.taskByTitle(title)
    }
    fun taskByMessage(message: String): List<Task> {
        return repository.taskByMessage(message)
    }
    fun deleteTask(task:Task){
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(task)
        }
    }
    fun insertTask(task:Task){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(task)
        }
    }
}