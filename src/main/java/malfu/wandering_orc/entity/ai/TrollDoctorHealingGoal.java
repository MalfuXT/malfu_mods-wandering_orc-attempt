package malfu.wandering_orc.entity.ai;

import malfu.wandering_orc.entity.custom.OrcGroupEntity;
import malfu.wandering_orc.entity.custom.TrollDoctorEntity;
import malfu.wandering_orc.util.MobMoveUtil;
import malfu.wandering_orc.util.ParticleUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.List;

public class TrollDoctorHealingGoal extends Goal {
    private final TrollDoctorEntity orc;
    private double speed;

    private int condition = 0;
    private int cooldown = 0;
    private int persecond = 0;

    private static final double HEALING_RANGE = 15.0; // Healing range in blocks
    private static final double SCAN_RANGE = 40.0; // Scan range in blocks
    private static final double MIN_DISTANCE_TO_TARGET = 10.0; // Stay 5 blocks away from target
    private static final int CASTING_TIME = 100; // 5 seconds in ticks (20 ticks = 1 second)
    private static final float HEAL_PERCENT_PER_SECOND = 0.10f; // 10% HP per second
    private static final float HEALSET = HEAL_PERCENT_PER_SECOND/20;
    private static final float AREA_HEAL = 0.40f; //Heal Percent per-trigger
    private static final float areaHealRadius = 15;
    //HEAL AND AREAHEAL COOLDOWN ON TrollDoctorEntity

    private LivingEntity healingTarget; // The target being healed
    private int castingTimer = 0; // Timer for casting
    private boolean isCasting = false; // Whether the Troll Doctor is currently casting

    public TrollDoctorHealingGoal(TrollDoctorEntity orc, double speed) {
        this.orc = orc;
        this.speed = speed;
        this.setControls(EnumSet.of(Control.TARGET, Control.MOVE));
    }

    @Override
    public boolean canStart() {
        // Find the lowest health teammate within the scan range
        this.healingTarget = findLowestHealthTeammate();

        // Check if the Troll Doctor can heal
        return this.healingTarget != null && this.healingTarget.isAlive() && this.orc.distanceTo(this.healingTarget) <= SCAN_RANGE;
    }

    @Override
    public void start() {
        // Start healing
        this.isCasting = false; // Reset casting state
        this.healingTarget = findLowestHealthTeammate(); // Find a new healing target
        this.orc.setHealingProcess(true);
        this.orc.setAttacking(true);
    }

    @Override
    public void stop() {
        // Reset healing state
        this.healingTarget = null;
        this.isCasting = false;

        // Reset other states
        this.orc.setHealing(false);
        this.orc.setHealingProcess(false);
        this.orc.setAttacking(false);
        this.orc.setAreaHealAnim(false);
    }

    @Override
    public boolean shouldContinue() {
        // Continue healing if:
        // 1. The healing target is still alive.
        // 2. The healing target is within the healing range.
        // 3. The Troll Doctor is not on cooldown.
        return this.healingTarget != null && this.healingTarget.isAlive() && this.orc.distanceTo(this.healingTarget) <= HEALING_RANGE && !this.orc.isHealCD();
    }

    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (healingTarget == null || !healingTarget.isAlive() || this.orc.distanceTo(healingTarget) > HEALING_RANGE) {
            // Find a new target
            healingTarget = findLowestHealthTeammate();
            if (healingTarget == null) return;
        }

        if (this.condition == 0) {
            if (this.canTriggerAreaHeal() && !this.orc.isAreaHealCD()) {
                this.condition = 1;
            } else condition = 2;
        }

        if (this.condition == 1) {
            this.cooldown = Math.max(this.cooldown - 1, 0);

            if(this.cooldown == 0){
                this.orc.setAreaHealAnim(true);
                this.cooldown = 55;
            } else if (this.cooldown == 18) {
                triggerAreaHeal();
                this.orc.setAreaHealCD(true);
                
            } else if (this.cooldown == 5) {
                this.orc.setAreaHealAnim(false);
            } else if (this.cooldown == 1) {
                this.condition = 0;
            }
        }

        if (this.condition == 2) {
            this.Healing();
        }

    }

    //HEALING MECHANIC!
    private void triggerAreaHeal() {
        // Get all OrcGroupEntity within a 15-block radius (including the Troll Doctor itself)
        List<LivingEntity> nearbyEntities = this.orc.getWorld().getEntitiesByClass(
                LivingEntity.class,
                this.orc.getBoundingBox().expand(areaHealRadius), // 15-block radius
                entity -> entity instanceof OrcGroupEntity // Include Troll Doctor itself
        );

        // Heal all OrcGroupEntity in the area
        for (LivingEntity entity : nearbyEntities) {
            double healAmount = entity.getMaxHealth() * AREA_HEAL; // Heal 40% of max HP
            entity.heal((float) healAmount);

            // Generate healing particles for visual feedback
            ParticleUtil.generateAreaHealParticle(this.orc);
            ParticleUtil.generateHealingParticle(entity);
        }

        // Play a sound or animation to indicate the area heal
        this.orc.setHealing(true); // Trigger healing animation
    }

    protected void Healing() {
        if (!this.isCasting) {
            moveToHealingTarget();
        } else {
            if(this.castingTimer > 0 && !this.orc.isHealCD()) {
                this.castingTimer = Math.max(this.castingTimer - 1, 0);
                this.healingTarget.heal(healingTarget.getMaxHealth()*HEALSET);
                this.orc.setHealing(true); //ANIMATION ACTIVE
                this.orc.lookAtEntity(healingTarget, 20, 20);
                this.orc.getMoveControl().strafeTo(0.02F, 0F);

                if(this.persecond > 0) {
                    this.persecond--;
                } else {
                    ParticleUtil.generateHealingParticle(this.healingTarget);
                    this.persecond = 20;
                }
                
            } if (this.castingTimer == 0) {
                this.orc.setHealing(false);
                this.orc.setHealCD(true); //turn on the TrollD global cooldown on TrollDoctorEntity
                this.isCasting = false;
                this.castingTimer = CASTING_TIME;
                this.condition = 0;
            }
        }

        // Check for interruptions (e.g., if attacked)
        if (this.orc.hurtTime > 0) {
            interruptHealing();

            LivingEntity runTarget = this.orc.getAttacker();
            if(runTarget != null) {
                MobMoveUtil.moveAwayFromTarget(this.orc, runTarget, 6, speed);
            }
        }
    }

    private void moveToHealingTarget() {
        if (this.healingTarget == null) return;

        double distance = this.orc.distanceTo(this.healingTarget);
        if (distance > MIN_DISTANCE_TO_TARGET) {
            // Move closer to the target
            this.orc.getNavigation().startMovingTo(this.healingTarget, speed); // Adjust speed as needed
            this.orc.setHealing(false);
        } else {
            // Stop moving and start casting
            this.orc.setHealing(false);
            this.orc.getNavigation().stop();
            this.startCasting();
        }
    }

    private void startCasting() {
        this.isCasting = true;
        this.castingTimer = CASTING_TIME;
    }

    private void interruptHealing() {
        if (!isCasting) return;

        this.isCasting = false;
        this.orc.setHealing(false);
        this.castingTimer = CASTING_TIME;
        this.orc.setHealCD(true);
        this.condition = 0;


    }

    private LivingEntity findLowestHealthTeammate() {
        // Get all entities within a 30-block radius
        List<LivingEntity> nearbyEntities = this.orc.getWorld().getEntitiesByClass(
                LivingEntity.class,
                this.orc.getBoundingBox().expand(SCAN_RANGE), // 30-block radius
                entity -> entity instanceof OrcGroupEntity // Include Troll Doctor itself
        );

        LivingEntity lowestHealthTeammate = null;
        double lowestHealthPercentage = Double.MAX_VALUE;

        // Check if the Troll Doctor's HP is below 30%
        double trollDoctorHealthPercentage = (this.orc.getHealth() / this.orc.getMaxHealth()) * 100;
        if (trollDoctorHealthPercentage < 30) {
            return this.orc; // Prioritize healing itself
        }

        // Find the teammate with the lowest percentage HP
        for (LivingEntity teammate : nearbyEntities) {
            double teammateHealthPercentage = (teammate.getHealth() / teammate.getMaxHealth()) * 100;

            // Skip teammates that are already at full health
            if (teammateHealthPercentage >= 100) continue;

            // Prioritize the teammate with the lowest percentage HP
            if (teammateHealthPercentage <= 80){
                if (teammateHealthPercentage < lowestHealthPercentage) {
                    lowestHealthPercentage = teammateHealthPercentage;
                    lowestHealthTeammate = teammate;
                }
            }

        }



        return lowestHealthTeammate;
    }

    private boolean canTriggerAreaHeal() {
        // Get all OrcGroupEntity within a 10-block radius (including the Troll Doctor itself)
        List<LivingEntity> nearbyEntities = this.orc.getWorld().getEntitiesByClass(
                LivingEntity.class,
                this.orc.getBoundingBox().expand(10.0), // 10-block radius
                entity -> entity instanceof OrcGroupEntity // Include Troll Doctor itself
        );

        int count = 0; // Counter for entities with less than 80% HP

        // Check each entity's HP
        for (LivingEntity entity : nearbyEntities) {
            double healthPercentage = (entity.getHealth() / entity.getMaxHealth()) * 100;
            if (healthPercentage < 80) {
                count++;
            }

            // If at least 4 entities meet the condition, return true
            if (count >= 3) {
                return true;
            }
        }

        // Return false if fewer than 4 entities meet the condition
        return false;
    }

    //ENDS OF HEALING MECHANIC!
}
