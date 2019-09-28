package theblockbox.huntersdream.entity.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelHunter - VampireRedEye
 * Created using Tabula 7.0.1
 */
// code isn't that great, but it's going to get changed in the next version anyways, so we'll just keep this here
public class ModelHunter extends ModelBiped {
    public ModelRenderer jacket;
    public ModelRenderer hat;
    public ModelRenderer armmr;
    public ModelRenderer nose;
    public ModelRenderer armml;
    public ModelRenderer armm;
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
        this.bipedLeftArm = new ModelRenderer(this, 48, 46);
        this.bipedLeftArm.mirror = true;
        this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
        this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
        this.armmr = new ModelRenderer(this, 44, 22);
        this.armmr.setRotationPoint(0.0F, 3.0F, -1.0F);
        this.armmr.addBox(-8.0F, -2.0F, -2.0F, 4, 8, 4, 0.0F);
        ModelHunter.setRotateAngle(this.armmr, -0.7499679795819634F, 0.0F, 0.0F);
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
        this.armm = new ModelRenderer(this, 40, 38);
        this.armm.setRotationPoint(0.0F, 3.0F, -1.0F);
        this.armm.addBox(-4.0F, -1.0F, -1.0F, 8, 4, 4, 0.0F);
        this.bipedRightArm = new ModelRenderer(this, 48, 46);
        this.bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
        this.bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
        this.armml = new ModelRenderer(this, 44, 22);
        this.armml.mirror = true;
        this.armml.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.armml.addBox(4.0F, -2.0F, -2.0F, 4, 8, 4, 0.0F);
        this.bipedBody.addChild(this.bipedLeftLeg);
        this.bipedHead.addChild(this.nose);
        this.bipedHead.addChild(this.hat);
        this.bipedBody.addChild(this.bipedHead);
        this.bipedBody.addChild(this.bipedLeftArm);
        this.bipedBody.addChild(this.armmr);
        this.hat.addChild(this.hat2);
        this.bipedBody.addChild(this.bipedRightLeg);
        this.armmr.addChild(this.armm);
        this.bipedBody.addChild(this.bipedRightArm);
        this.armmr.addChild(this.armml);
        this.bipedBody.addChild(this.jacket);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.armmr.isHidden = true;
        this.bipedLeftArm.isHidden = false;
        this.bipedRightArm.isHidden = false;
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