package malfu.wandering_orc.entity.client;

import malfu.wandering_orc.WanderingOrc;
import malfu.wandering_orc.entity.custom.OrcArcherEntity;
import malfu.wandering_orc.entity.custom.OrcWarriorEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

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

    @Override
    public void setCustomAnimations(OrcArcherEntity animatable, long instanceId, AnimationState<OrcArcherEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if(head != null) {
            EntityModelData entitydata = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entitydata.headPitch() * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(entitydata.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}