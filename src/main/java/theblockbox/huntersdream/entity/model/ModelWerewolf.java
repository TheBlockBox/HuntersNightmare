package theblockbox.huntersdream.entity.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import theblockbox.huntersdream.api.helpers.WerewolfHelper;
import theblockbox.huntersdream.util.handlers.ConfigHandler;

public class ModelWerewolf extends ModelBiped {
    public ModelRenderer tail;
    public ModelRenderer bodyfurb;
    public ModelRenderer bodyfurf;
    public ModelRenderer snout;
    public ModelRenderer earr;
    public ModelRenderer earl;
    public ModelRenderer facefurl;
    public ModelRenderer facefurr;
    public ModelRenderer earr2;
    public ModelRenderer earl2;
    public ModelRenderer arml2;
    public ModelRenderer clawarml;
    public ModelRenderer clawarml2;
    public ModelRenderer clawarml3;
    public ModelRenderer legl2;
    public ModelRenderer clawlegl;
    public ModelRenderer clawlegl2;
    public ModelRenderer clawlegl3;
    public ModelRenderer armr2;
    public ModelRenderer clawarmr;
    public ModelRenderer clawarmr2;
    public ModelRenderer clawarmr3;
    public ModelRenderer legr2;
    public ModelRenderer clawlegr;
    public ModelRenderer clawlegr2;
    public ModelRenderer clawlegr3;
    protected boolean isTailGoingUp = false;

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public static void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, true);
    }

    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
                       float headPitch, float scale, boolean moveTail) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn, moveTail);
        if (this.isSneak) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.18F, 0.0F);
            this.bipedBody.render(scale);
            GlStateManager.popMatrix();
        } else {
            this.bipedBody.render(scale);
        }
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn, true);
    }

    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
                                  float headPitch, float scaleFactor, Entity entityIn, boolean moveTail) {
        scaleFactor *= 0.9F;
        boolean isBiting = ConfigHandler.client.biteAnimation && (entityIn instanceof EntityPlayer)
                && ((entityIn.world.getTotalWorldTime() - WerewolfHelper.getBiteTicks((EntityPlayer) entityIn)) < 4);
        this.isSneak = isBiting || entityIn.isSneaking();
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
        if (isBiting) {
            this.bipedRightArm.rotateAngleX = -1.0F;
            this.bipedLeftArm.rotateAngleX = -1.0F;
        }

        // fix legs
        this.bipedRightLeg.rotateAngleX -= this.isSneak ? 1.2D : 0.3D;
        this.bipedLeftLeg.rotateAngleX -= this.isSneak ? 1.2D : 0.3D;
        // fix head and arms when sneaking
        if (this.isSneak) {
            this.bipedHead.rotateAngleX -= 0.4D;
            this.bipedRightArm.rotateAngleX -= 0.65D;
            this.bipedLeftArm.rotateAngleX -= 0.65D;
            this.bipedRightLeg.rotationPointY = 13.2F;
            this.bipedLeftLeg.rotationPointY = 13.2F;
            this.bipedRightLeg.rotationPointZ = 2.0F;
            this.bipedLeftLeg.rotationPointZ = 2.0F;
        }
        if (moveTail) {
            if (limbSwingAmount >= 0.2F) {
                // animate
                // if it's going up and bigger than 0.2, go down
                // if it's going down and less than -0.6, go up
                this.isTailGoingUp = this.tail.rotateAngleX < (this.isTailGoingUp ? 1.4F : 0.6F);
                this.tail.rotateAngleX += this.isTailGoingUp ? 0.01F : -0.01F;
            } else if (this.tail.rotateAngleX > 1.95F) {
                this.tail.rotateAngleX -= 0.01F;
            }
        }
    }
}
