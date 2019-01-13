package theblockbox.huntersdream.entity.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

// TODO: Add sneaking
public class ModelWerewolf extends ModelBiped {
    public ModelRenderer tail;
    public ModelRenderer bodyfurb;
    public ModelRenderer bodyfurf;
    public ModelRenderer snout;
    public ModelRenderer earr;
    public ModelRenderer earl;
    public ModelRenderer facefurl;
    public ModelRenderer facefurr;
    public ModelRenderer snout2;
    public ModelRenderer earr2;
    public ModelRenderer earr3;
    public ModelRenderer earl2;
    public ModelRenderer earl3;
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
    public ModelRenderer tail2;
    public ModelRenderer tail3;
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
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
        GlStateManager.pushMatrix();
        if (entityIn.isSneaking()) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
        }

        this.bipedBody.render(scale);

        GlStateManager.popMatrix();
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
        // fix legs
        this.bipedRightLeg.rotateAngleX -= 0.3D;
        this.bipedLeftLeg.rotateAngleX -= 0.3D;
        if (limbSwingAmount >= 0.2F) {
            // animate
            // if it's going up and bigger than 0.5, go down
            // if it's going down and less than 0, go up
            this.isTailGoingUp = this.tail.rotateAngleX  < (this.isTailGoingUp ? 0.2F : -0.6F);
            this.tail.rotateAngleX += this.isTailGoingUp ? 0.01F : -0.01F;
        } else if (this.tail.rotateAngleX > -0.75F) {
            this.tail.rotateAngleX -= 0.01F;
        }
    }
}
