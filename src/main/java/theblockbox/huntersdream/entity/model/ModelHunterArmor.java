package theblockbox.huntersdream.entity.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.items.ItemHunterArmor;

/**
 * ModelHunterArmor - VampireRedEye
 * Created using Tabula 7.0.1
 */
// TODO: Redo hat and jacket
public class ModelHunterArmor extends ModelBiped {
    public ModelRenderer jacket;
    public ModelRenderer bootr;
    public ModelRenderer bootl;
    public ModelRenderer hat;

    public ModelHunterArmor() {
        this.textureWidth = 96;
        this.textureHeight = 64;
        this.jacket = new ModelRenderer(this, 0, 32);
        this.jacket.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.jacket.addBox(-4.0F, 0.0F, -2.0F, 8, 18, 4, 0.5F);
        this.bipedLeftLeg = new ModelRenderer(this, 0, 16);
        this.bipedLeftLeg.mirror = true;
        this.bipedLeftLeg.setRotationPoint(2.0F, 12.0F, 0.0F);
        this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.1F);
        this.bipedBody = new ModelRenderer(this, 16, 16);
        this.bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.1F);
        this.hat = new ModelRenderer(this, 40, 0);
        this.hat.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.hat.addBox(-8.0F, -5.0F, -8.0F, 16, 0, 16, 0.0F);
        this.bipedRightLeg = new ModelRenderer(this, 0, 16);
        this.bipedRightLeg.setRotationPoint(-2.0F, 12.0F, 0.0F);
        this.bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.1F);
        this.bootr = new ModelRenderer(this, 0, 16);
        this.bootr.setRotationPoint(-2.0F, 12.0F, 0.0F);
        this.bootr.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.2F);
        this.bootl = new ModelRenderer(this, 0, 16);
        this.bootl.mirror = true;
        this.bootl.setRotationPoint(2.0F, 12.0F, 0.0F);
        this.bootl.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.2F);
        this.bipedRightArm = new ModelRenderer(this, 40, 16);
        this.bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
        this.bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, 0.2F);
        this.bipedHead = new ModelRenderer(this, 0, 0);
        this.bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bipedHead.addBox(-4.0F, -9.0F, -4.0F, 8, 4, 8, 0.2F);
        this.bipedLeftArm = new ModelRenderer(this, 40, 16);
        this.bipedLeftArm.mirror = true;
        this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
        this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, 0.2F);
        this.bipedHead.addChild(this.hat);
        this.bipedBody.addChild(this.jacket);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        GlStateManager.pushMatrix();
        if (this.isSneak) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
        }
        for (ItemStack armor : entity.getArmorInventoryList()) {
            if (armor.getItem() instanceof ItemHunterArmor) {
                switch (((ItemHunterArmor) armor.getItem()).armorType) {
                    case HEAD:
                        this.bipedHead.render(scale);
                        break;
                    case CHEST:
                        this.bipedBody.render(scale);
                        this.bipedLeftArm.render(scale);
                        this.bipedRightArm.render(scale);
                        break;
                    case LEGS:
                        this.bipedLeftLeg.render(scale);
                        this.bipedRightLeg.render(scale);
                        break;
                    case FEET:
                        this.bootl.render(scale);
                        this.bootr.render(scale);
                        break;
                }
            }
        }
        GlStateManager.popMatrix();
    }

    @Override
    public void setModelAttributes(ModelBase model) {
        super.setModelAttributes(model);
        if (model instanceof ModelBiped) {
            ModelBiped modelBiped = (ModelBiped) model;
            ModelBase.copyModelAngles(modelBiped.bipedHead, this.bipedHead);
            ModelBase.copyModelAngles(modelBiped.bipedBody, this.bipedBody);
            ModelBase.copyModelAngles(modelBiped.bipedLeftArm, this.bipedLeftArm);
            ModelBase.copyModelAngles(modelBiped.bipedRightArm, this.bipedRightArm);
            ModelBase.copyModelAngles(modelBiped.bipedLeftLeg, this.bipedLeftLeg);
            ModelBase.copyModelAngles(modelBiped.bipedRightLeg, this.bipedRightLeg);
            ModelBase.copyModelAngles(modelBiped.bipedLeftLeg, this.bootl);
            ModelBase.copyModelAngles(modelBiped.bipedRightLeg, this.bootr);
        } else {
            Main.getLogger().error("Tried to copy model attributes to ModelHunterArmor from a model that isn't an " +
                    "instance of ModelBiped. ({} of type {}.)", model, model.getClass().getName());
        }
    }
}
