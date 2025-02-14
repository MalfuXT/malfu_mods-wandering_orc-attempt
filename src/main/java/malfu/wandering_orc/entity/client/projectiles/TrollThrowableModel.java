package malfu.wandering_orc.entity.client.projectiles;

import malfu.wandering_orc.WanderingOrc;
import malfu.wandering_orc.entity.custom.MinotaurEntity;
import malfu.wandering_orc.entity.projectiles.TrollThrowableEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class TrollThrowableModel extends DefaultedEntityGeoModel<TrollThrowableEntity> {
    public TrollThrowableModel() {
        super(new Identifier(WanderingOrc.MOD_ID, "troll_throwable"), true);
    }
}
