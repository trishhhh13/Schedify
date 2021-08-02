package com.trishala13kohad.myapplication

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TaskDao {

    @Insert
    suspend fun insert(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("Select * from task_table order by id ASC")
    fun getAllTask(): LiveData<List<Task>>
}