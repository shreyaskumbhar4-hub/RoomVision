package com.example.roomvision.model

enum class WallTarget(val label: String) {
    BACK_WALL("Back Wall"),
    LEFT_WALL("Left Wall"),
    RIGHT_WALL("Right Wall"),
    ALL_WALLS("All Walls")
}

enum class PaintFinish(val label: String, val glossiness: Float) {
    MATTE("Matte", 0.05f),
    EGGSHELL("Eggshell", 0.20f),
    SATIN("Satin", 0.45f),
    SEMI_GLOSS("Semi-Gloss", 0.70f)
}

enum class WallpaperPattern(val label: String, val category: String) {
    NONE("Solid Paint", "Solid"),
    LINEN_TEXTURE("Natural Linen Weave", "Textured"),
    WOOD_SLATS("Acoustic Oak Slats", "Architectural"),
    GEOMETRIC_ARCHES("Art Deco Arches", "Pattern"),
    BOTANICAL_JUNGLE("Moody Botanical", "Nature"),
    HERRINGBONE_BRICK("Exposed Lime Brick", "Rustic"),
    TERRAZZO_STONE("Micro Terrazzo", "Stone")
}

data class WallColorSwatch(
    val id: String,
    val name: String,
    val hexValue: Long,
    val collection: String,
    val hexString: String
)

data class WallDesignConfig(
    val targetWall: WallTarget = WallTarget.BACK_WALL,
    val colorHex: Long = 0xFFE2E8F0,
    val colorName: String = "Nordic Alabaster",
    val finish: PaintFinish = PaintFinish.EGGSHELL,
    val wallpaper: WallpaperPattern = WallpaperPattern.NONE,
    val wallpaperScale: Float = 1.0f,
    val opacity: Float = 0.90f
)

object CuratedWallPalettes {
    val swatches: List<WallColorSwatch> = listOf(
        // Warm Neutrals
        WallColorSwatch("wn-1", "Alabaster Whisper", 0xFFF8F7F4, "Warm Neutrals", "#F8F7F4"),
        WallColorSwatch("wn-2", "Warm Greige", 0xFFE5DFD7, "Warm Neutrals", "#E5DFD7"),
        WallColorSwatch("wn-3", "Oatmeal Canvas", 0xFFD8CFBE, "Warm Neutrals", "#D8CFBE"),
        WallColorSwatch("wn-4", "Smoked Taupe", 0xFF9E9283, "Warm Neutrals", "#9E9283"),
        
        // Earthy & Botanical
        WallColorSwatch("eb-1", "Nordic Sage", 0xFF99A89E, "Earthy Sage", "#99A89E"),
        WallColorSwatch("eb-2", "Eucalyptus Mist", 0xFF6C7C71, "Earthy Sage", "#6C7C71"),
        WallColorSwatch("eb-3", "Deep Olive", 0xFF434E43, "Earthy Sage", "#434E43"),
        WallColorSwatch("eb-4", "Terracotta Clay", 0xFFCB6D51, "Earthy Terracotta", "#CB6D51"),
        WallColorSwatch("eb-5", "Desert Sienna", 0xFFA64B2A, "Earthy Terracotta", "#A64B2A"),

        // Scandinavian & Crisp
        WallColorSwatch("sc-1", "Polar White", 0xFFFAFAFA, "Scandinavian", "#FAFAFA"),
        WallColorSwatch("sc-2", "Foggy Morning", 0xFFDFE4E6, "Scandinavian", "#DFE4E6"),
        WallColorSwatch("sc-3", "Slate Blue Haze", 0xFF94A3B8, "Scandinavian", "#94A3B8"),

        // Moody & Dramatic
        WallColorSwatch("md-1", "Midnight Velvet", 0xFF1E293B, "Moody Luxe", "#1E293B"),
        WallColorSwatch("md-2", "Prussian Teal", 0xFF134E4A, "Moody Luxe", "#134E4A"),
        WallColorSwatch("md-3", "Charcoal Noir", 0xFF18181B, "Moody Luxe", "#18181B"),
        WallColorSwatch("md-4", "Plum Royale", 0xFF4A1D37, "Moody Luxe", "#4A1D37")
    )

    fun getSwatchById(id: String) = swatches.find { it.id == id }
}
