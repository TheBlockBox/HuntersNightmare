package theblockbox.huntersdream.entity.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

/**
 * ModelGoblinTD - VampireRedEye Created using Tabula 7.0.0
 */
public class ModelGoblinTD extends ModelBase {
	public ModelRenderer head;
	public ModelRenderer chest;
	public ModelRenderer legl;
	public ModelRenderer legr;
	public ModelRenderer arml;
	public ModelRenderer armr;
	public ModelRenderer nose;
	public ModelRenderer nose2;
	public ModelRenderer earl;
	public ModelRenderer earr;
	public ModelRenderer earl2;
	public ModelRenderer earr2;
	public ModelRenderer tummy;

	public ModelGoblinTD() {
		this.textureWidth = 64;
		this.textureHeight = 32;
		this.armr = new ModelRenderer(this, 24, 4);
		this.armr.setRotationPoint(-3.0F, 10.0F, 0.0F);
		this.armr.addBox(-2.0F, -1.0F, -1.0F, 2, 8, 2, 0.0F);
		this.setRotateAngle(armr, 0.0F, 0.0F, 0.5235987755982988F);
		this.tummy = new ModelRenderer(this, 18, 16);
		this.tummy.setRotationPoint(0.0F, 3.0F, 0.0F);
		this.tummy.addBox(-4.5F, 0.0F, -3.0F, 9, 8, 6, 0.0F);
		this.arml = new ModelRenderer(this, 24, 4);
		this.arml.setRotationPoint(3.0F, 10.0F, 0.0F);
		this.arml.addBox(0.0F, -1.0F, -1.0F, 2, 8, 2, 0.0F);
		this.setRotateAngle(arml, 0.0F, 0.0F, -0.5235987755982988F);
		this.earr = new ModelRenderer(this, 18, 0);
		this.earr.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.earr.addBox(-6.0F, -4.0F, 0.0F, 3, 1, 1, 0.0F);
		this.nose2 = new ModelRenderer(this, 0, 0);
		this.nose2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.nose2.addBox(-0.5F, -4.0F, -4.0F, 1, 1, 1, 0.0F);
		this.chest = new ModelRenderer(this, 0, 12);
		this.chest.setRotationPoint(0.0F, 8.0F, 0.0F);
		this.chest.addBox(-3.5F, 0.0F, -2.5F, 7, 3, 5, 0.0F);
		this.earl = new ModelRenderer(this, 18, 0);
		this.earl.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.earl.addBox(3.0F, -4.0F, 0.0F, 3, 1, 1, 0.0F);
		this.earl2 = new ModelRenderer(this, 26, 0);
		this.earl2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.earl2.addBox(3.0F, -3.0F, 0.0F, 2, 1, 1, 0.0F);
		this.head = new ModelRenderer(this, 0, 0);
		this.head.setRotationPoint(0.0F, 8.0F, 0.0F);
		this.head.addBox(-3.0F, -6.0F, -3.0F, 6, 6, 6, 0.0F);
		this.legl = new ModelRenderer(this, 0, 20);
		this.legl.setRotationPoint(2.0F, 19.0F, 0.0F);
		this.legl.addBox(-1.5F, 0.0F, -1.5F, 3, 5, 3, 0.0F);
		this.earr2 = new ModelRenderer(this, 26, 0);
		this.earr2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.earr2.addBox(-5.0F, -3.0F, 0.0F, 2, 1, 1, 0.0F);
		this.legr = new ModelRenderer(this, 0, 20);
		this.legr.setRotationPoint(-2.0F, 19.0F, 0.0F);
		this.legr.addBox(-1.5F, 0.0F, -1.5F, 3, 5, 3, 0.0F);
		this.nose = new ModelRenderer(this, 0, 2);
		this.nose.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.nose.addBox(-0.5F, -3.0F, -5.0F, 1, 2, 2, 0.0F);
		this.chest.addChild(this.tummy);
		this.head.addChild(this.earr);
		this.head.addChild(this.nose2);
		this.head.addChild(this.earl);
		this.head.addChild(this.earl2);
		this.head.addChild(this.earr2);
		this.head.addChild(this.nose);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		this.armr.render(f5);
		this.arml.render(f5);
		this.chest.render(f5);
		this.head.render(f5);
		this.legl.render(f5);
		this.legr.render(f5);
	}

	/**
	 * This is a helper function from Tabula to set the rotation of model parts
	 */
	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scaleFactor, Entity entityIn) {
		this.head.rotateAngleY = netHeadYaw * 0.017453292F;
		this.head.rotateAngleX = headPitch * 0.017453292F;
		this.legr.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
		this.legl.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount * 0.5F;
		this.legr.rotateAngleY = 0.0F;
		this.legl.rotateAngleY = 0.0F;
		this.armr.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
		this.arml.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount * 0.5F;
		this.armr.rotateAngleY = 0.0F;
		this.arml.rotateAngleY = 0.0F;
	}
}
