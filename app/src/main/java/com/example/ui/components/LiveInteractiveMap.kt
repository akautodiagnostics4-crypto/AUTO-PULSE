package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.DriverProfile
import com.example.model.MapLocation
import com.example.model.RideBooking
import com.example.model.RideStatus
import com.example.model.RideType
import com.example.ui.theme.AmberDark
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.EmeraldEco
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LiveInteractiveMap(
    booking: RideBooking?,
    nearbyDrivers: List<DriverProfile>,
    userLocation: MapLocation,
    onLocationSelected: (MapLocation) -> Unit,
    modifier: Modifier = Modifier
) {
    // Zoom and pan state
    var scale by remember { mutableFloatStateOf(1.0f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    // Pulsing animation for pickup marker and search radar
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseFraction by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseRadius"
    )

    val routeDashPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 40f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dashPhase"
    )

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("interactive_map_canvas")
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures { tapOffset ->
                        // Reverse project tap coordinate into map space
                        val mapX = (tapOffset.x - offsetX) / scale
                        val mapY = (tapOffset.y - offsetY) / scale
                        val tappedLoc = MapLocation(
                            id = "custom_${System.currentTimeMillis()}",
                            name = "Selected Pin Location",
                            address = "Lat/Lng (${(mapX / 10).toInt()}, ${(mapY / 10).toInt()})",
                            x = mapX.coerceIn(50f, 950f),
                            y = mapY.coerceIn(50f, 950f),
                            category = "Custom"
                        )
                        onLocationSelected(tappedLoc)
                    }
                }
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Center base coordinates
            val mapBaseWidth = 1000f
            val mapBaseHeight = 1000f

            // Transformation helper
            fun toScreenX(mapX: Float) = (mapX * (canvasWidth / mapBaseWidth)) * scale + offsetX
            fun toScreenY(mapY: Float) = (mapY * (canvasHeight / mapBaseHeight)) * scale + offsetY

            // 1. Draw Map Background & City Blocks
            drawRect(
                color = Color(0xFF131B26), // Dark slate asphalt ground
                size = size
            )

            // City Zone Blocks
            drawCityBlocks(
                toScreenX = ::toScreenX,
                toScreenY = ::toScreenY,
                scale = scale,
                canvasWidth = canvasWidth,
                canvasHeight = canvasHeight
            )

            // 2. Draw Street Grid & Major Arterials
            drawStreetNetwork(
                toScreenX = ::toScreenX,
                toScreenY = ::toScreenY,
                scale = scale
            )

            // 3. Draw Waterway / River & Green Park
            drawNaturalFeatures(
                toScreenX = ::toScreenX,
                toScreenY = ::toScreenY,
                scale = scale
            )

            // 4. Draw Route Polyline if Active Booking
            if (booking != null && booking.status != RideStatus.IDLE) {
                drawActiveRoute(
                    booking = booking,
                    dashPhase = routeDashPhase,
                    toScreenX = ::toScreenX,
                    toScreenY = ::toScreenY,
                    scale = scale
                )
            }

            // 5. Draw Nearby Vehicles (ambient drivers)
            nearbyDrivers.forEach { driver ->
                val sx = toScreenX(driver.x)
                val sy = toScreenY(driver.y)
                drawVehicleIcon(
                    screenX = sx,
                    screenY = sy,
                    heading = driver.heading,
                    type = driver.vehicleType,
                    isAssigned = booking?.driver?.id == driver.id,
                    scale = scale
                )
            }

            // 6. Draw Live Tracking Driver Vehicle if assigned
            if (booking?.driver != null && (booking.status == RideStatus.DRIVER_ARRIVING || booking.status == RideStatus.IN_TRANSIT)) {
                // Calculate interpolated position
                val startX: Float
                val startY: Float
                val endX: Float
                val endY: Float

                if (booking.status == RideStatus.DRIVER_ARRIVING) {
                    // Moving from driver initial spot to pickup
                    startX = booking.driver.x
                    startY = booking.driver.y
                    endX = booking.pickup.x
                    endY = booking.pickup.y
                } else {
                    // Moving from pickup to dropoff
                    startX = booking.pickup.x
                    startY = booking.pickup.y
                    endX = booking.dropoff.x
                    endY = booking.dropoff.y
                }

                val currentMapX = startX + (endX - startX) * booking.progress
                val currentMapY = startY + (endY - startY) * booking.progress

                val liveSx = toScreenX(currentMapX)
                val liveSy = toScreenY(currentMapY)

                val angle = Math.toDegrees(
                    atan2((endY - startY).toDouble(), (endX - startX).toDouble())
                ).toFloat()

                // Driver pulse halo
                drawCircle(
                    color = AmberPrimary.copy(alpha = 0.25f * (1f - pulseFraction)),
                    radius = (28f + pulseFraction * 35f) * scale,
                    center = Offset(liveSx, liveSy)
                )

                drawVehicleIcon(
                    screenX = liveSx,
                    screenY = liveSy,
                    heading = angle,
                    type = booking.rideType,
                    isAssigned = true,
                    scale = scale
                )
            }

            // 7. Draw Pickup Pin & Pulse
            if (booking != null) {
                val px = toScreenX(booking.pickup.x)
                val py = toScreenY(booking.pickup.y)

                // Expanding pulse wave
                drawCircle(
                    color = EmeraldEco.copy(alpha = 0.35f * (1f - pulseFraction)),
                    radius = (16f + pulseFraction * 32f) * scale,
                    center = Offset(px, py)
                )
                // Outer ring
                drawCircle(
                    color = EmeraldEco,
                    radius = 9f * scale,
                    center = Offset(px, py)
                )
                // Inner center dot
                drawCircle(
                    color = Color.White,
                    radius = 4f * scale,
                    center = Offset(px, py)
                )

                // 8. Draw Drop-off Pin
                val dx = toScreenX(booking.dropoff.x)
                val dy = toScreenY(booking.dropoff.y)

                drawCircle(
                    color = Color(0xFFEF4444),
                    radius = 10f * scale,
                    center = Offset(dx, dy)
                )
                drawCircle(
                    color = Color.White,
                    radius = 4.5f * scale,
                    center = Offset(dx, dy)
                )
            } else {
                // User Current Location marker
                val ux = toScreenX(userLocation.x)
                val uy = toScreenY(userLocation.y)

                drawCircle(
                    color = AmberPrimary.copy(alpha = 0.3f * (1f - pulseFraction)),
                    radius = (18f + pulseFraction * 36f) * scale,
                    center = Offset(ux, uy)
                )
                drawCircle(
                    color = AmberPrimary,
                    radius = 8f * scale,
                    center = Offset(ux, uy)
                )
                drawCircle(
                    color = Color.White,
                    radius = 3.5f * scale,
                    center = Offset(ux, uy)
                )
            }
        }

        // Map Control Floating Actions (Zoom In, Zoom Out, Recenter)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SmallFloatingActionButton(
                onClick = { scale = (scale + 0.25f).coerceAtMost(2.5f) },
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .testTag("map_zoom_in_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Zoom In")
            }

            SmallFloatingActionButton(
                onClick = { scale = (scale - 0.25f).coerceAtLeast(0.6f) },
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .testTag("map_zoom_out_btn")
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Zoom Out")
            }

            SmallFloatingActionButton(
                onClick = {
                    scale = 1.0f
                    offsetX = 0f
                    offsetY = 0f
                },
                containerColor = AmberPrimary,
                contentColor = Color(0xFF1E293B),
                modifier = Modifier.testTag("map_recenter_btn")
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "Recenter Map")
            }
        }
    }
}

private fun DrawScope.drawCityBlocks(
    toScreenX: (Float) -> Float,
    toScreenY: (Float) -> Float,
    scale: Float,
    canvasWidth: Float,
    canvasHeight: Float
) {
    // City Block definitions in map coordinates (0..1000)
    val blocks = listOf(
        // x, y, width, height, color
        listOf(60f, 60f, 220f, 180f, Color(0xFF1A2433)),
        listOf(340f, 60f, 280f, 180f, Color(0xFF172230)),
        listOf(680f, 60f, 260f, 180f, Color(0xFF1B2636)),
        listOf(60f, 300f, 220f, 160f, Color(0xFF16202D)),
        listOf(340f, 300f, 280f, 160f, Color(0xFF1E2C3D)),
        listOf(680f, 300f, 260f, 160f, Color(0xFF1A2433)),
        listOf(60f, 520f, 220f, 180f, Color(0xFF182332)),
        listOf(340f, 520f, 280f, 180f, Color(0xFF16202C)),
        listOf(680f, 520f, 260f, 180f, Color(0xFF1C2838)),
        listOf(60f, 760f, 220f, 180f, Color(0xFF192534)),
        listOf(340f, 760f, 280f, 180f, Color(0xFF17212E)),
        listOf(680f, 760f, 260f, 180f, Color(0xFF1A2635))
    )

    blocks.forEach { b ->
        val bx = toScreenX(b[0] as Float)
        val by = toScreenY(b[1] as Float)
        val bw = (b[2] as Float) * scale * (canvasWidth / 1000f)
        val bh = (b[3] as Float) * scale * (canvasHeight / 1000f)
        drawRoundRect(
            color = b[4] as Color,
            topLeft = Offset(bx, by),
            size = Size(bw, bh),
            cornerRadius = CornerRadius(12f * scale, 12f * scale)
        )
    }
}

private fun DrawScope.drawNaturalFeatures(
    toScreenX: (Float) -> Float,
    toScreenY: (Float) -> Float,
    scale: Float
) {
    // City Park block
    val parkX = toScreenX(360f)
    val parkY = toScreenY(320f)
    val parkW = 240f * scale * (size.width / 1000f)
    val parkH = 120f * scale * (size.height / 1000f)
    drawRoundRect(
        color = Color(0xFF11382A), // Soft dark forest green
        topLeft = Offset(parkX, parkY),
        size = Size(parkW, parkH),
        cornerRadius = CornerRadius(14f * scale, 14f * scale)
    )

    // Lake / River curve
    val lakePath = Path().apply {
        moveTo(toScreenX(0f), toScreenY(880f))
        cubicTo(
            toScreenX(250f), toScreenY(840f),
            toScreenX(600f), toScreenY(940f),
            toScreenX(1000f), toScreenY(900f)
        )
    }
    drawPath(
        path = lakePath,
        color = Color(0xFF143048), // Deep water blue
        style = Stroke(width = 36f * scale, cap = StrokeCap.Round)
    )
}

private fun DrawScope.drawStreetNetwork(
    toScreenX: (Float) -> Float,
    toScreenY: (Float) -> Float,
    scale: Float
) {
    val roadColor = Color(0xFF283648)
    val avenueColor = Color(0xFF33455C)
    val laneDashColor = Color(0xFF536A87)

    // Major Avenues (Horizontal)
    listOf(270f, 490f, 730f).forEach { yCoord ->
        val y = toScreenY(yCoord)
        drawLine(
            color = avenueColor,
            start = Offset(toScreenX(0f), y),
            end = Offset(toScreenX(1000f), y),
            strokeWidth = 24f * scale,
            cap = StrokeCap.Square
        )
        // Center lane dashes
        drawLine(
            color = laneDashColor,
            start = Offset(toScreenX(0f), y),
            end = Offset(toScreenX(1000f), y),
            strokeWidth = 2f * scale,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f * scale, 10f * scale), 0f)
        )
    }

    // Major Avenues (Vertical)
    listOf(310f, 650f).forEach { xCoord ->
        val x = toScreenX(xCoord)
        drawLine(
            color = avenueColor,
            start = Offset(x, toScreenY(0f)),
            end = Offset(x, toScreenY(1000f)),
            strokeWidth = 24f * scale,
            cap = StrokeCap.Square
        )
        // Center lane dashes
        drawLine(
            color = laneDashColor,
            start = Offset(x, toScreenY(0f)),
            end = Offset(x, toScreenY(1000f)),
            strokeWidth = 2f * scale,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f * scale, 10f * scale), 0f)
        )
    }

    // Secondary connecting streets
    listOf(150f, 850f).forEach { yCoord ->
        val y = toScreenY(yCoord)
        drawLine(
            color = roadColor,
            start = Offset(toScreenX(0f), y),
            end = Offset(toScreenX(1000f), y),
            strokeWidth = 14f * scale
        )
    }
}

private fun DrawScope.drawActiveRoute(
    booking: RideBooking,
    dashPhase: Float,
    toScreenX: (Float) -> Float,
    toScreenY: (Float) -> Float,
    scale: Float
) {
    val px = toScreenX(booking.pickup.x)
    val py = toScreenY(booking.pickup.y)
    val dx = toScreenX(booking.dropoff.x)
    val dy = toScreenY(booking.dropoff.y)

    // Intermediate street-following waypoints
    val midY = py
    val midX = dx

    val routePath = Path().apply {
        moveTo(px, py)
        lineTo(midX, midY)
        lineTo(dx, dy)
    }

    // Route Outer Glow
    drawPath(
        path = routePath,
        color = AmberPrimary.copy(alpha = 0.3f),
        style = Stroke(width = 12f * scale, cap = StrokeCap.Round)
    )

    // Route Core Line with animated Dash
    drawPath(
        path = routePath,
        color = AmberPrimary,
        style = Stroke(
            width = 5f * scale,
            cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(18f * scale, 12f * scale),
                dashPhase
            )
        )
    )
}

private fun DrawScope.drawVehicleIcon(
    screenX: Float,
    screenY: Float,
    heading: Float,
    type: RideType,
    isAssigned: Boolean,
    scale: Float
) {
    val vehicleColor = when {
        isAssigned -> AmberPrimary
        type == RideType.AUTO_ELECTRIC -> EmeraldEco
        type == RideType.CAB_MINI -> Color(0xFF38BDF8)
        type == RideType.BIKE_TAXI -> Color(0xFFF97316)
        else -> AmberDark
    }

    val bodyRadius = (if (isAssigned) 12f else 9f) * scale

    // Vehicle Shadow
    drawCircle(
        color = Color.Black.copy(alpha = 0.5f),
        radius = bodyRadius * 1.15f,
        center = Offset(screenX + 2f, screenY + 3f)
    )

    // Vehicle Body
    drawCircle(
        color = vehicleColor,
        radius = bodyRadius,
        center = Offset(screenX, screenY)
    )

    // Vehicle Roof / Cab Highlight
    drawCircle(
        color = Color.White.copy(alpha = 0.85f),
        radius = bodyRadius * 0.45f,
        center = Offset(screenX, screenY)
    )

    // Heading direction tip
    val rad = Math.toRadians(heading.toDouble())
    val tipX = screenX + (bodyRadius * 1.4f * cos(rad)).toFloat()
    val tipY = screenY + (bodyRadius * 1.4f * sin(rad)).toFloat()
    drawCircle(
        color = AmberLight,
        radius = bodyRadius * 0.28f,
        center = Offset(tipX, tipY)
    )
}
