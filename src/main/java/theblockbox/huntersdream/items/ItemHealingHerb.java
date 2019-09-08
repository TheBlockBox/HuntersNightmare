package theblockbox.huntersdream.items;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemSeedFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import theblockbox.huntersdream.api.init.BlockInit;

import javax.annotation.Nullable;
import java.util.List;

public class ItemHealingHerb extends ItemSeedFood {
    public ItemHealingHerb() {
        super(2, 0.6F, Blocks.AIR, Blocks.FARMLAND);
        this.setAlwaysEdible();
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        EnumActionResult result = super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
        if (result == EnumActionResult.SUCCESS) {
            worldIn.setBlockState(pos.up(), this.getPlant(worldIn, pos), 11);
        }
        return result;
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
        if (!worldIn.isRemote) {
            player.heal(4.0F);
        }
    }

    @Override
    public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
        return BlockInit.HEALING_HERB.getDefaultState();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format(this.getTranslationKey() + ".tooltip"));
    }
}
