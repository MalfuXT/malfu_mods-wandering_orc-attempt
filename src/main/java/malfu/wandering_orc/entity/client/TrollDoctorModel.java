package malfu.wandering_orc.entity.client;

import malfu.wandering_orc.WanderingOrc;
import malfu.wandering_orc.entity.custom.TrollDoctorEntity;
import malfu.wandering_orc.entity.custom.TrollEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class TrollDoctorModel extends DefaultedEntityGeoModel<TrollDoctorEntity> {
    public TrollDoctorModel() {
        super(new Identifier(WanderingOrc.MOD_ID, "troll_doctor"), true);
    }
}
