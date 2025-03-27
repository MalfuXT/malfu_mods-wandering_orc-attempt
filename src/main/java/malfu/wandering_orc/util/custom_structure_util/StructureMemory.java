package malfu.wandering_orc.util.custom_structure_util;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StructureMemory {
    // Map to store block data for each camp (key: bounding box, value: block data)
    public static final Map<BlockBox, Map<BlockPos, BlockState>> ORC_CAMP_BLOCKS = new HashMap<>();
    public static final Map<BlockBox, Map<BlockPos, NbtCompound>> ORC_CAMP_TILE_ENTITIES = new HashMap<>(); // Tile entity data
    public static final Map<Block, Integer> BLOCK_PRIORITIES = new HashMap<>();
    public static final Map<Block, Set<Block>> IGNORED_TRANSITIONS = new HashMap<>();
    private static final int MAX_SAVES_BEFORE_RESET = 30;
    private static int saveCounter = 0;

    static {
        // Priority 3 (High Priority)
        BLOCK_PRIORITIES.put(Blocks.SPRUCE_LOG, 3);
        BLOCK_PRIORITIES.put(Blocks.STRIPPED_SPRUCE_LOG, 3);
        BLOCK_PRIORITIES.put(Blocks.SPRUCE_FENCE, 3);
        BLOCK_PRIORITIES.put(Blocks.SPRUCE_SLAB, 3);
        BLOCK_PRIORITIES.put(Blocks.SPRUCE_TRAPDOOR, 3);
        BLOCK_PRIORITIES.put(Blocks.OAK_TRAPDOOR, 3);
        BLOCK_PRIORITIES.put(Blocks.DARK_OAK_TRAPDOOR, 3);
        BLOCK_PRIORITIES.put(Blocks.SPRUCE_STAIRS, 3);
        BLOCK_PRIORITIES.put(Blocks.COBBLESTONE, 3);
        BLOCK_PRIORITIES.put(Blocks.CAMPFIRE, 3);
        BLOCK_PRIORITIES.put(Blocks.SPAWNER, 3);
        BLOCK_PRIORITIES.put(Blocks.BROWN_WOOL, 3);

        // Priority 2 (Medium Priority)
        BLOCK_PRIORITIES.put(Blocks.BLUE_WOOL, 2);
        BLOCK_PRIORITIES.put(Blocks.LIME_WOOL, 2);
        BLOCK_PRIORITIES.put(Blocks.GREEN_WOOL, 2);
        BLOCK_PRIORITIES.put(Blocks.LADDER, 2);
        BLOCK_PRIORITIES.put(Blocks.COBBLESTONE_WALL, 2);
        BLOCK_PRIORITIES.put(Blocks.COBBLESTONE_SLAB, 2);
        BLOCK_PRIORITIES.put(Blocks.RED_BANNER, 2);
        BLOCK_PRIORITIES.put(Blocks.RED_WALL_BANNER, 2);
        BLOCK_PRIORITIES.put(Blocks.BROWN_BANNER, 2);
        BLOCK_PRIORITIES.put(Blocks.BROWN_WALL_BANNER, 2);
        BLOCK_PRIORITIES.put(Blocks.ANDESITE, 2);
        BLOCK_PRIORITIES.put(Blocks.SKELETON_SKULL, 2);
        BLOCK_PRIORITIES.put(Blocks.TORCH, 2);
        BLOCK_PRIORITIES.put(Blocks.WATER, 2);

        // Priority 1 (Low Priority)
        BLOCK_PRIORITIES.put(Blocks.COARSE_DIRT, 1);
        BLOCK_PRIORITIES.put(Blocks.GRASS_BLOCK, 1);
        BLOCK_PRIORITIES.put(Blocks.DIRT_PATH, 1);

        // Default priority (if not specified)
        BLOCK_PRIORITIES.put(null, 0);
    }

    static {
        // Grass Block can turn into Dirt, and vice versa
        IGNORED_TRANSITIONS.put(Blocks.GRASS_BLOCK, Set.of(Blocks.DIRT));
        IGNORED_TRANSITIONS.put(Blocks.DIRT, Set.of(Blocks.GRASS_BLOCK));
    }

    // List of blocks to monitor
    public static final Set<Block> BLOCKS_TO_MONITOR = Set.of(
            Blocks.SPRUCE_LOG,
            Blocks.BROWN_WOOL,
            Blocks.STRIPPED_SPRUCE_LOG,
            Blocks.SPRUCE_FENCE,
            Blocks.SPRUCE_SLAB,
            Blocks.SPRUCE_TRAPDOOR,
            Blocks.OAK_TRAPDOOR,
            Blocks.DARK_OAK_TRAPDOOR,
            Blocks.SPRUCE_STAIRS,
            Blocks.COBBLESTONE,
            Blocks.CAMPFIRE,
            Blocks.BLUE_WOOL,
            Blocks.LIME_WOOL,
            Blocks.GREEN_WOOL,
            Blocks.LADDER,
            Blocks.COBBLESTONE_WALL,
            Blocks.COBBLESTONE_SLAB,
            Blocks.RED_BANNER,
            Blocks.RED_WALL_BANNER,
            Blocks.BROWN_BANNER,
            Blocks.BROWN_WALL_BANNER,
            Blocks.ANDESITE,
            Blocks.SKELETON_SKULL,
            Blocks.COARSE_DIRT,
            Blocks.GRASS_BLOCK,
            Blocks.DIRT,
            Blocks.TORCH,
            Blocks.DIRT_PATH,
            Blocks.FARMLAND,
            Blocks.SPAWNER,
            Blocks.WATER
            // Add more blocks here as needed
    );

    // Save blocks for a specific camp
    public static void saveCampBlocks(BlockBox boundingBox, ServerWorld world) {
        // Skip if this camp is already saved
        if (ORC_CAMP_BLOCKS.containsKey(boundingBox)) {
            System.out.println("Camp already saved. Skipping...");
            return;
        }

        Map<BlockPos, BlockState> blocks = new HashMap<>();
        Map<BlockPos, NbtCompound> tileEntityData = new HashMap<>(); // Store tile entity data

        // Iterate through all blocks within the bounding box
        for (int x = boundingBox.getMinX(); x <= boundingBox.getMaxX(); x++) {
            for (int y = boundingBox.getMinY(); y <= boundingBox.getMaxY(); y++) {
                for (int z = boundingBox.getMinZ(); z <= boundingBox.getMaxZ(); z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = world.getBlockState(pos);

                    // Skip water blocks that are not full (flow level != 0)
                    if (state.getBlock() == Blocks.WATER && state.get(FluidBlock.LEVEL) != 0) {
                        continue;
                    }

                    // Only save specific blocks
                    if (BLOCKS_TO_MONITOR.contains(state.getBlock())) {
                        blocks.put(pos, state);

                        // Save tile entity data for spawners
                        if (state.getBlock() == Blocks.SPAWNER) {
                            BlockEntity blockEntity = world.getBlockEntity(pos);
                            if (blockEntity instanceof MobSpawnerBlockEntity) {
                                NbtCompound nbt = blockEntity.createNbt();
                                tileEntityData.put(pos, nbt);
                                System.out.println("Saved spawner data at " + pos);
                            }
                        }
                    }
                }
            }
        }

        ORC_CAMP_BLOCKS.put(boundingBox, blocks);
        ORC_CAMP_TILE_ENTITIES.put(boundingBox, tileEntityData); // Save tile entity data
        System.out.println("Saved " + blocks.size() + " blocks for camp at " + boundingBox);
    }

    // Get blocks for a specific camp
    public static Map<BlockPos, BlockState> getBlocksForCamp(BlockBox boundingBox) {
        return ORC_CAMP_BLOCKS.getOrDefault(boundingBox, new HashMap<>());
    }

    // Reset memory for a specific camp
    public static void resetCamp(BlockBox boundingBox) {
        ORC_CAMP_BLOCKS.remove(boundingBox);
        System.out.println("Reset memory for camp at " + boundingBox);
    }

    // Save and load methods (updated to handle multiple camps)
    public static NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putInt("saveCounter", saveCounter); // Add this line
        NbtList campList = new NbtList();

        for (Map.Entry<BlockBox, Map<BlockPos, BlockState>> entry : ORC_CAMP_BLOCKS.entrySet()) {
            NbtCompound campNbt = new NbtCompound();
            BlockBox boundingBox = entry.getKey();

            // Save bounding box
            campNbt.putIntArray("boundingBox", new int[] {
                    boundingBox.getMinX(), boundingBox.getMinY(), boundingBox.getMinZ(),
                    boundingBox.getMaxX(), boundingBox.getMaxY(), boundingBox.getMaxZ()
            });

            // Save block data
            NbtList blockList = new NbtList();
            for (Map.Entry<BlockPos, BlockState> blockEntry : entry.getValue().entrySet()) {
                NbtCompound blockNbt = new NbtCompound();
                blockNbt.putIntArray("pos", new int[] {
                        blockEntry.getKey().getX(), blockEntry.getKey().getY(), blockEntry.getKey().getZ()
                });
                blockNbt.putInt("state", Block.getRawIdFromState(blockEntry.getValue()));
                blockList.add(blockNbt);
            }
            campNbt.put("blocks", blockList);

            // Save tile entity data
            Map<BlockPos, NbtCompound> tileEntities = ORC_CAMP_TILE_ENTITIES.get(boundingBox);
            if (tileEntities != null) {
                NbtList tileEntityList = new NbtList();
                for (Map.Entry<BlockPos, NbtCompound> tileEntityEntry : tileEntities.entrySet()) {
                    NbtCompound tileEntityNbt = new NbtCompound();
                    tileEntityNbt.putIntArray("pos", new int[] {
                            tileEntityEntry.getKey().getX(), tileEntityEntry.getKey().getY(), tileEntityEntry.getKey().getZ()
                    });
                    tileEntityNbt.put("data", tileEntityEntry.getValue());
                    tileEntityList.add(tileEntityNbt);
                }
                campNbt.put("tileEntities", tileEntityList);
            }

            campList.add(campNbt);
        }

        nbt.put("camps", campList);
        return nbt;
    }

    public static void fromNbt(NbtCompound nbt) {
        ORC_CAMP_BLOCKS.clear();
        ORC_CAMP_TILE_ENTITIES.clear();

        NbtList campList = nbt.getList("camps", NbtElement.COMPOUND_TYPE);

        for (NbtElement element : campList) {
            NbtCompound campNbt = (NbtCompound) element;
            int[] boundingBoxArray = campNbt.getIntArray("boundingBox");
            BlockBox boundingBox = new BlockBox(
                    boundingBoxArray[0], boundingBoxArray[1], boundingBoxArray[2],
                    boundingBoxArray[3], boundingBoxArray[4], boundingBoxArray[5]
            );

            // Load block data
            NbtList blockList = campNbt.getList("blocks", NbtElement.COMPOUND_TYPE);
            Map<BlockPos, BlockState> blocks = new HashMap<>();

            for (NbtElement blockElement : blockList) {
                NbtCompound blockNbt = (NbtCompound) blockElement;
                int[] posArray = blockNbt.getIntArray("pos");
                BlockPos pos = new BlockPos(posArray[0], posArray[1], posArray[2]);
                int stateId = blockNbt.getInt("state");
                BlockState state = Block.getStateFromRawId(stateId);
                blocks.put(pos, state);
            }

            ORC_CAMP_BLOCKS.put(boundingBox, blocks);

            // Load tile entity data
            if (campNbt.contains("tileEntities", NbtElement.LIST_TYPE)) {
                NbtList tileEntityList = campNbt.getList("tileEntities", NbtElement.COMPOUND_TYPE);
                Map<BlockPos, NbtCompound> tileEntities = new HashMap<>();

                for (NbtElement tileEntityElement : tileEntityList) {
                    NbtCompound tileEntityNbt = (NbtCompound) tileEntityElement;
                    int[] posArray = tileEntityNbt.getIntArray("pos");
                    BlockPos pos = new BlockPos(posArray[0], posArray[1], posArray[2]);
                    NbtCompound data = tileEntityNbt.getCompound("data");
                    tileEntities.put(pos, data);
                }

                ORC_CAMP_TILE_ENTITIES.put(boundingBox, tileEntities);
            }
        }
    }

    public static void save(MinecraftServer server) {
        try {
            // Increment save counter
            saveCounter++;

            // Check if we should reset
            if (saveCounter >= MAX_SAVES_BEFORE_RESET) {
                ORC_CAMP_BLOCKS.clear();
                ORC_CAMP_TILE_ENTITIES.clear();
                saveCounter = 0;
                System.out.println("StructureMemory automatically reset after " + MAX_SAVES_BEFORE_RESET + " saves");
            }

            // Get the save file path
            Path savePath = server.getSavePath(WorldSavePath.ROOT).resolve("wandering_orc_structure_memory.dat");

            // Convert StructureMemory to NBT
            NbtCompound nbt = StructureMemory.toNbt();

            // Write NBT to file
            NbtIo.writeCompressed(nbt, savePath.toFile());
            System.out.println("StructureMemory saved to disk. Save count: " + saveCounter);
        } catch (IOException e) {
            System.err.println("Failed to save StructureMemory: " + e.getMessage());
        }
    }

    public static void load(MinecraftServer server) {
        try {
            // Get the save file path
            Path savePath = server.getSavePath(WorldSavePath.ROOT).resolve("wandering_orc_structure_memory.dat");

            // Check if the file exists
            if (Files.exists(savePath)) {
                // Read NBT from file
                NbtCompound nbt = NbtIo.readCompressed(savePath.toFile());
                if (nbt != null) {
                    StructureMemory.fromNbt(nbt);
                    // Initialize counter from file if needed
                    if (nbt.contains("saveCounter")) {
                        saveCounter = nbt.getInt("saveCounter");
                    }
                    System.out.println("StructureMemory loaded from disk. Save count: " + saveCounter);
                }
            } else {
                System.out.println("No StructureMemory save file found.");
            }
        } catch (IOException e) {
            System.err.println("Failed to load StructureMemory: " + e.getMessage());
        }
    }
}
