package malfu.wandering_orc.entity.custom;


import malfu.wandering_orc.entity.ModEntities;
import malfu.wandering_orc.util.ModTags;
import malfu.wandering_orc.util.config.SpawnConfig;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import org.jetbrains.annotations.Nullable;


import java.util.function.Predicate;

public class OrcGroupEntity extends AnimalEntity {

    protected OrcGroupEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }
    private LivingEntity leader;
    public static final TrackedData<Boolean> FLEE = DataTracker.registerData(OrcGroupEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> CHASE = DataTracker.registerData(OrcGroupEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Float> SPEED = DataTracker.registerData(OrcGroupEntity.class, TrackedDataHandlerRegistry.FLOAT);

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(FLEE, false);
        this.dataTracker.startTracking(CHASE, false);
        this.dataTracker.startTracking(SPEED, 0f);
    }

    @Override
    public void tick() {
        super.tick();

        // Only run this logic on the server
        if (!this.getWorld().isClient()) {
            double speed = this.getMoveControl().getSpeed();

            // Synchronize the speed to the client
            this.setMovementSpeedForClient((float) speed);
        }
    }

    public void setMovementSpeedForClient(float speed) {
        this.dataTracker.set(SPEED, speed);
    }

    public boolean isChase() {
        return (Boolean)this.dataTracker.get(CHASE);
    }
    public void setChase(boolean chase) {
        this.dataTracker.set(CHASE, chase);
    }
    public boolean isFleeing() {
        return (Boolean)this.dataTracker.get(FLEE);
    }
    public void setFleeing(boolean flee) {
        this.dataTracker.set(FLEE, flee);
    }


    public LivingEntity getLeader() {
        return this.leader;
    }
    public void setLeader(LivingEntity leader) {
        this.leader = leader;
    }

    public String getTeamOrc() {
        return "orc_team"; // orc Grouping for projectiles
    }

    public static final Predicate<LivingEntity> TARGET_ORC_ENEMIES = (livingEntity) -> {
        if (!(livingEntity instanceof MobEntity)) {  // Check if it's a MobEntity
            return false;
        }

        // Check if the entity is alive
        if (!livingEntity.isAlive()) {
            return false;
        }

        // Check if the entity's type is in the validated list
        return ModTags.VALID_ORC_ENEMIES.contains(livingEntity.getType());
    };

    @Override
    public int getMinAmbientSoundDelay() {
        return 240;
    }

    public static boolean canMobSpawnWithRate(EntityType<? extends MobEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        // 1. Always allow spawner-based spawning
        if (spawnReason == SpawnReason.SPAWNER) {
            return true;
        }

        // 2. Check if the block below allows spawning
        BlockPos blockPos = pos.down();
        if (!world.getBlockState(blockPos).allowsSpawning(world, blockPos, type)) {
            return false;
        }

        // 3. Get day/night status (convert WorldAccess to World)
        if (world instanceof World realWorld) {
            boolean isNight = realWorld.isNight();

            float spawnChance = isNight ? 0.01f : SpawnConfig.naturalSpawnRate;

            return random.nextFloat() < spawnChance;
        }

        // Fallback if we can't get World instance
        return random.nextFloat() < SpawnConfig.naturalSpawnRate;
    }


    @Override
    protected boolean isDisallowedInPeaceful() {
        return true;
    }

    @Override
    public @Nullable PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }
}
