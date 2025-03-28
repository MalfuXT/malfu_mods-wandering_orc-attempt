package malfu.wandering_orc.entity.ai;

import malfu.wandering_orc.entity.custom.MinotaurEntity;
import malfu.wandering_orc.entity.custom.OrcChampionEntity;
import malfu.wandering_orc.entity.custom.OrcGroupEntity;
import malfu.wandering_orc.sound.ModSounds;
import malfu.wandering_orc.util.AreaDamage;
import malfu.wandering_orc.util.SoundUtil;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class MinotaurMeleeGoal extends Goal {
    private final MinotaurEntity orc;
    private LivingEntity target;
    private int attackCooldown;
    private int stompCooldown;
    private double speed;
    private int attackCondition = 0;
    double randomizer;

    private int initialcooldown = 80;
    private int normalattack = 9;
    private int holdattack = 25;
    private int holdmeattack = 39;
    private int stompaoe = 25;

    public MinotaurMeleeGoal(MinotaurEntity orc, double speed) {
        this.orc = orc;
        this.speed = speed;
        this.setControls(EnumSet.of(Control.TARGET, Control.MOVE)); // Important!
    }

    private void generateParticles() {
        Vec3d sourcePos = orc.getPos();
        if (this.orc.getWorld().isClient()) {
            return;
        }
        ((ServerWorld) this.orc.getWorld()).spawnParticles(ParticleTypes.EXPLOSION, sourcePos.x, sourcePos.y, sourcePos.z, 20, 1.0, 1.0, 1.0, 1.0);
    }

    private void stompAttack() {
        World world = orc.getWorld();
        BlockPos centerPos = orc.getBlockPos();
        AreaDamage.dealAreaDamageWithEffect(orc, 3, OrcGroupEntity.class, StatusEffects.SLOWNESS,
                100, 5, 4, new Vec3d(0, 0,1.5));
        AreaDamage.AreaCrackedGround(orc, world, centerPos, 3, new Vec3d(0, 0,1.5));
        this.orc.playSound(ModSounds.MINO_STOMP, 1.0F, 1.0F);
        this.generateParticles();

        if(target.getWidth() <= 2 && target.getHeight() <= 3){
            target.addVelocity(
                    0.0,
                    0.3,
                    0.0
            );
        }

        if (target instanceof PlayerEntity && target.isBlocking()) {
            disablePlayerShield((PlayerEntity) target);
        }
    }

    private void attackNormal() {
        // Get all entities hit by the attack
        List<LivingEntity> hitEntities = AreaDamage.dealAreaDamageReadHitTarget(
                orc,
                1.2,
                OrcGroupEntity.class,
                1,
                new Vec3d(0, 0, 1.8)
        );

        // Apply effects to each hit entity
        for (LivingEntity target : hitEntities) {
            // Apply knockback to small/medium entities
            if (target.getWidth() <= 2 && target.getHeight() <= 2.5) {
                target.addVelocity(
                        (target.getX() - this.orc.getX())*0.4,
                        0.3,
                        (target.getZ() - this.orc.getZ())*0.4
                );
            }

            SoundUtil.MinoHit(target, 1.0f, 1.2f);
            // Disable shields for players
            if (target instanceof PlayerEntity player && player.isBlocking()) {
                player.disableShield(true);
            }
        }
    }

    //SHIELD BROKE MECHANIC
    private void disablePlayerShield(PlayerEntity player) {
        ItemStack activeItem = player.getActiveItem();

        if (activeItem.getItem() instanceof ShieldItem) {
            player.getItemCooldownManager().set(activeItem.getItem(), 100);
            player.getWorld().sendEntityStatus(player, EntityStatuses.BREAK_SHIELD);
            player.clearActiveItem();
        }
    }
    //SHIELD BROKE MECHANIC ENDS HERE

    //START OF STOP ATTACK CODE BOOLEAN
    private int stopAttackCD;
    private void stopAttackTrig(int stopATimer) {
        this.stopAttackCD = stopATimer;
        this.orc.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.05f);
    }

    private void stopAttack() {
        if (this.stopAttackCD > 0) {
            this.stopAttackCD--;
        } else {
            this.orc.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.25f); //GETBASEVALUE DIDN'T WORK SOMEHOW, SO YEAH MANUAL.
        }
    }
    //END OF STOP ATTACK CODE

    @Override
    public boolean canStart() {
        LivingEntity target = this.orc.getTarget();
        return target != null && target.isAlive() && this.orc.canSee(target);
    }

    @Override
    public void start() {
        this.orc.setAttacking(true);
        this.orc.setTrigger(false);
    }

    @Override
    public void stop() {
        this.target = null;
        this.orc.setTrigger(false);
        this.orc.setAttacking(false);
        this.orc.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.25f);
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
        this.stompCooldown = Math.max(this.stompCooldown - 1, 0);
        if (this.target == null) return;
        double distanceToTarget = this.orc.distanceTo(this.target);
        double d = getSquaredMaxAttackDistance(target);
        this.orc.getNavigation().startMovingTo(target, speed);
        this.stopAttack();

        if(distanceToTarget <= d) {
            this.orc.setAttacking(false);
        } else {
            this.orc.setAttacking(true);
        }

        if(this.attackCondition == 0) {
            this.randomizer = Math.random();
            if(this.randomizer < 0.5 && stompCooldown == 0) {
                this.orc.setAttackName("animation.minotaur.stomp_aoe");
                this.attackCondition = 4;
            } else {
                if (this.randomizer < 0.2) {
                    this.orc.setAttackName("animation.minotaur.holdme_attack");
                    this.attackCondition = 3;
                } else if (this.randomizer < 0.5) {
                    this.orc.setAttackName("animation.minotaur.hold_attack");
                    this.attackCondition = 1;
                } else {
                    this.orc.setAttackName("animation.minotaur.normal_attack");
                    this.attackCondition = 2;
                }
            }
        }

        if(this.attackCondition == 1) {

            if(distanceToTarget <= d && this.attackCooldown == 0) {
                this.attackCooldown = initialcooldown;
                this.orc.setTrigger(true);
                this.stopAttackTrig(holdattack);

            } else if (distanceToTarget <= d && this.attackCooldown == initialcooldown-holdattack) {
                this.attackNormal();

            } else if (this.attackCooldown == initialcooldown-holdattack-5) {
                this.orc.setTrigger(false);

            } else if (this.attackCooldown <= 1) {
                this.attackCondition = 0;
            }
        }

        if(this.attackCondition == 2) {

            if(distanceToTarget <= d && this.attackCooldown == 0) {
                this.attackCooldown = initialcooldown;
                this.orc.setTrigger(true);
                this.stopAttackTrig(normalattack);

            } else if (distanceToTarget <= d && this.attackCooldown == initialcooldown-normalattack) {
                this.attackNormal();

            } else if (this.attackCooldown == initialcooldown-normalattack-5) {
                this.orc.setTrigger(false);

            } else if (this.attackCooldown <= 1) {
                this.attackCondition = 0;
            }

        }

        if(this.attackCondition == 3) {

            if(distanceToTarget <= d && this.attackCooldown == 0) {
                this.attackCooldown = initialcooldown;
                this.orc.setTrigger(true);
                this.stopAttackTrig(holdmeattack);

            } else if (distanceToTarget <= d && this.attackCooldown == initialcooldown-holdmeattack) {
                this.attackNormal();

            } else if (this.attackCooldown == initialcooldown-holdmeattack-5) {
                this.orc.setTrigger(false);

            } else if (this.attackCooldown <= 1) {
                this.attackCondition = 0;
            }

        }

        if(this.attackCondition == 4) {

            if(distanceToTarget <= d && this.attackCooldown == 0) {
                this.orc.playSound(ModSounds.MINO_GROWL, 1.0f, 1.0f);
                this.attackCooldown = initialcooldown;
                this.orc.setTrigger(true);
                this.stopAttackTrig(stompaoe);

            } else if (this.attackCooldown == initialcooldown-stompaoe) {
                this.stompAttack();
                stompCooldown = 200;

            } else if (this.attackCooldown == initialcooldown-stompaoe-5) {
                this.orc.setTrigger(false);

            } else if (this.attackCooldown <= 1) {
                this.attackCondition = 0;
            }

        }
    }

    protected double getSquaredMaxAttackDistance(LivingEntity entity) {
        return (double) (2.5F + entity.getWidth());
    }
}
