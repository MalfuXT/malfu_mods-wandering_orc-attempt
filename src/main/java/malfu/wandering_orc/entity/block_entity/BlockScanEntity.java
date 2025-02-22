package malfu.wandering_orc.entity.block_entity;

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

    public BlockScanEntity(EntityType<?> type, World world) {
        super(type, world);
        this.blockState = Blocks.STONE.getDefaultState(); // Default block state
    }

    public void setBlockState(BlockState blockState) {
        this.blockState = blockState;
    }

    @Override
    public void tick() {
        super.tick();
        // Update the block state below the entity
        BlockPos posBelow = this.getBlockPos();
        this.blockState = this.getWorld().getBlockState(posBelow);
        this.timerTodespawn++;

        if(timerTodespawn >= 200){
            this.discard();
            this.timerTodespawn = 0;
        }
    }

    public BlockState getBlockState() {
        return blockState;
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
