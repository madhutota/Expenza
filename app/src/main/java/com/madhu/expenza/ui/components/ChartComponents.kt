package com.madhu.expenza.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.madhu.expenza.ui.theme.CardDark
import com.madhu.expenza.ui.theme.GreenAccent
import com.madhu.expenza.ui.theme.PeachAccent
import com.madhu.expenza.ui.theme.PinkAccent
import com.madhu.expenza.ui.theme.TextLight
import com.madhu.expenza.ui.theme.TextSecondary
import com.madhu.expenza.ui.theme.YellowAccent
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ChartLegendItem(
    label: String,
    color: Color,
    percentage: String,
    amount: String = ""
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(color, shape = CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = label,
                    fontSize = 11.sp,
                    color = TextLight,
                    fontWeight = FontWeight.Medium
                )
                if (amount.isNotEmpty()) {
                    Text(
                        text = amount,
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }
            }
        }
        Text(
            text = percentage,
            fontSize = 12.sp,
            color = TextLight,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SimpleDonutChart(
    modifier: Modifier = Modifier,
    segments: List<DonutSegment> = listOf(
        DonutSegment("Giveaway", 60f, GreenAccent, "$56,685"),
        DonutSegment("Affiliate", 21f, PinkAccent, "$19,839"),
        DonutSegment("Offline Sales", 19f, YellowAccent, "$17,950")
    ),
    title: String = "Monthly Profits",
    subtitle: String = "Total Profit Growth of 26%",
    totalAmount: String = "$94,475"
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = CardDark,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                color = TextLight,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Simple visual representation
        Box(
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.CenterHorizontally)
                .background(
                    color = Color(0xFF2a2a2a),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = totalAmount,
                    fontSize = 24.sp,
                    color = TextLight,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Total",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Legend
        segments.forEach { segment ->
            ChartLegendItem(
                label = segment.label,
                color = segment.color,
                percentage = "${segment.percentage.toInt()}%",
                amount = segment.amount
            )
        }
    }
}

data class DonutSegment(
    val label: String,
    val percentage: Float,
    val color: Color,
    val amount: String = ""
)

@Composable
fun BarChart(
    modifier: Modifier = Modifier,
    title: String = "Weekly Statistics",
    bars: List<BarData> = listOf(
        BarData("Mon", 15f),
        BarData("Tue", 25f),
        BarData("Wed", 35f),
        BarData("Thu", 20f),
        BarData("Fri", 40f),
        BarData("Sat", 30f),
        BarData("Sun", 25f)
    )
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = CardDark,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(20.dp)
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            color = TextLight,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            val maxValue = bars.maxOf { it.value }
            bars.forEach { bar ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .width(30.dp)
                            .height((bar.value / maxValue * 80).dp)
                            .background(
                                color = GreenAccent,
                                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                            )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = bar.label,
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

data class BarData(
    val label: String,
    val value: Float
)
