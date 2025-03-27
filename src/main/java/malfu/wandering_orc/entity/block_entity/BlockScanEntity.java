package malfu.wandering_orc.entity.block_entity;

import malfu.wandering_orc.util.ParticleUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;

public class BlockScanEntity extends Entity implements GeoAnimatable {
    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    private BlockState blockState;
    private int timerTodespawn = 0;
    private float alpha = 1.0f;
    private float fadeSpeed = 0.12f;

    public BlockScanEntity(EntityType<?> type, World world) {
        super(type, world);
        this.blockState = Blocks.STONE.getDefaultState();
    }

    public void setBlockState(BlockState blockState) {
        this.blockState = blockState;
    }

    @Override
    public void tick() {
        super.tick();
        // Update the block state below the entity
        if(timerTodespawn == 0){
            ParticleUtil.spawnBlockParticles(this.getWorld(), this, random);
            BlockPos posBelow = this.getBlockPos(); //TO KEEP READING THE BLOCK BELOW.
            this.blockState = this.getWorld().getBlockState(posBelow);
        }

        this.timerTodespawn++;

        if(timerTodespawn >= 200){
            this.discard();
            this.timerTodespawn = 0;
            this.alpha = 1.0f;
        } else if(timerTodespawn >= 140) {
            this.alpha -= fadeSpeed;
        }

        if (timerTodespawn >= 160) {
            double currentY = this.getY();
            double newY = currentY - 0.015;
            this.setPosition(this.getX(), newY, this.getZ());
        }
    }

    public BlockState getBlockState() {
        return blockState;
    }

    public float getAlpha() {
        return alpha;
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        // Read block state from NBT
        this.blockState = NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), nbt.getCompound("BlockState"));
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        // Write block state to NBT
        nbt.put("BlockState", NbtHelper.fromBlockState(this.blockState));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    public double getTick(Object o) {
        return 0;
    }
}
