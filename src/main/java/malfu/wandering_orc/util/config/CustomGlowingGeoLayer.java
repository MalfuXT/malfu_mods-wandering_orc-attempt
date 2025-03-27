package malfu.wandering_orc.util.config;

import malfu.wandering_orc.entity.projectiles.FireProjectileEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.texture.AutoGlowingTexture;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.RenderUtils;

public class CustomGlowingGeoLayer<T extends GeoAnimatable> extends GeoRenderLayer<T> {
    public CustomGlowingGeoLayer(GeoRenderer<T> renderer) {
        super(renderer);
    }

    protected RenderLayer getRenderType(T animatable) {
        // Automatically detect the glow texture
        return AutoGlowingTexture.getRenderType(this.getTextureResource(animatable));
    }

    public void render(MatrixStack poseStack, T animatable, BakedGeoModel bakedModel, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        // Apply the same transformations as the main renderer
        if (animatable instanceof Entity) {
            RenderUtils.faceRotation(poseStack, (Entity) animatable, partialTick);
        }

        // Get the emissive render type
        RenderLayer emissiveRenderType = this.getRenderType(animatable);

        // Re-render the model with the emissive texture
        this.getRenderer().reRender(
                bakedModel,
                poseStack,
                bufferSource,
                animatable,
                emissiveRenderType,
                bufferSource.getBuffer(emissiveRenderType),
                partialTick,
                15728640, // Full brightness for glowing effect
                packedOverlay,
                1.0F, 1.0F, 1.0F, 1.0F // RGBA (white color)
        );
    }
}
