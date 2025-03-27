package malfu.wandering_orc.entity.client;

import malfu.wandering_orc.WanderingOrc;
import malfu.wandering_orc.entity.custom.FirelinkEntity;
import malfu.wandering_orc.entity.custom.TrollEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class FirelinkModel extends DefaultedEntityGeoModel<FirelinkEntity> {
    public FirelinkModel() {
        super(new Identifier(WanderingOrc.MOD_ID, "firelink"), true);
    }
}
