package malfu.wandering_orc.entity.client;

import malfu.wandering_orc.WanderingOrc;
import malfu.wandering_orc.entity.custom.OrcWarlockEntity;
import malfu.wandering_orc.entity.custom.TrollEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class OrcWarlockModel extends DefaultedEntityGeoModel<OrcWarlockEntity> {
    public OrcWarlockModel() {
        super(new Identifier(WanderingOrc.MOD_ID, "orc_warlock"), true);
    }
}
