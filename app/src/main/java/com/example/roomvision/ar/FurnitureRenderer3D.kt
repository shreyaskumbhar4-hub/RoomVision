package com.example.roomvision.ar

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.example.roomvision.model.FinishType
import com.example.roomvision.model.FurnitureModel3D
import com.example.roomvision.model.PlacedFurniture
import kotlin.math.cos
import kotlin.math.sin

object FurnitureRenderer3D {

    fun drawPlacedFurniture(
        drawScope: DrawScope,
        item: PlacedFurniture,
        centerX: Float,
        centerY: Float
    ) {
        val s = item.scale
        val rot = item.rotationDegrees

        // Resolve primary upholstery / body color from finish
        val (baseColor, woodAccent, metalAccent) = resolveColors(item.selectedFinish, item.customColorHex)

        // 1. Draw floor contact shadow (always flat on floor beneath the object)
        drawFloorShadow(drawScope, centerX, centerY, s, item.model3D)

        // 2. Draw 3D Model with rotation
        drawScope.rotate(rot, pivot = Offset(centerX, centerY)) {
            when (item.model3D) {
                FurnitureModel3D.NORDIC_ARMCHAIR,
                FurnitureModel3D.LOUNGE_ACCENT_CHAIR -> drawArmchair(this, centerX, centerY, s, baseColor, woodAccent)
                FurnitureModel3D.MODERN_SECTIONAL_SOFA -> drawSectionalSofa(this, centerX, centerY, s, baseColor)
                FurnitureModel3D.OAK_COFFEE_TABLE -> drawCoffeeTable(this, centerX, centerY, s, woodAccent)
                FurnitureModel3D.MINIMAL_DINING_SET -> drawDiningSet(this, centerX, centerY, s, woodAccent, baseColor)
                FurnitureModel3D.FLOOR_ARC_LAMP -> drawArcLamp(this, centerX, centerY, s, metalAccent)
                FurnitureModel3D.FIDDLE_LEAF_FIG,
                FurnitureModel3D.MONSTERA_PLANT -> drawHousePlant(this, centerX, centerY, s, baseColor, woodAccent)
                FurnitureModel3D.MODULAR_BOOKSHELF -> drawBookshelf(this, centerX, centerY, s, woodAccent, metalAccent)
                FurnitureModel3D.PLATFORM_BED -> drawPlatformBed(this, centerX, centerY, s, woodAccent, baseColor)
                FurnitureModel3D.MEDIA_CONSOLE -> drawMediaConsole(this, centerX, centerY, s, woodAccent, metalAccent)
            }
        }

        // 3. Draw selection ring & bounding indicators if item is selected
        if (item.isSelected) {
            drawSelectionGizmo(drawScope, centerX, centerY, s, rot, item.dimensionsDesc)
        }
    }

    private fun drawFloorShadow(
        drawScope: DrawScope,
        cx: Float,
        cy: Float,
        s: Float,
        model: FurnitureModel3D
    ) {
        val shadowColor = Color.Black.copy(alpha = 0.22f)
        val (w, h) = when (model) {
            FurnitureModel3D.MODERN_SECTIONAL_SOFA -> Pair(160f * s, 70f * s)
            FurnitureModel3D.PLATFORM_BED -> Pair(150f * s, 110f * s)
            FurnitureModel3D.MEDIA_CONSOLE -> Pair(140f * s, 50f * s)
            FurnitureModel3D.MINIMAL_DINING_SET -> Pair(120f * s, 85f * s)
            FurnitureModel3D.OAK_COFFEE_TABLE -> Pair(100f * s, 55f * s)
            FurnitureModel3D.MODULAR_BOOKSHELF -> Pair(110f * s, 45f * s)
            FurnitureModel3D.FLOOR_ARC_LAMP -> Pair(55f * s, 40f * s)
            else -> Pair(80f * s, 60f * s)
        }

        drawScope.drawOval(
            color = shadowColor,
            topLeft = Offset(cx - w / 2f + 4f, cy - h / 2f + 8f),
            size = Size(w, h)
        )
    }

    private fun drawArmchair(
        drawScope: DrawScope,
        cx: Float,
        cy: Float,
        s: Float,
        cushionColor: Color,
        woodColor: Color
    ) {
        val lightColor = cushionColor.copy(
            red = (cushionColor.red * 1.15f).coerceAtMost(1f),
            green = (cushionColor.green * 1.15f).coerceAtMost(1f),
            blue = (cushionColor.blue * 1.15f).coerceAtMost(1f)
        )
        val darkColor = cushionColor.copy(
            red = (cushionColor.red * 0.8f),
            green = (cushionColor.green * 0.8f),
            blue = (cushionColor.blue * 0.8f)
        )

        val w = 64f * s
        val d = 56f * s
        val h = 60f * s

        // Four wooden stilt legs
        val legStroke = 4.5f * s
        val legCol = woodColor
        drawScope.drawLine(legCol, Offset(cx - w * 0.35f, cy + d * 0.25f), Offset(cx - w * 0.42f, cy + d * 0.45f), legStroke)
        drawScope.drawLine(legCol, Offset(cx + w * 0.35f, cy + d * 0.25f), Offset(cx + w * 0.42f, cy + d * 0.45f), legStroke)
        drawScope.drawLine(legCol, Offset(cx - w * 0.32f, cy - d * 0.25f), Offset(cx - w * 0.38f, cy - d * 0.05f), legStroke)
        drawScope.drawLine(legCol, Offset(cx + w * 0.32f, cy - d * 0.25f), Offset(cx + w * 0.38f, cy - d * 0.05f), legStroke)

        // Main seat base cushion
        drawScope.drawRoundRect(
            color = darkColor,
            topLeft = Offset(cx - w * 0.42f, cy - d * 0.15f),
            size = Size(w * 0.84f, d * 0.55f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f * s)
        )
        drawScope.drawRoundRect(
            color = cushionColor,
            topLeft = Offset(cx - w * 0.40f, cy - d * 0.25f),
            size = Size(w * 0.80f, d * 0.55f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f * s)
        )

        // Curved Backrest (elevated above seat)
        drawScope.drawRoundRect(
            color = lightColor,
            topLeft = Offset(cx - w * 0.38f, cy - d * 0.50f - h * 0.25f),
            size = Size(w * 0.76f, d * 0.45f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(14f * s)
        )

        // Left & Right Armrests
        drawScope.drawRoundRect(
            color = darkColor,
            topLeft = Offset(cx - w * 0.48f, cy - d * 0.35f - h * 0.1f),
            size = Size(w * 0.18f, d * 0.55f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f * s)
        )
        drawScope.drawRoundRect(
            color = darkColor,
            topLeft = Offset(cx + w * 0.30f, cy - d * 0.35f - h * 0.1f),
            size = Size(w * 0.18f, d * 0.55f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f * s)
        )

        // Seam piping detail
        drawScope.drawRoundRect(
            color = Color.Black.copy(alpha = 0.12f),
            topLeft = Offset(cx - w * 0.36f, cy - d * 0.22f),
            size = Size(w * 0.72f, d * 0.45f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f * s),
            style = Stroke(width = 1.5f * s)
        )
    }

    private fun drawSectionalSofa(
        drawScope: DrawScope,
        cx: Float,
        cy: Float,
        s: Float,
        color: Color
    ) {
        val lightColor = color.copy(
            red = (color.red * 1.12f).coerceAtMost(1f),
            green = (color.green * 1.12f).coerceAtMost(1f),
            blue = (color.blue * 1.12f).coerceAtMost(1f)
        )
        val darkColor = color.copy(
            red = color.red * 0.78f,
            green = color.green * 0.78f,
            blue = color.blue * 0.78f
        )

        val w = 150f * s
        val d = 60f * s

        // Low profile feet
        val footCol = Color(0xFF1E293B)
        drawScope.drawRect(footCol, Offset(cx - w * 0.45f, cy + d * 0.35f), Size(12f * s, 8f * s))
        drawScope.drawRect(footCol, Offset(cx + w * 0.40f, cy + d * 0.35f), Size(12f * s, 8f * s))
        drawScope.drawRect(footCol, Offset(cx - w * 0.45f, cy - d * 0.40f), Size(12f * s, 8f * s))
        drawScope.drawRect(footCol, Offset(cx + w * 0.40f, cy - d * 0.40f), Size(12f * s, 8f * s))

        // Main seat platform
        drawScope.drawRoundRect(
            color = darkColor,
            topLeft = Offset(cx - w * 0.48f, cy - d * 0.20f),
            size = Size(w * 0.96f, d * 0.55f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f * s)
        )

        // 3 Modular seat cushions
        val cushionW = (w * 0.92f) / 3f
        for (i in 0..2) {
            val startX = cx - w * 0.46f + (i * cushionW)
            drawScope.drawRoundRect(
                color = color,
                topLeft = Offset(startX + 2f * s, cy - d * 0.22f),
                size = Size(cushionW - 4f * s, d * 0.50f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f * s)
            )
        }

        // Long backrest
        drawScope.drawRoundRect(
            color = lightColor,
            topLeft = Offset(cx - w * 0.48f, cy - d * 0.55f),
            size = Size(w * 0.96f, d * 0.38f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f * s)
        )

        // Side armrests
        drawScope.drawRoundRect(
            color = darkColor,
            topLeft = Offset(cx - w * 0.52f, cy - d * 0.48f),
            size = Size(w * 0.12f, d * 0.75f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f * s)
        )
        drawScope.drawRoundRect(
            color = darkColor,
            topLeft = Offset(cx + w * 0.40f, cy - d * 0.48f),
            size = Size(w * 0.12f, d * 0.75f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f * s)
        )

        // Throw pillow accents
        drawScope.drawRoundRect(
            color = Color(0xFFE07A5F),
            topLeft = Offset(cx - w * 0.38f, cy - d * 0.45f),
            size = Size(24f * s, 22f * s),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(5f * s)
        )
        drawScope.drawRoundRect(
            color = Color(0xFFF1F5F9),
            topLeft = Offset(cx + w * 0.22f, cy - d * 0.45f),
            size = Size(24f * s, 22f * s),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(5f * s)
        )
    }

    private fun drawCoffeeTable(
        drawScope: DrawScope,
        cx: Float,
        cy: Float,
        s: Float,
        woodColor: Color
    ) {
        val lightWood = woodColor.copy(
            red = (woodColor.red * 1.15f).coerceAtMost(1f),
            green = (woodColor.green * 1.15f).coerceAtMost(1f),
            blue = (woodColor.blue * 1.15f).coerceAtMost(1f)
        )
        val darkWood = woodColor.copy(
            red = woodColor.red * 0.75f,
            green = woodColor.green * 0.75f,
            blue = woodColor.blue * 0.75f
        )

        val w = 90f * s
        val d = 48f * s

        // 4 Round wooden cylinder legs
        val legR = 7f * s
        drawScope.drawCircle(darkWood, legR, Offset(cx - w * 0.38f, cy - d * 0.30f))
        drawScope.drawCircle(darkWood, legR, Offset(cx + w * 0.38f, cy - d * 0.30f))
        drawScope.drawCircle(darkWood, legR, Offset(cx - w * 0.38f, cy + d * 0.25f))
        drawScope.drawCircle(darkWood, legR, Offset(cx + w * 0.38f, cy + d * 0.25f))

        // Table top thickness / rim
        drawScope.drawRoundRect(
            color = darkWood,
            topLeft = Offset(cx - w * 0.46f, cy - d * 0.35f + 6f * s),
            size = Size(w * 0.92f, d * 0.70f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(18f * s)
        )

        // Table top surface
        drawScope.drawRoundRect(
            color = lightWood,
            topLeft = Offset(cx - w * 0.46f, cy - d * 0.35f),
            size = Size(w * 0.92f, d * 0.70f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(18f * s)
        )

        // Wood grain lines
        val grainCol = darkWood.copy(alpha = 0.25f)
        drawScope.drawLine(grainCol, Offset(cx - w * 0.35f, cy - d * 0.1f), Offset(cx + w * 0.35f, cy - d * 0.1f), 1.5f * s)
        drawScope.drawLine(grainCol, Offset(cx - w * 0.28f, cy + d * 0.05f), Offset(cx + w * 0.28f, cy + d * 0.05f), 1.5f * s)

        // Decorative ceramic bowl & art book on table
        drawScope.drawCircle(Color.White, 9f * s, Offset(cx - w * 0.18f, cy - d * 0.05f))
        drawScope.drawRect(Color(0xFF334155), Offset(cx + w * 0.10f, cy - d * 0.12f), Size(22f * s, 16f * s))
    }

    private fun drawDiningSet(
        drawScope: DrawScope,
        cx: Float,
        cy: Float,
        s: Float,
        woodColor: Color,
        chairColor: Color
    ) {
        val w = 110f * s
        val d = 65f * s

        // 4 Surrounding chairs tucked in
        val chairR = 14f * s
        drawScope.drawCircle(chairColor, chairR, Offset(cx - w * 0.25f, cy - d * 0.55f))
        drawScope.drawCircle(chairColor, chairR, Offset(cx + w * 0.25f, cy - d * 0.55f))
        drawScope.drawCircle(chairColor, chairR, Offset(cx - w * 0.25f, cy + d * 0.55f))
        drawScope.drawCircle(chairColor, chairR, Offset(cx + w * 0.25f, cy + d * 0.55f))

        // Main dining tabletop
        drawScope.drawRoundRect(
            color = woodColor.copy(red = woodColor.red * 0.8f, green = woodColor.green * 0.8f, blue = woodColor.blue * 0.8f),
            topLeft = Offset(cx - w * 0.45f, cy - d * 0.38f + 5f * s),
            size = Size(w * 0.90f, d * 0.76f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f * s)
        )
        drawScope.drawRoundRect(
            color = woodColor,
            topLeft = Offset(cx - w * 0.45f, cy - d * 0.38f),
            size = Size(w * 0.90f, d * 0.76f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f * s)
        )

        // Center vase with flower sprig
        drawScope.drawCircle(Color(0xFFF8FAFC), 8f * s, Offset(cx, cy))
        drawScope.drawCircle(Color(0xFF10B981), 4f * s, Offset(cx, cy - 2f * s))
    }

    private fun drawArcLamp(
        drawScope: DrawScope,
        cx: Float,
        cy: Float,
        s: Float,
        brassColor: Color
    ) {
        // Base heavy stone disc
        drawScope.drawCircle(Color(0xFF334155), 16f * s, Offset(cx - 20f * s, cy + 15f * s))

        // Sweeping brass curved tube
        val arcPath = Path().apply {
            moveTo(cx - 20f * s, cy + 15f * s)
            cubicTo(
                cx - 20f * s, cy - 65f * s,
                cx + 35f * s, cy - 75f * s,
                cx + 30f * s, cy - 10f * s
            )
        }
        drawScope.drawPath(arcPath, brassColor, style = Stroke(width = 4.5f * s))

        // Dome shade suspended
        drawScope.drawOval(
            color = brassColor,
            topLeft = Offset(cx + 18f * s, cy - 18f * s),
            size = Size(24f * s, 16f * s)
        )

        // Warm light glow pool on floor below dome
        drawScope.drawCircle(
            color = Color(0xFFFBBF24).copy(alpha = 0.22f),
            radius = 28f * s,
            center = Offset(cx + 30f * s, cy + 10f * s)
        )
    }

    private fun drawHousePlant(
        drawScope: DrawScope,
        cx: Float,
        cy: Float,
        s: Float,
        potColor: Color,
        woodAccent: Color
    ) {
        // Ceramic Pot
        val potW = 32f * s
        val potH = 28f * s
        drawScope.drawOval(potColor, Offset(cx - potW / 2f, cy - potH * 0.2f), Size(potW, potH * 0.5f))
        drawScope.drawRect(potColor, Offset(cx - potW / 2f, cy - potH * 0.2f), Size(potW, potH))
        drawScope.drawOval(
            color = Color(0xFF3E2723), // Dark soil
            topLeft = Offset(cx - potW * 0.45f, cy - potH * 0.35f),
            size = Size(potW * 0.9f, potH * 0.35f)
        )

        // Lush green leaves radiating outward
        val leafGreen = Color(0xFF2D6A4F)
        val leafGreenLight = Color(0xFF52B788)

        val leafAngles = listOf(-60f, -30f, 0f, 35f, 65f, -90f, 90f)
        leafAngles.forEachIndexed { i, deg ->
            val rad = Math.toRadians(deg.toDouble())
            val lx = cx + (cos(rad) * 28f * s).toFloat()
            val ly = cy - 25f * s + (sin(rad) * 16f * s).toFloat()
            val col = if (i % 2 == 0) leafGreen else leafGreenLight

            drawScope.drawOval(
                color = col,
                topLeft = Offset(lx - 10f * s, ly - 7f * s),
                size = Size(20f * s, 14f * s)
            )
        }
    }

    private fun drawBookshelf(
        drawScope: DrawScope,
        cx: Float,
        cy: Float,
        s: Float,
        woodColor: Color,
        metalColor: Color
    ) {
        val w = 85f * s
        val d = 32f * s

        // Vertical frame stiles
        val stileStroke = 4f * s
        drawScope.drawLine(metalColor, Offset(cx - w * 0.44f, cy - d * 0.4f), Offset(cx - w * 0.44f, cy + d * 0.4f), stileStroke)
        drawScope.drawLine(metalColor, Offset(cx + w * 0.44f, cy - d * 0.4f), Offset(cx + w * 0.44f, cy + d * 0.4f), stileStroke)

        // Shelves
        for (i in -1..1) {
            val sy = cy + (i * d * 0.32f)
            drawScope.drawRoundRect(
                woodColor,
                topLeft = Offset(cx - w * 0.46f, sy - 4f * s),
                size = Size(w * 0.92f, 8f * s),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f * s)
            )
        }

        // Shelf decor (books & vase)
        drawScope.drawRect(Color(0xFFE07A5F), Offset(cx - w * 0.3f, cy - d * 0.35f), Size(8f * s, 16f * s))
        drawScope.drawRect(Color(0xFF0284C7), Offset(cx - w * 0.2f, cy - d * 0.35f), Size(7f * s, 14f * s))
        drawScope.drawCircle(Color.White, 6f * s, Offset(cx + w * 0.22f, cy + d * 0.05f))
    }

    private fun drawPlatformBed(
        drawScope: DrawScope,
        cx: Float,
        cy: Float,
        s: Float,
        woodColor: Color,
        duvetColor: Color
    ) {
        val w = 120f * s
        val d = 95f * s

        // Outer low wooden platform frame
        drawScope.drawRoundRect(
            color = woodColor,
            topLeft = Offset(cx - w * 0.48f, cy - d * 0.45f),
            size = Size(w * 0.96f, d * 0.90f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f * s)
        )

        // Padded Headboard
        drawScope.drawRoundRect(
            color = woodColor.copy(red = woodColor.red * 0.7f, green = woodColor.green * 0.7f, blue = woodColor.blue * 0.7f),
            topLeft = Offset(cx - w * 0.46f, cy - d * 0.52f),
            size = Size(w * 0.92f, d * 0.18f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f * s)
        )

        // 2 Sleeping Pillows
        val pillowW = w * 0.34f
        val pillowH = d * 0.20f
        drawScope.drawRoundRect(
            color = Color.White,
            topLeft = Offset(cx - w * 0.40f, cy - d * 0.32f),
            size = Size(pillowW, pillowH),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f * s)
        )
        drawScope.drawRoundRect(
            color = Color.White,
            topLeft = Offset(cx + w * 0.06f, cy - d * 0.32f),
            size = Size(pillowW, pillowH),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f * s)
        )

        // Duvet folded across bed
        drawScope.drawRoundRect(
            color = duvetColor,
            topLeft = Offset(cx - w * 0.42f, cy - d * 0.08f),
            size = Size(w * 0.84f, d * 0.50f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f * s)
        )
    }

    private fun drawMediaConsole(
        drawScope: DrawScope,
        cx: Float,
        cy: Float,
        s: Float,
        woodColor: Color,
        metalColor: Color
    ) {
        val w = 130f * s
        val d = 42f * s

        // 4 Stilt metal legs
        drawScope.drawLine(metalColor, Offset(cx - w * 0.42f, cy + d * 0.35f), Offset(cx - w * 0.45f, cy + d * 0.52f), 3.5f * s)
        drawScope.drawLine(metalColor, Offset(cx + w * 0.42f, cy + d * 0.35f), Offset(cx + w * 0.45f, cy + d * 0.52f), 3.5f * s)
        drawScope.drawLine(metalColor, Offset(cx - w * 0.42f, cy - d * 0.35f), Offset(cx - w * 0.45f, cy - d * 0.20f), 3.5f * s)
        drawScope.drawLine(metalColor, Offset(cx + w * 0.42f, cy - d * 0.35f), Offset(cx + w * 0.45f, cy - d * 0.20f), 3.5f * s)

        // Console cabinet box
        drawScope.drawRoundRect(
            color = woodColor,
            topLeft = Offset(cx - w * 0.48f, cy - d * 0.42f),
            size = Size(w * 0.96f, d * 0.78f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f * s)
        )

        // Fluted tambour sliding door grooves
        val grooveCol = Color.Black.copy(alpha = 0.18f)
        val count = 12
        val step = (w * 0.88f) / count
        for (i in 1 until count) {
            val gx = cx - w * 0.44f + (i * step)
            drawScope.drawLine(grooveCol, Offset(gx, cy - d * 0.35f), Offset(gx, cy + d * 0.30f), 1.5f * s)
        }
    }

    private fun drawSelectionGizmo(
        drawScope: DrawScope,
        cx: Float,
        cy: Float,
        s: Float,
        rot: Float,
        dimens: String
    ) {
        val radius = 55f * s
        val gizmoColor = Color(0xFF0284C7)

        // Outer cyan dashed rotation ring
        drawScope.drawCircle(
            color = gizmoColor,
            radius = radius,
            center = Offset(cx, cy),
            style = Stroke(
                width = 2.5f,
                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(12f, 8f))
            )
        )

        // Direction / Front facing marker
        val rad = Math.toRadians((rot - 90.0))
        val frontX = cx + (cos(rad) * radius).toFloat()
        val frontY = cy + (sin(rad) * radius).toFloat()
        drawScope.drawCircle(Color.White, 7f, Offset(frontX, frontY))
        drawScope.drawCircle(gizmoColor, 5f, Offset(frontX, frontY))

        // Center translation pivot dot
        drawScope.drawCircle(Color.White, 5f, Offset(cx, cy))
        drawScope.drawCircle(gizmoColor, 3.5f, Offset(cx, cy))

        // Corner bounding brackets
        val boxW = 50f * s
        val boxH = 40f * s
        val bracketSize = 12f

        val corners = listOf(
            Offset(cx - boxW, cy - boxH),
            Offset(cx + boxW, cy - boxH),
            Offset(cx - boxW, cy + boxH),
            Offset(cx + boxW, cy + boxH)
        )
        corners.forEach { c ->
            drawScope.drawCircle(Color.White, 4f, c)
            drawScope.drawCircle(gizmoColor, 2.5f, c)
        }
    }

    private fun resolveColors(finish: FinishType, customHex: Long?): Triple<Color, Color, Color> {
        if (customHex != null) {
            val custom = Color(customHex)
            return Triple(custom, Color(0xFF8D5B4C), Color(0xFF1E293B))
        }

        return when (finish) {
            FinishType.FABRIC_CREAM -> Triple(Color(0xFFF7F4EE), Color(0xFFD4A373), Color(0xFF334155))
            FinishType.FABRIC_CHARCOAL -> Triple(Color(0xFF334155), Color(0xFF8D5B4C), Color(0xFF0F172A))
            FinishType.FABRIC_TERRACOTTA -> Triple(Color(0xFFCB6D51), Color(0xFF58311F), Color(0xFF1E293B))
            FinishType.FABRIC_SAGE -> Triple(Color(0xFF84A98C), Color(0xFFD4A373), Color(0xFF1E293B))
            FinishType.FABRIC_INDIGO -> Triple(Color(0xFF1E3A8A), Color(0xFF58311F), Color(0xFF0F172A))
            FinishType.LEATHER_COGNAC -> Triple(Color(0xFF9C4931), Color(0xFF3D2619), Color(0xFFD97706))
            FinishType.LEATHER_BLACK -> Triple(Color(0xFF18181B), Color(0xFF3D2619), Color(0xFF71717A))
            FinishType.WOOD_NATURAL_OAK -> Triple(Color(0xFFE0C4A4), Color(0xFFC7A783), Color(0xFF1E293B))
            FinishType.WOOD_WALNUT -> Triple(Color(0xFF58311F), Color(0xFF3D2619), Color(0xFFD97706))
            FinishType.WOOD_ASH -> Triple(Color(0xFFECE4D8), Color(0xFFD8CFBE), Color(0xFF334155))
            FinishType.METAL_MATTE_BLACK -> Triple(Color(0xFF1E293B), Color(0xFF334155), Color(0xFF0F172A))
            FinishType.METAL_BRUSHED_BRASS -> Triple(Color(0xFFE5B55F), Color(0xFFD4A373), Color(0xFFB45309))
        }
    }
}
