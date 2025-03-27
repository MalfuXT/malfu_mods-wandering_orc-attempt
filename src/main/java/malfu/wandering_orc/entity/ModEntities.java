package malfu.wandering_orc.entity;

import malfu.wandering_orc.WanderingOrc;
import malfu.wandering_orc.entity.block_entity.BlockScanEntity;
import malfu.wandering_orc.entity.custom.*;
import malfu.wandering_orc.entity.projectiles.FireProjectileEntity;
import malfu.wandering_orc.entity.projectiles.MagicProjectileEntity;
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
                    .dimensions(EntityDimensions.fixed(0.75f, 2.2f)).build());

    public static final EntityType<OrcWarriorEntity> ORC_WARRIOR = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(WanderingOrc.MOD_ID, "orc_warrior"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, OrcWarriorEntity::new)
                    .dimensions(EntityDimensions.fixed(0.85f, 2.2f)).build());

    public static final EntityType<OrcWarlockEntity> ORC_WARLOCK = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(WanderingOrc.MOD_ID, "orc_warlock"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, OrcWarlockEntity::new)
                    .dimensions(EntityDimensions.fixed(0.75f, 2.10f)).build());

    public static final EntityType<MinotaurEntity> MINOTAUR = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(WanderingOrc.MOD_ID, "minotaur"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, MinotaurEntity::new)
                    .dimensions(EntityDimensions.fixed(1.2f, 2.65f)).build());

    public static final EntityType<TrollEntity> TROLL = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(WanderingOrc.MOD_ID, "troll"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, TrollEntity::new)
                    .dimensions(EntityDimensions.fixed(0.7f, 1.95f)).build());

    public static final EntityType<TrollDoctorEntity> TROLL_DOCTOR = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(WanderingOrc.MOD_ID, "troll_doctor"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, TrollDoctorEntity::new)
                    .dimensions(EntityDimensions.fixed(0.7f, 1.95f)).build());

    public static final EntityType<PoentEntity> POENT = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(WanderingOrc.MOD_ID, "poent"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, PoentEntity::new)
                    .dimensions(EntityDimensions.fixed(0.7f, 1.65f)).build());

    public static final EntityType<FirelinkEntity> FIRELINK = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(WanderingOrc.MOD_ID, "firelink"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, FirelinkEntity::new)
                    .dimensions(EntityDimensions.fixed(0.7f, 1.20f)).build());

    public static final EntityType<OrcChampionEntity> ORC_CHAMPION = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(WanderingOrc.MOD_ID, "orc_champion"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, OrcChampionEntity::new)
                    .dimensions(EntityDimensions.fixed(0.85f, 2.2f)).build());

    //PROJECTILES ENTITY
    public static final EntityType<TrollThrowableEntity> TROLL_THROWABLE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(WanderingOrc.MOD_ID, "troll_throwable"),
            FabricEntityTypeBuilder.<TrollThrowableEntity>create(SpawnGroup.MISC, TrollThrowableEntity::new)
                    .dimensions(EntityDimensions.fixed(0.3f, 0.3f)).build());

    public static final EntityType<FireProjectileEntity> FIRE_PROJECTILE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(WanderingOrc.MOD_ID, "fire_projectile"),
            FabricEntityTypeBuilder.<FireProjectileEntity>create(SpawnGroup.MISC, FireProjectileEntity::new)
                    .dimensions(EntityDimensions.fixed(0.3f, 0.3f)).build());

    public static final EntityType<MagicProjectileEntity> MAGIC_PROJECTILE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(WanderingOrc.MOD_ID, "magic_projectile"),
            FabricEntityTypeBuilder.<MagicProjectileEntity>create(SpawnGroup.MISC, MagicProjectileEntity::new)
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
