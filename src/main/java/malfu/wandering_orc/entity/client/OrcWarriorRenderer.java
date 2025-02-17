package malfu.wandering_orc.entity.client;

import malfu.wandering_orc.entity.custom.MinotaurEntity;
import malfu.wandering_orc.entity.custom.OrcWarriorEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class OrcWarriorRenderer extends GeoEntityRenderer<OrcWarriorEntity> {
    public OrcWarriorRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new OrcWarriorModel());
    }

    @Override
    public float getMotionAnimThreshold(OrcWarriorEntity animatable) {
        return 0.008F; // Adjust this value as needed
    }
}
