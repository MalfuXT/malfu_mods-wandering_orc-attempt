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
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;

public class AreaDamage {
    public static void dealAreaDamage(LivingEntity source, double radius, Class<? extends Entity> excludedEntityClass) {
        Vec3d sourcePos = source.getPos();
        Box areaOfEffect = Box.from(sourcePos).expand(radius);

        // Use a Predicate to filter out the source entity itself
        List<Entity> entities = source.getWorld().getEntitiesByClass(Entity.class, areaOfEffect,
                entity -> entity != source && !excludedEntityClass.isInstance(entity));

        float sourceDamage = (float)source.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);

        for (Entity entity : entities) {
            if (entity instanceof LivingEntity) {
                ((LivingEntity) entity).damage(source.getDamageSources().mobAttack(source), sourceDamage);
            }
        }
    }

    //You can set it with StatusEffect."Some Effect Here" or your custom effect
    public static void dealAreaDamageWithEffect(LivingEntity source, double radius, Class<? extends Entity> excludedEntityClass,
                                      StatusEffect statusEffect, int duration, int amplifier) {
        Vec3d sourcePos = source.getPos();
        Box areaOfEffect = Box.from(sourcePos).expand(radius);

        List<Entity> entities = source.getWorld().getEntitiesByClass(Entity.class, areaOfEffect,
                entity -> entity != source && !excludedEntityClass.isInstance(entity));

        float sourceDamage = (float) source.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);


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

    public static void AreaCrackedGround(LivingEntity mob, World world, BlockPos centerPos, int radius) {
        // Start scanning from the block directly under the mob
        BlockPos startPos = centerPos.down();

        // Calculate the forward direction based on the mob's rotation
        float yawRadians = mob.getYaw() * (float) (Math.PI / 180.0);
        int forwardOffsetX = (int) (Math.sin(-yawRadians) * radius); // Offset in front of the mob
        int forwardOffsetZ = (int) (Math.cos(yawRadians) * radius); // Offset in front of the mob
        BlockPos offsetPos = startPos.add(forwardOffsetX, -1, forwardOffsetZ);

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                BlockPos targetPos = offsetPos.add(x, 0, z);

                // Check if the block is within the circular radius
                if (offsetPos.getSquaredDistance(targetPos) <= radius * radius) {
                    // Get the block state at the target position
                    BlockState blockState = world.getBlockState(targetPos);

                    // Spawn the entity with a random Y offset
                    BlockPos spawnPos = targetPos.up();
                    spawnBlockScanEntity(world, spawnPos, blockState, mob.getRandom());
                }
            }
        }
    }

    public static void spawnBlockScanEntity(World world, BlockPos pos, BlockState blockState, Random random) {
        BlockScanEntity blockScanEntity = new BlockScanEntity(ModEntities.BLOCK_SCAN_ENTITY, world);

        // Add a random Y offset between +0.0 and +0.2
        float randomYOffset = random.nextFloat() * 0.2f + 0.2f; // Random Y offset between 0.0 and 0.2
        blockScanEntity.setPosition(pos.getX(), pos.getY() + randomYOffset, pos.getZ()); // Center the entity on the block

        // Set the block state
        blockScanEntity.setBlockState(blockState);

        // Apply random rotation (up to 20 degrees on X and Z axes)
        float randomPitch = random.nextFloat() * 40 - 20; // Random pitch between -20 and 20 degrees
        float randomYaw = random.nextFloat() * 40 - 20; // Random yaw between -20 and 20 degrees
        blockScanEntity.setPitch(randomPitch);
        blockScanEntity.setYaw(randomYaw);

        // Spawn the entity
        world.spawnEntity(blockScanEntity);
    }
}
