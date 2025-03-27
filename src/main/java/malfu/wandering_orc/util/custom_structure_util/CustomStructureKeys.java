package malfu.wandering_orc.util.custom_structure_util;

import malfu.wandering_orc.WanderingOrc;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.structure.Structure;

public class CustomStructureKeys { //THIS IS FOR DETECTING STRUCTURES FOR POENT

    public static final RegistryKey<Structure> ORC_CAMP = RegistryKey.of(RegistryKeys.STRUCTURE, new Identifier(WanderingOrc.MOD_ID, "orc_camp"));
}
