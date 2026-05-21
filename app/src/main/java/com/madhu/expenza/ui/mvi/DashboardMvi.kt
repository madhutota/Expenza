package com.madhu.expenza.ui.mvi

import com.madhu.expenza.data.model.Expense
import com.madhu.expenza.data.model.MonthlyExpenseSummary

// Intent - User actions
sealed class DashboardIntent {
    object LoadExpenses : DashboardIntent()
    object LoadMonthlySummary : DashboardIntent()
    data class AddExpense(val expense: Expense) : DashboardIntent()
    data class UpdateExpense(val expense: Expense) : DashboardIntent()
    data class DeleteExpense(val id: Int) : DashboardIntent()
    data class FilterByMonth(val month: String) : DashboardIntent()
    data class FilterByCategory(val category: String) : DashboardIntent()
    object ClearFilters : DashboardIntent()
}

// ViewState
data class DashboardViewState(
    val isLoading: Boolean = false,
    val expenses: List<Expense> = emptyList(),
    val monthlySummary: MonthlyExpenseSummary? = null,
    val filteredExpenses: List<Expense> = emptyList(),
    val error: String? = null,
    val selectedMonth: String = "",
    val selectedCategory: String = "",
    val totalMonthlyExpense: Double = 0.0,
    val categoryBreakdown: Map<String, Double> = emptyMap()
)

// Side Effect
sealed class DashboardSideEffect {
    data class ShowError(val message: String) : DashboardSideEffect()
    data class ShowSuccess(val message: String) : DashboardSideEffect()
    object NavigateBack : DashboardSideEffect()
}
