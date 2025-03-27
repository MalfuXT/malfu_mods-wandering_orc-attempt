package malfu.wandering_orc.entity.custom;

import com.eliotlash.mclib.math.functions.classic.Mod;
import malfu.wandering_orc.entity.ai.FirelinkAttackBehaviour;
import malfu.wandering_orc.entity.ai.SummonerFollowGoal;
import malfu.wandering_orc.entity.ai.TargetSummonerTargetGoal;
import malfu.wandering_orc.sound.ModSounds;
import malfu.wandering_orc.util.ParticleUtil;
import malfu.wandering_orc.util.config.ModBonusHealthConfig;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.EntityView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.ClientUtils;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;


public class FirelinkEntity extends TameableEntity implements GeoEntity {
    private final int firelinkTimetoLive = 400;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private UUID summonerUUID; // Store the summoner's UUID
    private LivingEntity summoner; // Store the summoner entity
    private boolean isSpawning = true;
    private boolean isDesummoning = false;

    public FirelinkEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public @Nullable PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    public static final TrackedData<Boolean> ATKTIMING =
            DataTracker.registerData(FirelinkEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<String> ATKVARI =
            DataTracker.registerData(FirelinkEntity.class, TrackedDataHandlerRegistry.STRING);


    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ATKTIMING, false);
        this.dataTracker.startTracking(ATKVARI, "attack");
    }

    public boolean isTrigger() {
        return (Boolean)this.dataTracker.get(ATKTIMING);
    }
    public void setTrigger(boolean atktiming) {
        this.dataTracker.set(ATKTIMING, atktiming);
    }

    public void setAttackName(String attackName) {this.dataTracker.set(ATKVARI, attackName);}
    public String getAttackName() {return (String)this.dataTracker.get(ATKVARI);}

    public boolean isSpawning() {
        return this.isSpawning;
    }

    public boolean isDesummoning() {
        return this.isDesummoning;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.age == 1) {
            ParticleUtil.generateSummonParticle(this);
            this.playSound(ModSounds.SUMMON, 1.0f, 1.0f);
            this.playSound(ModSounds.FIRELINK_AMBIENCE, 0.5f, 1.0f);
        }
        if (this.age <= 20 || this.age >= firelinkTimetoLive) {
            this.setAiDisabled(true);
        } else this.setAiDisabled(false);
        // Disable the spawning state after the animation finishes
        if (this.isSpawning && this.age >= 20) {
            this.isSpawning = false;
        }

        if (this.age == firelinkTimetoLive) {
            this.isDesummoning = true;
            ParticleUtil.generateSummonParticle(this);
            this.playSound(ModSounds.SUMMON, 1.0f, 0.2f);
            this.playSound(ModSounds.FIRELINK_AMBIENCE, 0.5f, 1.0f);
        }

        // Discard the entity after 320 ticks
        if (this.age >= firelinkTimetoLive + 20) {
            this.discard();
        }
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return OrcGroupEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25f)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 22.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0f)
                .add(EntityAttributes.GENERIC_ARMOR, 0.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.1f);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new FirelinkAttackBehaviour(this, 1.4f));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(4, new FollowOwnerGoal(this, 1.4f, 10.0F, 2.0F, false));
        this.goalSelector.add(4, new SummonerFollowGoal(this, 1.4f, 10.0F, 2.0F, false));
        this.goalSelector.add(6, new LookAroundGoal(this));
        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
        this.targetSelector.add(2, new TargetSummonerTargetGoal(this));
    }


    // Custom method to set the owner when spawned
    // Custom method to set the owner when spawned
    public void setSummoner(LivingEntity summoner) {
        this.summoner = summoner;
        this.summonerUUID = summoner.getUuid(); // Store the summoner's UUID
    }

    // Custom method to get the owner
    public LivingEntity getSummoner() {
        if (this.summoner == null && this.summonerUUID != null && this.getWorld() != null) {
            // Look up the summoner by UUID if it hasn't been loaded yet
            Entity entity = ((ServerWorld) this.getWorld()).getEntity(this.summonerUUID);
            if (entity instanceof LivingEntity) {
                this.summoner = (LivingEntity) entity;
            }
        }
        return this.summoner;
    }

    @Override
    public void setOwner(PlayerEntity player) {
        this.setTamed(true);
        this.setOwnerUuid(player.getUuid());
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);

        // Save the summoner's UUID if it exists
        if (this.summoner != null) {
            nbt.putUuid("SummonerUUID", this.summoner.getUuid());
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        // Load the summoner's UUID if it exists
        if (nbt.containsUuid("SummonerUUID")) {
            UUID summonerUUID = nbt.getUuid("SummonerUUID");
            this.summonerUUID = summonerUUID; // Store the UUID for later lookup
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
        controllers.add(new AnimationController<>(this, "attack", 0, this::attackPredicate).setSoundKeyframeHandler((event) -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null) {
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), ModSounds.FIRELINK_SWING, this.getSoundCategory(),0.5F, 1.0f);
            }
        }));
    }

    private <E extends GeoAnimatable> PlayState attackPredicate(AnimationState<E> event) {
        if (this.isTrigger() && (!this.isSpawning()||!this.isDesummoning())
                && event.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            event.getController().setAnimation(RawAnimation.begin().then(this.getAttackName(), Animation.LoopType.PLAY_ONCE));
        }
        event.getController().forceAnimationReset();
        return PlayState.CONTINUE;
     }


    private <E extends GeoAnimatable> PlayState predicate(AnimationState<E> event) {
        if (this.isSpawning()) {
            event.getController().setAnimation(RawAnimation.begin().then("summoned", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        }

        if (this.isDesummoning()) {
            event.getController().setAnimation(RawAnimation.begin().then("desummon", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        }

        if (event.isMoving() && (!this.isSpawning()||!this.isDesummoning())) {
            event.getController().setAnimation(RawAnimation.begin().then("walk_transition", Animation.LoopType.PLAY_ONCE).
                    thenLoop("walk"));
            return PlayState.CONTINUE;

        } else if (!this.isSpawning()||!this.isDesummoning()) {
            event.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        return PlayState.CONTINUE;
    }

    public boolean hurtByWater() {
        return true;
    }

    @Override
    public boolean isFireImmune() {
        return true; // Make the entity immune to fire and lava
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.FIRELINK_AMBIENCE;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.FIRELINK_AMBIENCE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.FIRELINK_HURT;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
    public int getXpToDrop() {
        return 0;
    }

    @Override
    public EntityView method_48926() {
        return super.getWorld(); //NEED TO ADD THIS. (WAS NULL). I DONT UNDERSTAND
    }
}