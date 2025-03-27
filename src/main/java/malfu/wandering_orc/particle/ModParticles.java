package malfu.wandering_orc.particle;

import malfu.wandering_orc.WanderingOrc;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModParticles {
    public static final DefaultParticleType HEAL_CIRCLE = FabricParticleTypes.simple();
    public static final DefaultParticleType HEAL_LINES = FabricParticleTypes.simple();
    public static final DefaultParticleType AREA_HEAL = FabricParticleTypes.simple();
    public static final DefaultParticleType FIRE_EXPLODE = FabricParticleTypes.simple();
    public static final DefaultParticleType MAGIC_EXPLODE = FabricParticleTypes.simple();
    public static final DefaultParticleType SUMMON_HOLE = FabricParticleTypes.simple();

    public static void registerParticles () {
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(WanderingOrc.MOD_ID, "heal_circle"),
                HEAL_CIRCLE);
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(WanderingOrc.MOD_ID, "heal_lines"),
                HEAL_LINES);
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(WanderingOrc.MOD_ID, "area_heal"),
                AREA_HEAL);
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(WanderingOrc.MOD_ID, "fire_explode"),
                FIRE_EXPLODE);
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(WanderingOrc.MOD_ID, "magic_explode"),
                MAGIC_EXPLODE);
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(WanderingOrc.MOD_ID, "summon_hole"),
                SUMMON_HOLE);
    }
}
