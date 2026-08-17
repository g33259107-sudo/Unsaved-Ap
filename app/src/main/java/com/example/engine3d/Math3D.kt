package com.example.engine3d

import kotlin.math.*

data class Vec3(var x: Float = 0f, var y: Float = 0f, var z: Float = 0f) {
    fun set(nx: Float, ny: Float, nz: Float): Vec3 {
        x = nx; y = ny; z = nz
        return this
    }

    fun set(other: Vec3): Vec3 {
        x = other.x; y = other.y; z = other.z
        return this
    }

    operator fun plus(o: Vec3) = Vec3(x + o.x, y + o.y, z + o.z)
    operator fun minus(o: Vec3) = Vec3(x - o.x, y - o.y, z - o.z)
    operator fun times(scale: Float) = Vec3(x * scale, y * scale, z * scale)

    fun length(): Float = sqrt(x * x + y * y + z * z)
    fun lengthSq(): Float = x * x + y * y + z * z

    fun normalize(): Vec3 {
        val len = length()
        if (len > 0.00001f) {
            x /= len
            y /= len
            z /= len
        }
        return this
    }

    fun normalized(): Vec3 {
        val len = length()
        return if (len > 0.00001f) Vec3(x / len, y / len, z / len) else Vec3(0f, 0f, 0f)
    }

    fun dot(o: Vec3): Float = x * o.x + y * o.y + z * o.z

    fun cross(o: Vec3): Vec3 = Vec3(
        y * o.z - z * o.y,
        z * o.x - x * o.z,
        x * o.y - y * o.x
    )

    fun distanceTo(o: Vec3): Float = (this - o).length()
    fun distanceToSq(o: Vec3): Float = (this - o).lengthSq()
}

data class AABB(
    val minX: Float, val minY: Float, val minZ: Float,
    val maxX: Float, val maxY: Float, val maxZ: Float
) {
    fun intersectsSphere(center: Vec3, radius: Float): Boolean {
        val closestX = center.x.coerceIn(minX, maxX)
        val closestY = center.y.coerceIn(minY, maxY)
        val closestZ = center.z.coerceIn(minZ, maxZ)
        val distSq = (center.x - closestX) * (center.x - closestX) +
                (center.y - closestY) * (center.y - closestY) +
                (center.z - closestZ) * (center.z - closestZ)
        return distSq <= radius * radius
    }

    fun intersectsRay(origin: Vec3, dir: Vec3, maxDistance: Float): Float? {
        var tmin = (minX - origin.x) / if (abs(dir.x) < 1e-6f) 1e-6f else dir.x
        var tmax = (maxX - origin.x) / if (abs(dir.x) < 1e-6f) 1e-6f else dir.x
        if (tmin > tmax) { val tmp = tmin; tmin = tmax; tmax = tmp }

        var tymin = (minY - origin.y) / if (abs(dir.y) < 1e-6f) 1e-6f else dir.y
        var tymax = (maxY - origin.y) / if (abs(dir.y) < 1e-6f) 1e-6f else dir.y
        if (tymin > tymax) { val tmp = tymin; tymin = tymax; tymax = tmp }

        if (tmin > tymax || tymin > tmax) return null
        if (tymin > tmin) tmin = tymin
        if (tymax < tmax) tmax = tymax

        var tzmin = (minZ - origin.z) / if (abs(dir.z) < 1e-6f) 1e-6f else dir.z
        var tzmax = (maxZ - origin.z) / if (abs(dir.z) < 1e-6f) 1e-6f else dir.z
        if (tzmin > tzmax) { val tmp = tzmin; tzmin = tzmax; tzmax = tmp }

        if (tmin > tzmax || tzmin > tmax) return null
        if (tzmin > tmin) tmin = tzmin
        if (tzmax < tmax) tmax = tzmax

        if (tmax < 0f || tmin > maxDistance) return null
        return if (tmin >= 0f) tmin else tmax
    }
}

class Mat4 {
    val m = FloatArray(16)

    init {
        identity()
    }

    fun identity(): Mat4 {
        for (i in 0..15) m[i] = 0f
        m[0] = 1f; m[5] = 1f; m[10] = 1f; m[15] = 1f
        return this
    }

    fun set(src: FloatArray): Mat4 {
        System.arraycopy(src, 0, m, 0, 16)
        return this
    }

    companion object {
        fun perspective(fovY: Float, aspect: Float, zNear: Float, zFar: Float, out: Mat4 = Mat4()): Mat4 {
            android.opengl.Matrix.perspectiveM(out.m, 0, fovY, aspect, zNear, zFar)
            return out
        }

        fun lookAt(
            eyeX: Float, eyeY: Float, eyeZ: Float,
            centerX: Float, centerY: Float, centerZ: Float,
            upX: Float, upY: Float, upZ: Float,
            out: Mat4 = Mat4()
        ): Mat4 {
            android.opengl.Matrix.setLookAtM(out.m, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ)
            return out
        }

        fun multiply(a: Mat4, b: Mat4, out: Mat4 = Mat4()): Mat4 {
            android.opengl.Matrix.multiplyMM(out.m, 0, a.m, 0, b.m, 0)
            return out
        }
    }
}
