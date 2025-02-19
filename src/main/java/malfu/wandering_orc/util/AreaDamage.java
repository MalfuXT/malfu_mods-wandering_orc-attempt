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
}
