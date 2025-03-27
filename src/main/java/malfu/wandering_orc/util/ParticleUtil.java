package malfu.wandering_orc.util;

import malfu.wandering_orc.particle.ModParticles;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class ParticleUtil {

    //this one set effect towards target from mob.
    public static void spawnHealingBeam(LivingEntity mob, LivingEntity target) {
        if (mob.getWorld().isClient()) {
            return;
        }

        if (!mob.getWorld().isClient) {
            double mobX = mob.getX();
            double mobY = mob.getEyeY();
            double mobZ = mob.getZ();

            double targetX = target.getX();
            double targetY = target.getEyeY();
            double targetZ = target.getZ();

            // Spawn particles along the line between the mob and the target
            for (int i = 0; i < 10; i++) {
                double progress = i / 10.0;
                double particleX = mobX + (targetX - mobX) * progress;
                double particleY = mobY + (targetY - mobY) * progress;
                double particleZ = mobZ + (targetZ - mobZ) * progress;

                ((ServerWorld) mob.getWorld()).spawnParticles(ParticleTypes.HEART, particleX, particleY, particleZ, 3, 0.1, 0.1, 0.1, 0.01);
            }
        }
    }

    public static void generateHealingParticle(LivingEntity target) {
        Vec3d sourcePos = target.getPos();
        if (target.getWorld().isClient()) {
            return;
        }
        ((ServerWorld) target.getWorld()).spawnParticles(ModParticles.HEAL_CIRCLE, sourcePos.x, sourcePos.y, sourcePos.z, 1, 0, 0, 0, 0);
        ((ServerWorld) target.getWorld()).spawnParticles(ModParticles.HEAL_LINES, sourcePos.x, sourcePos.y+1, sourcePos.z, 20, 0.4, 0.5, 0.4, 0);
    }

    public static void generateAreaHealParticle(LivingEntity target) { //ABOUT THIS, CAN'T CHANGE SIZE FROM HERE, MUST FROM THE PARTICLE CLASS ITSELF
        Vec3d sourcePos = target.getPos();
        if (target.getWorld().isClient()) {
            return;
        }
        ((ServerWorld) target.getWorld()).spawnParticles(ModParticles.AREA_HEAL, sourcePos.x, sourcePos.y, sourcePos.z, 1, 0, 0, 0, 0);
    }

    public static void generateSummonParticle(LivingEntity target) {
        Vec3d sourcePos = target.getPos();
        if (target.getWorld().isClient()) {
            return;
        }
        ((ServerWorld) target.getWorld()).spawnParticles(ModParticles.SUMMON_HOLE, sourcePos.x, sourcePos.y, sourcePos.z, 1, 0, 0, 0, 0);
    }

    public static void generateFireParticle(Entity entity) {
        if (entity.getWorld().isClient()) {
            return;
        }
        ((ServerWorld) entity.getWorld()).spawnParticles(ModParticles.FIRE_EXPLODE, entity.getX(), entity.getY(), entity.getZ(), 1, 0, 0, 0, 0);
    }

    public static void generateMagicParticle(Entity entity) {
        if (entity.getWorld().isClient()) {
            return;
        }
        ((ServerWorld) entity.getWorld()).spawnParticles(ModParticles.MAGIC_EXPLODE, entity.getX(), entity.getY(), entity.getZ(), 1, 0, 0, 0, 0);
    }

    public static void generateBubbleParticle(Entity entity) {
        if (entity.getWorld().isClient()) {
            return;
        }
        ((ServerWorld) entity.getWorld()).spawnParticles(ParticleTypes.BUBBLE_POP, entity.getX(), entity.getY(), entity.getZ(), 4, 0.1, 0.1, 0.1, 0);
    }

    public static void spawnBlockParticles(World world, Entity entity, Random random) {
        double posX = entity.getX();
        double posY = entity.getY()+0.8;
        double posZ = entity.getZ();

        BlockPos blockPos = BlockPos.ofFloored(posX, entity.getY() - 1, posZ);
        BlockState blockState = world.getBlockState(blockPos);

        if (!blockState.isAir()) {
            for (int i = 0; i < 10; i++) {
                double offsetX = (random.nextDouble() - 1) * 1;
                double offsetZ = (random.nextDouble() - 1) * 1;
                double velocityY = 5;

                world.addParticle(
                        new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState),
                        posX + offsetX, posY, posZ + offsetZ,
                        0.0, velocityY, 0.0
                );
            }
        }
    }
}
