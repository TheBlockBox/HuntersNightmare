package theblockbox.huntersdream.entity.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

/**
 * ModelLycanthrope - VampireRedEye Created using Tabula 7.0.0
 */
public class ModelLycanthrope extends ModelBase {
	public ModelRenderer head;
	public ModelRenderer body;
	public ModelRenderer legl;
	public ModelRenderer legr;
	public ModelRenderer arml;
	public ModelRenderer tail;
	public ModelRenderer armr;
	public ModelRenderer snout;
	public ModelRenderer earl;
	public ModelRenderer earr;
	public ModelRenderer hip;
	public ModelRenderer legl2;
	public ModelRenderer legr2;
	public ModelRenderer arml2;
	public ModelRenderer armr2;

	public ModelLycanthrope() {
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.arml = new ModelRenderer(this, 46, 0);
		this.arml.setRotationPoint(5.0F, -5.0F, 1.0F);
		this.arml.addBox(0.0F, -2.0F, -2.0F, 4, 8, 4, 0.0F);
		this.snout = new ModelRenderer(this, 29, 0);
		this.snout.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.snout.addBox(-2.0F, -4.1F, -7.7F, 4, 4, 4, 0.0F);
		this.earl = new ModelRenderer(this, 9, 15);
		this.earl.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.earl.addBox(1.0F, -11.0F, -1.0F, 3, 3, 1, 0.0F);
		this.head = new ModelRenderer(this, 0, 0);
		this.head.setRotationPoint(0.0F, -6.0F, -1.0F);
		this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 6, 0.0F);
		this.hip = new ModelRenderer(this, 0, 40);
		this.hip.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.hip.addBox(-4.0F, 6.0F, -0.8F, 8, 10, 6, 0.0F);
		this.setRotateAngle(hip, -0.3490658503988659F, 0.0F, 0.0F);
		this.legr = new ModelRenderer(this, 30, 44);
		this.legr.setRotationPoint(-2.5F, 8.0F, 3.0F);
		this.legr.addBox(-2.0F, 0.0F, -2.0F, 4, 8, 4, 0.0F);
		this.setRotateAngle(legr, -0.4363323129985824F, 0.0F, 0.0F);
		this.tail = new ModelRenderer(this, 46, 22);
		this.tail.setRotationPoint(0.0F, 6.0F, 3.0F);
		this.tail.addBox(-1.5F, 0.0F, -1.5F, 3, 12, 3, 0.0F);
		this.setRotateAngle(tail, 0.7853981633974483F, 0.0F, 0.0F);
		this.armr = new ModelRenderer(this, 46, 0);
		this.armr.setRotationPoint(-5.0F, -5.0F, 1.0F);
		this.armr.addBox(-4.0F, -2.0F, -2.0F, 4, 8, 4, 0.0F);
		this.arml2 = new ModelRenderer(this, 46, 0);
		this.arml2.setRotationPoint(0.1F, 0.0F, 0.0F);
		this.arml2.addBox(0.0F, 4.5F, 0.5F, 4, 12, 4, 0.0F);
		this.setRotateAngle(arml2, -0.4363323129985824F, 0.0F, 0.0F);
		this.body = new ModelRenderer(this, 0, 20);
		this.body.setRotationPoint(0.0F, -7.0F, 0.0F);
		this.body.addBox(-5.0F, 0.0F, -4.0F, 10, 8, 8, 0.0F);
		this.setRotateAngle(body, 0.3490658503988659F, 0.0F, 0.0F);
		this.earr = new ModelRenderer(this, 0, 15);
		this.earr.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.earr.addBox(-4.0F, -11.0F, -1.0F, 3, 3, 1, 0.0F);
		this.legl2 = new ModelRenderer(this, 48, 45);
		this.legl2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.legl2.addBox(-1.5F, 4.0F, 2.0F, 3, 12, 4, 0.0F);
		this.armr2 = new ModelRenderer(this, 46, 0);
		this.armr2.setRotationPoint(0.1F, 0.0F, 0.0F);
		this.armr2.addBox(-4.2F, 4.5F, 0.5F, 4, 12, 4, 0.0F);
		this.setRotateAngle(armr2, -0.4363323129985824F, 0.0F, 0.0F);
		this.legr2 = new ModelRenderer(this, 48, 45);
		this.legr2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.legr2.addBox(-1.5F, 4.0F, 2.0F, 3, 12, 4, 0.0F);
		this.legl = new ModelRenderer(this, 30, 44);
		this.legl.setRotationPoint(2.5F, 7.9F, 3.0F);
		this.legl.addBox(-2.0F, 0.0F, -2.0F, 4, 8, 4, 0.0F);
		this.setRotateAngle(legl, -0.4363323129985824F, 0.0F, 0.0F);
		this.head.addChild(this.snout);
		this.head.addChild(this.earl);
		this.body.addChild(this.hip);
		this.arml.addChild(this.arml2);
		this.head.addChild(this.earr);
		this.legl.addChild(this.legl2);
		this.armr.addChild(this.armr2);
		this.legr.addChild(this.legr2);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		this.arml.render(f5);
		this.head.render(f5);
		this.legr.render(f5);
		this.tail.render(f5);
		this.armr.render(f5);
		this.body.render(f5);
		this.legl.render(f5);
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
		this.head.rotateAngleX = headPitch * 0.017453292F;
		this.head.rotateAngleY = netHeadYaw * 0.017453292F;
		this.arml.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		this.legl.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
		this.armr.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
		this.legr.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;

		if (entityIn.isSprinting()) {
			// TODO: handle sprinting animation
			this.head.rotateAngleX = headPitch * 0.017453292F;
			this.head.rotateAngleY = netHeadYaw * 0.017453292F;
			this.body.rotateAngleX = ((float) Math.PI / 2F);
			this.legl.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
			this.legr.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
			this.arml.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
			this.armr.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		}
	}
}
