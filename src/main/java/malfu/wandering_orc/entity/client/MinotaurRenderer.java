package malfu.wandering_orc.entity.client;

import malfu.wandering_orc.entity.custom.MinotaurEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MinotaurRenderer extends GeoEntityRenderer<MinotaurEntity> {
    public MinotaurRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new MinotaurModel());
    }
}
