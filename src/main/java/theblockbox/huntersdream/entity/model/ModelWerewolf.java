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
 * Created using Tabula 7.0.0
 */
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

    public ModelWerewolf() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.tail = new ModelRenderer(this, 24, 40);
        this.tail.setRotationPoint(0.0F, 10.0F, 1.0F);
        this.tail.addBox(-1.5F, 0.0F, -1.5F, 3, 10, 3, 0.0F);
        ModelWerewolf.setRotateAngle(this.tail, 0.7853981633974483F, 0.0F, 0.0F);
        this.facefurl = new ModelRenderer(this, 52, 30);
        this.facefurl.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.facefurl.addBox(4.5F, -4.5F, -0.8F, 0, 6, 3, 0.0F);
        ModelWerewolf.setRotateAngle(this.facefurl, 0.0F, 0.8726646259971648F, 0.0F);
        this.bipedBody = new ModelRenderer(this, 16, 16);
        this.bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F);
        ModelWerewolf.setRotateAngle(this.bipedBody, 0.17453292519943295F, 0.0F, 0.0F);
        this.facefurr = new ModelRenderer(this, 32, 30);
        this.facefurr.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.facefurr.addBox(-4.5F, -4.5F, -0.8F, 0, 6, 3, 0.0F);
        ModelWerewolf.setRotateAngle(this.facefurr, 0.0F, -0.8726646259971648F, 0.0F);
        this.bipedLeftLeg = new ModelRenderer(this, 0, 16);
        this.bipedLeftLeg.mirror = true;
        this.bipedLeftLeg.setRotationPoint(2.5F, 12.0F, 0.1F);
        this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 7, 4, 0.0F);
        ModelWerewolf.setRotateAngle(this.bipedLeftLeg, -0.5235987755982988F, 0.0F, 0.0F);
        this.bodyfurb = new ModelRenderer(this, 0, 32);
        this.bodyfurb.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bodyfurb.addBox(-4.5F, 0.3F, -1.0F, 9, 10, 3, 0.0F);
        ModelWerewolf.setRotateAngle(this.bodyfurb, 0.17453292519943295F, 0.0F, 0.0F);
        this.clawlegr3 = new ModelRenderer(this, 0, 58);
        this.clawlegr3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.clawlegr3.addBox(0.8F, 11.0F, -3.0F, 1, 1, 2, 0.0F);
        ModelWerewolf.setRotateAngle(this.clawlegr3, 0.3490658503988659F, 0.0F, 0.0F);
        this.clawlegl = new ModelRenderer(this, 0, 58);
        this.clawlegl.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.clawlegl.addBox(-1.8F, 11.0F, -3.0F, 1, 1, 2, 0.0F);
        ModelWerewolf.setRotateAngle(this.clawlegl, 0.3490658503988659F, 0.0F, 0.0F);
        this.earr = new ModelRenderer(this, 38, 0);
        this.earr.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.earr.addBox(-3.0F, -8.0F, -1.0F, 2, 2, 1, 0.0F);
        this.bipedRightArm = new ModelRenderer(this, 40, 16);
        this.bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
        this.bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
        ModelWerewolf.setRotateAngle(this.bipedRightArm, -0.17453292519943295F, 0.0F, 0.10000736613927509F);
        this.bipedLeftArm = new ModelRenderer(this, 40, 16);
        this.bipedLeftArm.mirror = true;
        this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
        this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
        ModelWerewolf.setRotateAngle(this.bipedLeftArm, -0.17453292519943295F, 0.0F, -0.10000736613927509F);
        this.bipedRightLeg = new ModelRenderer(this, 0, 16);
        this.bipedRightLeg.setRotationPoint(-2.5F, 12.0F, 0.1F);
        this.bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 7, 4, 0.0F);
        ModelWerewolf.setRotateAngle(this.bipedRightLeg, -0.5235987755982988F, 0.0F, 0.0F);
        this.clawlegl2 = new ModelRenderer(this, 0, 58);
        this.clawlegl2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.clawlegl2.addBox(-0.5F, 11.0F, -3.0F, 1, 1, 2, 0.0F);
        ModelWerewolf.setRotateAngle(this.clawlegl2, 0.3490658503988659F, 0.0F, 0.0F);
        this.clawarmr = new ModelRenderer(this, 8, 54);
        this.clawarmr.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.clawarmr.addBox(-3.0F, 10.0F, -2.0F, 2, 2, 1, 0.0F);
        this.bipedHead = new ModelRenderer(this, 0, 0);
        this.bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bipedHead.addBox(-3.0F, -6.0F, -4.0F, 6, 6, 6, 0.0F);
        ModelWerewolf.setRotateAngle(this.bipedHead, -0.17453292519943295F, 0.0F, 0.0F);
        this.clawlegr = new ModelRenderer(this, 0, 58);
        this.clawlegr.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.clawlegr.addBox(-1.8F, 11.0F, -3.0F, 1, 1, 2, 0.0F);
        ModelWerewolf.setRotateAngle(this.clawlegr, 0.3490658503988659F, 0.0F, 0.0F);
        this.legl2 = new ModelRenderer(this, 32, 50);
        this.legl2.mirror = true;
        this.legl2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.legl2.addBox(-2.0F, 4.0F, 2.0F, 4, 8, 4, 0.0F);
        this.bodyfurf = new ModelRenderer(this, 0, 45);
        this.bodyfurf.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bodyfurf.addBox(-4.5F, 0.5F, -2.0F, 9, 6, 3, 0.0F);
        ModelWerewolf.setRotateAngle(this.bodyfurf, -0.17453292519943295F, 0.0F, 0.0F);
        this.armr2 = new ModelRenderer(this, 40, 26);
        this.armr2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.armr2.addBox(-1.0F, 1.0F, -0.5F, 0, 10, 6, 0.0F);
        this.clawarml2 = new ModelRenderer(this, 0, 54);
        this.clawarml2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.clawarml2.addBox(1.0F, 10.0F, 1.0F, 2, 2, 1, 0.0F);
        this.snout = new ModelRenderer(this, 24, 0);
        this.snout.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.snout.addBox(-1.5F, -3.2F, -7.0F, 3, 3, 3, 0.0F);
        this.clawlegr2 = new ModelRenderer(this, 0, 58);
        this.clawlegr2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.clawlegr2.addBox(-0.5F, 11.0F, -3.0F, 1, 1, 2, 0.0F);
        ModelWerewolf.setRotateAngle(this.clawlegr2, 0.3490658503988659F, 0.0F, 0.0F);
        this.arml2 = new ModelRenderer(this, 40, 26);
        this.arml2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.arml2.addBox(1.0F, 1.0F, -0.5F, 0, 10, 6, 0.0F);
        this.clawarmr3 = new ModelRenderer(this, 8, 54);
        this.clawarmr3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.clawarmr3.addBox(-3.0F, 10.0F, -0.5F, 2, 2, 1, 0.0F);
        this.clawarml3 = new ModelRenderer(this, 0, 54);
        this.clawarml3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.clawarml3.addBox(1.0F, 10.0F, -0.5F, 2, 2, 1, 0.0F);
        this.clawarmr2 = new ModelRenderer(this, 8, 54);
        this.clawarmr2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.clawarmr2.addBox(-3.0F, 10.0F, 1.0F, 2, 2, 1, 0.0F);
        this.legr2 = new ModelRenderer(this, 32, 50);
        this.legr2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.legr2.addBox(-2.0F, 4.0F, 2.0F, 4, 8, 4, 0.0F);
        this.clawlegl3 = new ModelRenderer(this, 0, 58);
        this.clawlegl3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.clawlegl3.addBox(0.8F, 11.0F, -3.0F, 1, 1, 2, 0.0F);
        ModelWerewolf.setRotateAngle(this.clawlegl3, 0.3490658503988659F, 0.0F, 0.0F);
        this.earl = new ModelRenderer(this, 38, 0);
        this.earl.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.earl.addBox(1.0F, -8.0F, -1.0F, 2, 2, 1, 0.0F);
        this.clawarml = new ModelRenderer(this, 0, 54);
        this.clawarml.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.clawarml.addBox(1.0F, 10.0F, -2.0F, 2, 2, 1, 0.0F);
        this.bipedBody.addChild(this.tail);
        this.bipedHead.addChild(this.facefurl);
        this.bipedHead.addChild(this.facefurr);
        this.bipedBody.addChild(this.bipedLeftLeg);
        this.bipedBody.addChild(this.bodyfurb);
        this.legr2.addChild(this.clawlegr3);
        this.legl2.addChild(this.clawlegl);
        this.bipedHead.addChild(this.earr);
        this.bipedBody.addChild(this.bipedRightArm);
        this.bipedBody.addChild(this.bipedLeftArm);
        this.bipedBody.addChild(this.bipedRightLeg);
        this.legl2.addChild(this.clawlegl2);
        this.bipedRightArm.addChild(this.clawarmr);
        this.bipedBody.addChild(this.bipedHead);
        this.legr2.addChild(this.clawlegr);
        this.bipedLeftLeg.addChild(this.legl2);
        this.bipedBody.addChild(this.bodyfurf);
        this.bipedRightArm.addChild(this.armr2);
        this.bipedLeftArm.addChild(this.clawarml2);
        this.bipedHead.addChild(this.snout);
        this.legr2.addChild(this.clawlegr2);
        this.bipedLeftArm.addChild(this.arml2);
        this.bipedRightArm.addChild(this.clawarmr3);
        this.bipedLeftArm.addChild(this.clawarml3);
        this.bipedRightArm.addChild(this.clawarmr2);
        this.bipedRightLeg.addChild(this.legr2);
        this.legl2.addChild(this.clawlegl3);
        this.bipedHead.addChild(this.earl);
        this.bipedLeftArm.addChild(this.clawarml);
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
