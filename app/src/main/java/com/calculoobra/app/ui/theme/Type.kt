package com.calculoobra.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    titleLarge = androidx.compose.ui.text.TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    ),
    titleMedium = androidx.compose.ui.text.TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    ),
    bodyMedium = androidx.compose.ui.text.TextStyle(
        fontSize = 14.sp
    ),
    bodySmall = androidx.compose.ui.text.TextStyle(
        fontSize = 12.sp
    ),
    labelSmall = androidx.compose.ui.text.TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp
    )
)