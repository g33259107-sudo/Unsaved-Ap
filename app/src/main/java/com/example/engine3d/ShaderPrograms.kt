package com.example.engine3d

import android.opengl.GLES20
import android.util.Log

object ShaderPrograms {
    private const val TAG = "ShaderPrograms"

    private const val VERTEX_SHADER_MAIN = """
        uniform mat4 uMVPMatrix;
        uniform mat4 uModelMatrix;
        uniform mat3 uNormalMatrix;
        
        attribute vec3 aPosition;
        attribute vec3 aNormal;
        attribute vec4 aColor;
        
        varying vec3 vWorldPos;
        varying vec3 vNormal;
        varying vec4 vColor;
        
        void main() {
            vec4 worldPos = uModelMatrix * vec4(aPosition, 1.0);
            vWorldPos = worldPos.xyz;
            vNormal = normalize(uNormalMatrix * aNormal);
            vColor = aColor;
            gl_Position = uMVPMatrix * vec4(aPosition, 1.0);
        }
    """

    private const val FRAGMENT_SHADER_MAIN = """
        precision mediump float;
        
        varying vec3 vWorldPos;
        varying vec3 vNormal;
        varying vec4 vColor;
        
        uniform vec3 uCameraPos;
        uniform vec3 uAmbientColor;
        
        // Directional Light
        uniform vec3 uDirLightDir;
        uniform vec3 uDirLightColor;
        
        // Point Light 1 (Memory Shard / Lamp)
        uniform vec3 uPointLight1Pos;
        uniform vec3 uPointLight1Color;
        uniform float uPointLight1Radius;
        
        // Point Light 2 (Scene Spot / Neon)
        uniform vec3 uPointLight2Pos;
        uniform vec3 uPointLight2Color;
        uniform float uPointLight2Radius;
        
        // Flashlight (Spotlight)
        uniform vec3 uFlashlightPos;
        uniform vec3 uFlashlightDir;
        uniform vec3 uFlashlightColor;
        uniform float uFlashlightCutoff;
        uniform float uFlashlightEnabled;
        
        // Fog
        uniform vec3 uFogColor;
        uniform float uFogStart;
        uniform float uFogEnd;
        
        // Emissive Boost
        uniform float uEmissive;
        
        void main() {
            vec3 norm = normalize(vNormal);
            vec3 viewDir = normalize(uCameraPos - vWorldPos + vec3(0.0001, 0.0001, 0.0001));
            
            // 1. Ambient
            vec3 lighting = uAmbientColor;
            
            // 2. Directional Light
            vec3 lightDir = normalize(-uDirLightDir);
            float diff = max(dot(norm, lightDir), 0.0);
            lighting += uDirLightColor * diff;
            
            // 3. Point Light 1
            vec3 p1Dir = uPointLight1Pos - vWorldPos;
            float p1Dist = length(p1Dir);
            if (p1Dist < uPointLight1Radius) {
                p1Dir = normalize(p1Dir + vec3(0.0001, 0.0001, 0.0001));
                float p1Diff = max(dot(norm, p1Dir), 0.0);
                float atten = 1.0 - (p1Dist / max(uPointLight1Radius, 0.001));
                atten = atten * atten;
                lighting += uPointLight1Color * p1Diff * atten;
                
                // Specular
                vec3 reflectDir = reflect(-p1Dir, norm);
                float spec = pow(max(dot(viewDir, reflectDir), 0.0), 16.0);
                lighting += uPointLight1Color * spec * atten * 0.4;
            }
            
            // 4. Point Light 2
            vec3 p2Dir = uPointLight2Pos - vWorldPos;
            float p2Dist = length(p2Dir);
            if (p2Dist < uPointLight2Radius) {
                p2Dir = normalize(p2Dir + vec3(0.0001, 0.0001, 0.0001));
                float p2Diff = max(dot(norm, p2Dir), 0.0);
                float atten = 1.0 - (p2Dist / max(uPointLight2Radius, 0.001));
                atten = atten * atten;
                lighting += uPointLight2Color * p2Diff * atten;
            }
            
            // 5. Flashlight (Spotlight)
            if (uFlashlightEnabled > 0.5) {
                vec3 flashDir = normalize(uFlashlightPos - vWorldPos + vec3(0.0001, 0.0001, 0.0001));
                float flashDist = length(uFlashlightPos - vWorldPos);
                if (flashDist < 28.0) {
                    float theta = dot(-flashDir, normalize(uFlashlightDir + vec3(0.0001, 0.0001, 0.0001)));
                    if (theta > uFlashlightCutoff) {
                        float intensity = clamp((theta - uFlashlightCutoff) / max(1.0 - uFlashlightCutoff, 0.001), 0.0, 1.0);
                        float flashDiff = max(dot(norm, flashDir), 0.0);
                        float flashAtten = clamp(1.0 - (flashDist / 28.0), 0.0, 1.0);
                        lighting += uFlashlightColor * (flashDiff * 1.5 + 0.35) * flashAtten * intensity;
                    }
                }
            }
            
            // Emissive materials (glowing crystals, screens, neon)
            if (uEmissive > 0.0) {
                lighting += vec3(uEmissive);
            }
            
            vec3 finalColor = vColor.rgb * lighting;
            
            // 6. Atmospheric Distance Fog
            float dist = length(uCameraPos - vWorldPos);
            float fogRange = max(uFogEnd - uFogStart, 0.001);
            float fogFactor = clamp((dist - uFogStart) / fogRange, 0.0, 1.0);
            fogFactor = fogFactor * fogFactor;
            finalColor = mix(finalColor, uFogColor, fogFactor);
            
            gl_FragColor = vec4(finalColor, vColor.a);
        }
    """

    private const val VERTEX_SHADER_PARTICLE = """
        uniform mat4 uMVPMatrix;
        attribute vec3 aPosition;
        attribute vec4 aColor;
        attribute float aSize;
        varying vec4 vColor;
        
        void main() {
            vColor = aColor;
            gl_Position = uMVPMatrix * vec4(aPosition, 1.0);
            gl_PointSize = aSize;
        }
    """

    private const val FRAGMENT_SHADER_PARTICLE = """
        precision mediump float;
        varying vec4 vColor;
        
        void main() {
            gl_FragColor = vColor;
        }
    """

    class MainProgram(val programId: Int) {
        val uMVPMatrix = GLES20.glGetUniformLocation(programId, "uMVPMatrix")
        val uModelMatrix = GLES20.glGetUniformLocation(programId, "uModelMatrix")
        val uNormalMatrix = GLES20.glGetUniformLocation(programId, "uNormalMatrix")
        val uCameraPos = GLES20.glGetUniformLocation(programId, "uCameraPos")
        val uAmbientColor = GLES20.glGetUniformLocation(programId, "uAmbientColor")
        val uDirLightDir = GLES20.glGetUniformLocation(programId, "uDirLightDir")
        val uDirLightColor = GLES20.glGetUniformLocation(programId, "uDirLightColor")
        val uPointLight1Pos = GLES20.glGetUniformLocation(programId, "uPointLight1Pos")
        val uPointLight1Color = GLES20.glGetUniformLocation(programId, "uPointLight1Color")
        val uPointLight1Radius = GLES20.glGetUniformLocation(programId, "uPointLight1Radius")
        val uPointLight2Pos = GLES20.glGetUniformLocation(programId, "uPointLight2Pos")
        val uPointLight2Color = GLES20.glGetUniformLocation(programId, "uPointLight2Color")
        val uPointLight2Radius = GLES20.glGetUniformLocation(programId, "uPointLight2Radius")
        val uFlashlightPos = GLES20.glGetUniformLocation(programId, "uFlashlightPos")
        val uFlashlightDir = GLES20.glGetUniformLocation(programId, "uFlashlightDir")
        val uFlashlightColor = GLES20.glGetUniformLocation(programId, "uFlashlightColor")
        val uFlashlightCutoff = GLES20.glGetUniformLocation(programId, "uFlashlightCutoff")
        val uFlashlightEnabled = GLES20.glGetUniformLocation(programId, "uFlashlightEnabled")
        val uFogColor = GLES20.glGetUniformLocation(programId, "uFogColor")
        val uFogStart = GLES20.glGetUniformLocation(programId, "uFogStart")
        val uFogEnd = GLES20.glGetUniformLocation(programId, "uFogEnd")
        val uEmissive = GLES20.glGetUniformLocation(programId, "uEmissive")

        val aPosition = GLES20.glGetAttribLocation(programId, "aPosition")
        val aNormal = GLES20.glGetAttribLocation(programId, "aNormal")
        val aColor = GLES20.glGetAttribLocation(programId, "aColor")
    }

    class ParticleProgram(val programId: Int) {
        val uMVPMatrix = GLES20.glGetUniformLocation(programId, "uMVPMatrix")
        val aPosition = GLES20.glGetAttribLocation(programId, "aPosition")
        val aColor = GLES20.glGetAttribLocation(programId, "aColor")
        val aSize = GLES20.glGetAttribLocation(programId, "aSize")
    }

    fun buildMainProgram(): MainProgram? {
        val programId = createProgram(VERTEX_SHADER_MAIN, FRAGMENT_SHADER_MAIN)
        return if (programId != 0) MainProgram(programId) else null
    }

    fun buildParticleProgram(): ParticleProgram? {
        val programId = createProgram(VERTEX_SHADER_PARTICLE, FRAGMENT_SHADER_PARTICLE)
        return if (programId != 0) ParticleProgram(programId) else null
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        if (shader == 0) return 0
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        val compiled = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)
        if (compiled[0] == 0) {
            Log.e(TAG, "Shader compilation error: " + GLES20.glGetShaderInfoLog(shader))
            GLES20.glDeleteShader(shader)
            return 0
        }
        return shader
    }

    private fun createProgram(vertexCode: String, fragmentCode: String): Int {
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentCode)
        if (vertexShader == 0 || fragmentShader == 0) return 0

        val program = GLES20.glCreateProgram()
        if (program != 0) {
            GLES20.glAttachShader(program, vertexShader)
            GLES20.glAttachShader(program, fragmentShader)
            GLES20.glLinkProgram(program)
            val linkStatus = IntArray(1)
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e(TAG, "Could not link program: " + GLES20.glGetProgramInfoLog(program))
                GLES20.glDeleteProgram(program)
                return 0
            }
        }
        return program
    }
}
