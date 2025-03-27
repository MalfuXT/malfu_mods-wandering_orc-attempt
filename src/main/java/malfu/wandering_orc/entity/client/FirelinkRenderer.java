package malfu.wandering_orc.entity.client;

import malfu.wandering_orc.entity.custom.FirelinkEntity;
import malfu.wandering_orc.entity.custom.TrollEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class FirelinkRenderer extends GeoEntityRenderer<FirelinkEntity> {
    public FirelinkRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new FirelinkModel());

        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public RenderLayer getRenderType(FirelinkEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }

    @Override
    public float getMotionAnimThreshold(FirelinkEntity animatable) {
        return 0.008F; // Adjust this value as needed
    }
}
