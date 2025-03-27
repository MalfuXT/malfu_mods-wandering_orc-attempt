package malfu.wandering_orc.entity.custom;

import malfu.wandering_orc.entity.ModEntities;
import malfu.wandering_orc.entity.ai.CrossOrcRevengeGoal;
import malfu.wandering_orc.entity.ai.OrcWarlockAttackSummonGoal;
import malfu.wandering_orc.entity.ai.wander.OrcFollowLeaderGoal;
import malfu.wandering_orc.sound.ModSounds;
import malfu.wandering_orc.util.config.ModBonusHealthConfig;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.PatrolEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.ClientUtils;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrcWarlockEntity extends OrcGroupEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int animationTick;
    private int idleCondition = 0;
    private static final int COOLDOWN = 500;
    private int cooldown;

    public OrcWarlockEntity(EntityType<? extends OrcGroupEntity> entityType, World world) {
        super(entityType, world);
    }

    public static final TrackedData<Boolean> ATKTIMING = DataTracker.registerData(OrcWarlockEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> SUMMON = DataTracker.registerData(OrcWarlockEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<String> ATKVARI = DataTracker.registerData(OrcWarlockEntity.class, TrackedDataHandlerRegistry.STRING);

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ATKTIMING, false);
        this.dataTracker.startTracking(ATKVARI, "attack");
        this.dataTracker.startTracking(SUMMON, false);
    }

    public boolean isSummonCD() {return (Boolean)this.dataTracker.get(SUMMON);}
    public void setSummonCD(boolean summon) {this.dataTracker.set(SUMMON, summon);}

    public boolean isTrigger() {
        return (Boolean)this.dataTracker.get(ATKTIMING);
    }
    public void setTrigger(boolean atktiming) {
        this.dataTracker.set(ATKTIMING, atktiming);
    }

    public void setAttackName(String attackName) {this.dataTracker.set(ATKVARI, attackName);}
    public String getAttackName() {return (String)this.dataTracker.get(ATKVARI);}


    public static DefaultAttributeContainer.Builder setAttributes() {
        return OrcGroupEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.20f)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 21.0D + ModBonusHealthConfig.orcWarlockBonusHealth)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 10.0f)
                .add(EntityAttributes.GENERIC_ARMOR, 1.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.1f);
    }

    public void tick() {
        super.tick();

        if (isSummonCD()) { //TRUE IS ON COOLDOWN
            this.cooldown--;
        }

        if (this.cooldown <= 0) { //FALSE IS READY
            this.setSummonCD(false);
            this.cooldown = COOLDOWN;
        }
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new OrcWarlockAttackSummonGoal(this, 1.2f));
        this.goalSelector.add(3, new OrcFollowLeaderGoal(this));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(6, new LookAroundGoal(this));
        this.targetSelector.add(1, new CrossOrcRevengeGoal(this, OrcGroupEntity.class).setGroupRevenge());
        this.targetSelector.add(4, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, MerchantEntity.class, false));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, PatrolEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, AbstractPiglinEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, ZombieEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, LivingEntity.class, 10, true, false, OrcGroupEntity.TARGET_ORC_ENEMIES));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
        controllers.add(new AnimationController<>(this, "attack", 0, this::attackPredicate).setSoundKeyframeHandler((event) -> {
            // Get the sound instruction from the keyframe
            String soundInstruction = event.getKeyframeData().getSound();

            // Check the instruction and play the corresponding sound
            if (soundInstruction != null) {
                PlayerEntity player = ClientUtils.getClientPlayer();
                if (player != null) {
                    switch (soundInstruction) {
                        case "charge" -> this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(),
                                ModSounds.FIREBALL_CHARGE, this.getSoundCategory(), 1.0F, 1.0F);
                        case "summon_charge" -> this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(),
                                ModSounds.SUMMON_CHARGE, this.getSoundCategory(), 1.0F, 1.0F);
                    }
                }
            }
        }));
    }

    private <E extends GeoAnimatable> PlayState attackPredicate(AnimationState<E> event) {
        if (this.isTrigger() && event.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            event.getController().setAnimation(RawAnimation.begin().then(this.getAttackName(), Animation.LoopType.PLAY_ONCE));
        }
        event.getController().forceAnimationReset();
        return PlayState.CONTINUE;
     }


    private <E extends GeoAnimatable> PlayState predicate(AnimationState<E> event) {
        this.animationTick = Math.max(this.animationTick - 1, 0);

        if(this.isAttacking() || this.isChase()){
            event.getController().setAnimationSpeed(1.3f);
        } else event.getController().setAnimationSpeed(1.0f);

        if (event.isMoving()) {
            event.getController().setAnimation(RawAnimation.begin().then("walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if (this.animationTick == 0) {
            this.idleCondition = Math.random() < 0.5 ? 1 : 2;
        }

        if (idleCondition == 1) {
            event.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        } else {
            event.getController().setAnimation(RawAnimation.begin().then("idle2", Animation.LoopType.LOOP));
        }

        this.animationTick = 200;

        return PlayState.CONTINUE;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.ORC_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.ORC_HURT;
    }

    @Override
    public float getSoundPitch() {
        return 1.2F;
    }

    public int getXpToDrop() {
        return 5;
    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    //SUMMON FIRELINK HERE
    private BlockPos getPositionInFront(LivingEntity summoner, double distance) {
        // Get the summoner's yaw (rotation) in radians
        float yaw = summoner.getYaw();
        double radians = Math.toRadians(yaw);

        // Calculate the offset in front of the summoner
        double xOffset = -Math.sin(radians) * distance;
        double zOffset = Math.cos(radians) * distance;

        // Calculate the new position
        double x = summoner.getX() + xOffset;
        double y = summoner.getY();
        double z = summoner.getZ() + zOffset;

        return new BlockPos((int) x, (int) y, (int) z);
    }

    private List<BlockPos> getPositionsInArea(BlockPos centerPos) {
        List<BlockPos> positions = new ArrayList<>();

        // Define a 3x3 area around the center position
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                positions.add(centerPos.add(x, 0, z)); // Add positions at the same Y level
            }
        }

        return positions;
    }

    private BlockPos findRandomNonBlockedPosition(World world, List<BlockPos> positions) {
        // Shuffle the list to randomize the order
        Collections.shuffle(positions);

        // Iterate through the shuffled positions
        for (BlockPos pos : positions) {
            if (!isPositionBlocked(world, pos)) {
                return pos; // Return the first non-blocked position
            }
        }

        return null; // No valid position found
    }

    private boolean isPositionBlocked(World world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        return blockState.isSolidBlock(world, pos); // Check if the block is solid
    }

    public void summonFirelink() {
        FirelinkEntity firelink = new FirelinkEntity(ModEntities.FIRELINK, this.getWorld());
        firelink.setSummoner(this); // Set the Orc Warlock as the summoner

        // Calculate the position in front of the summoner
        BlockPos frontPos = getPositionInFront(this, 2.0); // 2 blocks in front

        // Define a 3x3 area around the front position
        List<BlockPos> positionsInArea = getPositionsInArea(frontPos);

        // Find a random, non-blocked position in the area
        BlockPos spawnPos = findRandomNonBlockedPosition(this.getWorld(), positionsInArea);

        if (spawnPos == null) {
            // Spawn at the summoner's position if no valid position is found
            firelink.setPosition(this.getX(), this.getY(), this.getZ());
        } else {
            // Spawn at the random position in the area
            firelink.setPosition(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
        }

        // Spawn the FirelinkEntity in the world
        this.getWorld().spawnEntity(firelink);
    }
    //ENDS HERE FOR SUMMON
}