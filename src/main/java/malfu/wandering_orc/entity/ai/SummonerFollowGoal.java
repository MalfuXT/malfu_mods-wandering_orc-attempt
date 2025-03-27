package malfu.wandering_orc.entity.ai;

import malfu.wandering_orc.entity.custom.FirelinkEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;

public class SummonerFollowGoal extends Goal {
    private final MobEntity mob;
    private final double speed;
    private final float minDistance;
    private final float maxDistance;

    public SummonerFollowGoal(MobEntity mob, double speed, float minDistance, float maxDistance, boolean pauseWhenIdle) {
        this.mob = mob;
        this.speed = speed;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
    }

    @Override
    public boolean canStart() {
        LivingEntity summoner = ((FirelinkEntity) this.mob).getSummoner();
        return summoner != null && this.mob.squaredDistanceTo(summoner) > (double) (this.minDistance * this.minDistance);
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity summoner = ((FirelinkEntity) this.mob).getSummoner();
        return summoner != null && this.mob.squaredDistanceTo(summoner) > (double) (this.maxDistance * this.maxDistance);
    }

    @Override
    public void start() {
        LivingEntity summoner = ((FirelinkEntity) this.mob).getSummoner();
        if (summoner != null) {
            this.mob.getNavigation().startMovingTo(summoner, this.speed);
        }
    }
}
