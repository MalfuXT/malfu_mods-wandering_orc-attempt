package malfu.wandering_orc.world.gen;

import malfu.wandering_orc.entity.ModEntities;
import malfu.wandering_orc.entity.custom.*;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.BiomeKeys;


public class ModEntityGeneration {

    public static void addSpawns() {
        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld().or(BiomeSelectors.foundInTheNether()),
                SpawnGroup.MONSTER, ModEntities.ORC_ARCHER, 7, 2, 3);
        SpawnRestriction.register(ModEntities.ORC_ARCHER, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, OrcGroupEntity::canMobSpawn);

        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld().or(BiomeSelectors.foundInTheNether()),
                SpawnGroup.MONSTER, ModEntities.ORC_WARRIOR, 6, 1, 2);
        SpawnRestriction.register(ModEntities.ORC_WARRIOR, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnIgnoreLightLevel);

        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld().or(BiomeSelectors.foundInTheNether()),
                SpawnGroup.MONSTER, ModEntities.ORC_CHAMPION, 2, 1, 1);
        SpawnRestriction.register(ModEntities.ORC_CHAMPION, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, OrcGroupEntity::canSpawnIgnoreLightLevel);

        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld().or(BiomeSelectors.foundInTheNether()),
                SpawnGroup.MONSTER, ModEntities.TROLL, 7, 1, 3);
        SpawnRestriction.register(ModEntities.TROLL, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> {
                    // Custom spawning logic
                    if (world.getDifficulty() == Difficulty.PEACEFUL) {
                        return false; // Don't spawn in Peaceful difficulty
                    }

                    // Check if the block below is solid
                    if (!world.getBlockState(pos.down()).isSolidBlock(world, pos.down())) {
                        return false;
                    }

                    // Allow spawning in any light level
                    return HostileEntity.canSpawnIgnoreLightLevel(type, world, reason, pos, random);
                });

        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld().or(BiomeSelectors.foundInTheNether()),
                SpawnGroup.MONSTER, ModEntities.MINOTAUR, 3, 1, 1);
        SpawnRestriction.register(ModEntities.MINOTAUR, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, spawnReason, pos, random) -> {
            if(world.getDifficulty() == Difficulty.PEACEFUL) {
                return false;
            }
            return MinotaurEntity.canMobSpawn(type, world, spawnReason, pos, random);
        });
    }
}
