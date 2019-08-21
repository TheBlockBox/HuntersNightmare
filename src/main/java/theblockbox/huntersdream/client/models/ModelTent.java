package theblockbox.huntersdream.client.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * ModelTent - NoxEternisZ
 * Created using Tabula 7.0.1
 */
@SideOnly(Side.CLIENT)
public class ModelTent extends ModelBase {
    public ModelRenderer fencepolefront;
    public ModelRenderer fencepoleback;
    public ModelRenderer sheetl;
    public ModelRenderer chest;
    public ModelRenderer bed;
    public ModelRenderer sheetr;
    public ModelRenderer peg1;
    public ModelRenderer peg2;
    public ModelRenderer peg3;
    public ModelRenderer peg4;

    public ModelTent() {
        this.textureWidth = 150;
        this.textureHeight = 128;
        this.peg4 = new ModelRenderer(this, 15, 0);
        this.peg4.setRotationPoint(-31.0F, 0.0F, 36.0F);
        this.peg4.addBox(-1.0F, -1.0F, -0.5F, 1, 3, 1, 0.0F);
        this.setRotateAngle(peg4, 0.0F, 0.0F, 0.7853981633974483F);
        this.chest = new ModelRenderer(this, 0, 34);
        this.chest.setRotationPoint(0.0F, 10.0F, 17.0F);
        this.chest.addBox(-7.0F, 0.0F, -7.0F, 14, 14, 14, 0.0F);
        this.sheetr = new ModelRenderer(this, 0, 62);
        this.sheetr.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.sheetr.addBox(-34.01F, 0.0F, -2.01F, 34, 1, 40, 0.0F);
        this.setRotateAngle(sheetr, 0.0F, 0.0F, -0.7853981633974483F);
        this.peg3 = new ModelRenderer(this, 15, 0);
        this.peg3.setRotationPoint(-31.0F, 0.0F, 0.0F);
        this.peg3.addBox(-1.0F, -1.0F, -0.5F, 1, 3, 1, 0.0F);
        this.setRotateAngle(peg3, 0.0F, 0.0F, 0.7853981633974483F);
        this.bed = new ModelRenderer(this, 0, 0);
        this.bed.setRotationPoint(0.0F, 23.0F, 10.0F);
        this.bed.addBox(-8.0F, 0.0F, -8.0F, 16, 1, 32, 0.0F);
        this.peg1 = new ModelRenderer(this, 15, 0);
        this.peg1.setRotationPoint(31.0F, 0.0F, 0.0F);
        this.peg1.addBox(0.0F, -1.0F, -0.5F, 1, 3, 1, 0.0F);
        this.setRotateAngle(peg1, 0.0F, 0.0F, -0.7853981633974483F);
        this.peg2 = new ModelRenderer(this, 15, 0);
        this.peg2.setRotationPoint(31.0F, 0.0F, 36.0F);
        this.peg2.addBox(0.0F, -1.0F, -0.5F, 1, 3, 1, 0.0F);
        this.setRotateAngle(peg2, 0.0F, 0.0F, -0.7853981633974483F);
        this.fencepolefront = new ModelRenderer(this, 0, 0);
        this.fencepolefront.setRotationPoint(0.0F, 0.0F, -10.0F);
        this.fencepolefront.addBox(-1.5F, 0.0F, -1.5F, 3, 24, 3, 0.0F);
        this.fencepoleback = new ModelRenderer(this, 0, 0);
        this.fencepoleback.setRotationPoint(0.0F, 0.0F, 36.0F);
        this.fencepoleback.addBox(-1.5F, 0.0F, -1.5F, 3, 24, 3, 0.0F);
        this.sheetl = new ModelRenderer(this, 0, 62);
        this.sheetl.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.sheetl.addBox(0.01F, 0.0F, -2.0F, 34, 1, 40, 0.0F);
        this.setRotateAngle(sheetl, 0.0F, 0.0F, 0.7853981633974483F);
        this.sheetr.addChild(this.peg4);
        this.fencepolefront.addChild(this.chest);
        this.fencepolefront.addChild(this.sheetr);
        this.sheetr.addChild(this.peg3);
        this.fencepolefront.addChild(this.bed);
        this.sheetl.addChild(this.peg1);
        this.sheetl.addChild(this.peg2);
        this.fencepolefront.addChild(this.fencepoleback);
        this.fencepolefront.addChild(this.sheetl);
    }
    
    public void render() {
        this.fencepolefront.render(0.0625F);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
