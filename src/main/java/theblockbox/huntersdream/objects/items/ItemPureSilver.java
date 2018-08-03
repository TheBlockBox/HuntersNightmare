package theblockbox.huntersdream.objects.items;

import theblockbox.huntersdream.init.CreativeTabInit;
import theblockbox.huntersdream.util.interfaces.effective.ISilverEffectiveAgainstTransformation;

public class ItemPureSilver extends ItemBase implements ISilverEffectiveAgainstTransformation {

	public ItemPureSilver(String name) {
		super(name, CreativeTabInit.HUNTERSDREAM_MISC);
	}

}
