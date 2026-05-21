package com.madhu.expenza.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.madhu.expenza.data.database.dao.ExpenseDao
import com.madhu.expenza.data.database.entity.ExpenseEntity

@Database(
    entities = [ExpenseEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ExpenzaDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao

    companion object {
        const val DATABASE_NAME = "expenza.db"
    }
}
