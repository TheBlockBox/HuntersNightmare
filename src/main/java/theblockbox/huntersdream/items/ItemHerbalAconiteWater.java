package theblockbox.huntersdream.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import theblockbox.huntersdream.init.CreativeTabInit;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;

public class ItemHerbalAconiteWater extends ItemFood {
    public ItemHerbalAconiteWater() {
        super(0, 0.0F, false);
        this.setAlwaysEdible();
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabInit.HUNTERSDREAM_MISC);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        ItemStack returnedStack = super.onItemUseFinish(stack, worldIn, entityLiving);
        return returnedStack.isEmpty() ? new ItemStack(Items.GLASS_BOTTLE) : returnedStack;
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
        super.onFoodEaten(stack, worldIn, player);
        if (!worldIn.isRemote) {
            WerewolfHelper.cureLycanthropy(player, false);
        }
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.DRINK;
    }
}
