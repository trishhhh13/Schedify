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

    fun taskTitleUpdate(title: String, name: String, url: String, message: String, date: String, time: String, oldTitle: String?){
        taskDao.taskUpdateByTitle(title, name,url , message, date, time, oldTitle)
    }
    fun taskMessageUpdate(title: String, name: String, url: String, message: String, date: String, time: String, oldName: String?){
        taskDao.taskUpdateByTitle(title, name,url , message, date, time, oldName)
    }

}