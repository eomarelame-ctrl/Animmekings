package com.animekings.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.animekings.app.ui.theme.BgBlack
import com.animekings.app.ui.theme.NeonPurple
import com.animekings.app.ui.theme.TextPrimary
import com.animekings.app.ui.theme.TextSecondary

/**
 * Featured hero banner shown at the top of Home — poster/backdrop with a
 * dark-to-transparent gradient and the title overlaid, matching the reference design.
 */
@Composable
fun HeroBanner(
    title: String?,
    subtitle: String?,
    bannerUrl: String?,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(20.dp))
    ) {
        if (bannerUrl != null) {
            AsyncImage(
                model = bannerUrl,
                contentDescription = title,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(listOf(NeonPurple.copy(alpha = 0.35f), BgBlack))
                    )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, BgBlack.copy(alpha = 0.95f))
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = title ?: "Anime KINGS",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            if (subtitle != null) {
                Text(text = subtitle, color = TextSecondary, fontSize = 13.sp)
            }
        }
    }
}
