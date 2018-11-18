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
		this.head.rotateAngleY = netHeadYaw * 0.017453292F;
		this.head.rotateAngleX = headPitch * 0.017453292F;
		this.legr.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
		this.legl.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount * 0.5F;
		this.armr.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
		this.arml.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount * 0.5F;

		// fix legs
		this.legr.rotateAngleX += -0.4235987755982988D;
		this.legl.rotateAngleX += -0.4235987755982988D;
		if (this.isCrouched) {
			this.armr.rotateAngleX -= -0.2235987755982988D;
			this.arml.rotateAngleX -= -0.2235987755982988D;
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
