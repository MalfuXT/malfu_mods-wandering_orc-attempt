package malfu.wandering_orc.world.gen;

import malfu.wandering_orc.entity.ModEntities;
import malfu.wandering_orc.entity.custom.*;
import net.fabricmc.fabric.api.biome.v1.BiomeModification;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.Difficulty;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.BiomeKeys;


public class ModEntityGeneration {

    public static void addSpawns() {
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(BiomeKeys.PLAINS),
                SpawnGroup.CREATURE, ModEntities.ORC_ARCHER, 100, 2, 3);
        SpawnRestriction.register(ModEntities.ORC_ARCHER, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, OrcArcherEntity::canSpawn);

        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(BiomeKeys.PLAINS),
                SpawnGroup.CREATURE, ModEntities.ORC_WARRIOR, 100, 1, 2);
        SpawnRestriction.register(ModEntities.ORC_WARRIOR, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, OrcWarriorEntity::canSpawn);

        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld().or(BiomeSelectors.foundInTheNether()),
                SpawnGroup.CREATURE, ModEntities.ORC_CHAMPION, 1000, 1, 1);

        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld().or(BiomeSelectors.foundInTheNether()),
                SpawnGroup.MONSTER, ModEntities.TROLL, 100, 1, 3);
        SpawnRestriction.register(ModEntities.TROLL, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, TrollEntity::canMobSpawn);

        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(BiomeKeys.SAVANNA, BiomeKeys.PLAINS, BiomeKeys.BADLANDS, BiomeKeys.BEACH, BiomeKeys.JUNGLE),
                SpawnGroup.CREATURE, ModEntities.MINOTAUR, 100, 1, 1);
        SpawnRestriction.register(ModEntities.MINOTAUR, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, spawnReason, pos, random) -> {
            if(world.getDifficulty() == Difficulty.PEACEFUL) {
                return false;
            }
            return MinotaurEntity.canMobSpawn(type, world, spawnReason, pos, random);
        });
    }
}
