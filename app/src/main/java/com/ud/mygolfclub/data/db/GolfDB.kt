package com.ud.mygolfclub.data.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase


object GolfDB {
    @Volatile
    private var INSTANCE: AppDB? = null

    val seedCallback = object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            // Only Call Once Time for DB creating
            db.execSQL("""
                    INSERT INTO clients (name, idClient, phoneClient, pwdClient) VALUES
                    ('Carlos Rodríguez', '1234', '3001234567', '1234'),
                    ('Ana Martínez',     '12345', '3109876543',    '1234'),
                    ('Luis Pérez',       '123456', '3205551234',   '1234')
                """)
        }
    }

    fun getDB(context: Context): AppDB{
        val temp = INSTANCE
        // Verify the instance db is executed once at time
        if (temp != null){
            return temp
        }
        synchronized(this){
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDB::class.java,
                "golf_db"
            ).addCallback(this.seedCallback)
                .fallbackToDestructiveMigration(false)
                .build()
            INSTANCE = instance
            return instance
        }
    }
}