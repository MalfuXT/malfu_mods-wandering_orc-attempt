package malfu.wandering_orc.entity.custom;


import malfu.wandering_orc.util.ModTags;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.*;


import java.util.function.Predicate;

public class OrcGroupEntity extends HostileEntity {

    protected OrcGroupEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    private LivingEntity leader;
    public LivingEntity getLeader() {
        return this.leader;
    }
    public void setLeader(LivingEntity leader) {
        this.leader = leader;
    }

    public static final Predicate<LivingEntity> TARGET_ORC_ENEMIES = (livingEntity) -> {
        if (!(livingEntity instanceof MobEntity)) {  // Check if it's a MobEntity
            return false;
        }

        World world = livingEntity.getWorld();
        if (!(world instanceof ServerWorld serverWorld)) { // Check if it's a ServerWorld
            return false;
        }

        return livingEntity.getType().isIn(ModTags.EntityEnemies.ORC_ENEMIES) && livingEntity.isAlive();
    };

    @Override
    public boolean canSpawn(WorldAccess world, SpawnReason spawnReason) {
        // Allow spawning in any light level and on solid ground
        return world.getBlockState(this.getBlockPos().down()).isSolidBlock(world, this.getBlockPos().down());
    }
}
