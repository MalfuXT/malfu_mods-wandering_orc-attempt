package malfu.wandering_orc.entity.client.projectiles;

import malfu.wandering_orc.entity.projectiles.FireProjectileEntity;
import malfu.wandering_orc.util.config.CustomGlowingGeoLayer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.RenderUtils;

public class FireProjectileRenderer extends GeoEntityRenderer<FireProjectileEntity> {
    public FireProjectileRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new FireProjectileModel());

        addRenderLayer(new CustomGlowingGeoLayer<>(this));
    }

    @Override
    public RenderLayer getRenderType(FireProjectileEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }

    @Override
    public void preRender(MatrixStack poseStack, FireProjectileEntity animatable, BakedGeoModel model, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue,
                          float alpha) {
        poseStack.scale(1f, 1f, 1f);
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
