package com.example.engine3d

import com.example.data.KeeperState
import kotlin.math.*

class KeeperAI {

    val pos = Vec3(0f, 0f, 0f)
    var yaw = 0f
    var state = KeeperState.IDLE_PATROL
    var tensionLevel = 0.0f // 0.0 to 1.0 sent to audio & HUD

    // Speed constants
    private val patrolSpeed = 1.8f
    private val chaseSpeed = 4.8f
    private val turnSpeed = 160f // deg/s

    // Patrol waypoints
    private val waypoints = mutableListOf<Vec3>()
    private var currentWaypointIndex = 0

    // Stalker AI Timers & Memory
    private var stateTimer = 0f
    private var lastKnownPlayerPos = Vec3(0f, 0f, 0f)
    var isEnabled = false

    fun setupWaypoints(points: List<Vec3>, startPos: Vec3) {
        waypoints.clear()
        waypoints.addAll(points)
        pos.set(startPos)
        currentWaypointIndex = 0
        state = KeeperState.IDLE_PATROL
        isEnabled = points.isNotEmpty()
        tensionLevel = 0f
    }

    fun update(
        dt: Float,
        playerPos: Vec3,
        playerSpeed: Float,
        isPlayerCrouching: Boolean,
        isPlayerHiding: Boolean,
        isFlashlightOn: Boolean
    ) {
        if (!isEnabled) {
            tensionLevel = 0f
            return
        }

        val clampedDt = dt.coerceIn(0.001f, 0.05f)
        stateTimer += clampedDt

        // 1. Calculate vector & distance to player
        val toPlayer = Vec3(playerPos.x - pos.x, 0f, playerPos.z - pos.z)
        val distToPlayer = toPlayer.length()
        val dirToPlayer = if (distToPlayer > 0.01f) toPlayer.normalized() else Vec3(0f, 0f, 1f)

        // Calculate angle to player relative to Keeper forward yaw
        val keeperFwd = Vec3(
            sin(Math.toRadians(yaw.toDouble()).toFloat()),
            0f,
            cos(Math.toRadians(yaw.toDouble()).toFloat())
        ).normalize()

        val dot = (keeperFwd.x * dirToPlayer.x + keeperFwd.z * dirToPlayer.z).coerceIn(-1f, 1f)
        val angleDeg = Math.toDegrees(acos(dot.toDouble())).toFloat()

        // 2. Perception: Visual and Sound Detection
        var canSeePlayer = false
        var canHearPlayer = false

        if (!isPlayerHiding) {
            // Visual check: inside 85-degree FOV and within sight distance
            val visualRange = if (isFlashlightOn) 16.0f else (if (isPlayerCrouching) 6.5f else 11.0f)
            if (distToPlayer < visualRange && angleDeg < 55f) {
                canSeePlayer = true
            }

            // Flashlight direct illumination: if flashlight is shining towards keeper within 14m
            if (isFlashlightOn && distToPlayer < 14f) {
                canSeePlayer = true
            }

            // Hearing check: sound radius depends on speed & crouching
            val hearingRange = if (playerSpeed > 4.5f) 14.0f else if (playerSpeed > 0.5f && !isPlayerCrouching) 7.5f else 0.0f
            if (distToPlayer < hearingRange) {
                canHearPlayer = true
            }
        }

        // 3. State Machine transitions
        when (state) {
            KeeperState.IDLE_PATROL -> {
                tensionLevel = (1.0f - (distToPlayer / 18.0f)).coerceIn(0.0f, 0.45f)

                if (canSeePlayer) {
                    state = KeeperState.ALERT_SPOTTED
                    stateTimer = 0f
                    lastKnownPlayerPos.set(playerPos)
                } else if (canHearPlayer) {
                    state = KeeperState.INVESTIGATING_NOISE
                    stateTimer = 0f
                    lastKnownPlayerPos.set(playerPos)
                } else {
                    // Follow waypoints
                    if (waypoints.isNotEmpty()) {
                        val target = waypoints[currentWaypointIndex]
                        val toTarget = Vec3(target.x - pos.x, 0f, target.z - pos.z)
                        val distTarget = toTarget.length()

                        if (distTarget < 0.6f) {
                            currentWaypointIndex = (currentWaypointIndex + 1) % waypoints.size
                        } else {
                            moveTowards(toTarget.normalized(), patrolSpeed, clampedDt)
                        }
                    }
                }
            }

            KeeperState.INVESTIGATING_NOISE -> {
                tensionLevel = (1.0f - (distToPlayer / 14.0f)).coerceIn(0.2f, 0.7f)

                if (canSeePlayer) {
                    state = KeeperState.PURSUING_CHASE
                    stateTimer = 0f
                } else {
                    val toTarget = Vec3(lastKnownPlayerPos.x - pos.x, 0f, lastKnownPlayerPos.z - pos.z)
                    val distTarget = toTarget.length()

                    if (distTarget < 0.8f || stateTimer > 6.0f) {
                        state = KeeperState.IDLE_PATROL
                        stateTimer = 0f
                    } else {
                        moveTowards(toTarget.normalized(), patrolSpeed * 1.3f, clampedDt)
                    }
                }
            }

            KeeperState.ALERT_SPOTTED -> {
                tensionLevel = 0.8f
                // Rotate to face player immediately, then burst into chase
                val targetYaw = Math.toDegrees(atan2(dirToPlayer.x.toDouble(), dirToPlayer.z.toDouble())).toFloat()
                yaw = targetYaw

                if (stateTimer > 0.5f) {
                    state = KeeperState.PURSUING_CHASE
                    stateTimer = 0f
                }
            }

            KeeperState.PURSUING_CHASE -> {
                tensionLevel = (1.0f - (distToPlayer / 20.0f)).coerceIn(0.65f, 1.0f)

                if (isPlayerHiding) {
                    // Player successfully hid in a locker/closet
                    state = KeeperState.LOST_TARGET
                    stateTimer = 0f
                } else if (canSeePlayer || canHearPlayer) {
                    lastKnownPlayerPos.set(playerPos)
                    moveTowards(dirToPlayer, chaseSpeed, clampedDt)
                } else {
                    // Lost direct line of sight, run to last known pos
                    val toTarget = Vec3(lastKnownPlayerPos.x - pos.x, 0f, lastKnownPlayerPos.z - pos.z)
                    if (toTarget.length() < 1.0f || stateTimer > 7.0f) {
                        state = KeeperState.LOST_TARGET
                        stateTimer = 0f
                    } else {
                        moveTowards(toTarget.normalized(), chaseSpeed * 0.85f, clampedDt)
                    }
                }
            }

            KeeperState.LOST_TARGET -> {
                tensionLevel = 0.4f
                if (canSeePlayer) {
                    state = KeeperState.PURSUING_CHASE
                    stateTimer = 0f
                } else if (stateTimer > 4.0f) {
                    state = KeeperState.IDLE_PATROL
                    stateTimer = 0f
                }
            }
        }
    }

    private fun moveTowards(dir: Vec3, speed: Float, dt: Float) {
        val targetYaw = Math.toDegrees(atan2(dir.x.toDouble(), dir.z.toDouble())).toFloat()
        // Smoothly rotate yaw towards target
        var diff = (targetYaw - yaw) % 360f
        if (diff < -180f) diff += 360f
        if (diff > 180f) diff -= 360f

        yaw += diff.coerceIn(-turnSpeed * dt, turnSpeed * dt)

        pos.x += dir.x * speed * dt
        pos.z += dir.z * speed * dt
    }
}
