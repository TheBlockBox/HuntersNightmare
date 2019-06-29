package theblockbox.huntersdream.util.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import theblockbox.huntersdream.blocks.tileentity.TileEntityCampfire;
import theblockbox.huntersdream.blocks.tileentity.TileEntitySpinningWheel;
import theblockbox.huntersdream.container.ContainerCampfire;
import theblockbox.huntersdream.container.ContainerSpinningWheel;
import theblockbox.huntersdream.gui.GuiCampfire;
import theblockbox.huntersdream.gui.GuiSpinningWheel;

public class GuiHandler implements IGuiHandler {
    public static final GuiHandler INSTANCE = new GuiHandler();

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        switch (id) {
            case 0:
                return new ContainerSpinningWheel(player.inventory,
                        (TileEntitySpinningWheel) world.getTileEntity(new BlockPos(x, y, z)));
            case 1:
                return new ContainerCampfire(player.inventory,
                        (TileEntityCampfire) world.getTileEntity(new BlockPos(x, y, z)));
            default:
                return null;
        }
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        switch (id) {
            case 0:
                return new GuiSpinningWheel((ContainerSpinningWheel) this.getServerGuiElement(id, player, world, x, y, z),
                        world.getBlockState(new BlockPos(x, y, z)).getBlock());
            case 1:
                return new GuiCampfire((ContainerCampfire) this.getServerGuiElement(id, player, world, x, y, z),
                        world.getBlockState(new BlockPos(x, y, z)).getBlock());
            default:
                return null;
        }
    }
}
