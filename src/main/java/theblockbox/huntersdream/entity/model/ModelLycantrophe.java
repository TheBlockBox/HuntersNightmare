package theblockbox.huntersdream.entity.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public abstract class ModelLycantrophe extends ModelBase {
	public ModelRenderer body;
	public ModelRenderer head;
	public ModelRenderer legl;
	public ModelRenderer legr;
	public ModelRenderer arml;
	public ModelRenderer armr;
	public ModelRenderer tail;
	protected final boolean isCrouched;
	protected boolean isTailGoingLeft = false;

	public ModelLycantrophe(boolean isCrouched) {
		this.isCrouched = isCrouched;
	}

	@Override
	public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scale) {
		this.tail.render(scale);
		this.arml.render(scale);
		this.armr.render(scale);
		this.legl.render(scale);
		this.legr.render(scale);
		this.head.render(scale);
		this.body.render(scale);
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scaleFactor, Entity entityIn) {
		this.arml.rotateAngleZ = 0.0F;
		this.armr.rotateAngleZ = 0.0F;
		this.armr.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F + 1) * 0.05F;
		this.arml.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F + 1) * 0.05F;
		this.head.rotateAngleY = netHeadYaw * 0.017453292F;
		this.head.rotateAngleX = headPitch * 0.017453292F;
		this.legl.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 0.7F * limbSwingAmount;
		this.legr.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 0.7F * limbSwingAmount;
		this.armr.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 0.7F * limbSwingAmount;
		this.arml.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 0.7F * limbSwingAmount;

		// fix legs
		this.legr.rotateAngleX -= 0.3D;
		this.legl.rotateAngleX -= 0.3D;
		if (this.isCrouched) {
			// fix arms
			this.armr.rotateAngleX -= -0.2235987755982988D;
			this.arml.rotateAngleX -= -0.2235987755982988D;
		}
		if (limbSwingAmount <= .1F) {
			float tailY = this.tail.rotateAngleY;
			this.isTailGoingLeft = tailY >= .2F || (tailY > -.15F && this.isTailGoingLeft);
			this.tail.rotateAngleY += this.isTailGoingLeft ? -.005F : .005F;
			this.tail.rotateAngleX -= this.tail.rotateAngleX > -.85F ? .1F : 0;
		} else {
			this.tail.rotateAngleY = 0F;
			this.tail.rotateAngleX += this.tail.rotateAngleX < .4F ? .1F : 0;
		}
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
