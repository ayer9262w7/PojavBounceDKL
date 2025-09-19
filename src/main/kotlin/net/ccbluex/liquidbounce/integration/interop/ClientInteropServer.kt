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
package net.ccbluex.liquidbounce.integration.interop

import net.ccbluex.liquidbounce.utils.client.logger

/**
 * Stub implementation of Client Interop Server for native GUI migration
 * 
 * This replaces the original HTTP interop server functionality with no-op stubs
 * since the native GUI doesn't require HTTP communication.
 */
object ClientInteropServer {
    
    // Stub URL for compatibility with code that references it
    const val url: String = "http://localhost:0" // Port 0 indicates disabled
    
    fun start() {
        logger.info("Client Interop Server is disabled - using native GUI instead of HTTP interop")
    }
    
    fun stop() {
        logger.info("Client Interop Server stop() called - no-op for native GUI")
    }
}
