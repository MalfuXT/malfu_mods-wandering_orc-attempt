package malfu.wandering_orc.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import malfu.wandering_orc.WanderingOrc;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ModTags {
    public static class Blocks {

        private static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, new Identifier(WanderingOrc.MOD_ID, name));
        }
    }

    public static class Items {

        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, new Identifier(WanderingOrc.MOD_ID, name));
        }
    }

    public static class EntityEnemies {

        private static TagKey<EntityType<?>> createTag(String name) {
            return TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(WanderingOrc.MOD_ID, name));
        }

    }

    private static List<EntityType<?>> loadValidEntityTypes(ResourceManager resourceManager, Identifier tagId) {
        List<EntityType<?>> validEntities = new ArrayList<>();

        try {
            // Load the tag file
            Resource resource = resourceManager.getResource(tagId).orElseThrow();
            JsonElement json = JsonParser.parseReader(new InputStreamReader(resource.getInputStream()));

            // Extract the "values" array
            JsonArray values = json.getAsJsonObject().getAsJsonArray("values");

            // Get the entity type registry
            Registry<EntityType<?>> entityTypeRegistry = Registries.ENTITY_TYPE;

            // Validate each entity ID
            for (JsonElement element : values) {
                Identifier entityId = new Identifier(element.getAsString());
                EntityType<?> entityType = entityTypeRegistry.get(entityId);
                if (entityType != null) {
                    validEntities.add(entityType);
                }
            }
        } catch (Exception e) {
            // Log the error and continue
            System.err.println("Failed to load or parse tag: " + tagId);
            e.printStackTrace();
        }

        return validEntities;
    }

    public static List<EntityType<?>> VALID_ORC_ENEMIES;

    public static void initialize(ResourceManager resourceManager) {
        Identifier tagId = new Identifier("wandering_orc", "tags/entity_types/orc_enemies.json");
        VALID_ORC_ENEMIES = loadValidEntityTypes(resourceManager, tagId);
    }
}
