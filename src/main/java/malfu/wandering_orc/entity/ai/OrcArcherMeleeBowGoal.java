package malfu.wandering_orc.entity.ai;

import malfu.wandering_orc.entity.custom.OrcArcherEntity;
import malfu.wandering_orc.entity.custom.OrcGroupEntity;
import malfu.wandering_orc.entity.projectiles.OrcArrowEntity;
import malfu.wandering_orc.sound.ModSounds;
import malfu.wandering_orc.util.MobMoveUtil;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
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
    private float damage;

    public OrcArcherMeleeBowGoal(OrcArcherEntity orc, double speed) {
        this.orc = orc;
        this.speed = speed;
        this.damage = (float) this.orc.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        this.setControls(EnumSet.of(Control.TARGET, Control.MOVE));
    }

    protected void getPunchSound() { //sounds decrease as range added
        this.orc.playSound(ModSounds.ORC_ARCHER_PUNCH, 1.0F, 1.0F);
    }

    protected void punchAttack(LivingEntity target) {
        if(this.orc.tryAttack(target)) {
            if(target.getWidth() <= 2 && target.getHeight() <= 2.5){
                target.addVelocity(
                        target.getX() - this.orc.getX(),
                        0.3,
                        target.getZ() - this.orc.getZ()
                );
            }
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
        double distanceToTarget = this.orc.distanceTo(this.target);

        if(this.attackCondition == 0) {
            if(distanceToTarget > 3) {
                this.orc.setAttackName("animation.orc_archer.bow_attack");
                this.attackCondition = 1;
            } else {
                this.orc.setAttackName("animation.orc_archer.melee_attack");
                this.attackCondition = 2;
            }
        }

        if(this.attackCondition == 1) {
            if(this.attackCooldown <= 80 & this.attackCooldown >= 64) {
                this.orc.getNavigation().stop();
                this.orc.lookAtEntity(target, 30, 30);
                this.orc.getMoveControl().strafeTo(-0.1f, 0f);
            }

            if (distanceToTarget <= 20 && this.attackCooldown <= 0 && this.orc.canSee(target)) {
                this.attackCooldown = 80;
                this.orc.setTrigger(true);
            } else if (distanceToTarget <= 20 && this.attackCooldown == 64 && this.orc.isTrigger()){
                World world = this.orc.getWorld();
                ItemStack arrowStack = this.orc.getProjectileType(new ItemStack(Items.ARROW));
                PersistentProjectileEntity arrow = createArrow(world, arrowStack);
                arrow.setOwner(this.orc);

                // Calculate the offset in front of the orc
                double offsetDistance = 1.0; // Distance in front of the orc (adjust as needed)
                double yawRadians = Math.toRadians(this.orc.getYaw()); // Convert yaw to radians

                double offsetX = -Math.sin(yawRadians) * offsetDistance; // Calculate X offset
                double offsetZ = Math.cos(yawRadians) * offsetDistance; // Calculate Z offset

                // Set the arrow's position slightly in front of the orc
                double arrowX = this.orc.getX() + offsetX;
                double arrowY = this.orc.getEyeY() - 0.1; // Adjust Y position to eye level
                double arrowZ = this.orc.getZ() + offsetZ;
                arrow.setPosition(arrowX, arrowY, arrowZ);

                // Calculate the arrow's velocity based on the target's position
                double targetX = this.target.getX() - arrowX;
                double targetY = this.target.getBodyY(0.33333333333) - arrowY;
                double targetZ = this.target.getZ() - arrowZ;
                double f = MathHelper.sqrt((float) (targetX * targetX + targetZ * targetZ));
                arrow.setVelocity(targetX, targetY + f * 0.15, targetZ, 1.8F, 0.5F);

                // Spawn the arrow in the world
                world.spawnEntity(arrow);

                // Reset the orc's trigger and play a sound
                this.orc.setTrigger(false);
                this.orc.playSound(SoundEvents.ITEM_CROSSBOW_SHOOT, 1.0F, 0.8F);
            } else if (distanceToTarget > 20) {
                Path path = this.orc.getNavigation().findPathTo(target, 6);
                this.orc.getNavigation().startMovingAlong(path, this.speed);
                this.orc.setTrigger(false);

            } else if (this.attackCooldown <= 1) {
                this.attackCondition = 0;

            } else if (this.attackCooldown <= 60 && distanceToTarget < 7) {
                MobMoveUtil.moveAwayFromTarget(orc, target, 10, speed);
            }
        }

        if(this.attackCondition == 2) {
            if(this.attackCooldown <= 60 && this.attackCooldown >= 5) {
                MobMoveUtil.moveAwayFromTarget(orc, target, 10, speed);
            } else {
                this.orc.getNavigation().startMovingTo(target, this.speed);
            }

            if(distanceToTarget <= 3 && this.attackCooldown == 0) {
                this.orc.setAttackName("animation.orc_archer.melee_attack");
                this.attackCooldown = 80;
                this.orc.setTrigger(true);

            } else if (distanceToTarget <= 3 && this.attackCooldown == 65) {
                this.punchAttack(target);
                this.orc.setTrigger(false);

            } else if (this.attackCooldown == 64) {
                this.orc.setTrigger(false);
            } else if (this.attackCooldown <= 1) {
                this.attackCondition = 0;
            }
        }
    }

    private PersistentProjectileEntity createArrow(World world, ItemStack arrowStack) {
        return new OrcArrowEntity(world, this.orc, damage);
    }
}
