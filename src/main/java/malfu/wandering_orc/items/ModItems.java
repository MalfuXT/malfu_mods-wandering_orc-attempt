package malfu.wandering_orc.items;

import malfu.wandering_orc.WanderingOrc;
import malfu.wandering_orc.entity.ModEntities;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item ORC_ARCHER_SPAWN_EGG = registerItem("orc_archer_spawn_egg",
            new SpawnEggItem(ModEntities.ORC_ARCHER, 0x476338, 0x000000  ,new FabricItemSettings()));
    public static final Item ORC_WARRIOR_SPAWN_EGG = registerItem("orc_warrior_spawn_egg",
            new SpawnEggItem(ModEntities.ORC_WARRIOR, 0x476338, 0x6b6b6b  ,new FabricItemSettings()));

    private static void addItemstoIngredientItemGroup(FabricItemGroupEntries entries) {
        entries.add(ORC_ARCHER_SPAWN_EGG);
        entries.add(ORC_WARRIOR_SPAWN_EGG);
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(WanderingOrc.MOD_ID, name), item);
    }

    public static void registerModItems() {
        WanderingOrc.LOGGER.info("Attempt to make the custom mobs" + WanderingOrc.MOD_ID);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(ModItems::addItemstoIngredientItemGroup);
    }
}
