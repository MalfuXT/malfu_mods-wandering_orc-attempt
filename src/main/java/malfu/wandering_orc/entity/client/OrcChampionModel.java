package malfu.wandering_orc.entity.client;

import malfu.wandering_orc.WanderingOrc;
import malfu.wandering_orc.entity.custom.MinotaurEntity;
import malfu.wandering_orc.entity.custom.OrcChampionEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class OrcChampionModel extends DefaultedEntityGeoModel<OrcChampionEntity> {
    public OrcChampionModel() {
        super(new Identifier(WanderingOrc.MOD_ID, "orc_champion"), true);
    }
}
