package com.madhu.expenza.di

import androidx.room.Room
import com.madhu.expenza.data.database.ExpenzaDatabase
import com.madhu.expenza.data.repository.ExpenseRepository
import com.madhu.expenza.data.repository.IExpenseRepository
import com.madhu.expenza.ui.mvi.DashboardViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Database
    single {
        Room.databaseBuilder(
            androidContext(),
            ExpenzaDatabase::class.java,
            ExpenzaDatabase.DATABASE_NAME
        ).build()
    }

    // DAO
    single {
        val database = get<ExpenzaDatabase>()
        database.expenseDao()
    }

    // Repository
    single<IExpenseRepository> {
        ExpenseRepository(get())
    }

    // ViewModels
    viewModel {
        DashboardViewModel(get())
    }
}
