package malfu.wandering_orc.entity.client;

import malfu.wandering_orc.entity.custom.MinotaurEntity;
import malfu.wandering_orc.entity.custom.OrcChampionEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class OrcChampionRenderer extends GeoEntityRenderer<OrcChampionEntity> {
    public OrcChampionRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new OrcChampionModel());
    }

    @Override
    public float getMotionAnimThreshold(OrcChampionEntity animatable) {
        return 0.008F; // Adjust this value as needed
    }
}
