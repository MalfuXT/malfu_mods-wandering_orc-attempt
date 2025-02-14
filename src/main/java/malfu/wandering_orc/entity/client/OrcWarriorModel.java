package malfu.wandering_orc.entity.client;

import malfu.wandering_orc.WanderingOrc;
import malfu.wandering_orc.entity.custom.OrcWarriorEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class OrcWarriorModel extends DefaultedEntityGeoModel<OrcWarriorEntity> {
    public OrcWarriorModel() {
        super(new Identifier(WanderingOrc.MOD_ID, "orc_warrior"), true);
    }
}
