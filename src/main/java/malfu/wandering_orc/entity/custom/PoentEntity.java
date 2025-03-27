package malfu.wandering_orc.entity.custom;

import malfu.wandering_orc.entity.ai.CrossOrcRevengeGoal;
import malfu.wandering_orc.entity.ai.poent_behavior.PoentGoToCampGoal;
import malfu.wandering_orc.entity.ai.poent_behavior.PoentRepairGoal;
import malfu.wandering_orc.sound.ModSounds;
import malfu.wandering_orc.util.config.ModBonusHealthConfig;
import malfu.wandering_orc.util.custom_structure_util.MobItemPickupUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
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

import java.util.List;

public class PoentEntity extends OrcGroupEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private double randomizer;
    private int pickUpCD;

    public PoentEntity(EntityType<? extends OrcGroupEntity> entityType, World world) {
        super(entityType, world);
    }

    public static final TrackedData<Boolean> ATKTIMING = DataTracker.registerData(PoentEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> WORK = DataTracker.registerData(PoentEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> NOW_WORKING = DataTracker.registerData(PoentEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<String> ATKVARI = DataTracker.registerData(PoentEntity.class, TrackedDataHandlerRegistry.STRING);

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ATKTIMING, false);
        this.dataTracker.startTracking(WORK, false);
        this.dataTracker.startTracking(NOW_WORKING, false);
        this.dataTracker.startTracking(ATKVARI, "attack");
    }

    public boolean isNowWorking() {
        return (Boolean)this.dataTracker.get(NOW_WORKING);
    }
    public void setNowWorking(boolean now_work) {
        this.dataTracker.set(NOW_WORKING, now_work);
    }
    public boolean isWork() {
        return (Boolean)this.dataTracker.get(WORK);
    }
    public void setWork(boolean work) {
        this.dataTracker.set(WORK, work);
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
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25f)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0f)
                .add(EntityAttributes.GENERIC_ARMOR, 0.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.1f);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new PoentRepairGoal(this, 1.2, 40));
        this.goalSelector.add(3, new PoentEscapeDanger(this, 1.5));
        this.goalSelector.add(3, new PoentGoToCampGoal(this, 1.0));
        this.goalSelector.add(6, new WanderAroundGoal(this, 0.8));
        this.goalSelector.add(6, new PoentLookAroundGoal(this));
        this.targetSelector.add(1, new CrossOrcRevengeGoal(this, OrcGroupEntity.class).setGroupRevenge());
    }

    @Override
    public void tick(){
        super.tick();
        if (this.pickUpCD < 20) { // Check for items every second (20 ticks)
            this.pickUpCD++;
        } else {
            this.pickupItems();
            this.pickUpCD = 0;
        }
    }

    class PoentLookAroundGoal extends LookAroundGoal {
        public PoentLookAroundGoal(MobEntity mob) {super(mob);}
        @Override
        public boolean canStart() {return PoentEntity.this.getRandom().nextFloat() < 0.02F && !PoentEntity.this.isNowWorking();}
    }

    class PoentEscapeDanger extends EscapeDangerGoal {
        public PoentEscapeDanger(PathAwareEntity mob, double speed) {
            super(mob, speed);
        }

        @Override
        public void start() {
            super.start();
            PoentEntity.this.setFleeing(true);
        }
        @Override
        public void stop() {
            super.stop();
            PoentEntity.this.setFleeing(false);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
        controllers.add(new AnimationController<>(this, "attack", 0, this::attackPredicate));
        controllers.add(new AnimationController<>(this, "work", 0, this::workPredicate).setSoundKeyframeHandler((event) -> {
            // Get the sound instruction from the keyframe
            String soundInstruction = event.getKeyframeData().getSound();

            // Check the instruction and play the corresponding sound
            if (soundInstruction != null) {
                PlayerEntity player = ClientUtils.getClientPlayer();
                if (player != null) {
                    if ("work_work".equals(soundInstruction)) {
                        this.randomizer = Math.random();
                        if(this.randomizer < 0.05){
                            this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(),
                                    ModSounds.POENT_COMPLAINT, this.getSoundCategory(), 1.0F, 1.0F);
                        }

                    } else if ("hit_work".equals(soundInstruction)) {
                        this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(),
                                SoundEvents.BLOCK_ANVIL_LAND, this.getSoundCategory(), 0.5F, 2.0F);
                    }
                }
            }
        }));
    }

    private <E extends GeoAnimatable> PlayState workPredicate(AnimationState<E> event) {
        if (this.isWork()) {
            event.getController().setAnimation(RawAnimation.begin().then("transition_work", Animation.LoopType.PLAY_ONCE)
                    .thenLoop("work"));
            return PlayState.CONTINUE;
        }
        event.getController().forceAnimationReset();
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
        float speed = ((OrcGroupEntity) event.getAnimatable()).getDataTracker().get(SPEED);
        if (event.isMoving() && !this.isWork() && speed >= 1.2 && speed < 1.5) {
            event.getController().setAnimationSpeed(0.8f);
            event.getController().setAnimation(RawAnimation.begin().then("run", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if (event.isMoving() && !this.isWork() && speed >= 1.5) {
            event.getController().setAnimationSpeed(1.0f);
            event.getController().setAnimation(RawAnimation.begin().then("run", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if (event.isMoving() && !this.isWork() && speed <= 1.0) {
            event.getController().setAnimationSpeed(1.0f);
            event.getController().setAnimation(RawAnimation.begin().then("walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        event.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.POENT_AMBIENCE;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.POENT_HURT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.POENT_HURT;
    }

    @Override
    public float getSoundPitch() {
        return 1.0F;
    }

    public int getXpToDrop() {
        return 1;
    }


    public void pickupItems() {
        if (!(this.getWorld() instanceof ServerWorld)) {
            return; // Only run on the server side
        }

        ServerWorld world = (ServerWorld) this.getWorld();

        // Get all item entities within a 4-block radius
        List<ItemEntity> items = world.getEntitiesByClass(ItemEntity.class, this.getBoundingBox().expand(1.0), itemEntity -> {
            ItemStack stack = itemEntity.getStack();
            return MobItemPickupUtil.POENT_ITEMS_TO_PICK_UP.contains(stack.getItem()); // Check if the item is in the pickup list
        });

        // Pick up the first valid item
        for (ItemEntity item : items) {
            ItemStack stack = item.getStack();
            System.out.println("PoentEntity picked up: " + stack.getItem().getName().getString());
            item.discard(); // Remove the item entity from the world
            break; // Stop after picking up one item
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}