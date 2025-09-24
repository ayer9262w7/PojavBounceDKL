
package net.ccbluex.liquidbounce.utils.aiming.point

import net.ccbluex.liquidbounce.config.types.nesting.Configurable
import net.ccbluex.liquidbounce.config.types.nesting.ToggleableConfigurable
import net.ccbluex.liquidbounce.event.EventListener
import net.ccbluex.liquidbounce.utils.aiming.point.features.Gaussian
import net.ccbluex.liquidbounce.utils.aiming.point.features.LazyPoint
import net.ccbluex.liquidbounce.utils.aiming.point.features.HumanizedTargeting
import net.ccbluex.liquidbounce.utils.aiming.point.preference.PreferredBoxPart
import net.ccbluex.liquidbounce.utils.aiming.point.preference.PreferredBoxPoint
import net.ccbluex.liquidbounce.utils.client.player
import net.ccbluex.liquidbounce.utils.entity.box
import net.ccbluex.liquidbounce.utils.entity.prevPos
import net.ccbluex.liquidbounce.utils.entity.sqrtSpeed
import net.ccbluex.liquidbounce.utils.math.plus
import net.minecraft.entity.LivingEntity
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * Enhanced Point Tracker with advanced anti-detection features
 * 
 * This improved version includes:
 * 1. Advanced humanized targeting with natural aiming patterns
 * 2. Dynamic point selection that varies based on context
 * 3. Enhanced Gaussian distribution with anti-pattern detection
 * 4. Adaptive box manipulation that avoids detection patterns
 * 5. Memory-based targeting that simulates human learning
 */
class PointTracker(
    highestPointDefault: PreferredBoxPart = PreferredBoxPart.HEAD,
    lowestPointDefault: PreferredBoxPart = PreferredBoxPart.BODY,
    timeEnemyOffsetDefault: Float = 0.4f,
    timeEnemyOffsetScale: ClosedFloatingPointRange<Float> = -1f..1f
) : Configurable("AimPoint", aliases = arrayOf("PointTracker")), EventListener {

    /**
     * Define the highest and lowest point of the box we want to aim at.
     */
    private val highestPoint: PreferredBoxPart by enumChoice("HighestPoint", highestPointDefault)
        .onChange { new ->
            if (lowestPoint.isHigherThan(new)) {
                lowestPoint
            } else {
                new
            }
        }
    private val lowestPoint: PreferredBoxPart by enumChoice("LowestPoint", lowestPointDefault)
        .onChange { new ->
            if (new.isHigherThan(highestPoint)) {
                highestPoint
            } else {
                new
            }
        }

    private val preferredBoxPoint by enumChoice("BoxPoint", PreferredBoxPoint.STRAIGHT)

    /**
     * The time offset defines a prediction or rather a delay of the point tracker.
     * We can either try to predict the next location of the player and use this as our newest point, or
     * we pretend to be slow in the head and aim behind.
     */
    private val timeEnemyOffset by float("TimeEnemyOffset", timeEnemyOffsetDefault, timeEnemyOffsetScale)

    /**
     * Enhanced Gaussian distribution with anti-pattern detection
     */
    private val gaussian = tree(Gaussian(this))

    /**
     * Enhanced lazy point system with natural human-like delays
     */
    private val lazyPoint = tree(LazyPoint(this))

    /**
     * New humanized targeting system that mimics human aiming patterns
     */
    private val humanizedTargeting = tree(HumanizedTargeting(this))

    /**
     * OutOfBox will set the box offset to an unreachable position.
     */
    private val outOfBox by boolean("OutOfBox", false)

    /**
     * The shrink box value will shrink the cut-off box by the given amount.
     */
    private val shrinkBox by float("ShrinkBox", 0.05f, 0.0f..0.3f)
    private val dynamicShrinkBox by boolean("DynamicShrinkBox", true)

    /**
     * New adaptive box manipulation settings
     */
    private val adaptiveBoxManipulation = tree(AdaptiveBoxManipulation())

    /**
     * Memory-based targeting that simulates human learning
     */
    private val memoryBasedTargeting = tree(MemoryBasedTargeting())

    /**
     * Target history tracking for more natural aiming patterns
     */
    private val targetHistory = mutableMapOf<Int, TargetMemory>()
    
    /**
     * Random instance with secure seed generation
     */
    private val random = Random(System.nanoTime() xor System.currentTimeMillis())

    /**
     * The point tracker is being used to track a certain point of an entity.
     *
     * @param entity The entity we want to track.
     */
    fun gatherPoint(entity: LivingEntity, situation: AimSituation): Point {
        val playerPosition = player.pos
        val playerEyes = player.eyePos
        val positionDifference = playerPosition.y - entity.pos.y

        // Get or create target memory for this entity
        val targetMemory = targetHistory.getOrPut(entity.id) { TargetMemory() }
        targetMemory.updateLastSeen()

        // Apply memory-based targeting if enabled
        val memoryOffset = if (memoryBasedTargeting.enabled) {
            memoryBasedTargeting.getMemoryOffset(entity, targetMemory)
        } else {
            Vec3d.ZERO
        }

        // Predicted target position with anti-pattern variation
        val targetVelocity = entity.pos.subtract(entity.prevPos)
        
        // Apply non-linear time offset with slight variation to avoid detection patterns
        val effectiveTimeOffset = if (adaptiveBoxManipulation.enabled) {
            adaptiveBoxManipulation.getAdaptiveTimeOffset(timeEnemyOffset, entity, situation)
        } else {
            timeEnemyOffset
        }
        
        var box = entity.box.offset(targetVelocity.multiply(effectiveTimeOffset.toDouble()))
        
        // Apply box manipulation based on situation
        if (!situation.isNear && outOfBox) {
            box = box.withMinY(box.maxY).withMaxY(box.maxY + 1.0)
        }

        // Apply adaptive box manipulation if enabled
        if (adaptiveBoxManipulation.enabled) {
            box = adaptiveBoxManipulation.manipulateBox(box, entity, situation)
        }

        val highest = (highestPoint.cutOff(box) + positionDifference)
            .coerceAtMost(box.maxY)
            .coerceAtLeast(box.minY + 1.0)
        val lowest = (lowestPoint.cutOff(box) + positionDifference)
            .coerceAtMost(box.maxY - 1.0)
            .coerceAtLeast(box.minY)

        // Calculate shrink factor with non-linear components to avoid detection
        val speedShrinkFactor = if (adaptiveBoxManipulation.enabled) {
            adaptiveBoxManipulation.calculateShrinkFactor(player, targetVelocity)
        } else {
            min(0.05, max(player.sqrtSpeed * 0.5, targetVelocity.sqrtSpeed * 0.5))
        }

        val initialCutoffBox = box
            .withMaxY(highest)
            .withMinY(lowest)
            .contract(shrinkBox.toDouble(), 0.0, shrinkBox.toDouble())

        val cutoffBox = if (dynamicShrinkBox) {
            // Add slight variation to dynamic shrink to avoid detection patterns
            val yVariation = if (adaptiveBoxManipulation.enabled) {
                abs(player.velocity.y) * (0.95 + random.nextDouble(0.1))
            } else {
                abs(player.velocity.y)
            }
            
            initialCutoffBox.contract(speedShrinkFactor, yVariation, speedShrinkFactor)
        } else {
            initialCutoffBox
        }

        val targetContext = PreferredBoxPoint.TargetContext.fromEntity(entity, playerEyes)

        // Apply Gaussian offset with enhanced anti-detection
        val offset = if (gaussian.enabled && gaussian.factorCheck()) {
            gaussian.updateGaussianOffset(entity)
            gaussian.currentOffset
        } else {
            Vec3d.ZERO
        }

        // Apply humanized targeting if enabled
        val basePoint = preferredBoxPoint.point(cutoffBox, playerEyes, targetContext)
        val targetPoint = if (humanizedTargeting.enabled) {
            humanizedTargeting.getHumanizedPoint(basePoint, cutoffBox, playerEyes, entity, situation)
        } else {
            basePoint
        }

        // Apply memory offset and Gaussian offset
        val combinedPoint = targetPoint.add(offset).add(memoryOffset)
        
        // Apply enhanced lazy point system
        val finalPoint = lazyPoint.update(combinedPoint, entity, situation)

        // Update target memory with the final point
        targetMemory.updateTargetPoint(finalPoint)

        val finalCutoffBox = Box(
            min(finalPoint.x, cutoffBox.minX),
            min(finalPoint.y, cutoffBox.minY),
            min(finalPoint.z, cutoffBox.minZ),
            max(finalPoint.x, cutoffBox.maxX),
            max(finalPoint.y, cutoffBox.maxY),
            max(finalPoint.z, cutoffBox.maxZ)
        )

        return Point(playerEyes, finalPoint, box, finalCutoffBox)
    }

    /**
     * Adaptive Box Manipulation class for advanced box manipulation techniques
     */
    private inner class AdaptiveBoxManipulation : ToggleableConfigurable(this, "AdaptiveBoxManipulation", true) {
        val boxVariation by float("BoxVariation", 0.02f, 0.0f..0.1f)
        val timeOffsetVariation by float("TimeOffsetVariation", 0.05f, 0.0f..0.2f)
        val nonLinearShrink by boolean("NonLinearShrink", true)
        val contextAwareBoxing by boolean("ContextAwareBoxing", true)
        
        /**
         * Calculate adaptive time offset with slight variations to avoid detection patterns
         */
        fun getAdaptiveTimeOffset(baseOffset: Float, entity: LivingEntity, situation: AimSituation): Float {
            val baseVariation = (random.nextDouble() - 0.5) * timeOffsetVariation * 2
            
            // Add context-aware variation based on situation
            val situationFactor = when(situation) {
                AimSituation.FOR_THE_FUTURE -> 1.2
                AimSituation.FOR_NEXT_TICK -> 1.0
                AimSituation.FOR_NOW -> 0.8
            }
            
            // Add entity-specific variation based on entity type and movement
            val entityFactor = if (entity.isOnGround) 1.0 else 1.1
            val speedFactor = (entity.velocity.lengthSquared() * 0.5 + 0.5).coerceIn(0.8, 1.2)
            
            return (baseOffset * situationFactor * entityFactor * speedFactor + baseVariation).toFloat()
        }
        
        /**
         * Manipulate box with advanced techniques to avoid detection patterns
         */
        fun manipulateBox(box: Box, entity: LivingEntity, situation: AimSituation): Box {
            if (!contextAwareBoxing) return box
            
            // Calculate variation based on entity state and situation
            val xzVariation = boxVariation * (0.8 + random.nextDouble(0.4))
            val yVariation = boxVariation * (0.7 + random.nextDouble(0.6))
            
            // Apply different variations based on situation
            return when(situation) {
                AimSituation.FOR_THE_FUTURE -> {
                    // Slightly expand box for future prediction
                    box.expand(xzVariation, yVariation, xzVariation)
                }
                AimSituation.FOR_NEXT_TICK -> {
                    // Minimal variation for next tick
                    box.expand(xzVariation * 0.5, yVariation * 0.5, xzVariation * 0.5)
                }
                AimSituation.FOR_NOW -> {
                    // Contract slightly for immediate targeting
                    if (entity.hurtTime > 0) {
                        // Expand slightly when entity is hurt to simulate human reaction
                        box.expand(xzVariation * 0.7, yVariation * 0.7, xzVariation * 0.7)
                    } else {
                        box.contract(xzVariation * 0.3, yVariation * 0.3, xzVariation * 0.3)
                    }
                }
            }
        }
        
        /**
         * Calculate shrink factor with non-linear components to avoid detection
         */
        fun calculateShrinkFactor(player: net.minecraft.client.network.ClientPlayerEntity, targetVelocity: Vec3d): Double {
            if (!nonLinearShrink) {
                return min(0.05, max(player.sqrtSpeed * 0.5, targetVelocity.sqrtSpeed * 0.5))
            }
            
            // Non-linear calculation with slight randomization
            val playerSpeedFactor = player.sqrtSpeed * (0.45 + random.nextDouble(0.1))
            val targetSpeedFactor = targetVelocity.sqrtSpeed * (0.45 + random.nextDouble(0.1))
            
            // Apply non-linear transformation to avoid detection patterns
            val combinedFactor = sqrt(playerSpeedFactor.pow(2) + targetSpeedFactor.pow(2))
            
            // Add slight random variation to break patterns
            val randomFactor = 0.98 + random.nextDouble(0.04)
            
            return min(0.05, combinedFactor * randomFactor)
        }
    }

    /**
     * Memory-based targeting that simulates human learning
     */
    private inner class MemoryBasedTargeting : ToggleableConfigurable(this, "MemoryBasedTargeting", true) {
        val learningRate by float("LearningRate", 0.05f, 0.01f..0.2f)
        val memoryDecay by float("MemoryDecay", 0.01f, 0.001f..0.05f)
        val successBias by float("SuccessBias", 0.1f, 0.0f..0.5f)
        
        /**
         * Get memory-based offset for targeting
         */
        fun getMemoryOffset(entity: LivingEntity, memory: TargetMemory): Vec3d {
            // Apply memory decay based on time since last seen
            val timeSinceLastSeen = memory.getTimeSinceLastSeen()
            val decayFactor = 1.0 - (memoryDecay * timeSinceLastSeen / 1000.0).coerceIn(0.0, 0.95)
            
            // Apply success bias if entity is hurt (successful hit)
            val successFactor = if (entity.hurtTime > 0) {
                1.0 + successBias
            } else {
                1.0
            }
            
            // Calculate memory offset with decay and success bias
            return memory.getAverageOffset().multiply(decayFactor * successFactor)
        }
    }

    /**
     * Target memory class to store information about previous targeting
     */
    private inner class TargetMemory {
        private val recentPoints = mutableListOf<Vec3d>()
        private val maxPoints = 10
        private var lastSeenTime = System.currentTimeMillis()
        
        /**
         * Update last seen time
         */
        fun updateLastSeen() {
            lastSeenTime = System.currentTimeMillis()
        }
        
        /**
         * Get time since last seen in milliseconds
         */
        fun getTimeSinceLastSeen(): Long {
            return System.currentTimeMillis() - lastSeenTime
        }
        
        /**
         * Update target point in memory
         */
        fun updateTargetPoint(point: Vec3d) {
            recentPoints.add(point)
            if (recentPoints.size > maxPoints) {
                recentPoints.removeAt(0)
            }
        }
        
        /**
         * Get average offset from recent points
         */
        fun getAverageOffset(): Vec3d {
            if (recentPoints.isEmpty()) return Vec3d.ZERO
            
            var sumX = 0.0
            var sumY = 0.0
            var sumZ = 0.0
            
            recentPoints.forEach { point ->
                sumX += point.x
                sumY += point.y
                sumZ += point.z
            }
            
            return Vec3d(
                sumX / recentPoints.size - recentPoints.last().x,
                sumY / recentPoints.size - recentPoints.last().y,
                sumZ / recentPoints.size - recentPoints.last().z
            )
        }
    }

    data class Point(val fromPoint: Vec3d, val toPoint: Vec3d, val box: Box, val cutOffBox: Box)

    enum class AimSituation {
        FOR_THE_FUTURE,
        FOR_NEXT_TICK,
        FOR_NOW;

        val isNear: Boolean
            get() = this == FOR_NEXT_TICK || this == FOR_NOW
    }
}
