package malfu.wandering_orc;

import malfu.wandering_orc.entity.ModEntities;
import malfu.wandering_orc.entity.client.*;
import malfu.wandering_orc.entity.client.projectiles.TrollThrowableRenderer;
import malfu.wandering_orc.entity.projectiles.TrollThrowableEntity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class WanderingOrcClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        //Living Entities
        EntityRendererRegistry.register(ModEntities.ORC_ARCHER, OrcArcherRenderer::new);
        EntityRendererRegistry.register(ModEntities.ORC_WARRIOR, OrcWarriorRenderer::new);
        EntityRendererRegistry.register(ModEntities.ORC_CHAMPION, OrcChampionRenderer::new);
        EntityRendererRegistry.register(ModEntities.MINOTAUR, MinotaurRenderer::new);
        EntityRendererRegistry.register(ModEntities.TROLL, TrollRenderer::new);

        //Projectile Entities
        EntityRendererRegistry.register(ModEntities.TROLL_THROWABLE, TrollThrowableRenderer::new);

    }
}