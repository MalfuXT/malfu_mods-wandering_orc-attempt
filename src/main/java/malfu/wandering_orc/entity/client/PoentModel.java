package malfu.wandering_orc.entity.client;

import malfu.wandering_orc.WanderingOrc;
import malfu.wandering_orc.entity.custom.PoentEntity;
import malfu.wandering_orc.entity.custom.TrollEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class PoentModel extends DefaultedEntityGeoModel<PoentEntity> {
    public PoentModel() {
        super(new Identifier(WanderingOrc.MOD_ID, "poent"), true);
    }
}
