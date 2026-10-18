package com.example.roomvision.model

import java.util.UUID

enum class FurnitureCategory(val displayName: String) {
    SEATING("Seating"),
    TABLES("Tables"),
    BEDROOM("Bedroom"),
    LIGHTING("Lighting"),
    STORAGE("Storage"),
    DECOR("Plants & Decor")
}

enum class FurnitureModel3D {
    NORDIC_ARMCHAIR,
    MODERN_SECTIONAL_SOFA,
    LOUNGE_ACCENT_CHAIR,
    OAK_COFFEE_TABLE,
    MINIMAL_DINING_SET,
    FLOOR_ARC_LAMP,
    MONSTERA_PLANT,
    FIDDLE_LEAF_FIG,
    PLATFORM_BED,
    MODULAR_BOOKSHELF,
    MEDIA_CONSOLE
}

enum class FinishType(val displayName: String, val category: String) {
    FABRIC_CHARCOAL("Charcoal Weave", "Fabric"),
    FABRIC_CREAM("Ivory Bouclé", "Fabric"),
    FABRIC_TERRACOTTA("Terracotta Chenille", "Fabric"),
    FABRIC_SAGE("Sage Velvet", "Fabric"),
    FABRIC_INDIGO("Midnight Indigo", "Fabric"),
    LEATHER_COGNAC("Cognac Leather", "Leather"),
    LEATHER_BLACK("Espresso Leather", "Leather"),
    WOOD_NATURAL_OAK("Natural Oak", "Wood"),
    WOOD_WALNUT("Dark Walnut", "Wood"),
    WOOD_ASH("Scandinavian Ash", "Wood"),
    METAL_MATTE_BLACK("Matte Black Steel", "Metal"),
    METAL_BRUSHED_BRASS("Brushed Brass", "Metal")
}

data class FurnitureCatalogItem(
    val id: String,
    val name: String,
    val category: FurnitureCategory,
    val model3D: FurnitureModel3D,
    val dimensionsDesc: String, // e.g. "86cm W × 82cm D × 78cm H"
    val defaultWidthMeters: Float,
    val defaultDepthMeters: Float,
    val defaultHeightMeters: Float,
    val priceEstimate: String,
    val description: String,
    val availableFinishes: List<FinishType>,
    val defaultFinish: FinishType,
    val primaryColorHex: Long = 0xFF475569
)

data class PlacedFurniture(
    val instanceId: String = UUID.randomUUID().toString(),
    val catalogId: String,
    val name: String,
    val category: FurnitureCategory,
    val model3D: FurnitureModel3D,
    var positionX: Float, // Normalized X position on floor plane (-1.0f to 1.0f)
    var positionY: Float, // Normalized Y/Depth position on floor plane (-1.0f to 1.0f)
    var rotationDegrees: Float = 0f, // 0 to 360 degrees
    var scale: Float = 1.0f, // Clamped between 0.5f and 2.5f
    var elevationMeters: Float = 0f,
    var selectedFinish: FinishType,
    var customColorHex: Long? = null,
    val dimensionsDesc: String,
    val priceEstimate: String,
    var isSelected: Boolean = false
) {
    fun clone(): PlacedFurniture {
        return copy(
            instanceId = UUID.randomUUID().toString(),
            positionX = (positionX + 0.15f).coerceIn(-0.8f, 0.8f),
            positionY = (positionY + 0.1f).coerceIn(-0.8f, 0.8f),
            isSelected = true
        )
    }
}
