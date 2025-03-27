package malfu.wandering_orc.util;

import com.eliotlash.mclib.math.functions.classic.Mod;
import malfu.wandering_orc.entity.ModEntities;
import malfu.wandering_orc.entity.block_entity.BlockScanEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class AreaDamage {
    public static void dealAreaDamage(LivingEntity source, double radius, Class<? extends Entity> excludedEntityClass, float bonusDamage,
                                      Vec3d relativeOffset) {
        // Get the mob's forward direction vector (normalized)
        Vec3d forward = source.getRotationVec(1.0F).normalize();

        // Calculate right vector (perpendicular to forward and up)
        Vec3d up = new Vec3d(0, 0, 0);
        Vec3d right = forward.crossProduct(up).normalize();

        // Calculate the offset position in world space
        Vec3d offsetPosition = source.getPos()
                .add(forward.multiply(relativeOffset.z))  // Forward/back (z-axis in local space)
                .add(right.multiply(relativeOffset.x))    // Left/right (x-axis in local space)
                .add(up.multiply(relativeOffset.y));      // Up/down (y-axis in local space)

        Box areaOfEffect = Box.from(offsetPosition).expand(radius);

        // Use a Predicate to filter out the source entity itself
        List<Entity> entities = source.getWorld().getEntitiesByClass(Entity.class, areaOfEffect,
                entity -> entity != source && !excludedEntityClass.isInstance(entity));

        float sourceDamage = (float) source.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) + bonusDamage;

        if (sourceDamage <= 0) {
            sourceDamage = 0;
        }

        for (Entity entity : entities) {
            if (entity instanceof LivingEntity) {
                ((LivingEntity) entity).damage(source.getDamageSources().mobAttack(source), sourceDamage);
            }
        }
    }


    //You can set it with StatusEffect."Some Effect Here" or your custom effect
    public static void dealAreaDamageWithEffect(LivingEntity source, double radius, Class<? extends Entity> excludedEntityClass,
                                      StatusEffect statusEffect, int duration, int amplifier, float BonusDamage, Vec3d relativeOffset) {
        // Get the mob's forward direction vector (normalized)
        Vec3d forward = source.getRotationVec(1.0F).normalize();

        // Calculate right vector (perpendicular to forward and up)
        Vec3d up = new Vec3d(0, 0, 0);
        Vec3d right = forward.crossProduct(up).normalize();

        // Calculate the offset position in world space
        Vec3d offsetPosition = source.getPos()
                .add(forward.multiply(relativeOffset.z))  // Forward/back (z-axis in local space)
                .add(right.multiply(relativeOffset.x))    // Left/right (x-axis in local space)
                .add(up.multiply(relativeOffset.y));      // Up/down (y-axis in local space)

        Box areaOfEffect = Box.from(offsetPosition).expand(radius);

        List<Entity> entities = source.getWorld().getEntitiesByClass(Entity.class, areaOfEffect,
                entity -> entity != source && !excludedEntityClass.isInstance(entity));

        float sourceDamage = (float) source.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) + BonusDamage;

        if(sourceDamage <= 0) {
            sourceDamage = 0;
        }

        for (Entity entity : entities) {
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;
                livingEntity.damage(source.getDamageSources().mobAttack(source), sourceDamage);

                if (statusEffect != null) {
                    livingEntity.addStatusEffect(new StatusEffectInstance(statusEffect, duration, amplifier));
                }
            }
        }
    }

    public static List<LivingEntity> dealAreaDamageReadHitTarget
            (LivingEntity source, double radius, Class<? extends Entity> excludedEntityClass, float bonusDamage, Vec3d relativeOffset) {

        // Direction calculation (fixed up vector)
        Vec3d forward = source.getRotationVec(1.0F).normalize();
        Vec3d up = new Vec3d(0, 1, 0);
        Vec3d right = forward.crossProduct(up).normalize();

        // Position calculation
        Vec3d offsetPosition = source.getPos()
                .add(forward.multiply(relativeOffset.z))
                .add(right.multiply(relativeOffset.x))
                .add(up.multiply(relativeOffset.y));

        Box areaOfEffect = Box.from(offsetPosition).expand(radius);

        // Get entities and filter
        List<Entity> entities = source.getWorld().getEntitiesByClass(Entity.class, areaOfEffect,
                entity -> entity != source && !excludedEntityClass.isInstance(entity));

        float sourceDamage = Math.max(
                (float) source.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) + bonusDamage,
                0
        );

        // List to store hit entities
        List<LivingEntity> hitEntities = new ArrayList<>();

        for (Entity entity : entities) {
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.damage(source.getDamageSources().mobAttack(source), sourceDamage);
                hitEntities.add(livingEntity);
            }
        }

        return hitEntities;
    }

    public static void AreaCrackedGround(LivingEntity mob, World world, BlockPos centerPos, int radius, Vec3d relativeOffset) {
        // Start from ground level below the provided center position
        BlockPos startPos = centerPos.down();

        // Calculate directional offsets based on mob's rotation
        float yawRadians = mob.getYaw() * (float) (Math.PI / 180.0);

        // Forward/backward offset (using relativeOffset.z)
        int forwardOffsetX = (int) (Math.sin(-yawRadians) * relativeOffset.z);
        int forwardOffsetZ = (int) (Math.cos(yawRadians) * relativeOffset.z);

        // Left/right offset (using relativeOffset.x)
        int rightOffsetX = (int) (Math.cos(yawRadians) * relativeOffset.x);
        int rightOffsetZ = (int) (Math.sin(yawRadians) * relativeOffset.x);

        // Apply both forward and right offsets
        BlockPos offsetPos = startPos.add(
                forwardOffsetX + rightOffsetX,
                -1,  // Keep Y position unchanged (ground level)
                forwardOffsetZ + rightOffsetZ
        );

        // Scan blocks in radius
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                BlockPos targetPos = offsetPos.add(x, 0, z);

                // Circular area check
                if (offsetPos.getSquaredDistance(targetPos) <= radius * radius) {
                    BlockState blockState = world.getBlockState(targetPos);


                    // Spawn crack effect
                    spawnBlockScanEntity(world, targetPos.up(), blockState, mob.getRandom());

                    SoundUtil.CrackedGround(mob, 1.0f, 1.4f);
                }
            }
        }
    }

    public static void spawnBlockScanEntity(World world, BlockPos pos, BlockState blockState, Random random) {
        BlockScanEntity blockScanEntity = new BlockScanEntity(ModEntities.BLOCK_SCAN_ENTITY, world);

        // Position the entity with random Y offset
        float randomYOffset = random.nextFloat() * 0.2f + 0.15f;
        blockScanEntity.setPosition(pos.getX(), pos.getY() + randomYOffset, pos.getZ());

        // Set the block state
        blockScanEntity.setBlockState(blockState);

        // Random rotation
        float randomPitch = random.nextFloat() * 24 - 12;
        float randomYaw = random.nextFloat() * 24 - 12;
        blockScanEntity.setPitch(randomPitch);
        blockScanEntity.setYaw(randomYaw);

        // Spawn the entity
        world.spawnEntity(blockScanEntity);
    }
}
