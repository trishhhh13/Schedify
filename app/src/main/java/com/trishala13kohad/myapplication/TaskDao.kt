package com.trishala13kohad.myapplication

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TaskDao {

    //Insert a new task
    @Insert
    suspend fun insert(task: Task)

    //Delete the task
    @Delete
    suspend fun delete(task: Task)

    //update task of old name and message
    @Query("Update task_table SET title=:title, name=:name, url=:url, message=:message," +
            " date=:date, time=:time, eventId=:eventId" +
            " WHERE name LIKE :oldName LIKE :oldMsg")
    fun taskUpdateByName(title: String, name: String, url: String, message: String,
                         date: String, time: String, oldName: String?, oldMsg:String?, eventId: Int)

    @Query("Select * from task_table order by id ASC")
    fun getAllTask(): LiveData<List<Task>>

    //update task of old title and link
    @Query("Update task_table SET title=:title, name=:name, url=:url, " +
            "message=:message, date=:date, time=:time, eventId=:eventId " +
            "WHERE title LIKE :oldTitle LIKE :oldLink")
    fun taskUpdateByTitle(title: String, name: String, url: String, message: String,
                          date: String, time: String, oldTitle: String?, oldLink: String?,
                          eventId: Int)

    //get task by title and link
    @Query("Select * from task_table WHERE title LIKE :title AND url LIKE :link")
    fun findByTitle(title: String, link: String): List<Task>

    //get task by message and name
    @Query("Select * from task_table WHERE name LIKE :name AND message LIKE :message")
    fun findByMessage(name: String, message: String): List<Task>

}