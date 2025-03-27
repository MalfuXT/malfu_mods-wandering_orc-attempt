package malfu.wandering_orc.entity.projectiles;

import malfu.wandering_orc.entity.custom.OrcGroupEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class OrcArrowEntity extends ArrowEntity {
    float damage;
    public OrcArrowEntity(World world, LivingEntity owner, float damage) {
        super(world, owner);
        this.damage = damage;
    }

    protected void onEntityHit(EntityHitResult entityHitResult) {
        // Do not call super.onEntityHit() to prevent the default sound
        if (!this.getWorld().isClient) {
            Entity target = entityHitResult.getEntity();

            // IGNORE PROJECTILE COLLISION AGAINTS TEAMATES OF ORC
            if (target instanceof OrcGroupEntity && this.getOwner() instanceof OrcGroupEntity) {
                OrcGroupEntity orcTarget = (OrcGroupEntity) target;
                OrcGroupEntity shooter = (OrcGroupEntity) this.getOwner();

                if (orcTarget.getTeamOrc().equals(shooter.getTeamOrc())) {
                    return; // Ignore the hit if the target is in the same group
                }
            }

            // Inflict damage and play the custom sound on entity impact
            if (target instanceof LivingEntity) {
                LivingEntity livingTarget = (LivingEntity) target;
                livingTarget.damage(this.getDamageSources().thrown(this, this.getOwner()), this.damage);
            }
            this.discard();
        }
    }
}
