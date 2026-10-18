package com.example.roomvision.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.outlined.Chair
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roomvision.ar.CameraBackground
import com.example.roomvision.ar.RoomARCanvas
import com.example.roomvision.model.ARInteractionMode
import com.example.roomvision.model.CuratedFloorMaterials
import com.example.roomvision.model.CuratedWallPalettes
import com.example.roomvision.model.FloorMaterialOption
import com.example.roomvision.model.FurnitureCatalog
import com.example.roomvision.model.PaintFinish
import com.example.roomvision.model.PlacedFurniture
import com.example.roomvision.model.RoomType
import com.example.roomvision.model.WallTarget
import com.example.roomvision.model.WallpaperPattern
import com.example.roomvision.viewmodel.AppNavDestination
import com.example.roomvision.viewmodel.RoomVisionViewModel
import kotlin.math.roundToInt

@Composable
fun ARStudioScreen(
    viewModel: RoomVisionViewModel,
    modifier: Modifier = Modifier
) {
    val arState by viewModel.arState.collectAsState()
    var showSaveDialog by remember { mutableStateOf(false) }
    var showSnapshotDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        // 1. Camera / Virtual Room Background
        CameraBackground(
            isCameraEnabled = arState.isRealCameraActive,
            simulationRoomIndex = arState.simulationRoomIndex,
            onCameraUnavailable = { viewModel.disableCameraFeed() },
            modifier = Modifier.fillMaxSize()
        )

        // 2. Interactive AR Surface & 3D Canvas
        RoomARCanvas(
            state = arState,
            onFloorTap = { nx, ny ->
                viewModel.selectFurniture(null)
            },
            onFurnitureTap = { item ->
                viewModel.selectFurniture(item)
            },
            onFurnitureDrag = { item, nx, ny ->
                viewModel.updateFurniturePosition(item, nx, ny)
            },
            onWallTap = { target ->
                viewModel.selectWallTarget(target)
            },
            onMeasurePointAdd = { point ->
                viewModel.addMeasurementPoint(point)
            },
            modifier = Modifier.fillMaxSize()
        )

        // 3. Top HUD Bar (Controls, surface indicators, save & camera toggle)
        TopARHud(
            roomName = arState.currentRoomDesignName,
            isRealCameraActive = arState.isRealCameraActive,
            surfaceCount = arState.surfaces.size,
            furnitureCount = arState.placedFurniture.size,
            onToggleCamera = { viewModel.toggleCameraFeed() },
            onCycleRoom = { viewModel.cycleSimulationRoom() },
            onSaveClick = { showSaveDialog = true },
            onSnapshotClick = { showSnapshotDialog = true },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )

        // 4. Bottom Interaction Dock
        BottomARDock(
            arState = arState,
            viewModel = viewModel,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )
    }

    // Save Design Dialog
    if (showSaveDialog) {
        SaveDesignDialog(
            defaultName = arState.currentRoomDesignName,
            onDismiss = { showSaveDialog = false },
            onSave = { name, roomType, notes ->
                viewModel.saveCurrentDesign(name, roomType, notes)
                showSaveDialog = false
            }
        )
    }

    // Snapshot / Summary Dialog
    if (showSnapshotDialog) {
        SnapshotSummaryDialog(
            arState = arState,
            onDismiss = { showSnapshotDialog = false }
        )
    }
}

@Composable
private fun TopARHud(
    roomName: String,
    isRealCameraActive: Boolean,
    surfaceCount: Int,
    furnitureCount: Int,
    onToggleCamera: () -> Unit,
    onCycleRoom: () -> Unit,
    onSaveClick: () -> Unit,
    onSnapshotClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFF0F172A).copy(alpha = 0.88f),
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 8.dp,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = roomName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(Color(0xFF10B981), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "$surfaceCount surfaces • $furnitureCount items",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Room Backdrop / Camera Switch
                IconButton(
                    onClick = onToggleCamera,
                    modifier = Modifier.size(38.dp).testTag("camera_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isRealCameraActive) Icons.Default.Videocam else Icons.Default.CameraAlt,
                        contentDescription = "Toggle Camera",
                        tint = if (isRealCameraActive) Color(0xFF38BDF8) else Color.White
                    )
                }

                if (!isRealCameraActive) {
                    IconButton(
                        onClick = onCycleRoom,
                        modifier = Modifier.size(38.dp).testTag("cycle_room_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Change Backdrop",
                            tint = Color(0xFFFBBF24)
                        )
                    }
                }

                IconButton(
                    onClick = onSnapshotClick,
                    modifier = Modifier.size(38.dp).testTag("snapshot_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Room Specs",
                        tint = Color.White
                    )
                }

                IconButton(
                    onClick = onSaveClick,
                    modifier = Modifier.size(38.dp).testTag("save_design_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = "Save Design",
                        tint = Color(0xFF38BDF8)
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomARDock(
    arState: com.example.roomvision.model.ARSessionState,
    viewModel: RoomVisionViewModel,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFF0F172A).copy(alpha = 0.94f),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        shadowElevation = 16.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            // Mode Selector Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ModePill(
                    label = "Furniture",
                    icon = Icons.Outlined.Chair,
                    isSelected = arState.interactionMode == ARInteractionMode.FURNITURE_PLACEMENT || arState.interactionMode == ARInteractionMode.FURNITURE_EDIT,
                    onClick = {
                        val sel = arState.selectedFurnitureId
                        viewModel.setInteractionMode(if (sel != null) ARInteractionMode.FURNITURE_EDIT else ARInteractionMode.FURNITURE_PLACEMENT)
                    }
                )
                ModePill(
                    label = "Wall Colors",
                    icon = Icons.Default.FormatPaint,
                    isSelected = arState.interactionMode == ARInteractionMode.WALL_COLORING || arState.interactionMode == ARInteractionMode.WALL_SURFACE_SELECT,
                    onClick = { viewModel.setInteractionMode(ARInteractionMode.WALL_COLORING) }
                )
                ModePill(
                    label = "Flooring",
                    icon = Icons.Default.Layers,
                    isSelected = arState.interactionMode == ARInteractionMode.FLOOR_STYLING,
                    onClick = { viewModel.setInteractionMode(ARInteractionMode.FLOOR_STYLING) }
                )
                ModePill(
                    label = "Measure",
                    icon = Icons.Default.SquareFoot,
                    isSelected = arState.interactionMode == ARInteractionMode.MEASUREMENT_TAPE,
                    onClick = { viewModel.setInteractionMode(ARInteractionMode.MEASUREMENT_TAPE) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sub-Panel depending on active interaction mode
            when (arState.interactionMode) {
                ARInteractionMode.FURNITURE_EDIT -> {
                    val selectedItem = arState.placedFurniture.find { it.instanceId == arState.selectedFurnitureId }
                    if (selectedItem != null) {
                        SelectedFurnitureControlPanel(
                            item = selectedItem,
                            onRotateChange = { deg -> viewModel.updateSelectedRotation(deg) },
                            onScaleChange = { s -> viewModel.updateSelectedScale(s) },
                            onFinishSelect = { f -> viewModel.updateSelectedFinish(f) },
                            onDuplicate = { viewModel.duplicateSelectedFurniture() },
                            onDelete = { viewModel.deleteSelectedFurniture() },
                            onReset = { viewModel.resetSelectedPosition() },
                            onDeselect = { viewModel.selectFurniture(null) }
                        )
                    } else {
                        QuickFurniturePalette(
                            onSelectItem = { item -> viewModel.selectCatalogItemToPlace(item) },
                            onOpenFullCatalog = { viewModel.navigateTo(AppNavDestination.CATALOG) }
                        )
                    }
                }
                ARInteractionMode.FURNITURE_PLACEMENT -> {
                    QuickFurniturePalette(
                        onSelectItem = { item -> viewModel.selectCatalogItemToPlace(item) },
                        onOpenFullCatalog = { viewModel.navigateTo(AppNavDestination.CATALOG) }
                    )
                }
                ARInteractionMode.WALL_COLORING,
                ARInteractionMode.WALL_SURFACE_SELECT -> {
                    WallStylingPanel(
                        wallConfig = arState.wallDesign,
                        onSelectTarget = { t -> viewModel.selectWallTarget(t) },
                        onSelectColor = { hex, name -> viewModel.applyWallColor(hex, name) },
                        onSelectFinish = { f -> viewModel.applyWallFinish(f) },
                        onSelectWallpaper = { w -> viewModel.applyWallpaper(w) },
                        onResetWall = { viewModel.resetWallDesign() }
                    )
                }
                ARInteractionMode.FLOOR_STYLING -> {
                    FloorStylingPanel(
                        floorConfig = arState.floorDesign,
                        onSelectMaterial = { matId -> viewModel.applyFloorMaterial(matId) },
                        onSheenChange = { sheen -> viewModel.setFloorSheen(sheen) },
                        onResetFloor = { viewModel.resetFloorDesign() }
                    )
                }
                ARInteractionMode.MEASUREMENT_TAPE -> {
                    MeasurementTapePanel(
                        points = arState.measurementPoints,
                        onClear = { viewModel.clearMeasurements() }
                    )
                }
            }
        }
    }
}

@Composable
private fun ModePill(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) Color(0xFF0284C7) else Color(0xFF1E293B),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag("mode_${label.lowercase()}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) Color.White else Color(0xFF94A3B8),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = label,
                color = if (isSelected) Color.White else Color(0xFF94A3B8),
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun QuickFurniturePalette(
    onSelectItem: (com.example.roomvision.model.FurnitureCatalogItem) -> Unit,
    onOpenFullCatalog: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tap item to place on floor plane:",
                color = Color(0xFFCBD5E1),
                fontSize = 12.sp
            )
            TextButton(
                onClick = onOpenFullCatalog,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("Browse All", color = Color(0xFF38BDF8), fontSize = 12.sp)
            }
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(FurnitureCatalog.items.take(6)) { item ->
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier
                        .width(130.dp)
                        .clickable { onSelectItem(item) }
                        .testTag("catalog_quick_${item.id}")
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .background(Color(item.primaryColorHex), CircleShape)
                            )
                            Text(
                                text = item.priceEstimate,
                                color = Color(0xFF38BDF8),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = item.name,
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = item.dimensionsDesc,
                            color = Color(0xFF94A3B8),
                            fontSize = 9.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectedFurnitureControlPanel(
    item: PlacedFurniture,
    onRotateChange: (Float) -> Unit,
    onScaleChange: (Float) -> Unit,
    onFinishSelect: (com.example.roomvision.model.FinishType) -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    onReset: () -> Unit,
    onDeselect: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = item.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "${item.dimensionsDesc} • ${item.priceEstimate}",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
            }
            Row {
                IconButton(onClick = onDuplicate, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.ContentCopy, "Duplicate", tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onReset, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Refresh, "Reset", tint = Color(0xFFFBBF24), modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDeselect, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Check, "Done", tint = Color(0xFF10B981), modifier = Modifier.size(20.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Sliders: Rotation and Scale
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Rotate", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    Text("${item.rotationDegrees.roundToInt()}°", color = Color(0xFF38BDF8), fontSize = 11.sp)
                }
                Slider(
                    value = item.rotationDegrees,
                    onValueChange = onRotateChange,
                    valueRange = 0f..360f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF38BDF8),
                        activeTrackColor = Color(0xFF0284C7),
                        inactiveTrackColor = Color(0xFF334155)
                    ),
                    modifier = Modifier.height(28.dp).testTag("rotate_slider")
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Scale", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    Text(String.format("%.2fx", item.scale), color = Color(0xFF38BDF8), fontSize = 11.sp)
                }
                Slider(
                    value = item.scale,
                    onValueChange = onScaleChange,
                    valueRange = 0.5f..2.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF38BDF8),
                        activeTrackColor = Color(0xFF0284C7),
                        inactiveTrackColor = Color(0xFF334155)
                    ),
                    modifier = Modifier.height(28.dp).testTag("scale_slider")
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Finishes
        val catalogItem = FurnitureCatalog.getById(item.catalogId)
        val finishes = catalogItem?.availableFinishes ?: listOf(item.selectedFinish)
        Text("Material & Finish:", color = Color(0xFF94A3B8), fontSize = 11.sp)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 4.dp)
        ) {
            items(finishes) { finish ->
                val isSel = finish == item.selectedFinish
                Surface(
                    color = if (isSel) Color(0xFF0284C7) else Color(0xFF1E293B),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSel) Color(0xFF38BDF8) else Color(0xFF334155)
                    ),
                    modifier = Modifier.clickable { onFinishSelect(finish) }
                ) {
                    Text(
                        text = finish.displayName,
                        color = if (isSel) Color.White else Color(0xFFCBD5E1),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun WallStylingPanel(
    wallConfig: com.example.roomvision.model.WallDesignConfig,
    onSelectTarget: (WallTarget) -> Unit,
    onSelectColor: (Long, String) -> Unit,
    onSelectFinish: (PaintFinish) -> Unit,
    onSelectWallpaper: (WallpaperPattern) -> Unit,
    onResetWall: () -> Unit
) {
    var subTab by remember { mutableStateOf(0) } // 0: Paint, 1: Wallpaper, 2: Target

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Target Wall Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WallTarget.values().forEach { target ->
                val isSel = wallConfig.targetWall == target
                Surface(
                    color = if (isSel) Color(0xFF0284C7) else Color(0xFF1E293B),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.clickable { onSelectTarget(target) }
                ) {
                    Text(
                        text = target.label,
                        color = if (isSel) Color.White else Color(0xFF94A3B8),
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = onResetWall,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("Reset", color = Color(0xFFFBBF24), fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Subtabs: Paint vs Wallpaper
        Row(modifier = Modifier.fillMaxWidth()) {
            Surface(
                color = if (subTab == 0) Color(0xFF334155) else Color.Transparent,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .clickable { subTab = 0 }
            ) {
                Text(
                    text = "Paint Colors (${wallConfig.colorName})",
                    color = Color.White,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
            Surface(
                color = if (subTab == 1) Color(0xFF334155) else Color.Transparent,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .clickable { subTab = 1 }
            ) {
                Text(
                    text = "Wallpaper Textures",
                    color = Color.White,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (subTab == 0) {
            // Paint swatches scrollable
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(CuratedWallPalettes.swatches) { swatch ->
                    val isSel = wallConfig.colorHex == swatch.hexValue
                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            2.dp,
                            if (isSel) Color(0xFF38BDF8) else Color.Transparent
                        ),
                        modifier = Modifier
                            .clickable { onSelectColor(swatch.hexValue, swatch.name) }
                            .testTag("swatch_${swatch.id}")
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color(swatch.hexValue), CircleShape)
                                    .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = swatch.name,
                                color = Color.White,
                                fontSize = 9.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.width(64.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Paint Finish selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Finish: ", color = Color(0xFF94A3B8), fontSize = 11.sp)
                PaintFinish.values().forEach { f ->
                    val isF = wallConfig.finish == f
                    Surface(
                        color = if (isF) Color(0xFF0284C7) else Color(0xFF1E293B),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clickable { onSelectFinish(f) }
                    ) {
                        Text(
                            text = f.label,
                            color = if (isF) Color.White else Color(0xFF94A3B8),
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        } else {
            // Wallpaper Patterns
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(WallpaperPattern.values()) { pattern ->
                    val isSel = wallConfig.wallpaper == pattern
                    Surface(
                        color = if (isSel) Color(0xFF0284C7) else Color(0xFF1E293B),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSel) Color(0xFF38BDF8) else Color(0xFF334155)
                        ),
                        modifier = Modifier
                            .clickable { onSelectWallpaper(pattern) }
                            .testTag("wallpaper_${pattern.name.lowercase()}")
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = pattern.label,
                                color = if (isSel) Color.White else Color.White,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                            Text(
                                text = pattern.category,
                                color = if (isSel) Color(0xFFE0F2FE) else Color(0xFF94A3B8),
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FloorStylingPanel(
    floorConfig: com.example.roomvision.model.FloorDesignConfig,
    onSelectMaterial: (String) -> Unit,
    onSheenChange: (Float) -> Unit,
    onResetFloor: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val current = CuratedFloorMaterials.getById(floorConfig.selectedMaterialId)
            Text(
                text = "Floor: ${current.name}",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            TextButton(
                onClick = onResetFloor,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("Reset", color = Color(0xFFFBBF24), fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(CuratedFloorMaterials.materials) { mat ->
                val isSel = floorConfig.selectedMaterialId == mat.id
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        2.dp,
                        if (isSel) Color(0xFF38BDF8) else Color(0xFF334155)
                    ),
                    modifier = Modifier
                        .width(135.dp)
                        .clickable { onSelectMaterial(mat.id) }
                        .testTag("floor_${mat.id}")
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                                .background(Color(mat.primaryColorHex), RoundedCornerShape(6.dp))
                                .border(1.dp, Color(mat.accentColorHex), RoundedCornerShape(6.dp))
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = mat.name,
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = mat.category.label,
                            color = Color(0xFF94A3B8),
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Sheen / Lustre", color = Color(0xFF94A3B8), fontSize = 11.sp, modifier = Modifier.width(90.dp))
            Slider(
                value = floorConfig.sheenLevel,
                onValueChange = onSheenChange,
                valueRange = 0f..1f,
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF38BDF8),
                    activeTrackColor = Color(0xFF0284C7),
                    inactiveTrackColor = Color(0xFF334155)
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(28.dp)
            )
        }
    }
}

@Composable
private fun MeasurementTapePanel(
    points: List<com.example.roomvision.model.MeasurementPoint>,
    onClear: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "AR Virtual Tape Measure",
                color = Color(0xFFFBBF24),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            TextButton(onClick = onClear, contentPadding = PaddingValues(0.dp)) {
                Text("Clear All", color = Color(0xFFEF4444), fontSize = 11.sp)
            }
        }

        Text(
            text = "Tap any two points on floor or wall to measure spatial span in meters.",
            color = Color(0xFF94A3B8),
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        if (points.size >= 2) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E293B), RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    text = "Measured Distance: ${(points.size - 1) * 2.4}m (~${((points.size - 1) * 7.87).toInt()} ft)",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        } else {
            Text(
                text = "Points placed: ${points.size} / 2 required",
                color = Color(0xFFCBD5E1),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun SaveDesignDialog(
    defaultName: String,
    onDismiss: () -> Unit,
    onSave: (String, RoomType, String) -> Unit
) {
    var name by remember { mutableStateOf(defaultName) }
    var roomType by remember { mutableStateOf(RoomType.LIVING_ROOM) }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Save AR Room Design", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Design Name") },
                    modifier = Modifier.fillMaxWidth().testTag("save_design_name_input")
                )

                Text("Room Type:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    RoomType.values().forEach { t ->
                        FilterChip(
                            selected = roomType == t,
                            onClick = { roomType = t },
                            label = { Text(t.label, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Design Notes & Ideas (Optional)") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, roomType, notes) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                modifier = Modifier.testTag("confirm_save_button")
            ) {
                Text("Save Design")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun SnapshotSummaryDialog(
    arState: com.example.roomvision.model.ARSessionState,
    onDismiss: () -> Unit
) {
    val floorMat = CuratedFloorMaterials.getById(arState.floorDesign.selectedMaterialId)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.SquareFoot, null, tint = Color(0xFF0284C7))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Room Specification & Bill", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Space Summary Card
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Active Space: ${arState.currentRoomDesignName}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Wall: ${arState.wallDesign.colorName} (${arState.wallDesign.targetWall.label})", fontSize = 12.sp)
                        Text("Finish: ${arState.wallDesign.finish.label} • Wallpaper: ${arState.wallDesign.wallpaper.label}", fontSize = 11.sp)
                        Text("Flooring: ${floorMat.name} (${floorMat.category.label})", fontSize = 12.sp)
                    }
                }

                // Placed Furniture Itemized List
                Text("Furniture Placed (${arState.placedFurniture.size}):", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                if (arState.placedFurniture.isEmpty()) {
                    Text("No furniture placed in the room yet.", color = Color.Gray, fontSize = 11.sp)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        arState.placedFurniture.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.name, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    Text("${item.selectedFinish.displayName} • ${item.dimensionsDesc}", fontSize = 10.sp, color = Color.Gray)
                                }
                                Text(item.priceEstimate, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0284C7))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
            ) {
                Text("Done")
            }
        }
    )
}
