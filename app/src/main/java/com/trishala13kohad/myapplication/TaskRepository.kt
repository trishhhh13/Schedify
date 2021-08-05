package com.trishala13kohad.myapplication

import androidx.lifecycle.LiveData

class TaskRepository(private val taskDao: TaskDao) {

    val allTask: LiveData<List<Task>> = taskDao.getAllTask()

    fun taskByTitle(title: String) : List<Task> {
        return taskDao.findByTitle(title)
    }
    fun taskByMessage(message: String): List<Task> {
        return taskDao.findByMessage(message)
    }

    suspend fun insert(task: Task){
        taskDao.insert(task)
    }

    suspend fun delete(task: Task){
        taskDao.delete(task)
    }
}