package malfu.wandering_orc.entity.ai;

import malfu.wandering_orc.entity.custom.TrollEntity;
import malfu.wandering_orc.entity.projectiles.TrollThrowableEntity;
import malfu.wandering_orc.item.ModItems;
import malfu.wandering_orc.sound.ModSounds;
import malfu.wandering_orc.util.MobMoveUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.EnumSet;

public class TrollThrowGoal extends Goal {
    private final TrollEntity orc;
    private LivingEntity target;
    private int attackCooldown;
    private int fastAttackCD;
    private int condition = 0;
    private double speed;
    double randomizer;
    private int initialcooldown = 80;
    private int throwattackcd = 7;
    private float damage;

    public TrollThrowGoal(TrollEntity orc, double speed) {
        this.orc = orc;
        this.speed = speed;
        this.damage = (float) this.orc.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        this.setControls(EnumSet.of(Control.TARGET, Control.MOVE));
    }

    //FASTER ATTACK SETTING
    private static final float MAX_ATTACK_SPEED_INCREASE = 0.85f; // Maximum attack speed increase (%)
    private static final float ATTACK_SPEED_INCREASE_PER_ATTACK = 0.08f; // Increase per attack (%)
    private float currentAttackSpeedIncrease = 0.0f;
    private void increaseAttackSpeed() {
        currentAttackSpeedIncrease += ATTACK_SPEED_INCREASE_PER_ATTACK;
        currentAttackSpeedIncrease = Math.min(currentAttackSpeedIncrease, MAX_ATTACK_SPEED_INCREASE); // Clamp to maximum
    }
    private void resetAttackSpeed() {
        currentAttackSpeedIncrease = 0.0f;
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
        this.orc.setAttacking(false);
        this.orc.setTrigger(false);
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
        this.fastAttackCD = Math.max(this.fastAttackCD - 1, 0);
        if (this.target == null) return;
        this.randomizer = Math.random();
        double distanceToTarget = this.orc.distanceTo(this.target);
        int attackIncreaseCD = (int) (initialcooldown * (1 - currentAttackSpeedIncrease));

        if (condition == 0 && currentAttackSpeedIncrease == MAX_ATTACK_SPEED_INCREASE) {
            this.condition = 1;
            this.fastAttackCD = 140;

        } else if (this.condition == 1 && fastAttackCD <= 1) {
            this.condition = 0;
            this.resetAttackSpeed();
        }

        if (currentAttackSpeedIncrease >= 0.80f) {
            this.orc.setAttackName("animation.troll.attack3");
        } else this.orc.setAttackName("animation.troll.attack2");


        if (distanceToTarget > 9) {
            this.orc.setTrigger(false);
            Path path = this.orc.getNavigation().findPathTo(target, 3);
            this.orc.getNavigation().startMovingAlong(path, this.speed);
        }

        if(this.attackCooldown <= attackIncreaseCD && this.attackCooldown >= attackIncreaseCD-throwattackcd) {
            this.orc.getNavigation().stop();
            this.orc.lookAtEntity(target, 30, 30);
            this.orc.getMoveControl().strafeTo(-0.1f, 0f);
        }

        if (distanceToTarget <= 9 && this.attackCooldown <= 0) {
            this.attackCooldown = attackIncreaseCD;
            this.orc.setTrigger(true);

        } else if (distanceToTarget <= 9 && this.attackCooldown == attackIncreaseCD - throwattackcd) {

            World world = this.orc.getWorld();
            ItemStack arrowStack = this.orc.getProjectileType(new ItemStack(ModItems.TROLL_THROWABLE_ITEM));
            PersistentProjectileEntity arrow = createThrow(world, arrowStack);
            arrow.setOwner(this.orc);

            double x = this.target.getX() - this.orc.getX();
            double y = this.target.getBodyY(0.6) - arrow.getY();
            double z = this.target.getZ() - this.orc.getZ();
            double f = MathHelper.sqrt((float) (x * x + z * z));
            arrow.setVelocity(x, y + f * 0.1, z, 1.1F, 0.1F);
            world.spawnEntity(arrow);
            this.orc.playSound(SoundEvents.ENTITY_SNOWBALL_THROW, 1.0F, 1.0F);

            if (currentAttackSpeedIncrease <= 0.60f) {
                MobMoveUtil.moveAwayFromTarget(orc, target, 5, speed);
            }

        } else if (this.attackCooldown == attackIncreaseCD - throwattackcd - 3) {
            this.orc.setTrigger(false);

        } else if (attackCooldown <= 1) {
            this.orc.setTrigger(false);
            this.increaseAttackSpeed(); //ATTACK INCREASE HERE

        }
    }

    private PersistentProjectileEntity createThrow(World world, ItemStack arrowStack) {
        // from TrollThrowableEntity, different dmg if its player item. check on item
        return new TrollThrowableEntity(world, this.orc, damage);
    }
}
