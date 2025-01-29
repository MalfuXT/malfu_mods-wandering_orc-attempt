package malfu.wandering_orc.entity.client;

import malfu.wandering_orc.WanderingOrc;
import malfu.wandering_orc.entity.custom.OrcArcherEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class OrcArcherModel extends GeoModel<OrcArcherEntity> {

    private static final Identifier MODEL_LOC = new Identifier(WanderingOrc.MOD_ID, "geo/orc_archer.geo.json");
    private static final Identifier TEXTURE_LOC = new Identifier(WanderingOrc.MOD_ID, "textures/entity/orc_archer.png");
    private static final Identifier ANIMATION_LOC = new Identifier(WanderingOrc.MOD_ID, "animations/orc_archer.animation.json");

    @Override
    public Identifier getModelResource(OrcArcherEntity object) {
        return MODEL_LOC;
    }

    @Override
    public Identifier getTextureResource(OrcArcherEntity animatable) {
        return TEXTURE_LOC;
    }

    @Override
    public Identifier getAnimationResource(OrcArcherEntity animatable) {
        return ANIMATION_LOC;
    }
}