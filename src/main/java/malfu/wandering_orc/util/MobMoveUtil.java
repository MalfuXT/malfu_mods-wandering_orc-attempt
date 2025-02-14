package malfu.wandering_orc.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class MobMoveUtil {
    private static final int CIRCLE_DELAY = 100; // Delay in ticks (1 second = 20 ticks)

    public static void circleTarget(LivingEntity mob, LivingEntity target, double distance, double speedMultiplier) {
        if (mob instanceof MobEntity) {
            MobEntity mobEntity = (MobEntity) mob;

            if (mobEntity.getWorld().getTime() % CIRCLE_DELAY == 0) { // Check if it's time to change direction
                // Calculate the vector from the mob to the target
                Vec3d directionToTarget = new Vec3d(target.getX() - mob.getX(), 0, target.getZ() - mob.getZ());

                // Determine clockwise or counter-clockwise circling randomly
                boolean clockwise = mobEntity.getRandom().nextBoolean();

                // Calculate perpendicular vector
                Vec3d perpendicular = clockwise ?
                        new Vec3d(-directionToTarget.z, 0, directionToTarget.x) :
                        new Vec3d(directionToTarget.z, 0, -directionToTarget.x);

                // Calculate flee position by offsetting the target position perpendicularly
                BlockPos fleePos = target.getBlockPos().add((int)perpendicular.x * (int)distance, 0, (int)perpendicular.z * (int)distance);

                // Find a path to the flee position
                Path path = mobEntity.getNavigation().findPathTo(fleePos, 0);

                // If a path is found, start moving along the path
                if (path != null) {
                    mobEntity.getNavigation().startMovingAlong(path, speedMultiplier);
                }
            }
        }
    }
}
