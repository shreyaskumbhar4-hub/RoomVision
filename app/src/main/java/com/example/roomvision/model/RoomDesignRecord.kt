package com.example.roomvision.model

import java.util.UUID

enum class RoomType(val label: String, val defaultIconName: String) {
    LIVING_ROOM("Living Room", "Weekend Lounge"),
    BEDROOM("Bedroom", "Restful Sanctuary"),
    DINING_ROOM("Dining Room", "Entertaining & Feast"),
    HOME_OFFICE("Home Office", "Focus Studio"),
    STUDIO_APARTMENT("Open Studio", "Compact Living")
}

data class RoomDesignRecord(
    val designId: String = UUID.randomUUID().toString(),
    val name: String,
    val roomType: RoomType = RoomType.LIVING_ROOM,
    val furniture: List<PlacedFurniture> = emptyList(),
    val wallDesign: WallDesignConfig = WallDesignConfig(),
    val floorDesign: FloorDesignConfig = FloorDesignConfig(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val estimatedBudgetTotal: String = "$0",
    val notes: String = ""
)

enum class TrackingQuality {
    INITIALIZING,
    SCANNING,
    GOOD_TRACKING,
    LIMITED_LIGHTING,
    EXCESSIVE_MOTION
}

enum class ARInteractionMode {
    FURNITURE_PLACEMENT,
    FURNITURE_EDIT,
    WALL_SURFACE_SELECT,
    WALL_COLORING,
    FLOOR_STYLING,
    MEASUREMENT_TAPE
}

data class MeasurementPoint(
    val x: Float,
    val y: Float,
    val label: String
)

data class ARSessionState(
    val isCameraPermissionGranted: Boolean = false,
    val isRealCameraActive: Boolean = false,
    val trackingQuality: TrackingQuality = TrackingQuality.GOOD_TRACKING,
    val surfaces: List<SurfaceCandidate> = emptyList(),
    val selectedSurfaceId: String? = null,
    val interactionMode: ARInteractionMode = ARInteractionMode.FURNITURE_PLACEMENT,
    val placedFurniture: List<PlacedFurniture> = emptyList(),
    val selectedFurnitureId: String? = null,
    val wallDesign: WallDesignConfig = WallDesignConfig(),
    val floorDesign: FloorDesignConfig = FloorDesignConfig(),
    val measurementPoints: List<MeasurementPoint> = emptyList(),
    val currentRoomDesignName: String = "My AR Space",
    val simulationRoomIndex: Int = 0 // 0: Sunlit Loft, 1: Minimalist Studio, 2: Warm Japandi Room
)
