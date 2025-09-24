
package net.ccbluex.liquidbounce.utils.aiming.point.features

import net.ccbluex.liquidbounce.config.types.nesting.ToggleableConfigurable
import net.ccbluex.liquidbounce.event.EventListener
import net.ccbluex.liquidbounce.features.module.modules.render.ModuleDebug
import net.ccbluex.liquidbounce.utils.aiming.point.PointTracker.AimSituation
import net.ccbluex.liquidbounce.utils.kotlin.random
import net.minecraft.entity.LivingEntity
import net.minecraft.util.math.Vec3d
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

/**
 * Enhanced Lazy Point with advanced human-like behavior
 * 
 * This improved version includes:
 * 1. Context-aware threshold adjustment based on entity state
 * 2. Human-like micro-adjustments during "lazy" periods
 * 3. Non-linear threshold calculation to avoid detection patterns
 * 4. Adaptive behavior that changes based on combat situation
 */
internal class LazyPoint(parent: EventListener) : ToggleableConfigurable(parent, "Lazy", false) {

    private val threshold by floatRange("Threshold", 0.0f..0.2f, 0.01f..1f).onChanged { range ->
        currentThreshold = range.random()
    }
    
    /**
     * New parameters for enhanced lazy point behavior
     */
    val contextAwareness by boolean("ContextAwareness", true)
    val microAdjustments by boolean("MicroAdjustments", true)
    val adaptiveBehavior by boolean("AdaptiveBehavior", true)
    val nonLinearThreshold by boolean("NonLinearThreshold", true)
    
    /**
     * Micro-adjustment parameters
     */
    private val microAdjustmentStrength by float("MicroStrength", 0.01f, 0.001f..0.05f)
    private val microAdjustmentFrequency by float("MicroFrequency", 0.5f, 0.1f..2.0f)

    private var currentThreshold: Float = threshold.random()
    private var currentPoint: Vec3d? = null
    
    /**
     * Time tracking for micro-adjustments
     */
    private var lastUpdateTime = System.currentTimeMillis()
    private var microAdjustmentPhase = 0.0
    
    /**
     * Random instance with secure seed generation
     */
    private val random = Random(System.nanoTime() xor System.currentTimeMillis())
    
    /**
     * History of recent points for pattern analysis
     */
    private val pointHistory = mutableListOf<Vec3d>()
    private val maxHistorySize = 10
    
    /**
     * Calculate context-aware threshold based on entity state and situation
     */
    private fun getContextAwareThreshold(entity: LivingEntity?, situation: AimSituation): Float {
        if (!contextAwareness || entity == null) {
            return currentThreshold
        }
        
        // Base threshold adjustment factors
        var thresholdFactor = 1.0f
        
        // Adjust based on entity state
        if (entity.hurtTime > 0) {
            // Reduce threshold when entity is hurt (more reactive)
            thresholdFactor *= 0.7f + (entity.hurtTime / 20f) * 0.3f
        }
        
        // Adjust based on entity movement
        val entitySpeed = entity.velocity.lengthSquared().toFloat()
        if (entitySpeed > 0.01f) {
            // Reduce threshold for moving targets (more reactive)
            thresholdFactor *= (1.0f - (entitySpeed * 0.5f).coerceIn(0.0f, 0.3f))
        }
        
        // Adjust based on aim situation
        thresholdFactor *= when(situation) {
            AimSituation.FOR_THE_FUTURE -> 1.2f  // Less reactive for future prediction
            AimSituation.FOR_NEXT_TICK -> 1.0f  // Normal reactivity
            AimSituation.FOR_NOW -> 0.8f        // More reactive for immediate aiming
            else -> 1.0f
        }
        
        // Apply non-linear transformation if enabled
        return if (nonLinearThreshold) {
            // Non-linear transformation to avoid detection patterns
            val baseThreshold = currentThreshold * thresholdFactor
            val nonLinearFactor = 0.9f + sin(System.currentTimeMillis() / 5000.0).toFloat() * 0.1f
            baseThreshold * nonLinearFactor
        } else {
            currentThreshold * thresholdFactor
        }
    }
    
    /**
     * Apply micro-adjustments to simulate human hand tremor
     */
    private fun applyMicroAdjustments(point: Vec3d): Vec3d {
        if (!microAdjustments) {
            return point
        }
        
        val currentTime = System.currentTimeMillis()
        val timeDelta = (currentTime - lastUpdateTime) / 1000.0
        lastUpdateTime = currentTime
        
        // Update micro-adjustment phase
        microAdjustmentPhase += microAdjustmentFrequency * timeDelta * 2 * Math.PI
        if (microAdjustmentPhase > 2 * Math.PI) {
            microAdjustmentPhase -= 2 * Math.PI
        }
        
        // Calculate micro-adjustment offset with multiple frequency components
        val baseStrength = microAdjustmentStrength * (0.8 + random.nextDouble() * 0.4)
        val offsetX = sin(microAdjustmentPhase) * baseStrength
        val offsetY = sin(microAdjustmentPhase * 1.3) * baseStrength * 0.8
        val offsetZ = sin(microAdjustmentPhase * 0.7) * baseStrength
        
        return Vec3d(
            point.x + offsetX,
            point.y + offsetY,
            point.z + offsetZ
        )
    }
    
    /**
     * Check for suspicious patterns in point history
     */
    private fun detectSuspiciousPatterns(): Boolean {
        if (pointHistory.size < 8) return false
        
        // Check for repeating patterns
        var repeatingPatterns = 0
        val recentPoints = pointHistory.takeLast(8)
        
        for (i in 0 until recentPoints.size - 2) {
            val delta1 = recentPoints[i+1].subtract(recentPoints[i])
            val delta2 = recentPoints[i+2].subtract(recentPoints[i+1])
            
            // Check if consecutive deltas are very similar
            val similarity = delta1.dotProduct(delta2) / (delta1.length() * delta2.length())
            if (similarity > 0.95) {
                repeatingPatterns++
            }
        }
        
        return repeatingPatterns > 2
    }
    
    /**
     * Apply pattern-breaking adjustments if suspicious patterns are detected
     */
    private fun applyPatternBreaking(point: Vec3d): Vec3d {
        if (!detectSuspiciousPatterns()) return point
        
        // Add non-linear variation to break patterns
        val breakFactor = 0.95 + random.nextDouble() * 0.1
        val offsetX = (random.nextDouble() - 0.5) * 0.01
        val offsetY = (random.nextDouble() - 0.5) * 0.01
        val offsetZ = (random.nextDouble() - 0.5) * 0.01
        
        return Vec3d(
            point.x * breakFactor + offsetX,
            point.y * breakFactor + offsetY,
            point.z * breakFactor + offsetZ
        )
    }
    
    /**
     * Calculate adaptive distance threshold based on point characteristics
     */
    private fun calculateAdaptiveThreshold(currentPoint: Vec3d, newPoint: Vec3d): Float {
        if (!adaptiveBehavior) {
            return currentThreshold
        }
        
        // Calculate base distance
        val distance = currentPoint.distanceTo(newPoint)
        
        // Adjust threshold based on distance magnitude
        val distanceFactor = when {
            distance < 0.1 -> 1.2f  // Increase threshold for small movements
            distance < 0.5 -> 1.0f  // Normal threshold for medium movements
            else -> 0.8f           // Decrease threshold for large movements
        }
        
        // Adjust based on movement direction
        val verticalComponent = abs(newPoint.y - currentPoint.y) / distance
        val directionFactor = if (verticalComponent > 0.7) {
            // Vertical movements are more precise in human aiming
            0.9f
        } else {
            1.0f
        }
        
        return currentThreshold * distanceFactor * directionFactor
    }

    /**
     * Update the point if the distance is greater than the threshold
     */
    fun update(point: Vec3d, entity: LivingEntity? = null, situation: AimSituation = AimSituation.FOR_NOW): Vec3d {
        if (!enabled) {
            return point
        }

        val currentPoint = currentPoint ?: run {
            this.currentPoint = point
            pointHistory.add(point)
            return point
        }
        
        // Calculate context-aware threshold
        val effectiveThreshold = if (entity != null) {
            getContextAwareThreshold(entity, situation)
        } else {
            currentThreshold
        }
        
        // Calculate adaptive threshold based on point characteristics
        val adaptiveThreshold = calculateAdaptiveThreshold(currentPoint, point)
        val finalThreshold = effectiveThreshold * adaptiveThreshold

        val distance = point.distanceTo(currentPoint)
        ModuleDebug.debugParameter(this, "Distance", distance)
        ModuleDebug.debugParameter(this, "Threshold", finalThreshold)

        // Apply micro-adjustments to current point
        val adjustedCurrentPoint = if (distance < finalThreshold) {
            // Only apply micro-adjustments when we're not updating the point
            applyMicroAdjustments(currentPoint)
        } else {
            point
        }
        
        // Check if the current point has not reached the minimum threshold to move
        if (distance < finalThreshold) {
            // Store the point in history even if we don't update
            if (pointHistory.size >= maxHistorySize) {
                pointHistory.removeAt(0)
            }
            pointHistory.add(adjustedCurrentPoint)
            
            return adjustedCurrentPoint
        }

        // Apply pattern-breaking if needed
        val finalPoint = applyPatternBreaking(point)
        
        this.currentPoint = finalPoint
        this.currentThreshold = threshold.random()
        
        // Update point history
        if (pointHistory.size >= maxHistorySize) {
            pointHistory.removeAt(0)
        }
        pointHistory.add(finalPoint)
        
        return finalPoint
    }
}
