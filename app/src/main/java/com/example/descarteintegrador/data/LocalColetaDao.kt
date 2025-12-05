package com.example.descarteintegrador.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface LocalColetaDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(local: LocalColeta)
}