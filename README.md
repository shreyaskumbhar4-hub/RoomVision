# RoomVision 🛋️✨

**Android-Based Augmented Reality Interior Design & Wall Colour Visualization Application**

[![Android CI & Build](https://github.com/your-username/RoomVision/actions/workflows/android.yml/badge.svg)](https://github.com/your-username/RoomVision/actions/workflows/android.yml)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF.svg?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-M3-4285F4.svg?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![CameraX](https://img.shields.io/badge/CameraX-1.5.0-34A853.svg)](https://developer.android.com/training/camerax)
[![Room](https://img.shields.io/badge/Room%20DB-2.7.0-F57C00.svg)](https://developer.android.com/training/data-storage/room)
[![API](https://img.shields.io/badge/Min%20SDK-24%20(Android%207.0)-blue.svg)](https://android-arsenal.com/api?level=24)

RoomVision lets you preview interior design concepts in your real surroundings using augmented reality (AR). Scan supported room surfaces, test furniture in 3D, customize upholstery and wood finishes, explore wall colors and textured wallpapers with dynamic lighting, preview flooring materials, and save room configurations with an itemized bill of materials.

---

## 🚀 Key Features

### 1. AR Surface Plane Detection
- **Horizontal Floor Detection:** Perspective grid projection, boundary detection lines, and scanning anchor pulses.
- **Vertical Wall Candidates:** Interactive candidate meshes for **Back Wall**, **Left Wall**, and **Right Wall** with tap-to-select support.
- **Dual-Mode Visualizer:** Live CameraX background on physical devices with seamless automatic fallback to simulated architectural room studios (*Sunlit Loft*, *Minimalist Scandinavian*, *Warm Japandi*) for testing in virtual or emulator environments.

### 2. Wall Colour & Wallpaper Studio
- **Selective Recoloring:** Recolor specific detected walls or all walls simultaneously.
- **Curated Architectural Palettes:** Handcrafted collections including *Warm Neutrals*, *Earthy Sage*, *Scandinavian*, and *Moody Luxe*.
- **Realistic Paint Finishes:** Real-time sheen simulation for *Matte*, *Eggshell*, *Satin*, and *Semi-Gloss*.
- **Textured Wallpapers:** Procedural patterns with scale adjustment (*Acoustic Wood Slats*, *Art Deco Arches*, *Moody Botanical*, *Exposed Lime Brick*, *Micro Terrazzo*, *Natural Linen Weave*).

### 3. Flooring Material Previews
- **Surface Textures:** *Scandinavian White Oak* (wide-plank), *Dark Walnut Herringbone* (chevron parquet), *Polished Concrete*, *Calacatta Gold Marble*, *Terracotta Hexagon Tiles*, and *Berber Wool Carpet*.
- **Dynamic Lustre:** Adjustable slider to simulate floor sheen, reflection, and ambient room light wash.

### 4. 3D Furniture Placement & Manipulation
- **Curated Catalogue:** Modern Sectional Sofas, Oslo Lounge Armchairs, Koto Oak Coffee Tables, Kyoto Dining Sets, Arco Brass Floor Lamps, Stilt Modular Bookshelves, Platform Beds, Fluted Media Consoles, and Indoor Houseplants (Fiddle Leaf Fig & Monstera).
- **Direct Touch Manipulation:**
  - **Tap to Place:** Instant placement on the floor plane.
  - **Drag Translation:** Smooth real-time repositioning across floor coordinates.
  - **360° Rotary Control:** Radial slider with front-facing directional marker.
  - **Proportional Scaling:** Clamped scaling (0.5x – 2.0x) with real-world dimension feedback.
  - **Finishes & Fabrics:** Switch on the fly between bouclé, velvet, leather, natural oak, walnut, brass, and matte black steel.
  - **Quick Actions:** Duplicate, Reset Transform, and Delete.
  - **Dynamic Shadows:** Ambient drop shadows cast on the floor beneath objects.

### 5. AR Virtual Tape Measure Tool
- Tap two points on any floor or wall surface to measure distance and span in meters and feet with interactive dimension callouts.

### 6. Design Persistence & Room Bill of Materials
- **Offline-First Persistence:** Powered by Android Room Database to store room layouts, furniture coordinates, transforms, finishes, and wall/floor configurations.
- **Itemized Bill of Materials:** Real-time cost estimator and furniture spec card summarizing room budget.
- **Starter Templates:** Pre-seeded with *Nordic Serenity Living Room* and *Japandi Focus Sanctuary*.

---

## 🛠️ Architecture & Tech Stack

```
com.example.roomvision/
├── ar/                   # CameraX background, 3D Canvas engine, Isometric renderers
│   ├── CameraPreviewView.kt
│   ├── FurnitureRenderer3D.kt
│   └── RoomARCanvas.kt
├── data/                 # Room database entities, DAOs, and repository layer
│   ├── AppDatabase.kt
│   ├── RoomDesignDao.kt
│   ├── RoomDesignEntity.kt
│   └── RoomDesignRepository.kt
├── model/                # Domain models for surfaces, furniture, walls, floors, designs
│   ├── ARSessionState.kt
│   ├── FloorDesign.kt
│   ├── FurnitureCatalog.kt
│   ├── FurnitureItem.kt
│   ├── RoomDesignRecord.kt
│   ├── RoomSurface.kt
│   └── WallDesign.kt
├── ui/                   # Jetpack Compose screens and M3 theme
│   ├── ARStudioScreen.kt
│   ├── CatalogScreen.kt
│   ├── HomeScreen.kt
│   ├── MainScreen.kt
│   ├── MyDesignsScreen.kt
│   └── PalettesScreen.kt
└── viewmodel/            # ViewModel managing AR state, selection, and CRUD
    └── RoomVisionViewModel.kt
```

* **Language:** Kotlin 2.2.10
* **UI Framework:** Jetpack Compose with Material Design 3 (M3)
* **Camera:** CameraX 1.5.0 (with `TextureView` compatible mode)
* **Database:** AndroidX Room 2.7.0 with Kotlin Symbol Processing (KSP)
* **Build Tooling:** Gradle 9.3.1 (Kotlin DSL)
* **Testing:** Robolectric 4.16.1 & JUnit 4

---

## ⚡ Getting Started

### Prerequisites
* **Android Studio** (Ladybug or newer recommended)
* **JDK 21**
* **Android SDK 36** (Min SDK: 24, Target SDK: 36)

### Clone & Build
```bash
# Clone the repository
git clone https://github.com/your-username/RoomVision.git
cd RoomVision

# Prepare local environment file
cp .env.example .env

# Build debug APK
./gradlew assembleDebug

# Run unit and Robolectric tests
./gradlew testDebugUnitTest
```

---

## 🤖 Continuous Integration & GitHub Actions

This repository includes automated CI/CD workflows under `.github/workflows/`:

* **`android.yml` (CI & Automated Testing):**
  - Runs on every push and pull request to `main` and `master`.
  - Sets up JDK 21 and the Android SDK.
  - Executes unit and Robolectric test suites and uploads test reports.
  - Builds the Debug APK and uploads it as a workflow artifact.

* **`release.yml` (Release Packaging):**
  - Triggers on version tags (`v*.*.*`) or via manual `workflow_dispatch`.
  - Builds the signed release APK and Google Play Android App Bundle (`.aab`).
  - Uploads artifacts ready for distribution.

---

## 📄 License
This project is licensed under the [MIT License](LICENSE).
