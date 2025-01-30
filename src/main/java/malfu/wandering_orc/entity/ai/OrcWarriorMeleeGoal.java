package malfu.wandering_orc.entity.ai;

import malfu.wandering_orc.entity.custom.OrcArcherEntity;
import malfu.wandering_orc.entity.custom.OrcWarriorEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.EnumSet;

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
        this.orc.getNavigation().startMovingTo(target, this.speed);

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
                this.orc.tryAttack(target);
                this.orc.setTrigger(false);

            } else if (this.attackCooldown <= 1) {
                this.attackCondition = 0;
            }

        }
        if (this.attackCondition == 3) {

            if(distanceToTarget <= d && this.attackCooldown == 0) {
                this.attackCooldown = 40;
                this.orc.setTrigger(true);
                this.orc.speed -= 1.3;

            } else if (distanceToTarget <= d && this.attackCooldown == 25) {
                this.orc.tryAttack(target);

            } else if (distanceToTarget <= d && this.attackCooldown == 15) {
                this.orc.tryAttack(target);
                this.orc.setTrigger(false);

            } else if (this.attackCooldown <= 1) {
                this.attackCondition = 0;

            } else if (this.attackCooldown <= 13) {
                this.orc.speed += 1.3;
            }
        }
    }

    protected double getSquaredMaxAttackDistance(LivingEntity entity) {
        return (double) (2.5F + entity.getWidth());
    }
}
