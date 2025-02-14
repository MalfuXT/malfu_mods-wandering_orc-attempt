package malfu.wandering_orc.item.custom;

import malfu.wandering_orc.entity.projectiles.TrollThrowableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TrollThrowableItem extends Item {
    public TrollThrowableItem(Settings settings) {
        super(settings);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 1.0F);

        if (!world.isClient) {
            TrollThrowableEntity trollThrowableEntity = new TrollThrowableEntity(world, player, 5);

            Vec3d direction = player.getRotationVec(1.0F);
            trollThrowableEntity.setVelocity(direction.x, direction.y, direction.z, 1.1F, 1.0F);

            trollThrowableEntity.setPosition(player.getX(), player.getY() + 0.75, player.getZ());

            world.spawnEntity(trollThrowableEntity);
        }
        player.getItemCooldownManager().set(this, 10);
        if (!player.getAbilities().creativeMode) {
            itemStack.decrement(1);
        }

        player.incrementStat(Stats.USED.getOrCreateStat(this));
        return TypedActionResult.success(itemStack, world.isClient());
    }
}
