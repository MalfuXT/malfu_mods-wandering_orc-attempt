package malfu.wandering_orc.entity.client;

import malfu.wandering_orc.entity.custom.MinotaurEntity;
import malfu.wandering_orc.entity.custom.TrollEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TrollRenderer extends GeoEntityRenderer<TrollEntity> {
    public TrollRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new TrollModel());
    }

    @Override
    public float getMotionAnimThreshold(TrollEntity animatable) {
        return 0.008F; // Adjust this value as needed
    }
}
