package com.example.engine3d

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import kotlin.math.*

class Mesh3D(
    val vertexBuffer: FloatBuffer,
    val indexBuffer: ShortBuffer?,
    val vertexCount: Int,
    val indexCount: Int = 0,
    val bounds: AABB? = null
) {
    // Vertex layout: PosX, PosY, PosZ, NormX, NormY, NormZ, ColR, ColG, ColB, ColA (10 floats = 40 bytes)
    companion object {
        const val FLOATS_PER_VERTEX = 10
        const val STRIDE = FLOATS_PER_VERTEX * 4

        fun createBox(
            x: Float, y: Float, z: Float,
            sizeX: Float, sizeY: Float, sizeZ: Float,
            r: Float, g: Float, b: Float, a: Float = 1.0f
        ): Mesh3D {
            val halfX = sizeX / 2f
            val halfY = sizeY / 2f
            val halfZ = sizeZ / 2f

            val minX = x - halfX; val maxX = x + halfX
            val minY = y - halfY; val maxY = y + halfY
            val minZ = z - halfZ; val maxZ = z + halfZ

            val vertices = floatArrayOf(
                // Front face (+Z)
                minX, minY, maxZ,   0f, 0f, 1f,   r, g, b, a,
                maxX, minY, maxZ,   0f, 0f, 1f,   r, g, b, a,
                maxX, maxY, maxZ,   0f, 0f, 1f,   r, g, b, a,
                minX, maxY, maxZ,   0f, 0f, 1f,   r, g, b, a,

                // Back face (-Z)
                maxX, minY, minZ,   0f, 0f, -1f,  r * 0.85f, g * 0.85f, b * 0.85f, a,
                minX, minY, minZ,   0f, 0f, -1f,  r * 0.85f, g * 0.85f, b * 0.85f, a,
                minX, maxY, minZ,   0f, 0f, -1f,  r * 0.85f, g * 0.85f, b * 0.85f, a,
                maxX, maxY, minZ,   0f, 0f, -1f,  r * 0.85f, g * 0.85f, b * 0.85f, a,

                // Top face (+Y)
                minX, maxY, maxZ,   0f, 1f, 0f,   r * 1.1f, g * 1.1f, b * 1.1f, a,
                maxX, maxY, maxZ,   0f, 1f, 0f,   r * 1.1f, g * 1.1f, b * 1.1f, a,
                maxX, maxY, minZ,   0f, 1f, 0f,   r * 1.1f, g * 1.1f, b * 1.1f, a,
                minX, maxY, minZ,   0f, 1f, 0f,   r * 1.1f, g * 1.1f, b * 1.1f, a,

                // Bottom face (-Y)
                minX, minY, minZ,   0f, -1f, 0f,  r * 0.7f, g * 0.7f, b * 0.7f, a,
                maxX, minY, minZ,   0f, -1f, 0f,  r * 0.7f, g * 0.7f, b * 0.7f, a,
                maxX, minY, maxZ,   0f, -1f, 0f,  r * 0.7f, g * 0.7f, b * 0.7f, a,
                minX, minY, maxZ,   0f, -1f, 0f,  r * 0.7f, g * 0.7f, b * 0.7f, a,

                // Right face (+X)
                maxX, minY, maxZ,   1f, 0f, 0f,   r * 0.9f, g * 0.9f, b * 0.9f, a,
                maxX, minY, minZ,   1f, 0f, 0f,   r * 0.9f, g * 0.9f, b * 0.9f, a,
                maxX, maxY, minZ,   1f, 0f, 0f,   r * 0.9f, g * 0.9f, b * 0.9f, a,
                maxX, maxY, maxZ,   1f, 0f, 0f,   r * 0.9f, g * 0.9f, b * 0.9f, a,

                // Left face (-X)
                minX, minY, minZ,  -1f, 0f, 0f,   r * 0.9f, g * 0.9f, b * 0.9f, a,
                minX, minY, maxZ,  -1f, 0f, 0f,   r * 0.9f, g * 0.9f, b * 0.9f, a,
                minX, maxY, maxZ,  -1f, 0f, 0f,   r * 0.9f, g * 0.9f, b * 0.9f, a,
                minX, maxY, minZ,  -1f, 0f, 0f,   r * 0.9f, g * 0.9f, b * 0.9f, a
            )

            val indices = shortArrayOf(
                0, 1, 2, 0, 2, 3,       // front
                4, 5, 6, 4, 6, 7,       // back
                8, 9, 10, 8, 10, 11,    // top
                12, 13, 14, 12, 14, 15, // bottom
                16, 17, 18, 16, 18, 19, // right
                20, 21, 22, 20, 22, 23  // left
            )

            return fromArrays(vertices, indices, AABB(minX, minY, minZ, maxX, maxY, maxZ))
        }

        fun createCrystal(
            radius: Float, height: Float,
            r: Float, g: Float, b: Float, a: Float = 0.95f
        ): Mesh3D {
            val h2 = height / 2f
            val segments = 6
            val vertices = ArrayList<Float>()
            val indices = ArrayList<Short>()

            // Top apex
            val topApexIdx = 0.toShort()
            vertices.addAll(listOf(0f, h2, 0f, 0f, 1f, 0f, r, g, b, a))

            // Bottom apex
            val botApexIdx = 1.toShort()
            vertices.addAll(listOf(0f, -h2, 0f, 0f, -1f, 0f, r * 0.8f, g * 0.8f, b * 0.8f, a))

            // Ring vertices
            val ringStartIdx = 2
            for (i in 0 until segments) {
                val angle = i * (2f * Math.PI.toFloat() / segments)
                val rx = cos(angle) * radius
                val rz = sin(angle) * radius
                val normX = cos(angle)
                val normZ = sin(angle)
                vertices.addAll(listOf(rx, 0f, rz, normX, 0f, normZ, r * 1.2f, g * 1.2f, b * 1.2f, a))
            }

            for (i in 0 until segments) {
                val curr = (ringStartIdx + i).toShort()
                val next = (ringStartIdx + (i + 1) % segments).toShort()
                // Top cone
                indices.add(topApexIdx)
                indices.add(curr)
                indices.add(next)
                // Bottom cone
                indices.add(botApexIdx)
                indices.add(next)
                indices.add(curr)
            }

            val vertArray = vertices.toFloatArray()
            val indArray = indices.toShortArray()
            return fromArrays(vertArray, indArray, AABB(-radius, -h2, -radius, radius, h2, radius))
        }

        fun createCylinder(
            x: Float, y: Float, z: Float,
            radius: Float, height: Float, segments: Int = 12,
            r: Float, g: Float, b: Float, a: Float = 1.0f
        ): Mesh3D {
            val halfH = height / 2f
            val vertices = ArrayList<Float>()
            val indices = ArrayList<Short>()

            for (i in 0..segments) {
                val angle = i * (2f * Math.PI.toFloat() / segments)
                val cosA = cos(angle)
                val sinA = sin(angle)
                val px = x + cosA * radius
                val pz = z + sinA * radius

                // Bottom vertex
                vertices.addAll(listOf(px, y - halfH, pz, cosA, 0f, sinA, r * 0.8f, g * 0.8f, b * 0.8f, a))
                // Top vertex
                vertices.addAll(listOf(px, y + halfH, pz, cosA, 0f, sinA, r * 1.1f, g * 1.1f, b * 1.1f, a))
            }

            for (i in 0 until segments) {
                val b1 = (i * 2).toShort()
                val t1 = (i * 2 + 1).toShort()
                val b2 = ((i + 1) * 2).toShort()
                val t2 = ((i + 1) * 2 + 1).toShort()

                indices.add(b1); indices.add(t1); indices.add(t2)
                indices.add(b1); indices.add(t2); indices.add(b2)
            }

            return fromArrays(
                vertices.toFloatArray(),
                indices.toShortArray(),
                AABB(x - radius, y - halfH, z - radius, x + radius, y + halfH, z + radius)
            )
        }

        fun createHumanoid(
            height: Float = 1.85f,
            r: Float, g: Float, b: Float,
            glowR: Float = 1.0f, glowG: Float = 0.2f, glowB: Float = 0.2f
        ): Mesh3D {
            // Creates stylized 3D humanoid mesh with torso, head, and glowing visor/face
            val meshList = mutableListOf<Mesh3D>()

            // Lower Robe / Legs
            meshList.add(createBox(0f, height * 0.3f, 0f, 0.45f, height * 0.6f, 0.35f, r * 0.8f, g * 0.8f, b * 0.8f))
            // Torso & Cloak
            meshList.add(createBox(0f, height * 0.7f, 0f, 0.52f, height * 0.4f, 0.38f, r, g, b))
            // Head / Hood
            meshList.add(createBox(0f, height * 0.95f, 0f, 0.32f, 0.32f, 0.32f, r * 1.1f, g * 1.1f, b * 1.1f))
            // Glowing Eyes / Mask (+Z face)
            meshList.add(createBox(0f, height * 0.96f, 0.17f, 0.22f, 0.08f, 0.04f, glowR, glowG, glowB))

            return combine(meshList)
        }

        fun combine(meshes: List<Mesh3D>): Mesh3D {
            val totalVertFloats = meshes.sumOf { it.vertexCount * FLOATS_PER_VERTEX }
            val totalIndices = meshes.sumOf { it.indexCount }

            val combinedVerts = FloatArray(totalVertFloats)
            val combinedIndices = if (totalIndices > 0) ShortArray(totalIndices) else null

            var vOffset = 0
            var iOffset = 0
            var baseVertex = 0

            var minX = Float.MAX_VALUE; var minY = Float.MAX_VALUE; var minZ = Float.MAX_VALUE
            var maxX = -Float.MAX_VALUE; var maxY = -Float.MAX_VALUE; var maxZ = -Float.MAX_VALUE

            for (mesh in meshes) {
                mesh.vertexBuffer.position(0)
                val vertCount = mesh.vertexCount * FLOATS_PER_VERTEX
                mesh.vertexBuffer.get(combinedVerts, vOffset, vertCount)

                mesh.bounds?.let {
                    minX = min(minX, it.minX); minY = min(minY, it.minY); minZ = min(minZ, it.minZ)
                    maxX = max(maxX, it.maxX); maxY = max(maxY, it.maxY); maxZ = max(maxZ, it.maxZ)
                }

                if (combinedIndices != null && mesh.indexBuffer != null && mesh.indexCount > 0) {
                    mesh.indexBuffer.position(0)
                    for (i in 0 until mesh.indexCount) {
                        val idx = mesh.indexBuffer.get().toInt()
                        combinedIndices[iOffset + i] = (idx + baseVertex).toShort()
                    }
                    iOffset += mesh.indexCount
                }

                vOffset += vertCount
                baseVertex += mesh.vertexCount
            }

            return fromArrays(
                combinedVerts,
                combinedIndices,
                if (minX != Float.MAX_VALUE) AABB(minX, minY, minZ, maxX, maxY, maxZ) else null
            )
        }

        fun fromArrays(vertices: FloatArray, indices: ShortArray?, bounds: AABB? = null): Mesh3D {
            val vBuf = ByteBuffer.allocateDirect(vertices.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
            vBuf.put(vertices).position(0)

            var iBuf: ShortBuffer? = null
            var iCount = 0
            if (indices != null) {
                iBuf = ByteBuffer.allocateDirect(indices.size * 2)
                    .order(ByteOrder.nativeOrder())
                    .asShortBuffer()
                iBuf.put(indices).position(0)
                iCount = indices.size
            }

            return Mesh3D(
                vertexBuffer = vBuf,
                indexBuffer = iBuf,
                vertexCount = vertices.size / FLOATS_PER_VERTEX,
                indexCount = iCount,
                bounds = bounds
            )
        }
    }

    fun render(program: ShaderPrograms.MainProgram) {
        vertexBuffer.position(0)
        GLES20.glVertexAttribPointer(
            program.aPosition, 3, GLES20.GL_FLOAT, false, STRIDE, vertexBuffer
        )
        GLES20.glEnableVertexAttribArray(program.aPosition)

        vertexBuffer.position(3)
        GLES20.glVertexAttribPointer(
            program.aNormal, 3, GLES20.GL_FLOAT, false, STRIDE, vertexBuffer
        )
        GLES20.glEnableVertexAttribArray(program.aNormal)

        vertexBuffer.position(6)
        GLES20.glVertexAttribPointer(
            program.aColor, 4, GLES20.GL_FLOAT, false, STRIDE, vertexBuffer
        )
        GLES20.glEnableVertexAttribArray(program.aColor)

        if (indexBuffer != null && indexCount > 0) {
            indexBuffer.position(0)
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexCount, GLES20.GL_UNSIGNED_SHORT, indexBuffer)
        } else {
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)
        }
    }
}
