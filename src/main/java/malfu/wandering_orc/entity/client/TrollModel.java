package malfu.wandering_orc.entity.client;

import malfu.wandering_orc.WanderingOrc;
import malfu.wandering_orc.entity.custom.MinotaurEntity;
import malfu.wandering_orc.entity.custom.TrollEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class TrollModel extends DefaultedEntityGeoModel<TrollEntity> {
    public TrollModel() {
        super(new Identifier(WanderingOrc.MOD_ID, "troll"), true);
    }
}
