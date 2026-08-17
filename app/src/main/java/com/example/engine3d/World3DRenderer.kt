package com.example.engine3d

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.SystemClock
import com.example.data.EntityType
import com.example.data.LocationId
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.sin

class World3DRenderer(
    val context: Context,
    val onEntityFocusChanged: (Entity3D?) -> Unit = {},
    val onPlayerStateChanged: (x: Float, y: Float, z: Float, yaw: Float, isFlashlightOn: Boolean) -> Unit = { _, _, _, _, _ -> },
    val onStealthStateChanged: (tension: Float, noise: Float, isCrouched: Boolean, isHiding: Boolean) -> Unit = { _, _, _, _ -> }
) : GLSurfaceView.Renderer {

    val playerController = Player3DController()
    val keeperAI = KeeperAI()
    private val particleSystem = ParticleSystem3D(maxParticles = 250)

    var currentScene: Scene3DEnvironment = Scene3DBuilder.buildScene(LocationId.NOCTURNE_STATION)
        private set

    // Input state from touch controls
    var moveJoystickX = 0f
    var moveJoystickY = 0f
    var isRunning = false

    // Shaders
    private var mainProgram: ShaderPrograms.MainProgram? = null
    private var particleProgram: ShaderPrograms.ParticleProgram? = null

    // Dynamic procedural models
    private var memoryCrystalMesh: Mesh3D? = null
    private var itemAuraMesh: Mesh3D? = null
    private var keeperMesh: Mesh3D? = null
    private var npcMesh: Mesh3D? = null

    // Transformation Matrices
    private val projMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)
    private val normalMatrix = FloatArray(9)
    private val tempNormalMat4 = FloatArray(16)

    // Animation & Timing
    private var lastFrameTimeNanos = 0L
    private var totalTime = 0f
    private var lastFocusedEntityId: String? = null

    fun loadLocation(locationId: LocationId) {
        val newScene = Scene3DBuilder.buildScene(locationId)
        currentScene = newScene
        playerController.resetTo(newScene.playerSpawnPos, newScene.playerSpawnYaw)

        // Setup Keeper AI if location has waypoints
        if (newScene.keeperWaypoints.isNotEmpty()) {
            keeperAI.setupWaypoints(newScene.keeperWaypoints, newScene.keeperStartPos)
        } else {
            keeperAI.isEnabled = false
        }
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glDepthFunc(GLES20.GL_LEQUAL)
        GLES20.glEnable(GLES20.GL_CULL_FACE)
        GLES20.glCullFace(GLES20.GL_BACK)

        mainProgram = ShaderPrograms.buildMainProgram()
        particleProgram = ShaderPrograms.buildParticleProgram()

        memoryCrystalMesh = Mesh3D.createCrystal(radius = 0.38f, height = 1.1f, r = 0.2f, g = 0.85f, b = 1.0f)
        itemAuraMesh = Mesh3D.createCrystal(radius = 0.22f, height = 0.6f, r = 1.0f, g = 0.85f, b = 0.25f)
        keeperMesh = Mesh3D.createHumanoid(height = 2.2f, r = 0.08f, g = 0.08f, b = 0.10f, glowR = 1.0f, glowG = 0.1f, glowB = 0.1f)
        npcMesh = Mesh3D.createHumanoid(height = 1.75f, r = 0.25f, g = 0.32f, b = 0.45f, glowR = 0.4f, glowG = 0.85f, glowB = 1.0f)

        lastFrameTimeNanos = SystemClock.elapsedRealtimeNanos()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val aspect = width.toFloat() / height.toFloat().coerceAtLeast(1f)
        Matrix.perspectiveM(projMatrix, 0, 68f, aspect, 0.1f, 60.0f)
    }

    override fun onDrawFrame(gl: GL10?) {
        val now = SystemClock.elapsedRealtimeNanos()
        val dt = ((now - lastFrameTimeNanos) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
        lastFrameTimeNanos = now
        totalTime += dt

        // 1. Update Player 3D Movement & Physics
        playerController.update(
            dt = dt,
            moveX = moveJoystickX,
            moveZ = moveJoystickY,
            isRunning = isRunning,
            scene = currentScene
        )

        // 2. Update Keeper AI
        if (keeperAI.isEnabled) {
            val playerSpeed = if (playerController.isCrouching) playerController.crouchSpeed else (if (isRunning) playerController.runSpeed else playerController.walkSpeed)
            keeperAI.update(
                dt = dt,
                playerPos = playerController.pos,
                playerSpeed = if (moveJoystickX != 0f || moveJoystickY != 0f) playerSpeed else 0f,
                isPlayerCrouching = playerController.isCrouching,
                isPlayerHiding = playerController.isHiding,
                isFlashlightOn = playerController.isFlashlightOn
            )
        }

        // 3. Update Rain & Atmospheric Particles
        particleSystem.update(
            dt = dt,
            playerX = playerController.pos.x,
            playerY = playerController.pos.y,
            playerZ = playerController.pos.z
        )

        // Notify HUD listeners
        val currentFocus = playerController.focusedEntity
        if (currentFocus?.id != lastFocusedEntityId) {
            lastFocusedEntityId = currentFocus?.id
            onEntityFocusChanged(currentFocus)
        }

        onPlayerStateChanged(
            playerController.pos.x,
            playerController.pos.y,
            playerController.pos.z,
            playerController.yaw,
            playerController.isFlashlightOn
        )

        onStealthStateChanged(
            if (keeperAI.isEnabled) keeperAI.tensionLevel else 0f,
            playerController.noiseLevel,
            playerController.isCrouching,
            playerController.isHiding
        )

        // 4. Clear Screen with Fog Color
        val fog = currentScene.fogColor
        GLES20.glClearColor(fog.x, fog.y, fog.z, 1.0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        val program = mainProgram ?: return

        // 5. Setup First-Person View Matrix
        val camPos = playerController.getCameraPosition()
        val lookDir = playerController.getForwardVector()
        val targetPos = camPos + lookDir

        Matrix.setLookAtM(
            viewMatrix, 0,
            camPos.x, camPos.y, camPos.z,
            targetPos.x, targetPos.y, targetPos.z,
            0f, 1f, 0f
        )

        // 6. Use Main Shader Program and upload Scene Uniforms
        GLES20.glUseProgram(program.programId)

        // Ambient & Directional light
        GLES20.glUniform3f(program.uCameraPos, camPos.x, camPos.y, camPos.z)
        GLES20.glUniform3f(program.uAmbientColor, currentScene.ambientColor.x, currentScene.ambientColor.y, currentScene.ambientColor.z)
        GLES20.glUniform3f(program.uDirLightDir, currentScene.dirLightDir.x, currentScene.dirLightDir.y, currentScene.dirLightDir.z)
        GLES20.glUniform3f(program.uDirLightColor, currentScene.dirLightColor.x, currentScene.dirLightColor.y, currentScene.dirLightColor.z)

        // Point Light 1 (Scene highlight)
        val p1Pos = currentScene.pointLight1Pos
        val p1Col = currentScene.pointLight1Color
        GLES20.glUniform3f(program.uPointLight1Pos, p1Pos.x, p1Pos.y, p1Pos.z)
        GLES20.glUniform3f(program.uPointLight1Color, p1Col.x, p1Col.y, p1Col.z)
        GLES20.glUniform1f(program.uPointLight1Radius, currentScene.pointLight1Radius)

        // Point Light 2 (Keeper crimson light or secondary highlight)
        if (keeperAI.isEnabled) {
            GLES20.glUniform3f(program.uPointLight2Pos, keeperAI.pos.x, keeperAI.pos.y + 1.8f, keeperAI.pos.z)
            GLES20.glUniform3f(program.uPointLight2Color, 1.0f, 0.15f, 0.15f)
            GLES20.glUniform1f(program.uPointLight2Radius, 10.0f)
        } else {
            val p2Pos = currentScene.pointLight2Pos
            val p2Col = currentScene.pointLight2Color
            GLES20.glUniform3f(program.uPointLight2Pos, p2Pos.x, p2Pos.y, p2Pos.z)
            GLES20.glUniform3f(program.uPointLight2Color, p2Col.x, p2Col.y, p2Col.z)
            GLES20.glUniform1f(program.uPointLight2Radius, currentScene.pointLight2Radius)
        }

        // Flashlight (Spotlight)
        val flashColor = if (playerController.isFlashlightOn) 1.0f else 0.0f
        GLES20.glUniform3f(program.uFlashlightPos, camPos.x, camPos.y, camPos.z)
        GLES20.glUniform3f(program.uFlashlightDir, lookDir.x, lookDir.y, lookDir.z)
        GLES20.glUniform3f(program.uFlashlightColor, 0.95f, 0.98f, 1.0f)
        GLES20.glUniform1f(program.uFlashlightCutoff, 0.88f)
        GLES20.glUniform1f(program.uFlashlightEnabled, flashColor)

        // Fog
        GLES20.glUniform3f(program.uFogColor, fog.x, fog.y, fog.z)
        GLES20.glUniform1f(program.uFogStart, 4.0f)
        GLES20.glUniform1f(program.uFogEnd, 38.0f)

        // 7. Render Environment Architecture Mesh
        Matrix.setIdentityM(modelMatrix, 0)
        computeAndUploadMatrices(program)
        currentScene.environmentMesh.render(program)

        // 8. Render Memory Shard Crystals & NPC meshes
        for (entity in currentScene.entities) {
            when (entity.type) {
                EntityType.MEMORY_SHARD -> {
                    memoryCrystalMesh?.let { crystal ->
                        val floatY = entity.position.y + sin(totalTime * 2.2f + entity.position.x) * 0.12f
                        Matrix.setIdentityM(modelMatrix, 0)
                        Matrix.translateM(modelMatrix, 0, entity.position.x, floatY, entity.position.z)
                        Matrix.rotateM(modelMatrix, 0, totalTime * 45f, 0f, 1f, 0f)
                        computeAndUploadMatrices(program)
                        crystal.render(program)
                    }
                }
                EntityType.NPC -> {
                    npcMesh?.let { npc ->
                        Matrix.setIdentityM(modelMatrix, 0)
                        Matrix.translateM(modelMatrix, 0, entity.position.x, 0f, entity.position.z)
                        computeAndUploadMatrices(program)
                        npc.render(program)
                    }
                }
                else -> { /* Rendered in static environment mesh */ }
            }
        }

        // 9. Render The Keeper in 3D when active
        if (keeperAI.isEnabled) {
            keeperMesh?.let { keeper ->
                val floatY = sin(totalTime * 3.5f) * 0.08f
                Matrix.setIdentityM(modelMatrix, 0)
                Matrix.translateM(modelMatrix, 0, keeperAI.pos.x, floatY, keeperAI.pos.z)
                Matrix.rotateM(modelMatrix, 0, keeperAI.yaw, 0f, 1f, 0f)
                computeAndUploadMatrices(program)
                keeper.render(program)
            }
        }

        // 10. Render Rain Particles
        val pProg = particleProgram
        if (pProg != null) {
            GLES20.glEnable(GLES20.GL_BLEND)
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE)
            GLES20.glDepthMask(false)

            GLES20.glUseProgram(pProg.programId)
            Matrix.multiplyMM(mvpMatrix, 0, projMatrix, 0, viewMatrix, 0)
            GLES20.glUniformMatrix4fv(pProg.uMVPMatrix, 1, false, mvpMatrix, 0)
            particleSystem.render(pProg, mvpMatrix)

            GLES20.glDepthMask(true)
            GLES20.glDisable(GLES20.GL_BLEND)
        }
    }

    private fun computeAndUploadMatrices(program: ShaderPrograms.MainProgram) {
        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projMatrix, 0, mvpMatrix, 0)
        GLES20.glUniformMatrix4fv(program.uMVPMatrix, 1, false, mvpMatrix, 0)
        GLES20.glUniformMatrix4fv(program.uModelMatrix, 1, false, modelMatrix, 0)

        Matrix.invertM(tempNormalMat4, 0, modelMatrix, 0)
        Matrix.transposeM(tempNormalMat4, 0, tempNormalMat4, 0)
        normalMatrix[0] = tempNormalMat4[0]; normalMatrix[1] = tempNormalMat4[1]; normalMatrix[2] = tempNormalMat4[2]
        normalMatrix[3] = tempNormalMat4[4]; normalMatrix[4] = tempNormalMat4[5]; normalMatrix[5] = tempNormalMat4[6]
        normalMatrix[6] = tempNormalMat4[8]; normalMatrix[7] = tempNormalMat4[9]; normalMatrix[8] = tempNormalMat4[10]
        GLES20.glUniformMatrix3fv(program.uNormalMatrix, 1, false, normalMatrix, 0)
    }
}
