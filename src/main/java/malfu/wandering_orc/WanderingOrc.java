package malfu.wandering_orc;

import malfu.wandering_orc.entity.ModEntities;
import malfu.wandering_orc.entity.custom.OrcArcherEntity;
import malfu.wandering_orc.entity.custom.OrcWarriorEntity;
import malfu.wandering_orc.items.ModItems;
import malfu.wandering_orc.world.gen.ModWorldGeneration;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WanderingOrc implements ModInitializer {
	public static final String MOD_ID = "wandering_orc";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		ModWorldGeneration.generateModWorldGen();

		ModItems.registerModItems();
		FabricDefaultAttributeRegistry.register(ModEntities.ORC_ARCHER, OrcArcherEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.ORC_WARRIOR, OrcWarriorEntity.setAttributes());


	}
}