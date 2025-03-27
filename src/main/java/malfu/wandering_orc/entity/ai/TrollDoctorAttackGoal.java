package malfu.wandering_orc.entity.ai;

import malfu.wandering_orc.entity.custom.TrollDoctorEntity;
import malfu.wandering_orc.entity.projectiles.MagicProjectileEntity;
import malfu.wandering_orc.sound.ModSounds;
import malfu.wandering_orc.util.MobMoveUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.EnumSet;

public class TrollDoctorAttackGoal extends Goal {
    private final TrollDoctorEntity orc;
    private LivingEntity target;
    private int attackCooldown;
    private double speed;
    private float damage;

    public TrollDoctorAttackGoal(TrollDoctorEntity orc, double speed) {
        this.orc = orc;
        this.speed = speed;
        this.damage = (float) this.orc.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        this.setControls(EnumSet.of(Control.TARGET, Control.MOVE));
    }

    @Override
    public boolean canStart() {
        LivingEntity target = this.orc.getTarget();
        return target != null && target.isAlive() && this.orc.canSee(target) && !this.orc.isHealingProcess();
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
        return this.target != null && this.target.isAlive() && !this.orc.isHealingProcess();
    }

    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        this.target = this.orc.getTarget(); // Keep target updated
        this.attackCooldown = Math.max(this.attackCooldown - 1, 0);
        if (this.target == null) return;
        this.Attack();
    }


    protected void Attack() {
        double distanceToTarget = this.orc.distanceTo(this.target);

        if(this.attackCooldown <= 80 & this.attackCooldown >= 50) {
            this.orc.lookAtEntity(target, 30, 30);
            this.orc.getMoveControl().strafeTo(-0.025f, 0f);
        }

        if (distanceToTarget <= 15 && this.attackCooldown <= 0) {
            this.attackCooldown = 80;
            this.orc.setTrigger(true);
        } else if (distanceToTarget <= 15 && this.attackCooldown == 60){
            World world = this.orc.getWorld();
            MagicProjectileEntity magicProjectileEntity = new MagicProjectileEntity(world, orc, damage);

            double x = this.target.getX() - this.orc.getX();
            double y = this.target.getBodyY(0.33333333333) - magicProjectileEntity.getY();
            double z = this.target.getZ() - this.orc.getZ();
            double f = MathHelper.sqrt((float) (x * x + z * z));
            magicProjectileEntity.setVelocity(x, y + f * 0.05, z, 0.8F, 0.5F);
            world.spawnEntity(magicProjectileEntity);

            this.orc.playSound(ModSounds.MAGIC_SHOOT, 0.5F, 1.0F);
        } else if (this.attackCooldown == 58) {
            this.orc.setTrigger(false);

        } else if (distanceToTarget > 15) {
            Path path = this.orc.getNavigation().findPathTo(target, 6);
            this.orc.getNavigation().startMovingAlong(path, this.speed);
            this.orc.setTrigger(false);

        } else if (this.attackCooldown <= 50 && distanceToTarget < 7) {
            MobMoveUtil.moveAwayFromTarget(orc, target, 8, speed);
            this.orc.setTrigger(false);
        }
    }
}
