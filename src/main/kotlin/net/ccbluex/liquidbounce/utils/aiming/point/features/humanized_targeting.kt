
package net.ccbluex.liquidbounce.utils.aiming.point.features

import net.ccbluex.liquidbounce.config.types.nesting.ToggleableConfigurable
import net.ccbluex.liquidbounce.event.EventListener
import net.ccbluex.liquidbounce.utils.aiming.point.PointTracker.AimSituation
import net.ccbluex.liquidbounce.utils.client.player
import net.ccbluex.liquidbounce.utils.entity.sqrtSpeed
import net.ccbluex.liquidbounce.utils.kotlin.random
import net.minecraft.entity.LivingEntity
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * Humanized Targeting System
 * 
 * This class implements advanced human-like targeting behaviors:
 * 1. Target point selection based on human aiming patterns
 * 2. Predictive targeting with natural error
 * 3. Context-aware aiming adjustments
 * 4. Anti-pattern techniques to avoid detection
 */
internal class HumanizedTargeting(parent: EventListener) : ToggleableConfigurable(parent, "HumanizedTargeting", true) {

    /**
     * Configuration parameters
     */
    val targetingPrecision by floatRange("TargetingPrecision", 0.8f..1.0f, 0.5f..1.0f)
    val humanErrorRate by float("HumanErrorRate", 0.05f, 0.0f..0.2f)
    val predictiveAiming by boolean("PredictiveAiming", true)
    val contextAwareness by boolean("ContextAwareness", true)
    val antiPatternSystem by boolean("AntiPatternSystem", true)
    
    /**
     * Advanced targeting parameters
     */
    private val hitboxPreference by float("HitboxPreference", 0.5f, 0.0f..1.0f)
    private val verticalBias by float("VerticalBias", 0.2f, -0.5f..0.5f)
    private val horizontalVariation by float("HorizontalVariation", 0.1f, 0.0f..0.3f)
    
    /**
     * Random instance with secure seed generation
     */
    private val random = Random(System.nanoTime() xor System.currentTimeMillis())
    
    /**
     * State tracking for natural aiming patterns
     */
    private var lastTargetId = -1
    private var targetingPhase = 0.0
    private var lastUpdateTime = System.currentTimeMillis()
    private var consecutiveTargetCount = 0
    
    /**
     * Target history for pattern analysis
     */
    private val targetHistory = mutableMapOf<Int, MutableList<Vec3d>>()
    private val maxHistorySize = 15
    
    /**
     * Calculate humanized target point with natural aiming patterns
     */
    fun getHumanizedPoint(
        basePoint: Vec3d, 
        box: Box, 
        playerEyes: Vec3d, 
        entity: LivingEntity, 
        situation: AimSituation
    ): Vec3d {
        // Update targeting state
        val currentTime = System.currentTimeMillis()
        val timeDelta = (currentTime - lastUpdateTime) / 1000.0
        lastUpdateTime = currentTime
        
        // Check if targeting the same entity
        if (entity.id == lastTargetId) {
            consecutiveTargetCount++
            // Update targeting phase with non-linear progression
            targetingPhase += timeDelta * (0.5 + 0.5 * (1.0 - (consecutiveTargetCount / 50.0).coerceIn(0.0, 0.9)))
        } else {
            // Reset for new target
            lastTargetId = entity.id
            consecutiveTargetCount = 0
            targetingPhase = random.nextDouble() * 2 * Math.PI
        }
        
        // Get or create target history
        val history = targetHistory.getOrPut(entity.id) { mutableListOf() }
        
        // Calculate precision factor based on configuration and context
        val basePrecision = targetingPrecision.random()
        val contextPrecision = if (contextAwareness) {
            calculateContextPrecision(entity, situation, basePrecision)
        } else {
            basePrecision
        }
        
        // Apply human error based on configuration
        val errorFactor = (if (random.nextDouble() < humanErrorRate) {
            // Occasional larger errors to simulate human mistakes
            0.7 + random.nextDouble() * 0.2
        } else {
            // Normal precision with slight variation
            contextPrecision * (0.98 + random.nextDouble() * 0.04)
        }).toFloat()
        
        // Calculate target point with humanized adjustments
        val targetPoint = calculateHumanizedTargetPoint(basePoint, box, playerEyes, entity, errorFactor)
        
        // Apply predictive aiming if enabled
        val finalPoint = if (predictiveAiming) {
            applyPredictiveAiming(targetPoint, entity, situation)
        } else {
            targetPoint
        }
        
        // Apply anti-pattern adjustments if enabled
        val adjustedPoint = if (antiPatternSystem && detectSuspiciousPatterns(history)) {
            applyAntiPatternAdjustments(finalPoint)
        } else {
            finalPoint
        }
        
        // Update target history
        history.add(adjustedPoint)
        if (history.size > maxHistorySize) {
            history.removeAt(0)
        }
        
        return adjustedPoint
    }
    
    /**
     * Calculate context-aware precision based on entity state and situation
     */
    private fun calculateContextPrecision(
        entity: LivingEntity, 
        situation: AimSituation,
        basePrecision: Float
    ): Float {
        var precisionFactor = 1.0f
        
        // Adjust based on entity state
        if (entity.hurtTime > 0) {
            // Higher precision when entity is hurt (easier to hit)
            precisionFactor *= 1.0f + (entity.hurtTime / 20f) * 0.1f
        }
        
        // Adjust based on entity movement
        val entitySpeed = entity.velocity.lengthSquared().toFloat()
        if (entitySpeed > 0.01f) {
            // Lower precision for moving targets (harder to hit)
            precisionFactor *= (1.0f - (entitySpeed * 0.3f).coerceIn(0.0f, 0.15f))
        }
        
        // Adjust based on player movement
        if (player.sqrtSpeed > 0.1f) {
            // Lower precision when player is moving (harder to aim)
            val speedCalc: Float = (player.sqrtSpeed * 0.2f).toFloat()
            val clamped: Float = speedCalc.coerceIn(0.0f, 0.1f)
            val factor: Float = 1.0f - clamped
            precisionFactor *= factor
        }
        
        // Adjust based on aim situation
        precisionFactor *= when(situation) {
            AimSituation.FOR_THE_FUTURE -> 0.95f  // Lower precision for future prediction
            AimSituation.FOR_NEXT_TICK -> 1.0f    // Normal precision
            AimSituation.FOR_NOW -> 1.05f         // Higher precision for immediate aiming
            else -> 1.0f
        }
        
        return (basePrecision * precisionFactor).coerceIn(0.5f, 1.0f)
    }
    
    /**
     * Calculate humanized target point with natural aiming patterns
     */
    private fun calculateHumanizedTargetPoint(
        basePoint: Vec3d,
        box: Box,
        playerEyes: Vec3d,
        entity: LivingEntity,
        precisionFactor: Float
    ): Vec3d {
        // Calculate box dimensions
        val boxWidth = box.maxX - box.minX
        val boxHeight = box.maxY - box.minY
        val boxDepth = box.maxZ - box.minZ
        
        // Calculate center and dimensions of the target area based on precision
        val centerX = box.minX + boxWidth * (0.5 + (hitboxPreference.toDouble() - 0.5) * 0.2)
        val centerY = box.minY + boxHeight * (0.5 + verticalBias.toDouble())
        val centerZ = box.minZ + boxDepth * 0.5
        
        // Calculate target area dimensions based on precision
        val targetWidth = boxWidth * (1.0 - precisionFactor.toDouble()) * 0.8
        val targetHeight = boxHeight * (1.0 - precisionFactor.toDouble()) * 0.8
        val targetDepth = boxDepth * (1.0 - precisionFactor.toDouble()) * 0.8
        
        // Add natural variation with non-linear components
        val phaseX = sin(targetingPhase) * horizontalVariation.toDouble()
        val phaseY = cos(targetingPhase * 1.3) * (horizontalVariation.toDouble() * 0.7)
        val phaseZ = sin(targetingPhase * 0.7) * horizontalVariation.toDouble()
        
        // Calculate final point with precision-based targeting
        val x = centerX + targetWidth * phaseX
        val y = centerY + targetHeight * phaseY
        val z = centerZ + targetDepth * phaseZ
        
        return Vec3d(x, y, z)
    }
    
    /**
     * Apply predictive aiming based on entity movement
     */
    private fun applyPredictiveAiming(
        basePoint: Vec3d,
        entity: LivingEntity,
        situation: AimSituation
    ): Vec3d {
        // Skip prediction for non-moving entities
        if (entity.velocity.lengthSquared() < 0.001) {
            return basePoint
        }
        
        // Calculate prediction factor based on situation
        val predictionFactor = when(situation) {
            AimSituation.FOR_THE_FUTURE -> 0.3
            AimSituation.FOR_NEXT_TICK -> 0.2
            AimSituation.FOR_NOW -> 0.1
            else -> 0.0
        }
        
        // Calculate human-like prediction error
        val predictionError = 0.8 + random.nextDouble() * 0.4
        
        // Apply prediction with natural error
        return basePoint.add(
            entity.velocity.multiply(predictionFactor * predictionError)
        )
    }
    
    /**
     * Detect suspicious patterns in targeting history
     */
    private fun detectSuspiciousPatterns(history: List<Vec3d>): Boolean {
        if (history.size < 8) return false
        
        // Check for repeating patterns
        var repeatingPatterns = 0
        val recentPoints = history.takeLast(8)
        
        for (i in 0 until recentPoints.size - 2) {
            val delta1 = recentPoints[i+1].subtract(recentPoints[i])
            val delta2 = recentPoints[i+2].subtract(recentPoints[i+1])
            
            // Check if consecutive deltas are very similar
            val similarity = delta1.dotProduct(delta2) / (delta1.length() * delta2.length())
            if (similarity > 0.95) {
                repeatingPatterns++
            }
        }
        
        // Check for too consistent distances
        val distances = mutableListOf<Double>()
        for (i in 1 until recentPoints.size) {
            distances.add(recentPoints[i].distanceTo(recentPoints[i-1]))
        }
        
        // Calculate standard deviation of distances
        val mean = distances.average()
        val variance = distances.map { (it - mean).pow(2) }.average()
        val stdDev = sqrt(variance)
        
        // Low standard deviation indicates too consistent movement
        val tooConsistent = stdDev / mean < 0.1
        
        return repeatingPatterns > 2 || tooConsistent
    }
    
    /**
     * Apply anti-pattern adjustments to break suspicious patterns
     */
    private fun applyAntiPatternAdjustments(point: Vec3d): Vec3d {
        // Add non-linear variation to break patterns
        val breakFactor = 0.97 + random.nextDouble() * 0.06
        val offsetX = (random.nextDouble() - 0.5) * 0.02
        val offsetY = (random.nextDouble() - 0.5) * 0.02
        val offsetZ = (random.nextDouble() - 0.5) * 0.02
        
        return Vec3d(
            point.x * breakFactor + offsetX,
            point.y * breakFactor + offsetY,
            point.z * breakFactor + offsetZ
        )
    }
}
