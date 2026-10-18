package com.example.roomvision.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomvision.data.RoomDesignRepository
import com.example.roomvision.data.RoomVisionDatabase
import com.example.roomvision.model.ARInteractionMode
import com.example.roomvision.model.ARSessionState
import com.example.roomvision.model.CuratedFloorMaterials
import com.example.roomvision.model.CuratedWallPalettes
import com.example.roomvision.model.FinishType
import com.example.roomvision.model.FloorDesignConfig
import com.example.roomvision.model.FurnitureCatalog
import com.example.roomvision.model.FurnitureCatalogItem
import com.example.roomvision.model.MeasurementPoint
import com.example.roomvision.model.PaintFinish
import com.example.roomvision.model.PlacedFurniture
import com.example.roomvision.model.RoomDesignRecord
import com.example.roomvision.model.RoomType
import com.example.roomvision.model.SurfaceCandidate
import com.example.roomvision.model.SurfaceType
import com.example.roomvision.model.TrackingQuality
import com.example.roomvision.model.WallDesignConfig
import com.example.roomvision.model.WallTarget
import com.example.roomvision.model.WallpaperPattern
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppNavDestination(val title: String) {
    HOME("Home"),
    AR_STUDIO("Design in AR"),
    CATALOG("Catalogue"),
    MY_DESIGNS("My Designs"),
    PALETTES("Materials")
}

class RoomVisionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RoomDesignRepository =
        RoomDesignRepository(RoomVisionDatabase.getDatabase(application).roomDesignDao())

    val savedDesigns: StateFlow<List<RoomDesignRecord>> = repository.allDesigns
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentScreen = MutableStateFlow(AppNavDestination.HOME)
    val currentScreen: StateFlow<AppNavDestination> = _currentScreen.asStateFlow()

    private val _arState = MutableStateFlow(
        ARSessionState(
            surfaces = listOf(
                SurfaceCandidate("floor-1", SurfaceType.FLOOR_PLANE, "Main Floor Plane (Detected)", true),
                SurfaceCandidate("wall-back", SurfaceType.WALL_BACK, "Back Wall Candidate (Detected)", false),
                SurfaceCandidate("wall-left", SurfaceType.WALL_LEFT, "Left Wall Candidate (Detected)", false),
                SurfaceCandidate("wall-right", SurfaceType.WALL_RIGHT, "Right Wall Candidate (Detected)", false)
            ),
            trackingQuality = TrackingQuality.GOOD_TRACKING
        )
    )
    val arState: StateFlow<ARSessionState> = _arState.asStateFlow()

    private val _selectedCatalogItemForPlacement = MutableStateFlow<FurnitureCatalogItem?>(null)
    val selectedCatalogItemForPlacement: StateFlow<FurnitureCatalogItem?> = _selectedCatalogItemForPlacement.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedInitialDesignsIfEmpty()
        }
    }

    fun navigateTo(destination: AppNavDestination) {
        _currentScreen.value = destination
    }

    fun setCameraPermissionGranted(granted: Boolean) {
        _arState.update { it.copy(isCameraPermissionGranted = granted) }
    }

    fun disableCameraFeed() {
        _arState.update { it.copy(isRealCameraActive = false) }
        showStatus("Virtual room backdrop active")
    }

    fun toggleCameraFeed() {
        _arState.update {
            val next = !it.isRealCameraActive
            it.copy(isRealCameraActive = next)
        }
        showStatus(if (_arState.value.isRealCameraActive) "Live camera feed active" else "Virtual room backdrop active")
    }

    fun cycleSimulationRoom() {
        _arState.update { it.copy(simulationRoomIndex = (it.simulationRoomIndex + 1) % 3) }
        val names = listOf("Sunlit Loft Studio", "Minimalist Scandinavian Bright", "Modern Warm Japandi")
        showStatus("Room backdrop: ${names[_arState.value.simulationRoomIndex]}")
    }

    fun setInteractionMode(mode: ARInteractionMode) {
        _arState.update { it.copy(interactionMode = mode) }
    }

    fun selectCatalogItemToPlace(item: FurnitureCatalogItem) {
        _selectedCatalogItemForPlacement.value = item
        // Place automatically on floor center if in AR Studio
        placeFurnitureOnFloor(item, 0f, 0.2f)
        _currentScreen.value = AppNavDestination.AR_STUDIO
        showStatus("Placed ${item.name}. Tap and drag to position.")
    }

    fun placeFurnitureOnFloor(item: FurnitureCatalogItem, normX: Float, normY: Float) {
        val newPlaced = PlacedFurniture(
            instanceId = UUID.randomUUID().toString(),
            catalogId = item.id,
            name = item.name,
            category = item.category,
            model3D = item.model3D,
            positionX = normX,
            positionY = normY,
            rotationDegrees = 0f,
            scale = 1.0f,
            selectedFinish = item.defaultFinish,
            dimensionsDesc = item.dimensionsDesc,
            priceEstimate = item.priceEstimate,
            isSelected = true
        )

        _arState.update { current ->
            val unselected = current.placedFurniture.map { it.copy(isSelected = false) }
            val updatedList = unselected + newPlaced
            current.copy(
                placedFurniture = updatedList,
                selectedFurnitureId = newPlaced.instanceId,
                interactionMode = ARInteractionMode.FURNITURE_EDIT
            )
        }
    }

    fun selectFurniture(item: PlacedFurniture?) {
        _arState.update { current ->
            val updated = current.placedFurniture.map { f ->
                f.copy(isSelected = f.instanceId == item?.instanceId)
            }
            current.copy(
                placedFurniture = updated,
                selectedFurnitureId = item?.instanceId,
                interactionMode = if (item != null) ARInteractionMode.FURNITURE_EDIT else ARInteractionMode.FURNITURE_PLACEMENT
            )
        }
    }

    fun updateFurniturePosition(item: PlacedFurniture, normX: Float, normY: Float) {
        _arState.update { current ->
            val updated = current.placedFurniture.map { f ->
                if (f.instanceId == item.instanceId) {
                    f.copy(positionX = normX, positionY = normY)
                } else f
            }
            current.copy(placedFurniture = updated)
        }
    }

    fun updateSelectedRotation(degrees: Float) {
        val selId = _arState.value.selectedFurnitureId ?: return
        _arState.update { current ->
            val updated = current.placedFurniture.map { f ->
                if (f.instanceId == selId) {
                    f.copy(rotationDegrees = (degrees % 360f + 360f) % 360f)
                } else f
            }
            current.copy(placedFurniture = updated)
        }
    }

    fun updateSelectedScale(scale: Float) {
        val selId = _arState.value.selectedFurnitureId ?: return
        val clamped = scale.coerceIn(0.5f, 2.5f)
        _arState.update { current ->
            val updated = current.placedFurniture.map { f ->
                if (f.instanceId == selId) {
                    f.copy(scale = clamped)
                } else f
            }
            current.copy(placedFurniture = updated)
        }
    }

    fun updateSelectedFinish(finish: FinishType) {
        val selId = _arState.value.selectedFurnitureId ?: return
        _arState.update { current ->
            val updated = current.placedFurniture.map { f ->
                if (f.instanceId == selId) {
                    f.copy(selectedFinish = finish, customColorHex = null)
                } else f
            }
            current.copy(placedFurniture = updated)
        }
        showStatus("Finish updated to ${finish.displayName}")
    }

    fun duplicateSelectedFurniture() {
        val selId = _arState.value.selectedFurnitureId ?: return
        val target = _arState.value.placedFurniture.find { it.instanceId == selId } ?: return
        val dup = target.clone()
        _arState.update { current ->
            val unselected = current.placedFurniture.map { it.copy(isSelected = false) }
            current.copy(
                placedFurniture = unselected + dup,
                selectedFurnitureId = dup.instanceId
            )
        }
        showStatus("Duplicated ${target.name}")
    }

    fun deleteSelectedFurniture() {
        val selId = _arState.value.selectedFurnitureId ?: return
        _arState.update { current ->
            val filtered = current.placedFurniture.filterNot { it.instanceId == selId }
            current.copy(
                placedFurniture = filtered,
                selectedFurnitureId = null,
                interactionMode = ARInteractionMode.FURNITURE_PLACEMENT
            )
        }
        showStatus("Item deleted from room")
    }

    fun resetSelectedPosition() {
        val selId = _arState.value.selectedFurnitureId ?: return
        _arState.update { current ->
            val updated = current.placedFurniture.map { f ->
                if (f.instanceId == selId) {
                    f.copy(positionX = 0f, positionY = 0.2f, rotationDegrees = 0f, scale = 1.0f)
                } else f
            }
            current.copy(placedFurniture = updated)
        }
        showStatus("Reset item transform")
    }

    fun clearAllFurniture() {
        _arState.update { it.copy(placedFurniture = emptyList(), selectedFurnitureId = null) }
        showStatus("Cleared room furniture")
    }

    // --- Wall Color & Wallpaper Actions ---
    fun selectWallTarget(target: WallTarget) {
        _arState.update { current ->
            current.copy(
                wallDesign = current.wallDesign.copy(targetWall = target),
                interactionMode = ARInteractionMode.WALL_COLORING
            )
        }
        showStatus("Selected ${target.label} for styling")
    }

    fun applyWallColor(hex: Long, name: String) {
        _arState.update { current ->
            current.copy(
                wallDesign = current.wallDesign.copy(
                    colorHex = hex,
                    colorName = name
                )
            )
        }
        showStatus("Applied paint: $name")
    }

    fun applyWallFinish(finish: PaintFinish) {
        _arState.update { current ->
            current.copy(wallDesign = current.wallDesign.copy(finish = finish))
        }
        showStatus("Paint sheen set to ${finish.label}")
    }

    fun applyWallpaper(pattern: WallpaperPattern) {
        _arState.update { current ->
            current.copy(wallDesign = current.wallDesign.copy(wallpaper = pattern))
        }
        showStatus("Applied wallpaper: ${pattern.label}")
    }

    fun setWallpaperScale(scale: Float) {
        _arState.update { current ->
            current.copy(wallDesign = current.wallDesign.copy(wallpaperScale = scale.coerceIn(0.5f, 2.5f)))
        }
    }

    fun resetWallDesign() {
        _arState.update { current ->
            current.copy(wallDesign = WallDesignConfig())
        }
        showStatus("Restored default wall finish")
    }

    // --- Floor Material Actions ---
    fun applyFloorMaterial(materialId: String) {
        _arState.update { current ->
            current.copy(
                floorDesign = current.floorDesign.copy(selectedMaterialId = materialId),
                interactionMode = ARInteractionMode.FLOOR_STYLING
            )
        }
        val mat = CuratedFloorMaterials.getById(materialId)
        showStatus("Applied flooring: ${mat.name}")
    }

    fun setFloorSheen(sheen: Float) {
        _arState.update { current ->
            current.copy(floorDesign = current.floorDesign.copy(sheenLevel = sheen.coerceIn(0f, 1f)))
        }
    }

    fun resetFloorDesign() {
        _arState.update { current ->
            current.copy(floorDesign = FloorDesignConfig())
        }
        showStatus("Restored default floor")
    }

    // --- Measurement Tape Actions ---
    fun addMeasurementPoint(point: MeasurementPoint) {
        _arState.update { current ->
            val points = (current.measurementPoints + point).takeLast(4)
            current.copy(measurementPoints = points)
        }
    }

    fun clearMeasurements() {
        _arState.update { it.copy(measurementPoints = emptyList()) }
        showStatus("Cleared tape measurements")
    }

    // --- Design Save / Load Actions ---
    fun saveCurrentDesign(name: String, roomType: RoomType, notes: String) {
        val current = _arState.value
        // Calculate estimated budget total
        var totalCents = 0
        current.placedFurniture.forEach { f ->
            val clean = f.priceEstimate.replace("$", "").replace(",", "").trim().toIntOrNull() ?: 0
            totalCents += clean
        }
        val budgetFormatted = if (totalCents > 0) "$${"%,d".format(totalCents)}" else "$0"

        val record = RoomDesignRecord(
            designId = UUID.randomUUID().toString(),
            name = name.ifBlank { "Untitled Space" },
            roomType = roomType,
            furniture = current.placedFurniture,
            wallDesign = current.wallDesign,
            floorDesign = current.floorDesign,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            estimatedBudgetTotal = budgetFormatted,
            notes = notes
        )

        viewModelScope.launch {
            repository.saveDesign(record)
            _arState.update { it.copy(currentRoomDesignName = record.name) }
            showStatus("Saved '${record.name}' to My Designs")
        }
    }

    fun loadDesign(record: RoomDesignRecord) {
        _arState.update { current ->
            current.copy(
                currentRoomDesignName = record.name,
                placedFurniture = record.furniture.map { it.copy(isSelected = false) },
                selectedFurnitureId = null,
                wallDesign = record.wallDesign,
                floorDesign = record.floorDesign,
                interactionMode = ARInteractionMode.FURNITURE_PLACEMENT
            )
        }
        _currentScreen.value = AppNavDestination.AR_STUDIO
        showStatus("Loaded '${record.name}' into AR Studio")
    }

    fun deleteDesign(id: String) {
        viewModelScope.launch {
            repository.deleteDesign(id)
            showStatus("Design deleted")
        }
    }

    fun duplicateDesign(record: RoomDesignRecord) {
        viewModelScope.launch {
            repository.duplicateDesign(record)
            showStatus("Duplicated '${record.name}'")
        }
    }

    fun createNewDesign() {
        _arState.update {
            ARSessionState(
                currentRoomDesignName = "New AR Project",
                surfaces = it.surfaces,
                isCameraPermissionGranted = it.isCameraPermissionGranted,
                isRealCameraActive = it.isRealCameraActive,
                simulationRoomIndex = it.simulationRoomIndex
            )
        }
        _currentScreen.value = AppNavDestination.AR_STUDIO
        showStatus("Created fresh room canvas")
    }

    fun showStatus(msg: String) {
        _statusMessage.value = msg
    }

    fun dismissStatus() {
        _statusMessage.value = null
    }
}
