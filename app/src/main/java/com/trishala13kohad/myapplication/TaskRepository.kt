package com.trishala13kohad.myapplication

import androidx.lifecycle.LiveData

class TaskRepository(private val taskDao: TaskDao) {

    val allTask: LiveData<List<Task>> = taskDao.getAllTask()

    suspend fun insert(task: Task){
        taskDao.insert(task)
    }

    suspend fun delete(task: Task){
        taskDao.delete(task)
    }
}