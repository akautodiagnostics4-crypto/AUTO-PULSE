package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppMode
import com.example.ui.theme.AmberDark
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.EmeraldEco

/**
 * A simple, elegant view switcher using a shared ViewModel state [currentMode]
 * to toggle between 'Rider' and 'Driver' screens without creating new activities.
 */
@Composable
fun RiderDriverViewSwitcher(
    currentMode: AppMode,
    onModeChange: (AppMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val isRider = currentMode == AppMode.PASSENGER

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = modifier
            .height(44.dp)
            .testTag("rider_driver_view_switcher")
    ) {
        Row(
            modifier = Modifier
                .padding(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rider Tab
            val riderBgColor by animateColorAsState(
                targetValue = if (isRider) AmberPrimary else Color.Transparent,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "rider_bg_anim"
            )
            val riderContentColor by animateColorAsState(
                targetValue = if (isRider) Color(0xFF1E293B) else MaterialTheme.colorScheme.onSurfaceVariant,
                label = "rider_content_anim"
            )

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(20.dp))
                    .background(riderBgColor)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        if (!isRider) onModeChange(AppMode.PASSENGER)
                    }
                    .padding(horizontal = 12.dp)
                    .testTag("view_switcher_rider_tab"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Rider View",
                        tint = riderContentColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Rider",
                        fontSize = 12.sp,
                        fontWeight = if (isRider) FontWeight.Black else FontWeight.Medium,
                        color = riderContentColor
                    )
                }
            }

            Spacer(modifier = Modifier.width(2.dp))

            // Driver Tab
            val driverBgColor by animateColorAsState(
                targetValue = if (!isRider) EmeraldEco else Color.Transparent,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "driver_bg_anim"
            )
            val driverContentColor by animateColorAsState(
                targetValue = if (!isRider) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                label = "driver_content_anim"
            )

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(20.dp))
                    .background(driverBgColor)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        if (isRider) onModeChange(AppMode.DRIVER_CONSOLE)
                    }
                    .padding(horizontal = 12.dp)
                    .testTag("view_switcher_driver_tab"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🛺",
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Driver",
                        fontSize = 12.sp,
                        fontWeight = if (!isRider) FontWeight.Black else FontWeight.Medium,
                        color = driverContentColor
                    )
                }
            }
        }
    }
}
