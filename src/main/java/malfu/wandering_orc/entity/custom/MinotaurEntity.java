package malfu.wandering_orc.entity.custom;

import malfu.wandering_orc.entity.ai.CrossOrcRevengeGoal;
import malfu.wandering_orc.entity.ai.MinotaurMeleeGoal;
import malfu.wandering_orc.entity.ai.OrcChampionMeleeGoal;
import malfu.wandering_orc.entity.ai.OrcWarriorMeleeGoal;
import malfu.wandering_orc.entity.ai.wander.OrcFollowLeaderGoal;
import malfu.wandering_orc.sound.ModSounds;
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
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.ClientUtils;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MinotaurEntity extends OrcGroupEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int animationTick = 0;
    private int idleCondition = 0;
    private double randomizer;

    public MinotaurEntity(EntityType<? extends OrcGroupEntity> entityType, World world) {
        super(entityType, world);
    }

    public static final TrackedData<Boolean> ATKTIMING =
            DataTracker.registerData(MinotaurEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public static final TrackedData<String> ATKVARI =
            DataTracker.registerData(MinotaurEntity.class, TrackedDataHandlerRegistry.STRING);

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ATKTIMING, false);
        this.dataTracker.startTracking(ATKVARI, "animation.minotaur.normal_attack");
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
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25f)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 90.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 18.0f)
                .add(EntityAttributes.GENERIC_ARMOR, 8.0f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5f);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new MinotaurMeleeGoal(this, 1.2));
        this.goalSelector.add(3, new OrcFollowLeaderGoal(this));
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
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, LivingEntity.class, 10, true, false, OrcGroupEntity.TARGET_ORC_ENEMIES));
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate).setSoundKeyframeHandler((event) -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null) {
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_COW_STEP, this.getSoundCategory(),0.5F, 0.8f);
            }
        }));
        controllers.add(new AnimationController<>(this, "attack", 0, this::attackPredicate).setSoundKeyframeHandler((event) -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null) {
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), ModSounds.SWING_SOUND, this.getSoundCategory(),1.0F, 1.0f);
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


        if (event.isMoving()) {
            event.getController().setAnimation(RawAnimation.begin().then("animation.minotaur.walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if (this.animationTick == 0) {
            this.idleCondition = Math.random() < 0.5 ? 1 : 2;
        }

        if (idleCondition == 1) {
            event.getController().setAnimation(RawAnimation.begin().then("animation.minotaur.idle", Animation.LoopType.LOOP));
        } else {
            event.getController().setAnimation(RawAnimation.begin().then("animation.minotaur.idle2", Animation.LoopType.LOOP));
        }

        this.animationTick = 200;

        return PlayState.CONTINUE;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.MINO_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.MINO_HURT;
    }



    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}