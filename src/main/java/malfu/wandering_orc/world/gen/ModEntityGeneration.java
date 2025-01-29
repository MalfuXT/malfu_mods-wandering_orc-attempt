package malfu.wandering_orc.world.gen;

import malfu.wandering_orc.entity.ModEntities;
import malfu.wandering_orc.entity.custom.OrcArcherEntity;
import malfu.wandering_orc.entity.custom.OrcWarriorEntity;
import net.fabricmc.fabric.api.biome.v1.BiomeModification;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.BiomeKeys;


public class ModEntityGeneration {

    public static void addSpawns() {
        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld().or(BiomeSelectors.foundInTheNether()),
                SpawnGroup.CREATURE, ModEntities.ORC_ARCHER, 20, 2, 3);
        SpawnRestriction.register(ModEntities.ORC_ARCHER, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, OrcArcherEntity::canSpawn);

        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld().or(BiomeSelectors.foundInTheNether()),
                SpawnGroup.CREATURE, ModEntities.ORC_WARRIOR, 15, 1, 2);
        SpawnRestriction.register(ModEntities.ORC_WARRIOR, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, OrcWarriorEntity::canSpawn);
    }
}
