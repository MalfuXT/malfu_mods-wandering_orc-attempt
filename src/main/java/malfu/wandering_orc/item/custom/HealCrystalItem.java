package malfu.wandering_orc.item.custom;

import malfu.wandering_orc.entity.projectiles.FireProjectileEntity;
import malfu.wandering_orc.sound.ModSounds;
import malfu.wandering_orc.util.ParticleUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class HealCrystalItem extends Item {
    private static final int MAX_DURABILITY = 5; // Simulated durability per item
    private static final int HEAL_INTERVAL = 20; // Heal every 10 ticks (0.5 seconds)
    private static final float HEAL_AMOUNT = 1.0F; // Heal 1 heart per interval
    private static final int COOLDOWN_TICKS = 80;

    public HealCrystalItem(Settings settings) {
        super(settings); // Set max stack size to 16
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (user.getItemCooldownManager().isCoolingDown(this)) {
            return TypedActionResult.fail(stack);
        }
        // Start using the item
        user.setCurrentHand(hand);

        // Play a sound when starting to use the item
        world.playSound(null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.PLAYERS, 1.0F, 1.0F);

        return TypedActionResult.consume(stack);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) return;

        // Heal the player every HEAL_INTERVAL ticks
        if (world.getTime() % HEAL_INTERVAL == 0) {
            // Heal the player
            if (player.getHealth() < player.getMaxHealth()) {
                player.heal(HEAL_AMOUNT);

                // Play a healing sound
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        ModSounds.HEAL, SoundCategory.PLAYERS, 0.5F, 1.0F);
                ParticleUtil.generateHealingParticle(player);

                // Reduce the custom durability
                reduceDurability(world, stack, player);
            }
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000; // Maximum use time (in ticks)
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) return;
        // Play a sound when stopping the use
        if (!world.isClient()) {
            world.playSound(null, user.getX(), user.getY(), user.getZ(),
                    SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }

        player.getItemCooldownManager().set(stack.getItem(), COOLDOWN_TICKS);
    }

    // Helper method to reduce custom durability
    private void reduceDurability(World world, ItemStack stack, PlayerEntity player) {
        // Get or create the NBT tag for the stack
        NbtCompound nbt = stack.getOrCreateNbt();

        // Get the current durability from the NBT tag (default to MAX_DURABILITY if not set)
        int durability = nbt.getInt("Durability");
        if (durability <= 0) {
            durability = MAX_DURABILITY; // Reset durability if not set
        }

        // Reduce durability
        durability--;

        // If durability reaches 0, remove one item from the stack
        if (durability <= 0) {
            stack.decrement(1); // Remove one item from the stack
            durability = MAX_DURABILITY; // Reset durability for the next item
            if (!world.isClient()) {
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS, 1.0F, 1.5F);
            }
            player.stopUsingItem();
        }

        // Save the updated durability to the NBT tag
        nbt.putInt("Durability", durability);
    }

    private int getDurability(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();
        return nbt.getInt("Durability");
    }
}
