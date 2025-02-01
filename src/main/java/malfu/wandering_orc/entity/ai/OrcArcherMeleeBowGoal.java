package malfu.wandering_orc.entity.ai;

import malfu.wandering_orc.entity.custom.OrcArcherEntity;
import malfu.wandering_orc.sound.ModSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.EnumSet;

public class OrcArcherMeleeBowGoal extends Goal {
    private final OrcArcherEntity orc;
    private LivingEntity target;
    private int attackCooldown;
    private double speed;
    private int attackCondition = 0;
    double randomizer;

    public OrcArcherMeleeBowGoal(OrcArcherEntity orc, double speed) {
        this.orc = orc;
        this.speed = speed;
        this.setControls(EnumSet.of(Control.TARGET, Control.MOVE));
    }

    protected void getPunchSound() { //sounds decrease as range added
        this.orc.playSound(ModSounds.ORC_ARCHER_PUNCH, 1.0F, 1.0F);
    }

    protected void punchAttack(LivingEntity target) {
        if(this.orc.tryAttack(target)) {
            target.addVelocity(
                    target.getX() - this.orc.getX(),
                    0.5,
                    target.getZ() - this.orc.getZ()
            );
        }
        this.getPunchSound();
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
        this.randomizer = Math.random();
        double distanceToTarget = this.orc.distanceTo(this.target);

        if(this.attackCondition == 0) {
            if(distanceToTarget > 3) {
                this.orc.setAttackName("animation.orc_archer.bow_attack");
                this.attackCondition = 1;
            } else {
                if(this.randomizer < 0.3) {
                    this.orc.setAttackName("animation.orc_archer.bow_attack");
                    this.attackCondition = 1;
                } else {
                    this.orc.setAttackName("animation.orc_archer.melee_attack");
                    this.attackCondition = 2;
                }

            }
        }

        if(this.attackCondition == 1) {
            this.orc.setAttacking(false);

            if (target != null) {
                this.orc.getLookControl().lookAt(this.target, 10.0F, 10.0F);
            }

            if (distanceToTarget <= 15 && this.attackCooldown <= 0) {
                this.orc.getNavigation().startMovingTo(target, 0.1f);
                this.orc.setAttackName("animation.orc_archer.bow_attack");
                this.attackCooldown = 80;
                this.orc.setTrigger(true);
            } else if (distanceToTarget <= 15 && this.attackCooldown == 64){
                World world = this.orc.getWorld();
                ItemStack arrowStack = this.orc.getProjectileType(new ItemStack(Items.ARROW));
                PersistentProjectileEntity arrow = createArrow(world, arrowStack);
                arrow.setOwner(this.orc);

                double x = this.target.getX() - this.orc.getX();
                double y = this.target.getBodyY(0.33333333333) - arrow.getY();
                double z = this.target.getZ() - this.orc.getZ();
                double f = MathHelper.sqrt((float) (x * x + z * z));
                arrow.setVelocity(x, y + f * 0.1, z, 1.6F, 0.5F);
                world.spawnEntity(arrow);

                this.orc.setTrigger(false);
            } else if (distanceToTarget > 12) {
                this.orc.getNavigation().startMovingTo(this.target, this.speed);

            } else if (this.attackCooldown <= 1) {
                this.attackCondition = 0;
            }
        } else {
            this.orc.setAttacking(true);
        }

        if(this.attackCondition == 2) {
            this.orc.getNavigation().startMovingTo(target, this.speed);

            if(distanceToTarget <= 2) {
                this.orc.setAttacking(false);
            } else {
                this.orc.setAttacking(true);
            }

            if(distanceToTarget <= 3 && this.attackCooldown == 0) {
                this.orc.setAttackName("animation.orc_archer.melee_attack");
                this.attackCooldown = 80;
                this.orc.setTrigger(true);

            } else if (distanceToTarget <= 3 && this.attackCooldown == 65) {
                this.orc.getLookControl().lookAt(target.getX(), target.getEyeY(), target.getZ());
                this.punchAttack(target);
                this.orc.setTrigger(false);

            } else if (attackCooldown <= 1) {
                this.attackCondition = 0;
            }
        }
    }

    private PersistentProjectileEntity createArrow(World world, ItemStack arrowStack) {
        ArrowEntity arrow = new ArrowEntity(world, this.orc); // Use ArrowEntity
        arrow.setDamage(4.0); // Adjust damage
        // ... other arrow customizations if needed (e.g., pickup type) ...
        return arrow;
    }
}
