package malfu.wandering_orc.entity.client;

import malfu.wandering_orc.entity.custom.OrcArcherEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class OrcArcherRenderer extends GeoEntityRenderer<OrcArcherEntity> {
    public OrcArcherRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new OrcArcherModel());
    }
}
