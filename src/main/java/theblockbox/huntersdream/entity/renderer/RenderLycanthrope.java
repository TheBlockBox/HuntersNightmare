package theblockbox.huntersdream.entity.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.api.helpers.TransformationHelper;
import theblockbox.huntersdream.api.interfaces.transformation.ITransformation;
import theblockbox.huntersdream.entity.EntityWerewolf;
import theblockbox.huntersdream.entity.model.ModelWerewolf;
import theblockbox.huntersdream.util.Reference;

import java.util.Optional;

public abstract class RenderLycanthrope<T extends EntityLivingBase> extends RenderLivingBase<T> implements LayerRenderer<T> {
    private static final ResourceLocation WEREWOLF_OVERLAY = GeneralHelper.newResLoc(
            Reference.ENTITY_TEXTURE_PATH + "werewolf/werewolf_overlay.png");

    public RenderLycanthrope(RenderManager manager) {
        super(manager, new ModelWerewolf(), 0.5F);
        this.addLayer(this);
    }

    public static boolean isAlex(Entity entity) {
        if (entity instanceof AbstractClientPlayer) {
            return "slim".equals(((AbstractClientPlayer) entity).getSkinType());
        } else if (entity instanceof EntityWerewolf) {
            return ((EntityWerewolf) entity).usesAlexSkin();
        } else {
            return false;
        }
    }

    @Override
    public ResourceLocation getEntityTexture(T entity) {
        Optional<ITransformation> transformation = TransformationHelper.getITransformation(entity);
        return Transformation.WEREWOLF.getTextures()[(transformation.isPresent() ? transformation.get().getTextureIndex()
                : 0)];
    }

    @Override
    public void doRenderLayer(T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
                              float netHeadYaw, float headPitch, float scale) {
        // add werewolf overlay
        this.bindTexture(RenderLycanthrope.WEREWOLF_OVERLAY);
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(!entity.isInvisible());
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 61680.0F, 0.0F);
        GlStateManager.enableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
        ModelWerewolf model = (ModelWerewolf) this.getMainModel();
        model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, false);
        Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
        int brightness = entity.getBrightnessForRender();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightness % 65536, brightness / 65536.0F);
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}