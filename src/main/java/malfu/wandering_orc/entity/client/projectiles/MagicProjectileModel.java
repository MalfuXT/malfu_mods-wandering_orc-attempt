package malfu.wandering_orc.entity.client.projectiles;

import malfu.wandering_orc.WanderingOrc;
import malfu.wandering_orc.entity.projectiles.FireProjectileEntity;
import malfu.wandering_orc.entity.projectiles.MagicProjectileEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class MagicProjectileModel extends DefaultedEntityGeoModel<MagicProjectileEntity> {
    public MagicProjectileModel() {
        super(new Identifier(WanderingOrc.MOD_ID, "magic_projectile"), true);
    }
}
