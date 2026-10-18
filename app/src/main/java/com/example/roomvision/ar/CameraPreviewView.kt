package com.example.roomvision.ar

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun CameraBackground(
    isCameraEnabled: Boolean,
    simulationRoomIndex: Int,
    onCameraUnavailable: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    if (isCameraEnabled) {
        val previewView = remember {
            PreviewView(context).apply {
                // Use TextureView to avoid SurfaceView BLAST BufferQueue abandonment issues in Compose
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
        }

        DisposableEffect(isCameraEnabled, lifecycleOwner) {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            var cameraProvider: ProcessCameraProvider? = null

            cameraProviderFuture.addListener({
                try {
                    cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.surfaceProvider = previewView.surfaceProvider
                    }
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    cameraProvider?.unbindAll()
                    if (cameraProvider?.hasCamera(cameraSelector) == true) {
                        cameraProvider?.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
                    } else if (cameraProvider?.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) == true) {
                        cameraProvider?.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_FRONT_CAMERA, preview)
                    } else {
                        onCameraUnavailable()
                    }
                } catch (e: Exception) {
                    onCameraUnavailable()
                }
            }, ContextCompat.getMainExecutor(context))

            onDispose {
                try {
                    cameraProvider?.unbindAll()
                } catch (e: Exception) {
                    // Ignore on disposal
                }
            }
        }

        AndroidView(
            factory = { previewView },
            modifier = modifier.fillMaxSize()
        )
    } else {
        // High-fidelity procedural room background simulation for emulator and indoor testing
        SimulatedRoomBackdrop(
            roomIndex = simulationRoomIndex,
            modifier = modifier.fillMaxSize()
        )
    }
}

@Composable
fun SimulatedRoomBackdrop(
    roomIndex: Int,
    modifier: Modifier = Modifier
) {
    // Elegant, clean room backgrounds representing modern interior spaces
    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                val w = size.width
                val h = size.height

                // Horizon / Wall-to-floor boundary line around 48% height
                val horizonY = h * 0.48f

                val (ceilingColor, wallColorTop, wallColorBottom, floorColorFar, floorColorNear) = when (roomIndex % 3) {
                    0 -> listOf(
                        // Sunlit Loft Studio
                        Color(0xFFE2E8F0),
                        Color(0xFFCBD5E1),
                        Color(0xFFE2E8F0),
                        Color(0xFFD1C7BD),
                        Color(0xFFB8A99A)
                    )
                    1 -> listOf(
                        // Minimalist Scandinavian Bright
                        Color(0xFFF8FAFC),
                        Color(0xFFE5E7EB),
                        Color(0xFFF3F4F6),
                        Color(0xFFE0D7C9),
                        Color(0xFFD4C5B2)
                    )
                    else -> listOf(
                        // Modern Warm Japandi
                        Color(0xFFECE7E1),
                        Color(0xFFDDD5CA),
                        Color(0xFFEFEBE4),
                        Color(0xFFC7B299),
                        Color(0xFFA89278)
                    )
                }

                // Draw Ceiling & Upper Wall Gradient
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(ceilingColor, wallColorTop, wallColorBottom),
                        startY = 0f,
                        endY = horizonY
                    ),
                    topLeft = Offset.Zero,
                    size = androidx.compose.ui.geometry.Size(w, horizonY)
                )

                // Architectural wall corners & perspective lines
                val leftCornerX = w * 0.18f
                val rightCornerX = w * 0.82f

                // Left wall vertical edge
                drawLine(
                    color = Color.Black.copy(alpha = 0.08f),
                    start = Offset(leftCornerX, 0f),
                    end = Offset(leftCornerX, horizonY),
                    strokeWidth = 2f
                )

                // Right wall vertical edge
                drawLine(
                    color = Color.Black.copy(alpha = 0.08f),
                    start = Offset(rightCornerX, 0f),
                    end = Offset(rightCornerX, horizonY),
                    strokeWidth = 2f
                )

                // Baseboard / Skirting board
                drawRect(
                    color = Color.White.copy(alpha = 0.6f),
                    topLeft = Offset(0f, horizonY - 12f),
                    size = androidx.compose.ui.geometry.Size(w, 12f)
                )
                drawLine(
                    color = Color.Black.copy(alpha = 0.15f),
                    start = Offset(0f, horizonY),
                    end = Offset(w, horizonY),
                    strokeWidth = 2f
                )

                // Floor gradient receding into perspective
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(floorColorFar, floorColorNear),
                        startY = horizonY,
                        endY = h
                    ),
                    topLeft = Offset(0f, horizonY),
                    size = androidx.compose.ui.geometry.Size(w, h - horizonY)
                )

                // Window light wash simulation from top left
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.White.copy(alpha = 0.25f), Color.Transparent),
                        center = Offset(w * 0.2f, horizonY * 0.4f),
                        radius = w * 0.7f
                    ),
                    radius = w * 0.7f,
                    center = Offset(w * 0.2f, horizonY * 0.4f)
                )
            }
    )
}
