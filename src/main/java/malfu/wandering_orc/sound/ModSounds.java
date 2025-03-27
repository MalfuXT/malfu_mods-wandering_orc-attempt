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
    public static SoundEvent POENT_HURT = registerSoundEvent("poent_hurt");
    public static SoundEvent POENT_DEATH = registerSoundEvent("poent_death");
    public static SoundEvent POENT_DONE = registerSoundEvent("poent_done");
    public static SoundEvent POENT_COMPLAINT = registerSoundEvent("poent_complaint");
    public static SoundEvent POENT_AMBIENCE = registerSoundEvent("poent_ambience");
    public static SoundEvent FIRELINK_AMBIENCE = registerSoundEvent("firelink_ambience");
    public static SoundEvent FIRELINK_HURT = registerSoundEvent("firelink_hurt");
    public static SoundEvent FIRELINK_SWING = registerSoundEvent("firelink_swing");


    public static SoundEvent SWING_SOUND = registerSoundEvent("swing_sound");
    public static SoundEvent SWING_LIGHT = registerSoundEvent("swing_light");
    public static SoundEvent CRACKED_GROUND = registerSoundEvent("cracked_ground");
    public static SoundEvent SHARP_IMPACT = registerSoundEvent("sharp_impact");
    public static SoundEvent SHIELD_STANCE = registerSoundEvent("shield_stance");
    public static SoundEvent FIREBALL_IMPACT = registerSoundEvent("fireball_impact");
    public static SoundEvent FIREBALL_SHOOT = registerSoundEvent("fireball_shoot");
    public static SoundEvent FIREBALL_CHARGE = registerSoundEvent("fireball_charge");
    public static SoundEvent FIREBALL_TRAVEL = registerSoundEvent("fireball_travel");
    public static SoundEvent MAGIC_IMPACT = registerSoundEvent("magic_impact");
    public static SoundEvent MAGIC_SHOOT = registerSoundEvent("magic_shoot");
    public static SoundEvent MAGIC_CHARGE = registerSoundEvent("magic_charge");
    public static SoundEvent SUMMON_CHARGE = registerSoundEvent("summon_charge");
    public static SoundEvent SUMMON = registerSoundEvent("summon");
    public static SoundEvent HEAL = registerSoundEvent("heal");
    public static SoundEvent AREA_HEAL = registerSoundEvent("area_heal");


    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = new Identifier(WanderingOrc.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerSounds() {
        WanderingOrc.LOGGER.info("[WanderingOrc] Registering sounds for " + WanderingOrc.MOD_ID);
    }
}
