package malfu.wandering_orc;

import malfu.wandering_orc.entity.ModEntities;
import malfu.wandering_orc.entity.custom.*;
import malfu.wandering_orc.item.ModItems;
import malfu.wandering_orc.item.ModItemGroups;
import malfu.wandering_orc.particle.ModParticles;
import malfu.wandering_orc.sound.ModSounds;
import malfu.wandering_orc.util.config.ModBonusHealthConfig;
import malfu.wandering_orc.util.ModTags;
import malfu.wandering_orc.util.config.SpawnConfig;
import malfu.wandering_orc.util.custom_structure_util.StructureMemory;
import malfu.wandering_orc.world.gen.ModWorldGeneration;
import malfu.wandering_orc.world.gen.SpawnGroupManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class WanderingOrc implements ModInitializer {
	public static final String MOD_ID = "wandering_orc";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// Register a resource reload listener
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public Identifier getFabricId() {
				return new Identifier("wandering_orc", "orc_enemies_tag_loader");
			}

			@Override
			public void reload(ResourceManager resourceManager) {
				// Load the validated entity types
				ModTags.initialize(resourceManager);
			}
		});


		//SAVE AND LOAD FOR POENT
		ServerLifecycleEvents.SERVER_STARTING.register(StructureMemory::load);
		ServerLifecycleEvents.SERVER_STOPPING.register(StructureMemory::save);
		ServerWorldEvents.LOAD.register((server, world) -> {
			if (world.getRegistryKey() == World.OVERWORLD) { // Only reload for the overworld
				StructureMemory.load(server);
				System.out.println("Reloaded structure memory after world reload.");
			}
		});

		//CONFIG REGISTER HERE
		File configDir = new File(FabricLoader.getInstance().getConfigDir().toFile(), "wandering_orc");
		ModBonusHealthConfig.loadConfig(configDir);
		SpawnConfig.loadConfig(configDir);

		//ClassRegister
		ModItemGroups.registerItemGroups();
		ModWorldGeneration.generateModWorldGen();
		ModSounds.registerSounds();
		ModItems.registerModItems();
		SpawnGroupManager.register();
		ModParticles.registerParticles();

		//Living Entities
		FabricDefaultAttributeRegistry.register(ModEntities.ORC_ARCHER, OrcArcherEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.ORC_WARRIOR, OrcWarriorEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.ORC_CHAMPION, OrcChampionEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.MINOTAUR, MinotaurEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.TROLL, TrollEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.POENT, PoentEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.TROLL_DOCTOR, TrollDoctorEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.ORC_WARLOCK, OrcWarlockEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.FIRELINK, FirelinkEntity.setAttributes());

	}
}