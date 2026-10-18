package com.example.roomvision.model

enum class SurfaceType(val displayName: String, val isVertical: Boolean) {
    FLOOR_PLANE("Floor Surface", false),
    WALL_BACK("Back Wall", true),
    WALL_LEFT("Left Wall", true),
    WALL_RIGHT("Right Wall", true),
    CEILING_PLANE("Ceiling", false)
}

data class SurfaceCandidate(
    val id: String,
    val type: SurfaceType,
    val label: String,
    val isSelected: Boolean = false,
    val confidence: Float = 0.95f,
    val widthMeters: Float = 3.6f,
    val heightMeters: Float = 2.8f,
    val areaSqMeters: Float = 10.08f
)
