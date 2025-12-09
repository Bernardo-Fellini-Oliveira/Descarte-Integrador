package com.example.descarteintegrador.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalColetaDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(local: LocalColeta)

    @Update
    suspend fun update(local: LocalColeta)

    @Delete
    suspend fun delete(local: LocalColeta)

    @Query("SELECT * from locais WHERE id = :id")
    fun getLocal(id: Int): Flow<LocalColeta>

    @Query("SELECT * from locais ORDER BY nome ASC")
    fun getAllLocais(): Flow<List<LocalColeta>>

    @Query("SELECT * from locais WHERE tipo = :tipo")
    fun getLocaisByType(tipo: String): Flow<List<LocalColeta>>

    @Query("SELECT COUNT(*) FROM locais")
    fun getLocaisCount(): Flow<Int>

    @Query("DELETE FROM locais")
    suspend fun clearAll()
}