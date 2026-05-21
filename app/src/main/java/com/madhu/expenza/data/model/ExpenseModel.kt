package com.madhu.expenza.data.model

import androidx.compose.ui.graphics.Color
import java.time.LocalDateTime

data class Expense(
    val id: String = "",
    val name: String,
    val amount: Double,
    val category: ExpenseCategory,
    val date: LocalDateTime = LocalDateTime.now(),
    val source: TransactionSource = TransactionSource.MANUAL,
    val description: String = ""
)

enum class ExpenseCategory {
    FOOD_DINING, TRANSPORTATION, SHOPPING, ENTERTAINMENT, UTILITIES, HEALTH, OTHER
}

enum class TransactionSource {
    MANUAL, PHONEPE, GPAY, OTHER
}

data class MonthlyExpenseSummary(
    val month: String,
    val totalExpense: Double,
    val categoriesBreakdown: Map<ExpenseCategory, Double>,
    val percentageChange: Float
)

