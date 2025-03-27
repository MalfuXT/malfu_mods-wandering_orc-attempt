package malfu.wandering_orc.entity.client.projectiles;

import malfu.wandering_orc.WanderingOrc;
import malfu.wandering_orc.entity.projectiles.FireProjectileEntity;
import malfu.wandering_orc.entity.projectiles.TrollThrowableEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class FireProjectileModel extends DefaultedEntityGeoModel<FireProjectileEntity> {
    public FireProjectileModel() {
        super(new Identifier(WanderingOrc.MOD_ID, "fire_projectile"), true);
    }
}
