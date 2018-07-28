package theblockbox.huntersdream.potions;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class PotionFear extends PotionBase {
	/** If performEffect has been called yet */
	private boolean performedEffectedOneTime = false;
	private BlockPos pos;

	public PotionFear() {
		super(true, 787878, "fear");
	}

	// TODO: Test if this even works
	@Override
	public void performEffect(EntityLivingBase entity, int amplifier) {
		if (!performedEffectedOneTime) {
			performedEffectedOneTime = true;
			pos = new BlockPos(entity);
			System.out.println("Effect performed for first time");
			return;
		} else {
			entity.setPosition(pos.getX(), pos.getY(), pos.getZ());
		}
	}

	// TODO: Add curative items
	@Override
	public List<ItemStack> getCurativeItems() {
		ArrayList<ItemStack> items = new ArrayList<>();

		return items;
	}

}
