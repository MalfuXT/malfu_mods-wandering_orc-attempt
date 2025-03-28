package malfu.wandering_orc.entity.custom;

import malfu.wandering_orc.entity.ai.CrossOrcRevengeGoal;
import malfu.wandering_orc.entity.ai.OrcWarriorMeleeGoal;
import malfu.wandering_orc.entity.ai.wander.OrcFollowLeaderGoal;
import malfu.wandering_orc.sound.ModSounds;
import malfu.wandering_orc.util.config.ModBonusHealthConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.ClientUtils;
import software.bernie.geckolib.util.GeckoLibUtil;

public class OrcWarriorEntity extends OrcGroupEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int animationTick = 0;
    private int idleCondition = 0;
    private double randomizer;

    public OrcWarriorEntity(EntityType<? extends OrcGroupEntity> entityType, World world) {
        super(entityType, world);
    }

    public static final TrackedData<Boolean> ATKTIMING =
            DataTracker.registerData(OrcWarriorEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public static final TrackedData<String> ATKVARI =
            DataTracker.registerData(OrcWarriorEntity.class, TrackedDataHandlerRegistry.STRING);

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ATKTIMING, false);
        this.dataTracker.startTracking(ATKVARI, "animation.orc_warrior.attack_running");
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
        return OrcGroupEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.20f)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 28.0D + ModBonusHealthConfig.orcWarriorBonusHealth)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0f)
                .add(EntityAttributes.GENERIC_ARMOR, 6.0f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.3f);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new OrcWarriorMeleeGoal(this, 1.55));
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
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, LivingEntity.class, 10, true, false, OrcGroupEntity.TARGET_ORC_ENEMIES));
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
        controllers.add(new AnimationController<>(this, "attackingstance", 0, this::attackstancePredicate));
        controllers.add(new AnimationController<>(this, "attack", 0, this::attackPredicate).setSoundKeyframeHandler((event) -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null) {
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), ModSounds.SWING_LIGHT, this.getSoundCategory(),0.5F, 2.0f);
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
        this.randomizer = Math.random();

        float speed = ((OrcGroupEntity) event.getAnimatable()).getDataTracker().get(SPEED);
        if (speed >= 1.2f) {
            event.getController().setAnimationSpeed(1.4f);
        } else {
            event.getController().setAnimationSpeed(1.0f);
        }

        if(idleCondition == 0) {
            if(randomizer < 0.5) {
                idleCondition = 1;
            } else {
                idleCondition = 2;
            }
        }
        if(idleCondition == 1) {
            if (event.isMoving()) {
                event.animationTick = 80;
                event.getController().setAnimation(RawAnimation.begin().then("animation.orc_warrior.walk2", Animation.LoopType.LOOP));
                return PlayState.CONTINUE;
            } else if (animationTick == 0) {
                this.idleCondition = 0;
            }
        }
        if(idleCondition == 2) {
            if (event.isMoving()) {
                event.animationTick = 80;
                event.getController().setAnimation(RawAnimation.begin().then("animation.orc_warrior.walk3", Animation.LoopType.LOOP));
                return PlayState.CONTINUE;
            } else if (animationTick == 0) {
            this.idleCondition = 0;
            }
        }
        event.getController().setAnimation(RawAnimation.begin().then("animation.orc_warrior.idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    private <E extends GeoAnimatable> PlayState attackstancePredicate(AnimationState<E> event) {
        if ((this.isAttacking() && event.isMoving())) {
            event.getController().setAnimation(RawAnimation.begin().then("animation.orc_warrior.running", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
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
        return 1.0F;
    }

    public int getXpToDrop() {
        return 3;
    }



    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}