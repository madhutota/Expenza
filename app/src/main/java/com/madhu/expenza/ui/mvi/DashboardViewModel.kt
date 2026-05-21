package com.madhu.expenza.ui.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.madhu.expenza.data.model.Expense
import com.madhu.expenza.data.model.MonthlyExpenseSummary
import com.madhu.expenza.data.repository.IExpenseRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.YearMonth

class DashboardViewModel(
    private val expenseRepository: IExpenseRepository
) : ViewModel() {

    private val _viewState = MutableStateFlow(DashboardViewState())
    val viewState = _viewState.asStateFlow()

    private val _sideEffectChannel = Channel<DashboardSideEffect>()
    val sideEffect: Flow<DashboardSideEffect> = _sideEffectChannel.receiveAsFlow()

    init {
        handleIntent(DashboardIntent.LoadExpenses)
    }

    fun handleIntent(intent: DashboardIntent) {
        when (intent) {
            is DashboardIntent.LoadExpenses -> loadExpenses()
            is DashboardIntent.LoadMonthlySummary -> loadMonthlySummary(intent)
            is DashboardIntent.AddExpense -> addExpense(intent.expense)
            is DashboardIntent.UpdateExpense -> updateExpense(intent.expense)
            is DashboardIntent.DeleteExpense -> deleteExpense(intent.id)
            is DashboardIntent.FilterByMonth -> filterByMonth(intent.month)
            is DashboardIntent.FilterByCategory -> filterByCategory(intent.category)
            is DashboardIntent.ClearFilters -> clearFilters()
        }
    }

    private fun loadExpenses() {
        viewModelScope.launch {
            _viewState.update { it.copy(isLoading = true) }
            try {
                expenseRepository.getAllExpenses().collect { expenses ->
                    _viewState.update { state ->
                        state.copy(
                            expenses = expenses,
                            filteredExpenses = expenses,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _viewState.update { it.copy(isLoading = false, error = e.message) }
                _sideEffectChannel.send(DashboardSideEffect.ShowError(e.message ?: "Unknown error"))
            }
        }
    }

    private fun loadMonthlySummary(intent: DashboardIntent.LoadMonthlySummary) {
        viewModelScope.launch {
            try {
                val now = java.time.LocalDateTime.now()
                val startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
                val endOfMonth = now.withHour(23).withMinute(59).withSecond(59)

                val startMillis = startOfMonth.toEpochSecond(java.time.ZoneOffset.UTC) * 1000
                val endMillis = endOfMonth.toEpochSecond(java.time.ZoneOffset.UTC) * 1000

                expenseRepository.getTotalExpensesInRange(startMillis, endMillis).collect { total ->
                    expenseRepository.getCategoryBreakdown(startMillis, endMillis).collect { breakdown ->
                        val summary = MonthlyExpenseSummary(
                            month = YearMonth.now().toString(),
                            totalExpense = total,
                            categoriesBreakdown = breakdown.mapKeys {
                                com.madhu.expenza.data.model.ExpenseCategory.valueOf(it.key)
                            },
                            percentageChange = 23f
                        )
                        _viewState.update { it.copy(monthlySummary = summary, totalMonthlyExpense = total, categoryBreakdown = breakdown) }
                    }
                }
            } catch (e: Exception) {
                _sideEffectChannel.send(DashboardSideEffect.ShowError(e.message ?: "Error loading summary"))
            }
        }
    }

    private fun addExpense(expense: Expense) {
        viewModelScope.launch {
            try {
                expenseRepository.addExpense(expense)
                _sideEffectChannel.send(DashboardSideEffect.ShowSuccess("Expense added successfully"))
                loadExpenses()
            } catch (e: Exception) {
                _sideEffectChannel.send(DashboardSideEffect.ShowError(e.message ?: "Failed to add expense"))
            }
        }
    }

    private fun updateExpense(expense: Expense) {
        viewModelScope.launch {
            try {
                expenseRepository.updateExpense(expense)
                _sideEffectChannel.send(DashboardSideEffect.ShowSuccess("Expense updated successfully"))
                loadExpenses()
            } catch (e: Exception) {
                _sideEffectChannel.send(DashboardSideEffect.ShowError(e.message ?: "Failed to update expense"))
            }
        }
    }

    private fun deleteExpense(id: Int) {
        viewModelScope.launch {
            try {
                expenseRepository.deleteExpense(id)
                _sideEffectChannel.send(DashboardSideEffect.ShowSuccess("Expense deleted successfully"))
                loadExpenses()
            } catch (e: Exception) {
                _sideEffectChannel.send(DashboardSideEffect.ShowError(e.message ?: "Failed to delete expense"))
            }
        }
    }

    private fun filterByMonth(month: String) {
        viewModelScope.launch {
            _viewState.update { it.copy(selectedMonth = month) }
            applyFilters()
        }
    }

    private fun filterByCategory(category: String) {
        viewModelScope.launch {
            _viewState.update { it.copy(selectedCategory = category) }
            applyFilters()
        }
    }

    private fun clearFilters() {
        _viewState.update {
            it.copy(
                selectedMonth = "",
                selectedCategory = "",
                filteredExpenses = it.expenses
            )
        }
    }

    private fun applyFilters() {
        val state = _viewState.value
        var filtered = state.expenses

        if (state.selectedCategory.isNotEmpty()) {
            filtered = filtered.filter { it.category.name == state.selectedCategory }
        }

        _viewState.update { it.copy(filteredExpenses = filtered) }
    }
}
