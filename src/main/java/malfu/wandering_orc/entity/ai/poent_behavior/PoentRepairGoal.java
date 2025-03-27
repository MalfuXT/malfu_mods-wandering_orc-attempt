package malfu.wandering_orc.entity.ai.poent_behavior;

import malfu.wandering_orc.entity.custom.PoentEntity;
import malfu.wandering_orc.sound.ModSounds;
import malfu.wandering_orc.util.custom_structure_util.CustomStructureKeys;
import malfu.wandering_orc.util.custom_structure_util.StructureMemory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.structure.Structure;

import java.util.*;


public class PoentRepairGoal extends Goal {
    private static final Set<BlockPos> TARGETED_BLOCKS = new HashSet<>();
    private final PoentEntity poent;
    private final ServerWorld world;
    private double speed;
    private BlockPos targetBlockPos;
    private int workCD;
    private int cooldown;
    private Vec3d lastPos;
    private int stuckTimer;
    private BlockBox currentCampBox;

    public PoentRepairGoal(PoentEntity poent, double speed, int RepairCooldown) {
        this.poent = poent;
        this.speed = speed;
        this.cooldown = RepairCooldown;
        this.world = (ServerWorld) poent.getWorld();
    }

    @Override
    public boolean canStart() {
        return this.isInOrcCampStructure() && !this.poent.isFleeing();
    }

    @Override
    public void start() {
        this.lastPos = this.poent.getPos();
        this.stuckTimer = 0;
        this.poent.setNowWorking(true);

        // Get the structure start object
        RegistryEntry<Structure> orcCampStructure = this.world.getRegistryManager()
                .get(RegistryKeys.STRUCTURE)
                .getEntry(CustomStructureKeys.ORC_CAMP)
                .orElse(null);

        if (orcCampStructure == null) {
            return;
        }

        StructureAccessor structureAccessor = this.world.getStructureAccessor();
        StructureStart structureStart = structureAccessor.getStructureAt(this.poent.getBlockPos(), orcCampStructure.value());

        if (structureStart != StructureStart.DEFAULT) {
            this.currentCampBox = structureStart.getBoundingBox();

            // Check if this camp is already saved
            if (!StructureMemory.ORC_CAMP_BLOCKS.containsKey(currentCampBox)) {
                StructureMemory.saveCampBlocks(currentCampBox, world);
            }
        }
    }

    @Override
    public void stop() {
        this.poent.setNowWorking(false);
        this.poent.setWork(false);
        this.lastPos = null; // Reset lastPos
        this.stuckTimer = 0; // Reset stuck timer

        // Release the target block
        if (this.targetBlockPos != null) {
            TARGETED_BLOCKS.remove(this.targetBlockPos);
            this.targetBlockPos = null;
        }
    }

    @Override
    public void tick() {

        // Check if the entity is suffocating
        if (this.poent.isInsideWall()) {
            this.teleportToSafePosition(this.poent.getBlockPos());
            return;
        }

        if (this.isStuck()) {
            if (this.targetBlockPos != null) {
                this.poent.teleport(targetBlockPos.getX() + 0.5, targetBlockPos.getY()+ 1, targetBlockPos.getZ() + 0.5);
                System.out.println("tp to " + targetBlockPos);
            }
            this.stuckTimer = 0; // Reset the stuck timer after teleporting
            return;
        }
        // Check if the entity is stuck

        if (this.targetBlockPos == null) {
            // Find the next missing block
            this.targetBlockPos = findNextMissingBlock();
            if (this.targetBlockPos == null) {
                return;
            }
        }

        if(this.isCloseToBlock(this.targetBlockPos)) {
            this.poent.getLookControl().lookAt(targetBlockPos.toCenterPos());
            this.poent.getNavigation().stop();
            this.workCD = Math.max(this.workCD - 1, 0);
            this.poent.setWork(true);
        }

        if (this.isCloseToBlock(this.targetBlockPos) && this.workCD == 0) {
            this.workCD = cooldown;
            this.poent.getNavigation().stop();

        } else if (this.workCD == 1) {
            this.poent.setWork(false);
            this.repairBlock(targetBlockPos);
            this.targetBlockPos = null;
            this.workCD = 0;
            this.poent.playSound(SoundEvents.ENTITY_VILLAGER_WORK_TOOLSMITH, 0.5f, 1.0f);
            double randomizer = Math.random();
            if(randomizer < 0.5){
                this.poent.playSound(ModSounds.POENT_DONE, 0.8f, 1.0f);
            }

        } else if (!this.isCloseToBlock(targetBlockPos)) {
            // Move towards the target block
            this.moveTowardsBlock(targetBlockPos);
            this.poent.setWork(false);
        }

        if (this.poent.hurtTime > 0) {
            this.workCD = 0;
        }
    }

    private boolean isCloseToBlock(BlockPos targetPos) {
        // Get the entity's position and the target position
        BlockPos entityPos = this.poent.getBlockPos();

        // Calculate the horizontal distance (ignore Y-axis)
        double dx = entityPos.getX() - targetPos.getX();
        double dz = entityPos.getZ() - targetPos.getZ();
        double horizontalDistanceSquared = dx * dx + dz * dz;

        // Check if the horizontal distance is within 4 blocks
        return horizontalDistanceSquared <= 7; // 4 blocks squared (4 * 4 = 16)
    }

    private void moveTowardsBlock(BlockPos targetPos) {
        // Calculate the center of the block (add 0.5 to x and z)
        Vec3d targetCenter = new Vec3d(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5);

        // Use the entity's navigation system to move towards the target
        this.poent.getNavigation().startMovingTo(targetCenter.getX(), targetCenter.getY(), targetCenter.getZ(), this.speed); // 1.0 is the speed
    }

    private void repairBlock(BlockPos targetPos) {
        Map<BlockPos, BlockState> campBlocks = StructureMemory.getBlocksForCamp(currentCampBox);
        BlockState expectedState = campBlocks.get(targetPos);

        if (expectedState != null) {
            BlockState currentState = world.getBlockState(targetPos);

            // Check if the block transition should be ignored
            if (shouldIgnoreTransition(expectedState.getBlock(), currentState.getBlock())) {
                System.out.println("Skipping repair for ignored transition at " + targetPos);
                return;
            }

            // Repair the block if it doesn't match the expected state
            if (!currentState.equals(expectedState)) {
                // Handle spawners specially
                if (expectedState.getBlock() == Blocks.SPAWNER) {
                    // Restore the spawner's mob data
                    Map<BlockPos, NbtCompound> tileEntities = StructureMemory.ORC_CAMP_TILE_ENTITIES.get(currentCampBox);
                    if (tileEntities != null) {
                        NbtCompound spawnerData = tileEntities.get(targetPos);
                        if (spawnerData != null) {
                            world.setBlockState(targetPos, expectedState, Block.NOTIFY_ALL); // Set the block state
                            BlockEntity blockEntity = world.getBlockEntity(targetPos); // Get the new spawner
                            if (blockEntity instanceof MobSpawnerBlockEntity) {
                                blockEntity.readNbt(spawnerData); // Restore the spawner data
                                System.out.println("Repaired spawner at " + targetPos + " with preserved mob data.");
                            }
                        }
                    }
                } else {
                    // Repair normal blocks
                    world.setBlockState(targetPos, expectedState, Block.NOTIFY_ALL);
                    System.out.println("Repaired block at " + targetPos);
                }
            } else {
                System.out.println("Block at " + targetPos + " is already in the correct state.");
            }
        } else {
            System.out.println("No expected state found for block at " + targetPos);
        }

        // Release the target block
        TARGETED_BLOCKS.remove(targetPos);
    }

    private BlockPos findNextMissingBlock() {
        Map<BlockPos, BlockState> campBlocks = StructureMemory.getBlocksForCamp(currentCampBox);

        // Create lists for each priority level
        List<BlockPos> highPriorityBlocks = new ArrayList<>();
        List<BlockPos> mediumPriorityBlocks = new ArrayList<>();
        List<BlockPos> lowPriorityBlocks = new ArrayList<>();

        for (Map.Entry<BlockPos, BlockState> entry : campBlocks.entrySet()) {
            BlockPos pos = entry.getKey();
            BlockState expectedState = entry.getValue();
            BlockState currentState = world.getBlockState(pos);

            // Skip if the block is already being targeted
            if (TARGETED_BLOCKS.contains(pos)) {
                continue;
            }

            // Skip if the block transition should be ignored
            if (shouldIgnoreTransition(expectedState.getBlock(), currentState.getBlock())) {
                continue;
            }


            // Check if the block needs repair
            if (!currentState.equals(expectedState)) {
                Block block = expectedState.getBlock();
                int priority = StructureMemory.BLOCK_PRIORITIES.getOrDefault(block, 0);

                // Sort blocks into priority lists
                if (priority >= 3) {
                    highPriorityBlocks.add(pos);
                } else if (priority == 2) {
                    mediumPriorityBlocks.add(pos);
                } else {
                    lowPriorityBlocks.add(pos);
                }
            }
        }

        // Return the highest priority block available
        if (!highPriorityBlocks.isEmpty()) {
            return highPriorityBlocks.get(world.random.nextInt(highPriorityBlocks.size())); // Randomize selection
        } else if (!mediumPriorityBlocks.isEmpty()) {
            return mediumPriorityBlocks.get(world.random.nextInt(mediumPriorityBlocks.size())); // Randomize selection
        } else if (!lowPriorityBlocks.isEmpty()) {
            return lowPriorityBlocks.get(world.random.nextInt(lowPriorityBlocks.size())); // Randomize selection
        }

        return null;
    }

    //TO CHECK IF STUCK OR SUFFOCATED
    private void teleportToSafePosition(BlockPos targetPos) {
        // Find a safe position near the target
        BlockPos safePos = findSafePosition(targetPos);
        if (safePos != null) {
            this.poent.teleport(safePos.getX() + 0.5, safePos.getY(), safePos.getZ() + 0.5);
        }
    }

    private BlockPos findSafePosition(BlockPos targetPos) {
        // Check a 3x3 area around the target position for a safe spot
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                BlockPos checkPos = targetPos.add(x, 2, z);
                if (isPositionSafe(checkPos)) {
                    return checkPos;
                }
            }
        }
        return null; // No safe position found
    }

    private boolean shouldIgnoreTransition(Block expectedBlock, Block currentBlock) {
        // Check if the expected block has any ignored transitions
        Set<Block> ignoredBlocks = StructureMemory.IGNORED_TRANSITIONS.get(expectedBlock);
        if (ignoredBlocks != null && ignoredBlocks.contains(currentBlock)) {
            return true;
        }

        // Ignore changes in farmland moisture state
        if (expectedBlock == Blocks.FARMLAND && currentBlock == Blocks.FARMLAND) {
            return true;
        }

        // Ignore changes in water flow states
        if (expectedBlock == Blocks.WATER && currentBlock == Blocks.WATER) {
            return true;
        }

        if (expectedBlock == Blocks.WATER && currentBlock == Blocks.BUBBLE_COLUMN) {
            return true;
        }

        if (expectedBlock == Blocks.GRASS_BLOCK && currentBlock == Blocks.GRASS_BLOCK) {
            return true;
        }

        return false;
    }

    private boolean isPositionSafe(BlockPos pos) {
        // Check if the block at the position is air or replaceable
        BlockState state = this.world.getBlockState(pos);
        return state.isAir() || state.isReplaceable();
    }

    private boolean isStuck() {
        Vec3d currentPos = this.poent.getPos();

        // If the entity hasn't moved significantly, increment the stuck timer
        if (targetBlockPos != null && currentPos.squaredDistanceTo(targetBlockPos.toCenterPos()) > 7) { // Adjust threshold as needed
            this.stuckTimer++;
        } else {
            this.stuckTimer = 0; // Reset the timer if the entity has moved
        }

        // If the stuck timer exceeds 10 seconds (200 ticks), the entity is stuck
        return stuckTimer > 200; // 200 ticks = 10 seconds
    }
    //END OF IT

    private boolean isInOrcCampStructure() {
        // Get the structure registry entry for orc_camp
        RegistryEntry<Structure> orcCampStructure = world.getRegistryManager()
                .get(RegistryKeys.STRUCTURE)
                .getEntry(CustomStructureKeys.ORC_CAMP)
                .orElse(null);

        if (orcCampStructure == null) {
            System.out.println("ORC_CAMP structure not found in registry!");
            return false;
        }

        // Check if the entity is within the structure bounds
        StructureAccessor structureAccessor = world.getStructureAccessor();
        StructureStart structureStart = structureAccessor.getStructureAt(poent.getBlockPos(), orcCampStructure.value());


        return structureStart != StructureStart.DEFAULT;
    }
}