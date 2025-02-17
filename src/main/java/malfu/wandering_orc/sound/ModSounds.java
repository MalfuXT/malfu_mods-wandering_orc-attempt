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
    public static SoundEvent ORC_ARCHER_PUNCH = registerSoundEvent("orc_archer_punch");
    public static SoundEvent MINO_GROWL = registerSoundEvent("mino_growl");
    public static SoundEvent MINO_DEATH = registerSoundEvent("mino_death");
    public static SoundEvent MINO_HIT = registerSoundEvent("mino_hit");
    public static SoundEvent MINO_STOMP = registerSoundEvent("mino_stomp");
    public static SoundEvent MINO_HURT = registerSoundEvent("mino_hurt");
    public static SoundEvent TROLL_HURT = registerSoundEvent("troll_hurt");
    public static SoundEvent TROLL_DEATH = registerSoundEvent("troll_death");
    public static SoundEvent SWING_SOUND = registerSoundEvent("swing_sound");


    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = new Identifier(WanderingOrc.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerSounds() {
        WanderingOrc.LOGGER.info("[WanderingOrc] Registering sounds for " + WanderingOrc.MOD_ID);
    }
}
