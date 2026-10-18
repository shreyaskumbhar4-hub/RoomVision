package com.example.roomvision.ar

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.example.roomvision.model.ARInteractionMode
import com.example.roomvision.model.ARSessionState
import com.example.roomvision.model.CuratedFloorMaterials
import com.example.roomvision.model.FloorDesignConfig
import com.example.roomvision.model.MeasurementPoint
import com.example.roomvision.model.PlacedFurniture
import com.example.roomvision.model.SurfaceType
import com.example.roomvision.model.WallDesignConfig
import com.example.roomvision.model.WallTarget
import com.example.roomvision.model.WallpaperPattern
import kotlin.math.hypot
import kotlin.math.sqrt

@Composable
fun RoomARCanvas(
    state: ARSessionState,
    onFloorTap: (normX: Float, normY: Float) -> Unit,
    onFurnitureTap: (item: PlacedFurniture) -> Unit,
    onFurnitureDrag: (item: PlacedFurniture, normX: Float, normY: Float) -> Unit,
    onWallTap: (target: WallTarget) -> Unit,
    onMeasurePointAdd: (point: MeasurementPoint) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "arPulse")
    val pulseAnim by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    var canvasSize by remember { mutableStateOf(Size.Zero) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(state.interactionMode, state.selectedFurnitureId) {
                detectTapGestures { tapOffset ->
                    val w = size.width.toFloat()
                    val h = size.height.toFloat()
                    val horizonY = h * 0.48f

                    // 1. Check if tap is in tape measure mode
                    if (state.interactionMode == ARInteractionMode.MEASUREMENT_TAPE) {
                        val label = "P${state.measurementPoints.size + 1}"
                        onMeasurePointAdd(MeasurementPoint(tapOffset.x, tapOffset.y, label))
                        return@detectTapGestures
                    }

                    // 2. Check if tapped an existing placed furniture item (depth sorted top-to-bottom)
                    val sortedFurniture = state.placedFurniture.sortedByDescending { it.positionY }
                    for (item in sortedFurniture) {
                        val cx = (w / 2f) + (item.positionX * (w * 0.38f))
                        val cy = horizonY + (w * 0.22f) + (item.positionY * (h * 0.24f))
                        val hitRadius = 45f * item.scale
                        if (hypot(tapOffset.x - cx, tapOffset.y - cy) <= hitRadius) {
                            onFurnitureTap(item)
                            return@detectTapGestures
                        }
                    }

                    // 3. Check if tapped on wall candidates (upper region above horizon)
                    if (tapOffset.y < horizonY) {
                        val leftCornerX = w * 0.22f
                        val rightCornerX = w * 0.78f
                        when {
                            tapOffset.x < leftCornerX -> onWallTap(WallTarget.LEFT_WALL)
                            tapOffset.x > rightCornerX -> onWallTap(WallTarget.RIGHT_WALL)
                            else -> onWallTap(WallTarget.BACK_WALL)
                        }
                        return@detectTapGestures
                    }

                    // 4. Tap is on floor surface -> compute normalized coords
                    val normX = ((tapOffset.x - (w / 2f)) / (w * 0.38f)).coerceIn(-0.95f, 0.95f)
                    val normY = ((tapOffset.y - horizonY - (w * 0.22f)) / (h * 0.24f)).coerceIn(-0.95f, 0.95f)
                    onFloorTap(normX, normY)
                }
            }
            .pointerInput(state.selectedFurnitureId) {
                detectDragGestures { change, dragAmount ->
                    val currentSelId = state.selectedFurnitureId ?: return@detectDragGestures
                    val selectedItem = state.placedFurniture.find { it.instanceId == currentSelId } ?: return@detectDragGestures

                    change.consume()
                    val w = canvasSize.width
                    val h = canvasSize.height
                    if (w > 0 && h > 0) {
                        val deltaNormX = dragAmount.x / (w * 0.38f)
                        val deltaNormY = dragAmount.y / (h * 0.24f)
                        val newX = (selectedItem.positionX + deltaNormX).coerceIn(-0.95f, 0.95f)
                        val newY = (selectedItem.positionY + deltaNormY).coerceIn(-0.95f, 0.95f)
                        onFurnitureDrag(selectedItem, newX, newY)
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            canvasSize = size
            val w = size.width
            val h = size.height
            val horizonY = h * 0.48f

            val leftCornerX = w * 0.20f
            val rightCornerX = w * 0.80f

            // 1. Draw Recycled / Recolored Wall Surfaces
            drawWallSurfaces(
                drawScope = this,
                w = w,
                horizonY = horizonY,
                leftCornerX = leftCornerX,
                rightCornerX = rightCornerX,
                wallConfig = state.wallDesign,
                selectedWall = state.wallDesign.targetWall,
                isWallSelectMode = state.interactionMode == ARInteractionMode.WALL_SURFACE_SELECT || state.interactionMode == ARInteractionMode.WALL_COLORING
            )

            // 2. Draw Floor Surface Material (Custom floor texture/finish)
            drawFloorSurface(
                drawScope = this,
                w = w,
                h = h,
                horizonY = horizonY,
                leftCornerX = leftCornerX,
                rightCornerX = rightCornerX,
                floorConfig = state.floorDesign
            )

            // 3. Draw AR Plane Detection Grid & Scanning Dots
            drawARPlaneIndicators(
                drawScope = this,
                w = w,
                h = h,
                horizonY = horizonY,
                pulse = pulseAnim
            )

            // 4. Draw Placed Furniture Items sorted by depth (back to front)
            val sortedFurniture = state.placedFurniture.sortedBy { it.positionY }
            for (item in sortedFurniture) {
                val cx = (w / 2f) + (item.positionX * (w * 0.38f))
                val cy = horizonY + (w * 0.22f) + (item.positionY * (h * 0.24f))
                FurnitureRenderer3D.drawPlacedFurniture(this, item, cx, cy)
            }

            // 5. Draw AR Measurement Tape Points if active
            if (state.measurementPoints.isNotEmpty()) {
                drawMeasurements(this, state.measurementPoints)
            }
        }
    }
}

private fun drawWallSurfaces(
    drawScope: DrawScope,
    w: Float,
    horizonY: Float,
    leftCornerX: Float,
    rightCornerX: Float,
    wallConfig: WallDesignConfig,
    selectedWall: WallTarget,
    isWallSelectMode: Boolean
) {
    val wallColor = Color(wallConfig.colorHex).copy(alpha = wallConfig.opacity)

    // A. Back Wall (middle rectangle)
    val backWallApplies = selectedWall == WallTarget.BACK_WALL || selectedWall == WallTarget.ALL_WALLS
    val backWallFill = if (backWallApplies) wallColor else Color.Transparent

    if (backWallApplies) {
        drawScope.drawRect(
            color = backWallFill,
            topLeft = Offset(leftCornerX, 0f),
            size = Size(rightCornerX - leftCornerX, horizonY)
        )

        // Wallpaper pattern overlay on back wall
        drawWallpaper(
            drawScope = drawScope,
            pattern = wallConfig.wallpaper,
            left = leftCornerX,
            top = 0f,
            width = rightCornerX - leftCornerX,
            height = horizonY,
            scale = wallConfig.wallpaperScale
        )

        // Paint finish sheen highlight / ceiling light bounce
        val sheenAlpha = wallConfig.finish.glossiness * 0.25f
        if (sheenAlpha > 0f) {
            drawScope.drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White.copy(alpha = sheenAlpha), Color.Transparent),
                    startY = 0f,
                    endY = horizonY * 0.6f
                ),
                topLeft = Offset(leftCornerX, 0f),
                size = Size(rightCornerX - leftCornerX, horizonY * 0.6f)
            )
        }
    }

    // B. Left Wall (trapezoid polygon in perspective)
    val leftWallApplies = selectedWall == WallTarget.LEFT_WALL || selectedWall == WallTarget.ALL_WALLS
    if (leftWallApplies) {
        val leftPath = Path().apply {
            moveTo(0f, 0f)
            lineTo(leftCornerX, 0f)
            lineTo(leftCornerX, horizonY)
            lineTo(0f, horizonY + 20f)
            close()
        }
        drawScope.drawPath(
            path = leftPath,
            color = wallColor.copy(
                red = (wallColor.red * 0.90f),
                green = (wallColor.green * 0.90f),
                blue = (wallColor.blue * 0.90f)
            )
        )
    }

    // C. Right Wall (trapezoid polygon in perspective)
    val rightWallApplies = selectedWall == WallTarget.RIGHT_WALL || selectedWall == WallTarget.ALL_WALLS
    if (rightWallApplies) {
        val rightPath = Path().apply {
            moveTo(rightCornerX, 0f)
            lineTo(w, 0f)
            lineTo(w, horizonY + 20f)
            lineTo(rightCornerX, horizonY)
            close()
        }
        drawScope.drawPath(
            path = rightPath,
            color = wallColor.copy(
                red = (wallColor.red * 0.82f),
                green = (wallColor.green * 0.82f),
                blue = (wallColor.blue * 0.82f)
            )
        )
    }

    // D. Wall Selection Highlights / Candidate bounding mesh
    if (isWallSelectMode) {
        val strokeCol = Color(0xFF0284C7)
        // Back wall frame
        drawScope.drawRect(
            color = if (selectedWall == WallTarget.BACK_WALL) strokeCol else strokeCol.copy(alpha = 0.4f),
            topLeft = Offset(leftCornerX + 4f, 4f),
            size = Size(rightCornerX - leftCornerX - 8f, horizonY - 8f),
            style = Stroke(
                width = if (selectedWall == WallTarget.BACK_WALL) 3f else 1.5f,
                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(16f, 10f))
            )
        )

        // Center badge on selected wall
        if (selectedWall == WallTarget.BACK_WALL) {
            val centerWall = Offset((leftCornerX + rightCornerX) / 2f, horizonY * 0.45f)
            drawScope.drawCircle(strokeCol, 16f, centerWall)
            drawScope.drawCircle(Color.White, 7f, centerWall)
        }
    }
}

private fun drawWallpaper(
    drawScope: DrawScope,
    pattern: WallpaperPattern,
    left: Float,
    top: Float,
    width: Float,
    height: Float,
    scale: Float
) {
    if (pattern == WallpaperPattern.NONE) return

    val patCol = Color.Black.copy(alpha = 0.16f)

    when (pattern) {
        WallpaperPattern.WOOD_SLATS -> {
            val slatWidth = 14f * scale
            val gap = 6f * scale
            var x = left + gap
            while (x < left + width) {
                drawScope.drawRect(
                    color = Color(0xFF3E2723).copy(alpha = 0.28f),
                    topLeft = Offset(x, top),
                    size = Size(slatWidth, height)
                )
                // highlight edge on slat
                drawScope.drawLine(
                    color = Color.White.copy(alpha = 0.18f),
                    start = Offset(x, top),
                    end = Offset(x, top + height),
                    strokeWidth = 1.5f
                )
                x += (slatWidth + gap)
            }
        }
        WallpaperPattern.GEOMETRIC_ARCHES -> {
            val archW = 55f * scale
            val archH = 75f * scale
            var y = top + 20f
            while (y < top + height) {
                var x = left + 10f
                while (x < left + width) {
                    val archPath = Path().apply {
                        moveTo(x, y + archH)
                        lineTo(x, y + archH * 0.4f)
                        cubicTo(x, y, x + archW, y, x + archW, y + archH * 0.4f)
                        lineTo(x + archW, y + archH)
                    }
                    drawScope.drawPath(archPath, patCol, style = Stroke(width = 2f * scale))
                    x += archW + 16f * scale
                }
                y += archH + 20f * scale
            }
        }
        WallpaperPattern.HERRINGBONE_BRICK -> {
            val brickW = 40f * scale
            val brickH = 18f * scale
            var r = 0
            var y = top
            while (y < top + height) {
                val offset = if (r % 2 == 1) brickW / 2f else 0f
                var x = left + offset - brickW
                while (x < left + width + brickW) {
                    drawScope.drawRect(
                        color = Color.White.copy(alpha = 0.12f),
                        topLeft = Offset(x, y),
                        size = Size(brickW - 3f, brickH - 3f)
                    )
                    x += brickW
                }
                y += brickH
                r++
            }
        }
        WallpaperPattern.BOTANICAL_JUNGLE -> {
            val leafRadius = 18f * scale
            var y = top + 30f
            while (y < top + height) {
                var x = left + 25f
                while (x < left + width) {
                    drawScope.drawOval(
                        color = Color(0xFF1B4332).copy(alpha = 0.20f),
                        topLeft = Offset(x - leafRadius, y - leafRadius * 0.6f),
                        size = Size(leafRadius * 2f, leafRadius * 1.2f)
                    )
                    x += 65f * scale
                }
                y += 50f * scale
            }
        }
        WallpaperPattern.TERRAZZO_STONE -> {
            val stones = listOf(
                Pair(0.2f, 0.3f), Pair(0.45f, 0.2f), Pair(0.7f, 0.4f),
                Pair(0.3f, 0.65f), Pair(0.85f, 0.7f), Pair(0.6f, 0.85f)
            )
            stones.forEach { (nx, ny) ->
                val px = left + (width * nx)
                val py = top + (height * ny)
                drawScope.drawCircle(Color(0xFFE07A5F).copy(alpha = 0.35f), 7f * scale, Offset(px, py))
                drawScope.drawCircle(Color(0xFF0F172A).copy(alpha = 0.30f), 5f * scale, Offset(px + 20f, py - 10f))
            }
        }
        WallpaperPattern.LINEN_TEXTURE -> {
            val step = 8f * scale
            var x = left
            while (x < left + width) {
                drawScope.drawLine(
                    color = Color.Black.copy(alpha = 0.06f),
                    start = Offset(x, top),
                    end = Offset(x, top + height),
                    strokeWidth = 1f
                )
                x += step
            }
        }
        else -> {}
    }
}

private fun drawFloorSurface(
    drawScope: DrawScope,
    w: Float,
    h: Float,
    horizonY: Float,
    leftCornerX: Float,
    rightCornerX: Float,
    floorConfig: FloorDesignConfig
) {
    val material = CuratedFloorMaterials.getById(floorConfig.selectedMaterialId)
    val baseCol = Color(material.primaryColorHex)
    val accentCol = Color(material.accentColorHex)

    // Floor area polygon (perspective quad)
    val floorPath = Path().apply {
        moveTo(0f, horizonY)
        lineTo(w, horizonY)
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }

    // Base floor color tint with depth gradient
    drawScope.drawPath(
        path = floorPath,
        brush = Brush.verticalGradient(
            colors = listOf(baseCol.copy(alpha = 0.85f), accentCol.copy(alpha = 0.95f)),
            startY = horizonY,
            endY = h
        )
    )

    // Draw Floor Texture patterns
    when (material.patternType) {
        "PLANK" -> {
            // Scandinavian Oak Planks running towards perspective center
            val plankCount = 14
            for (i in 0..plankCount) {
                val topX = (w * 0.15f) + (i * ((w * 0.70f) / plankCount))
                val botX = -w * 0.2f + (i * ((w * 1.4f) / plankCount))
                drawScope.drawLine(
                    color = accentCol.copy(alpha = 0.40f),
                    start = Offset(topX, horizonY),
                    end = Offset(botX, h),
                    strokeWidth = 2f
                )
            }
            // Horizontal plank cross seams
            val seams = listOf(0.2f, 0.45f, 0.72f, 0.90f)
            seams.forEach { f ->
                val y = horizonY + ((h - horizonY) * f)
                drawScope.drawLine(
                    color = accentCol.copy(alpha = 0.25f),
                    start = Offset(0f, y),
                    end = Offset(w, y),
                    strokeWidth = 1.5f
                )
            }
        }
        "HERRINGBONE" -> {
            // Dark Walnut Chevron / Herringbone Parquet
            val step = 32f * floorConfig.patternScale
            var y = horizonY + 10f
            while (y < h) {
                var x = 0f
                var flip = false
                while (x < w) {
                    val endY = y + (if (flip) step * 0.6f else -step * 0.6f)
                    drawScope.drawLine(
                        color = accentCol.copy(alpha = 0.45f),
                        start = Offset(x, y),
                        end = Offset(x + step, endY),
                        strokeWidth = 2f
                    )
                    x += step
                    flip = !flip
                }
                y += step * 0.75f
            }
        }
        "MARBLE" -> {
            // Calacatta Marble Veining
            val veinCol = accentCol.copy(alpha = 0.35f)
            val veinPath = Path().apply {
                moveTo(w * 0.1f, horizonY + 30f)
                cubicTo(w * 0.3f, horizonY + 120f, w * 0.25f, h * 0.75f, w * 0.45f, h)
                moveTo(w * 0.65f, horizonY + 10f)
                cubicTo(w * 0.8f, horizonY + 90f, w * 0.7f, h * 0.8f, w * 0.9f, h)
            }
            drawScope.drawPath(veinPath, veinCol, style = Stroke(width = 3.5f))
        }
        "HEX_TILE" -> {
            // Terracotta Hexagon Grid
            val hexR = 26f * floorConfig.patternScale
            var y = horizonY + 15f
            var row = 0
            while (y < h + hexR) {
                val off = if (row % 2 == 1) hexR * 1.5f else 0f
                var x = off
                while (x < w + hexR) {
                    drawScope.drawCircle(
                        color = accentCol.copy(alpha = 0.35f),
                        radius = hexR,
                        center = Offset(x, y),
                        style = Stroke(width = 1.5f)
                    )
                    x += hexR * 3f
                }
                y += hexR * 1.6f
                row++
            }
        }
        "CONCRETE" -> {
            // Minimalist expansion joints
            val jointCol = Color.Black.copy(alpha = 0.15f)
            drawScope.drawLine(jointCol, Offset(w * 0.5f, horizonY), Offset(w * 0.5f, h), 2.5f)
            drawScope.drawLine(jointCol, Offset(0f, horizonY + (h - horizonY) * 0.5f), Offset(w, horizonY + (h - horizonY) * 0.5f), 2.5f)
        }
        "CARPET" -> {
            // Soft Berber stipple dots
            val dotCol = accentCol.copy(alpha = 0.25f)
            var y = horizonY + 20f
            while (y < h) {
                var x = 20f
                while (x < w) {
                    drawScope.drawCircle(dotCol, 2f, Offset(x, y))
                    x += 24f
                }
                y += 20f
            }
        }
    }

    // Floor Sheen reflection highlight
    if (floorConfig.sheenLevel > 0.1f) {
        drawScope.drawOval(
            brush = Brush.radialGradient(
                colors = listOf(Color.White.copy(alpha = floorConfig.sheenLevel * 0.35f), Color.Transparent),
                center = Offset(w * 0.45f, horizonY + (h - horizonY) * 0.4f),
                radius = w * 0.6f
            ),
            topLeft = Offset(w * 0.1f, horizonY + 10f),
            size = Size(w * 0.8f, (h - horizonY) * 0.7f)
        )
    }
}

private fun drawARPlaneIndicators(
    drawScope: DrawScope,
    w: Float,
    h: Float,
    horizonY: Float,
    pulse: Float
) {
    val cyanColor = Color(0xFF0284C7)

    // Floor plane boundary polygon (dashed)
    val polyPath = Path().apply {
        moveTo(w * 0.15f, horizonY + 25f)
        lineTo(w * 0.85f, horizonY + 25f)
        lineTo(w * 0.95f, h - 35f)
        lineTo(w * 0.05f, h - 35f)
        close()
    }

    drawScope.drawPath(
        path = polyPath,
        color = cyanColor.copy(alpha = 0.35f * pulse),
        style = Stroke(
            width = 2f,
            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(14f, 10f))
        )
    )

    // Corner anchor points for detected floor plane
    val anchorPoints = listOf(
        Offset(w * 0.15f, horizonY + 25f),
        Offset(w * 0.85f, horizonY + 25f),
        Offset(w * 0.95f, h - 35f),
        Offset(w * 0.05f, h - 35f),
        Offset(w * 0.50f, horizonY + (h - horizonY) * 0.5f)
    )

    anchorPoints.forEach { pt ->
        drawScope.drawCircle(cyanColor.copy(alpha = 0.25f * pulse), 12f * pulse, pt)
        drawScope.drawCircle(Color.White, 4f, pt)
        drawScope.drawCircle(cyanColor, 2.5f, pt)
    }
}

private fun drawMeasurements(
    drawScope: DrawScope,
    points: List<MeasurementPoint>
) {
    val measureCol = Color(0xFFF59E0B)

    points.forEachIndexed { i, p ->
        // Draw anchor node
        drawScope.drawCircle(measureCol.copy(alpha = 0.3f), 14f, Offset(p.x, p.y))
        drawScope.drawCircle(Color.White, 6f, Offset(p.x, p.y))
        drawScope.drawCircle(measureCol, 4f, Offset(p.x, p.y))

        // Draw connecting line to previous point
        if (i > 0) {
            val prev = points[i - 1]
            drawScope.drawLine(
                color = measureCol,
                start = Offset(prev.x, prev.y),
                end = Offset(p.x, p.y),
                strokeWidth = 3f,
                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(12f, 8f))
            )

            // Calculate distance in simulated meters (100px ~ 1.0 meter)
            val pxDist = hypot(p.x - prev.x, p.y - prev.y)
            val meters = (pxDist / 120f).coerceAtLeast(0.3f)
            val midX = (prev.x + p.x) / 2f
            val midY = (prev.y + p.y) / 2f

            // Distance label tag pill
            drawScope.drawRoundRect(
                color = Color(0xFF1E293B),
                topLeft = Offset(midX - 35f, midY - 14f),
                size = Size(70f, 28f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f)
            )
            drawScope.drawRoundRect(
                color = measureCol,
                topLeft = Offset(midX - 35f, midY - 14f),
                size = Size(70f, 28f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f),
                style = Stroke(width = 1.5f)
            )
        }
    }
}
