package theblockbox.huntersdream.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import theblockbox.huntersdream.api.helpers.GeneralHelper;

public class SlotOutput extends SlotItemHandler {
    private final TileEntity te;

    public SlotOutput(IItemHandler itemHandler, int index, int xPosition, int yPosition, TileEntity te) {
        super(itemHandler, index, xPosition, yPosition);
        this.te = te;
    }

    @Override
    public ItemStack onTake(EntityPlayer player, ItemStack stack) {
        if (player != null) {
            GeneralHelper.spawnXP(this.te.getWorld(), this.te.getPos(), stack.getCount(),
                    FurnaceRecipes.instance().getSmeltingExperience(stack));
        }
        return super.onTake(player, stack);
    }
}
