package malfu.wandering_orc.entity.ai;

import malfu.wandering_orc.entity.custom.OrcChampionEntity;
import malfu.wandering_orc.sound.ModSounds;
import malfu.wandering_orc.util.MobMoveUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;
import java.util.List;

public class OrcChampionMeleeGoal extends Goal {
    private final OrcChampionEntity orc;
    private LivingEntity target;
    private int attackCooldown;
    private double speed;
    private int attackCondition = 0;
    double randomizer;
    private int shieldCD;
    private int shieldCount;
    private int dodgeCooldown;
    private int dodgeCount;
    private int dodgeNoDMGTimer = 10;

    private int initialcooldown = 40;
    private int initiallongercd = 100;
    private int initialdodgecd = 10;
    private int moveattack = 9;
    private int moveshieldattack = 9;
    private int movecombo1 = 9;
    private int movecombo2 = 20;
    private int movecombo3 = 32;

    public OrcChampionMeleeGoal(OrcChampionEntity orc, double speed) {
        this.orc = orc;
        this.speed = speed;
        this.setControls(EnumSet.of(Control.TARGET, Control.MOVE)); // Important!
    }

    private void attackNormal() {
        this.orc.tryAttack(target);
        this.orc.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 1.0F);
    }

    private void shieldAttack() {
        if(this.orc.tryAttack(target)) {
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 20, 50));
        }

        this.orc.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 0.7F, 0.7F);
    }

    //PARTICLE PRODUCE
    private void generateDodgeParticle() {
        Vec3d sourcePos = orc.getPos();
        if (this.orc.getWorld().isClient()) {
            return;
        }
        ((ServerWorld) this.orc.getWorld()).spawnParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, sourcePos.x, sourcePos.y, sourcePos.z, 6, 0.3, 0.2, 0.3, 0.01);
    }

    private void generateShieldParticle() {
        Vec3d sourcePos = orc.getPos();
        if (this.orc.getWorld().isClient()) {
            return;
        }
        ((ServerWorld) this.orc.getWorld()).spawnParticles(ParticleTypes.FLASH, sourcePos.x, sourcePos.y, sourcePos.z, 2, 0.1, 0.1, 0.1, 0.01);
    }
    //END OF PARTICLE PRODUCE

    private boolean isBeingTargeted() {
        // Get all nearby entities
        List<LivingEntity> nearbyEntities = orc.getWorld().getEntitiesByClass(
                LivingEntity.class,
                orc.getBoundingBox().expand(10), // Adjust the range as needed
                entity -> entity instanceof MobEntity && ((MobEntity) entity).getTarget() == orc
        );

        // Return true if any nearby entity is targeting the mob
        return !nearbyEntities.isEmpty();
    }

    //START OF STOP ATTACK CODE
    private void setAttackStopped() {
        if (this.orc.isShieldStop()) {
            this.orc.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.025f);
        } else {
            this.orc.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.25f);
        }
    }
    //END OF STOP ATTACK CODE

    //DODGE MECHANIC HERE
    private void dodgeCountdown() {
        if (this.dodgeNoDMGTimer > 0) {
            this.dodgeNoDMGTimer--;
        } else {
            orc.setInvulnerable(false);
        }
    }

    private void dodge() {
        // Play sound and generate particles
        this.orc.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 0.5F, 0.3F);
        generateDodgeParticle();

        // Set invulnerability and dodge timer
        orc.setInvulnerable(true);
        this.dodgeNoDMGTimer = 12;

        // Determine dodge direction based on chance
        double random = orc.getRandom().nextDouble();
        Vec3d dodgeDirection;

        if (random < 0.4) {
            // Front dodge (40% chance)
            dodgeDirection = orc.getRotationVector().add(0, 0.1, 0); // Dodge in the direction the mob is facing
        } else if (random < 0.7) {
            // Left dodge (30% chance)
            dodgeDirection = orc.getRotationVector().rotateY((float) Math.toRadians(-90)).add(0, 0.1, 0); // Rotate 90 degrees left
        } else if (random < 1.0) {
            // Right dodge (30% chance)
            dodgeDirection = orc.getRotationVector().rotateY((float) Math.toRadians(90)).add(0, 0.1, 0); // Rotate 90 degrees right
        } else {
            // Back dodge (10% chance)
            dodgeDirection = orc.getRotationVector().rotateY((float) Math.toRadians(180)).add(0, 0.1, 0); // Rotate 180 degrees (backward)
        }

        // Calculate dodge distance (adjust as needed)
        double dodgeDistance = 1.0;

        // Calculate dodge velocity
        Vec3d dodgeVelocity = dodgeDirection.normalize().multiply(dodgeDistance);

        // Apply dodge velocity
        orc.setVelocity(dodgeVelocity);
    }
    //DODGE MECHANIC ENDS HERE

    @Override
    public boolean canStart() {
        LivingEntity target = this.orc.getTarget();
        return target != null && target.isAlive() && this.orc.canSee(target);
    }

    @Override
    public void start() {
        this.orc.setAttacking(true);
        this.orc.setTrigger(false);
        this.orc.setDodge(false);
    }

    @Override
    public void stop() {
        this.target = null;
        this.orc.setTrigger(false);
        this.orc.setAttacking(false);
        this.orc.setDodge(false);
    }

    @Override
    public boolean shouldContinue() {
        return this.target != null && this.target.isAlive();
    }

    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        this.target = this.orc.getTarget();
        this.attackCooldown = Math.max(this.attackCooldown - 1, 0);
        this.dodgeCooldown = Math.max(this.dodgeCooldown - 1, 0);
        this.shieldCD = Math.max(this.shieldCD - 1, 0);
        this.dodgeCountdown();
        if (this.target == null) return;
        this.orc.getLookControl().lookAt(target);
        double distanceToTarget = this.orc.distanceTo(this.target);
        double d = getSquaredMaxAttackDistance(target);
        this.setAttackStopped();

        if(this.dodgeCount >= 3) {
            this.dodgeCount = 0;
            this.dodgeCooldown = 100;
        }
        if(this.shieldCount >= 3) {
            this.shieldCount = 0;
            this.shieldCD = 100;
        }

        if((attackCondition == 1 || attackCondition == 2 || attackCondition == 3 && attackCooldown <= initialcooldown-moveattack-3)
                || (attackCondition == 4 && attackCooldown <= initiallongercd-(movecombo3+movecombo2+movecombo1+3))) {
            MobMoveUtil.circleTarget(orc, target, 5, this.speed);
        } else {
            this.orc.getNavigation().startMovingTo(target, this.speed);
        }

        if(this.attackCondition == 0) {
            this.randomizer = Math.random();
            if(this.randomizer < 0.7) {
                this.randomizer = Math.random();
                if(this.randomizer < 0.5 && (isBeingTargeted() || (target != null && target.isPlayer()))) {
                    if(this.dodgeCooldown == 0) {
                        this.attackCondition = 99;
                    } else if (this.shieldCD == 0) {
                        this.attackCondition = 98;
                    }
                }
            } else {
                this.randomizer = Math.random();
                if (this.randomizer < 0.1) {
                    this.orc.setAttackName("animation.orc_champion.move_combo");
                    this.attackCondition = 4;
                } else if (this.randomizer < 0.3) {
                    this.orc.setAttackName("animation.orc_champion.move_shield_attack");
                    this.attackCondition = 3;
                } else if (this.randomizer < 0.65) {
                    this.orc.setAttackName("animation.orc_champion.move_attack");
                    this.attackCondition = 1;
                } else {
                    this.orc.setAttackName("animation.orc_champion.move_attack2");
                    this.attackCondition = 2;
                }
            }
        }

        if(this.attackCondition == 1 || this.attackCondition == 2) {

            if(distanceToTarget <= d && this.attackCooldown == 0) {
                this.attackCooldown = initialcooldown;
                this.orc.setTrigger(true);

            } else if (distanceToTarget <= d && this.attackCooldown == initialcooldown-moveattack) {
                this.attackNormal();

            } else if (this.attackCooldown == initialcooldown-moveattack-3) {
                this.orc.setTrigger(false);

            } else if (this.attackCooldown <= 1) {
                this.attackCondition = 0;
            }

        }

        if(this.attackCondition == 3) {

            if(distanceToTarget <= d && this.attackCooldown == 0) {
                this.attackCooldown = initialcooldown;
                this.orc.setTrigger(true);

            } else if (distanceToTarget <= d && this.attackCooldown == initialcooldown-moveshieldattack) {
                this.shieldAttack();

            } else if (this.attackCooldown == initialcooldown-moveshieldattack-3) {
                this.orc.setTrigger(false);

            } else if (this.attackCooldown <= 1) {
                this.attackCondition = 0;
            }
        }

        if (this.attackCondition == 4) {

            if(distanceToTarget <= d && this.attackCooldown == 0) {
                this.attackCooldown = initiallongercd;
                this.orc.setTrigger(true);

            } else if (distanceToTarget <= d && this.attackCooldown == initiallongercd-movecombo1) {
                this.attackNormal();

            } else if (distanceToTarget <= d && this.attackCooldown == initiallongercd-movecombo2) {
                this.attackNormal();

            } else if (distanceToTarget <= d && this.attackCooldown == initiallongercd-movecombo3) {
                this.attackNormal();

            } else if (this.attackCooldown == initiallongercd-movecombo3-3) {
                this.orc.setTrigger(false);

            } else if (this.attackCooldown <= 1) {
                this.attackCondition = 0;
            }
        }

        if(this.attackCondition == 99) {
            if(distanceToTarget <= d && this.attackCooldown == 0) {
                this.attackCooldown = initialdodgecd;
                this.dodge();
                this.dodgeCount++;

            } else if (this.attackCooldown <= 1) {
                this.attackCondition = 0;
            }

        }

        if(this.attackCondition == 98) {
            if(distanceToTarget <= d && this.attackCooldown == 0) {
                this.generateShieldParticle();
                this.orc.playSound(ModSounds.SHIELD_STANCE, 1.0f, 1.0f);
                this.orc.setShielding(true);
                this.orc.setAnimShielding(true);
                this.orc.setShieldStop(true);
                this.attackCooldown = 40;
                this.shieldCount++;

            } else if (this.attackCooldown <= 1) {
                this.orc.setShielding(false);
                this.orc.setAnimShielding(false);
                this.orc.setShieldStop(false);
                this.attackCondition = 0;
            }

        }
    }

    protected double getSquaredMaxAttackDistance(LivingEntity entity) {
        return (double) (2.0F + entity.getWidth());
    }
}
