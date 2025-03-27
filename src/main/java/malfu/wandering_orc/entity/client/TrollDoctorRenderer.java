package malfu.wandering_orc.entity.client;

import malfu.wandering_orc.entity.custom.TrollDoctorEntity;
import malfu.wandering_orc.entity.custom.TrollEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class TrollDoctorRenderer extends GeoEntityRenderer<TrollDoctorEntity> {
    public TrollDoctorRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new TrollDoctorModel());

        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public float getMotionAnimThreshold(TrollDoctorEntity animatable) {
        return 0.008F; // Adjust this value as needed
    }
}
