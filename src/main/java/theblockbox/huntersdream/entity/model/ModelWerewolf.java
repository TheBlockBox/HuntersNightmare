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
    public ModelRenderer hip;
    public ModelRenderer joint;
    public ModelRenderer bodyfur1;
    public ModelRenderer bodyfur2;
    public ModelRenderer joint_1;
    public ModelRenderer tail;
    public ModelRenderer lleg2;
    public ModelRenderer lfoot;
    public ModelRenderer lmtoe;
    public ModelRenderer lltoe;
    public ModelRenderer lrtoe;
    public ModelRenderer rleg2;
    public ModelRenderer rfoot;
    public ModelRenderer rltoe;
    public ModelRenderer rmtoe;
    public ModelRenderer rrtoe;
    public ModelRenderer tail_1;
    public ModelRenderer tail_2;
    public ModelRenderer snout;
    public ModelRenderer lear1;
    public ModelRenderer lear2;
    public ModelRenderer jaw;
    public ModelRenderer rear1;
    public ModelRenderer rear1_1;
    public ModelRenderer headfur1;
    public ModelRenderer headfur2;
    public ModelRenderer lfacefur;
    public ModelRenderer rfacefur;

    public ModelWerewolf() {
        this.textureWidth = 96;
        this.textureHeight = 96;
        this.bipedHead = new ModelRenderer(this, 0, 0);
        this.bipedHead.setRotationPoint(0.0F, -4.0F, -3.6F);
        this.bipedHead.addBox(-3.5F, -7.0F, -3.5F, 7, 7, 7, 0.0F);
        this.bipedHead.offsetY = -0.2F;
        this.rltoe = new ModelRenderer(this, 36, 72);
        this.rltoe.setRotationPoint(-1.0F, 0.0F, -1.6F);
        this.rltoe.addBox(-0.5F, 0.0F, -1.5F, 1, 3, 1, 0.0F);
        ModelWerewolf.setRotateAngle(this.rltoe, -0.08726646259971647F, 0.3490658503988659F, 0.0F);
        this.jaw = new ModelRenderer(this, 48, 0);
        this.jaw.setRotationPoint(0.0F, -1.0F, -2.5F);
        this.jaw.addBox(-1.5F, -0.5F, -5.0F, 3, 1, 5, 0.0F);
        this.lear1 = new ModelRenderer(this, 34, 12);
        this.lear1.mirror = true;
        this.lear1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.lear1.addBox(2.5F, -3.5F, 2.0F, 1, 3, 5, 0.0F);
        ModelWerewolf.setRotateAngle(this.lear1, 0.7853981633974483F, 0.2617993877991494F, 0.0F);
        this.bipedRightArm = new ModelRenderer(this, 48, 10);
        this.bipedRightArm.mirror = true;
        this.bipedRightArm.setRotationPoint(-6.0F, 0.0F, 0.0F);
        this.bipedRightArm.addBox(-3.5F, -2.0F, -2.5F, 5, 16, 5, 0.0F);
        this.bipedRightArm.offsetX = -0.07F;
        this.bipedBody = new ModelRenderer(this, 0, 16);
        this.bipedBody.setRotationPoint(0.0F, -5.0F, 0.0F);
        this.bipedBody.addBox(-6.0F, 0.0F, -4.0F, 12, 8, 8, 0.0F);
        ModelWerewolf.setRotateAngle(this.bipedBody, 0.3490658503988659F, 0.0F, 0.0F);
        this.rear1 = new ModelRenderer(this, 34, 12);
        this.rear1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rear1.addBox(-3.5F, -3.5F, 2.0F, 1, 3, 5, 0.0F);
        ModelWerewolf.setRotateAngle(this.rear1, 0.7853981633974483F, -0.2617993877991494F, 0.0F);
        this.lleg2 = new ModelRenderer(this, 52, 48);
        this.lleg2.setRotationPoint(0.5F, 2.5F, 1.2F);
        this.lleg2.addBox(-1.5F, 0.0F, -1.5F, 3, 10, 3, 0.0F);
        ModelWerewolf.setRotateAngle(this.lleg2, 0.2617993877991494F, 0.0F, 0.0F);
        this.joint_1 = new ModelRenderer(this, 0, 0);
        this.joint_1.setRotationPoint(0.0F, 3.0F, 0.0F);
        this.joint_1.addBox(-3.0F, -4.0F, -3.0F, 1, 1, 1, 0.0F);
        ModelWerewolf.setRotateAngle(this.joint_1, -0.3490658503988659F, 0.0F, 0.0F);
        this.lrtoe = new ModelRenderer(this, 56, 72);
        this.lrtoe.setRotationPoint(1.0F, 0.0F, -1.6F);
        this.lrtoe.addBox(-0.5F, 0.0F, -1.5F, 1, 3, 1, 0.0F);
        ModelWerewolf.setRotateAngle(this.lrtoe, -0.08726646259971647F, -0.3490658503988659F, 0.0F);
        this.tail_2 = new ModelRenderer(this, 70, 52);
        this.tail_2.setRotationPoint(0.0F, 6.0F, 0.0F);
        this.tail_2.addBox(-1.5F, 0.0F, -1.5F, 3, 4, 3, 0.0F);
        ModelWerewolf.setRotateAngle(this.tail_2, -0.17453292519943295F, 0.0F, 0.0F);
        this.rear1_1 = new ModelRenderer(this, 34, 8);
        this.rear1_1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rear1_1.addBox(-3.5F, -2.6F, 7.0F, 1, 2, 2, 0.0F);
        ModelWerewolf.setRotateAngle(this.rear1_1, 0.8651597102135892F, -0.2617993877991494F, 0.0F);
        this.bipedLeftArm = new ModelRenderer(this, 48, 10);
        this.bipedLeftArm.setRotationPoint(6.0F, 0.0F, 0.0F);
        this.bipedLeftArm.addBox(-1.5F, -2.0F, -2.5F, 5, 16, 5, 0.0F);
        this.bipedLeftArm.offsetX = 0.07F;
        this.rleg2 = new ModelRenderer(this, 32, 48);
        this.rleg2.setRotationPoint(-0.5F, 2.5F, 1.2F);
        this.rleg2.addBox(-1.5F, 0.0F, -1.5F, 3, 10, 3, 0.0F);
        ModelWerewolf.setRotateAngle(this.rleg2, 0.2617993877991494F, 0.0F, 0.0F);
        this.lfoot = new ModelRenderer(this, 52, 64);
        this.lfoot.setRotationPoint(-0.02F, 8.0F, -0.8F);
        this.lfoot.addBox(-1.5F, 0.0F, -2.5F, 3, 3, 4, 0.0F);
        this.lear2 = new ModelRenderer(this, 34, 8);
        this.lear2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.lear2.addBox(2.5F, -2.6F, 7.0F, 1, 2, 2, 0.0F);
        ModelWerewolf.setRotateAngle(this.lear2, 0.8651597102135892F, 0.2617993877991494F, 0.0F);
        this.bipedLeftLeg = new ModelRenderer(this, 50, 34);
        this.bipedLeftLeg.setRotationPoint(3.0F, 9.5F, 0.0F);
        this.bipedLeftLeg.addBox(-1.5F, -3.0F, -3.0F, 4, 8, 5, 0.0F);
        ModelWerewolf.setRotateAngle(this.bipedLeftLeg, -0.3490658503988659F, 0.0F, 0.0F);
        this.bodyfur2 = new ModelRenderer(this, 64, 82);
        this.bodyfur2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bodyfur2.addBox(-5.0F, 6.2F, 0.3F, 10, 6, 4, 0.0F);
        ModelWerewolf.setRotateAngle(this.bodyfur2, 0.091106186954104F, 0.0F, 0.0F);
        this.lltoe = new ModelRenderer(this, 56, 72);
        this.lltoe.setRotationPoint(-1.0F, 0.0F, -1.6F);
        this.lltoe.addBox(-0.5F, 0.0F, -1.5F, 1, 3, 1, 0.0F);
        ModelWerewolf.setRotateAngle(this.lltoe, -0.08726646259971647F, 0.3490658503988659F, 0.0F);
        this.rmtoe = new ModelRenderer(this, 36, 72);
        this.rmtoe.setRotationPoint(-0.0F, 0.0F, -1.6F);
        this.rmtoe.addBox(-0.5F, 0.0F, -1.5F, 1, 3, 1, 0.0F);
        ModelWerewolf.setRotateAngle(this.rmtoe, -0.08726646259971647F, 0.0F, 0.0F);
        this.headfur2 = new ModelRenderer(this, 0, 62);
        this.headfur2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.headfur2.addBox(-2.5F, -2.5F, 2.0F, 5, 8, 4, 0.0F);
        ModelWerewolf.setRotateAngle(this.headfur2, 0.5009094953223726F, 0.0F, 0.0F);
        this.rrtoe = new ModelRenderer(this, 36, 72);
        this.rrtoe.setRotationPoint(1.0F, 0.0F, -1.6F);
        this.rrtoe.addBox(-0.5F, 0.0F, -1.5F, 1, 3, 1, 0.0F);
        ModelWerewolf.setRotateAngle(this.rrtoe, -0.08726646259971647F, -0.3490658503988659F, 0.0F);
        this.rfacefur = new ModelRenderer(this, 0, 74);
        this.rfacefur.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rfacefur.addBox(-3.0F, -5.5F, -4.0F, 3, 5, 0, 0.0F);
        ModelWerewolf.setRotateAngle(this.rfacefur, 0.0F, 1.0471975511965976F, 0.0F);
        this.lmtoe = new ModelRenderer(this, 56, 72);
        this.lmtoe.setRotationPoint(-0.0F, 0.0F, -1.6F);
        this.lmtoe.addBox(-0.5F, 0.0F, -1.5F, 1, 3, 1, 0.0F);
        ModelWerewolf.setRotateAngle(this.lmtoe, -0.08726646259971647F, 0.0F, 0.0F);
        this.joint = new ModelRenderer(this, 0, 0);
        this.joint.setRotationPoint(0.0F, 5.0F, 0.0F);
        this.joint.addBox(-3.0F, -4.0F, -3.0F, 1, 1, 1, 0.0F);
        ModelWerewolf.setRotateAngle(this.joint, -0.3490658503988659F, 0.0F, 0.0F);
        this.tail_1 = new ModelRenderer(this, 70, 40);
        this.tail_1.setRotationPoint(0.0F, 3.0F, 0.0F);
        this.tail_1.addBox(-2.0F, 0.0F, -2.0F, 4, 7, 4, 0.0F);
        ModelWerewolf.setRotateAngle(this.tail_1, -0.36425021489121656F, 0.0F, 0.0F);
        this.snout = new ModelRenderer(this, 30, 0);
        this.snout.setRotationPoint(0.0F, -3.5F, -2.5F);
        this.snout.addBox(-1.5F, -1.0F, -5.0F, 3, 3, 5, 0.0F);
        this.rfoot = new ModelRenderer(this, 31, 64);
        this.rfoot.setRotationPoint(0.02F, 8.0F, -0.8F);
        this.rfoot.addBox(-1.5F, 0.0F, -2.5F, 3, 3, 4, 0.0F);
        this.bipedRightLeg = new ModelRenderer(this, 30, 34);
        this.bipedRightLeg.setRotationPoint(-3.0F, 9.5F, 0.0F);
        this.bipedRightLeg.addBox(-2.5F, -3.0F, -3.0F, 4, 8, 5, 0.0F);
        ModelWerewolf.setRotateAngle(this.bipedRightLeg, -0.3490658503988659F, 0.0F, 0.0F);
        this.bodyfur1 = new ModelRenderer(this, 64, 72);
        this.bodyfur1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bodyfur1.addBox(-5.5F, 2.5F, 0.3F, 11, 6, 4, 0.0F);
        ModelWerewolf.setRotateAngle(this.bodyfur1, 0.45378560551852565F, 0.0F, 0.0F);
        this.hip = new ModelRenderer(this, 0, 32);
        this.hip.setRotationPoint(0.0F, 7.0F, 0.0F);
        this.hip.addBox(-4.0F, 0.0F, -3.0F, 8, 10, 6, 0.0F);
        ModelWerewolf.setRotateAngle(this.hip, -0.2617993877991494F, 0.0F, 0.0F);
        this.headfur1 = new ModelRenderer(this, 0, 50);
        this.headfur1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.headfur1.addBox(-3.0F, -1.5F, 2.8F, 6, 8, 4, 0.0F);
        ModelWerewolf.setRotateAngle(this.headfur1, 1.2217304763960306F, 0.0F, 0.0F);
        this.lfacefur = new ModelRenderer(this, 0, 74);
        this.lfacefur.mirror = true;
        this.lfacefur.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.lfacefur.addBox(0.0F, -5.5F, -4.0F, 3, 5, 0, 0.0F);
        ModelWerewolf.setRotateAngle(this.lfacefur, 0.0F, -1.0471975511965976F, 0.0F);
        this.tail = new ModelRenderer(this, 72, 32);
        this.tail.setRotationPoint(0.0F, 8.5F, 2.0F);
        this.tail.addBox(-1.5F, 0.0F, -1.5F, 3, 4, 3, 0.0F);
        ModelWerewolf.setRotateAngle(this.tail, 0.6283185307179586F, 0.0F, 0.0F);
        this.joint.addChild(this.bipedHead);
        this.rfoot.addChild(this.rltoe);
        this.bipedHead.addChild(this.jaw);
        this.bipedHead.addChild(this.lear1);
        this.joint_1.addChild(this.bipedRightArm);
        this.bipedHead.addChild(this.rear1);
        this.bipedLeftLeg.addChild(this.lleg2);
        this.bipedBody.addChild(this.joint_1);
        this.lfoot.addChild(this.lrtoe);
        this.tail_1.addChild(this.tail_2);
        this.bipedHead.addChild(this.rear1_1);
        this.joint_1.addChild(this.bipedLeftArm);
        this.bipedRightLeg.addChild(this.rleg2);
        this.lleg2.addChild(this.lfoot);
        this.bipedHead.addChild(this.lear2);
        this.hip.addChild(this.bipedLeftLeg);
        this.bipedBody.addChild(this.bodyfur2);
        this.lfoot.addChild(this.lltoe);
        this.rfoot.addChild(this.rmtoe);
        this.bipedHead.addChild(this.headfur2);
        this.rfoot.addChild(this.rrtoe);
        this.bipedHead.addChild(this.rfacefur);
        this.lfoot.addChild(this.lmtoe);
        this.bipedBody.addChild(this.joint);
        this.tail.addChild(this.tail_1);
        this.bipedHead.addChild(this.snout);
        this.rleg2.addChild(this.rfoot);
        this.hip.addChild(this.bipedRightLeg);
        this.bipedBody.addChild(this.bodyfur1);
        this.bipedBody.addChild(this.hip);
        this.bipedHead.addChild(this.headfur1);
        this.bipedHead.addChild(this.lfacefur);
        this.hip.addChild(this.tail);
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
        scale *= 0.87F;
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, this.isSneak ? 0.22F : 0.15F, 0.0F); //0.19?
        this.bipedBody.render(scale);
        GlStateManager.popMatrix();
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn, true);
    }

    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
                                  float headPitch, float scaleFactor, Entity entityIn, boolean moveTail) {
        boolean isBiting = ConfigHandler.client.biteAnimation && (entityIn instanceof EntityPlayer)
                && ((entityIn.world.getTotalWorldTime() - WerewolfHelper.getBiteTicks((EntityPlayer) entityIn)) < 4);
        this.isSneak = isBiting || entityIn.isSneaking();
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);

        if (isBiting) {
            this.bipedRightArm.rotateAngleX = -1.0F;
            this.bipedLeftArm.rotateAngleX = -1.0F;
        }

        // fix body and legs
        this.bipedBody.rotateAngleX += this.isSneak ? 0.15F : 0.3F;
        this.bipedRightLeg.rotateAngleX -= this.isSneak ? 0.7D : 0.3D;
        this.bipedLeftLeg.rotateAngleX -= this.isSneak ? 0.7D : 0.3D;
        this.bipedRightLeg.offsetY = this.isSneak ? 0.05F : -0.15F;
        this.bipedLeftLeg.offsetY = this.isSneak ? 0.05F : -0.15F;
        this.bipedRightLeg.offsetZ = this.isSneak ? -0.25F : 0.0F;
        this.bipedLeftLeg.offsetZ = this.isSneak ? -0.25F : 0.0F;

        // fix head and arms when sneaking
        if (this.isSneak) {
            this.bipedRightArm.rotateAngleX -= 0.65D;
            this.bipedLeftArm.rotateAngleX -= 0.65D;
        }

        if (moveTail) {
            if (limbSwingAmount >= 0.2F) {
                this.tail.rotateAngleX += (this.tail.rotateAngleX < 1.7F) ? 0.01F : 0.0F;
            } else if (this.tail.rotateAngleX > 0.5F) {
                this.tail.rotateAngleX -= 0.01F;
            }
        }
    }
}
