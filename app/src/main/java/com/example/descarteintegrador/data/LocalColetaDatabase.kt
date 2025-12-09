package com.example.descarteintegrador.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [LocalColeta::class], version = 2, exportSchema = false)
abstract class LocalColetaDatabase : RoomDatabase() {

    abstract fun localColetaDao(): LocalColetaDao

    companion object {
        @Volatile
        private var Instance: LocalColetaDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE locais ADD COLUMN telefone TEXT")
                database.execSQL("ALTER TABLE locais ADD COLUMN email TEXT")
            }
        }

        fun getDatabase(context: Context): LocalColetaDatabase {
            // if the Instance is not null, then return it,
            // otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, LocalColetaDatabase::class.java, "local_coleta_database")
                    .addMigrations(MIGRATION_1_2) // Add this line
                    .fallbackToDestructiveMigration() // Keep for now, but migrations are preferred
                    .build()
                    .also { Instance = it }
            }
        }
    }
}