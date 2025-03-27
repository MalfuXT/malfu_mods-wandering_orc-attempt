package malfu.wandering_orc.item;

import malfu.wandering_orc.WanderingOrc;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup WANDERING_ORC_ITEM = Registry.register(Registries.ITEM_GROUP,
            new Identifier(WanderingOrc.MOD_ID, "wandering_orc_group"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.wandering_orc_group"))
                    .icon(() -> new ItemStack(ModItems.WARRIOR_HELMET)).entries((displayContext, entries) -> {


                        entries.add(ModItems.WARRIOR_ARM_PLATE);
                        entries.add(ModItems.WARRIOR_LEATHER_BOOTS);
                        entries.add(ModItems.WARRIOR_HELMET);
                        entries.add(ModItems.WARRIOR_CHESTPLATE);
                        entries.add(ModItems.WARRIOR_LEGGINGS);
                        entries.add(ModItems.WARRIOR_BOOTS);
                        entries.add(ModItems.WARRIOR_DIAMOND_HELMET);
                        entries.add(ModItems.WARRIOR_DIAMOND_CHESTPLATE);
                        entries.add(ModItems.WARRIOR_DIAMOND_LEGGINGS);
                        entries.add(ModItems.WARRIOR_DIAMOND_BOOTS);



                        entries.add(ModItems.ORC_IRON_AXE);
                        entries.add(ModItems.ORC_DIAMOND_AXE);
                        entries.add(ModItems.ORC_NETHERITE_SWORD);

                        entries.add(ModItems.NETHERITE_ARMOR_PIECE);
                        entries.add(ModItems.IRON_ARMOR_PLATE);

                        entries.add(ModItems.FIREBALL_CRYSTAL_ITEM);
                        entries.add(ModItems.HEAL_CRYSTAL_ITEM);
                        entries.add(ModItems.TROLL_THROWABLE_ITEM);

                        entries.add(ModItems.ORC_ARCHER_SPAWN_EGG);
                        entries.add(ModItems.ORC_WARRIOR_SPAWN_EGG);
                        entries.add(ModItems.MINOTAUR_SPAWN_EGG);
                        entries.add(ModItems.TROLL_SPAWN_EGG);
                        entries.add(ModItems.ORC_CHAMPION_SPAWN_EGG);
                        entries.add(ModItems.ORC_WARLOCK_SPAWN_EGG);
                        entries.add(ModItems.TROLL_DOCTOR_SPAWN_EGG);
                        entries.add(ModItems.POENT_SPAWN_EGG);
                        entries.add(ModItems.FIRELINK_SPAWN_EGG);

                    }).build());

    public static void registerItemGroups() {
        WanderingOrc.LOGGER.info("Registering Item Groups for " + WanderingOrc.MOD_ID);
    }
}
