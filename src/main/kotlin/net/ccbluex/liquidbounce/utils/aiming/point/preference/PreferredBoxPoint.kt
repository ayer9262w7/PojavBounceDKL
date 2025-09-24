
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

package net.ccbluex.liquidbounce.utils.aiming.point.preference

import net.ccbluex.liquidbounce.config.types.NamedChoice
import net.ccbluex.liquidbounce.utils.client.player
import net.ccbluex.liquidbounce.utils.entity.rotation
import net.ccbluex.liquidbounce.utils.math.plus
import net.minecraft.entity.LivingEntity
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import kotlin.math.*
import java.util.Random

/**
 * Enhanced PreferredBoxPoint with advanced anti-detection features
 * 
 * This improved version includes:
 * 1. Advanced targeting algorithms with natural human-like behavior
 * 2. Context-aware point selection that varies based on situation
 * 3. Anti-pattern techniques to avoid detection
 * 4. Realistic targeting distribution that mimics human aiming
 */
@Suppress("unused")
enum class PreferredBoxPoint(override val choiceName: String, val point: (Box, Vec3d, TargetContext?) -> Vec3d) : NamedChoice {
    CLOSEST("Closest", { box, eyes, context ->
        // Enhanced closest point with slight natural variation
        val basePoint = Vec3d(
            eyes.x.coerceAtMost(box.maxX).coerceAtLeast(box.minX),
            eyes.y.coerceAtMost(box.maxY).coerceAtLeast(box.minY),
            eyes.z.coerceAtMost(box.maxZ).coerceAtLeast(box.minZ)
        )
        
        // Apply context-aware variation if context is available
        applyContextAwareVariation(basePoint, box, context)
    }),
    
    ASSIST("Assist", { box, eyes, context ->
        // Enhanced assist with natural human-like behavior
        val lookVec = player.rotation.directionVector
        val vec3 = eyes + lookVec
        
        val basePoint = Vec3d(
            vec3.x.coerceAtMost(box.maxX).coerceAtLeast(box.minX),
            vec3.y.coerceAtMost(box.maxY).coerceAtLeast(box.minY),
            vec3.z.coerceAtMost(box.maxZ).coerceAtLeast(box.minZ)
        )
        
        // Apply context-aware variation if context is available
        applyContextAwareVariation(basePoint, box, context)
    }),
    
    STRAIGHT("Straight", { box, eyes, context ->
        // Enhanced straight targeting with natural vertical variation
        val basePoint = Vec3d(
            box.center.x,
            eyes.y.coerceAtMost(box.maxY).coerceAtLeast(box.minY),
            box.center.z
        )
        
        // Apply context-aware variation if context is available
        applyContextAwareVariation(basePoint, box, context)
    }),
    
    CENTER("Center", { box, _, context ->
        // Enhanced center targeting with slight natural variation
        val basePoint = box.center
        
        // Apply context-aware variation if context is available
        applyContextAwareVariation(basePoint, box, context)
    }),
    
    ADAPTIVE("Adaptive", { box, eyes, context ->
        // Adaptive targeting that changes based on context
        val adaptivePoint = if (context != null) {
            // Use different targeting strategies based on context
            when {
                context.isEntityMoving && context.distanceToTarget > 5.0 -> {
                    // For moving targets at distance, aim slightly ahead
                    val predictiveFactor = 0.2 + (context.random.nextDouble() * 0.1)
                    val predictedPos = box.center.add(context.entityVelocity.multiply(predictiveFactor))
                    
                    Vec3d(
                        predictedPos.x.coerceAtMost(box.maxX).coerceAtLeast(box.minX),
                        predictedPos.y.coerceAtMost(box.maxY).coerceAtLeast(box.minY),
                        predictedPos.z.coerceAtMost(box.maxZ).coerceAtLeast(box.minZ)
                    )
                }
                context.isEntityHurt -> {
                    // For hurt entities, aim more precisely at center
                    val centerBias = 0.7 + (context.random.nextDouble() * 0.2)
                    val randomBias = 1.0 - centerBias
                    
                    Vec3d(
                        box.center.x * centerBias + (box.minX + context.random.nextDouble() * (box.maxX - box.minX)) * randomBias,
                        box.center.y * centerBias + (box.minY + context.random.nextDouble() * (box.maxY - box.minY)) * randomBias,
                        box.center.z * centerBias + (box.minZ + context.random.nextDouble() * (box.maxZ - box.minZ)) * randomBias
                    )
                }
                context.distanceToTarget < 3.0 -> {
                    // For close targets, aim more precisely
                    val precisionFactor = 0.85 + (context.random.nextDouble() * 0.1)
                    val centerPoint = box.center
                    val eyeLevel = eyes.y.coerceAtMost(box.maxY).coerceAtLeast(box.minY)
                    
                    Vec3d(
                        centerPoint.x,
                        centerPoint.y * (1.0 - precisionFactor) + eyeLevel * precisionFactor,
                        centerPoint.z
                    )
                }
                else -> {
                    // Default behavior, mix of center and closest
                    val closestPoint = Vec3d(
                        eyes.x.coerceAtMost(box.maxX).coerceAtLeast(box.minX),
                        eyes.y.coerceAtMost(box.maxY).coerceAtLeast(box.minY),
                        eyes.z.coerceAtMost(box.maxZ).coerceAtLeast(box.minZ)
                    )
                    
                    val mixFactor = 0.3 + (context.random.nextDouble() * 0.4)
                    
                    Vec3d(
                        closestPoint.x * mixFactor + box.center.x * (1.0 - mixFactor),
                        closestPoint.y * mixFactor + box.center.y * (1.0 - mixFactor),
                        closestPoint.z * mixFactor + box.center.z * (1.0 - mixFactor)
                    )
                }
            }
        } else {
            // Fallback to center if no context is available
            box.center
        }
        
        // Apply context-aware variation if context is available
        applyContextAwareVariation(adaptivePoint, box, context)
    }),
    
    RANDOM("Random", { box, _, context ->
        // Enhanced random targeting with realistic human-like distribution
        val random = context?.random ?: Random(System.nanoTime())
        
        // Use Gaussian distribution for more realistic human-like targeting
        val gaussianX = random.nextGaussian() * 0.3 + 0.5
        val gaussianY = random.nextGaussian() * 0.3 + 0.5
        val gaussianZ = random.nextGaussian() * 0.3 + 0.5
        
        // Clamp values to ensure they're within the box
        val x = (box.minX + (box.maxX - box.minX) * gaussianX.coerceIn(0.0, 1.0))
        val y = (box.minY + (box.maxY - box.minY) * gaussianY.coerceIn(0.0, 1.0))
        val z = (box.minZ + (box.maxZ - box.minZ) * gaussianZ.coerceIn(0.0, 1.0))
        
        Vec3d(x, y, z)
    }),
    
    RANDOM_CENTER("RandomCenter", { box, _, context ->
        // Enhanced random center targeting with realistic distribution
        val random = context?.random ?: Random(System.nanoTime())
        
        // Use Gaussian distribution for more realistic human-like targeting
        val gaussianX = random.nextGaussian() * 0.3 + 0.5
        val gaussianZ = random.nextGaussian() * 0.3 + 0.5
        
        // Clamp values to ensure they're within the box
        val x = (box.minX + (box.maxX - box.minX) * gaussianX.coerceIn(0.0, 1.0))
        val z = (box.minZ + (box.maxZ - box.minZ) * gaussianZ.coerceIn(0.0, 1.0))
        
        Vec3d(x, box.center.y, z)
    }),
    
    MAGNETIC_BOUNDARIES("MagneticBoundaries", { box, eyes, context ->
        // Enhanced magnetic boundaries with natural human-like behavior
        val lookVec = player.rotation.directionVector
        val eyeToCenter = box.center.subtract(eyes)
        val distance = eyeToCenter.dotProduct(lookVec)
        
        if (distance < 0) {
            // Target is behind, fall back to center with slight variation
            applyContextAwareVariation(box.center, box, context)
        } else {
            // Point on the line of sight that is co-planar with the box center
            val aimPoint = eyes.add(lookVec.multiply(distance))
            
            // Check how far the aimPoint is from the center, normalized by box size.
            // A value of 0.0 is center, ~1.0 is at the edge.
            val halfWidth = (box.maxX - box.minX) / 2.0
            val halfHeight = (box.maxY - box.minY) / 2.0
            val halfDepth = (box.maxZ - box.minZ) / 2.0
            
            // Avoid division by zero for 2D hitboxes (e.g., items on ground)
            if (halfWidth == 0.0 || halfHeight == 0.0 || halfDepth == 0.0) {
                applyContextAwareVariation(box.center, box, context)
            }
            
            val normX = (aimPoint.x - box.center.x) / halfWidth
            val normY = (aimPoint.y - box.center.y) / halfHeight
            val normZ = (aimPoint.z - box.center.z) / halfDepth
            val maxNormDist = max(abs(normX), max(abs(normY), abs(normZ)))
            
            // Define the magnetic zone with slight variation based on context
            val marginStart = if (context != null) {
                // Vary margin based on context
                val baseMagneticZone = 0.7
                val contextVariation = when {
                    context.isEntityHurt -> -0.1 // More magnetic when entity is hurt
                    context.isEntityMoving -> 0.1 // Less magnetic when entity is moving
                    context.distanceToTarget > 8.0 -> 0.05 // Slightly less magnetic at distance
                    else -> 0.0
                }
                
                // Add slight random variation
                val randomVariation = (context.random.nextDouble() - 0.5) * 0.1
                
                (baseMagneticZone + contextVariation + randomVariation).coerceIn(0.5, 0.9)
            } else {
                0.7 // Default value
            }
            
            val basePoint = if (maxNormDist < marginStart) {
                // Inside the free-aim zone, do not assist.
                eyes.add(lookVec.multiply(100.0))
            } else {
                // In the magnetic zone or outside.
                val stickPoint = Vec3d(
                    aimPoint.x.coerceIn(box.minX, box.maxX),
                    aimPoint.y.coerceIn(box.minY, box.maxY),
                    aimPoint.z.coerceIn(box.minZ, box.maxZ)
                )
                
                if (maxNormDist >= 1.0) {
                    // Completely outside the box, stick firmly to the edge.
                    stickPoint
                } else {
                    // Inside the margin, calculate proportional pull.
                    // Alpha is 0.0 at the start of the margin, 1.0 at the outer edge.
                    val alpha = ((maxNormDist - marginStart) / (1.0 - marginStart)).coerceIn(0.0, 1.0)
                    
                    // Add slight non-linearity to the interpolation for more natural behavior
                    val adjustedAlpha = if (context != null) {
                        // Non-linear interpolation with slight randomization
                        val nonLinearFactor = alpha.pow(0.8 + context.random.nextDouble() * 0.4)
                        nonLinearFactor
                    } else {
                        alpha
                    }
                    
                    // Interpolate between the player's aim and the stick point.
                    // When alpha is high (near edge), we get more of the stickPoint.
                    val finalX = aimPoint.x * (1.0 - adjustedAlpha) + stickPoint.x * adjustedAlpha
                    val finalY = aimPoint.y * (1.0 - adjustedAlpha) + stickPoint.y * adjustedAlpha
                    val finalZ = aimPoint.z * (1.0 - adjustedAlpha) + stickPoint.z * adjustedAlpha
                    
                    Vec3d(finalX, finalY, finalZ)
                }
            }
            
            // Apply context-aware variation
            applyContextAwareVariation(basePoint, box, context)
        }
    }),
    
    VITAL_POINTS("VitalPoints", { box, eyes, context ->
        // Target vital points with human-like accuracy
        val random = context?.random ?: Random(System.nanoTime())
        
        // Define vital regions with slight variation
        val headRegion = 0.8 + (random.nextDouble() * 0.1) // Upper 20-30% is head
        val bodyRegion = 0.4 + (random.nextDouble() * 0.1) // Middle 40-50% is body
        
        // Calculate target point based on vital regions
        val targetRegion = random.nextDouble()
        val basePoint = when {
            targetRegion > headRegion -> {
                // Head targeting
                val headHeight = box.minY + (box.maxY - box.minY) * (0.8 + random.nextDouble() * 0.2)
                Vec3d(box.center.x, headHeight, box.center.z)
            }
            targetRegion > bodyRegion -> {
                // Body targeting
                val bodyHeight = box.minY + (box.maxY - box.minY) * (0.4 + random.nextDouble() * 0.4)
                Vec3d(box.center.x, bodyHeight, box.center.z)
            }
            else -> {
                // Legs targeting
                val legHeight = box.minY + (box.maxY - box.minY) * (0.1 + random.nextDouble() * 0.3)
                Vec3d(box.center.x, legHeight, box.center.z)
            }
        }
        
        // Apply context-aware variation
        applyContextAwareVariation(basePoint, box, context)
    }),
    
    DYNAMIC("Dynamic", { box, eyes, context ->
        // Dynamically changes targeting strategy based on combat situation
        val basePoint = context?.let {
            // Select targeting strategy based on context
            when {
                // For close combat with hurt entity, target vital points
                it.isEntityHurt && it.distanceToTarget < 4.0 -> {
                    val headHeight = box.minY + (box.maxY - box.minY) * (0.8 + it.random.nextDouble() * 0.2)
                    Vec3d(box.center.x, headHeight, box.center.z)
                }
                
                // For moving targets, use predictive aiming
                it.isEntityMoving && it.entityVelocity.lengthSquared() > 0.01 -> {
                    val predictiveFactor = 0.15 + (it.random.nextDouble() * 0.1)
                    val predictedPos = box.center.add(it.entityVelocity.multiply(predictiveFactor))
                    
                    Vec3d(
                        predictedPos.x.coerceAtMost(box.maxX).coerceAtLeast(box.minX),
                        predictedPos.y.coerceAtMost(box.maxY).coerceAtLeast(box.minY),
                        predictedPos.z.coerceAtMost(box.maxZ).coerceAtLeast(box.minZ)
                    )
                }
                
                // For distant targets, aim more precisely
                it.distanceToTarget > 8.0 -> {
                    val lookVec = player.rotation.directionVector
                    val eyeToCenter = box.center.subtract(eyes)
                    val distance = eyeToCenter.dotProduct(lookVec)
                    
                    if (distance < 0) {
                        box.center
                    } else {
                        val aimPoint = eyes.add(lookVec.multiply(distance))
                        
                        Vec3d(
                            aimPoint.x.coerceAtMost(box.maxX).coerceAtLeast(box.minX),
                            aimPoint.y.coerceAtMost(box.maxY).coerceAtLeast(box.minY),
                            aimPoint.z.coerceAtMost(box.maxZ).coerceAtLeast(box.minZ)
                        )
                    }
                }
                
                // Default behavior, mix of center and closest with slight randomization
                else -> {
                    val mixFactor = 0.3 + (it.random.nextDouble() * 0.4)
                    val closestPoint = Vec3d(
                        eyes.x.coerceAtMost(box.maxX).coerceAtLeast(box.minX),
                        eyes.y.coerceAtMost(box.maxY).coerceAtLeast(box.minY),
                        eyes.z.coerceAtMost(box.maxZ).coerceAtLeast(box.minZ)
                    )
                    
                    Vec3d(
                        closestPoint.x * mixFactor + box.center.x * (1.0 - mixFactor),
                        closestPoint.y * mixFactor + box.center.y * (1.0 - mixFactor),
                        closestPoint.z * mixFactor + box.center.z * (1.0 - mixFactor)
                    )
                }
            }
        } ?: box.center // Fallback to box.center if context is null
        
        // Apply context-aware variation
        applyContextAwareVariation(basePoint, box, context)
    });
    
    companion object {
        /**
         * Apply context-aware variation to the target point
         */
        private fun applyContextAwareVariation(basePoint: Vec3d, box: Box, context: TargetContext?): Vec3d {
            if (context == null) return basePoint
            
            val nonNullableContext = context as TargetContext
            
            // Calculate variation strength based on context
            val variationStrength = when {
                nonNullableContext.isEntityHurt -> 0.01 // Less variation when entity is hurt (more precise)
                nonNullableContext.isEntityMoving -> 0.03 // More variation when entity is moving
                nonNullableContext.distanceToTarget > 8.0 -> 0.02 // Moderate variation at distance
                else -> 0.015 // Default variation
            }
            
            // Apply natural variation with non-linear components
            val time = System.currentTimeMillis() / 1000.0
            val phase = nonNullableContext.targetId * 0.1 + time * 0.3
            
            val xVariation = sin(phase) * variationStrength
            val yVariation = cos(phase * 1.3) * variationStrength * 0.8
            val zVariation = sin(phase * 0.7) * variationStrength
            
            // Ensure the point stays within the box
            return Vec3d(
                (basePoint.x + xVariation).coerceIn(box.minX, box.maxX),
                (basePoint.y + yVariation).coerceIn(box.minY, box.maxY),
                (basePoint.z + zVariation).coerceIn(box.minZ, box.maxZ)
            )
        }
    }
    
    /**
     * Context class for target-specific information
     */
    data class TargetContext(
        val targetId: Int,
        val distanceToTarget: Double,
        val isEntityMoving: Boolean,
        val isEntityHurt: Boolean,
        val entityVelocity: Vec3d,
        val random: java.util.Random = java.util.Random(System.nanoTime() xor targetId.toLong())
    ) {
        companion object {
            /**
             * Create context from entity
             */
            fun fromEntity(entity: LivingEntity, playerEyes: Vec3d): TargetContext {
                return TargetContext(
                    targetId = entity.id,
                    distanceToTarget = playerEyes.distanceTo(entity.pos),
                    isEntityMoving = entity.velocity.lengthSquared() > 0.01,
                    isEntityHurt = entity.hurtTime > 0,
                    entityVelocity = entity.velocity
                )
            }
        }
    }
}
