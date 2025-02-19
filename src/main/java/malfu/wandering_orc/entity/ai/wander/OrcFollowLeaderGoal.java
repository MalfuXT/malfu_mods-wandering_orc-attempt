package malfu.wandering_orc.entity.ai.wander;

import malfu.wandering_orc.entity.custom.MinotaurEntity;
import malfu.wandering_orc.entity.custom.OrcChampionEntity;
import malfu.wandering_orc.entity.custom.OrcGroupEntity;
import malfu.wandering_orc.entity.custom.OrcWarriorEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class OrcFollowLeaderGoal extends Goal {
    private final MobEntity mob;
    private int timer;

    public OrcFollowLeaderGoal(MobEntity mob) {
        this.mob = mob;
    }

    private void CountdownToFollow() {
        if (this.timer > 0) {
            this.timer--;
        } else {
            Random random = new Random();
            this.timer = 60 + random.nextInt(61);
        }
    }

    @Override
    public boolean canStart() {
        LivingEntity leader = findNearestLeader(mob);
        return leader != null && mob.distanceTo(leader) > 15.0f;
    }

    @Override
    public void tick() {
        LivingEntity leader = findNearestLeader(mob);
        this.CountdownToFollow();
        if (leader != null && this.timer < 1) {
            if(!(this.mob.distanceTo(leader) < 4)) {
                mob.getNavigation().startMovingTo(leader.getX(), leader.getY(), leader.getZ(), 1.0);
            }

        }
    }

    private LivingEntity findNearestLeader(MobEntity follower) {
        // Priority order: OrcChampion, OrcWarrior, Minotaur
        Class<?>[] leaderClasses = {OrcChampionEntity.class, OrcWarriorEntity.class, MinotaurEntity.class};

        // Track the nearest leader and its priority
        LivingEntity nearestLeader = null;
        int nearestLeaderPriority = Integer.MAX_VALUE; // Start with the lowest priority

        for (int i = 0; i < leaderClasses.length; i++) {
            Class<?> leaderClass = leaderClasses[i];
            int currentPriority = i; // Lower index means higher priority

            // Skip if the current priority is lower than the nearest leader's priority
            if (currentPriority > nearestLeaderPriority) {
                continue;
            }

            // Search for potential leaders of the current priority class
            List<LivingEntity> potentialLeaders = follower.getWorld().getEntitiesByClass(
                    (Class<LivingEntity>) leaderClass,
                    follower.getBoundingBox().expand(42), // Search within 32 blocks
                    entity -> isValidLeader(entity, follower, currentPriority)); // Check if the entity is a valid leader

            if (!potentialLeaders.isEmpty()) {
                // Find the closest leader among the potential leaders
                LivingEntity closestLeader = potentialLeaders.stream()
                        .min(Comparator.comparingDouble(follower::squaredDistanceTo))
                        .orElse(null);

                // If a closer leader is found, update the nearest leader and its priority
                if (closestLeader != null) {
                    nearestLeader = closestLeader;
                    nearestLeaderPriority = currentPriority;
                }
            }
        }

        // If a leader is found, ensure it does not follow leaders or non-leaders of the same class
        if (nearestLeader != null && nearestLeader instanceof OrcGroupEntity) {
            ensureSingleLeaderInArea((OrcGroupEntity) nearestLeader, follower.getWorld(), 42);
            ensureLeaderDoesNotFollowSameClass((OrcGroupEntity) nearestLeader);
        }

        // Return the nearest leader (if any)
        return nearestLeader;
    }

    private boolean isValidLeader(LivingEntity entity, MobEntity follower, int currentPriority) {
        // Exclude the follower itself
        if (entity == follower) {
            return false;
        }

        // If the entity is a leader, ensure it does not follow leaders or non-leaders of the same class
        if (entity instanceof OrcGroupEntity) {
            OrcGroupEntity orcEntity = (OrcGroupEntity) entity;
            if (orcEntity.getLeader() != null) {
                int leaderPriority = getPriorityForClass(orcEntity.getLeader().getClass());
                if (leaderPriority >= currentPriority) {
                    return false; // Leader is following someone of the same or lower priority
                }
            }
        }

        return true;
    }

    private void ensureSingleLeaderInArea(OrcGroupEntity leader, World world, double radius) {
        // Safely cast the leader's class to Class<LivingEntity>
        Class<LivingEntity> leaderClass = (Class<LivingEntity>) leader.getClass().asSubclass(LivingEntity.class);

        // Find all leaders of the same class within the radius
        List<LivingEntity> sameClassLeaders = world.getEntitiesByClass(
                leaderClass,
                leader.getBoundingBox().expand(radius),
                entity -> entity != leader && entity instanceof OrcGroupEntity);

        // Ensure only one leader exists in the area
        for (LivingEntity sameClassLeader : sameClassLeaders) {
            if (sameClassLeader instanceof OrcGroupEntity) {
                OrcGroupEntity otherLeader = (OrcGroupEntity) sameClassLeader;
                if (otherLeader.getLeader() == null) {
                    otherLeader.setLeader(leader); // Make the other leader follow the nearest leader
                }
            }
        }
    }

    private void ensureLeaderDoesNotFollowSameClass(OrcGroupEntity leader) {
        // Ensure the leader does not follow any entity of the same class
        LivingEntity currentLeader = leader.getLeader();
        if (currentLeader != null && currentLeader.getClass() == leader.getClass()) {
            leader.setLeader(null); // Stop following
        }
    }

    // Helper method to get the priority of a leader class
    private int getPriorityForClass(Class<?> leaderClass) {
        if (leaderClass == OrcChampionEntity.class) {
            return 0; // Highest priority
        } else if (leaderClass == OrcWarriorEntity.class) {
            return 1;
        } else if (leaderClass == MinotaurEntity.class) {
            return 2; // Lowest priority
        }
        return Integer.MAX_VALUE; // Default for unknown classes
    }
}
