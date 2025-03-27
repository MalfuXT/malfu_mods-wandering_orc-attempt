package malfu.wandering_orc.item;

import malfu.wandering_orc.WanderingOrc;
import malfu.wandering_orc.entity.ModEntities;
import malfu.wandering_orc.item.custom.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item ORC_IRON_AXE = registerItem("weapon/orc_iron_axe",
            new SwordItem(ToolMaterials.IRON,4, -2.6f,
                    new FabricItemSettings()));
    public static final Item ORC_DIAMOND_AXE = registerItem("weapon/orc_diamond_axe",
            new SwordItem(ToolMaterials.DIAMOND,4, -2.6f,
                    new FabricItemSettings()));
    public static final Item ORC_NETHERITE_SWORD = registerItem("weapon/orc_netherite_sword",
            new SwordItem(ToolMaterials.NETHERITE,5, -2.9f,
                    new FabricItemSettings()));

    public static final WarriorArmorItem WARRIOR_HELMET = (WarriorArmorItem) registerItem("armor/warrior_helmet",
            new WarriorArmorItem(ModArmorMaterials.WARRIOR_ARMOR, ArmorItem.Type.HELMET, new FabricItemSettings()));
    public static final WarriorArmorItem WARRIOR_CHESTPLATE = (WarriorArmorItem) registerItem("armor/warrior_chestplate",
            new WarriorArmorItem(ModArmorMaterials.WARRIOR_ARMOR, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));
    public static final WarriorArmorItem WARRIOR_LEGGINGS = (WarriorArmorItem) registerItem("armor/warrior_leggings",
            new WarriorArmorItem(ModArmorMaterials.WARRIOR_ARMOR, ArmorItem.Type.LEGGINGS, new FabricItemSettings()));
    public static final WarriorArmorItem WARRIOR_BOOTS = (WarriorArmorItem) registerItem("armor/warrior_boots",
            new WarriorArmorItem(ModArmorMaterials.WARRIOR_ARMOR, ArmorItem.Type.BOOTS, new FabricItemSettings()));
    public static final WarriorDiamondArmorItem WARRIOR_DIAMOND_HELMET = (WarriorDiamondArmorItem) registerItem("armor/warrior_diamond_helmet",
            new WarriorDiamondArmorItem(ModArmorMaterials.WARRIOR_DIAMOND_ARMOR, ArmorItem.Type.HELMET, new FabricItemSettings()));
    public static final WarriorDiamondArmorItem WARRIOR_DIAMOND_CHESTPLATE = (WarriorDiamondArmorItem) registerItem("armor/warrior_diamond_chestplate",
            new WarriorDiamondArmorItem(ModArmorMaterials.WARRIOR_DIAMOND_ARMOR, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));
    public static final WarriorDiamondArmorItem WARRIOR_DIAMOND_LEGGINGS = (WarriorDiamondArmorItem) registerItem("armor/warrior_diamond_leggings",
            new WarriorDiamondArmorItem(ModArmorMaterials.WARRIOR_DIAMOND_ARMOR, ArmorItem.Type.LEGGINGS, new FabricItemSettings()));
    public static final WarriorDiamondArmorItem WARRIOR_DIAMOND_BOOTS = (WarriorDiamondArmorItem) registerItem("armor/warrior_diamond_boots",
            new WarriorDiamondArmorItem(ModArmorMaterials.WARRIOR_DIAMOND_ARMOR, ArmorItem.Type.BOOTS, new FabricItemSettings()));
    public static final WarriorArmArmorItem WARRIOR_ARM_PLATE = (WarriorArmArmorItem) registerItem("armor/warrior_armplate",
            new WarriorArmArmorItem(ModArmorMaterials.WARRIOR_ARM_ARMOR, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));
    public static final WarriorArmArmorItem WARRIOR_LEATHER_BOOTS = (WarriorArmArmorItem) registerItem("armor/warrior_leather_boots",
            new WarriorArmArmorItem(ModArmorMaterials.WARRIOR_ARM_ARMOR, ArmorItem.Type.BOOTS, new FabricItemSettings()));

    public static final Item TROLL_THROWABLE_ITEM = registerItem("weapon/troll_throwable",
            new TrollThrowableItem(new FabricItemSettings().maxCount(16)));
    public static final Item FIREBALL_CRYSTAL_ITEM = registerItem("crystal_fireball",
            new FireballCrystalItem(new FabricItemSettings().maxCount(16)));
    public static final Item HEAL_CRYSTAL_ITEM = registerItem("crystal_heal",
            new HealCrystalItem(new FabricItemSettings().maxCount(16)));

    public static final Item NETHERITE_ARMOR_PIECE = registerItem("netherite_armor_piece",
            new Item(new FabricItemSettings()));
    public static final Item IRON_ARMOR_PLATE = registerItem("iron_armor_plate",
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
    public static final Item ORC_WARLOCK_SPAWN_EGG = registerItem("orc_warlock_spawn_egg",
            new SpawnEggItem(ModEntities.ORC_WARLOCK, 0x592d2d, 0x476338  ,new FabricItemSettings()));
    public static final Item FIRELINK_SPAWN_EGG = registerItem("firelink_spawn_egg",
            new SpawnEggItem(ModEntities.FIRELINK, 0x280e09, 0xff8b05  ,new FabricItemSettings()));
    public static final Item POENT_SPAWN_EGG = registerItem("poent_spawn_egg",
            new SpawnEggItem(ModEntities.POENT, 0x485e34, 0x432e1a  ,new FabricItemSettings()));
    public static final Item TROLL_DOCTOR_SPAWN_EGG = registerItem("troll_doctor_spawn_egg",
            new SpawnEggItem(ModEntities.TROLL_DOCTOR, 0x427fa0, 0x6929a0  ,new FabricItemSettings()));

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
