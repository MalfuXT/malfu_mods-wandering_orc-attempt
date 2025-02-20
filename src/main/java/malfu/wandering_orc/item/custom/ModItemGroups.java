package malfu.wandering_orc.item.custom;

import malfu.wandering_orc.WanderingOrc;
import malfu.wandering_orc.item.ModItems;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup WANDERING_ORC_ITEM = Registry.register(Registries.ITEM_GROUP,
            new Identifier(WanderingOrc.MOD_ID, "wandering_orc_group"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.wandering_orc_group"))
                    .icon(() -> new ItemStack(ModItems.TROLL_THROWABLE_ITEM)).entries((displayContext, entries) -> {
                        entries.add(ModItems.ORC_ARCHER_SPAWN_EGG);
                        entries.add(ModItems.ORC_WARRIOR_SPAWN_EGG);
                        entries.add(ModItems.MINOTAUR_SPAWN_EGG);
                        entries.add(ModItems.TROLL_SPAWN_EGG);
                        entries.add(ModItems.ORC_CHAMPION_SPAWN_EGG);

                        entries.add(ModItems.NETHERITE_ARMOR_PIECE);

                        entries.add(ModItems.TROLL_THROWABLE_ITEM);

                    }).build());

    public static void registerItemGroups() {
        WanderingOrc.LOGGER.info("Registering Item Groups for " + WanderingOrc.MOD_ID);
    }
}
