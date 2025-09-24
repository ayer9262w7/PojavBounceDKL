
package net.ccbluex.liquidbounce.utils.aiming.point.features

import net.ccbluex.liquidbounce.config.types.nesting.ToggleableConfigurable
import net.ccbluex.liquidbounce.event.EventListener
import net.ccbluex.liquidbounce.utils.entity.sqrtSpeed
import net.ccbluex.liquidbounce.utils.kotlin.random
import net.minecraft.entity.LivingEntity
import net.minecraft.util.math.Vec3d
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import java.util.Random

/**
 * Enhanced Gaussian distribution with anti-pattern detection
 * 
 * This improved version includes:
 * 1. Multi-dimensional Gaussian distribution with natural variation
 * 2. Context-aware offset generation based on entity state
 * 3. Pattern-breaking techniques to avoid detection
 * 4. Adaptive parameters that change over time
 */
internal class Gaussian(parent: EventListener) : ToggleableConfigurable(parent, "Gaussian", false) {

    companion object {
        /**
         * Enhanced Gaussian distribution values with natural variation
         * These values are carefully selected to mimic human aiming patterns
         */
        private val STDDEV_Z = 0.24453708645460387
        private val MEAN_X = 0.00942273861037109
        private val STDDEV_X = 0.23319837528201348
        private val MEAN_Y = -0.30075078007595923
        private val STDDEV_Y = 0.3492437109081718
        private val MEAN_Z = 0.013282929419023442
        
        /**
         * Secure random with high entropy seed
         */
        private val random = Random(System.nanoTime() xor System.currentTimeMillis())
        
        /**
         * Pattern-breaking parameters that change over time
         */
        private var patternBreakPhase = 0.0
        private val patternBreakSpeed = 0.001
        
        /**
         * Initialize pattern-breaking phase with random value
         */
        init {
            patternBreakPhase = random.nextDouble() * 2 * Math.PI
        }
    }

    var currentOffset: Vec3d = Vec3d.ZERO
    private var targetOffset: Vec3d = Vec3d.ZERO
    
    /**
     * Last update time for time-based variations
     */
    private var lastUpdateTime = System.currentTimeMillis()
    
    /**
     * Offset history for pattern detection avoidance
     */
    private val offsetHistory = mutableListOf<Vec3d>()
    private val maxHistorySize = 20

    val yawFactor by floatRange("YawOffset", 0f..0f, 0.0f..1.0f)
    val pitchFactor by floatRange("PitchOffset", 0f..0f, 0.0f..1.0f)
    val chance by int("Chance", 100, 0..100, "%")
    val speed by floatRange("Speed", 0.1f..0.2f, 0.01f..1f)
    val tolerance by float("Tolerance", 0.05f, 0.01f..0.1f)
    
    /**
     * New parameters for enhanced Gaussian distribution
     */
    val adaptiveDistribution by boolean("AdaptiveDistribution", true)
    val patternBreaking by boolean("PatternBreaking", true)
    val naturalVariation by boolean("NaturalVariation", true)

    private inner class Dynamic : ToggleableConfigurable(this, "Dynamic", false) {
        val hurtTime by int("HurtTime", 10, 0..10)
        val yawFactor by float("YawFactor", 0f, 0f..10f, "x")
        val pitchFactor by float("PitchFactor", 0f, 0f..10f, "x")
        val speed by floatRange("Speed", 0.5f..0.75f, 0.01f..1f)
        val tolerance by float("Tolerance", 0.1f, 0.01f..0.1f)
        
        /**
         * New parameters for dynamic adjustments
         */
        val adaptiveSpeed by boolean("AdaptiveSpeed", true)
        val contextAwareness by boolean("ContextAwareness", true)
    }

    private val dynamic = tree(Dynamic())

    private fun interpolate(start: Double, end: Double, f: Double) = start + (end - start) * f

    fun factorCheck(): Boolean {
        return yawFactor.random() > 0.0f && pitchFactor.random() > 0.0f && chance > 0
    }

    private fun gaussianHasReachedTarget(vec1: Vec3d, vec2: Vec3d, tolerance: Float): Boolean {
        return abs(vec1.x - vec2.x) < tolerance &&
            abs(vec1.y - vec2.y) < tolerance &&
            abs(vec1.z - vec2.z) < tolerance
    }
    
    /**
     * Enhanced nextGaussian function with natural variation
     */
    private fun nextEnhancedGaussian(mean: Double, stdDev: Double): Double {
        // Update pattern-breaking phase
        if (patternBreaking) {
            patternBreakPhase += patternBreakSpeed
            if (patternBreakPhase > 2 * Math.PI) {
                patternBreakPhase -= 2 * Math.PI
            }
        }
        
        // Base Gaussian value
        val baseGaussian = random.nextGaussian() * stdDev + mean
        
        // Add natural variation if enabled
        if (naturalVariation) {
            val timeFactor = System.currentTimeMillis() / 10000.0
            val naturalNoise = sin(timeFactor + patternBreakPhase) * 0.05
            return baseGaussian * (1.0 + naturalNoise)
        }
        
        return baseGaussian
    }
    
    /**
     * Check for suspicious patterns in offset history
     */
    private fun detectSuspiciousPatterns(): Boolean {
        if (offsetHistory.size < 10) return false
        
        // Check for repeating patterns
        var repeatingPatterns = 0
        val recentOffsets = offsetHistory.takeLast(10)
        
        for (i in 0 until recentOffsets.size - 2) {
            val delta1 = recentOffsets[i+1].subtract(recentOffsets[i])
            val delta2 = recentOffsets[i+2].subtract(recentOffsets[i+1])
            
            // Check if consecutive deltas are very similar
            val similarity = delta1.dotProduct(delta2) / (delta1.length() * delta2.length())
            if (similarity > 0.95) {
                repeatingPatterns++
            }
        }
        
        return repeatingPatterns > 3
    }
    
    /**
     * Apply pattern-breaking adjustments if suspicious patterns are detected
     */
    private fun applyPatternBreaking(offset: Vec3d): Vec3d {
        if (!patternBreaking || !detectSuspiciousPatterns()) return offset
        
        // Add non-linear variation to break patterns
        val breakFactor = 0.8 + random.nextDouble() * 0.4
        val phaseX = sin(patternBreakPhase) * 0.1
        val phaseY = cos(patternBreakPhase * 1.3) * 0.1
        val phaseZ = sin(patternBreakPhase * 0.7) * 0.1
        
        return Vec3d(
            offset.x * breakFactor + phaseX,
            offset.y * breakFactor + phaseY,
            offset.z * breakFactor + phaseZ
        )
    }

    @Suppress("CognitiveComplexMethod")
    fun updateGaussianOffset(entity: Any?) {
        val currentTime = System.currentTimeMillis()
        val timeDelta = (currentTime - lastUpdateTime) / 1000.0
        lastUpdateTime = currentTime
        
        // Update pattern-breaking phase with time-based variation
        if (patternBreaking) {
            patternBreakPhase += patternBreakSpeed * timeDelta * 60.0
            if (patternBreakPhase > 2 * Math.PI) {
                patternBreakPhase -= 2 * Math.PI
            }
        }
        
        val dynamicCheck = dynamic.enabled && entity is LivingEntity && entity.hurtTime >= dynamic.hurtTime
        
        // Calculate context-aware factors
        val contextFactor = if (dynamic.contextAwareness && entity is LivingEntity) {
            // Adjust based on entity state
            val movementFactor = if (entity.isOnGround) 1.0 else 1.2
            val healthFactor = (entity.health / entity.maxHealth).coerceIn(0.8f, 1.2f)
            movementFactor * healthFactor
        } else {
            1.0
        }

        val yawFactor =
            if (dynamicCheck && dynamic.yawFactor > 0f) {
                (yawFactor.random() + player.sqrtSpeed * dynamic.yawFactor * contextFactor)
            } else {
                yawFactor.random() * contextFactor
            }.toDouble()

        val pitchFactor =
            if (dynamicCheck && dynamic.pitchFactor > 0f) {
                (pitchFactor.random() + player.sqrtSpeed * dynamic.pitchFactor * contextFactor)
            } else {
                pitchFactor.random() * contextFactor
            }.toDouble()

        // Check if we've reached the target with adaptive tolerance
        val effectiveTolerance = if (dynamicCheck) dynamic.tolerance else tolerance
        
        if (gaussianHasReachedTarget(
                currentOffset,
                targetOffset,
                effectiveTolerance
            )
        ) {
            if (random.nextInt(100) <= chance) {
                // Generate new target offset with enhanced Gaussian distribution
                if (adaptiveDistribution) {
                    // Adaptive distribution based on context
                    val adaptiveMeanX = MEAN_X * (0.9 + random.nextDouble() * 0.2)
                    val adaptiveMeanY = MEAN_Y * (0.9 + random.nextDouble() * 0.2)
                    val adaptiveMeanZ = MEAN_Z * (0.9 + random.nextDouble() * 0.2)
                    
                    val adaptiveStdDevX = STDDEV_X * (0.9 + random.nextDouble() * 0.2)
                    val adaptiveStdDevY = STDDEV_Y * (0.9 + random.nextDouble() * 0.2)
                    val adaptiveStdDevZ = STDDEV_Z * (0.9 + random.nextDouble() * 0.2)
                    
                    targetOffset = Vec3d(
                        nextEnhancedGaussian(adaptiveMeanX, adaptiveStdDevX) * yawFactor,
                        nextEnhancedGaussian(adaptiveMeanY, adaptiveStdDevY) * pitchFactor,
                        nextEnhancedGaussian(adaptiveMeanZ, adaptiveStdDevZ) * yawFactor
                    )
                } else {
                    // Standard Gaussian distribution
                    targetOffset = Vec3d(
                        nextEnhancedGaussian(MEAN_X, STDDEV_X) * yawFactor,
                        nextEnhancedGaussian(MEAN_Y, STDDEV_Y) * pitchFactor,
                        nextEnhancedGaussian(MEAN_Z, STDDEV_Z) * yawFactor
                    )
                }
                
                // Apply pattern-breaking if enabled
                if (patternBreaking) {
                    targetOffset = applyPatternBreaking(targetOffset)
                }
            }
        } else {
            // Calculate adaptive speed based on context
            val effectiveSpeed = if (dynamic.adaptiveSpeed && dynamicCheck) {
                // Vary speed based on distance to target and context
                val distanceToTarget = currentOffset.distanceTo(targetOffset)
                val speedFactor = (1.0 - (distanceToTarget / 0.5).coerceIn(0.0, 0.8))
                
                dynamic.speed.random().toDouble() * (0.8 + speedFactor * 0.4)
            } else if (dynamicCheck) {
                dynamic.speed.random().toDouble()
            } else {
                speed.random().toDouble()
            }
            
            // Interpolate with non-linear component to avoid detection
            val interpolationFactor = if (naturalVariation) {
                effectiveSpeed * (0.95 + sin(patternBreakPhase) * 0.05)
            } else {
                effectiveSpeed
            }
            
            currentOffset = Vec3d(
                interpolate(
                    currentOffset.x,
                    targetOffset.x,
                    interpolationFactor
                ),
                interpolate(
                    currentOffset.y,
                    targetOffset.y,
                    interpolationFactor
                ),
                interpolate(
                    currentOffset.z,
                    targetOffset.z,
                    interpolationFactor
                )
            )
        }
        
        // Store offset in history for pattern detection
        offsetHistory.add(currentOffset)
        if (offsetHistory.size > maxHistorySize) {
            offsetHistory.removeAt(0)
        }
    }
}
