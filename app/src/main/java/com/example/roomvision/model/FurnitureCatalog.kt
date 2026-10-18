package com.example.roomvision.model

object FurnitureCatalog {
    val items: List<FurnitureCatalogItem> = listOf(
        FurnitureCatalogItem(
            id = "nordic-chair-01",
            name = "Oslo Lounge Armchair",
            category = FurnitureCategory.SEATING,
            model3D = FurnitureModel3D.NORDIC_ARMCHAIR,
            dimensionsDesc = "84cm W × 82cm D × 78cm H",
            defaultWidthMeters = 0.84f,
            defaultDepthMeters = 0.82f,
            defaultHeightMeters = 0.78f,
            priceEstimate = "$490",
            description = "Sculptural Scandinavian accent armchair with curved solid oak armrests and textured bouclé cushions.",
            availableFinishes = listOf(
                FinishType.FABRIC_CREAM,
                FinishType.FABRIC_CHARCOAL,
                FinishType.FABRIC_TERRACOTTA,
                FinishType.FABRIC_SAGE,
                FinishType.LEATHER_COGNAC
            ),
            defaultFinish = FinishType.FABRIC_CREAM,
            primaryColorHex = 0xFFF5F2EB
        ),
        FurnitureCatalogItem(
            id = "sectional-sofa-02",
            name = "Copenhagen 3-Seater Sofa",
            category = FurnitureCategory.SEATING,
            model3D = FurnitureModel3D.MODERN_SECTIONAL_SOFA,
            dimensionsDesc = "220cm W × 95cm D × 80cm H",
            defaultWidthMeters = 2.20f,
            defaultDepthMeters = 0.95f,
            defaultHeightMeters = 0.80f,
            priceEstimate = "$1,350",
            description = "Low-profile architectural modular sofa upholstered in tailored linen weave with deep seating.",
            availableFinishes = listOf(
                FinishType.FABRIC_CHARCOAL,
                FinishType.FABRIC_CREAM,
                FinishType.FABRIC_INDIGO,
                FinishType.FABRIC_SAGE,
                FinishType.LEATHER_BLACK
            ),
            defaultFinish = FinishType.FABRIC_CHARCOAL,
            primaryColorHex = 0xFF334155
        ),
        FurnitureCatalogItem(
            id = "oak-coffee-table-03",
            name = "Koto Low Oak Coffee Table",
            category = FurnitureCategory.TABLES,
            model3D = FurnitureModel3D.OAK_COFFEE_TABLE,
            dimensionsDesc = "110cm W × 65cm D × 42cm H",
            defaultWidthMeters = 1.10f,
            defaultDepthMeters = 0.65f,
            defaultHeightMeters = 0.42f,
            priceEstimate = "$380",
            description = "Solid white oak minimalist coffee table with soft beveled perimeter and pillared legs.",
            availableFinishes = listOf(
                FinishType.WOOD_NATURAL_OAK,
                FinishType.WOOD_WALNUT,
                FinishType.WOOD_ASH
            ),
            defaultFinish = FinishType.WOOD_NATURAL_OAK,
            primaryColorHex = 0xFFD4A373
        ),
        FurnitureCatalogItem(
            id = "minimal-dining-04",
            name = "Kyoto 4-Seat Dining Set",
            category = FurnitureCategory.TABLES,
            model3D = FurnitureModel3D.MINIMAL_DINING_SET,
            dimensionsDesc = "140cm W × 90cm D × 75cm H",
            defaultWidthMeters = 1.40f,
            defaultDepthMeters = 0.90f,
            defaultHeightMeters = 0.75f,
            priceEstimate = "$890",
            description = "Japandi dining table with matching cantilever chairs in rich walnut and matte black joinery.",
            availableFinishes = listOf(
                FinishType.WOOD_WALNUT,
                FinishType.WOOD_NATURAL_OAK,
                FinishType.METAL_MATTE_BLACK
            ),
            defaultFinish = FinishType.WOOD_WALNUT,
            primaryColorHex = 0xFF58311F
        ),
        FurnitureCatalogItem(
            id = "arc-lamp-05",
            name = "Arco Brushed Brass Floor Lamp",
            category = FurnitureCategory.LIGHTING,
            model3D = FurnitureModel3D.FLOOR_ARC_LAMP,
            dimensionsDesc = "45cm W × 110cm D × 195cm H",
            defaultWidthMeters = 0.45f,
            defaultDepthMeters = 1.10f,
            defaultHeightMeters = 1.95f,
            priceEstimate = "$260",
            description = "Cantilevered sweeping brass arch with marble ballast base for warm ambient reading light.",
            availableFinishes = listOf(
                FinishType.METAL_BRUSHED_BRASS,
                FinishType.METAL_MATTE_BLACK
            ),
            defaultFinish = FinishType.METAL_BRUSHED_BRASS,
            primaryColorHex = 0xFFE0A96D
        ),
        FurnitureCatalogItem(
            id = "fiddle-fig-06",
            name = "Fiddle Leaf Fig & Ceramic Pot",
            category = FurnitureCategory.DECOR,
            model3D = FurnitureModel3D.FIDDLE_LEAF_FIG,
            dimensionsDesc = "55cm W × 55cm D × 160cm H",
            defaultWidthMeters = 0.55f,
            defaultDepthMeters = 0.55f,
            defaultHeightMeters = 1.60f,
            priceEstimate = "$140",
            description = "Living statement indoor tree in handcrafted fluted ceramic planter.",
            availableFinishes = listOf(
                FinishType.FABRIC_CREAM,
                FinishType.METAL_MATTE_BLACK,
                FinishType.FABRIC_TERRACOTTA
            ),
            defaultFinish = FinishType.FABRIC_CREAM,
            primaryColorHex = 0xFF2D6A4F
        ),
        FurnitureCatalogItem(
            id = "monstera-plant-07",
            name = "Monstera Deliciosa Planter",
            category = FurnitureCategory.DECOR,
            model3D = FurnitureModel3D.MONSTERA_PLANT,
            dimensionsDesc = "60cm W × 60cm D × 90cm H",
            defaultWidthMeters = 0.60f,
            defaultDepthMeters = 0.60f,
            defaultHeightMeters = 0.90f,
            priceEstimate = "$95",
            description = "Vibrant glossy fenestrated monstera foliage in elevated tripod planter.",
            availableFinishes = listOf(
                FinishType.FABRIC_TERRACOTTA,
                FinishType.WOOD_NATURAL_OAK
            ),
            defaultFinish = FinishType.FABRIC_TERRACOTTA,
            primaryColorHex = 0xFF40916C
        ),
        FurnitureCatalogItem(
            id = "modular-shelf-08",
            name = "Stilt Open Modular Bookshelf",
            category = FurnitureCategory.STORAGE,
            model3D = FurnitureModel3D.MODULAR_BOOKSHELF,
            dimensionsDesc = "120cm W × 38cm D × 185cm H",
            defaultWidthMeters = 1.20f,
            defaultDepthMeters = 0.38f,
            defaultHeightMeters = 1.85f,
            priceEstimate = "$680",
            description = "Architectural tiered open display shelving in black metal frame and walnut shelves.",
            availableFinishes = listOf(
                FinishType.WOOD_WALNUT,
                FinishType.WOOD_NATURAL_OAK,
                FinishType.METAL_MATTE_BLACK
            ),
            defaultFinish = FinishType.WOOD_WALNUT,
            primaryColorHex = 0xFF3D2619
        ),
        FurnitureCatalogItem(
            id = "platform-bed-09",
            name = "Kyoto Queen Platform Bed",
            category = FurnitureCategory.BEDROOM,
            model3D = FurnitureModel3D.PLATFORM_BED,
            dimensionsDesc = "170cm W × 215cm D × 88cm H",
            defaultWidthMeters = 1.70f,
            defaultDepthMeters = 2.15f,
            defaultHeightMeters = 0.88f,
            priceEstimate = "$1,450",
            description = "Floating low platform bed frame with integrated upholstered headboard in warm oatmeal bouclé.",
            availableFinishes = listOf(
                FinishType.WOOD_NATURAL_OAK,
                FinishType.WOOD_WALNUT,
                FinishType.FABRIC_CREAM
            ),
            defaultFinish = FinishType.WOOD_NATURAL_OAK,
            primaryColorHex = 0xFFD4A373
        ),
        FurnitureCatalogItem(
            id = "media-console-10",
            name = "Linea Fluted Media Console",
            category = FurnitureCategory.STORAGE,
            model3D = FurnitureModel3D.MEDIA_CONSOLE,
            dimensionsDesc = "180cm W × 45cm D × 52cm H",
            defaultWidthMeters = 1.80f,
            defaultDepthMeters = 0.45f,
            defaultHeightMeters = 0.52f,
            priceEstimate = "$720",
            description = "Tambour fluted sliding doors with soft cable pass-throughs and brass capped stilt legs.",
            availableFinishes = listOf(
                FinishType.WOOD_WALNUT,
                FinishType.WOOD_NATURAL_OAK,
                FinishType.METAL_MATTE_BLACK
            ),
            defaultFinish = FinishType.WOOD_WALNUT,
            primaryColorHex = 0xFF4A3525
        )
    )

    fun getById(id: String): FurnitureCatalogItem? = items.find { it.id == id }
}
