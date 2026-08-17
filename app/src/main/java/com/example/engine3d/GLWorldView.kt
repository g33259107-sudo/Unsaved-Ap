package com.example.engine3d

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import com.example.data.LocationId
import kotlin.math.sqrt

class GLWorldView(
    context: Context,
    val onEntityFocus: (Entity3D?) -> Unit = {},
    val onPlayerState: (x: Float, y: Float, z: Float, yaw: Float, isFlashlight: Boolean) -> Unit = { _, _, _, _, _ -> },
    val onStealthState: (tension: Float, noise: Float, isCrouched: Boolean, isHiding: Boolean) -> Unit = { _, _, _, _ -> }
) : GLSurfaceView(context) {

    val renderer: World3DRenderer

    // Touch tracking pointers
    private var movePointerId = -1
    private var lookPointerId = -1

    private var moveStartX = 0f
    private var moveStartY = 0f

    private var lastLookX = 0f
    private var lastLookY = 0f

    var lookSensitivity = 0.22f

    init {
        setPreserveEGLContextOnPause(true)
        setEGLContextClientVersion(2)
        renderer = World3DRenderer(
            context = context,
            onEntityFocusChanged = { entity ->
                post { onEntityFocus(entity) }
            },
            onPlayerStateChanged = { x, y, z, yaw, isFlash ->
                post { onPlayerState(x, y, z, yaw, isFlash) }
            },
            onStealthStateChanged = { tension, noise, isCrouched, isHiding ->
                post { onStealthState(tension, noise, isCrouched, isHiding) }
            }
        )
        setRenderer(renderer)
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        onResume()
    }

    override fun onDetachedFromWindow() {
        onPause()
        super.onDetachedFromWindow()
    }

    fun loadLocation(locationId: LocationId) {
        queueEvent {
            renderer.loadLocation(locationId)
        }
    }

    fun setMovementInput(dx: Float, dy: Float, running: Boolean) {
        queueEvent {
            renderer.moveJoystickX = dx
            renderer.moveJoystickY = dy
            renderer.isRunning = running
        }
    }

    fun rotateCamera(deltaYaw: Float, deltaPitch: Float) {
        queueEvent {
            renderer.playerController.rotateLook(deltaYaw, deltaPitch, lookSensitivity)
        }
    }

    fun toggleCrouch(): Boolean {
        var isCrouched = false
        queueEvent {
            isCrouched = renderer.playerController.toggleCrouch()
        }
        return isCrouched
    }

    fun toggleHiding(hidingSpot: Entity3D? = null): Boolean {
        var isHiding = false
        queueEvent {
            isHiding = renderer.playerController.toggleHiding(hidingSpot)
        }
        return isHiding
    }

    fun jump() {
        queueEvent {
            renderer.playerController.jump()
        }
    }

    fun toggleFlashlight() {
        queueEvent {
            renderer.playerController.toggleFlashlight()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val actionMasked = event.actionMasked
        val actionIndex = event.actionIndex

        when (actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                val pid = event.getPointerId(actionIndex)
                val x = event.getX(actionIndex)
                val y = event.getY(actionIndex)

                val screenWidthHalf = width / 2f

                if (x < screenWidthHalf && movePointerId == -1) {
                    movePointerId = pid
                    moveStartX = x
                    moveStartY = y
                } else if (x >= screenWidthHalf && lookPointerId == -1) {
                    lookPointerId = pid
                    lastLookX = x
                    lastLookY = y
                }
            }

            MotionEvent.ACTION_MOVE -> {
                for (i in 0 until event.pointerCount) {
                    val pid = event.getPointerId(i)
                    val x = event.getX(i)
                    val y = event.getY(i)

                    if (pid == movePointerId) {
                        val maxRadius = 140f
                        var deltaX = (x - moveStartX)
                        var deltaY = (y - moveStartY)
                        val dist = sqrt(deltaX * deltaX + deltaY * deltaY)
                        if (dist > maxRadius) {
                            deltaX = (deltaX / dist) * maxRadius
                            deltaY = (deltaY / dist) * maxRadius
                        }

                        val normX = (deltaX / maxRadius).coerceIn(-1f, 1f)
                        val normY = (-deltaY / maxRadius).coerceIn(-1f, 1f)

                        queueEvent {
                            renderer.moveJoystickX = normX
                            renderer.moveJoystickY = normY
                        }
                    } else if (pid == lookPointerId) {
                        val deltaX = x - lastLookX
                        val deltaY = y - lastLookY
                        lastLookX = x
                        lastLookY = y

                        queueEvent {
                            renderer.playerController.rotateLook(
                                deltaYaw = deltaX * lookSensitivity,
                                deltaPitch = -deltaY * lookSensitivity
                            )
                        }
                    }
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                val pid = event.getPointerId(actionIndex)
                if (pid == movePointerId) {
                    movePointerId = -1
                    queueEvent {
                        renderer.moveJoystickX = 0f
                        renderer.moveJoystickY = 0f
                    }
                } else if (pid == lookPointerId) {
                    lookPointerId = -1
                }
            }

            MotionEvent.ACTION_CANCEL -> {
                movePointerId = -1
                lookPointerId = -1
                queueEvent {
                    renderer.moveJoystickX = 0f
                    renderer.moveJoystickY = 0f
                }
            }
        }
        return true
    }
}
