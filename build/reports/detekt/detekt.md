# detekt

## Metrics

* 10,493 number of properties

* 4,158 number of functions

* 1,838 number of classes

* 274 number of packages

* 1,094 number of kt files

## Complexity Report

* 127,438 lines of code (loc)

* 78,576 source lines of code (sloc)

* 53,971 logical lines of code (lloc)

* 29,494 comment lines of code (cloc)

* 13,991 cyclomatic complexity (mcc)

* 6,353 cognitive complexity

* 57 number of total code smells

* 37% comment source ratio

* 259 mcc per 1,000 lloc

* 1 code smells per 1,000 lloc

## Findings (57)

### complexity, CognitiveComplexMethod (3)

Prefer splitting up complex methods into smaller, easier to understand methods.

[Documentation](https://detekt.dev/docs/rules/complexity#cognitivecomplexmethod)

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/features/module/modules/combat/killaura/ModuleKillAura.kt:344:17
```
The function updateTarget appears to be too complex based on Cognitive Complexity (complexity: 24). Defined complexity threshold for methods is set to '16'
```
```kotlin
341         }
342     }
343 
344     private fun updateTarget() {
!!!                 ^ error
345         val situation = when {
346             clickScheduler.isClickTick || clickScheduler.willClickAt(1)
347                 -> PointTracker.AimSituation.FOR_NEXT_TICK

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/features/processors/anglesmooth/impl/InterpolationAngleSmooth.kt:646:13
```
The function noise appears to be too complex based on Cognitive Complexity (complexity: 18). Defined complexity threshold for methods is set to '16'
```
```kotlin
643         private val permMod12 = IntArray(512) { i -> perm[i] % 12 }
644         
645         // Basic simplex noise function
646         fun noise(x: Double, y: Double, z: Double = 0.0): Double {
!!!             ^ error
647             val s = (x + y + z) * 0.3333333333333333
648             val i = floor(x + s).toInt()
649             val j = floor(y + s).toInt()

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/point/PointTracker.kt:124:9
```
The function gatherPoint appears to be too complex based on Cognitive Complexity (complexity: 19). Defined complexity threshold for methods is set to '16'
```
```kotlin
121      *
122      * @param entity The entity we want to track.
123      */
124     fun gatherPoint(entity: LivingEntity, situation: AimSituation): Point {
!!!         ^ error
125         val playerPosition = player.pos
126         val playerEyes = player.eyePos
127         val positionDifference = playerPosition.y - entity.pos.y

```

### complexity, LongMethod (3)

One method should have one responsibility. Long methods tend to handle many things at once. Prefer smaller methods to make them easier to understand.

[Documentation](https://detekt.dev/docs/rules/complexity#longmethod)

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/features/processors/anglesmooth/impl/InterpolationAngleSmooth.kt:1194:18
```
The function process is too long (123). The maximum length is 60.
```
```kotlin
1191     }
1192     
1193     // Main processing function
1194     override fun process(rotationTarget: RotationTarget, currentRotation: Rotation, targetRotation: Rotation): Rotation {
!!!!                  ^ error
1195         val now = System.currentTimeMillis()
1196         val timeDelta = (now - lastUpdateTime).coerceAtLeast(1) / 1000f
1197         lastUpdateTime = now

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/point/PointTracker.kt:124:9
```
The function gatherPoint is too long (75). The maximum length is 60.
```
```kotlin
121      *
122      * @param entity The entity we want to track.
123      */
124     fun gatherPoint(entity: LivingEntity, situation: AimSituation): Point {
!!!         ^ error
125         val playerPosition = player.pos
126         val playerEyes = player.eyePos
127         val positionDifference = playerPosition.y - entity.pos.y

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/point/features/Gaussian.kt:182:9
```
The function updateGaussianOffset is too long (99). The maximum length is 60.
```
```kotlin
179     }
180 
181     @Suppress("CognitiveComplexMethod")
182     fun updateGaussianOffset(entity: Any?) {
!!!         ^ error
183         val currentTime = System.currentTimeMillis()
184         val timeDelta = (currentTime - lastUpdateTime) / 1000.0
185         lastUpdateTime = currentTime

```

### complexity, LongParameterList (3)

The more parameters a function has the more complex it is. Long parameter lists are often used to control complex algorithms and violate the Single Responsibility Principle. Prefer functions with short parameter lists.

[Documentation](https://detekt.dev/docs/rules/complexity#longparameterlist)

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/features/module/modules/render/gui/ClickGuiPanelWidgetFactory.kt:565:52
```
The function createToggleableSectionHeaderWidget(value: net.ccbluex.liquidbounce.config.types.nesting.ToggleableConfigurable, widgetX: Int, widgetY: Int, widgetWidth: Int, expandedSections: Map<String, Boolean>, module: ClientModule) has too many parameters. The current threshold is set to 6.
```
```kotlin
562         )
563     }
564 
565     private fun createToggleableSectionHeaderWidget(
!!!                                                    ^ error
566         value: net.ccbluex.liquidbounce.config.types.nesting.ToggleableConfigurable,
567         widgetX: Int,
568         widgetY: Int,

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/features/processors/anglesmooth/impl/InterpolationAngleSmooth.kt:494:41
```
The function processWithOptimizations(currentYaw: Float, currentPitch: Float, targetYaw: Float, targetPitch: Float, distance: Float, workingVec: Triple<Double, Double, Double>, workingRotation: Pair<Float, Float>) has too many parameters. The current threshold is set to 6.
```
```kotlin
491         return result
492     }
493     
494     private fun processWithOptimizations(
!!!                                         ^ error
495         currentYaw: Float,
496         currentPitch: Float,
497         targetYaw: Float,

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/features/processors/anglesmooth/impl/InterpolationAngleSmooth.kt:715:16
```
The function fbm(x: Double, y: Double, z: Double, octaves: Int, amplitudeMod: Double, frequencyMod: Double, lacunarity: Double, persistence: Double) has too many parameters. The current threshold is set to 6.
```
```kotlin
712         }
713         
714         // Enhanced fractal Brownian motion with variable parameters
715         fun fbm(
!!!                ^ error
716             x: Double, 
717             y: Double, 
718             z: Double = 0.0, 

```

### complexity, NestedBlockDepth (1)

Excessive nesting leads to hidden complexity. Prefer extracting code to make it easier to understand.

[Documentation](https://detekt.dev/docs/rules/complexity#nestedblockdepth)

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/features/module/modules/combat/killaura/ModuleKillAura.kt:344:17
```
Function updateTarget is nested too deeply.
```
```kotlin
341         }
342     }
343 
344     private fun updateTarget() {
!!!                 ^ error
345         val situation = when {
346             clickScheduler.isClickTick || clickScheduler.willClickAt(1)
347                 -> PointTracker.AimSituation.FOR_NEXT_TICK

```

### complexity, TooManyFunctions (1)

Too many functions inside a/an file/class/object/interface always indicate a violation of the single responsibility principle. Maybe the file/class/object/interface wants to manage too many things at once. Extract functionality which clearly belongs together.

[Documentation](https://detekt.dev/docs/rules/complexity#toomanyfunctions)

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/features/processors/anglesmooth/impl/InterpolationAngleSmooth.kt:22:7
```
Class 'InterpolationAngleSmooth' with '17' functions detected. Defined threshold inside classes is set to '11'
```
```kotlin
19  * 4. Utilizing context-aware aim behavior that adapts to different scenarios
20  * 5. Incorporating machine-learning-inspired randomization to avoid pattern detection
21  */
22 class InterpolationAngleSmooth(name: String, parent: ChoiceConfigurable<*>) : AngleSmooth(name, parent) {
!!       ^ error
23 
24     // Enhanced state management with additional parameters for anti-detection
25     private data class AimingState(

```

### naming, MatchingDeclarationName (1)

If a source file contains only a single non-private top-level class or object, the file name should reflect the case-sensitive name plus the .kt extension.

[Documentation](https://detekt.dev/docs/rules/naming#matchingdeclarationname)

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/point/features/humanized_targeting.kt:29:16
```
The file name 'humanized_targeting' does not match the name of the single top-level declaration 'HumanizedTargeting'.
```
```kotlin
26  * 3. Context-aware aiming adjustments
27  * 4. Anti-pattern techniques to avoid detection
28  */
29 internal class HumanizedTargeting(parent: EventListener) : ToggleableConfigurable(parent, "HumanizedTargeting", true) {
!!                ^ error
30 
31     /**
32      * Configuration parameters

```

### style, BracesOnIfStatements (12)

Braces do not comply with the specified policy

[Documentation](https://detekt.dev/docs/rules/style#bracesonifstatements)

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/features/processors/anglesmooth/impl/InterpolationAngleSmooth.kt:376:36
```
Missing braces on this branch, add them.
```
```kotlin
373         when {
374             hitRate > 0.8f && averageReactionTime < 200 -> {
375                 // Performing well, might be experienced or competitive
376                 currentArchetype = if (random.nextBoolean()) 
!!!                                    ^ error
377                     PlayerArchetype.EXPERIENCED else PlayerArchetype.COMPETITIVE
378             }
379             hitRate < 0.3f -> {

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/features/processors/anglesmooth/impl/InterpolationAngleSmooth.kt:381:36
```
Missing braces on this branch, add them.
```
```kotlin
378             }
379             hitRate < 0.3f -> {
380                 // Performing poorly, might be nervous or tired
381                 currentArchetype = if (random.nextBoolean()) 
!!!                                    ^ error
382                     PlayerArchetype.NERVOUS else PlayerArchetype.TIRED
383             }
384             else -> {

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/features/processors/anglesmooth/impl/InterpolationAngleSmooth.kt:660:17
```
Missing braces on this branch, add them.
```
```kotlin
657             val z0 = z - Z0
658             
659             val (i1, j1, k1, i2, j2, k2) = if (x0 >= y0) {
660                 if (y0 >= z0)      SixInts(1, 0, 0, 1, 1, 0)
!!!                 ^ error
661                 else if (x0 >= z0) SixInts(1, 0, 0, 1, 0, 1)
662                 else               SixInts(0, 0, 1, 1, 0, 1)
663             } else {

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/features/processors/anglesmooth/impl/InterpolationAngleSmooth.kt:661:22
```
Missing braces on this branch, add them.
```
```kotlin
658             
659             val (i1, j1, k1, i2, j2, k2) = if (x0 >= y0) {
660                 if (y0 >= z0)      SixInts(1, 0, 0, 1, 1, 0)
661                 else if (x0 >= z0) SixInts(1, 0, 0, 1, 0, 1)
!!!                      ^ error
662                 else               SixInts(0, 0, 1, 1, 0, 1)
663             } else {
664                 if (y0 < z0)       SixInts(0, 0, 1, 0, 1, 1)

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/features/processors/anglesmooth/impl/InterpolationAngleSmooth.kt:662:17
```
Missing braces on this branch, add them.
```
```kotlin
659             val (i1, j1, k1, i2, j2, k2) = if (x0 >= y0) {
660                 if (y0 >= z0)      SixInts(1, 0, 0, 1, 1, 0)
661                 else if (x0 >= z0) SixInts(1, 0, 0, 1, 0, 1)
662                 else               SixInts(0, 0, 1, 1, 0, 1)
!!!                 ^ error
663             } else {
664                 if (y0 < z0)       SixInts(0, 0, 1, 0, 1, 1)
665                 else if (x0 < z0)  SixInts(0, 1, 0, 0, 1, 1)

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/features/processors/anglesmooth/impl/InterpolationAngleSmooth.kt:664:17
```
Missing braces on this branch, add them.
```
```kotlin
661                 else if (x0 >= z0) SixInts(1, 0, 0, 1, 0, 1)
662                 else               SixInts(0, 0, 1, 1, 0, 1)
663             } else {
664                 if (y0 < z0)       SixInts(0, 0, 1, 0, 1, 1)
!!!                 ^ error
665                 else if (x0 < z0)  SixInts(0, 1, 0, 0, 1, 1)
666                 else               SixInts(0, 1, 0, 1, 1, 0)
667             }

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/features/processors/anglesmooth/impl/InterpolationAngleSmooth.kt:665:22
```
Missing braces on this branch, add them.
```
```kotlin
662                 else               SixInts(0, 0, 1, 1, 0, 1)
663             } else {
664                 if (y0 < z0)       SixInts(0, 0, 1, 0, 1, 1)
665                 else if (x0 < z0)  SixInts(0, 1, 0, 0, 1, 1)
!!!                      ^ error
666                 else               SixInts(0, 1, 0, 1, 1, 0)
667             }
668             

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/features/processors/anglesmooth/impl/InterpolationAngleSmooth.kt:666:17
```
Missing braces on this branch, add them.
```
```kotlin
663             } else {
664                 if (y0 < z0)       SixInts(0, 0, 1, 0, 1, 1)
665                 else if (x0 < z0)  SixInts(0, 1, 0, 0, 1, 1)
666                 else               SixInts(0, 1, 0, 1, 1, 0)
!!!                 ^ error
667             }
668             
669             val x1 = x0 - i1 + 0.16666666666666666

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/features/processors/anglesmooth/impl/InterpolationAngleSmooth.kt:684:22
```
Missing braces on this branch, add them.
```
```kotlin
681             val kk = k and 255
682             
683             var t0 = 0.6 - x0 * x0 - y0 * y0 - z0 * z0
684             val n0 = if (t0 < 0) 0.0 else {
!!!                      ^ error
685                 t0 *= t0
686                 t0 * t0 * dot(grad3[permMod12[ii + perm[jj + perm[kk]]]], x0, y0, z0)
687             }

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/features/processors/anglesmooth/impl/InterpolationAngleSmooth.kt:690:22
```
Missing braces on this branch, add them.
```
```kotlin
687             }
688             
689             var t1 = 0.6 - x1 * x1 - y1 * y1 - z1 * z1
690             val n1 = if (t1 < 0) 0.0 else {
!!!                      ^ error
691                 t1 *= t1
692                 t1 * t1 * dot(grad3[permMod12[ii + i1 + perm[jj + j1 + perm[kk + k1]]]], x1, y1, z1)
693             }

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/features/processors/anglesmooth/impl/InterpolationAngleSmooth.kt:696:22
```
Missing braces on this branch, add them.
```
```kotlin
693             }
694             
695             var t2 = 0.6 - x2 * x2 - y2 * y2 - z2 * z2
696             val n2 = if (t2 < 0) 0.0 else {
!!!                      ^ error
697                 t2 *= t2
698                 t2 * t2 * dot(grad3[permMod12[ii + i2 + perm[jj + j2 + perm[kk + k2]]]], x2, y2, z2)
699             }

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/features/processors/anglesmooth/impl/InterpolationAngleSmooth.kt:702:22
```
Missing braces on this branch, add them.
```
```kotlin
699             }
700             
701             var t3 = 0.6 - x3 * x3 - y3 * y3 - z3 * z3
702             val n3 = if (t3 < 0) 0.0 else {
!!!                      ^ error
703                 t3 *= t3
704                 t3 * t3 * dot(grad3[permMod12[ii + 1 + perm[jj + 1 + perm[kk + 1]]]], x3, y3, z3)
705             }

```

### style, DestructuringDeclarationWithTooManyEntries (2)

Too many entries in a destructuring declaration make the code hard to understand.

[Documentation](https://detekt.dev/docs/rules/style#destructuringdeclarationwithtoomanyentries)

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/features/processors/anglesmooth/impl/InterpolationAngleSmooth.kt:659:13
```
The destructuring declaration contains 6 but only 3 are allowed.
```
```kotlin
656             val y0 = y - Y0
657             val z0 = z - Z0
658             
659             val (i1, j1, k1, i2, j2, k2) = if (x0 >= y0) {
!!!             ^ error
660                 if (y0 >= z0)      SixInts(1, 0, 0, 1, 1, 0)
661                 else if (x0 >= z0) SixInts(1, 0, 0, 1, 0, 1)
662                 else               SixInts(0, 0, 1, 1, 0, 1)

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/features/processors/anglesmooth/impl/InterpolationAngleSmooth.kt:780:13
```
The destructuring declaration contains 5 but only 3 are allowed.
```
```kotlin
777             strength: Float
778         ): Pair<Double, Double> {
779             // Different profiles have different micro-movement characteristics
780             val (freqX, freqY, ampX, ampY, speedMod) = when (profile) {
!!!             ^ error
781                 0 -> FiveDoubles(12.0, 14.0, 0.08, 0.06, 1.0)  // Precise, small movements
782                 1 -> FiveDoubles(8.0, 10.0, 0.12, 0.09, 1.2)   // Slightly larger movements
783                 2 -> FiveDoubles(15.0, 18.0, 0.05, 0.04, 0.8)  // Very fine, fast movements

```

### style, MaxLineLength (9)

Line detected, which is longer than the defined maximum line length in the code style.

[Documentation](https://detekt.dev/docs/rules/style#maxlinelength)

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/features/processors/anglesmooth/impl/InterpolationAngleSmooth.kt:820:1
```
Line detected, which is longer than the defined maximum line length in the code style.
```
```kotlin
817     }
818     
819     // Enhanced hitbox estimation with dynamic adjustment
820     private fun estimateHitboxSize(targetYaw: Float, targetPitch: Float, currentYaw: Float, currentPitch: Float): Float {
!!! ^ error
821         if (targetHistory.size < 5) return 1.0f
822         
823         val recentDeltas = targetHistory.takeLast(5).map { pair ->

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/features/processors/anglesmooth/impl/InterpolationAngleSmooth.kt:1194:1
```
Line detected, which is longer than the defined maximum line length in the code style.
```
```kotlin
1191     }
1192     
1193     // Main processing function
1194     override fun process(rotationTarget: RotationTarget, currentRotation: Rotation, targetRotation: Rotation): Rotation {
!!!! ^ error
1195         val now = System.currentTimeMillis()
1196         val timeDelta = (now - lastUpdateTime).coerceAtLeast(1) / 1000f
1197         lastUpdateTime = now

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/features/processors/anglesmooth/impl/InterpolationAngleSmooth.kt:1386:1
```
Line detected, which is longer than the defined maximum line length in the code style.
```
```kotlin
1383             tempHistory.add(Pair(targetRotation.yaw, targetRotation.pitch))
1384             if (tempHistory.size > 15) tempHistory.removeAt(0)
1385 
1386             val (predictedYaw, predictedPitch) = predictMovement(targetRotation.yaw, targetRotation.pitch, tempState.lastDistanceToTarget)
!!!! ^ error
1387 
1388             val deltaYaw = calculateShortestDelta(simRotation.yaw, predictedYaw)
1389             val deltaPitch = predictedPitch - simRotation.pitch

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/features/processors/anglesmooth/impl/InterpolationAngleSmooth.kt:1401:1
```
Line detected, which is longer than the defined maximum line length in the code style.
```
```kotlin
1398             val newVelocity = angleMagnitude / timeDelta
1399             tempState.velocity = tempState.velocity * 0.65f + newVelocity * 0.35f
1400             tempState.acceleration = (tempState.velocity - tempState.acceleration) * 0.25f
1401             tempState.fatigue = (tempState.fatigue + (angleMagnitude / 650f).coerceIn(0f, 0.022f)).coerceIn(0f, 1f) * 0.997f
!!!! ^ error
1402 
1403             val smoothFactor = if (tempState.velocity > 120f && angleMagnitude > 30f) {
1404                 val speedFactor = (tempState.velocity / 220f).pow(1.1f).coerceIn(0.5f, 2.2f)

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/point/PointTracker.kt:297:1
```
Line detected, which is longer than the defined maximum line length in the code style.
```
```kotlin
294         /**
295          * Calculate shrink factor with non-linear components to avoid detection
296          */
297         fun calculateShrinkFactor(player: net.minecraft.client.network.ClientPlayerEntity, targetVelocity: Vec3d): Double {
!!! ^ error
298             if (!nonLinearShrink) {
299                 return min(0.05, max(player.sqrtSpeed * 0.5, targetVelocity.sqrtSpeed * 0.5))
300             }

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/point/preference/PreferredBoxPoint.kt:43:1
```
Line detected, which is longer than the defined maximum line length in the code style.
```
```kotlin
40  * 4. Realistic targeting distribution that mimics human aiming
41  */
42 @Suppress("unused")
43 enum class PreferredBoxPoint(override val choiceName: String, val point: (Box, Vec3d, TargetContext?) -> Vec3d) : NamedChoice {
!! ^ error
44     CLOSEST("Closest", { box, eyes, context ->
45         // Enhanced closest point with slight natural variation
46         val basePoint = Vec3d(

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/point/preference/PreferredBoxPoint.kt:113:1
```
Line detected, which is longer than the defined maximum line length in the code style.
```
```kotlin
110                     val randomBias = 1.0 - centerBias
111                     
112                     Vec3d(
113                         box.center.x * centerBias + (box.minX + context.random.nextDouble() * (box.maxX - box.minX)) * randomBias,
!!! ^ error
114                         box.center.y * centerBias + (box.minY + context.random.nextDouble() * (box.maxY - box.minY)) * randomBias,
115                         box.center.z * centerBias + (box.minZ + context.random.nextDouble() * (box.maxZ - box.minZ)) * randomBias
116                     )

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/point/preference/PreferredBoxPoint.kt:114:1
```
Line detected, which is longer than the defined maximum line length in the code style.
```
```kotlin
111                     
112                     Vec3d(
113                         box.center.x * centerBias + (box.minX + context.random.nextDouble() * (box.maxX - box.minX)) * randomBias,
114                         box.center.y * centerBias + (box.minY + context.random.nextDouble() * (box.maxY - box.minY)) * randomBias,
!!! ^ error
115                         box.center.z * centerBias + (box.minZ + context.random.nextDouble() * (box.maxZ - box.minZ)) * randomBias
116                     )
117                 }

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/point/preference/PreferredBoxPoint.kt:115:1
```
Line detected, which is longer than the defined maximum line length in the code style.
```
```kotlin
112                     Vec3d(
113                         box.center.x * centerBias + (box.minX + context.random.nextDouble() * (box.maxX - box.minX)) * randomBias,
114                         box.center.y * centerBias + (box.minY + context.random.nextDouble() * (box.maxY - box.minY)) * randomBias,
115                         box.center.z * centerBias + (box.minZ + context.random.nextDouble() * (box.maxZ - box.minZ)) * randomBias
!!! ^ error
116                     )
117                 }
118                 context.distanceToTarget < 3.0 -> {

```

### style, MayBeConst (6)

Usage of `vals` that can be `const val` detected.

[Documentation](https://detekt.dev/docs/rules/style#maybeconst)

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/point/features/Gaussian.kt:32:21
```
STDDEV_Z can be a `const val`.
```
```kotlin
29          * Enhanced Gaussian distribution values with natural variation
30          * These values are carefully selected to mimic human aiming patterns
31          */
32         private val STDDEV_Z = 0.24453708645460387
!!                     ^ error
33         private val MEAN_X = 0.00942273861037109
34         private val STDDEV_X = 0.23319837528201348
35         private val MEAN_Y = -0.30075078007595923

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/point/features/Gaussian.kt:33:21
```
MEAN_X can be a `const val`.
```
```kotlin
30          * These values are carefully selected to mimic human aiming patterns
31          */
32         private val STDDEV_Z = 0.24453708645460387
33         private val MEAN_X = 0.00942273861037109
!!                     ^ error
34         private val STDDEV_X = 0.23319837528201348
35         private val MEAN_Y = -0.30075078007595923
36         private val STDDEV_Y = 0.3492437109081718

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/point/features/Gaussian.kt:34:21
```
STDDEV_X can be a `const val`.
```
```kotlin
31          */
32         private val STDDEV_Z = 0.24453708645460387
33         private val MEAN_X = 0.00942273861037109
34         private val STDDEV_X = 0.23319837528201348
!!                     ^ error
35         private val MEAN_Y = -0.30075078007595923
36         private val STDDEV_Y = 0.3492437109081718
37         private val MEAN_Z = 0.013282929419023442

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/point/features/Gaussian.kt:36:21
```
STDDEV_Y can be a `const val`.
```
```kotlin
33         private val MEAN_X = 0.00942273861037109
34         private val STDDEV_X = 0.23319837528201348
35         private val MEAN_Y = -0.30075078007595923
36         private val STDDEV_Y = 0.3492437109081718
!!                     ^ error
37         private val MEAN_Z = 0.013282929419023442
38         
39         /**

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/point/features/Gaussian.kt:37:21
```
MEAN_Z can be a `const val`.
```
```kotlin
34         private val STDDEV_X = 0.23319837528201348
35         private val MEAN_Y = -0.30075078007595923
36         private val STDDEV_Y = 0.3492437109081718
37         private val MEAN_Z = 0.013282929419023442
!!                     ^ error
38         
39         /**
40          * Secure random with high entropy seed

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/point/features/Gaussian.kt:48:21
```
patternBreakSpeed can be a `const val`.
```
```kotlin
45          * Pattern-breaking parameters that change over time
46          */
47         private var patternBreakPhase = 0.0
48         private val patternBreakSpeed = 0.001
!!                     ^ error
49         
50         /**
51          * Initialize pattern-breaking phase with random value

```

### style, NewLineAtEndOfFile (3)

Checks whether files end with a line separator.

[Documentation](https://detekt.dev/docs/rules/style#newlineatendoffile)

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/features/module/modules/render/ModuleHud.kt:56:2
```
The file /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/features/module/modules/render/ModuleHud.kt is not ending with a new line.
```
```kotlin
53             element.renderPreview(event.context)
54         }
55     }
56 }
!!  ^ error

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/features/module/modules/world/scaffold/ScaffoldRotationsConfigurable.kt:23:101
```
The file /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/features/module/modules/world/scaffold/ScaffoldRotationsConfigurable.kt is not ending with a new line.
```
```kotlin
20 
21 import net.ccbluex.liquidbounce.utils.aiming.RotationsConfigurable
22 
23 object ScaffoldRotationsConfigurable : RotationsConfigurable(ModuleScaffold, combatSpecific = false)
!!                                                                                                     ^ error

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/timer/MSTimer.kt:19:2
```
The file /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/timer/MSTimer.kt is not ending with a new line.
```
```kotlin
16     fun reset() {
17         time = System.currentTimeMillis()
18     }
19 }
!!  ^ error

```

### style, UnusedParameter (9)

Function parameter is unused and should be removed.

[Documentation](https://detekt.dev/docs/rules/style#unusedparameter)

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/features/processors/anglesmooth/impl/InterpolationAngleSmooth.kt:499:9
```
Function parameter `distance` is unused.
```
```kotlin
496         currentPitch: Float,
497         targetYaw: Float,
498         targetPitch: Float,
499         distance: Float,
!!!         ^ error
500         workingVec: Triple<Double, Double, Double>,
501         workingRotation: Pair<Float, Float>
502     ): ProcessingResult {

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/features/processors/anglesmooth/impl/InterpolationAngleSmooth.kt:500:9
```
Function parameter `workingVec` is unused.
```
```kotlin
497         targetYaw: Float,
498         targetPitch: Float,
499         distance: Float,
500         workingVec: Triple<Double, Double, Double>,
!!!         ^ error
501         workingRotation: Pair<Float, Float>
502     ): ProcessingResult {
503         

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/features/processors/anglesmooth/impl/InterpolationAngleSmooth.kt:501:9
```
Function parameter `workingRotation` is unused.
```
```kotlin
498         targetPitch: Float,
499         distance: Float,
500         workingVec: Triple<Double, Double, Double>,
501         workingRotation: Pair<Float, Float>
!!!         ^ error
502     ): ProcessingResult {
503         
504         // Check cache for distance calculations

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/features/processors/anglesmooth/impl/InterpolationAngleSmooth.kt:569:42
```
Function parameter `frameDelta` is unused.
```
```kotlin
566         )
567     }
568     
569     private fun adaptProcessingIntensity(frameDelta: Float) {
!!!                                          ^ error
570         val targetFrameTime = 16.67f // 60 FPS
571         val averageFrameTime = frameTimeHistory.average().toFloat()
572         

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/features/processors/anglesmooth/impl/InterpolationAngleSmooth.kt:820:36
```
Function parameter `targetYaw` is unused.
```
```kotlin
817     }
818     
819     // Enhanced hitbox estimation with dynamic adjustment
820     private fun estimateHitboxSize(targetYaw: Float, targetPitch: Float, currentYaw: Float, currentPitch: Float): Float {
!!!                                    ^ error
821         if (targetHistory.size < 5) return 1.0f
822         
823         val recentDeltas = targetHistory.takeLast(5).map { pair ->

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/features/processors/anglesmooth/impl/InterpolationAngleSmooth.kt:820:54
```
Function parameter `targetPitch` is unused.
```
```kotlin
817     }
818     
819     // Enhanced hitbox estimation with dynamic adjustment
820     private fun estimateHitboxSize(targetYaw: Float, targetPitch: Float, currentYaw: Float, currentPitch: Float): Float {
!!!                                                      ^ error
821         if (targetHistory.size < 5) return 1.0f
822         
823         val recentDeltas = targetHistory.takeLast(5).map { pair ->

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/point/features/humanized_targeting.kt:186:9
```
Function parameter `basePoint` is unused.
```
```kotlin
183      * Calculate humanized target point with natural aiming patterns
184      */
185     private fun calculateHumanizedTargetPoint(
186         basePoint: Vec3d,
!!!         ^ error
187         box: Box,
188         playerEyes: Vec3d,
189         entity: LivingEntity,

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/point/features/humanized_targeting.kt:188:9
```
Function parameter `playerEyes` is unused.
```
```kotlin
185     private fun calculateHumanizedTargetPoint(
186         basePoint: Vec3d,
187         box: Box,
188         playerEyes: Vec3d,
!!!         ^ error
189         entity: LivingEntity,
190         precisionFactor: Float
191     ): Vec3d {

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/point/features/humanized_targeting.kt:189:9
```
Function parameter `entity` is unused.
```
```kotlin
186         basePoint: Vec3d,
187         box: Box,
188         playerEyes: Vec3d,
189         entity: LivingEntity,
!!!         ^ error
190         precisionFactor: Float
191     ): Vec3d {
192         // Calculate box dimensions

```

### style, UnusedPrivateProperty (4)

Property is unused and should be removed.

[Documentation](https://detekt.dev/docs/rules/style#unusedprivateproperty)

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/features/processors/anglesmooth/impl/InterpolationAngleSmooth.kt:1300:17
```
Private property `ampMod` is unused.
```
```kotlin
1297         } else {
1298             // Standard tracking with advanced noise for AI evasion
1299             // Apply dynamic FBM with variable parameters
1300             val ampMod = 0.92 + (random.nextDouble() * 0.16)
!!!!                 ^ error
1301             val freqMod = 0.94 + (random.nextDouble() * 0.12)
1302             
1303             // Generate complex noise pattern

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/features/processors/anglesmooth/impl/InterpolationAngleSmooth.kt:1301:17
```
Private property `freqMod` is unused.
```
```kotlin
1298             // Standard tracking with advanced noise for AI evasion
1299             // Apply dynamic FBM with variable parameters
1300             val ampMod = 0.92 + (random.nextDouble() * 0.16)
1301             val freqMod = 0.94 + (random.nextDouble() * 0.12)
!!!!                 ^ error
1302             
1303             // Generate complex noise pattern
1304             val noise = EnhancedNoise.multiLayeredNoise(

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/features/processors/anglesmooth/impl/InterpolationAngleSmooth.kt:1476:13
```
Private property `baseYawDelta` is unused.
```
```kotlin
1473         val targetYaw = rotationTarget.rotation.yaw
1474         val targetPitch = rotationTarget.rotation.pitch
1475         
1476         val baseYawDelta = targetYaw - currentRotation.yaw
!!!!             ^ error
1477         val basePitchDelta = targetPitch - currentRotation.pitch
1478         
1479         // Use optimized processor for enhanced calculations

```

* /project/workspace/PojavBounceDKL/src/main/kotlin/net/ccbluex/liquidbounce/utils/aiming/features/processors/anglesmooth/impl/InterpolationAngleSmooth.kt:1477:13
```
Private property `basePitchDelta` is unused.
```
```kotlin
1474         val targetPitch = rotationTarget.rotation.pitch
1475         
1476         val baseYawDelta = targetYaw - currentRotation.yaw
1477         val basePitchDelta = targetPitch - currentRotation.pitch
!!!!             ^ error
1478         
1479         // Use optimized processor for enhanced calculations
1480         val processedResult = OptimizedAimProcessor.processOptimized(

```

generated with [detekt version 1.23.8](https://detekt.dev/) on 2025-09-24 15:10:28 UTC
