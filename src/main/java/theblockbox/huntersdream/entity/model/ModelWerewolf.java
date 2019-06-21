package theblockbox.huntersdream.entity.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import theblockbox.huntersdream.api.helpers.WerewolfHelper;
import theblockbox.huntersdream.util.handlers.ConfigHandler;

/**
 * ModelWerewolf - VampireRedEye
 * Created using Tabula 7.0.1
 */
public class ModelWerewolf extends ModelBiped {
    public ModelRenderer upperbody;
    public ModelRenderer bodyfurb2;
    public ModelRenderer tail;
    public ModelRenderer bodyfurb3;
    public ModelRenderer snout;
    public ModelRenderer facefurl;
    public ModelRenderer facefurr;
    public ModelRenderer earr;
    public ModelRenderer earl;
    public ModelRenderer headfur1;
    public ModelRenderer headfur2;
    public ModelRenderer headfur3;
    public ModelRenderer headfur4;
    public ModelRenderer jaw;
    public ModelRenderer jawfur;
    public ModelRenderer arml2;
    public ModelRenderer armr2;
    public ModelRenderer legr2;
    public ModelRenderer legr3;
    public ModelRenderer legl2;
    public ModelRenderer legl3;
    protected boolean isTailGoingUp = false;

    public ModelWerewolf() {
        this.textureWidth = 96;
        this.textureHeight = 96;
        this.headfur4 = new ModelRenderer(this, 28, 44);
        this.headfur4.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.headfur4.addBox(-3.51F, -2.1F, 1.2F, 7, 8, 3, 0.0F);
        ModelWerewolf.setRotateAngle(this.headfur4, 0.8651597102135892F, 0.0F, 0.0F);
        this.bipedRightArm = new ModelRenderer(this, 40, 16);
        this.bipedRightArm.setRotationPoint(-5.0F, 3.0F, 0.0F);
        this.bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 14, 4, 0.0F);
        ModelWerewolf.setRotateAngle(this.bipedRightArm, -0.17453292519943295F, 0.0F, 0.10000736613927509F);
        this.bipedRightLeg = new ModelRenderer(this, 0, 16);
        this.bipedRightLeg.setRotationPoint(-2.2F, 12.0F, 0.0F);
        this.bipedRightLeg.addBox(-2.04F, 0.0F, -2.0F, 4, 7, 4, 0.0F);
        ModelWerewolf.setRotateAngle(this.bipedRightLeg, -0.5235987755982988F, 0.0F, 0.0F);
        this.bodyfurb2 = new ModelRenderer(this, 0, 46);
        this.bodyfurb2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bodyfurb2.addBox(-5.5F, 0.5F, -0.1F, 11, 8, 3, 0.0F);
        ModelWerewolf.setRotateAngle(this.bodyfurb2, 0.17453292519943295F, 0.0F, 0.0F);
        this.upperbody = new ModelRenderer(this, 0, 32);
        this.upperbody.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.upperbody.addBox(-5.0F, 0.0F, -3.0F, 10, 6, 6, 0.0F);
        this.bodyfurb3 = new ModelRenderer(this, 28, 44);
        this.bodyfurb3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bodyfurb3.addBox(-3.5F, 5.0F, -1.0F, 7, 8, 3, 0.0F);
        ModelWerewolf.setRotateAngle(this.bodyfurb3, 0.17453292519943295F, 0.0F, 0.0F);
        this.armr2 = new ModelRenderer(this, 42, -6);
        this.armr2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.armr2.addBox(-1.0F, 1.0F, -0.5F, 0, 10, 6, 0.0F);
        this.snout = new ModelRenderer(this, 24, 0);
        this.snout.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.snout.addBox(-1.5F, -4.0F, -6.5F, 3, 3, 3, 0.0F);
        ModelWerewolf.setRotateAngle(this.snout, 0.06981317007977318F, 0.0F, 0.0F);
        this.facefurr = new ModelRenderer(this, 54, -2);
        this.facefurr.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.facefurr.addBox(-4.5F, -4.5F, -0.8F, 0, 6, 3, 0.0F);
        ModelWerewolf.setRotateAngle(this.facefurr, 0.0F, -0.8726646259971648F, 0.0F);
        this.facefurl = new ModelRenderer(this, 54, -2);
        this.facefurl.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.facefurl.addBox(4.5F, -4.5F, -0.8F, 0, 6, 3, 0.0F);
        ModelWerewolf.setRotateAngle(this.facefurl, 0.0F, 0.8726646259971648F, 0.0F);
        this.bipedLeftLeg = new ModelRenderer(this, 0, 16);
        this.bipedLeftLeg.setRotationPoint(2.2F, 12.0F, 0.0F);
        this.bipedLeftLeg.addBox(-1.94F, 0.0F, -2.0F, 4, 7, 4, 0.0F);
        ModelWerewolf.setRotateAngle(this.bipedLeftLeg, -0.5235987755982988F, 0.0F, 0.0F);
        this.legr2 = new ModelRenderer(this, 46, 52);
        this.legr2.setRotationPoint(0.0F, 5.2F, -1.0F);
        this.legr2.addBox(-2.02F, 0.0F, -2.0F, 4, 6, 4, 0.0F);
        ModelWerewolf.setRotateAngle(this.legr2, 1.0471975511965976F, 0.0F, 0.0F);
        this.headfur3 = new ModelRenderer(this, 28, 44);
        this.headfur3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.headfur3.addBox(-3.49F, -1.2F, 2.0F, 7, 6, 3, 0.0F);
        ModelWerewolf.setRotateAngle(this.headfur3, 1.2747884856566583F, 0.0F, 0.0F);
        this.bipedLeftArm = new ModelRenderer(this, 40, 16);
        this.bipedLeftArm.mirror = true;
        this.bipedLeftArm.setRotationPoint(5.0F, 3.0F, 0.0F);
        this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 14, 4, 0.0F);
        ModelWerewolf.setRotateAngle(this.bipedLeftArm, -0.17453292519943295F, 0.0F, -0.10000736613927509F);
        this.jaw = new ModelRenderer(this, 37, 10);
        this.jaw.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.jaw.addBox(-2.0F, -2.0F, -7.0F, 4, 2, 3, 0.0F);
        this.earr = new ModelRenderer(this, 36, 0);
        this.earr.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.earr.addBox(-3.5F, -7.0F, -2.5F, 1, 5, 2, 0.0F);
        ModelWerewolf.setRotateAngle(this.earr, -0.5235987755982988F, -0.3490658503988659F, 0.0F);
        this.earl = new ModelRenderer(this, 36, 0);
        this.earl.mirror = true;
        this.earl.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.earl.addBox(2.5F, -7.0F, -2.5F, 1, 5, 2, 0.0F);
        ModelWerewolf.setRotateAngle(this.earl, -0.5235987755982988F, 0.3490658503988659F, 0.0F);
        this.headfur2 = new ModelRenderer(this, 28, 44);
        this.headfur2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.headfur2.addBox(-3.52F, 0.7F, 2.5F, 7, 6, 3, 0.0F);
        ModelWerewolf.setRotateAngle(this.headfur2, 1.5025539530419183F, 0.0F, 0.0F);
        this.arml2 = new ModelRenderer(this, 42, -6);
        this.arml2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.arml2.addBox(1.0F, 1.0F, -0.5F, 0, 10, 6, 0.0F);
        this.tail = new ModelRenderer(this, 54, 10);
        this.tail.setRotationPoint(0.0F, 11.0F, 1.0F);
        this.tail.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
        ModelWerewolf.setRotateAngle(this.tail, 0.5235987755982988F, 0.0F, 0.0F);
        this.bipedHead = new ModelRenderer(this, 0, 0);
        this.bipedHead.setRotationPoint(0.0F, 0.0F, -1.0F);
        this.bipedHead.addBox(-3.0F, -6.0F, -4.0F, 6, 6, 6, 0.0F);
        ModelWerewolf.setRotateAngle(this.bipedHead, -0.17453292519943295F, 0.0F, 0.0F);
        this.headfur1 = new ModelRenderer(this, 28, 44);
        this.headfur1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.headfur1.addBox(-3.5F, 2.0F, 2.5F, 7, 6, 3, 0.0F);
        ModelWerewolf.setRotateAngle(this.headfur1, 1.9123572614101867F, 0.0F, 0.0F);
        this.bipedBody = new ModelRenderer(this, 16, 16);
        this.bipedBody.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.bipedBody.addBox(-4.0F, 2.0F, -2.0F, 8, 10, 4, 0.0F);
        ModelWerewolf.setRotateAngle(this.bipedBody, 0.17453292519943295F, 0.0F, 0.0F);
        this.legr3 = new ModelRenderer(this, 46, 34);
        this.legr3.setRotationPoint(0.0F, 4.4F, 0.6F);
        this.legr3.addBox(-2.0F, 0.0F, -2.0F, 4, 7, 4, 0.0F);
        ModelWerewolf.setRotateAngle(this.legr3, -0.8726646259971648F, 0.0F, 0.0F);
        this.legl3 = new ModelRenderer(this, 46, 34);
        this.legl3.setRotationPoint(0.0F, 4.4F, 0.6F);
        this.legl3.addBox(-1.98F, 0.0F, -2.0F, 4, 7, 4, 0.0F);
        ModelWerewolf.setRotateAngle(this.legl3, -0.8726646259971648F, 0.0F, 0.0F);
        this.jawfur = new ModelRenderer(this, 24, 6);
        this.jawfur.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.jawfur.addBox(-1.5F, -0.5F, -6.99F, 3, 2, 3, 0.0F);
        this.legl2 = new ModelRenderer(this, 46, 52);
        this.legl2.setRotationPoint(0.0F, 5.2F, -1.0F);
        this.legl2.addBox(-1.96F, 0.0F, -2.0F, 4, 6, 4, 0.0F);
        ModelWerewolf.setRotateAngle(this.legl2, 1.0471975511965976F, 0.0F, 0.0F);
        this.bipedHead.addChild(this.headfur4);
        this.bipedBody.addChild(this.bipedRightArm);
        this.bipedBody.addChild(this.bipedRightLeg);
        this.bipedBody.addChild(this.bodyfurb2);
        this.bipedBody.addChild(this.upperbody);
        this.bipedBody.addChild(this.bodyfurb3);
        this.bipedRightArm.addChild(this.armr2);
        this.bipedHead.addChild(this.snout);
        this.bipedHead.addChild(this.facefurr);
        this.bipedHead.addChild(this.facefurl);
        this.bipedBody.addChild(this.bipedLeftLeg);
        this.bipedRightLeg.addChild(this.legr2);
        this.bipedHead.addChild(this.headfur3);
        this.bipedBody.addChild(this.bipedLeftArm);
        this.bipedHead.addChild(this.jaw);
        this.bipedHead.addChild(this.earr);
        this.bipedHead.addChild(this.earl);
        this.bipedHead.addChild(this.headfur2);
        this.bipedLeftArm.addChild(this.arml2);
        this.bipedBody.addChild(this.tail);
        this.bipedBody.addChild(this.bipedHead);
        this.bipedHead.addChild(this.headfur1);
        this.legr2.addChild(this.legr3);
        this.legl2.addChild(this.legl3);
        this.bipedHead.addChild(this.jawfur);
        this.bipedLeftLeg.addChild(this.legl2);
    }

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
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
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
