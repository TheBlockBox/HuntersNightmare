package theblockbox.huntersdream.entity.model;

import net.minecraft.client.model.ModelRenderer;

/**
 * LycanthropeAlexCrouched - VampireRedEye Created using Tabula 7.0.0
 */
public class ModelLycanthropeAlexCrouched extends ModelLycanthrope {
	public ModelRenderer mane;
	public ModelRenderer furbodyb;
	public ModelRenderer furbodyb2;
	public ModelRenderer snout;
	public ModelRenderer jaw;
	public ModelRenderer earl;
	public ModelRenderer earr;
	public ModelRenderer furfl;
	public ModelRenderer furfr;
	public ModelRenderer furft;
	public ModelRenderer furfb;
	public ModelRenderer snoutteeth;
	public ModelRenderer nose;
	public ModelRenderer jawteeth;
	public ModelRenderer earl2;
	public ModelRenderer earl2_1;
	public ModelRenderer earr_1;
	public ModelRenderer earr_2;
	public ModelRenderer legl2;
	public ModelRenderer legl3;
	public ModelRenderer footl;
	public ModelRenderer footlclaw1;
	public ModelRenderer footlclaw2;
	public ModelRenderer footlclaw3;
	public ModelRenderer legr2;
	public ModelRenderer legr3;
	public ModelRenderer footr;
	public ModelRenderer footrclaw1;
	public ModelRenderer footrclaw3;
	public ModelRenderer footrclaw2;
	public ModelRenderer arml2;
	public ModelRenderer furarml;
	public ModelRenderer armlclaw1;
	public ModelRenderer armlclaw2;
	public ModelRenderer armlclaw3;
	public ModelRenderer armr2;
	public ModelRenderer furarmr;
	public ModelRenderer armrclaw1;
	public ModelRenderer armrclaw2;
	public ModelRenderer armrclaw3;
	public ModelRenderer tail2;
	public ModelRenderer tail_1;

	public ModelLycanthropeAlexCrouched() {
		super(true);
		this.textureWidth = 128;
		this.textureHeight = 128;
		this.furbodyb = new ModelRenderer(this, 68, 36);
		this.furbodyb.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.furbodyb.addBox(-3.9F, -5.1F, 6.0F, 8, 8, 0, 0.0F);
		ModelLycanthrope.setRotateAngle(this.furbodyb, 0.4363323129985824F, 0.0F, 0.0F);
		this.earr_2 = new ModelRenderer(this, 48, 26);
		this.earr_2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.earr_2.addBox(-5.0F, -9.0F, -4.0F, 1, 1, 1, 0.0F);
		this.armrclaw1 = new ModelRenderer(this, 110, 18);
		this.armrclaw1.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.armrclaw1.addBox(-3.0F, 14.0F, 0.0F, 2, 2, 1, 0.0F);
		this.earl2_1 = new ModelRenderer(this, 56, 26);
		this.earl2_1.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.earl2_1.addBox(4.0F, -9.0F, -4.0F, 1, 1, 1, 0.0F);
		this.furfl = new ModelRenderer(this, 90, 20);
		this.furfl.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.furfl.addBox(4.2F, -6.5F, -3.6F, 0, 6, 8, 0.0F);
		ModelLycanthrope.setRotateAngle(this.furfl, 0.0F, 0.22689280275926282F, 0.0F);
		this.armlclaw3 = new ModelRenderer(this, 110, 14);
		this.armlclaw3.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.armlclaw3.addBox(1.0F, 14.0F, 1.5F, 2, 2, 1, 0.0F);
		this.nose = new ModelRenderer(this, 0, 0);
		this.nose.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.nose.addBox(-1.0F, -3.1F, -8.6F, 2, 1, 1, 0.0F);
		this.arml2 = new ModelRenderer(this, 68, 0);
		this.arml2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.arml2.addBox(0.2F, 4.0F, 0.0F, 3, 10, 4, 0.0F);
		ModelLycanthrope.setRotateAngle(this.arml2, -0.3490658503988659F, 0.0F, 0.0F);
		this.tail = new ModelRenderer(this, 88, 0);
		this.tail.setRotationPoint(0.0F, 6.0F, 2.5F);
		this.tail.addBox(-1.5F, -1.5F, 0.0F, 3, 3, 3, 0.0F);
		ModelLycanthrope.setRotateAngle(this.tail, -0.7853981633974483F, 0.0F, 0.0F);
		this.armlclaw1 = new ModelRenderer(this, 110, 14);
		this.armlclaw1.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.armlclaw1.addBox(1.0F, 14.0F, 0.0F, 2, 2, 1, 0.0F);
		this.legr2 = new ModelRenderer(this, 0, 52);
		this.legr2.setRotationPoint(0.0F, 0.0F, -0.1F);
		this.legr2.addBox(-2.0F, 2.0F, -5.0F, 4, 6, 4, 0.0F);
		ModelLycanthrope.setRotateAngle(this.legr2, 1.0016444577195458F, 0.0F, 0.0F);
		this.footrclaw1 = new ModelRenderer(this, 110, 22);
		this.footrclaw1.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.footrclaw1.addBox(0.8F, 14.0F, -4.0F, 1, 2, 2, 0.0F);
		this.footl = new ModelRenderer(this, 30, 54);
		this.footl.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.footl.addBox(-2.0F, 13.5F, -2.4F, 4, 3, 5, 0.0F);
		ModelLycanthrope.setRotateAngle(this.footl, 0.3490658503988659F, 0.0F, 0.0F);
		this.jawteeth = new ModelRenderer(this, 38, 6);
		this.jawteeth.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.jawteeth.addBox(-1.0F, -1.0F, -3.0F, 2, 1, 3, 0.0F);
		this.furarmr = new ModelRenderer(this, 88, 10);
		this.furarmr.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.furarmr.addBox(-2.0F, 3.0F, 2.0F, 0, 8, 6, 0.0F);
		ModelLycanthrope.setRotateAngle(this.furarmr, -0.3141592653589793F, 0.0F, 0.0F);
		this.footlclaw3 = new ModelRenderer(this, 110, 22);
		this.footlclaw3.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.footlclaw3.addBox(-0.5F, 14.0F, -4.0F, 1, 2, 2, 0.0F);
		this.armr2 = new ModelRenderer(this, 68, 0);
		this.armr2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.armr2.addBox(-3.2F, 4.0F, 0.0F, 3, 10, 4, 0.0F);
		ModelLycanthrope.setRotateAngle(this.armr2, -0.3490658503988659F, 0.0F, 0.0F);
		this.mane = new ModelRenderer(this, 0, 32);
		this.mane.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.mane.addBox(-4.5F, -7.0F, -4.5F, 9, 8, 9, 0.0F);
		ModelLycanthrope.setRotateAngle(this.mane, 0.17453292519943295F, 0.0F, 0.0F);
		this.footr = new ModelRenderer(this, 30, 54);
		this.footr.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.footr.addBox(-2.0F, 13.5F, -2.4F, 4, 3, 5, 0.0F);
		ModelLycanthrope.setRotateAngle(this.footr, 0.3490658503988659F, 0.0F, 0.0F);
		this.footrclaw3 = new ModelRenderer(this, 110, 22);
		this.footrclaw3.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.footrclaw3.addBox(-0.5F, 14.0F, -4.0F, 1, 2, 2, 0.0F);
		this.furbodyb2 = new ModelRenderer(this, 68, 36);
		this.furbodyb2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.furbodyb2.addBox(-4.0F, 1.7F, 3.0F, 8, 8, 0, 0.0F);
		ModelLycanthrope.setRotateAngle(this.furbodyb2, 0.2617993877991494F, 0.0F, 0.0F);
		this.legl2 = new ModelRenderer(this, 0, 52);
		this.legl2.setRotationPoint(0.0F, 0.0F, -0.1F);
		this.legl2.addBox(-2.0F, 2.0F, -5.0F, 4, 6, 4, 0.0F);
		ModelLycanthrope.setRotateAngle(this.legl2, 1.0016444577195458F, 0.0F, 0.0F);
		this.footlclaw1 = new ModelRenderer(this, 110, 22);
		this.footlclaw1.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.footlclaw1.addBox(0.8F, 14.0F, -4.0F, 1, 2, 2, 0.0F);
		this.earr = new ModelRenderer(this, 48, 12);
		this.earr.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.earr.addBox(-5.0F, -7.0F, -4.0F, 1, 6, 3, 0.0F);
		ModelLycanthrope.setRotateAngle(this.earr, -1.0471975511965976F, -0.6108652381980153F, 0.2792526803190927F);
		this.legl3 = new ModelRenderer(this, 50, 50);
		this.legl3.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.legl3.addBox(-1.5F, 7.0F, 0.4F, 3, 8, 3, 0.0F);
		ModelLycanthrope.setRotateAngle(this.legl3, 0.3490658503988659F, 0.0F, 0.0F);
		this.armr = new ModelRenderer(this, 68, 17);
		this.armr.setRotationPoint(-4.0F, 0.5F, -7.0F);
		this.armr.addBox(-3.0F, -2.0F, -2.0F, 3, 8, 4, 0.0F);
		ModelLycanthrope.setRotateAngle(this.armr, 0.0F, 0.0F, 0.05235987755982988F);
		this.tail2 = new ModelRenderer(this, 100, 0);
		this.tail2.setRotationPoint(0.0F, 0.0F, 3.0F);
		this.tail2.addBox(-2.0F, -2.5F, -0.8F, 4, 4, 8, 0.0F);
		ModelLycanthrope.setRotateAngle(this.tail2, -0.3490658503988659F, 0.0F, 0.0F);
		this.jaw = new ModelRenderer(this, 50, 0);
		this.jaw.setRotationPoint(0.0F, -2.0F, -5.0F);
		this.jaw.addBox(-1.0F, 0.0F, -3.0F, 2, 2, 3, 0.0F);
		this.body = new ModelRenderer(this, 0, 16);
		this.body.setRotationPoint(0.0F, 2.0F, -4.0F);
		this.body.addBox(-3.5F, 0.0F, -3.5F, 7, 8, 7, 0.0F);
		ModelLycanthrope.setRotateAngle(this.body, 0.7853981633974483F, 0.0F, 0.0F);
		this.footlclaw2 = new ModelRenderer(this, 110, 22);
		this.footlclaw2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.footlclaw2.addBox(-1.8F, 14.0F, -4.0F, 1, 2, 2, 0.0F);
		this.legr3 = new ModelRenderer(this, 50, 50);
		this.legr3.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.legr3.addBox(-1.5F, 7.0F, 0.4F, 3, 8, 3, 0.0F);
		ModelLycanthrope.setRotateAngle(this.legr3, 0.3490658503988659F, 0.0F, 0.0F);
		this.armrclaw2 = new ModelRenderer(this, 110, 18);
		this.armrclaw2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.armrclaw2.addBox(-3.0F, 14.0F, 3.0F, 2, 2, 1, 0.0F);
		this.legl = new ModelRenderer(this, 40, 30);
		this.legl.setRotationPoint(3.0F, 8.0F, 0.0F);
		this.legl.addBox(-2.5F, -2.0F, -2.5F, 5, 8, 5, 0.0F);
		ModelLycanthrope.setRotateAngle(this.legl, -0.3490658503988659F, 0.0F, 0.0F);
		this.earl = new ModelRenderer(this, 56, 12);
		this.earl.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.earl.addBox(4.0F, -7.0F, -4.0F, 1, 6, 3, 0.0F);
		ModelLycanthrope.setRotateAngle(this.earl, -1.0471975511965976F, 0.6108652381980153F, -0.2792526803190927F);
		this.furarml = new ModelRenderer(this, 88, 10);
		this.furarml.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.furarml.addBox(2.0F, 3.0F, 2.0F, 0, 8, 6, 0.0F);
		ModelLycanthrope.setRotateAngle(this.furarml, -0.3141592653589793F, 0.0F, 0.0F);
		this.legr = new ModelRenderer(this, 40, 30);
		this.legr.setRotationPoint(-3.0F, 8.0F, 0.0F);
		this.legr.addBox(-2.5F, -2.0F, -2.5F, 5, 8, 5, 0.0F);
		ModelLycanthrope.setRotateAngle(this.legr, -0.3490658503988659F, 0.0F, 0.0F);
		this.earr_1 = new ModelRenderer(this, 48, 22);
		this.earr_1.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.earr_1.addBox(-5.0F, -8.0F, -4.0F, 1, 1, 2, 0.0F);
		this.furfr = new ModelRenderer(this, 90, 20);
		this.furfr.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.furfr.addBox(-4.2F, -6.5F, -3.6F, 0, 6, 8, 0.0F);
		ModelLycanthrope.setRotateAngle(this.furfr, 0.0F, -0.22689280275926282F, 0.0F);
		this.furfb = new ModelRenderer(this, 90, 30);
		this.furfb.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.furfb.addBox(3.5F, -3.0F, -5.7F, 0, 6, 6, 0.0F);
		ModelLycanthrope.setRotateAngle(this.furfb, 0.0F, -1.3089969389957472F, -1.5707963267948966F);
		this.footrclaw2 = new ModelRenderer(this, 110, 22);
		this.footrclaw2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.footrclaw2.addBox(-1.8F, 14.0F, -4.0F, 1, 2, 2, 0.0F);
		this.furft = new ModelRenderer(this, 90, 20);
		this.furft.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.furft.addBox(-7.3F, -3.0F, -3.0F, 0, 6, 8, 0.0F);
		ModelLycanthrope.setRotateAngle(this.furft, 0.0F, -0.19198621771937624F, 1.5707963267948966F);
		this.earl2 = new ModelRenderer(this, 56, 22);
		this.earl2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.earl2.addBox(4.0F, -8.0F, -4.0F, 1, 1, 2, 0.0F);
		this.armlclaw2 = new ModelRenderer(this, 110, 14);
		this.armlclaw2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.armlclaw2.addBox(1.0F, 14.0F, 3.0F, 2, 2, 1, 0.0F);
		this.arml = new ModelRenderer(this, 68, 17);
		this.arml.setRotationPoint(4.0F, 0.5F, -7.0F);
		this.arml.addBox(0.0F, -2.0F, -2.0F, 3, 8, 4, 0.0F);
		ModelLycanthrope.setRotateAngle(this.arml, 0.0F, 0.0F, -0.05235987755982988F);
		this.armrclaw3 = new ModelRenderer(this, 110, 18);
		this.armrclaw3.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.armrclaw3.addBox(-3.0F, 14.0F, 1.5F, 2, 2, 1, 0.0F);
		this.snoutteeth = new ModelRenderer(this, 48, 6);
		this.snoutteeth.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.snoutteeth.addBox(-1.5F, -1.0F, -8.5F, 3, 1, 4, 0.0F);
		this.head = new ModelRenderer(this, 0, 0);
		this.head.setRotationPoint(0.0F, 1.0F, -11.0F);
		this.head.addBox(-3.5F, -7.0F, -5.0F, 7, 7, 7, 0.0F);
		this.tail_1 = new ModelRenderer(this, 108, 26);
		this.tail_1.setRotationPoint(0.0F, 2.0F, 10.0F);
		this.tail_1.addBox(-1.5F, -1.8F, -1.5F, 3, 3, 4, 0.0F);
		ModelLycanthrope.setRotateAngle(this.tail_1, 0.03490658503988659F, 0.0F, 0.0F);
		this.snout = new ModelRenderer(this, 22, 0);
		this.snout.setRotationPoint(0.0F, -1.0F, 0.0F);
		this.snout.addBox(-1.5F, -3.0F, -8.5F, 3, 2, 4, 0.0F);
		this.body.addChild(this.furbodyb);
		this.earr_1.addChild(this.earr_2);
		this.armr2.addChild(this.armrclaw1);
		this.earl2.addChild(this.earl2_1);
		this.head.addChild(this.furfl);
		this.arml2.addChild(this.armlclaw3);
		this.snout.addChild(this.nose);
		this.arml.addChild(this.arml2);
		this.arml2.addChild(this.armlclaw1);
		this.legr.addChild(this.legr2);
		this.footr.addChild(this.footrclaw1);
		this.legl.addChild(this.footl);
		this.jaw.addChild(this.jawteeth);
		this.armr.addChild(this.furarmr);
		this.footl.addChild(this.footlclaw3);
		this.armr.addChild(this.armr2);
		this.body.addChild(this.mane);
		this.legr.addChild(this.footr);
		this.footr.addChild(this.footrclaw3);
		this.body.addChild(this.furbodyb2);
		this.legl.addChild(this.legl2);
		this.footl.addChild(this.footlclaw1);
		this.head.addChild(this.earr);
		this.legl.addChild(this.legl3);
		this.tail.addChild(this.tail2);
		this.head.addChild(this.jaw);
		this.footl.addChild(this.footlclaw2);
		this.legr.addChild(this.legr3);
		this.armr2.addChild(this.armrclaw2);
		this.head.addChild(this.earl);
		this.arml.addChild(this.furarml);
		this.earr.addChild(this.earr_1);
		this.head.addChild(this.furfr);
		this.head.addChild(this.furfb);
		this.footr.addChild(this.footrclaw2);
		this.head.addChild(this.furft);
		this.earl.addChild(this.earl2);
		this.arml2.addChild(this.armlclaw2);
		this.armr2.addChild(this.armrclaw3);
		this.snout.addChild(this.snoutteeth);
		this.tail.addChild(this.tail_1);
		this.head.addChild(this.snout);
	}
}
