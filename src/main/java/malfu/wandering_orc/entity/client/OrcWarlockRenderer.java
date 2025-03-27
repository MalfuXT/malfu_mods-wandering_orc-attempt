package malfu.wandering_orc.entity.client;

import malfu.wandering_orc.entity.custom.OrcWarlockEntity;
import malfu.wandering_orc.entity.custom.OrcWarriorEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class OrcWarlockRenderer extends GeoEntityRenderer<OrcWarlockEntity> {
    public OrcWarlockRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new OrcWarlockModel());

        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public float getMotionAnimThreshold(OrcWarlockEntity animatable) {
        return 0.008F; // Adjust this value as needed
    }
}
