package malfu.wandering_orc.entity.ai;

import malfu.wandering_orc.entity.custom.FirelinkEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;

import java.util.EnumSet;

public class TargetSummonerTargetGoal extends TrackTargetGoal {
    private final FirelinkEntity firelink;
    private LivingEntity leaderTarget;

    public TargetSummonerTargetGoal(FirelinkEntity firelink) {
        super(firelink, false);
        this.firelink = firelink;
        this.setControls(EnumSet.of(Goal.Control.TARGET));
    }

    @Override
    public boolean canStart() {
        LivingEntity leader = this.firelink.getSummoner();
        if (leader == null) {
            return false;
        }

        // Directly get the leader's target
        if (leader instanceof MobEntity) {
            this.leaderTarget = ((MobEntity) leader).getTarget();
        }

        if (this.leaderTarget == null) {
            return false; // No target to follow
        }

        // Check if the leader's target is valid
        return this.canTrack(this.leaderTarget, TargetPredicate.DEFAULT);
    }

    @Override
    public void start() {
        this.mob.setTarget(this.leaderTarget);
        super.start();
    }
}
