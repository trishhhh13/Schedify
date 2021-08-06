package com.trishala13kohad.myapplication

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TaskDao {

    @Insert
    suspend fun insert(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(task: Task)

    @Query("Select * from task_table order by id ASC")
    fun getAllTask(): LiveData<List<Task>>

    @Query("Select * from task_table WHERE title LIKE :title")
    fun findByTitle(title: String): List<Task>

    @Query("Select * from task_table WHERE message LIKe :message")
    fun findByMessage(message: String): List<Task>

}