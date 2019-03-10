package theblockbox.huntersdream.api.init;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleCrit;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import theblockbox.huntersdream.api.helpers.GeneralHelper;

import java.util.Objects;
import java.util.Random;

public class ParticleInit {
    public static final EnumParticleTypes BLOOD_PARTICLE = GeneralHelper.addParticle(GeneralHelper.newResLoc("blood"), false);
    public static TextureAtlasSprite bloodParticleTexture = null;

    public static void registerParticles() {
        Minecraft.getMinecraft().effectRenderer.registerParticle(ParticleInit.BLOOD_PARTICLE.getParticleID(),
                (particleID, world, x, y, z, xSpeed, ySpeed, zSpeed, args) -> {
                    Random random = Minecraft.getMinecraft().player.getRNG();
                    double xOffset = MathHelper.nextDouble(random, -0.5D, 0.5D);
                    double zOffset = MathHelper.nextDouble(random, -0.5D, 0.5D);
                    return new ParticleInit.BloodParticle(world, x + xOffset, y +
                            MathHelper.nextDouble(random, 0.5D, 1.8D), z + zOffset,
                            MathHelper.nextDouble(random, xOffset < 0.0D ? -0.4D : 0.0D, xOffset > 0.0D ? 0.4D : 0.0D),
                            -0.2D, MathHelper.nextDouble(random, zOffset < 0.0D ? -0.4D : 0.0D,
                            zOffset > 0.0D ? 0.4D : 0.0D));
                });
    }

    private static class BloodParticle extends ParticleCrit {
        private BloodParticle(World world, double x, double y, double z, double xSpeed, double ySpeed,
                              double zSpeed) {
            super(world, x, y, z, xSpeed, ySpeed, zSpeed, 2.0F);
            this.setParticleTexture(Objects.requireNonNull(ParticleInit.bloodParticleTexture));
        }

        @Override
        public int getFXLayer() {
            return 1;
        }

        @Override
        public void setParticleTextureIndex(int particleTextureIndex) {
        }
    }
}
