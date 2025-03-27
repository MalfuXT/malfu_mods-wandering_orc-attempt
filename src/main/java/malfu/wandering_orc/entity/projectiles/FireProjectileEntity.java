package malfu.wandering_orc.entity.projectiles;

import malfu.wandering_orc.entity.ModEntities;
import malfu.wandering_orc.entity.custom.OrcGroupEntity;
import malfu.wandering_orc.sound.ModSounds;
import malfu.wandering_orc.util.ParticleUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.ClientUtils;

import java.util.Map;
import java.util.WeakHashMap;

public class FireProjectileEntity extends ThrownItemEntity implements GeoEntity {
    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    private static final Map<Entity, HitData> entityHitCounts = new WeakHashMap<>();
    float damage;
    private int timerTodespawn = 0;

    public FireProjectileEntity(EntityType<? extends FireProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.damage = damage;
    }

    public FireProjectileEntity(World world, LivingEntity owner, float damage) {
        super(ModEntities.FIRE_PROJECTILE, owner, world);
        this.damage = damage;
    }

    @Override
    protected Item getDefaultItem() {
        return null;
    }

    protected void onEntityHit(EntityHitResult entityHitResult) {
        // Do not call super.onEntityHit() to prevent the default sound
        if (!this.getWorld().isClient) {
            Entity target = entityHitResult.getEntity();

            // IGNORE PROJECTILE COLLISION AGAINST TEAMMATES OF ORC
            if (target instanceof OrcGroupEntity && this.getOwner() instanceof OrcGroupEntity) {
                OrcGroupEntity orcTarget = (OrcGroupEntity) target;
                OrcGroupEntity shooter = (OrcGroupEntity) this.getOwner();

                if (orcTarget.getTeamOrc().equals(shooter.getTeamOrc())) {
                    return; // Ignore the hit if the target is in the same group
                }
            }

            if (target instanceof LivingEntity) {
                LivingEntity livingTarget = (LivingEntity) target;
                livingTarget.damage(this.getDamageSources().thrown(this, this.getOwner()), this.damage);

                // Get the current time
                long currentTime = this.getWorld().getTime();

                // Get the existing hit data or create a new one
                HitData hitData = entityHitCounts.getOrDefault(target, new HitData(0, currentTime));
                int hits = hitData.hits + 1;

                // Update the hit count and last hit time
                entityHitCounts.put(target, new HitData(hits, currentTime));

                // Set the entity on fire after 3 hits
                if (hits >= 3) {
                    livingTarget.setFireTicks(200); // Set on fire for 5 seconds (100 ticks)
                    entityHitCounts.remove(target); // Reset the hit count
                }
            }

            // Play the custom sound
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.FIREBALL_IMPACT,
                    SoundCategory.NEUTRAL, 0.55f, 0.50f);

            ParticleUtil.generateFireParticle(this);
            this.discard();
        }
    }


    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        World world = this.getWorld();
        BlockPos pos = blockHitResult.getBlockPos();

        // Do not call super.onBlockHit() to prevent the default sound
        if (!this.getWorld().isClient) {
            // Play the custom sound on block impact
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.FIREBALL_IMPACT,
                    SoundCategory.NEUTRAL, 0.55f, 0.50f);

            if (world.getRandom().nextFloat() < 0.05f) {
                // Get the block state at the hit position
                BlockState blockState = world.getBlockState(pos);

                // Check if the block is burnable
                if (blockState.isBurnable()) {
                    // Set the block on fire
                    BlockPos firePos = pos.offset(blockHitResult.getSide()); // Set fire on the side of the block
                    if (world.getBlockState(firePos).isAir()) {
                        world.setBlockState(firePos, Blocks.FIRE.getDefaultState());
                    }
                }
            }
        }

        ParticleUtil.generateFireParticle(this);
        this.discard();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", this::predicate).setSoundKeyframeHandler((event) -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null) {
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(),
                        ModSounds.FIREBALL_TRAVEL, this.getSoundCategory(),0.5F, 1.0f);
            }
        }));
    }

    private PlayState predicate(AnimationState animationState) {
        animationState.getController().setAnimation(RawAnimation.begin().then("flying", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public void tick() {
        super.tick();

        this.timerTodespawn++;

        if(this.timerTodespawn >= 20){
            this.discard();
            if(this.isTouchingWater()) {
                ParticleUtil.generateBubbleParticle(this);
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLOCK_FIRE_EXTINGUISH,
                        SoundCategory.NEUTRAL, 0.45f, 1.20f);
            } else {
                ParticleUtil.generateFireParticle(this);
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.FIREBALL_IMPACT,
                        SoundCategory.NEUTRAL, 0.45f, 1.00f);
            }
            this.timerTodespawn = 0;
        }

        if(this.isTouchingWater()) {
            this.timerTodespawn = 20;
        }

        if (!this.getWorld().isClient) {
            long currentTime = this.getWorld().getTime();
            entityHitCounts.entrySet().removeIf(entry -> {
                HitData hitData = entry.getValue();
                if (currentTime - hitData.lastHitTime > 200) { // 10 seconds (200 ticks)
                    return true; // Remove the entity if it hasn't been hit in 10 seconds
                }
                return false;
            });
        }

        HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
        boolean bl = false;
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
            BlockState blockState = this.getWorld().getBlockState(blockPos);
            if (blockState.isOf(Blocks.NETHER_PORTAL)) {
                this.setInNetherPortal(blockPos);
                bl = true;
            } else if (blockState.isOf(Blocks.END_GATEWAY)) {
                BlockEntity blockEntity = this.getWorld().getBlockEntity(blockPos);
                if (blockEntity instanceof EndGatewayBlockEntity && EndGatewayBlockEntity.canTeleport(this)) {
                    EndGatewayBlockEntity.tryTeleportingEntity(this.getWorld(), blockPos, blockState, this, (EndGatewayBlockEntity)blockEntity);
                }

                bl = true;
            }
        }

        if (hitResult.getType() != HitResult.Type.MISS && !bl) {
            this.onCollision(hitResult);
        }

        this.checkBlockCollision();
        Vec3d vec3d = this.getVelocity();
        double d = this.getX() + vec3d.x;
        double e = this.getY() + vec3d.y;
        double f = this.getZ() + vec3d.z;
        this.updateRotation();
        float h;
        if (this.isTouchingWater()) {
            for (int i = 0; i < 4; i++) {
                float g = 0.25F;
                this.getWorld().addParticle(ParticleTypes.BUBBLE, d - vec3d.x * 0.25, e - vec3d.y * 0.25, f - vec3d.z * 0.25, vec3d.x, vec3d.y, vec3d.z);
            }

            h = 0.8F;
        } else {
            h = 0.99F;
        }

        this.setVelocity(vec3d.multiply((double)h));
        if (!this.hasNoGravity()) {
            Vec3d vec3d2 = this.getVelocity();
            this.setVelocity(vec3d2.x, vec3d2.y - (double)this.getGravity(), vec3d2.z);
        }

        this.setPosition(d, e, f);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    protected float getGravity() {
        return 0.001F;
    }

    private static class HitData {
        public final int hits;
        public final long lastHitTime;

        public HitData(int hits, long lastHitTime) {
            this.hits = hits;
            this.lastHitTime = lastHitTime;
        }
    }
}
