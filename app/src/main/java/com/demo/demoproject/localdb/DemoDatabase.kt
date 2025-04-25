package com.demo.demoproject.localdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.demo.demoproject.model.Post

@Database(entities = [Post::class], version = 1)
abstract class DemoDatabase : RoomDatabase() {
    abstract fun dao(): Dao

    companion object {
        @Volatile private var INSTANCE: DemoDatabase? = null

        fun getDatabase(context: Context): DemoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DemoDatabase::class.java,
                    "demo_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}