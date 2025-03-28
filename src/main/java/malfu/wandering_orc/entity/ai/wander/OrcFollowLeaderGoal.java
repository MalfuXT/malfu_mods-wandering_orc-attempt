package malfu.wandering_orc.entity.ai.wander;

import malfu.wandering_orc.entity.custom.*;
import malfu.wandering_orc.util.custom_structure_util.CustomStructureKeys;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.world.World;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.structure.Structure;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class OrcFollowLeaderGoal extends Goal {
    private final OrcGroupEntity orc;
    private final ServerWorld world;
    private final float speed = 1.2f;
    private int timer;

    public OrcFollowLeaderGoal(OrcGroupEntity orc) {
        this.orc = orc;
        this.world = (ServerWorld) orc.getWorld();
    }

    private void CountdownToFollow() {
        if (this.timer > 0) {
            this.timer--;
        } else {
            Random random = new Random();
            this.timer = 40 + random.nextInt(81);
        }
    }

    @Override
    public boolean canStart() {
        LivingEntity leader = findNearestLeader(orc);
        return leader != null && orc.distanceTo(leader) > 20.0f && !orc.isAttacking() && !isInsideCamp();
    }

    @Override
    public void start() {
        this.orc.setChase(true);
    }

    @Override
    public void stop() {
        this.orc.setChase(false);
    }

    @Override
    public void tick() {
        LivingEntity leader = findNearestLeader(orc);
        this.CountdownToFollow();
        if (leader != null && this.timer <= 1) {
            if(!(this.orc.distanceTo(leader) < 4)) {
                orc.getNavigation().startMovingTo(leader.getX(), leader.getY(), leader.getZ(), speed);
            } else orc.getNavigation().stop();

        }
    }

    private LivingEntity findNearestLeader(MobEntity follower) {
        // Priority order: OrcChampion, Minotaur, OrcWarrior
        Class<?>[] leaderClasses = {OrcChampionEntity.class, MinotaurEntity.class, OrcWarriorEntity.class};

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
                    follower.getBoundingBox().expand(32), // Search within 32 blocks
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
            ensureSingleLeaderInArea((OrcGroupEntity) nearestLeader, follower.getWorld(), 32);
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
        } else if (leaderClass == MinotaurEntity.class) {
            return 1;
        } else if (leaderClass == OrcWarriorEntity.class) {
            return 2; // Lowest priority
        }
        return Integer.MAX_VALUE; // Default for unknown classes
    }

    private boolean isInsideCamp() {
        // Check if the PoentEntity is inside the camp's bounding box
        RegistryEntry<Structure> orcCampStructure = this.world.getRegistryManager()
                .get(RegistryKeys.STRUCTURE)
                .getEntry(CustomStructureKeys.ORC_CAMP)
                .orElse(null);

        if (orcCampStructure == null) {
            return false;
        }

        StructureAccessor structureAccessor = this.world.getStructureAccessor();
        StructureStart structureStart = structureAccessor.getStructureAt(this.orc.getBlockPos(), orcCampStructure.value());

        if (structureStart != StructureStart.DEFAULT) {
            BlockBox boundingBox = structureStart.getBoundingBox();
            BlockBox expandedBox = new BlockBox(
                    boundingBox.getMinX() - 32,  // West
                    boundingBox.getMinY(),  // Down
                    boundingBox.getMinZ() - 32,  // North
                    boundingBox.getMaxX() + 32,  // East
                    boundingBox.getMaxY(),  // Up
                    boundingBox.getMaxZ() + 32   // South
            );

            return expandedBox.contains(this.orc.getBlockPos());
        }

        return false;
    }
}
