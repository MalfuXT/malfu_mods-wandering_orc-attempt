package malfu.wandering_orc.entity;

import malfu.wandering_orc.WanderingOrc;
import malfu.wandering_orc.entity.block_entity.BlockScanEntity;
import malfu.wandering_orc.entity.custom.*;
import malfu.wandering_orc.entity.projectiles.TrollThrowableEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    //CUSTOM ENTITY
    public static final EntityType<OrcArcherEntity> ORC_ARCHER = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(WanderingOrc.MOD_ID, "orc_archer"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, OrcArcherEntity::new)
                    .dimensions(EntityDimensions.fixed(0.75f, 2.3f)).build());

    public static final EntityType<OrcWarriorEntity> ORC_WARRIOR = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(WanderingOrc.MOD_ID, "orc_warrior"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, OrcWarriorEntity::new)
                    .dimensions(EntityDimensions.fixed(0.85f, 2.3f)).build());

    public static final EntityType<MinotaurEntity> MINOTAUR = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(WanderingOrc.MOD_ID, "minotaur"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, MinotaurEntity::new)
                    .dimensions(EntityDimensions.fixed(1.2f, 2.7f)).build());

    public static final EntityType<TrollEntity> TROLL = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(WanderingOrc.MOD_ID, "troll"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, TrollEntity::new)
                    .dimensions(EntityDimensions.fixed(0.7f, 1.95f)).build());

    public static final EntityType<OrcChampionEntity> ORC_CHAMPION = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(WanderingOrc.MOD_ID, "orc_champion"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, OrcChampionEntity::new)
                    .dimensions(EntityDimensions.fixed(0.85f, 2.3f)).build());

    //PROJECTILES ENTITY
    public static final EntityType<TrollThrowableEntity> TROLL_THROWABLE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(WanderingOrc.MOD_ID, "troll_throwable"),
            FabricEntityTypeBuilder.<TrollThrowableEntity>create(SpawnGroup.MISC, TrollThrowableEntity::new)
                    .dimensions(EntityDimensions.fixed(0.3f, 0.3f)).build());

    //BLOCK ENTITY extends ENTITY
    public static final EntityType<BlockScanEntity> BLOCK_SCAN_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(WanderingOrc.MOD_ID, "block_scan_entity"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, BlockScanEntity::new)
                    .dimensions(EntityDimensions.fixed(1.0F, 1.0F)) // Set size to 1x1x1
                    .build()
    );
}
