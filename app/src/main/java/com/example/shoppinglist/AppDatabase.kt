package com.example.shoppinglist

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//assign a database with the entity of the product class
@Database(entities = [Product::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao

    companion object {
        //give a name to the database
        private const val DATABASE_NAME = "SHOPPING_DATABASE"

        @Volatile
        private var databaseInstance: AppDatabase? = null
        //build the database
        fun getDatabase(context: Context): AppDatabase? {
            if (databaseInstance == null) {
                synchronized(AppDatabase::class.java) {
                    if (databaseInstance == null) {
                        databaseInstance =
                            Room.databaseBuilder(context.applicationContext,
                                AppDatabase::class.java,
                                DATABASE_NAME
                            ).build()
                    }
                }
            }
            return databaseInstance
        }
    }

}
