package com.trishala13kohad.myapplication

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TaskDao {

    @Insert
    suspend fun insert(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("Update task_table SET title=:title, name=:name, url=:url, message=:message," +
            " date=:date, time=:time, eventId=:eventId WHERE name LIKE :oldName")
    fun taskUpdateByName(title: String, name: String, url: String, message: String,
                         date: String, time: String, oldName: String?, eventId: Int)

    @Query("Select * from task_table order by id ASC")
    fun getAllTask(): LiveData<List<Task>>

    @Query("Update task_table SET title=:title, name=:name, url=:url, " +
            "message=:message, date=:date, time=:time, eventId=:eventId WHERE title LIKE :oldTitle")
    fun taskUpdateByTitle(title: String, name: String, url: String, message: String,
                          date: String, time: String, oldTitle: String?, eventId: Int)

    @Query("Select * from task_table WHERE title LIKE :title")
    fun findByTitle(title: String): List<Task>

    @Query("Select * from task_table WHERE message LIKe :message")
    fun findByMessage(message: String): List<Task>

}