package malfu.wandering_orc.entity.ai;

import malfu.wandering_orc.entity.custom.OrcArcherEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.predicate.entity.EntityPredicates;

public class OrcArcherAttackGoal extends MeleeAttackGoal {

    private final OrcArcherEntity entity;
    private int cooldownAttack;


    public OrcArcherAttackGoal(PathAwareEntity mob, double speed, boolean pauseWhenMobIdle) {
        super(mob, speed, pauseWhenMobIdle);
        entity = ((OrcArcherEntity) mob);
    }

    private boolean isEnemyClose(LivingEntity cEnemy) {
        return this.entity.distanceTo(cEnemy) <= 15.0f;
    }

    protected void doAttack(LivingEntity cEnemy) {
        this.mob.tryAttack(cEnemy);
    }

    @Override
    public void stop() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (!EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(livingEntity)) {
            this.mob.setTarget(null);
        }

        this.mob.setAttacking(false);
        this.mob.getNavigation().stop();
        this.entity.setTrigger(false);
    }


    @Override
    public void tick() {
        super.tick();
        LivingEntity livingEntity = this.mob.getTarget();
        double d = this.mob.getSquaredDistanceToAttackPosOf(livingEntity);
        if(livingEntity != null && livingEntity.isAlive()){
            this.mob.getLookControl().lookAt(livingEntity, 15.0F, 15.0F);
            this.attack(livingEntity, d);
        } else {
            this.stop();
        }
    }

    @Override
    protected void attack(LivingEntity cEnemy, double sDistance) {
        double d = getSquaredMaxAttackDistance(cEnemy);
        this.cooldownAttack = Math.max(this.cooldownAttack - 1, 0);

        if(sDistance <= d && this.cooldownAttack == 0) {
            this.cooldownAttack = 80;

        } else if (cooldownAttack <= 80 && cooldownAttack >= 79) {
            this.entity.setTrigger(true);

        } else if (sDistance <= d && this.cooldownAttack <= 65 && this.cooldownAttack >= 64) {
            this.mob.getLookControl().lookAt(cEnemy.getX(), cEnemy.getEyeY(), cEnemy.getZ());
            this.doAttack(cEnemy);

        } else if (cooldownAttack <= 60) {
            this.entity.setTrigger(false);
        }
    }

    @Override
    protected double getSquaredMaxAttackDistance(LivingEntity entity) {
        return (double) (3.0F + entity.getWidth());
    }
}