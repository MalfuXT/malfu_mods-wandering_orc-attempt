package malfu.wandering_orc.entity.ai.poent_behavior;

import malfu.wandering_orc.entity.custom.PoentEntity;
import malfu.wandering_orc.util.custom_structure_util.CustomStructureKeys;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.structure.Structure;

public class PoentGoToCampGoal extends Goal {
    private final PoentEntity poent;
    private final ServerWorld world;
    private final double speed;
    private BlockPos targetPos;
    private int cooldown;

    public PoentGoToCampGoal(PoentEntity poent, double speed) {
        this.poent = poent;
        this.world = (ServerWorld) poent.getWorld();
        this.speed = speed;
    }

    @Override
    public boolean canStart() {
        // Check if the PoentEntity is already inside the camp bounding box
        if (this.isInsideCamp()) {
            return false;
        }

        // Find the nearest ORC_CAMP structure
        BlockPos campCenter = this.findNearestCamp();
        if (campCenter == null) {
            return false;
        }

        // Check if the PoentEntity is within 32 blocks of the camp's outer boundary
        double distanceToCamp = this.poent.getBlockPos().getSquaredDistance(campCenter);
        double campRadius = this.getCampRadius(campCenter);
        double outerRadius = campRadius + 32.0; // 32-block radius from the outer boundary

        if (distanceToCamp <= outerRadius * outerRadius) {
            return true;
        }
        return false;
    }

    @Override
    public void start() {
        // Calculate a random position within 10 blocks of the camp center
        BlockPos campCenter = this.findNearestCamp();
        if (campCenter == null) {
            return;
        }

        int radius = 10;
        int x = campCenter.getX() + world.random.nextInt(radius * 2) - radius;
        int z = campCenter.getZ() + world.random.nextInt(radius * 2) - radius;
        int y = world.getTopY(Heightmap.Type.WORLD_SURFACE, x, z);

        this.targetPos = new BlockPos(x, y, z);

        // Start navigation to the target position
        this.poent.getNavigation().startMovingTo(this.targetPos.getX(), this.targetPos.getY(), this.targetPos.getZ(), this.speed);
    }

    @Override
    public boolean shouldContinue() {
        // Continue if the PoentEntity hasn't reached the target position
        return !this.poent.getNavigation().isIdle() && !this.poent.getBlockPos().isWithinDistance(this.targetPos, 2.0);
    }

    @Override
    public void stop() {
        this.targetPos = null;
        this.poent.getNavigation().stop();
    }

    @Override
    public void tick() {
        // Check if the PoentEntity has reached the target position
        if (this.poent.getBlockPos().isWithinDistance(this.targetPos, 2.0)) {
            this.stop();
        }
    }

    private boolean isInsideCamp() {
        // Check if the PoentEntity is inside the camp's bounding box
        RegistryEntry<Structure> orcCampStructure = this.world.getRegistryManager()
                .get(RegistryKeys.STRUCTURE)
                .getEntry(CustomStructureKeys.ORC_CAMP)
                .orElse(null);

        if (orcCampStructure == null) {
            return false;
        }

        StructureAccessor structureAccessor = this.world.getStructureAccessor();
        StructureStart structureStart = structureAccessor.getStructureAt(this.poent.getBlockPos(), orcCampStructure.value());

        if (structureStart != StructureStart.DEFAULT) {
            BlockBox boundingBox = structureStart.getBoundingBox();
            return boundingBox.contains(this.poent.getBlockPos());
        }

        return false;
    }

    private BlockPos findNearestCamp() {
        // Define the scan box size (e.g., 64 blocks in each direction)
        int scanRadius = 32;

        // Get the PoentEntity's current position
        BlockPos entityPos = this.poent.getBlockPos();

        // Define the scan box boundaries
        BlockPos minPos = entityPos.add(-scanRadius, -scanRadius, -scanRadius);
        BlockPos maxPos = entityPos.add(scanRadius, scanRadius, scanRadius);

        // Get the ORC_CAMP structure type
        RegistryEntry<Structure> orcCampStructure = this.world.getRegistryManager()
                .get(RegistryKeys.STRUCTURE)
                .getEntry(CustomStructureKeys.ORC_CAMP)
                .orElse(null);

        if (orcCampStructure == null) {
            return null;
        }

        // Scan for the camp's bounding box within the defined area
        StructureAccessor structureAccessor = this.world.getStructureAccessor();
        for (int x = minPos.getX(); x <= maxPos.getX(); x += 16) { // Check every chunk
            for (int z = minPos.getZ(); z <= maxPos.getZ(); z += 16) {
                BlockPos chunkPos = new BlockPos(x, entityPos.getY(), z);

                // Check if the chunk contains the ORC_CAMP structure
                StructureStart structureStart = structureAccessor.getStructureAt(chunkPos, orcCampStructure.value());

                if (structureStart != StructureStart.DEFAULT) {
                    // Found the camp! Return the center of its bounding box
                    BlockBox boundingBox = structureStart.getBoundingBox();
                    BlockPos campCenter = boundingBox.getCenter();
                    return campCenter;
                }
            }
        }
        return null;
    }

    private double getCampRadius(BlockPos campCenter) {
        // Calculate the radius of the camp (distance from center to the farthest corner)
        RegistryEntry<Structure> orcCampStructure = this.world.getRegistryManager()
                .get(RegistryKeys.STRUCTURE)
                .getEntry(CustomStructureKeys.ORC_CAMP)
                .orElse(null);

        if (orcCampStructure == null) {
            return 0.0;
        }

        StructureAccessor structureAccessor = this.world.getStructureAccessor();
        StructureStart structureStart = structureAccessor.getStructureAt(campCenter, orcCampStructure.value());

        if (structureStart != StructureStart.DEFAULT) {
            BlockBox boundingBox = structureStart.getBoundingBox();
            double dx = boundingBox.getMaxX() - boundingBox.getMinX();
            double dz = boundingBox.getMaxZ() - boundingBox.getMinZ();
            return Math.sqrt(dx * dx + dz * dz) / 2.0; // Approximate radius
        }

        return 0.0;
    }
}
