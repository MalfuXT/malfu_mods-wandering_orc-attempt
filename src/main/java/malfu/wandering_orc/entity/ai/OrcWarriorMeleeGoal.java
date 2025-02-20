package malfu.wandering_orc.entity.ai;

import malfu.wandering_orc.entity.custom.OrcArcherEntity;
import malfu.wandering_orc.entity.custom.OrcWarriorEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.List;

public class OrcWarriorMeleeGoal extends Goal {
    private final OrcWarriorEntity orc;
    private LivingEntity target;
    private int attackCooldown;
    private double speed;
    private int attackCondition = 0;
    double randomizer;

    public OrcWarriorMeleeGoal(OrcWarriorEntity orc, double speed) {
        this.orc = orc;
        this.speed = speed;
        this.setControls(EnumSet.of(Control.TARGET, Control.MOVE)); // Important!
    }

    private void attackNormal() {
        this.orc.tryAttack(target);
        this.orc.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 1.0F);
    }

    //START OF STOP ATTACK CODE
    private int stopAttackCD;
    private void stopAttackTrig(int stopATimer) {
        this.stopAttackCD = stopATimer;
        this.orc.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.15f);
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
        this.target = this.orc.getTarget(); // Keep target updated
        this.attackCooldown = Math.max(this.attackCooldown - 1, 0);
        if (this.target == null) return;
        double distanceToTarget = this.orc.distanceTo(this.target);
        double d = getSquaredMaxAttackDistance(target);
        this.randomizer = Math.random();
        this.orc.getLookControl().lookAt(target);
        this.orc.getNavigation().startMovingTo(target, this.speed);
        this.stopAttack();

        if(this.attackCondition == 0) {
            if(this.randomizer < 0.7) {
                if(this.randomizer < 0.4) {
                    this.orc.setAttackName("animation.orc_warrior.attack_running");
                    this.attackCondition = 1;
                } else {
                    this.orc.setAttackName("animation.orc_warrior.attack_running2");
                    this.attackCondition = 2;
                }
            } else {
                this.orc.setAttackName("animation.orc_warrior.attack_combo");
                this.attackCondition = 3;
            }
        }

        if(this.attackCondition == 1 || this.attackCondition == 2) {

            if(distanceToTarget <= d && this.attackCooldown == 0) {
                this.attackCooldown = 40;
                this.orc.setTrigger(true);

            } else if (distanceToTarget <= d && this.attackCooldown == 35) {
                this.attackNormal();

            } else if (this.attackCooldown == 34) {
                this.orc.setTrigger(false);

            } else if (this.attackCooldown <= 1) {
                this.attackCondition = 0;
            }

        }
        if (this.attackCondition == 3) {

            if(distanceToTarget <= d && this.attackCooldown == 0) {
                this.attackCooldown = 40;
                this.orc.setTrigger(true);
                this.stopAttackTrig(25);

            } else if (distanceToTarget <= d && this.attackCooldown == 25) {
                this.attackNormal();

            } else if (distanceToTarget <= d && this.attackCooldown == 15) {
                this.attackNormal();

            } else if (this.attackCooldown == 14) {
                this.orc.setTrigger(false);

            } else if (this.attackCooldown <= 1) {
                this.attackCondition = 0;
            }
        }
    }

    protected double getSquaredMaxAttackDistance(LivingEntity entity) {
        return (double) (2.0F + entity.getWidth());
    }
}
