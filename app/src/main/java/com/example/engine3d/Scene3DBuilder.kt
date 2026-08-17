package com.example.engine3d

import com.example.data.EntityType
import com.example.data.LocationId

data class Scene3DEnvironment(
    val locationId: LocationId,
    val environmentMesh: Mesh3D,
    val collisionBoxes: List<AABB>,
    val entities: List<Entity3D>,
    val playerSpawnPos: Vec3,
    val playerSpawnYaw: Float,
    val ambientColor: Vec3,
    val dirLightDir: Vec3,
    val dirLightColor: Vec3,
    val pointLight1Pos: Vec3,
    val pointLight1Color: Vec3,
    val pointLight1Radius: Float,
    val pointLight2Pos: Vec3,
    val pointLight2Color: Vec3,
    val pointLight2Radius: Float,
    val fogColor: Vec3,
    val keeperWaypoints: List<Vec3> = emptyList(),
    val keeperStartPos: Vec3 = Vec3(0f, 0f, 0f)
)

data class Entity3D(
    val id: String,
    val name: String,
    val position: Vec3,
    val interactionRadius: Float = 1.4f,
    val promptText: String = "Inspect",
    val type: EntityType = EntityType.INTERACTABLE,
    val targetDialogueId: String? = null,
    val targetPuzzleId: String? = null,
    val targetMemoryId: String? = null,
    val requiredItemId: String? = null
)

object Scene3DBuilder {

    fun buildScene(locationId: LocationId): Scene3DEnvironment {
        val meshes = ArrayList<Mesh3D>()
        val colliders = ArrayList<AABB>()
        val entities = ArrayList<Entity3D>()

        var spawnPos = Vec3(0f, 0f, 2f)
        var spawnYaw = 0f

        var ambCol = Vec3(0.32f, 0.35f, 0.42f)
        var dirDir = Vec3(0.3f, 0.8f, 0.5f).normalize()
        var dirCol = Vec3(0.55f, 0.60f, 0.68f)

        var p1Pos = Vec3(0f, 3f, 5f)
        var p1Col = Vec3(0.3f, 0.85f, 1.0f)
        var p1Rad = 20f

        var p2Pos = Vec3(-4f, 2.5f, -2f)
        var p2Col = Vec3(1.0f, 0.75f, 0.35f)
        var p2Rad = 18f

        var fogCol = Vec3(0.08f, 0.10f, 0.14f)
        val keeperWaypoints = mutableListOf<Vec3>()
        var keeperStartPos = Vec3(0f, 0f, 0f)

        when (locationId) {
            LocationId.NOCTURNE_STATION -> {
                // ACT 1 — ARRIVAL: Nocturne Station Platform 4
                spawnPos = Vec3(0f, 0f, -8f)
                spawnYaw = 0f

                // Main concrete platform floor
                meshes.add(Mesh3D.createBox(0f, -0.2f, 0f, 12f, 0.4f, 28f, 0.18f, 0.20f, 0.22f))
                // Platform safety edge tactile strips (yellow-amber)
                meshes.add(Mesh3D.createBox(4.5f, 0.02f, 0f, 0.4f, 0.05f, 28f, 0.7f, 0.6f, 0.1f))

                // Track Trench (Lower depth)
                meshes.add(Mesh3D.createBox(7.5f, -1.2f, 0f, 5.5f, 0.4f, 32f, 0.08f, 0.09f, 0.1f))
                // Rails
                meshes.add(Mesh3D.createBox(6.8f, -0.95f, 0f, 0.12f, 0.1f, 32f, 0.4f, 0.42f, 0.45f))
                meshes.add(Mesh3D.createBox(8.2f, -0.95f, 0f, 0.12f, 0.1f, 32f, 0.4f, 0.42f, 0.45f))

                // Stationary Train Cars
                meshes.add(Mesh3D.createBox(7.5f, 0.8f, -4f, 2.6f, 3.2f, 18f, 0.12f, 0.14f, 0.18f))
                // Train glowing blue-tinted windows
                meshes.add(Mesh3D.createBox(6.15f, 1.2f, -4f, 0.1f, 0.9f, 16f, 0.2f, 0.6f, 0.9f, 0.8f))

                // Left platform brick wall
                meshes.add(Mesh3D.createBox(-5.8f, 2.5f, 0f, 0.6f, 5.5f, 28f, 0.14f, 0.15f, 0.17f))
                colliders.add(AABB(-6.2f, 0f, -14f, -5.4f, 5f, 14f))

                // North End Wall & Gate
                meshes.add(Mesh3D.createBox(0f, 2.5f, 13.8f, 12f, 5.5f, 0.6f, 0.14f, 0.15f, 0.17f))
                colliders.add(AABB(-6f, 0f, 13.4f, 6f, 5f, 14.2f))

                // South End Wall
                meshes.add(Mesh3D.createBox(0f, 2.5f, -13.8f, 12f, 5.5f, 0.6f, 0.14f, 0.15f, 0.17f))
                colliders.add(AABB(-6f, 0f, -14.2f, 6f, 5f, -13.4f))

                // Right Track Edge Collider
                colliders.add(AABB(4.8f, 0f, -14f, 5.2f, 3f, 14f))

                // Station Structural Pillars with Lanterns
                for (z in listOf(-8f, -2f, 4f, 10f)) {
                    meshes.add(Mesh3D.createBox(-2.5f, 2.2f, z, 0.7f, 4.8f, 0.7f, 0.25f, 0.28f, 0.32f))
                    colliders.add(AABB(-2.9f, 0f, z - 0.4f, -2.1f, 4.5f, z + 0.4f))
                    // Amber lantern casing
                    meshes.add(Mesh3D.createBox(-2.5f, 3.2f, z + 0.4f, 0.25f, 0.4f, 0.25f, 1.0f, 0.75f, 0.2f))
                }

                // Overhead Steel Beams & High Glass Ceiling
                meshes.add(Mesh3D.createBox(0f, 4.8f, 0f, 12f, 0.3f, 28f, 0.15f, 0.16f, 0.18f))

                // Wooden Bench with Phone & Umbrella (Start Location)
                meshes.add(Mesh3D.createBox(-4.2f, 0.4f, -7.5f, 1.4f, 0.6f, 2.2f, 0.35f, 0.22f, 0.15f))
                colliders.add(AABB(-5f, 0f, -8.7f, -3.4f, 1.2f, -6.3f))

                // Breaker Box on wall
                meshes.add(Mesh3D.createBox(-5.4f, 1.5f, 2f, 0.25f, 0.9f, 0.7f, 0.4f, 0.45f, 0.5f))

                // Steel Gate Door at end of platform
                meshes.add(Mesh3D.createBox(0f, 1.4f, 13.2f, 2.8f, 2.8f, 0.2f, 0.3f, 0.35f, 0.4f))

                // Interactive Entities
                entities.add(
                    Entity3D(
                        id = "ENT_1_BENCH",
                        name = "Platform Bench (Meera's Phone)",
                        position = Vec3(-4.2f, 0.8f, -7.5f),
                        promptText = "Examine Phone & Umbrella",
                        type = EntityType.INTERACTABLE,
                        targetDialogueId = "C1_BENCH"
                    )
                )

                entities.add(
                    Entity3D(
                        id = "ENT_1_IRIS",
                        name = "Iris Chen (Station Attendant)",
                        position = Vec3(-1.5f, 0.9f, -1.0f),
                        promptText = "Talk to Iris",
                        type = EntityType.NPC,
                        targetDialogueId = "C1_IRIS_1"
                    )
                )

                entities.add(
                    Entity3D(
                        id = "ENT_1_FUSE",
                        name = "Circuit Breaker Junction",
                        position = Vec3(-5.2f, 1.4f, 2.0f),
                        promptText = "Restore Platform Power",
                        type = EntityType.PUZZLE_STATION,
                        targetPuzzleId = "PUZZLE_FUSE_1"
                    )
                )

                entities.add(
                    Entity3D(
                        id = "ENT_1_MEM1",
                        name = "Memory Shard #1 (The Rooftop Vow)",
                        position = Vec3(1.2f, 1.2f, 6.0f),
                        promptText = "Relive Memory",
                        type = EntityType.MEMORY_SHARD,
                        targetMemoryId = "MEM_01"
                    )
                )

                entities.add(
                    Entity3D(
                        id = "ENT_1_GATE",
                        name = "Security Gate to City Avenue",
                        position = Vec3(0f, 1.2f, 13.0f),
                        promptText = "Pass Through Gate",
                        type = EntityType.DOOR_PORTAL,
                        targetDialogueId = "C1_GATE"
                    )
                )

                p1Pos = Vec3(1.2f, 1.8f, 6.0f) // Shard glow
                p1Col = Vec3(0.2f, 0.85f, 1.0f)
                p2Pos = Vec3(-2.5f, 3.0f, -2.0f) // Platform lantern
                p2Col = Vec3(1.0f, 0.7f, 0.25f)
            }

            LocationId.RAINY_AVENUE -> {
                // ACT 2 — INVESTIGATION: Rainy City Avenue Hub
                spawnPos = Vec3(0f, 0f, -10f)
                spawnYaw = 0f

                // Damp asphalt street
                meshes.add(Mesh3D.createBox(0f, -0.2f, 0f, 14f, 0.4f, 30f, 0.12f, 0.13f, 0.15f))
                // Sidewalks
                meshes.add(Mesh3D.createBox(-5f, 0.05f, 0f, 3.5f, 0.2f, 30f, 0.22f, 0.24f, 0.26f))
                meshes.add(Mesh3D.createBox(5f, 0.05f, 0f, 3.5f, 0.2f, 30f, 0.22f, 0.24f, 0.26f))

                // Left Brick Buildings (Apartment 404 facade)
                meshes.add(Mesh3D.createBox(-8f, 4.5f, 0f, 3f, 9.5f, 30f, 0.16f, 0.14f, 0.15f))
                colliders.add(AABB(-9.5f, 0f, -15f, -6.5f, 8f, 15f))

                // Right Buildings (Cafe Nocturne facade)
                meshes.add(Mesh3D.createBox(8f, 4.5f, 0f, 3f, 9.5f, 30f, 0.14f, 0.15f, 0.18f))
                colliders.add(AABB(6.5f, 0f, -15f, 9.5f, 8f, 15f))

                // North End Alley Barrier
                colliders.add(AABB(-7f, 0f, 14.2f, 7f, 8f, 15f))
                colliders.add(AABB(-7f, 0f, -15f, 7f, 8f, -14.2f))

                // Cafe Neon Sign overhang
                meshes.add(Mesh3D.createBox(6.4f, 3.2f, 2f, 0.2f, 0.8f, 3.2f, 0.9f, 0.15f, 0.35f))
                // Apartment Door Stoop
                meshes.add(Mesh3D.createBox(-6.2f, 1.2f, -3f, 0.4f, 2.4f, 1.4f, 0.25f, 0.28f, 0.3f))

                // Rain-slicked Newspaper Kiosk
                meshes.add(Mesh3D.createBox(-4.2f, 1.2f, 6f, 1.4f, 2.2f, 1.6f, 0.28f, 0.26f, 0.22f))
                colliders.add(AABB(-5f, 0f, 5.1f, -3.4f, 2.5f, 6.9f))

                // Interactive Entities
                entities.add(
                    Entity3D(
                        id = "ENT_AV_BUSKER",
                        name = "Memory Shard #2 (The Busker's Melody)",
                        position = Vec3(-2.8f, 1.2f, -5.5f),
                        promptText = "Listen to Street Melody",
                        type = EntityType.MEMORY_SHARD,
                        targetMemoryId = "MEM_02"
                    )
                )

                entities.add(
                    Entity3D(
                        id = "ENT_AV_APT",
                        name = "Apartment 404 Entrance",
                        position = Vec3(-5.8f, 1.2f, -3.0f),
                        promptText = "Enter Apartment 404",
                        type = EntityType.DOOR_PORTAL,
                        targetDialogueId = "AV_APT_ENTER"
                    )
                )

                entities.add(
                    Entity3D(
                        id = "ENT_AV_CAFE",
                        name = "Cafe Nocturne Entrance",
                        position = Vec3(5.8f, 1.2f, 2.0f),
                        promptText = "Enter Cafe Nocturne",
                        type = EntityType.DOOR_PORTAL,
                        targetDialogueId = "C2_DOOR"
                    )
                )

                entities.add(
                    Entity3D(
                        id = "ENT_AV_CATACOMB_GATE",
                        name = "Subway Catacombs Entrance",
                        position = Vec3(0f, 1.2f, 13.5f),
                        promptText = "Descend to Catacombs",
                        type = EntityType.DOOR_PORTAL,
                        targetDialogueId = "AV_CATACOMB_ENTER"
                    )
                )

                p1Pos = Vec3(6.4f, 3.2f, 2f) // Neon glow
                p1Col = Vec3(1.0f, 0.2f, 0.4f)
                p2Pos = Vec3(-2.8f, 1.5f, -5.5f) // Busker shard
                p2Col = Vec3(0.2f, 0.85f, 1.0f)
            }

            LocationId.CAFE_NOCTURNE -> {
                // ACT 2 — INVESTIGATION: Cafe Nocturne
                spawnPos = Vec3(0f, 0f, -6f)
                spawnYaw = 0f

                // Warm wooden floorboards
                meshes.add(Mesh3D.createBox(0f, -0.2f, 0f, 14f, 0.4f, 16f, 0.32f, 0.20f, 0.12f))
                // Walls
                meshes.add(Mesh3D.createBox(-6.8f, 2.2f, 0f, 0.4f, 4.8f, 16f, 0.18f, 0.16f, 0.15f))
                colliders.add(AABB(-7f, 0f, -8f, -6.4f, 4f, 8f))
                meshes.add(Mesh3D.createBox(6.8f, 2.2f, 0f, 0.4f, 4.8f, 16f, 0.18f, 0.16f, 0.15f))
                colliders.add(AABB(6.4f, 0f, -8f, 7f, 4f, 8f))
                meshes.add(Mesh3D.createBox(0f, 2.2f, 7.8f, 14f, 4.8f, 0.4f, 0.18f, 0.16f, 0.15f))
                colliders.add(AABB(-7f, 0f, 7.4f, 7f, 4f, 8f))
                meshes.add(Mesh3D.createBox(0f, 2.2f, -7.8f, 14f, 4.8f, 0.4f, 0.18f, 0.16f, 0.15f))
                colliders.add(AABB(-7f, 0f, -8f, 7f, 4f, -7.4f))

                // Bar Counter
                meshes.add(Mesh3D.createBox(-3.5f, 0.6f, 2f, 1.8f, 1.2f, 7f, 0.4f, 0.25f, 0.15f))
                colliders.add(AABB(-4.5f, 0f, -1.6f, -2.5f, 2f, 5.6f))

                // Cafe Table & Booths
                meshes.add(Mesh3D.createBox(3.8f, 0.4f, -2f, 2.2f, 0.8f, 1.8f, 0.35f, 0.22f, 0.14f))
                colliders.add(AABB(2.6f, 0f, -3f, 5f, 1.5f, -1f))

                // Safe Dial Box on Counter
                meshes.add(Mesh3D.createBox(-3.2f, 1.25f, 4.5f, 0.5f, 0.4f, 0.5f, 0.22f, 0.24f, 0.28f))

                // Entities
                entities.add(
                    Entity3D(
                        id = "ENT_2_MEERA_ECHO",
                        name = "Echo of Meera",
                        position = Vec3(3.8f, 0.9f, -2.0f),
                        promptText = "Speak with Meera's Echo",
                        type = EntityType.NPC,
                        targetDialogueId = "C2_MEERA_1"
                    )
                )

                entities.add(
                    Entity3D(
                        id = "ENT_2_SAFE",
                        name = "Vintage Dial Safe",
                        position = Vec3(-3.2f, 1.3f, 4.5f),
                        promptText = "Unlock Safe Dial",
                        type = EntityType.PUZZLE_STATION,
                        targetPuzzleId = "PUZZLE_COFFEE_2"
                    )
                )

                entities.add(
                    Entity3D(
                        id = "ENT_2_MEM3",
                        name = "Memory Shard #3 (The Unsent Letter)",
                        position = Vec3(3.8f, 1.1f, 3.5f),
                        promptText = "Examine Shard",
                        type = EntityType.MEMORY_SHARD,
                        targetMemoryId = "MEM_03"
                    )
                )

                entities.add(
                    Entity3D(
                        id = "ENT_2_MEM4",
                        name = "Memory Shard #4 (Promise Under the Neon)",
                        position = Vec3(-3.2f, 1.1f, -1.0f),
                        promptText = "Examine Shard",
                        type = EntityType.MEMORY_SHARD,
                        targetMemoryId = "MEM_04"
                    )
                )

                entities.add(
                    Entity3D(
                        id = "ENT_2_EXIT",
                        name = "Return to City Avenue",
                        position = Vec3(0f, 1.0f, -7.0f),
                        promptText = "Exit Cafe",
                        type = EntityType.DOOR_PORTAL,
                        targetDialogueId = "CAFE_LEAVE"
                    )
                )

                ambCol = Vec3(0.12f, 0.08f, 0.05f)
                p1Pos = Vec3(3.8f, 2.2f, -2.0f)
                p1Col = Vec3(1.0f, 0.8f, 0.4f)
                p2Pos = Vec3(-3.2f, 1.8f, 4.5f)
                p2Col = Vec3(0.3f, 0.8f, 1.0f)
            }

            LocationId.APARTMENT_404 -> {
                // ACT 2 — INVESTIGATION: Apartment 404 & Rooftop
                spawnPos = Vec3(0f, 0f, -4f)
                spawnYaw = 0f

                meshes.add(Mesh3D.createBox(0f, -0.2f, 0f, 10f, 0.4f, 12f, 0.24f, 0.22f, 0.20f))
                meshes.add(Mesh3D.createBox(-4.8f, 2.2f, 0f, 0.4f, 4.8f, 12f, 0.16f, 0.15f, 0.14f))
                colliders.add(AABB(-5.2f, 0f, -6f, -4.4f, 4f, 6f))
                meshes.add(Mesh3D.createBox(4.8f, 2.2f, 0f, 0.4f, 4.8f, 12f, 0.16f, 0.15f, 0.14f))
                colliders.add(AABB(4.4f, 0f, -6f, 5.2f, 4f, 6f))
                meshes.add(Mesh3D.createBox(0f, 2.2f, 5.8f, 10f, 4.8f, 0.4f, 0.16f, 0.15f, 0.14f))
                colliders.add(AABB(-5f, 0f, 5.4f, 5f, 4f, 6.2f))
                meshes.add(Mesh3D.createBox(0f, 2.2f, -5.8f, 10f, 4.8f, 0.4f, 0.4f, 0.16f, 0.15f, 0.14f))
                colliders.add(AABB(-5f, 0f, -6.2f, 5f, 4f, -5.4f))

                // Radio Desk
                meshes.add(Mesh3D.createBox(-2.8f, 0.5f, 3.5f, 2.2f, 1.0f, 1.4f, 0.32f, 0.22f, 0.15f))
                colliders.add(AABB(-4f, 0f, 2.7f, -1.6f, 1.5f, 4.3f))

                // Balcony Railing overlooking rain
                meshes.add(Mesh3D.createBox(2.5f, 0.5f, 4.8f, 3.2f, 1.0f, 0.2f, 0.35f, 0.38f, 0.42f))

                entities.add(
                    Entity3D(
                        id = "ENT_APT_RADIO",
                        name = "Emergency Frequency Radio",
                        position = Vec3(-2.8f, 1.1f, 3.5f),
                        promptText = "Tune Radio Frequency",
                        type = EntityType.PUZZLE_STATION,
                        targetPuzzleId = "PUZZLE_RADIO_3"
                    )
                )

                entities.add(
                    Entity3D(
                        id = "ENT_APT_ROOFTOP",
                        name = "Balcony Rain Overlook",
                        position = Vec3(2.5f, 1.1f, 4.5f),
                        promptText = "Look Out at Rain",
                        type = EntityType.INTERACTABLE,
                        targetDialogueId = "APT_ROOFTOP"
                    )
                )

                entities.add(
                    Entity3D(
                        id = "ENT_APT_EXIT",
                        name = "Return to City Avenue",
                        position = Vec3(0f, 1.0f, -5.0f),
                        promptText = "Exit Apartment",
                        type = EntityType.DOOR_PORTAL,
                        targetDialogueId = "APT_LEAVE"
                    )
                )

                p1Pos = Vec3(-2.8f, 1.8f, 3.5f)
                p1Col = Vec3(0.2f, 0.9f, 0.5f)
            }

            LocationId.CATACOMBS_DEPOT -> {
                // ACT 3 — DANGER & STEALTH: Subway Catacombs & Maintenance Depot
                spawnPos = Vec3(0f, 0f, -14f)
                spawnYaw = 0f

                // Rusted metallic and damp concrete tunnel
                meshes.add(Mesh3D.createBox(0f, -0.2f, 0f, 12f, 0.4f, 36f, 0.10f, 0.11f, 0.12f))
                meshes.add(Mesh3D.createBox(-5.8f, 2.5f, 0f, 0.5f, 5.5f, 36f, 0.14f, 0.12f, 0.11f))
                colliders.add(AABB(-6.2f, 0f, -18f, -5.4f, 5f, 18f))
                meshes.add(Mesh3D.createBox(5.8f, 2.5f, 0f, 0.5f, 5.5f, 36f, 0.14f, 0.12f, 0.11f))
                colliders.add(AABB(5.4f, 0f, -18f, 6.2f, 5f, 18f))
                colliders.add(AABB(-6f, 0f, 17.5f, 6f, 5f, 18.5f))
                colliders.add(AABB(-6f, 0f, -18.5f, 6f, 5f, -17.5f))

                // Low Hanging Industrial Steam Pipes (Requires Crouching to crawl under)
                meshes.add(Mesh3D.createBox(0f, 1.35f, -2f, 11f, 0.5f, 0.8f, 0.35f, 0.28f, 0.20f))
                // Collider blocks standing players (height > 1.2m)
                colliders.add(AABB(-5.5f, 1.1f, -2.5f, 5.5f, 2.5f, -1.5f))

                // Locker / Hiding Spot 1 (Left Alcove)
                meshes.add(Mesh3D.createBox(-4.8f, 1.2f, -8f, 1.2f, 2.4f, 1.0f, 0.25f, 0.30f, 0.35f))
                // Locker / Hiding Spot 2 (Right Alcove)
                meshes.add(Mesh3D.createBox(4.8f, 1.2f, 6f, 1.2f, 2.4f, 1.0f, 0.25f, 0.30f, 0.35f))

                // Steam Valve Station at middle depot
                meshes.add(Mesh3D.createBox(0f, 1.2f, 8f, 2.2f, 2.4f, 1.0f, 0.35f, 0.38f, 0.40f))
                colliders.add(AABB(-1.2f, 0f, 7.4f, 1.2f, 2.6f, 8.6f))

                // The Keeper AI Patrol Route Waypoints
                keeperWaypoints.addAll(
                    listOf(
                        Vec3(0f, 0f, 12f),
                        Vec3(-3.5f, 0f, 4f),
                        Vec3(3.5f, 0f, -4f),
                        Vec3(0f, 0f, -10f)
                    )
                )
                keeperStartPos = Vec3(0f, 0f, 12f)

                // Entities
                entities.add(
                    Entity3D(
                        id = "ENT_CATACOMB_LOCKER_1",
                        name = "Maintenance Locker (Hide)",
                        position = Vec3(-4.6f, 1.0f, -8.0f),
                        promptText = "Hide Inside Locker",
                        type = EntityType.HIDING_SPOT
                    )
                )

                entities.add(
                    Entity3D(
                        id = "ENT_CATACOMB_LOCKER_2",
                        name = "Ventilation Alcove (Hide)",
                        position = Vec3(4.6f, 1.0f, 6.0f),
                        promptText = "Hide in Alcove",
                        type = EntityType.HIDING_SPOT
                    )
                )

                entities.add(
                    Entity3D(
                        id = "ENT_CATACOMB_VALVES",
                        name = "Steam Pressure Equalizer",
                        position = Vec3(0f, 1.2f, 7.6f),
                        promptText = "Align Steam Valves",
                        type = EntityType.PUZZLE_STATION,
                        targetPuzzleId = "PUZZLE_STEAM_4"
                    )
                )

                entities.add(
                    Entity3D(
                        id = "ENT_CATACOMB_MEM5",
                        name = "Memory Shard #5 (The Catacomb Passage)",
                        position = Vec3(-3.5f, 1.0f, 14.0f),
                        promptText = "Recover Memory",
                        type = EntityType.MEMORY_SHARD,
                        targetMemoryId = "MEM_05"
                    )
                )

                entities.add(
                    Entity3D(
                        id = "ENT_CATACOMB_EXIT",
                        name = "Elevator to Station Tracks",
                        position = Vec3(0f, 1.2f, 16.5f),
                        promptText = "Ascend to Clocktower Tracks",
                        type = EntityType.DOOR_PORTAL,
                        targetDialogueId = "C3_STATION_ENTER"
                    )
                )

                ambCol = Vec3(0.04f, 0.04f, 0.05f)
                fogCol = Vec3(0.02f, 0.02f, 0.03f)
                p1Pos = Vec3(0f, 1.5f, 8f) // Steam console
                p1Col = Vec3(1.0f, 0.3f, 0.1f)
                p2Pos = Vec3(-3.5f, 1.5f, 14f) // Shard
                p2Col = Vec3(0.2f, 0.85f, 1.0f)
            }

            LocationId.STATION_TRACKS -> {
                // ACT 4 — TRUTH: Clocktower & Track 1
                spawnPos = Vec3(0f, 0f, -10f)
                spawnYaw = 0f

                meshes.add(Mesh3D.createBox(0f, -0.2f, 0f, 14f, 0.4f, 26f, 0.18f, 0.20f, 0.22f))
                meshes.add(Mesh3D.createBox(-6.8f, 3.5f, 0f, 0.5f, 7.5f, 26f, 0.16f, 0.18f, 0.20f))
                colliders.add(AABB(-7.2f, 0f, -13f, -6.4f, 6f, 13f))
                meshes.add(Mesh3D.createBox(6.8f, 3.5f, 0f, 0.5f, 7.5f, 26f, 0.16f, 0.18f, 0.20f))
                colliders.add(AABB(6.4f, 0f, -13f, 7.2f, 6f, 13f))
                colliders.add(AABB(-7f, 0f, 12.5f, 7f, 6f, 13.5f))
                colliders.add(AABB(-7f, 0f, -13.5f, 7f, 6f, -12.5f))

                // Grand Clock Mechanism Stand
                meshes.add(Mesh3D.createBox(0f, 2.2f, 6f, 3.4f, 4.4f, 1.2f, 0.38f, 0.32f, 0.22f))
                colliders.add(AABB(-1.8f, 0f, 5.2f, 1.8f, 4.5f, 6.8f))

                entities.add(
                    Entity3D(
                        id = "ENT_3_CLOCK",
                        name = "Grand Clock Mechanism (07:18)",
                        position = Vec3(0f, 1.4f, 5.2f),
                        promptText = "Align Clock Gears",
                        type = EntityType.PUZZLE_STATION,
                        targetPuzzleId = "PUZZLE_CLOCK_3"
                    )
                )

                entities.add(
                    Entity3D(
                        id = "ENT_3_MEM6",
                        name = "Memory Shard #6 (The Broken Locket)",
                        position = Vec3(4.0f, 1.1f, -2.0f),
                        promptText = "Inspect Locket Shard",
                        type = EntityType.MEMORY_SHARD,
                        targetMemoryId = "MEM_06"
                    )
                )

                entities.add(
                    Entity3D(
                        id = "ENT_3_PORTAL",
                        name = "Archway to Memory Archive",
                        position = Vec3(0f, 1.2f, 11.5f),
                        promptText = "Enter Core Archive",
                        type = EntityType.DOOR_PORTAL,
                        targetDialogueId = "C4_ARCHIVE_ENTER"
                    )
                )

                p1Pos = Vec3(0f, 2.5f, 5.2f)
                p1Col = Vec3(1.0f, 0.8f, 0.3f)
            }

            LocationId.MEMORY_ARCHIVE -> {
                // ACT 4 — TRUTH: The Core Memory Archive
                spawnPos = Vec3(0f, 0f, -8f)
                spawnYaw = 0f

                // Obsidian reflective circular dais
                meshes.add(Mesh3D.createBox(0f, -0.2f, 0f, 16f, 0.4f, 20f, 0.08f, 0.10f, 0.15f))
                // Floating Crystalline Monolith Pillars
                for (x in listOf(-5f, 5f)) {
                    for (z in listOf(-6f, 0f, 6f)) {
                        meshes.add(Mesh3D.createBox(x, 3.0f, z, 0.8f, 6.0f, 0.8f, 0.2f, 0.6f, 0.9f))
                        colliders.add(AABB(x - 0.5f, 0f, z - 0.5f, x + 0.5f, 5f, z + 0.5f))
                    }
                }

                // Central Memory Altar
                meshes.add(Mesh3D.createBox(0f, 0.6f, 2f, 2.4f, 1.2f, 2.4f, 0.18f, 0.25f, 0.35f))
                colliders.add(AABB(-1.3f, 0f, 0.7f, 1.3f, 1.5f, 3.3f))

                entities.add(
                    Entity3D(
                        id = "ENT_4_ARCHIVIST",
                        name = "The Archivist (Unknown Voice)",
                        position = Vec3(0f, 1.0f, -2.0f),
                        promptText = "Confront the Truth",
                        type = EntityType.NPC,
                        targetDialogueId = "C4_ARCHIVE_ENTER"
                    )
                )

                entities.add(
                    Entity3D(
                        id = "ENT_4_MEM7",
                        name = "Memory Shard #7 (The Unsaved Truth)",
                        position = Vec3(0f, 1.4f, 2.0f),
                        promptText = "Unlock Ultimate Truth",
                        type = EntityType.MEMORY_SHARD,
                        targetMemoryId = "MEM_07"
                    )
                )

                entities.add(
                    Entity3D(
                        id = "ENT_4_SUMMIT_DOOR",
                        name = "Ascent to Hilltop Overlook",
                        position = Vec3(0f, 1.2f, 8.5f),
                        promptText = "Ascend to Summit",
                        type = EntityType.DOOR_PORTAL,
                        targetDialogueId = "C5_OVERLOOK_1"
                    )
                )

                ambCol = Vec3(0.08f, 0.12f, 0.20f)
                p1Pos = Vec3(0f, 2.0f, 2.0f)
                p1Col = Vec3(0.4f, 0.9f, 1.0f)
            }

            LocationId.HILLTOP_OVERLOOK -> {
                // ACT 5 — FINAL DECISION: Hilltop Overlook at Dawn
                spawnPos = Vec3(0f, 0f, -8f)
                spawnYaw = 0f

                // Grassy cliffside ledge
                meshes.add(Mesh3D.createBox(0f, -0.2f, 0f, 14f, 0.4f, 20f, 0.15f, 0.22f, 0.18f))
                // Cliff Edge Boundary
                colliders.add(AABB(-7f, 0f, 8.5f, 7f, 3f, 9.5f))
                colliders.add(AABB(-7.5f, 0f, -10f, -6.5f, 3f, 9f))
                colliders.add(AABB(6.5f, 0f, -10f, 7.5f, 3f, 9f))
                colliders.add(AABB(-7f, 0f, -10.5f, 7f, 3f, -9.5f))

                entities.add(
                    Entity3D(
                        id = "ENT_5_MEERA_FINAL",
                        name = "Meera at the Edge of Dawn",
                        position = Vec3(0f, 1.0f, 4.0f),
                        promptText = "Make Your Final Choice",
                        type = EntityType.NPC,
                        targetDialogueId = "C5_MEERA_FINAL"
                    )
                )

                ambCol = Vec3(0.25f, 0.18f, 0.22f) // Dawn light
                dirDir = Vec3(0f, 0.3f, 1.0f).normalize()
                dirCol = Vec3(0.9f, 0.6f, 0.4f)
                fogCol = Vec3(0.18f, 0.14f, 0.18f)
                p1Pos = Vec3(0f, 1.8f, 4.0f)
                p1Col = Vec3(1.0f, 0.75f, 0.4f)
            }
        }

        val combinedMesh = Mesh3D.combine(meshes)

        return Scene3DEnvironment(
            locationId = locationId,
            environmentMesh = combinedMesh,
            collisionBoxes = colliders,
            entities = entities,
            playerSpawnPos = spawnPos,
            playerSpawnYaw = spawnYaw,
            ambientColor = ambCol,
            dirLightDir = dirDir,
            dirLightColor = dirCol,
            pointLight1Pos = p1Pos,
            pointLight1Color = p1Col,
            pointLight1Radius = p1Rad,
            pointLight2Pos = p2Pos,
            pointLight2Color = p2Col,
            pointLight2Radius = p2Rad,
            fogColor = fogCol,
            keeperWaypoints = keeperWaypoints,
            keeperStartPos = keeperStartPos
        )
    }
}
