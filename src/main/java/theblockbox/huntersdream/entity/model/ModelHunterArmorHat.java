package theblockbox.huntersdream.entity.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import theblockbox.huntersdream.Main;

/**
 * ModelHunterArmorHat - VampireRedEye
 * Created using Tabula 7.0.1
 */
public class ModelHunterArmorHat extends ModelBiped {
    public static final ModelHunterArmorHat INSTANCE = new ModelHunterArmorHat();
    public ModelRenderer hat;

    public ModelHunterArmorHat() {
        this.textureWidth = 89;
        this.textureHeight = 16;
        this.hat = new ModelRenderer(this, 40, 0);
        this.hat.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.hat.addBox(-8.0F, -5.0F, -8.0F, 16, 0, 16, 0.0F);
        this.bipedHead = new ModelRenderer(this, 0, 0);
        this.bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bipedHead.addBox(-4.0F, -9.0F, -4.0F, 8, 4, 8, 0.2F);
        this.bipedHead.addChild(this.hat);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        GlStateManager.pushMatrix();
        if (this.isSneak) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
        }
        GlStateManager.scale(1.1F, 1.1F, 1.1F);
        this.bipedHead.render(scale);
        GlStateManager.popMatrix();
    }

    @Override
    public void setModelAttributes(ModelBase model) {
        super.setModelAttributes(model);
        if (model instanceof ModelBiped) {
            ModelBase.copyModelAngles(((ModelBiped) model).bipedHead, this.bipedHead);
        } else {
            Main.getLogger().error("Tried to copy model attributes to ModelHunterArmor from a model that isn't an " +
                    "instance of ModelBiped. ({} of type {}.)", model, model.getClass().getName());
        }
    }
}
