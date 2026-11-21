package com.example.tayawaste.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tayawaste.ui.theme.*

enum class ChipStatus(val label: String) {
    AVAILABLE("Available"),
    PENDING("Pending"),
    ACCEPTED("Accepted"),
    REJECTED("Rejected"),
    COMPLETED("Completed")
}

@Composable
fun StatusChip(status: ChipStatus, modifier: Modifier = Modifier) {
    val (bg, fg) = when (status) {
        ChipStatus.AVAILABLE -> StatusAvailableBg to StatusAvailableText
        ChipStatus.PENDING -> StatusPendingBg to StatusPendingText
        ChipStatus.ACCEPTED -> StatusAcceptedBg to StatusAcceptedText
        ChipStatus.REJECTED -> Color(0xFFFEE2E2) to androidx.compose.ui.graphics.Color(0xFF991B1B)
        ChipStatus.COMPLETED -> StatusCompletedBg to StatusCompletedText
    }
    Text(
        text = status.label,
        color = fg,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun StatusChipPreview() {
    TayaWasteTheme {
        Column(Modifier.padding(16.dp)) {
            ChipStatus.values().forEach {
                StatusChip(it, Modifier.padding(bottom = 8.dp))
            }
        }
    }
}