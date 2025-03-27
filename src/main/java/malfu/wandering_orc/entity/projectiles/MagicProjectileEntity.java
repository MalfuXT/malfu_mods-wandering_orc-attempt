package malfu.wandering_orc.entity.projectiles;

import malfu.wandering_orc.entity.ModEntities;
import malfu.wandering_orc.entity.custom.OrcGroupEntity;
import malfu.wandering_orc.particle.ModParticles;
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
import net.minecraft.server.world.ServerWorld;
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

public class MagicProjectileEntity extends ThrownItemEntity implements GeoEntity {
    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    float damage;
    private int timerTodespawn = 0;

    public MagicProjectileEntity(EntityType<? extends MagicProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public MagicProjectileEntity(World world, LivingEntity owner, float damage) {
        super(ModEntities.MAGIC_PROJECTILE, owner, world);
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

            // IGNORE PROJECTILE COLLISION AGAINTS TEAMATES OF ORC
            if (target instanceof OrcGroupEntity && this.getOwner() instanceof OrcGroupEntity) {
                OrcGroupEntity orcTarget = (OrcGroupEntity) target;
                OrcGroupEntity shooter = (OrcGroupEntity) this.getOwner();

                if (orcTarget.getTeamOrc().equals(shooter.getTeamOrc())) {
                    return; // Ignore the hit if the target is in the same group
                }
            }

            // INFLICT DAMAGE AND SOUND
            if (target instanceof LivingEntity) {
                LivingEntity livingTarget = (LivingEntity) target;
                livingTarget.damage(this.getDamageSources().thrown(this, this.getOwner()), this.damage);
            }

            // Play the custom sound
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.MAGIC_IMPACT,
                    SoundCategory.NEUTRAL, 0.55f, 1.0f);

            ParticleUtil.generateMagicParticle(this);
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
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.MAGIC_IMPACT,
                    SoundCategory.NEUTRAL, 0.35f, 1.0f);
        }


        ParticleUtil.generateMagicParticle(this);
        this.discard();
    }

    @Override
    public void tick() {
        super.tick();

        this.timerTodespawn++;

        if(this.timerTodespawn >= 20){
            this.discard();
            ParticleUtil.generateMagicParticle(this);
            this.timerTodespawn = 0;
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
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", this::predicate).setSoundKeyframeHandler((event) -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            if (player != null) {
                this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.BLOCK_FIRE_AMBIENT, this.getSoundCategory(),0.5F, 0.8f);
            }
        }));
    }

    private PlayState predicate(AnimationState animationState) {
        animationState.getController().setAnimation(RawAnimation.begin().then("flying", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    protected float getGravity() {
        return 0.001F;
    }
}
