package malfu.wandering_orc.entity.client.projectiles;

import malfu.wandering_orc.entity.client.MinotaurModel;
import malfu.wandering_orc.entity.custom.MinotaurEntity;
import malfu.wandering_orc.entity.projectiles.TrollThrowableEntity;
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

public class TrollThrowableRenderer extends GeoEntityRenderer<TrollThrowableEntity> {
    public TrollThrowableRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new TrollThrowableModel());
    }
    @Override
    public RenderLayer getRenderType(TrollThrowableEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }

    //the faceRotation the one that make projectile entity face to target from where the living entity aiming for.
    @Override
    public void preRender(MatrixStack poseStack, TrollThrowableEntity animatable, BakedGeoModel model, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue,
                          float alpha) {
        poseStack.scale(1f, 1f, 1f);
        RenderUtils.faceRotation(poseStack, animatable, partialTick);
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
