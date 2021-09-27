package com.trishala13kohad.myapplication

import androidx.lifecycle.LiveData

class TaskRepository(private val taskDao: TaskDao) {

    val allTask: LiveData<List<Task>> = taskDao.getAllTask()

    fun taskByTitle(title: String, link: String): List<Task> {
        return taskDao.findByTitle(title, link)
    }

    fun taskByMessage(name: String, message: String): List<Task> {
        return taskDao.findByMessage(name, message)
    }

    suspend fun insert(task: Task) {
        taskDao.insert(task)
    }

    suspend fun delete(task: Task) {
        taskDao.delete(task)
    }

    fun taskTitleUpdate(
        title: String,
        name: String,
        url: String,
        message: String,
        date: String,
        time: String,
        oldTitle: String?,
        oldLink: String?,
        eventId:Int
    ) {
        taskDao.taskUpdateByTitle(title, name, url, message, date, time, oldTitle, oldLink, eventId)
    }

    fun taskMessageUpdate(
        title: String,
        name: String,
        url: String,
        message: String,
        date: String,
        time: String,
        oldName: String?,
        oldMsg: String?,
        eventId: Int
    ) {
        taskDao.taskUpdateByName(title, name, url, message, date, time, oldName, oldMsg, eventId)
    }

}
