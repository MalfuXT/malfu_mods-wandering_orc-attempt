package malfu.wandering_orc.item;

import malfu.wandering_orc.WanderingOrc;
import malfu.wandering_orc.entity.ModEntities;
import malfu.wandering_orc.item.custom.TrollThrowableItem;
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

    public static final Item TROLL_THROWABLE_ITEM = registerItem("weapon/troll_throwable",
            new TrollThrowableItem(new FabricItemSettings().maxCount(16)));
    public static final Item NETHERITE_ARMOR_PIECE = registerItem("netherite_armor_piece",
            new Item(new FabricItemSettings()));

    public static final Item ORC_ARCHER_SPAWN_EGG = registerItem("orc_archer_spawn_egg",
            new SpawnEggItem(ModEntities.ORC_ARCHER, 0x476338, 0x000000  ,new FabricItemSettings()));
    public static final Item ORC_WARRIOR_SPAWN_EGG = registerItem("orc_warrior_spawn_egg",
            new SpawnEggItem(ModEntities.ORC_WARRIOR, 0x476338, 0x6b6b6b  ,new FabricItemSettings()));
    public static final Item MINOTAUR_SPAWN_EGG = registerItem("minotaur_spawn_egg",
            new SpawnEggItem(ModEntities.MINOTAUR, 0x744f33, 0x742424  ,new FabricItemSettings()));
    public static final Item TROLL_SPAWN_EGG = registerItem("troll_spawn_egg",
            new SpawnEggItem(ModEntities.TROLL, 0x427fa0, 0x2e2e2e  ,new FabricItemSettings()));
    public static final Item ORC_CHAMPION_SPAWN_EGG = registerItem("orc_champion_spawn_egg",
            new SpawnEggItem(ModEntities.ORC_CHAMPION, 0x476338, 0x402121  ,new FabricItemSettings()));

    private static void addItemstoIngredientItemGroup(FabricItemGroupEntries entries) {
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(WanderingOrc.MOD_ID, name), item);
    }

    public static void registerModItems() {
        WanderingOrc.LOGGER.info("Attempt to make the custom mobs" + WanderingOrc.MOD_ID);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(ModItems::addItemstoIngredientItemGroup);
    }
}
