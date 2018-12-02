package theblockbox.huntersdream.util.effective_against_transformation;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import theblockbox.huntersdream.util.collection.TransformationToFloatMap;

public class ItemEffectiveAgainstTransformation extends EffectiveAgainstTransformation<ItemStack> {
	private static final Set<ItemEffectiveAgainstTransformation> OBJECTS = new HashSet<>();

	public ItemEffectiveAgainstTransformation(Predicate<ItemStack> isForObject,
			TransformationToFloatMap effectivenessMap) {
		super(isForObject, effectivenessMap);
	}

	@Nullable
	public static ItemEffectiveAgainstTransformation getFromItemStack(ItemStack stack) {
		for (ItemEffectiveAgainstTransformation ieat : OBJECTS)
			if (ieat.isForObject(stack))
				return ieat;
		return null;
	}

	public static Set<ItemEffectiveAgainstTransformation> getObjects() {
		return Collections.unmodifiableSet(OBJECTS);
	}

	@Override
	public boolean register() {
		return OBJECTS.add(this);
	}
}
