package malfu.wandering_orc.entity.projectiles;

import malfu.wandering_orc.entity.ModEntities;
import malfu.wandering_orc.entity.block_entity.BlockScanEntity;
import malfu.wandering_orc.entity.custom.OrcGroupEntity;
import malfu.wandering_orc.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

public class TrollThrowableEntity extends PersistentProjectileEntity implements GeoEntity {
    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    float damage;

    public TrollThrowableEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.damage = 7.0f;  // Default value for the projectile
    }

    public TrollThrowableEntity(World world, LivingEntity owner, float damage) {
        super(ModEntities.TROLL_THROWABLE, owner, world);
        this.damage = damage;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        // Do not call super.onEntityHit() to prevent the default sound
        if (!this.getWorld().isClient) {
            Entity target = entityHitResult.getEntity();

            // IGNORE PROJECTILE COLLISION AGAINTS TEAMATES OF ORC
            if (target instanceof OrcGroupEntity && this.getOwner() instanceof OrcGroupEntity) {
                OrcGroupEntity orcTarget = (OrcGroupEntity) target;
                OrcGroupEntity shooter = (OrcGroupEntity) this.getOwner();

                if (orcTarget.getTeamOrc().equals(shooter.getTeamOrc())) {
                    return; // Ignore the hit if the target is in the same group
                }
            }

            // Inflict damage and play the custom sound on entity impact
            if (target instanceof LivingEntity) {
                LivingEntity livingTarget = (LivingEntity) target;
                livingTarget.damage(this.getDamageSources().thrown(this, this.getOwner()), this.damage);
            }

            // Play the custom sound
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLOCK_CHAIN_HIT,
                    SoundCategory.NEUTRAL, 0.55f, 1.20f);
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLOCK_MUD_BRICKS_STEP,
                    SoundCategory.NEUTRAL, 0.55f, 2.00f);
        }
        this.discard();
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        World world = this.getWorld();
        BlockPos pos = blockHitResult.getBlockPos();
        // Do not call super.onBlockHit() to prevent the default sound
        if (!this.getWorld().isClient) {
            // Play the custom sound on block impact
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLOCK_CHAIN_HIT,
                    SoundCategory.NEUTRAL, 0.45f, 1.20f);
        }
        this.discard();
    }


    //SO ANIMATION WILL DETECT WHEN TO STOP BY SCAN AROUND BLOCK
    private boolean isOnAnyBlock() {
        BlockPos pos = this.getBlockPos();
        // Check the current block
        BlockState state = this.getWorld().getBlockState(pos);
        if (!state.isAir()) {
            return true;
        }
        // Check adjacent blocks within a 1-block radius
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0) {
                        continue; // Skip the current block position
                    }

                    BlockPos offsetPos = pos.add(x, y, z);
                    state = this.getWorld().getBlockState(offsetPos);
                    if (!state.isAir()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationState animationState) {
        if(!this.isOnAnyBlock()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("animation.troll_throwable.throw", Animation.LoopType.LOOP));
        } animationState.getController().stop();
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    protected ItemStack asItemStack() {
        return new ItemStack(ModItems.TROLL_THROWABLE_ITEM);  // Define the item as a feather
    }
}
