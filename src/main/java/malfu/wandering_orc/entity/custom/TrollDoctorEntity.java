package malfu.wandering_orc.entity.custom;

import malfu.wandering_orc.entity.ai.CrossOrcRevengeGoal;
import malfu.wandering_orc.entity.ai.TrollDoctorAttackGoal;
import malfu.wandering_orc.entity.ai.TrollDoctorHealingGoal;
import malfu.wandering_orc.entity.ai.wander.OrcFollowLeaderGoal;
import malfu.wandering_orc.sound.ModSounds;
import malfu.wandering_orc.util.config.ModBonusHealthConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
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

public class TrollDoctorEntity extends OrcGroupEntity implements GeoEntity {
    private static int HEALCOOLDOWN = 200;
    private static int AREAHEALCOOLDOWN = 800;
    private int healCD;
    private int areaHealCD;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public TrollDoctorEntity(EntityType<? extends OrcGroupEntity> entityType, World world) {
        super(entityType, world);
    }

    public static final TrackedData<Boolean> ATKTIMING = DataTracker.registerData(TrollDoctorEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> HEALING = DataTracker.registerData(TrollDoctorEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> HEALING_PR = DataTracker.registerData(TrollDoctorEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> HEALCD = DataTracker.registerData(TrollDoctorEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> AREAHEALCD = DataTracker.registerData(TrollDoctorEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> AREAHEALANIM = DataTracker.registerData(TrollDoctorEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<String> ATKVARI = DataTracker.registerData(TrollDoctorEntity.class, TrackedDataHandlerRegistry.STRING);


    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ATKTIMING, false);
        this.dataTracker.startTracking(HEALING, false);
        this.dataTracker.startTracking(HEALING_PR, false);
        this.dataTracker.startTracking(HEALCD, false);
        this.dataTracker.startTracking(AREAHEALCD, false);
        this.dataTracker.startTracking(AREAHEALANIM, false);
        this.dataTracker.startTracking(ATKVARI, "attack");
    }

    public boolean isHealing() {return (Boolean)this.dataTracker.get(HEALING);}
    public void setHealing(boolean healing) {this.dataTracker.set(HEALING, healing);}
    public boolean isHealingProcess() {return (Boolean)this.dataTracker.get(HEALING_PR);}
    public void setHealingProcess(boolean healingProcess) {this.dataTracker.set(HEALING_PR, healingProcess);}
    public boolean isHealCD() {return (Boolean)this.dataTracker.get(HEALCD);}
    public void setHealCD(boolean healcd) {this.dataTracker.set(HEALCD, healcd);}
    public boolean isAreaHealCD() {return (Boolean)this.dataTracker.get(AREAHEALCD);}
    public void setAreaHealCD(boolean areaHealCD) {this.dataTracker.set(AREAHEALCD, areaHealCD);}

    public boolean isAreaHealAnim() {return (Boolean)this.dataTracker.get(AREAHEALANIM);}
    public void setAreaHealAnim(boolean areaHealAnim) {this.dataTracker.set(AREAHEALANIM, areaHealAnim);}
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
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25f)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D + ModBonusHealthConfig.trollDoctorBonusHealth)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 7.0f)
                .add(EntityAttributes.GENERIC_ARMOR, 1.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.1f);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new TrollDoctorHealingGoal(this, 1.2f));
        this.goalSelector.add(3, new TrollDoctorAttackGoal(this, 1.2f));
        this.goalSelector.add(5, new OrcFollowLeaderGoal(this));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(7, new LookAroundGoal(this));
        this.targetSelector.add(3, new CrossOrcRevengeGoal(this, OrcGroupEntity.class).setGroupRevenge());
        this.targetSelector.add(4, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    public void tick() {
        super.tick();

        if (isHealCD()) { //TRUE IS ON COOLDOWN
            this.healCD--;
        }

        if (this.healCD <= 0) { //FALSE IS HEALING READY
            this.setHealCD(false);
            this.healCD = HEALCOOLDOWN;
        }

        if (isAreaHealCD()) { //TRUE IS ON COOLDOWN
            this.areaHealCD--;
        }

        if (this.areaHealCD <= 0) { //FALSE IS HEALING READY
            this.setAreaHealCD(false);
            this.areaHealCD = AREAHEALCOOLDOWN;
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
        controllers.add(new AnimationController<>(this, "attack", 0, this::attackPredicate)
                .setSoundKeyframeHandler((event) -> {
            // Get the sound instruction from the keyframe
            String soundInstruction = event.getKeyframeData().getSound();

            // Check the instruction and play the corresponding sound
            if (soundInstruction != null) {
                PlayerEntity player = ClientUtils.getClientPlayer();
                if (player != null) {
                    if (soundInstruction.equals("charge")) {
                        this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(),
                                ModSounds.FIREBALL_CHARGE, this.getSoundCategory(), 1.0F, 1.0F);
                    }
                }
            }
        }));
        controllers.add(new AnimationController<>(this, "healing", 0, this::healingPredicate).setSoundKeyframeHandler((event) -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null) {
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), ModSounds.HEAL, this.getSoundCategory(),1.0F, 1.0f);
            }
        }));
        controllers.add(new AnimationController<>(this, "areahealing", 0, this::areahealingPredicate).setSoundKeyframeHandler((event) -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null) {
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), ModSounds.AREA_HEAL, this.getSoundCategory(),1.0F, 1.0f);
            }
        }));
    }



    private <E extends GeoAnimatable> PlayState healingPredicate(AnimationState<E> event) {
        if (this.isHealing()) {
            event.getController().setAnimation(RawAnimation.begin().then("heal_transition", Animation.LoopType.PLAY_ONCE).
                    thenLoop("heal"));
            return PlayState.CONTINUE;
        }
        event.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    private <E extends GeoAnimatable> PlayState areahealingPredicate(AnimationState<E> event) {
        if (this.isAreaHealAnim() && event.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            event.getController().setAnimation(RawAnimation.begin().then("area_heal", Animation.LoopType.PLAY_ONCE));
        }
        event.getController().forceAnimationReset();
        return PlayState.CONTINUE;
    }

    private <E extends GeoAnimatable> PlayState attackPredicate(AnimationState<E> event) {
        if (this.isTrigger() && event.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            event.getController().setAnimation(RawAnimation.begin().then(this.getAttackName(), Animation.LoopType.PLAY_ONCE));
        }
        event.getController().forceAnimationReset();
        return PlayState.CONTINUE;
     }


    private <E extends GeoAnimatable> PlayState predicate(AnimationState<E> event) {
        float speed = ((OrcGroupEntity) event.getAnimatable()).getDataTracker().get(SPEED);
        if (speed >= 1.2f) {
            event.getController().setAnimationSpeed(1.4f);
        } else {
            event.getController().setAnimationSpeed(1.0f);
        }

        if (event.isMoving()) {
            event.getController().setAnimation(RawAnimation.begin().then("walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        event.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.ORC_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.ORC_HURT2;
    }

    @Override
    public float getSoundPitch() {
        return 1.4F;
    }

    public int getXpToDrop() {
        return 4;
    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}