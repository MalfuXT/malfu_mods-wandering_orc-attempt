package malfu.wandering_orc.entity.client;

import malfu.wandering_orc.WanderingOrc;
import malfu.wandering_orc.entity.custom.OrcArcherEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class OrcArcherModel extends DefaultedEntityGeoModel<OrcArcherEntity> {
    public OrcArcherModel() {
        super(new Identifier(WanderingOrc.MOD_ID, "orc_archer"), true);
    }
}