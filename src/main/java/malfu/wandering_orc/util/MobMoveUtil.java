package malfu.wandering_orc.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class MobMoveUtil {
    private static final int CIRCLE_DELAY = 200; // Delay in ticks (1 second = 20 ticks)

    public static void maintainRangeWhileCircling(LivingEntity mob, LivingEntity target, double distance, double speedMultiplier) {
        if (mob instanceof MobEntity) {
            MobEntity mobEntity = (MobEntity) mob;

            if (mobEntity.getWorld().getTime() % CIRCLE_DELAY == 0) { // Check if it's time to change direction
                // Calculate the vector from the mob to the target
                Vec3d directionToTarget = new Vec3d(target.getX() - mob.getX(), 0, target.getZ() - mob.getZ());

                // Normalize the direction vector
                double length = Math.sqrt(directionToTarget.x * directionToTarget.x + directionToTarget.z * directionToTarget.z);
                Vec3d normalizedDirection = new Vec3d(directionToTarget.x / length, 0, directionToTarget.z / length);

                // Determine clockwise or counter-clockwise circling randomly
                boolean clockwise = mobEntity.getRandom().nextBoolean();

                // Calculate perpendicular vector
                Vec3d perpendicular = clockwise ?
                        new Vec3d(-normalizedDirection.z, 0, normalizedDirection.x) :
                        new Vec3d(normalizedDirection.z, 0, -normalizedDirection.x);

                // Calculate the desired position by offsetting the target position perpendicularly and maintaining the range
                Vec3d desiredPosition = new Vec3d(
                        target.getX() + perpendicular.x * distance,
                        mob.getY(),
                        target.getZ() + perpendicular.z * distance
                );

                // Find a path to the desired position
                Path path = mobEntity.getNavigation().findPathTo(
                        new BlockPos((int) desiredPosition.x, (int) desiredPosition.y, (int) desiredPosition.z), 0);

                // If a path is found, start moving along the path
                if (path != null) {
                    mobEntity.getNavigation().startMovingAlong(path, speedMultiplier);
                }
            }
        }
    }

    public static void moveAwayFromTarget(LivingEntity mob, LivingEntity target, double distance, double speedMultiplier) {
        if (mob instanceof MobEntity) {
            MobEntity mobEntity = (MobEntity) mob;

            // Calculate the vector from the mob to the target
            Vec3d directionToTarget = new Vec3d(target.getX() - mob.getX(), 0, target.getZ() - mob.getZ());

            // Normalize the direction vector
            double length = Math.sqrt(directionToTarget.x * directionToTarget.x + directionToTarget.z * directionToTarget.z);
            Vec3d normalizedDirection = new Vec3d(directionToTarget.x / length, 0, directionToTarget.z / length);

            // Calculate the desired position by moving away from the target
            Vec3d desiredPosition = new Vec3d(
                    mob.getX() - normalizedDirection.x * distance,
                    mob.getY(),
                    mob.getZ() - normalizedDirection.z * distance
            );

            // Find a path to the desired position
            Path path = mobEntity.getNavigation().findPathTo(
                    new BlockPos((int) desiredPosition.x, (int) desiredPosition.y, (int) desiredPosition.z), 0);

            // If a path is found, start moving along the path
            if (path != null) {
                mobEntity.getNavigation().startMovingAlong(path, speedMultiplier);
            }
        }
    }

    public static void veloForward(LivingEntity mob, double distance) {
        Vec3d forwardDirection;

        forwardDirection = mob.getRotationVector().add(0, 0, 0); // Dodge in the direction the mob is facing
        double forwardDistance = distance;

        Vec3d forwardVelocity = forwardDirection.normalize().multiply(forwardDistance);

        mob.setVelocity(forwardVelocity);
    }
}
