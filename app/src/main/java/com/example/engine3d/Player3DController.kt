package com.example.engine3d

import com.example.data.EntityType
import kotlin.math.*

class Player3DController {

    // 3D Player Position
    val pos = Vec3(0f, 0f, 0f)
    var velocityY = 0f
    var isGrounded = true

    // Camera Angles
    var yaw = 0f // Degrees (0 = facing +Z)
    var pitch = 0f // Degrees (-80 to +80)

    // Player Eye Height & Bobbing
    var currentEyeHeight = 1.65f
    private val standingEyeHeight = 1.65f
    private val crouchingEyeHeight = 0.95f
    var headBobOffset = 0f
    private var headBobTimer = 0f

    // Movement & Collision settings
    val playerRadius = 0.35f
    var playerHeight = 1.8f
    val walkSpeed = 3.6f // m/s
    val runSpeed = 6.2f // m/s
    val crouchSpeed = 1.6f // m/s
    val jumpForce = 4.8f // m/s
    val gravity = -14.0f // m/s²

    // Stealth & State
    var isCrouching = false
    var isHiding = false
    var currentHidingSpot: Entity3D? = null
    var noiseLevel = 0.0f // 0.0 (silent) to 1.0 (running/jumping)

    // Flashlight
    var isFlashlightOn = true

    // Targeted 3D Entity
    var focusedEntity: Entity3D? = null
    var focusDistance: Float = 0f

    fun resetTo(spawnPos: Vec3, spawnYaw: Float) {
        pos.set(spawnPos)
        velocityY = 0f
        isGrounded = true
        yaw = spawnYaw
        pitch = 0f
        headBobOffset = 0f
        headBobTimer = 0f
        isCrouching = false
        isHiding = false
        currentHidingSpot = null
        currentEyeHeight = standingEyeHeight
        focusedEntity = null
        noiseLevel = 0f
    }

    fun getCameraPosition(): Vec3 {
        return Vec3(pos.x, pos.y + currentEyeHeight + headBobOffset, pos.z)
    }

    fun getForwardVector(): Vec3 {
        val yawRad = Math.toRadians(yaw.toDouble()).toFloat()
        val pitchRad = Math.toRadians(pitch.toDouble()).toFloat()
        return Vec3(
            sin(yawRad) * cos(pitchRad),
            -sin(pitchRad),
            cos(yawRad) * cos(pitchRad)
        ).normalize()
    }

    fun getHorizontalForwardVector(): Vec3 {
        val yawRad = Math.toRadians(yaw.toDouble()).toFloat()
        return Vec3(sin(yawRad), 0f, cos(yawRad)).normalize()
    }

    fun getHorizontalRightVector(): Vec3 {
        val yawRad = Math.toRadians(yaw.toDouble()).toFloat()
        return Vec3(cos(yawRad), 0f, -sin(yawRad)).normalize()
    }

    fun rotateLook(deltaYaw: Float, deltaPitch: Float, sensitivity: Float = 1.0f) {
        if (isHiding) {
            // When hiding inside locker, clamp yaw peek angle to +/- 45 deg
            yaw = (yaw + deltaYaw * sensitivity * 0.6f)
            pitch = (pitch + deltaPitch * sensitivity * 0.6f).coerceIn(-30f, 30f)
            return
        }

        yaw = (yaw + deltaYaw * sensitivity) % 360f
        if (yaw < 0f) yaw += 360f

        pitch = (pitch + deltaPitch * sensitivity).coerceIn(-80f, 80f)
    }

    fun toggleCrouch(): Boolean {
        if (isHiding) return false
        isCrouching = !isCrouching
        playerHeight = if (isCrouching) 1.1f else 1.8f
        return isCrouching
    }

    fun toggleHiding(hidingSpot: Entity3D? = null): Boolean {
        if (isHiding) {
            isHiding = false
            currentHidingSpot = null
            return false
        } else if (hidingSpot != null) {
            isHiding = true
            currentHidingSpot = hidingSpot
            pos.set(hidingSpot.position)
            isFlashlightOn = false // Turn off flashlight automatically when entering hiding spot
            return true
        }
        return false
    }

    fun jump(): Boolean {
        if (isGrounded && !isCrouching && !isHiding) {
            velocityY = jumpForce
            isGrounded = false
            noiseLevel = 0.9f
            return true
        }
        return false
    }

    fun toggleFlashlight() {
        if (isHiding) return
        isFlashlightOn = !isFlashlightOn
    }

    fun update(
        dt: Float,
        moveX: Float, // -1 (left) .. +1 (right)
        moveZ: Float, // -1 (back) .. +1 (forward)
        isRunning: Boolean,
        scene: Scene3DEnvironment
    ) {
        val clampedDt = dt.coerceIn(0.001f, 0.05f)

        // Smoothly interpolate eye height
        val targetEyeHeight = if (isCrouching) crouchingEyeHeight else standingEyeHeight
        currentEyeHeight += (targetEyeHeight - currentEyeHeight) * (clampedDt * 10f)

        if (isHiding) {
            noiseLevel = 0.0f
            focusedEntity = currentHidingSpot
            return
        }

        // 1. Calculate desired movement vector
        val currentSpeed = when {
            isCrouching -> crouchSpeed
            isRunning -> runSpeed
            else -> walkSpeed
        }

        val fwd = getHorizontalForwardVector()
        val right = getHorizontalRightVector()

        var moveVec = (fwd * moveZ + right * moveX)
        val moveLen = moveVec.length()
        val isMoving = moveLen > 0.05f

        if (isMoving) {
            moveVec = moveVec.normalized() * (currentSpeed * moveLen.coerceAtMost(1.0f))
            noiseLevel = when {
                isCrouching -> 0.0f
                isRunning -> 0.85f
                else -> 0.35f
            }
        } else {
            moveVec = Vec3(0f, 0f, 0f)
            noiseLevel = (noiseLevel - clampedDt * 2.0f).coerceAtLeast(0.0f)
        }

        // 2. Head bobbing
        if (isMoving && isGrounded) {
            val bobFreq = if (isRunning) 14f else if (isCrouching) 6f else 9f
            val bobAmp = if (isRunning) 0.06f else if (isCrouching) 0.02f else 0.035f
            headBobTimer += clampedDt * bobFreq
            headBobOffset = sin(headBobTimer) * bobAmp
        } else {
            headBobOffset *= 0.85f
            if (abs(headBobOffset) < 0.001f) headBobOffset = 0f
        }

        // 3. Gravity & Vertical Movement
        velocityY += gravity * clampedDt
        pos.y += velocityY * clampedDt

        // Ground collision
        val targetGroundY = 0f
        if (pos.y <= targetGroundY) {
            pos.y = targetGroundY
            velocityY = 0f
            isGrounded = true
        }

        // 4. Horizontal Movement with 3D Wall Sliding Collision
        val nextX = pos.x + moveVec.x * clampedDt
        val nextZ = pos.z + moveVec.z * clampedDt

        // Test X axis movement
        val testPosX = Vec3(nextX, pos.y, pos.z)
        var collideX = false
        for (box in scene.collisionBoxes) {
            val sphereCenterY = testPosX.y + (if (isCrouching) 0.5f else 0.9f)
            if (box.intersectsSphere(Vec3(testPosX.x, sphereCenterY, testPosX.z), playerRadius)) {
                collideX = true
                break
            }
        }
        if (!collideX) {
            pos.x = nextX
        }

        // Test Z axis movement
        val testPosZ = Vec3(pos.x, pos.y, nextZ)
        var collideZ = false
        for (box in scene.collisionBoxes) {
            val sphereCenterY = testPosZ.y + (if (isCrouching) 0.5f else 0.9f)
            if (box.intersectsSphere(Vec3(testPosZ.x, sphereCenterY, testPosZ.z), playerRadius)) {
                collideZ = true
                break
            }
        }
        if (!collideZ) {
            pos.z = nextZ
        }

        // 5. Raycasting for Focused Interactive Entity
        val camPos = getCameraPosition()
        val lookDir = getForwardVector()
        var closestEntity: Entity3D? = null
        var closestDist = 3.6f // Max interaction range

        for (entity in scene.entities) {
            val toEntity = entity.position - camPos
            val projDist = toEntity.dot(lookDir)

            if (projDist > 0.4f && projDist < closestDist) {
                val closestPointOnRay = camPos + (lookDir * projDist)
                val distToRay = (entity.position - closestPointOnRay).length()

                if (distToRay < entity.interactionRadius) {
                    closestDist = projDist
                    closestEntity = entity
                }
            }
        }

        focusedEntity = closestEntity
        focusDistance = closestDist
    }
}
