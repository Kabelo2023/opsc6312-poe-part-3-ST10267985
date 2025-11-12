package com.example.smartplanner.tasks.local

import androidx.room.*

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks_local WHERE createdBy = :uid ORDER BY localId ASC")
    suspend fun getAll(uid: String): List<TaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: TaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<TaskEntity>)

    @Query("DELETE FROM tasks_local WHERE createdBy = :uid")
    suspend fun deleteAllFor(uid: String)

    @Query("DELETE FROM tasks_local WHERE remoteId = :remoteId")
    suspend fun deleteByRemote(remoteId: String)

    @Update
    suspend fun update(entity: TaskEntity)
}
