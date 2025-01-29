package malfu.wandering_orc.entity.ai;

import malfu.wandering_orc.entity.custom.OrcWarriorEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.predicate.entity.EntityPredicates;

public class OrcWarriorAttackGoal extends MeleeAttackGoal {

    private final OrcWarriorEntity entity;
    private int cooldownAttack;
    double randomizer;
    private int attackCondition = 0;


    public OrcWarriorAttackGoal(PathAwareEntity mob, double speed, boolean pauseWhenMobIdle) {
        super(mob, speed, pauseWhenMobIdle);
        entity = ((OrcWarriorEntity) mob);
    }

    private boolean isEnemyClose(LivingEntity cEnemy) {
        return this.entity.distanceTo(cEnemy) <= 15.0f;
    }


    protected void doAttack(LivingEntity cEnemy) {
        this.mob.tryAttack(cEnemy);
    }

    public boolean shouldRunEveryTick() {
        return true;
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
    protected void attack(LivingEntity cEnemy, double sDistance) {
        double d = getSquaredMaxAttackDistance(cEnemy);
        this.cooldownAttack = Math.max(this.cooldownAttack - 1, 0);
        this.randomizer = Math.random();

        if(this.attackCondition == 0) {
            if(this.randomizer < 0.9) {
                if(this.randomizer < 0.5) {
                    this.entity.setAttackName("animation.orc_warrior.attack_running");
                    this.attackCondition = 1;
                } else {
                    this.entity.setAttackName("animation.orc_warrior.attack_running2");
                    this.attackCondition = 2;
                }
            } else {
                this.entity.setAttackName("animation.orc_warrior.attack_combo");
                this.attackCondition = 3;
            }
        }

        if(this.attackCondition == 1 || this.attackCondition == 2) {

            if(sDistance <= d && this.cooldownAttack == 0) {
                this.cooldownAttack = 40;


            } else if (this.cooldownAttack <= 40 && this.cooldownAttack >= 39) {
                this.entity.setTrigger(true);

            } else if (sDistance <= d && this.cooldownAttack <= 35 && this.cooldownAttack >= 34) {
                this.doAttack(cEnemy);

            } else if (this.cooldownAttack <= 3) {
                this.attackCondition = 0;

            } else if (this.cooldownAttack <= 28) {
                this.entity.setTrigger(false);
            }

        }
        if (this.attackCondition == 3) {

            if(sDistance <= d && this.cooldownAttack == 0) {
                this.cooldownAttack = 40;

            } else if (this.cooldownAttack <= 40 && this.cooldownAttack >= 39) {
                this.entity.setTrigger(true);
                this.entity.speed -= 1.3f;

            } else if (sDistance <= d && this.cooldownAttack <= 25 && this.cooldownAttack >= 24) {
                this.doAttack(cEnemy);

            } else if (sDistance <= d && this.cooldownAttack <= 19 && this.cooldownAttack >= 18) {
                this.doAttack(cEnemy);

            } else if (this.cooldownAttack <= 3) {
                this.attackCondition = 0;

            } else if (this.cooldownAttack <= 13) {
                this.entity.setTrigger(false);
                this.entity.speed += 1.3f;
            }
        }
    }

    @Override
    protected double getSquaredMaxAttackDistance(LivingEntity entity) {
        return (double) (2.7F + entity.getWidth());
    }
}