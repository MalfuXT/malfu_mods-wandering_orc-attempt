package malfu.wandering_orc.entity.ai;

import malfu.wandering_orc.entity.custom.OrcWarlockEntity;
import malfu.wandering_orc.entity.projectiles.FireProjectileEntity;
import malfu.wandering_orc.sound.ModSounds;
import malfu.wandering_orc.util.MobMoveUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.EnumSet;

public class OrcWarlockAttackSummonGoal extends Goal {
    private final OrcWarlockEntity orc;
    private LivingEntity target;
    private World world;
    private int attackCooldown;
    private double speed;
    private int condition = 0;
    private float damage;


    public OrcWarlockAttackSummonGoal(OrcWarlockEntity orc, double speed) {
        this.orc = orc;
        this.speed = speed;
        this.damage = (float) this.orc.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        this.setControls(EnumSet.of(Control.TARGET, Control.MOVE));
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

        if (this.condition == 0) {
            if (!this.orc.isSummonCD()) {
                this.condition = 1;
            } else condition = 2;
        }

        if (this.condition == 1) {
            this.Summon();
        }

        if (this.condition == 2) {
            this.Attack();
        }
    }

    protected void Summon() {
        this.orc.setAttackName("summon");

        if(this.attackCooldown > 0) {
            this.orc.getNavigation().startMovingTo(target, 0.1f);
        }

        if(this.attackCooldown <= 40) {
            this.orc.setTrigger(false);
        }

        if(this.attackCooldown <= 0) {
            this.attackCooldown = 80;
            this.orc.setTrigger(true);
        } else if (this.attackCooldown == 47) {
            this.orc.summonFirelink();
            this.orc.summonFirelink();
            this.orc.setSummonCD(true);
        } else if (this.attackCooldown == 5) {
            this.condition = 0;
        }
    }

    protected void Attack() {
        this.orc.setAttackName("attack");
        double distanceToTarget = this.orc.distanceTo(this.target);

        if(this.attackCooldown <= 80 & this.attackCooldown >= 55) {
            this.orc.getNavigation().stop();
            this.orc.lookAtEntity(target, 30, 30);
            this.orc.getMoveControl().strafeTo(-0.1f, 0f);
        }

        if (distanceToTarget <= 15 && this.attackCooldown <= 0 && this.orc.canSee(target)) {
            this.attackCooldown = 80;
            this.orc.setTrigger(true);
        } else if (distanceToTarget <= 15 && this.attackCooldown == 55 && this.orc.isTrigger()){
            World world = this.orc.getWorld();
            FireProjectileEntity fireProjectileEntity = new FireProjectileEntity(world, orc, damage);

            // Calculate the offset in front of the orc
            double offsetDistance = 1.25; // Distance in front of the orc (adjust as needed)
            double yawRadians = Math.toRadians(this.orc.getYaw()); // Convert yaw to radians

            double offsetX = -Math.sin(yawRadians) * offsetDistance; // Calculate X offset
            double offsetZ = Math.cos(yawRadians) * offsetDistance; // Calculate Z offset

            // Set the arrow's position slightly in front of the orc
            double arrowX = this.orc.getX() + offsetX;
            double arrowY = this.orc.getEyeY() - 0.05; // Adjust Y position to eye level
            double arrowZ = this.orc.getZ() + offsetZ;
            fireProjectileEntity.setPosition(arrowX, arrowY, arrowZ);

            // Calculate the arrow's velocity based on the target's position
            double targetX = this.target.getX() - arrowX;
            double targetY = this.target.getBodyY(0.33333333333) - arrowY;
            double targetZ = this.target.getZ() - arrowZ;
            double f = MathHelper.sqrt((float) (targetX * targetX + targetZ * targetZ));
            fireProjectileEntity.setVelocity(targetX, targetY + f * 0.05, targetZ, 0.8F, 0.5F);
            world.spawnEntity(fireProjectileEntity);

            this.orc.setTrigger(false);
            this.orc.playSound(ModSounds.FIREBALL_SHOOT, 0.5F, 1.0F);
        } else if (this.attackCooldown == 54) {
            this.orc.setTrigger(false);
        }
        if (distanceToTarget > 15) {
            Path path = this.orc.getNavigation().findPathTo(target, 6);
            this.orc.getNavigation().startMovingAlong(path, this.speed);
            this.orc.setTrigger(false);

        } else if (this.attackCooldown <= 54 && distanceToTarget < 7) {
            MobMoveUtil.moveAwayFromTarget(orc, target, 8, speed);
        } else if (this.attackCooldown == 5) {
            this.condition = 0;
        }
    }
}
