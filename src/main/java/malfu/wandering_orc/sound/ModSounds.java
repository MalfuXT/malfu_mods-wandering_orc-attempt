package malfu.wandering_orc.sound;

import malfu.wandering_orc.WanderingOrc;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {

    public static SoundEvent ORC_DEATH = registerSoundEvent("orc_death");
    public static SoundEvent ORC_HURT = registerSoundEvent("orc_hurt");
    public static SoundEvent ORC_HURT2 = registerSoundEvent("orc_hurt2");


    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = new Identifier(WanderingOrc.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerSounds() {
        WanderingOrc.LOGGER.info("[WanderingOrc] Registering sounds for " + WanderingOrc.MOD_ID);
    }
}
