package com.madhu.expenza.data.repository

import com.madhu.expenza.data.database.dao.CategoryTotal
import com.madhu.expenza.data.database.dao.ExpenseDao
import com.madhu.expenza.data.database.entity.ExpenseEntity
import com.madhu.expenza.data.model.Expense
import com.madhu.expenza.data.model.ExpenseCategory
import com.madhu.expenza.data.model.TransactionSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface IExpenseRepository {
    suspend fun addExpense(expense: Expense): Long
    suspend fun updateExpense(expense: Expense)
    suspend fun deleteExpense(id: Int)
    fun getAllExpenses(): Flow<List<Expense>>
    fun getExpensesByDateRange(startDate: Long, endDate: Long): Flow<List<Expense>>
    fun getExpensesByCategory(category: ExpenseCategory): Flow<List<Expense>>
    fun getTotalExpensesInRange(startDate: Long, endDate: Long): Flow<Double>
    fun getCategoryBreakdown(startDate: Long, endDate: Long): Flow<Map<String, Double>>
}

class ExpenseRepository(
    private val expenseDao: ExpenseDao
) : IExpenseRepository {

    override suspend fun addExpense(expense: Expense): Long {
        return expenseDao.insertExpense(expense.toEntity())
    }

    override suspend fun updateExpense(expense: Expense) {
        expenseDao.updateExpense(expense.toEntity())
    }

    override suspend fun deleteExpense(id: Int) {
        expenseDao.deleteExpenseById(id)
    }

    override fun getAllExpenses(): Flow<List<Expense>> {
        return expenseDao.getAllExpenses().map { entities ->
            entities.map { it.toModel() }
        }
    }

    override fun getExpensesByDateRange(startDate: Long, endDate: Long): Flow<List<Expense>> {
        return expenseDao.getExpensesByDateRange(startDate, endDate).map { entities ->
            entities.map { it.toModel() }
        }
    }

    override fun getExpensesByCategory(category: ExpenseCategory): Flow<List<Expense>> {
        return expenseDao.getExpensesByCategory(category.name).map { entities ->
            entities.map { it.toModel() }
        }
    }

    override fun getTotalExpensesInRange(startDate: Long, endDate: Long): Flow<Double> {
        return expenseDao.getTotalExpensesInRange(startDate, endDate)
    }

    override fun getCategoryBreakdown(startDate: Long, endDate: Long): Flow<Map<String, Double>> {
        return expenseDao.getCategoryBreakdown(startDate, endDate).map { categoryTotals ->
            categoryTotals.associate { it.category to it.total }
        }
    }

    private fun Expense.toEntity(): ExpenseEntity {
        return ExpenseEntity(
            id = id.toIntOrNull() ?: 0,
            name = name,
            amount = amount,
            category = category.name,
            date = date.toMillis(),
            source = source.name,
            description = description
        )
    }

    private fun ExpenseEntity.toModel(): Expense {
        return Expense(
            id = id.toString(),
            name = name,
            amount = amount,
            category = ExpenseCategory.valueOf(category),
            date = java.time.LocalDateTime.ofEpochSecond(date / 1000, 0, java.time.ZoneOffset.UTC),
            source = TransactionSource.valueOf(source),
            description = description
        )
    }
}

private fun java.time.LocalDateTime.toMillis(): Long {
    return this.toEpochSecond(java.time.ZoneOffset.UTC) * 1000
}
