package malfu.wandering_orc;

import malfu.wandering_orc.entity.ModEntities;
import malfu.wandering_orc.entity.client.OrcArcherRenderer;
import malfu.wandering_orc.entity.client.OrcWarriorRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class WanderingOrcClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.ORC_ARCHER, OrcArcherRenderer::new);
        EntityRendererRegistry.register(ModEntities.ORC_WARRIOR, OrcWarriorRenderer::new);

    }
}