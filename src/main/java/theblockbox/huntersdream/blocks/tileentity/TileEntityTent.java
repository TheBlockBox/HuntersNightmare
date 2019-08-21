package theblockbox.huntersdream.blocks.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import theblockbox.huntersdream.blocks.BlockTent;

public class TileEntityTent extends TileEntity {
	
	@SideOnly(Side.CLIENT)
    public boolean isHeadPiece() {
        return BlockTent.isHeadPiece(this.getBlockMetadata());
    }
	
	@Override
	public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }
	
	@Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 11, this.getUpdateTag());
    }
	
}
