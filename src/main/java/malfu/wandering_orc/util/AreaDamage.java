package malfu.wandering_orc.util;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Random;

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

    //initially to give random block rotation like broken ground effect just like in epic fight but i don't figure it out yet.
    // it does rotate but only the HORIZONTAL_FACING block like stair only to like reset the rotation.
    public static void rotateBlocksBelow(LivingEntity mob) {
        BlockPos mobPos = mob.getBlockPos();

        for (int xOffset = -1; xOffset <= 1; xOffset++) {
            for (int zOffset = -1; zOffset <= 1; zOffset++) {
                BlockPos pos = new BlockPos(mobPos.getX() + xOffset, mobPos.getY() - 1, mobPos.getZ() + zOffset);
                BlockState state = mob.getWorld().getBlockState(pos);

                if (state.contains(Properties.HORIZONTAL_FACING)) {
                    Direction currentFacing = state.get(Properties.HORIZONTAL_FACING);
                    Direction newFacing = rotateDirectionRandomly(currentFacing, 35);
                    mob.getWorld().setBlockState(pos, state.with(Properties.HORIZONTAL_FACING, newFacing));
                }
            }
        }
    }

    private static Direction rotateDirectionRandomly(Direction currentFacing, int maxDegrees) {
        Random random = new Random();
        int rotation = random.nextInt(maxDegrees * 2 + 1) - maxDegrees;
        return Direction.fromRotation(currentFacing.getHorizontal() + rotation);
    }
}
