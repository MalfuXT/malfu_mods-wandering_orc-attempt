package malfu.wandering_orc;

import malfu.wandering_orc.entity.ModEntities;
import malfu.wandering_orc.entity.client.*;
import malfu.wandering_orc.entity.client.block_entity.BlockScanRenderer;
import malfu.wandering_orc.entity.client.projectiles.FireProjectileRenderer;
import malfu.wandering_orc.entity.client.projectiles.MagicProjectileRenderer;
import malfu.wandering_orc.entity.client.projectiles.TrollThrowableRenderer;
import malfu.wandering_orc.particle.ModParticles;
import malfu.wandering_orc.particle.custom.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
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
        EntityRendererRegistry.register(ModEntities.TROLL_DOCTOR, TrollDoctorRenderer::new);
        EntityRendererRegistry.register(ModEntities.POENT, PoentRenderer::new);
        EntityRendererRegistry.register(ModEntities.ORC_WARLOCK, OrcWarlockRenderer::new);
        EntityRendererRegistry.register(ModEntities.FIRELINK, FirelinkRenderer::new);

        //Projectile Entities
        EntityRendererRegistry.register(ModEntities.TROLL_THROWABLE, TrollThrowableRenderer::new);
        EntityRendererRegistry.register(ModEntities.FIRE_PROJECTILE, FireProjectileRenderer::new);
        EntityRendererRegistry.register(ModEntities.MAGIC_PROJECTILE, MagicProjectileRenderer::new);

        EntityRendererRegistry.register(ModEntities.BLOCK_SCAN_ENTITY, BlockScanRenderer::new);

        //Particles
        ParticleFactoryRegistry.getInstance().register(ModParticles.HEAL_CIRCLE, HealCircleParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.AREA_HEAL, AreaHealCircleParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.HEAL_LINES, HealLinesParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.FIRE_EXPLODE, FireballExplodeParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.MAGIC_EXPLODE, MagicExplodeParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.SUMMON_HOLE, SummonHoleParticle.Factory::new);
    }
}