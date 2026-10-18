package com.example.roomvision.model

enum class FloorCategory(val label: String) {
    HARDWOOD("Hardwood"),
    STONE_TILE("Stone & Tile"),
    CONCRETE("Concrete"),
    CARPET_RUG("Carpet & Rugs")
}

data class FloorMaterialOption(
    val id: String,
    val name: String,
    val category: FloorCategory,
    val primaryColorHex: Long,
    val accentColorHex: Long,
    val patternType: String, // "PLANK", "HERRINGBONE", "CONCRETE", "MARBLE", "HEX_TILE", "CARPET"
    val description: String,
    val finishSheen: String = "Satin"
)

data class FloorDesignConfig(
    val selectedMaterialId: String = "oak-plank-01",
    val rotationAngle: Float = 0f,
    val patternScale: Float = 1.0f,
    val sheenLevel: Float = 0.35f
)

object CuratedFloorMaterials {
    val materials: List<FloorMaterialOption> = listOf(
        FloorMaterialOption(
            id = "oak-plank-01",
            name = "White Scandinavian Oak",
            category = FloorCategory.HARDWOOD,
            primaryColorHex = 0xFFD8C3A5,
            accentColorHex = 0xFFBFA07A,
            patternType = "PLANK",
            description = "Wide-plank brushed European white oak with subtle matte oil finish."
        ),
        FloorMaterialOption(
            id = "walnut-herringbone-02",
            name = "Dark Walnut Herringbone",
            category = FloorCategory.HARDWOOD,
            primaryColorHex = 0xFF583B2B,
            accentColorHex = 0xFF3D2619,
            patternType = "HERRINGBONE",
            description = "Sophisticated chevron parquet in rich roasted walnut with satin lustre."
        ),
        FloorMaterialOption(
            id = "honed-concrete-03",
            name = "Polished Industrial Concrete",
            category = FloorCategory.CONCRETE,
            primaryColorHex = 0xFF94A3B8,
            accentColorHex = 0xFF64748B,
            patternType = "CONCRETE",
            description = "Seamless minimalist aggregate concrete floor with soft architectural sheen."
        ),
        FloorMaterialOption(
            id = "calacatta-marble-04",
            name = "Calacatta Gold Marble",
            category = FloorCategory.STONE_TILE,
            primaryColorHex = 0xFFF8FAFC,
            accentColorHex = 0xFFCBD5E1,
            patternType = "MARBLE",
            description = "Large-format Italian white marble with warm grey and subtle brass veining."
        ),
        FloorMaterialOption(
            id = "terracotta-hex-05",
            name = "Artisan Terracotta Hexagon",
            category = FloorCategory.STONE_TILE,
            primaryColorHex = 0xFFC26D53,
            accentColorHex = 0xFF9C4931,
            patternType = "HEX_TILE",
            description = "Handmade Mediterranean terracotta tiles with natural shade variations."
        ),
        FloorMaterialOption(
            id = "berber-carpet-06",
            name = "Oatmeal Loop Berber Carpet",
            category = FloorCategory.CARPET_RUG,
            primaryColorHex = 0xFFE2D9CD,
            accentColorHex = 0xFFC7BBAA,
            patternType = "CARPET",
            description = "Plush 100% natural wool textured loop carpet for ultimate warmth and acoustic comfort."
        )
    )

    fun getById(id: String): FloorMaterialOption =
        materials.find { it.id == id } ?: materials.first()
}
