package com.example.roomvision.data

import com.example.roomvision.model.CuratedFloorMaterials
import com.example.roomvision.model.CuratedWallPalettes
import com.example.roomvision.model.FinishType
import com.example.roomvision.model.FloorDesignConfig
import com.example.roomvision.model.FurnitureCategory
import com.example.roomvision.model.FurnitureModel3D
import com.example.roomvision.model.PaintFinish
import com.example.roomvision.model.PlacedFurniture
import com.example.roomvision.model.RoomDesignRecord
import com.example.roomvision.model.RoomType
import com.example.roomvision.model.WallDesignConfig
import com.example.roomvision.model.WallTarget
import com.example.roomvision.model.WallpaperPattern
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class RoomDesignRepository(private val dao: RoomDesignDao) {

    val allDesigns: Flow<List<RoomDesignRecord>> = dao.getAllDesigns().map { list ->
        list.map { entityToRecord(it) }
    }

    suspend fun getDesignById(id: String): RoomDesignRecord? {
        val entity = dao.getDesignById(id) ?: return null
        return entityToRecord(entity)
    }

    suspend fun saveDesign(record: RoomDesignRecord) {
        dao.insertDesign(recordToEntity(record))
    }

    suspend fun updateDesign(record: RoomDesignRecord) {
        dao.updateDesign(recordToEntity(record))
    }

    suspend fun deleteDesign(id: String) {
        dao.deleteDesignById(id)
    }

    suspend fun duplicateDesign(record: RoomDesignRecord) {
        val duplicated = record.copy(
            designId = UUID.randomUUID().toString(),
            name = "${record.name} (Copy)",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        saveDesign(duplicated)
    }

    suspend fun seedInitialDesignsIfEmpty() {
        val sample1 = RoomDesignRecord(
            designId = "sample-nordic-living",
            name = "Nordic Serenity Living Room",
            roomType = RoomType.LIVING_ROOM,
            furniture = listOf(
                PlacedFurniture(
                    instanceId = "sofa-1",
                    catalogId = "sectional-sofa-02",
                    name = "Copenhagen 3-Seater Sofa",
                    category = FurnitureCategory.SEATING,
                    model3D = FurnitureModel3D.MODERN_SECTIONAL_SOFA,
                    positionX = -0.15f,
                    positionY = 0.15f,
                    rotationDegrees = 15f,
                    scale = 1.05f,
                    selectedFinish = FinishType.FABRIC_CHARCOAL,
                    dimensionsDesc = "220cm W × 95cm D × 80cm H",
                    priceEstimate = "$1,350"
                ),
                PlacedFurniture(
                    instanceId = "chair-1",
                    catalogId = "nordic-chair-01",
                    name = "Oslo Lounge Armchair",
                    category = FurnitureCategory.SEATING,
                    model3D = FurnitureModel3D.NORDIC_ARMCHAIR,
                    positionX = 0.45f,
                    positionY = 0.25f,
                    rotationDegrees = 320f,
                    scale = 0.95f,
                    selectedFinish = FinishType.FABRIC_CREAM,
                    dimensionsDesc = "84cm W × 82cm D × 78cm H",
                    priceEstimate = "$490"
                ),
                PlacedFurniture(
                    instanceId = "table-1",
                    catalogId = "oak-coffee-table-03",
                    name = "Koto Low Oak Coffee Table",
                    category = FurnitureCategory.TABLES,
                    model3D = FurnitureModel3D.OAK_COFFEE_TABLE,
                    positionX = 0.05f,
                    positionY = 0.40f,
                    rotationDegrees = 15f,
                    scale = 1.0f,
                    selectedFinish = FinishType.WOOD_NATURAL_OAK,
                    dimensionsDesc = "110cm W × 65cm D × 42cm H",
                    priceEstimate = "$380"
                ),
                PlacedFurniture(
                    instanceId = "plant-1",
                    catalogId = "fiddle-fig-06",
                    name = "Fiddle Leaf Fig & Ceramic Pot",
                    category = FurnitureCategory.DECOR,
                    model3D = FurnitureModel3D.FIDDLE_LEAF_FIG,
                    positionX = -0.65f,
                    positionY = -0.1f,
                    rotationDegrees = 0f,
                    scale = 1.1f,
                    selectedFinish = FinishType.FABRIC_CREAM,
                    dimensionsDesc = "55cm W × 55cm D × 160cm H",
                    priceEstimate = "$140"
                )
            ),
            wallDesign = WallDesignConfig(
                targetWall = WallTarget.BACK_WALL,
                colorHex = 0xFF99A89E,
                colorName = "Nordic Sage",
                finish = PaintFinish.EGGSHELL,
                wallpaper = WallpaperPattern.NONE
            ),
            floorDesign = FloorDesignConfig(
                selectedMaterialId = "oak-plank-01",
                patternScale = 1.0f,
                sheenLevel = 0.3f
            ),
            estimatedBudgetTotal = "$2,360",
            notes = "Minimalist natural wood and sage accents with ample natural light."
        )

        val sample2 = RoomDesignRecord(
            designId = "sample-japandi-office",
            name = "Japandi Focus Sanctuary",
            roomType = RoomType.HOME_OFFICE,
            furniture = listOf(
                PlacedFurniture(
                    instanceId = "chair-2",
                    catalogId = "nordic-chair-01",
                    name = "Oslo Lounge Armchair",
                    category = FurnitureCategory.SEATING,
                    model3D = FurnitureModel3D.NORDIC_ARMCHAIR,
                    positionX = 0.0f,
                    positionY = 0.25f,
                    rotationDegrees = 0f,
                    scale = 1.0f,
                    selectedFinish = FinishType.LEATHER_COGNAC,
                    dimensionsDesc = "84cm W × 82cm D × 78cm H",
                    priceEstimate = "$490"
                ),
                PlacedFurniture(
                    instanceId = "shelf-1",
                    catalogId = "modular-shelf-08",
                    name = "Stilt Open Modular Bookshelf",
                    category = FurnitureCategory.STORAGE,
                    model3D = FurnitureModel3D.MODULAR_BOOKSHELF,
                    positionX = -0.45f,
                    positionY = -0.2f,
                    rotationDegrees = 10f,
                    scale = 0.95f,
                    selectedFinish = FinishType.WOOD_WALNUT,
                    dimensionsDesc = "120cm W × 38cm D × 185cm H",
                    priceEstimate = "$680"
                ),
                PlacedFurniture(
                    instanceId = "lamp-1",
                    catalogId = "arc-lamp-05",
                    name = "Arco Brushed Brass Floor Lamp",
                    category = FurnitureCategory.LIGHTING,
                    model3D = FurnitureModel3D.FLOOR_ARC_LAMP,
                    positionX = 0.55f,
                    positionY = 0.0f,
                    rotationDegrees = 200f,
                    scale = 1.0f,
                    selectedFinish = FinishType.METAL_BRUSHED_BRASS,
                    dimensionsDesc = "45cm W × 110cm D × 195cm H",
                    priceEstimate = "$260"
                )
            ),
            wallDesign = WallDesignConfig(
                targetWall = WallTarget.BACK_WALL,
                colorHex = 0xFFD8CFBE,
                colorName = "Oatmeal Canvas",
                finish = PaintFinish.MATTE,
                wallpaper = WallpaperPattern.WOOD_SLATS
            ),
            floorDesign = FloorDesignConfig(
                selectedMaterialId = "walnut-herringbone-02",
                patternScale = 1.0f,
                sheenLevel = 0.4f
            ),
            estimatedBudgetTotal = "$1,430",
            notes = "Acoustic oak slat accent wall with dark walnut herringbone flooring."
        )

        saveDesign(sample1)
        saveDesign(sample2)
    }

    private fun recordToEntity(record: RoomDesignRecord): RoomDesignEntity {
        // Serialize furniture list
        val furnArray = JSONArray()
        record.furniture.forEach { f ->
            val obj = JSONObject().apply {
                put("instanceId", f.instanceId)
                put("catalogId", f.catalogId)
                put("name", f.name)
                put("category", f.category.name)
                put("model3D", f.model3D.name)
                put("positionX", f.positionX.toDouble())
                put("positionY", f.positionY.toDouble())
                put("rotationDegrees", f.rotationDegrees.toDouble())
                put("scale", f.scale.toDouble())
                put("elevationMeters", f.elevationMeters.toDouble())
                put("selectedFinish", f.selectedFinish.name)
                if (f.customColorHex != null) put("customColorHex", f.customColorHex!!)
                put("dimensionsDesc", f.dimensionsDesc)
                put("priceEstimate", f.priceEstimate)
            }
            furnArray.put(obj)
        }

        // Serialize wall config
        val wallObj = JSONObject().apply {
            put("targetWall", record.wallDesign.targetWall.name)
            put("colorHex", record.wallDesign.colorHex)
            put("colorName", record.wallDesign.colorName)
            put("finish", record.wallDesign.finish.name)
            put("wallpaper", record.wallDesign.wallpaper.name)
            put("wallpaperScale", record.wallDesign.wallpaperScale.toDouble())
            put("opacity", record.wallDesign.opacity.toDouble())
        }

        // Serialize floor config
        val floorObj = JSONObject().apply {
            put("selectedMaterialId", record.floorDesign.selectedMaterialId)
            put("rotationAngle", record.floorDesign.rotationAngle.toDouble())
            put("patternScale", record.floorDesign.patternScale.toDouble())
            put("sheenLevel", record.floorDesign.sheenLevel.toDouble())
        }

        return RoomDesignEntity(
            id = record.designId,
            name = record.name,
            roomType = record.roomType.name,
            furnitureJson = furnArray.toString(),
            wallDesignJson = wallObj.toString(),
            floorDesignJson = floorObj.toString(),
            estimatedBudget = record.estimatedBudgetTotal,
            notes = record.notes,
            createdAt = record.createdAt,
            updatedAt = record.updatedAt
        )
    }

    private fun entityToRecord(entity: RoomDesignEntity): RoomDesignRecord {
        val furnitureList = mutableListOf<PlacedFurniture>()
        try {
            val furnArray = JSONArray(entity.furnitureJson)
            for (i in 0 until furnArray.length()) {
                val obj = furnArray.getJSONObject(i)
                val cat = try {
                    FurnitureCategory.valueOf(obj.getString("category"))
                } catch (e: Exception) {
                    FurnitureCategory.SEATING
                }
                val model = try {
                    FurnitureModel3D.valueOf(obj.getString("model3D"))
                } catch (e: Exception) {
                    FurnitureModel3D.NORDIC_ARMCHAIR
                }
                val finish = try {
                    FinishType.valueOf(obj.getString("selectedFinish"))
                } catch (e: Exception) {
                    FinishType.FABRIC_CREAM
                }
                furnitureList.add(
                    PlacedFurniture(
                        instanceId = obj.optString("instanceId", UUID.randomUUID().toString()),
                        catalogId = obj.optString("catalogId", ""),
                        name = obj.optString("name", "Furniture"),
                        category = cat,
                        model3D = model,
                        positionX = obj.optDouble("positionX", 0.0).toFloat(),
                        positionY = obj.optDouble("positionY", 0.0).toFloat(),
                        rotationDegrees = obj.optDouble("rotationDegrees", 0.0).toFloat(),
                        scale = obj.optDouble("scale", 1.0).toFloat(),
                        elevationMeters = obj.optDouble("elevationMeters", 0.0).toFloat(),
                        selectedFinish = finish,
                        customColorHex = if (obj.has("customColorHex")) obj.getLong("customColorHex") else null,
                        dimensionsDesc = obj.optString("dimensionsDesc", ""),
                        priceEstimate = obj.optString("priceEstimate", "$0")
                    )
                )
            }
        } catch (e: Exception) {
            // fallback
        }

        val wallConfig = try {
            val obj = JSONObject(entity.wallDesignJson)
            WallDesignConfig(
                targetWall = WallTarget.valueOf(obj.optString("targetWall", WallTarget.BACK_WALL.name)),
                colorHex = obj.optLong("colorHex", 0xFFE2E8F0),
                colorName = obj.optString("colorName", "Alabaster"),
                finish = PaintFinish.valueOf(obj.optString("finish", PaintFinish.EGGSHELL.name)),
                wallpaper = WallpaperPattern.valueOf(obj.optString("wallpaper", WallpaperPattern.NONE.name)),
                wallpaperScale = obj.optDouble("wallpaperScale", 1.0).toFloat(),
                opacity = obj.optDouble("opacity", 0.9).toFloat()
            )
        } catch (e: Exception) {
            WallDesignConfig()
        }

        val floorConfig = try {
            val obj = JSONObject(entity.floorDesignJson)
            FloorDesignConfig(
                selectedMaterialId = obj.optString("selectedMaterialId", "oak-plank-01"),
                rotationAngle = obj.optDouble("rotationAngle", 0.0).toFloat(),
                patternScale = obj.optDouble("patternScale", 1.0).toFloat(),
                sheenLevel = obj.optDouble("sheenLevel", 0.35).toFloat()
            )
        } catch (e: Exception) {
            FloorDesignConfig()
        }

        val roomType = try {
            RoomType.valueOf(entity.roomType)
        } catch (e: Exception) {
            RoomType.LIVING_ROOM
        }

        return RoomDesignRecord(
            designId = entity.id,
            name = entity.name,
            roomType = roomType,
            furniture = furnitureList,
            wallDesign = wallConfig,
            floorDesign = floorConfig,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            estimatedBudgetTotal = entity.estimatedBudget,
            notes = entity.notes
        )
    }
}
