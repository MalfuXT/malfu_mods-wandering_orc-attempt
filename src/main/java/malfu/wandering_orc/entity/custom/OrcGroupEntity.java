package malfu.wandering_orc.entity.custom;


import malfu.wandering_orc.util.ModTags;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PatrolEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import net.minecraft.world.dimension.DimensionType;

import java.util.function.Predicate;

public class OrcGroupEntity extends HostileEntity {
    protected OrcGroupEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    public static final Predicate<LivingEntity> TARGET_ORC_ENEMIES = (livingEntity) -> {
        if (!(livingEntity instanceof MobEntity)) {  // Check if it's a MobEntity
            return false;
        }

        World world = livingEntity.getWorld();
        if (!(world instanceof ServerWorld serverWorld)) { // Check if it's a ServerWorld
            return false;
        }

        return livingEntity.getType().isIn(ModTags.EntityEnemies.ORC_ENEMIES) && livingEntity.isAlive();
    };

    public static boolean canSpawn(EntityType<? extends OrcGroupEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        return world.getLightLevel(LightType.BLOCK, pos) <= 8 && canSpawnIgnoreLightLevel(type, world, spawnReason, pos, random);
    }
}
