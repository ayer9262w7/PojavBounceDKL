/*
 * This file is part of LiquidBounce (https://github.com/CCBlueX/LiquidBounce)
 *
 * Copyright (c) 2015 - 2025 CCBlueX
 *
 * LiquidBounce is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LiquidBounce is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LiquidBounce. If not, see <https://www.gnu.org/licenses/>.
 */
package net.ccbluex.liquidbounce.features.module.modules.render.gui.hud

import net.ccbluex.liquidbounce.utils.client.mc

/**
 * Singleton to manage HUD elements, ensuring consistency between the editor and in-game display.
 */
object HudManager {
    val elements = mutableListOf<HudElement>()

    fun initialize() {
        if (elements.isNotEmpty()) return

        val screenWidth = mc.window.scaledWidth
        val screenHeight = mc.window.scaledHeight

        // Initialize with default elements and positions from the video
        elements.add(WatermarkElement(2, 2))
        elements.add(ArrayListElement(screenWidth - 120, 2))
        elements.add(InfoPanelElement(2, 30))
        elements.add(SpeedElement(screenWidth / 2 - 50, screenHeight - 40))
    }
}
