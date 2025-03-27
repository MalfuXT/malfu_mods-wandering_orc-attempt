package malfu.wandering_orc.util;

import malfu.wandering_orc.sound.ModSounds;
import net.minecraft.entity.LivingEntity;

import java.util.Random;

public class SoundUtil {
    private static final Random RANDOM = new Random();

    public static void sharpImpact(LivingEntity livingEntity, float minPitch, float maxPitch) {
        // Generate a random pitch between minPitch and maxPitch
        float randomPitch = minPitch + RANDOM.nextFloat() * (maxPitch - minPitch);
        livingEntity.playSound(ModSounds.SHARP_IMPACT, 0.5F, randomPitch);
    }

    public static void CrackedGround(LivingEntity livingEntity, float minPitch, float maxPitch) {
        // Generate a random pitch between minPitch and maxPitch
        float randomPitch = minPitch + RANDOM.nextFloat() * (maxPitch - minPitch);
        livingEntity.playSound(ModSounds.CRACKED_GROUND, 0.5F, randomPitch);
    }

    public static void MinoHit(LivingEntity livingEntity, float minPitch, float maxPitch) {
        // Generate a random pitch between minPitch and maxPitch
        float randomPitch = minPitch + RANDOM.nextFloat() * (maxPitch - minPitch);
        livingEntity.playSound(ModSounds.MINO_HIT, 0.5F, randomPitch);
    }
}
