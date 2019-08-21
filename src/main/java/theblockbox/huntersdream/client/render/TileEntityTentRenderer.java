package theblockbox.huntersdream.client.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import theblockbox.huntersdream.blocks.tileentity.TileEntityTent;
import theblockbox.huntersdream.client.models.ModelTent;
import theblockbox.huntersdream.util.Reference;

@SideOnly(Side.CLIENT)
public class TileEntityTentRenderer extends TileEntitySpecialRenderer<TileEntityTent> {
	
	private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MODID, "textures/blocks/tent.png");
	private ModelTent model = new ModelTent();
	
	@Override
	public void render(TileEntityTent te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {

        boolean flag = te.getWorld() != null;
        int facing = flag ? te.getBlockMetadata() & 3 : 0;

        if (destroyStage >= 0) {
            this.bindTexture(DESTROY_STAGES[destroyStage]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scale(4.0F, 4.0F, 1.0F);
            GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.matrixMode(5888);
        } else {
        	this.bindTexture(TEXTURE);
        }
        
        if (te.isHeadPiece()) {
        	this.renderTent(x, y, z, facing, alpha);
        }
        
        if (destroyStage >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
    }
	
	private void renderTent(double x, double y, double z, int facing, float alpha) {
        GlStateManager.pushMatrix();
        float f = 0.0F;
        float f1 = 0.0F;
        float f2 = 0.0F;

        if (facing == EnumFacing.NORTH.getHorizontalIndex()) {
            f = 0.0F;
            f1 = 0.5F;
            f2 = 1.5F;
        } else if (facing == EnumFacing.SOUTH.getHorizontalIndex()) {
            f = 180.0F;
            f1 = 0.5F;
            f2 = -0.5F;
        } else if (facing == EnumFacing.WEST.getHorizontalIndex()) {
            f = -90.0F;
            f1 = 0.5F;
            f2 = 0.5F;
        } else if (facing == EnumFacing.EAST.getHorizontalIndex()) {
            f = 90.0F;
            f1 = 0.5F;
            f2 = 0.5F;
        }

        GlStateManager.translate((float)x + f1, (float)y + 1.5625F, (float)z + f2);
        //GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(f, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(180.0f, 1.0F, 0.0F, 0.0F);
        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();
        this.model.render();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
        GlStateManager.popMatrix();
    }
	
}
