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
import net.minecraft.world.gen.GenerationStep;


public class ModEntityGeneration {

    public static void addSpawns() {
        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld().and(BiomeSelectors.excludeByKey(BiomeKeys.LUSH_CAVES,BiomeKeys.DRIPSTONE_CAVES)),
                SpawnGroup.CREATURE, ModEntities.ORC_ARCHER, 15, 1, 2);
        SpawnRestriction.register(ModEntities.ORC_ARCHER, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, OrcArcherEntity::canMobSpawnWithRate);

        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld(),
                SpawnGroup.CREATURE, ModEntities.ORC_WARRIOR, 10, 1, 2);
        SpawnRestriction.register(ModEntities.ORC_WARRIOR, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, OrcWarriorEntity::canMobSpawnWithRate);

        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld(),
                SpawnGroup.CREATURE, ModEntities.ORC_CHAMPION, 5, 1, 1);
        BiomeModifications.addSpawn(BiomeSelectors.foundInTheNether(),
                SpawnGroup.MONSTER, ModEntities.ORC_CHAMPION, 1, 1, 1);
        SpawnRestriction.register(ModEntities.ORC_CHAMPION, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, OrcChampionEntity::canMobSpawnWithRate);

        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld(),
                SpawnGroup.CREATURE, ModEntities.TROLL, 15, 1, 2);
        SpawnRestriction.register(ModEntities.TROLL, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, random) -> {
                    if (world.getDifficulty() == Difficulty.PEACEFUL) {
                        return false; // Don't spawn in Peaceful difficulty
                    }

                    return TrollEntity.canMobSpawnWithRate(type, world, reason, pos, random);
                });

        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld(),
                SpawnGroup.CREATURE, ModEntities.MINOTAUR, 5, 1, 1);
        SpawnRestriction.register(ModEntities.MINOTAUR, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, spawnReason, pos, random) -> {
            if(world.getDifficulty() == Difficulty.PEACEFUL) {
                return false;
            }
            return MinotaurEntity.canMobSpawnWithRate(type, world, spawnReason, pos, random);
        });
    }
}
