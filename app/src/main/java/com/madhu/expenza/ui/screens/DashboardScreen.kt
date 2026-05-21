package com.madhu.expenza.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.madhu.expenza.ui.components.BarChart
import com.madhu.expenza.ui.components.BarData
import com.madhu.expenza.ui.components.DonutSegment
import com.madhu.expenza.ui.components.SimpleDonutChart
import com.madhu.expenza.ui.components.StatCard
import com.madhu.expenza.ui.components.Transaction
import com.madhu.expenza.ui.components.TransactionsList
import com.madhu.expenza.ui.mvi.DashboardIntent
import com.madhu.expenza.ui.mvi.DashboardSideEffect
import com.madhu.expenza.ui.mvi.DashboardViewModel
import com.madhu.expenza.ui.theme.DarkBg
import com.madhu.expenza.ui.theme.GreenAccent
import com.madhu.expenza.ui.theme.PeachAccent
import com.madhu.expenza.ui.theme.PinkAccent
import com.madhu.expenza.ui.theme.TextLight
import com.madhu.expenza.ui.theme.YellowAccent
import org.koin.androidx.compose.koinViewModel

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = koinViewModel()
) {
    val viewState = viewModel.viewState.collectAsState()
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.handleIntent(DashboardIntent.LoadMonthlySummary)
    }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                is DashboardSideEffect.ShowError -> {
                    snackbarHostState.showSnackbar(sideEffect.message)
                }
                is DashboardSideEffect.ShowSuccess -> {
                    snackbarHostState.showSnackbar(sideEffect.message)
                }
                else -> {}
            }
        }
    }
    Scaffold(
        containerColor = DarkBg,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(16.dp)
            )
        }
    ) { innerpadding ->

    if (viewState.value.isLoading) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerpadding)
                .background(DarkBg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(color = GreenAccent)
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerpadding)
                .background(DarkBg)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Text(
                    text = "Expenza",
                    fontSize = 28.sp,
                    color = TextLight,
                    fontWeight = FontWeight.Bold
                )

                // Monthly Expense Summary
                StatCard(
                    title = "Monthly Expenses",
                    value = "$${String.format("%.2f", viewState.value.totalMonthlyExpense)}",
                    subtitle = "Current Month",
                    percentageChange = "+ 23%",
                    backgroundColor = Color(0xFF2a3a32)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Bar Chart - Weekly Breakdown
                BarChart(
                    title = "Weekly Breakdown",
                    bars = listOf(
                        BarData("Mon", 15f),
                        BarData("Tue", 25f),
                        BarData("Wed", 35f),
                        BarData("Thu", 20f),
                        BarData("Fri", 40f),
                        BarData("Sat", 30f),
                        BarData("Sun", 25f)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Donut Chart - Expense Categories
                val segments = viewState.value.categoryBreakdown.map { (category, amount) ->
                    val colors = mapOf(
                        "FOOD_DINING" to GreenAccent,
                        "TRANSPORTATION" to PinkAccent,
                        "SHOPPING" to YellowAccent,
                        "ENTERTAINMENT" to PeachAccent
                    )
                    val total = viewState.value.totalMonthlyExpense
                    val percentage = if (total > 0) (amount / total) * 100 else 0.0
                    DonutSegment(
                        category,
                        percentage.toFloat(),
                        colors[category] ?: Color.Gray,
                        "$${String.format("%.2f", amount)}"
                    )
                }

                SimpleDonutChart(
                    title = "Expense Categories",
                    subtitle = "Total Spending Distribution",
                    totalAmount = "$${String.format("%.2f", viewState.value.totalMonthlyExpense)}",
                    segments = segments.ifEmpty {
                        listOf(
                            DonutSegment("Food & Dining", 35f, GreenAccent, "$0.00"),
                            DonutSegment("Transportation", 25f, PinkAccent, "$0.00"),
                            DonutSegment("Shopping", 22f, YellowAccent, "$0.00"),
                            DonutSegment("Entertainment", 18f, PeachAccent, "$0.00")
                        )
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Recent Transactions
                val transactions = viewState.value.filteredExpenses.map { expense ->
                    Transaction(
                        name = expense.name,
                        date = expense.date.toString().take(16),
                        amount = String.format("%.2f", expense.amount),
                        isPositive = false,
                        avatarColor = Color(0xFF6C5B7B)
                    )
                }

                TransactionsList(
                    title = "Recent Transactions",
                    transactions = transactions
                )

                Spacer(modifier = Modifier.height(32.dp))
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
    }
}
