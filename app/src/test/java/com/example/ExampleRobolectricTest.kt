package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.roomvision.model.CuratedFloorMaterials
import com.example.roomvision.model.CuratedWallPalettes
import com.example.roomvision.model.FurnitureCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("RoomVision", appName)
    }

    @Test
    fun `verify furniture catalog items are loaded`() {
        val items = FurnitureCatalog.items
        assertTrue("Catalog should contain items", items.isNotEmpty())
        val sofa = FurnitureCatalog.getById("sectional-sofa-02")
        assertNotNull(sofa)
        assertEquals("Copenhagen 3-Seater Sofa", sofa?.name)
    }

    @Test
    fun `verify curated wall palettes and floor materials`() {
        assertTrue(CuratedWallPalettes.swatches.isNotEmpty())
        assertTrue(CuratedFloorMaterials.materials.isNotEmpty())
    }
}
