package theblockbox.huntersdream.potions;

import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.List;

public class PotionFear extends PotionBase {

    public PotionFear() {
        super(true, 787878, 1, "fear");
    }

    // TODO: Add curative items
    @Override
    public List<ItemStack> getCurativeItems() {
        return Collections.emptyList();
    }
}
