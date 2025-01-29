package malfu.wandering_orc.entity.client;

import malfu.wandering_orc.WanderingOrc;
import malfu.wandering_orc.entity.custom.OrcArcherEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class OrcArcherRenderer extends GeoEntityRenderer<OrcArcherEntity> {
    public OrcArcherRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new OrcArcherModel());
    }

    @Override
    public Identifier getTexture(OrcArcherEntity animatable) {
        return new Identifier(WanderingOrc.MOD_ID,"textures/entity/orc_archer.png");
    }
}
