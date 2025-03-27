package malfu.wandering_orc.entity.custom;

import malfu.wandering_orc.entity.ai.CrossOrcRevengeGoal;
import malfu.wandering_orc.entity.ai.OrcChampionMeleeGoal;
import malfu.wandering_orc.entity.ai.wander.WanderAroundReallyFarGoal;
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
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.PatrolEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.ClientUtils;
import software.bernie.geckolib.util.GeckoLibUtil;

public class OrcChampionEntity extends OrcGroupEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public OrcChampionEntity(EntityType<? extends OrcGroupEntity> entityType, World world) {
        super(entityType, world);
    }

    public static final TrackedData<Boolean> ATKTIMING =
            DataTracker.registerData(OrcChampionEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public static final TrackedData<String> ATKVARI =
            DataTracker.registerData(OrcChampionEntity.class, TrackedDataHandlerRegistry.STRING);

    public static final TrackedData<Boolean> DODGE =
            DataTracker.registerData(OrcChampionEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> SHIELD =
            DataTracker.registerData(OrcChampionEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ATKTIMING, false);
        this.dataTracker.startTracking(ATKVARI, "animation.orc_champion.move_attack");
        this.dataTracker.startTracking(DODGE, false);
        this.dataTracker.startTracking(SHIELD, false);
    }

    public boolean isDodge() {return (Boolean)this.dataTracker.get(DODGE);}
    public void setDodge(boolean dodge) {this.dataTracker.set(DODGE, dodge);}

    public boolean isTrigger() {return (Boolean)this.dataTracker.get(ATKTIMING);}
    public void setTrigger(boolean atktiming) {
        this.dataTracker.set(ATKTIMING, atktiming);
    }

    public void setAttackName(String attackName) {this.dataTracker.set(ATKVARI, attackName);}
    public String getAttackName() {return (String)this.dataTracker.get(ATKVARI);}

    public static DefaultAttributeContainer.Builder setAttributes() {
        return OrcGroupEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25f)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 35.0D + ModBonusHealthConfig.orcChampionBonusHealth)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 10.0f)
                .add(EntityAttributes.GENERIC_ARMOR, 10.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5f);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(3, new OrcChampionMeleeGoal(this, 1.2f));
        this.goalSelector.add(5, new WanderAroundReallyFarGoal(this, 1.0f, 20));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.targetSelector.add(1, new CrossOrcRevengeGoal(this, OrcGroupEntity.class).setGroupRevenge());
        this.targetSelector.add(4, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, MerchantEntity.class, false));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, PatrolEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, AbstractPiglinEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, ZombieEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, LivingEntity.class, 10, true, false, OrcGroupEntity.TARGET_ORC_ENEMIES));
    }

    //SHIELDING MECHANIC
    private boolean isShielding = false;
    private boolean shieldStop = false;

    public boolean isAnimShielding() {return (Boolean)this.dataTracker.get(SHIELD);}
    public void setAnimShielding(boolean shield) {this.dataTracker.set(SHIELD, shield);}

    public boolean isShielding() {return isShielding;}
    public void setShielding(boolean shielding) {this.isShielding = shielding;}
    public boolean isShieldStop() {return shieldStop;}
    public void setShieldStop(boolean shieldstop) {this.shieldStop = shieldstop;}

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (isShielding && isAttackFromFront(source)) {
            // Block the attack
            this.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 1.0F, 1.0F); // Play shield block sound
            setShielding(false); // Exit shielding stance after blocking
            setAnimShielding(false);
            setShieldStop(false); //i'm trippin. it is to stop moving when shielding, but i could've just use setShielding??.
            return false; // Cancel the damage
        }
        setShielding(false);
        setAnimShielding(false);
        setShieldStop(false);
        return super.damage(source, amount); // Apply damage normally
    }

    private boolean isAttackFromFront(DamageSource source) {
        if (source.getSource() instanceof LivingEntity attacker) {
            // Calculate the angle between the attacker and the Orc Champion
            Vec3d attackerPos = attacker.getPos();
            Vec3d orcPos = this.getPos();
            Vec3d directionToAttacker = attackerPos.subtract(orcPos).normalize();
            Vec3d orcFacing = this.getRotationVector();

            // Calculate the dot product to determine if the attack is from the front
            double dotProduct = orcFacing.dotProduct(directionToAttacker);
            return dotProduct > 0.7; // Adjust the threshold as needed
        }
        return false;
    }
    //SHIELDING MECHANIC ENDS HERE

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
        controllers.add(new AnimationController<>(this, "attackingstance", 0, this::attackstancePredicate));
        controllers.add(new AnimationController<>(this, "attack", 0, this::attackPredicate)
                .setSoundKeyframeHandler((event) -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null) {
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), ModSounds.SWING_LIGHT, this.getSoundCategory(),0.5F, 1.4f);
            }
        }));
        controllers.add(new AnimationController<>(this, "dodge", 0, this::dodgePredicate));
        controllers.add(new AnimationController<>(this, "shield", 0, this::shieldPredicate));
    }

    private <E extends GeoAnimatable> PlayState shieldPredicate(AnimationState<E> event) {
        if (this.isAnimShielding()) {
            event.getController().setAnimation(RawAnimation.begin().then("animation.orc_champion.shield_stance_transition", Animation.LoopType.PLAY_ONCE)
                    .thenLoop("animation.orc_champion.shield_stance"));
            return PlayState.CONTINUE;
        } event.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    private <E extends GeoAnimatable> PlayState dodgePredicate(AnimationState<E> event) {
        if (this.isDodge()) {
            event.getController().setAnimation(RawAnimation.begin().then("animation.orc_champion.dodge", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        } event.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    private <E extends GeoAnimatable> PlayState attackPredicate(AnimationState<E> event) {
        if (this.isTrigger() && event.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            event.getController().setAnimation(RawAnimation.begin().then(this.getAttackName(), Animation.LoopType.PLAY_ONCE));
        }
        event.getController().forceAnimationReset();
        return PlayState.CONTINUE;
     }

    private <E extends GeoAnimatable> PlayState predicate(AnimationState<E> event) {
        if (event.isMoving()) {
            event.getController().setAnimation(RawAnimation.begin().then("animation.orc_champion.walk", Animation.LoopType.LOOP));
        } else {
            event.getController().setAnimation(RawAnimation.begin().then("animation.orc_champion.idle", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    private <E extends GeoAnimatable> PlayState attackstancePredicate(AnimationState<E> event) {
        if (this.isAttacking() && event.isMoving()) {
            event.getController().setAnimationSpeed(1.3f);
            event.getController().setAnimation(RawAnimation.begin().then("animation.orc_champion.walk_target", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        } else event.getController().setAnimationSpeed(1.0f);
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

    public int getXpToDrop() {
        return 5;
    }



    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}