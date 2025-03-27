package malfu.wandering_orc.entity.ai.wander;

import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class WanderAroundReallyFarGoal extends WanderAroundGoal {

    public WanderAroundReallyFarGoal(PathAwareEntity mob, double speed, int chance) {
        super(mob, speed, chance);
    }

    public WanderAroundReallyFarGoal(PathAwareEntity mob, double speed) {
        super(mob, speed);
    }

    @Nullable
    protected Vec3d getWanderTarget() {
        return NoPenaltyTargeting.find(this.mob, 100, 70);
    }
}
