package malfu.wandering_orc.entity.client.block_entity;

import malfu.wandering_orc.entity.ModEntities;
import malfu.wandering_orc.entity.block_entity.BlockScanEntity;
import malfu.wandering_orc.entity.custom.MinotaurEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BlockScanRenderer extends GeoEntityRenderer<BlockScanEntity> {
    public BlockScanRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new BlockScanModel());
    }

    @Override
    public void render(BlockScanEntity entity, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {
        BlockState blockState = entity.getBlockState();
        float alpha = entity.getAlpha();

        // Apply the entity's rotation
        poseStack.push();
        poseStack.translate(0.5, 0.5, 0.5); // Center the entity
        poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(entity.getYaw())); // Apply yaw rotation
        poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(entity.getPitch())); // Apply pitch rotation
        poseStack.translate(-0.5, -0.5, -0.5); // Revert translation

        // Render the block state dynamically
        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(blockState, poseStack, bufferSource, 15728640, OverlayTexture.DEFAULT_UV);
        poseStack.pop();
    }
}
