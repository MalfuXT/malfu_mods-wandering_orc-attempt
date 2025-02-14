package malfu.wandering_orc.entity.projectiles;

import malfu.wandering_orc.entity.ModEntities;
import malfu.wandering_orc.item.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
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
        this.damage = 8.0f;  // Default value for the projectile
    }

    public TrollThrowableEntity(World world, LivingEntity owner, float damage) {
        super(ModEntities.TROLL_THROWABLE, owner, world);
        this.damage = damage;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        // Do not call super.onEntityHit() to prevent the default sound
        if (!this.getWorld().isClient) {
            // Inflict damage and play the custom sound on entity impact
            if (entityHitResult.getEntity() instanceof LivingEntity) {
                LivingEntity target = (LivingEntity) entityHitResult.getEntity();
                target.damage(this.getDamageSources().thrown(this, this.getOwner()), this.damage);
            }

            // Play the custom sound
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLOCK_CHAIN_HIT,
                    SoundCategory.NEUTRAL, 0.65f, 0.80f);
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        // Do not call super.onBlockHit() to prevent the default sound
        if (!this.getWorld().isClient) {
            // Play the custom sound on block impact
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLOCK_CHAIN_HIT,
                    SoundCategory.NEUTRAL, 0.65f, 0.80f);
        }
        this.discard();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationState animationState) {
        animationState.getController().setAnimation(RawAnimation.begin().then("animation.troll_throwable.throw", Animation.LoopType.LOOP));
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
