package theblockbox.huntersdream.entity.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped.ArmPose;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

/**
 * ModelWolfman - VampireRedEye Created using Tabula 7.0.0
 */
public class ModelWolfman extends ModelBase {
	public ModelRenderer head;
	public ModelRenderer body;
	public ModelRenderer arml;
	public ModelRenderer armr;
	public ModelRenderer legl;
	public ModelRenderer legr;
	public ModelRenderer snout;
	public ModelRenderer earl;
	public ModelRenderer earr;
	public ModelRenderer body2;
	public ModelRenderer tail;
	public ModelRenderer legl2;
	public ModelRenderer legr2;
	public boolean isSneak;
	public ArmPose rightArmPose;
	public ArmPose leftArmPose;

	public ModelWolfman() {
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.armr = new ModelRenderer(this, 36, 18);
		this.armr.setRotationPoint(-5.0F, 0.0F, 0.0F);
		this.armr.addBox(-3.0F, -2.0F, -2.0F, 3, 14, 4, 0.0F);
		this.snout = new ModelRenderer(this, 32, 0);
		this.snout.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.snout.addBox(-1.5F, -3.0F, -7.0F, 3, 3, 6, 0.0F);
		this.body2 = new ModelRenderer(this, 0, 32);
		this.body2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.body2.addBox(-3.0F, 8.0F, -2.0F, 6, 6, 4, 0.0F);
		this.head = new ModelRenderer(this, 3, 4);
		this.head.setRotationPoint(0.0F, -2.0F, 0.0F);
		this.head.addBox(-3.0F, -6.0F, -4.0F, 6, 6, 6, 0.0F);
		this.body = new ModelRenderer(this, 0, 16);
		this.body.setRotationPoint(0.0F, -2.0F, 0.0F);
		this.body.addBox(-5.0F, 0.0F, -3.0F, 10, 8, 6, 0.0F);
		this.tail = new ModelRenderer(this, 26, 40);
		this.tail.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.tail.addBox(-1.0F, 6.0F, 8.7F, 2, 2, 10, 0.0F);
		this.setRotateAngle(tail, -0.7853981633974483F, 0.0F, 0.0F);
		this.earr = new ModelRenderer(this, 24, 0);
		this.earr.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.earr.addBox(-3.0F, -8.0F, 0.0F, 2, 2, 1, 0.0F);
		this.arml = new ModelRenderer(this, 36, 18);
		this.arml.setRotationPoint(5.0F, 0.0F, 0.0F);
		this.arml.addBox(0.0F, -2.0F, -2.0F, 3, 14, 4, 0.0F);

		this.legl = new ModelRenderer(this, 0, 42);
		this.legl.setRotationPoint(3.0F, 11.0F, 0.0F);
		this.legl.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.0F);
		this.setRotateAngle(legl, -0.17453292519943295F, 0.0F, 0.0F);
		this.legr = new ModelRenderer(this, 0, 42);
		this.legr.setRotationPoint(-3.0F, 11.0F, 0.0F);
		this.legr.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.0F);
		this.setRotateAngle(legr, -0.17453292519943295F, 0.0F, 0.0F);

		this.legr2 = new ModelRenderer(this, 14, 50);
		this.legr2.setRotationPoint(0.5F, 0.0F, 0.0F);
		this.legr2.addBox(-2.0F, 3.0F, -0.5F, 3, 10, 3, 0.0F);
		this.setRotateAngle(legr2, 0.17453292519943295F, 0.0F, 0.0F);
		this.legl2 = new ModelRenderer(this, 14, 50);
		this.legl2.setRotationPoint(0.5F, 0.0F, 0.0F);
		this.legl2.addBox(-2.0F, 3.0F, -0.5F, 3, 10, 3, 0.0F);
		this.setRotateAngle(legl2, 0.17453292519943295F, 0.0F, 0.0F);
		this.earl = new ModelRenderer(this, 24, 0);
		this.earl.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.earl.addBox(1.0F, -8.0F, 0.0F, 2, 2, 1, 0.0F);
		this.head.addChild(this.snout);
		this.body.addChild(this.body2);
		this.body.addChild(this.tail);
		this.head.addChild(this.earr);
		this.legr.addChild(this.legr2);
		this.legl.addChild(this.legl2);
		this.head.addChild(this.earl);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		this.armr.render(f5);
		this.head.render(f5);
		this.body.render(f5);
		this.arml.render(f5);
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
		this.head.rotateAngleX = headPitch * 0.017453292F;
		this.head.rotateAngleY = netHeadYaw * 0.017453292F;
		this.arml.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount / 2;
		this.legl.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount / 2;
		this.armr.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount / 2;
		this.legr.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount / 2;
	}
}
