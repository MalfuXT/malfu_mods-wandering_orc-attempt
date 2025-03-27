package malfu.wandering_orc.item.custom;

import malfu.wandering_orc.entity.projectiles.FireProjectileEntity;
import malfu.wandering_orc.sound.ModSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FireballCrystalItem extends Item {

    public FireballCrystalItem(Settings settings) {
        super(settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPEAR;
    }

    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);

        if (!world.isClient()) {
            world.playSound(null, user.getX(), user.getY(), user.getZ(),
                    ModSounds.FIREBALL_CHARGE, SoundCategory.NEUTRAL, 0.5F, 1.0F);
        }

        return TypedActionResult.consume(itemStack);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        // Check if the user is a player
        if (!(user instanceof PlayerEntity player)) return;

        // Calculate how long the item has been used
        int usedTicks = this.getMaxUseTime(stack) - remainingUseTicks;

        // After 10 ticks of usage, hide the item and spawn break particles
        if (usedTicks == 10)
        {
            Vec3d handPos = calculateHandPosition(player, player.getActiveHand()); // Calculate hand position
            world.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, stack),
                    handPos.getX(), handPos.getY(), handPos.getZ(),
                    0, 0, 0);

            // Play the item break animation
            player.playSound(SoundEvents.BLOCK_GLASS_BREAK, 0.5F, 1.4F + world.random.nextFloat() * 0.4F);
        }

        // After 10 ticks, spawn flame particles on the hand
        if (usedTicks >= 10 && world.isClient()) {
            Vec3d handPos = calculateHandPosition(player, player.getActiveHand()); // Calculate hand position
            for (int i = 0; i < 2; i++) {
                world.addParticle(ParticleTypes.FLAME,
                        handPos.getX(), handPos.getY(), handPos.getZ(),
                        0.02 * (world.random.nextDouble() - 0.5),
                        0.02 * world.random.nextDouble(),
                        0.02 * (world.random.nextDouble() - 0.5));
            }
        }
    }

    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) return;

        int chargeTicks = this.getMaxUseTime(stack) - remainingUseTicks;

        if(chargeTicks >= 10) {
            if (!player.getAbilities().creativeMode) {
                stack.decrement(1);
            }
        }

        if (chargeTicks >= 20) {
            if (!world.isClient()) {
                world.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.FIREBALL_SHOOT, SoundCategory.NEUTRAL, 0.5F, 1.0F);

                // Create the fireball projectile
                FireProjectileEntity fireProjectileEntity = new FireProjectileEntity(world, player, 8);

                // Get the player's rotation vector (direction they are facing)
                Vec3d direction = player.getRotationVec(1.0F);

                // Calculate the spawn position 1 block in front of the player
                double spawnX = player.getX() + direction.x; // X position
                double spawnY = player.getY() + player.getEyeHeight(player.getPose()) + direction.y; // Y position (eye level)
                double spawnZ = player.getZ() + direction.z; // Z position

                // Set the projectile's position
                fireProjectileEntity.setPosition(spawnX, spawnY, spawnZ);

                // Set the projectile's velocity
                fireProjectileEntity.setVelocity(direction.x, direction.y + 0.05, direction.z, 0.8F, 1.0F);

                // Spawn the projectile in the world
                world.spawnEntity(fireProjectileEntity);
            }
            player.getItemCooldownManager().set(stack.getItem(), 150);
        }
    }

    private Vec3d calculateHandPosition(PlayerEntity player, Hand hand) {
        // Adjust these values to fine-tune the hand position
        double xOffset = (hand == Hand.MAIN_HAND) ? 0.4 : -0.4; // Left/right offset (main hand vs offhand)
        double yOffset = 2.0;  // Height offset
        double zOffset = 0.4;  // Forward/backward offset

        // Calculate the hand position based on the player's rotation
        Vec3d rotationVec = player.getRotationVec(1.0F);
        return player.getPos()
                .add(rotationVec.multiply(zOffset))
                .add(0, yOffset, 0)
                .add(rotationVec.crossProduct(new Vec3d(0, 1, 0)).multiply(xOffset));
    }
}
