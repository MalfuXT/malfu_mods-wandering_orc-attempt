package malfu.wandering_orc.entity;

import malfu.wandering_orc.WanderingOrc;
import malfu.wandering_orc.entity.custom.OrcArcherEntity;
import malfu.wandering_orc.entity.custom.OrcWarriorEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<OrcArcherEntity> ORC_ARCHER = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(WanderingOrc.MOD_ID, "orc_archer"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, OrcArcherEntity::new)
                    .dimensions(EntityDimensions.fixed(0.7f, 2.7f)).build());

    public static final EntityType<OrcWarriorEntity> ORC_WARRIOR = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(WanderingOrc.MOD_ID, "orc_warrior"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, OrcWarriorEntity::new)
                    .dimensions(EntityDimensions.fixed(0.7f, 2.7f)).build());
}
