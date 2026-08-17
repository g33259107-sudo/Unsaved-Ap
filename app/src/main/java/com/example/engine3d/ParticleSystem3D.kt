package com.example.engine3d

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.random.Random

class ParticleSystem3D(val maxParticles: Int = 300) {

    private val posBuffer: FloatBuffer
    private val colBuffer: FloatBuffer
    private val sizeBuffer: FloatBuffer

    private val posX = FloatArray(maxParticles)
    private val posY = FloatArray(maxParticles)
    private val posZ = FloatArray(maxParticles)

    private val velX = FloatArray(maxParticles)
    private val velY = FloatArray(maxParticles)
    private val velZ = FloatArray(maxParticles)

    private val colors = FloatArray(maxParticles * 4)
    private val sizes = FloatArray(maxParticles)

    init {
        posBuffer = ByteBuffer.allocateDirect(maxParticles * 3 * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        colBuffer = ByteBuffer.allocateDirect(maxParticles * 4 * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        sizeBuffer = ByteBuffer.allocateDirect(maxParticles * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()

        // Initialize particles
        for (i in 0 until maxParticles) {
            resetParticle(i, 0f, 0f, 0f, isInitial = true)
        }
    }

    private fun resetParticle(i: Int, playerX: Float, playerY: Float, playerZ: Float, isInitial: Boolean) {
        val spread = 24f
        posX[i] = playerX + (Random.nextFloat() - 0.5f) * spread
        posY[i] = if (isInitial) playerY + Random.nextFloat() * 12f else playerY + 10f + Random.nextFloat() * 4f
        posZ[i] = playerZ + (Random.nextFloat() - 0.5f) * spread

        // Rain-like falling velocity with slight wind
        velX[i] = -0.6f + (Random.nextFloat() - 0.5f) * 0.2f
        velY[i] = -14f - Random.nextFloat() * 8f
        velZ[i] = 0.2f + (Random.nextFloat() - 0.5f) * 0.2f

        // Rainy mist/droplet colors (pale cyan/white with alpha)
        val isMote = (i % 8 == 0)
        if (isMote) {
            // Glowing memory ember / ambient dust
            colors[i * 4 + 0] = 0.2f
            colors[i * 4 + 1] = 0.85f
            colors[i * 4 + 2] = 1.0f
            colors[i * 4 + 3] = 0.7f
            sizes[i] = 7f + Random.nextFloat() * 5f
            velY[i] = -0.5f + (Random.nextFloat() - 0.5f) * 0.5f
        } else {
            // Rain streak
            colors[i * 4 + 0] = 0.6f
            colors[i * 4 + 1] = 0.8f
            colors[i * 4 + 2] = 0.95f
            colors[i * 4 + 3] = 0.45f
            sizes[i] = 3.5f + Random.nextFloat() * 2f
        }
    }

    fun update(dt: Float, playerX: Float, playerY: Float, playerZ: Float) {
        for (i in 0 until maxParticles) {
            posX[i] += velX[i] * dt
            posY[i] += velY[i] * dt
            posZ[i] += velZ[i] * dt

            // Reset if below floor or too far from player
            val dx = posX[i] - playerX
            val dz = posZ[i] - playerZ
            if (posY[i] < -0.5f || dx * dx + dz * dz > 18f * 18f) {
                resetParticle(i, playerX, playerY, playerZ, isInitial = false)
            }
        }

        // Fill OpenGL buffers
        posBuffer.position(0)
        for (i in 0 until maxParticles) {
            posBuffer.put(posX[i])
            posBuffer.put(posY[i])
            posBuffer.put(posZ[i])
        }
        posBuffer.position(0)

        colBuffer.position(0)
        colBuffer.put(colors)
        colBuffer.position(0)

        sizeBuffer.position(0)
        sizeBuffer.put(sizes)
        sizeBuffer.position(0)
    }

    fun render(program: ShaderPrograms.ParticleProgram, mvpMatrix: FloatArray) {
        GLES20.glUseProgram(program.programId)
        GLES20.glUniformMatrix4fv(program.uMVPMatrix, 1, false, mvpMatrix, 0)

        GLES20.glEnableVertexAttribArray(program.aPosition)
        GLES20.glVertexAttribPointer(program.aPosition, 3, GLES20.GL_FLOAT, false, 0, posBuffer)

        GLES20.glEnableVertexAttribArray(program.aColor)
        GLES20.glVertexAttribPointer(program.aColor, 4, GLES20.GL_FLOAT, false, 0, colBuffer)

        GLES20.glEnableVertexAttribArray(program.aSize)
        GLES20.glVertexAttribPointer(program.aSize, 1, GLES20.GL_FLOAT, false, 0, sizeBuffer)

        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE)
        GLES20.glDepthMask(false)

        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, maxParticles)

        GLES20.glDepthMask(true)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        GLES20.glDisableVertexAttribArray(program.aPosition)
        GLES20.glDisableVertexAttribArray(program.aColor)
        GLES20.glDisableVertexAttribArray(program.aSize)
    }
}
