package com.example.descarteintegrador.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [LocalColeta::class], version = 1, exportSchema = false)
abstract class LocalColetaDatabase : RoomDatabase() {

    abstract fun localColetaDao(): LocalColetaDao

    companion object {
        @Volatile
        private var Instance: LocalColetaDatabase? = null

        fun getDatabase(context: Context): LocalColetaDatabase {
            // if the Instance is not null, then return it,
            // otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, LocalColetaDatabase::class.java, "local_coleta_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}