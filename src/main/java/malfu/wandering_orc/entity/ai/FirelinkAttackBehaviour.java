package malfu.wandering_orc.entity.ai;

import malfu.wandering_orc.entity.custom.FirelinkEntity;
import malfu.wandering_orc.entity.custom.OrcWarriorEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.sound.SoundEvents;

import java.util.EnumSet;

public class FirelinkAttackBehaviour extends Goal {
    private final FirelinkEntity orc;
    private LivingEntity target;
    private int attackCooldown;
    private int countdownToBurn;
    private double speed;
    private int attackCondition = 0;
    double randomizer;

    public FirelinkAttackBehaviour(FirelinkEntity orc, double speed) {
        this.orc = orc;
        this.speed = speed;
        this.setControls(EnumSet.of(Control.TARGET, Control.MOVE)); // Important!
    }

    private void attackNormal() {
        if (orc.tryAttack(target)) {
            countdownToBurn++;
            if (this.countdownToBurn >= 3){
                target.setFireTicks(100);
            }
        }
        this.orc.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 1.0F);
    }

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
        this.orc.getLookControl().lookAt(target.getX(), target.getEyeY(), target.getZ());
        this.orc.getNavigation().startMovingTo(target, this.speed);

        if(this.countdownToBurn >= 3) {
            this.countdownToBurn = 0;
        }

        if(this.attackCondition == 0) {
            if(this.randomizer < 0.7) {
                if(this.randomizer < 0.4) {
                    this.orc.setAttackName("attack");
                    this.attackCondition = 1;
                } else {
                    this.orc.setAttackName("attack2");
                    this.attackCondition = 2;
                }
            }
        }

        if(this.attackCondition == 1 || this.attackCondition == 2) {

            if(distanceToTarget <= d && this.attackCooldown == 0) {
                this.attackCooldown = 30;
                this.orc.setTrigger(true);

            } else if (distanceToTarget <= d && this.attackCooldown == 17) {
                this.attackNormal();

            } else if (this.attackCooldown == 10) {
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
