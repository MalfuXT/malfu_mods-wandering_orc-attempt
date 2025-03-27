package malfu.wandering_orc.particle.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class HealCircleParticle extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;

    HealCircleParticle(ClientWorld world, double x, double y, double z, double d, SpriteProvider spriteProvider) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.spriteProvider = spriteProvider;
        this.maxAge = 22;
        this.scale = 1.0F;
        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public int getBrightness(float tint) {
        return 15728880;
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.age++ >= this.maxAge) {
            this.markDead();
        } else {
            this.setSpriteForAge(this.spriteProvider);
        }
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        // Get the camera's position
        Vec3d cameraPos = camera.getPos();

        // Calculate the particle's position relative to the camera
        float x = (float)(this.prevPosX + (this.x - this.prevPosX) * tickDelta - cameraPos.getX());
        float y = (float)(this.prevPosY + (this.y - this.prevPosY) * tickDelta - cameraPos.getY())+0.01f;
        float z = (float)(this.prevPosZ + (this.z - this.prevPosZ) * tickDelta - cameraPos.getZ());

        // Calculate the particle's size
        float size = this.getSize(tickDelta);

        // Get the sprite's UV coordinates
        float minU = this.getMinU();
        float maxU = this.getMaxU();
        float minV = this.getMinV();
        float maxV = this.getMaxV();

        // Build the particle's geometry (facing up)
        vertexConsumer.vertex(x - size, y, z - size)
                .texture(maxU, maxV)
                .color(this.red, this.green, this.blue, this.alpha)
                .light(this.getBrightness(tickDelta))
                .next();
        vertexConsumer.vertex(x - size, y, z + size)
                .texture(maxU, minV)
                .color(this.red, this.green, this.blue, this.alpha)
                .light(this.getBrightness(tickDelta))
                .next();
        vertexConsumer.vertex(x + size, y, z + size)
                .texture(minU, minV)
                .color(this.red, this.green, this.blue, this.alpha)
                .light(this.getBrightness(tickDelta))
                .next();
        vertexConsumer.vertex(x + size, y, z - size)
                .texture(minU, maxV)
                .color(this.red, this.green, this.blue, this.alpha)
                .light(this.getBrightness(tickDelta))
                .next();

        // Build the particle's geometry (facing down)
        vertexConsumer.vertex(x - size, y, z - size)
                .texture(maxU, maxV)
                .color(this.red, this.green, this.blue, this.alpha)
                .light(this.getBrightness(tickDelta))
                .next();
        vertexConsumer.vertex(x + size, y, z - size)
                .texture(minU, maxV)
                .color(this.red, this.green, this.blue, this.alpha)
                .light(this.getBrightness(tickDelta))
                .next();
        vertexConsumer.vertex(x + size, y, z + size)
                .texture(minU, minV)
                .color(this.red, this.green, this.blue, this.alpha)
                .light(this.getBrightness(tickDelta))
                .next();
        vertexConsumer.vertex(x - size, y, z + size)
                .texture(maxU, minV)
                .color(this.red, this.green, this.blue, this.alpha)
                .light(this.getBrightness(tickDelta))
                .next();
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new HealCircleParticle(clientWorld, d, e, f, g, this.spriteProvider);
        }
    }
}
