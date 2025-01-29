package malfu.wandering_orc.entity.client;

import malfu.wandering_orc.WanderingOrc;
import malfu.wandering_orc.entity.custom.OrcWarriorEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class OrcWarriorModel extends GeoModel<OrcWarriorEntity> {

    private static final Identifier MODEL_LOC = new Identifier(WanderingOrc.MOD_ID, "geo/orc_warrior.geo.json");
    private static final Identifier TEXTURE_LOC = new Identifier(WanderingOrc.MOD_ID, "textures/entity/orc_warrior.png");
    private static final Identifier ANIMATION_LOC = new Identifier(WanderingOrc.MOD_ID, "animations/orc_warrior.animation.json");

    @Override
    public Identifier getModelResource(OrcWarriorEntity object) {
        return MODEL_LOC;
    }

    @Override
    public Identifier getTextureResource(OrcWarriorEntity animatable) {
        return TEXTURE_LOC;
    }

    @Override
    public Identifier getAnimationResource(OrcWarriorEntity animatable) {
        return ANIMATION_LOC;
    }
}