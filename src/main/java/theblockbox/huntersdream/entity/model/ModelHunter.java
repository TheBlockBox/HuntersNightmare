package theblockbox.huntersdream.entity.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelHunter - VampireRedEye
 * Created using Tabula 7.0.1
 */
public class ModelHunter extends ModelBiped {
    public ModelRenderer jacket;
    public ModelRenderer hat;
    public ModelRenderer nose;
    public ModelRenderer hat2;

    public ModelHunter() {
        this.textureWidth = 96;
        this.textureHeight = 64;
        this.bipedLeftLeg = new ModelRenderer(this, 0, 22);
        this.bipedLeftLeg.mirror = true;
        this.bipedLeftLeg.setRotationPoint(2.0F, 12.0F, 0.0F);
        this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
        this.nose = new ModelRenderer(this, 24, 0);
        this.nose.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.nose.addBox(-1.0F, -1.0F, -6.0F, 2, 4, 2, 0.0F);
        this.jacket = new ModelRenderer(this, 0, 38);
        this.jacket.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.jacket.addBox(-4.0F, 0.0F, -3.0F, 8, 18, 6, 0.5F);
        this.bipedHead = new ModelRenderer(this, 0, 0);
        this.bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bipedHead.addBox(-4.0F, -10.0F, -4.0F, 8, 10, 8, 0.0F);
        this.bipedRightArm = new ModelRenderer(this, 44, 22);
        this.bipedRightArm.setRotationPoint(0.0F, 3.0F, -1.0F);
        this.bipedRightArm.addBox(-8.0F, -2.0F, -2.0F, 4, 8, 4, 0.0F);
        ModelHunter.setRotateAngle(this.bipedRightArm, -0.7499679795819634F, 0.0F, 0.0F);
        this.hat2 = new ModelRenderer(this, 48, 0);
        this.hat2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.hat2.addBox(-8.0F, -6.1F, -8.0F, 16, 0, 16, 0.0F);
        this.bipedBody = new ModelRenderer(this, 16, 20);
        this.bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bipedBody.addBox(-4.0F, 0.0F, -3.0F, 8, 12, 6, 0.0F);
        this.hat = new ModelRenderer(this, 32, 0);
        this.hat.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.hat.addBox(-4.0F, -10.0F, -4.0F, 8, 12, 8, 0.2F);
        this.bipedRightLeg = new ModelRenderer(this, 0, 22);
        this.bipedRightLeg.setRotationPoint(-2.0F, 12.0F, 0.0F);
        this.bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
        this.bipedLeftArm = new ModelRenderer(this, 44, 22);
        this.bipedLeftArm.mirror = true;
        this.bipedLeftArm.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bipedLeftArm.addBox(4.0F, -2.0F, -2.0F, 4, 8, 4, 0.0F);
        this.bipedBody.addChild(this.bipedLeftLeg);
        this.bipedHead.addChild(this.nose);
        this.bipedHead.addChild(this.hat);
        this.bipedBody.addChild(this.bipedHead);
        this.bipedBody.addChild(this.bipedLeftArm);
        this.hat.addChild(this.hat2);
        this.bipedBody.addChild(this.bipedRightLeg);
        this.bipedBody.addChild(this.bipedRightArm);
        this.bipedBody.addChild(this.jacket);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.bipedBody.render(f5);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public static void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
