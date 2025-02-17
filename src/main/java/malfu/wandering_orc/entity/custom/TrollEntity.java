package malfu.wandering_orc.entity.custom;

import malfu.wandering_orc.entity.ai.CrossOrcRevengeGoal;
import malfu.wandering_orc.entity.ai.OrcArcherMeleeBowGoal;
import malfu.wandering_orc.entity.ai.OrcWarriorMeleeGoal;
import malfu.wandering_orc.entity.ai.TrollThrowGoal;
import malfu.wandering_orc.entity.ai.wander.OrcFollowLeaderGoal;
import malfu.wandering_orc.sound.ModSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class TrollEntity extends OrcGroupEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int animationTick;
    private int idleCondition = 0;

    public TrollEntity(EntityType<? extends OrcGroupEntity> entityType, World world) {
        super(entityType, world);
    }

    public static final TrackedData<Boolean> ATKTIMING =
            DataTracker.registerData(TrollEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public static final TrackedData<String> ATKVARI =
            DataTracker.registerData(TrollEntity.class, TrackedDataHandlerRegistry.STRING);

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ATKTIMING, false);
        this.dataTracker.startTracking(ATKVARI, "animation.troll.attack");
    }

    public boolean isTrigger() {
        return (Boolean)this.dataTracker.get(ATKTIMING);
    }
    public void setTrigger(boolean atktiming) {
        this.dataTracker.set(ATKTIMING, atktiming);
    }

    public void setAttackName(String attackName) {this.dataTracker.set(ATKVARI, attackName);}
    public String getAttackName() {return (String)this.dataTracker.get(ATKVARI);}

    public static DefaultAttributeContainer.Builder setAttributes() {
        return OrcGroupEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25f)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 28.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0f)
                .add(EntityAttributes.GENERIC_ARMOR, 1.0f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.1f);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(3, new TrollThrowGoal(this, 1.1));
        this.goalSelector.add(4, new OrcFollowLeaderGoal(this));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
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
        controllers.add(new AnimationController<>(this, "attack", 0, this::attackPredicate));
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


        if (event.isMoving()) {
            event.getController().setAnimation(RawAnimation.begin().then("animation.troll.walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if (this.animationTick == 0) {
            this.idleCondition = Math.random() < 0.5 ? 1 : 2;
        }

        if (idleCondition == 1) {
            event.getController().setAnimation(RawAnimation.begin().then("animation.troll.idle", Animation.LoopType.LOOP));
        } else {
            event.getController().setAnimation(RawAnimation.begin().then("animation.troll.idle2", Animation.LoopType.LOOP));
        }

        this.animationTick = 200;

        return PlayState.CONTINUE;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.TROLL_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.TROLL_HURT;
    }



    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}