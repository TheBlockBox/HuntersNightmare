package theblockbox.huntersdream.entity.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

/**
 * Lycanthrope - VampireRedEye Created using Tabula 7.0.0
 */
public class ModelLycanthropeBiped extends ModelBiped {
	public ModelRenderer tail;
	public ModelRenderer snout;
	public ModelRenderer jaw;
	public ModelRenderer nose;
	public ModelRenderer teeth;
	public ModelRenderer earl;
	public ModelRenderer earr;
	public ModelRenderer headfurl;
	public ModelRenderer headfurr;
	public ModelRenderer body2;
	public ModelRenderer neck;
	public ModelRenderer bodyfur1;
	public ModelRenderer bodyfur2;
	public ModelRenderer tail2;
	public ModelRenderer tail3;
	public ModelRenderer tail4;
	public ModelRenderer arml2;
	public ModelRenderer handl;
	public ModelRenderer armlfur1;
	public ModelRenderer hclawl;
	public ModelRenderer hclawl3;
	public ModelRenderer hclawl4;
	public ModelRenderer hclawl5;
	public ModelRenderer hclawl6;
	public ModelRenderer hclawl2;
	public ModelRenderer armr2;
	public ModelRenderer handr;
	public ModelRenderer armrfur1;
	public ModelRenderer hclawr;
	public ModelRenderer hclawr3;
	public ModelRenderer hclawr4;
	public ModelRenderer hclawr5;
	public ModelRenderer hclawr6;
	public ModelRenderer hclawr2;
	public ModelRenderer legl2;
	public ModelRenderer legl3;
	public ModelRenderer footl;
	public ModelRenderer clawl;
	public ModelRenderer clawl2;
	public ModelRenderer clawl3;
	public ModelRenderer clawl4;
	public ModelRenderer clawl5;
	public ModelRenderer clawl6;
	public ModelRenderer legr2;
	public ModelRenderer legr3;
	public ModelRenderer footr;
	public ModelRenderer clawr;
	public ModelRenderer clawr2;
	public ModelRenderer clawr3;
	public ModelRenderer clawr4;
	public ModelRenderer clawr5;
	public ModelRenderer clawr6;
	/** This value is weird */
	public static final float ROTATION = -0.5235987755982988F;
	/** The werewolf's height */
	public static final float HEIGHT = 2.6F;
	public static final float EYE_HEIGHT = 2.2F;

	public ModelLycanthropeBiped() {
		this.textureWidth = 128;
		this.textureHeight = 128;
		this.hclawl3 = new ModelRenderer(this, 42, 12);
		this.hclawl3.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.hclawl3.addBox(1.5F, 11.3F, -14.3F, 1, 1, 1, 0.0F);
		this.clawr5 = new ModelRenderer(this, 6, 120);
		this.clawr5.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.clawr5.addBox(-1.8F, 17.0F, -4.3F, 1, 3, 1, 0.0F);
		this.clawr6 = new ModelRenderer(this, 6, 120);
		this.clawr6.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.clawr6.addBox(-0.5F, 17.0F, -4.3F, 1, 4, 1, 0.0F);
		this.bodyfur2 = new ModelRenderer(this, 42, 60);
		this.bodyfur2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.bodyfur2.addBox(-3.2F, -3.2F, 8.5F, 6, 3, 8, 0.0F);
		this.setRotateAngle(bodyfur2, -1.48352986419518F, 0.0F, 0.0F);
		this.hclawr4 = new ModelRenderer(this, 42, 16);
		this.hclawr4.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.hclawr4.addBox(-1.2F, 11.5F, -15.3F, 1, 3, 1, 0.0F);
		this.hclawl6 = new ModelRenderer(this, 42, 16);
		this.hclawl6.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.hclawl6.addBox(1.5F, 11.5F, -15.3F, 1, 4, 1, 0.0F);
		this.hclawl2 = new ModelRenderer(this, 42, 12);
		this.hclawl2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.hclawl2.addBox(0.2F, 11.3F, -14.3F, 1, 1, 1, 0.0F);
		this.bodyfur1 = new ModelRenderer(this, 42, 48);
		this.bodyfur1.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.bodyfur1.addBox(-4.0F, -3.4F, 3.5F, 8, 3, 6, 0.0F);
		this.setRotateAngle(bodyfur1, -1.2217304763960306F, 0.0F, 0.0F);
		this.clawl6 = new ModelRenderer(this, 6, 120);
		this.clawl6.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.clawl6.addBox(-0.5F, 17.0F, -4.2F, 1, 4, 1, 0.0F);
		this.bipedBody = new ModelRenderer(this, 0, 30);
		this.bipedBody.setRotationPoint(0.0F, -13.0F, -5.0F);
		this.bipedBody.addBox(-5.0F, 0.0F, -4.5F, 10, 10, 9, 0.0F);
		this.setRotateAngle(bipedBody, 0.3490658503988659F, 0.0F, 0.0F);
		this.clawr = new ModelRenderer(this, 0, 120);
		this.clawr.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.clawr.addBox(0.8F, 16.7F, -3.3F, 1, 1, 1, 0.0F);
		this.clawl5 = new ModelRenderer(this, 6, 120);
		this.clawl5.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.clawl5.addBox(-1.8F, 17.0F, -4.2F, 1, 3, 1, 0.0F);
		this.handl = new ModelRenderer(this, 20, 116);
		this.handl.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.handl.addBox(0.0F, 11.0F, -14.0F, 4, 3, 6, 0.0F);
		this.armr2 = new ModelRenderer(this, 42, 12);
		this.armr2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.armr2.addBox(-4.2F, 4.5F, -15.0F, 4, 4, 12, 0.0F);
		this.setRotateAngle(armr2, 0.5235987755982988F, 0.0F, 0.0F);
		this.snout = new ModelRenderer(this, 28, 0);
		this.snout.setRotationPoint(0.0F, 0.8F, 0.0F);
		this.snout.addBox(-1.5F, -1.0F, -10.0F, 3, 3, 5, 0.0F);
		this.armrfur1 = new ModelRenderer(this, 42, 20);
		this.armrfur1.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.armrfur1.addBox(-2.0F, 8.5F, -14.0F, 0, 5, 10, 0.0F);
		this.setRotateAngle(armrfur1, 0.5235987755982988F, 0.0F, 0.0F);
		this.handr = new ModelRenderer(this, 20, 116);
		this.handr.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.handr.addBox(-4.0F, 11.0F, -14.0F, 4, 3, 6, 0.0F);
		this.hclawl5 = new ModelRenderer(this, 42, 16);
		this.hclawl5.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.hclawl5.addBox(0.2F, 11.5F, -15.3F, 1, 3, 1, 0.0F);
		this.bipedLeftArm = new ModelRenderer(this, 68, 8);
		this.bipedLeftArm.setRotationPoint(4.8F, -10.0F, -4.0F);
		this.bipedLeftArm.addBox(0.0F, -1.5F, -2.0F, 4, 10, 4, 0.0F);
		this.setRotateAngle(bipedLeftArm, 0.4363323129985824F, 0.0F, 0.0F);
		this.tail4 = new ModelRenderer(this, 74, 72);
		this.tail4.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.tail4.addBox(-2.0F, -1.5F, 11.0F, 4, 4, 4, 0.0F);
		this.setRotateAngle(tail4, -0.13962634015954636F, 0.0F, 0.0F);
		this.clawr3 = new ModelRenderer(this, 0, 120);
		this.clawr3.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.clawr3.addBox(-0.5F, 16.7F, -3.3F, 1, 1, 1, 0.0F);
		this.teeth = new ModelRenderer(this, 60, 0);
		this.teeth.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.teeth.addBox(-1.5F, 2.8F, -10.0F, 3, 2, 4, 0.0F);
		this.bipedLeftLeg = new ModelRenderer(this, 0, 76);
		this.bipedLeftLeg.setRotationPoint(4.0F, 4.6F, 0.0F);
		this.bipedLeftLeg.addBox(-2.0F, -2.0F, -3.0F, 4, 10, 6, 0.0F);
		this.setRotateAngle(bipedLeftLeg, ROTATION, 0.0F, 0.0F);
		this.hclawr5 = new ModelRenderer(this, 42, 16);
		this.hclawr5.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.hclawr5.addBox(-3.8F, 11.5F, -15.3F, 1, 3, 1, 0.0F);
		this.bipedRightLeg = new ModelRenderer(this, 0, 76);
		this.bipedRightLeg.setRotationPoint(-4.0F, 4.6F, 0.0F);
		this.bipedRightLeg.addBox(-2.0F, -2.0F, -3.0F, 4, 10, 6, 0.0F);
		this.setRotateAngle(bipedRightLeg, ROTATION, 0.0F, 0.0F);
		this.headfurl = new ModelRenderer(this, 26, 14);
		this.headfurl.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.headfurl.addBox(0.8F, 0.5F, -4.0F, 4, 4, 1, 0.0F);
		this.setRotateAngle(headfurl, -0.27314402793711257F, -0.5462880558742251F, 0.0F);
		this.hclawr3 = new ModelRenderer(this, 42, 12);
		this.hclawr3.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.hclawr3.addBox(-2.5F, 11.3F, -14.3F, 1, 1, 1, 0.0F);
		this.tail = new ModelRenderer(this, 74, 38);
		this.tail.setRotationPoint(0.0F, 3.5F, 2.0F);
		this.tail.addBox(-1.5F, -1.5F, 0.0F, 3, 3, 3, 0.0F);
		this.setRotateAngle(tail, -0.593411945678072F, 0.0F, 0.0F);
		this.clawr4 = new ModelRenderer(this, 6, 120);
		this.clawr4.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.clawr4.addBox(0.8F, 17.0F, -4.3F, 1, 3, 1, 0.0F);
		this.legr3 = new ModelRenderer(this, 0, 104);
		this.legr3.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.legr3.addBox(-1.5F, 6.5F, 0.0F, 3, 10, 3, 0.0F);
		this.setRotateAngle(legr3, 0.5235987755982988F, 0.0F, 0.0F);
		this.arml2 = new ModelRenderer(this, 42, 12);
		this.arml2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.arml2.addBox(0.2F, 4.5F, -15.0F, 4, 4, 12, 0.0F);
		this.setRotateAngle(arml2, 0.5235987755982988F, 0.0F, 0.0F);
		this.body2 = new ModelRenderer(this, 0, 50);
		this.body2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.body2.addBox(-4.0F, 9.0F, -2.0F, 8, 10, 7, 0.0F);
		this.setRotateAngle(body2, -0.13962634015954636F, 0.0F, 0.0F);
		this.footl = new ModelRenderer(this, 20, 116);
		this.footl.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.footl.addBox(-2.0F, 16.4F, -2.8F, 4, 3, 6, 0.0F);
		this.setRotateAngle(footl, 0.5235987755982988F, 0.0F, 0.0F);
		this.clawl2 = new ModelRenderer(this, 0, 120);
		this.clawl2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.clawl2.addBox(-1.8F, 16.7F, -3.3F, 1, 1, 1, 0.0F);
		this.clawr2 = new ModelRenderer(this, 0, 120);
		this.clawr2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.clawr2.addBox(-1.8F, 16.7F, -3.3F, 1, 1, 1, 0.0F);
		this.hclawl4 = new ModelRenderer(this, 42, 16);
		this.hclawl4.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.hclawl4.addBox(2.8F, 11.5F, -15.3F, 1, 3, 1, 0.0F);
		this.hclawr2 = new ModelRenderer(this, 42, 12);
		this.hclawr2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.hclawr2.addBox(-3.8F, 11.3F, -14.3F, 1, 1, 1, 0.0F);
		this.tail2 = new ModelRenderer(this, 74, 46);
		this.tail2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.tail2.addBox(-2.0F, -2.4F, 2.5F, 4, 4, 4, 0.0F);
		this.setRotateAngle(tail2, -0.13962634015954636F, 0.0F, 0.0F);
		this.earl = new ModelRenderer(this, 24, 0);
		this.earl.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.earl.addBox(1.0F, -5.9F, -3.0F, 2, 3, 1, 0.0F);
		this.clawl3 = new ModelRenderer(this, 0, 120);
		this.clawl3.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.clawl3.addBox(-0.5F, 16.7F, -3.3F, 1, 1, 1, 0.0F);
		this.nose = new ModelRenderer(this, 42, 0);
		this.nose.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.nose.addBox(-1.0F, -0.3F, -10.1F, 2, 1, 1, 0.0F);
		this.earr = new ModelRenderer(this, 24, 0);
		this.earr.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.earr.addBox(-3.0F, -5.9F, -3.0F, 2, 3, 1, 0.0F);
		this.hclawr6 = new ModelRenderer(this, 42, 16);
		this.hclawr6.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.hclawr6.addBox(-2.5F, 11.5F, -15.3F, 1, 4, 1, 0.0F);
		this.neck = new ModelRenderer(this, 0, 16);
		this.neck.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.neck.addBox(-3.0F, -3.0F, -3.0F, 6, 6, 6, 0.0F);
		this.setRotateAngle(neck, 0.6829473363053812F, 0.0F, 0.0F);
		this.bipedRightArm = new ModelRenderer(this, 68, 8);
		this.bipedRightArm.setRotationPoint(-4.8F, -10.0F, -4.0F);
		this.bipedRightArm.addBox(-4.0F, -1.5F, -2.0F, 4, 10, 4, 0.0F);
		this.setRotateAngle(bipedRightArm, 0.4363323129985824F, 0.0F, 0.0F);
		this.bipedHead = new ModelRenderer(this, 0, 0);
		this.bipedHead.setRotationPoint(0.0F, -15.0F, -6.2F);
		this.bipedHead.addBox(-3.5F, -3.0F, -6.0F, 7, 7, 6, 0.0F);
		this.tail3 = new ModelRenderer(this, 70, 56);
		this.tail3.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.tail3.addBox(-2.5F, -3.6F, 4.0F, 5, 5, 8, 0.0F);
		this.setRotateAngle(tail3, -0.2792526803190927F, 0.0F, 0.0F);
		this.clawl4 = new ModelRenderer(this, 6, 120);
		this.clawl4.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.clawl4.addBox(0.8F, 17.0F, -4.2F, 1, 3, 1, 0.0F);
		this.footr = new ModelRenderer(this, 20, 116);
		this.footr.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.footr.addBox(-2.0F, 16.4F, -2.8F, 4, 3, 6, 0.0F);
		this.setRotateAngle(footr, 0.5235987755982988F, 0.0F, 0.0F);
		this.clawl = new ModelRenderer(this, 0, 120);
		this.clawl.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.clawl.addBox(0.8F, 16.7F, -3.3F, 1, 1, 1, 0.0F);
		this.headfurr = new ModelRenderer(this, 28, 22);
		this.headfurr.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.headfurr.addBox(-4.8F, 0.5F, -4.0F, 4, 4, 1, 0.0F);
		this.setRotateAngle(headfurr, -0.27314402793711257F, 0.5462880558742251F, 0.0F);
		this.hclawl = new ModelRenderer(this, 42, 12);
		this.hclawl.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.hclawl.addBox(2.8F, 11.3F, -14.3F, 1, 1, 1, 0.0F);
		this.jaw = new ModelRenderer(this, 46, 0);
		this.jaw.setRotationPoint(0.0F, 2.6F, -6.0F);
		this.jaw.addBox(-1.0F, 0.0F, -3.5F, 2, 1, 4, 0.0F);
		this.setRotateAngle(jaw, 0.5235987755982988F, 0.0F, 0.0F);
		this.legl2 = new ModelRenderer(this, 0, 94);
		this.legl2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.legl2.addBox(-2.0F, 4.0F, 3.0F, 4, 4, 3, 0.0F);
		this.legr2 = new ModelRenderer(this, 0, 94);
		this.legr2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.legr2.addBox(-2.0F, 4.0F, 3.0F, 4, 4, 3, 0.0F);
		this.armlfur1 = new ModelRenderer(this, 42, 20);
		this.armlfur1.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.armlfur1.addBox(2.0F, 8.5F, -14.0F, 0, 5, 10, 0.0F);
		this.setRotateAngle(armlfur1, 0.5235987755982988F, 0.0F, 0.0F);
		this.hclawr = new ModelRenderer(this, 42, 12);
		this.hclawr.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.hclawr.addBox(-1.2F, 11.3F, -14.3F, 1, 1, 1, 0.0F);
		this.legl3 = new ModelRenderer(this, 0, 104);
		this.legl3.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.legl3.addBox(-1.5F, 6.5F, 0.0F, 3, 10, 3, 0.0F);
		this.setRotateAngle(legl3, 0.5235987755982988F, 0.0F, 0.0F);
		this.handl.addChild(this.hclawl3);
		this.footr.addChild(this.clawr5);
		this.footr.addChild(this.clawr6);
		this.bipedBody.addChild(this.bodyfur2);
		this.handr.addChild(this.hclawr4);
		this.handl.addChild(this.hclawl6);
		this.handl.addChild(this.hclawl2);
		this.bipedBody.addChild(this.bodyfur1);
		this.footl.addChild(this.clawl6);
		this.footr.addChild(this.clawr);
		this.footl.addChild(this.clawl5);
		this.bipedLeftArm.addChild(this.handl);
		this.bipedRightArm.addChild(this.armr2);
		this.bipedHead.addChild(this.snout);
		this.bipedRightArm.addChild(this.armrfur1);
		this.bipedRightArm.addChild(this.handr);
		this.handl.addChild(this.hclawl5);
		this.tail.addChild(this.tail4);
		this.footr.addChild(this.clawr3);
		this.bipedHead.addChild(this.teeth);
		this.handr.addChild(this.hclawr5);
		this.bipedHead.addChild(this.headfurl);
		this.handr.addChild(this.hclawr3);
		this.footr.addChild(this.clawr4);
		this.bipedRightLeg.addChild(this.legr3);
		this.bipedLeftArm.addChild(this.arml2);
		this.bipedBody.addChild(this.body2);
		this.bipedLeftLeg.addChild(this.footl);
		this.footl.addChild(this.clawl2);
		this.footr.addChild(this.clawr2);
		this.handl.addChild(this.hclawl4);
		this.handr.addChild(this.hclawr2);
		this.tail.addChild(this.tail2);
		this.bipedHead.addChild(this.earl);
		this.footl.addChild(this.clawl3);
		this.bipedHead.addChild(this.nose);
		this.bipedHead.addChild(this.earr);
		this.handr.addChild(this.hclawr6);
		this.bipedBody.addChild(this.neck);
		this.tail.addChild(this.tail3);
		this.footl.addChild(this.clawl4);
		this.bipedRightLeg.addChild(this.footr);
		this.footl.addChild(this.clawl);
		this.bipedHead.addChild(this.headfurr);
		this.handl.addChild(this.hclawl);
		this.bipedHead.addChild(this.jaw);
		this.bipedLeftLeg.addChild(this.legl2);
		this.bipedRightLeg.addChild(this.legr2);
		this.bipedLeftArm.addChild(this.armlfur1);
		this.handr.addChild(this.hclawr);
		this.bipedLeftLeg.addChild(this.legl3);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		this.bipedBody.render(f5);
		this.bipedLeftArm.render(f5);
		this.bipedLeftLeg.render(f5);
		this.bipedRightLeg.render(f5);
		this.tail.render(f5);
		this.bipedRightArm.render(f5);
		this.bipedHead.render(f5);
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
		this.bipedHead.rotateAngleX = headPitch * 0.017453292F;
		this.bipedRightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
		this.bipedLeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount
				* 0.5F;
		this.bipedRightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
		this.bipedLeftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount
				* 0.5F;

		// fix legs
		this.bipedRightLeg.rotateAngleX += ROTATION;
		this.bipedLeftLeg.rotateAngleX += ROTATION;
		this.bipedRightArm.rotateAngleX -= ROTATION;
		this.bipedLeftArm.rotateAngleX -= ROTATION;
	}
}
