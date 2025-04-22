package com.mainskown.blackjack.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CardButton (
    text: String,
    onClick: () -> Unit,
    modifier: Modifier,
    size: Dp = 150.dp,
    backgroundColor: Color = Color(0xFF1E1E1E), // Default background color
    textColor: Color = Color.White, // Default text color
    outline: Boolean = false, // Optional parameter for outline
    outlineWidth: Dp = 2.dp, // Default outline width
    outlineColor: Color = Color(0xFFFFFFFF), // Default outline color
){
    Card(
        modifier = modifier
            .then(
                if (outline) {
                    Modifier
                        .border(outlineWidth, outlineColor, shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                } else {
                    Modifier
                }
            )
            .clickable { onClick() },

        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        val width = size * 2f / 3f // Card should have 2:3 ratio
        Box(
            modifier = Modifier
                .padding(16.dp)
                .width(width)
                .height(size),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 20.sp,
                color = textColor,
                textAlign = TextAlign.Center
            )
        }
    }
}