package malfu.wandering_orc.util;

import malfu.wandering_orc.WanderingOrc;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTags {
    public static class Blocks {
    }

    public static class Items {
    }

    public static class EntityEnemies {
        public static final TagKey<EntityType<?>> ORC_ENEMIES = createTag("orc_enemies");

        private static TagKey<EntityType<?>> createTag(String name) {
            return TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(WanderingOrc.MOD_ID, name));
        }

    }
}
