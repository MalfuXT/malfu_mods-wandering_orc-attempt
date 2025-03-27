package malfu.wandering_orc.entity.ai;

import malfu.wandering_orc.entity.custom.OrcGroupEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.Box;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class CrossOrcRevengeGoal extends RevengeGoal {

    private final Class<?>[] noRevengeTypes;

    public CrossOrcRevengeGoal(OrcGroupEntity mob, Class<?>... noRevengeTypes) {
        super(mob, noRevengeTypes);
        this.noRevengeTypes = noRevengeTypes;
        this.setControls(EnumSet.of(Goal.Control.TARGET));
    }

    protected void callSameTypeForRevenge() {
        MobEntity mob = this.mob;
        double d = this.getFollowRange();
        Box box = Box.from(mob.getPos()).expand(d, 10.0D, d);

        List<MobEntity> list = mob.getWorld().getEntitiesByClass(MobEntity.class, box, (mobEntity) -> {
            if (mobEntity == mob) {
                return false;
            } else if (!mobEntity.isAlive()) {
                return false;
            } else if (!(mobEntity instanceof OrcGroupEntity)) { // This is the crucial check
                return false;
            } else if (mobEntity.isTeammate(mob.getAttacker())){
                return false;
            } else {
                return EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(mobEntity);
            }
        });

        for (MobEntity mobEntity : list) {
            this.setMobEntityTarget(mobEntity, this.mob.getAttacker());
        }
    }

    protected void setMobEntityTarget(MobEntity mob, LivingEntity target) {
        mob.setTarget(target);
    }
}

