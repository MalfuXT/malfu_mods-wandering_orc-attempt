package malfu.wandering_orc.entity.client;

import malfu.wandering_orc.entity.custom.PoentEntity;
import malfu.wandering_orc.entity.custom.TrollEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PoentRenderer extends GeoEntityRenderer<PoentEntity> {
    public PoentRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new PoentModel());
    }

    @Override
    public float getMotionAnimThreshold(PoentEntity animatable) {
        return 0.008F; // Adjust this value as needed
    }
}
