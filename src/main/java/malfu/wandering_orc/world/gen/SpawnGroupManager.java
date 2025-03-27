package malfu.wandering_orc.world.gen;

import malfu.wandering_orc.WanderingOrc;
import malfu.wandering_orc.entity.ModEntities;
import malfu.wandering_orc.util.config.SpawnConfig;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import java.util.*;

public class SpawnGroupManager {
    private static final int CHECK_INTERVAL = 20 * 60 * SpawnConfig.groupSpawnDelay;
    private static int patrolCheckCounter = 0;
    private static final Map<ServerWorld, ServerTickEvents.EndTick> registeredListeners = new HashMap<>();

    public static void register() {
        if(!SpawnConfig.groupSpawning) {
            WanderingOrc.LOGGER.info("[Wandering Orc] Group spawning is disabled in config file.");
            return;
        }

        WanderingOrc.LOGGER.info("[Wandering Orc] Registering group spawning for " + WanderingOrc.MOD_ID + ".");
        ServerWorldEvents.LOAD.register((server, world) -> {
            if (world.getRegistryKey() == World.OVERWORLD) {
                ServerTickEvents.EndTick listener = serverTick -> {
                    if (serverTick.getWorld(World.OVERWORLD) == world) {
                        patrolCheckCounter++;
                        if (patrolCheckCounter >= CHECK_INTERVAL) {
                            patrolCheckCounter = 0;
                            checkAndSpawnPatrol(world);
                        }
                    }
                };
                ServerTickEvents.END_SERVER_TICK.register(listener);
                registeredListeners.put(world, listener);
            }
        });
        ServerWorldEvents.UNLOAD.register((server, world) -> {
            if (world.getRegistryKey() == World.OVERWORLD) {
                registeredListeners.remove(world);
                WanderingOrc.LOGGER.info("[Wandering Orc] World unloaded, event listener removed.");
            }
        });
    }

    private static void checkAndSpawnPatrol(ServerWorld world) {
        // 1. First check if it's daytime
        if (world.isNight()) {
            WanderingOrc.LOGGER.debug("[Wandering Orc] Skipping patrol spawn - it's night time");
            return;
        }

        // 2. Proceed with normal spawn logic
        List<ServerPlayerEntity> players = world.getPlayers();
        if (!players.isEmpty()) {
            Random random = world.getRandom();
            PlayerEntity randomPlayer = players.get(random.nextInt(players.size()));

            if (randomPlayer.isAlive()) {
                BlockPos spawnPos = findSpawnPosition(world, randomPlayer);
                if (spawnPos != null) {
                    spawnOrcGroup(world, spawnPos, random);
                    WanderingOrc.LOGGER.info("[Wandering Orc] Daytime patrol spawned at: " + spawnPos);
                }
            }
        }
    }

    private static BlockPos findSpawnPosition(ServerWorld world, PlayerEntity player) {
        Random random = world.getRandom();
        BlockPos playerPos = player.getBlockPos();

        // Try to find a valid spawn position 40-70 blocks away from the player
        for (int i = 0; i < 10; i++) { // Try 10 times to find a valid position
            double angle = random.nextDouble() * 2 * Math.PI; // Random angle
            double distance = 40 + random.nextDouble() * 30; // Random distance between 40 and 70
            int x = playerPos.getX() + (int) (Math.cos(angle) * distance);
            int z = playerPos.getZ() + (int) (Math.sin(angle) * distance);
            BlockPos pos = new BlockPos(x, world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z), z);

            if (world.getBlockState(pos.down()).isSolidBlock(world, pos.down())) { // Check if the block below is solid
                return pos;
            }
        }

        return null; // No valid position found
    }

    private static void spawnOrcGroup(ServerWorld world, BlockPos centerPos, Random random) {
        // Spawn Orc Champion (0 or 1)
        if (random.nextBoolean()) {
            spawnMob(world, centerPos, ModEntities.ORC_CHAMPION, random);
        }

        // Spawn Minotaur (0 or 1)
        if (random.nextBoolean()) {
            spawnMob(world, centerPos, ModEntities.MINOTAUR, random);
        }

        // Spawn Orc Warrior (1 or 2)
        int orcWarriorCount = 1 + random.nextInt(2); // Randomly 1 or 2
        for (int i = 0; i < orcWarriorCount; i++) {
            spawnMob(world, centerPos, ModEntities.ORC_WARRIOR, random);
        }

        // Spawn Troll (1 or 2)
        int trollCount = 1 + random.nextInt(2); // Randomly 1 or 2
        for (int i = 0; i < trollCount; i++) {
            spawnMob(world, centerPos, ModEntities.TROLL, random);
        }

        // Spawn Orc Archer (1 or 2)
        int orcArcherCount = 1 + random.nextInt(2); // Randomly 1 or 2
        for (int i = 0; i < orcArcherCount; i++) {
            spawnMob(world, centerPos, ModEntities.ORC_ARCHER, random);
        }

        if (random.nextBoolean()) {
            spawnMob(world, centerPos, ModEntities.ORC_WARLOCK, random);
        }

        if (random.nextBoolean()) {
            spawnMob(world, centerPos, ModEntities.TROLL_DOCTOR, random);
        }
    }

    private static void spawnMob(ServerWorld world, BlockPos centerPos, EntityType<?> entityType, Random random) {
        Entity entity = entityType.create(world);
        if (entity != null) {
            // Spread out the spawn position around the center
            double offsetX = random.nextDouble() * 8 - 4; // Random offset between -4 and 4
            double offsetZ = random.nextDouble() * 8 - 4; // Random offset between -4 and 4
            BlockPos spawnPos = centerPos.add((int) offsetX, 0, (int) offsetZ);

            // Ensure the spawn position is valid
            spawnPos = new BlockPos(spawnPos.getX(), world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, spawnPos.getX(), spawnPos.getZ()), spawnPos.getZ());

            entity.refreshPositionAndAngles(spawnPos, random.nextFloat() * 360.0F, 0.0F);
            world.spawnEntity(entity);
        }
    }
}
