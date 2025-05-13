package com.example.atividadefinal.Database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.travelproject.database.Trip
import com.example.travelproject.database.TripDao

@Database(entities = [User::class, Trip::class], version = 3, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun tripDao(): TripDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE users RENAME TO users")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE trip ADD COLUMN tipo TEXT not null default ''")
            }
        }



        fun getDatabase(context: Context): AppDatabase {


            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "travelapp_database"
                ).addMigrations( MIGRATION_1_2,  MIGRATION_2_3)

                    .build()
                INSTANCE = instance
                instance
            }

        }
    }
}




