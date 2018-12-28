package theblockbox.huntersdream.util.interfaces.functional;

import java.util.function.BiFunction;
import java.util.function.ToDoubleBiFunction;

/**
 * A functional interface that has one method
 * {@link #applyAsFloat(Object, float)} that takes the arguments T and float and
 * returns a float
 */
@FunctionalInterface
public interface ToFloatObjFloatFunction<T> extends BiFunction<T, Float, Float>, ToDoubleBiFunction<T, Float> {
	public float applyAsFloat(T value, float f);

	@Override
	default Float apply(T t, Float f) {
		return this.applyAsFloat(t, f);
	}

	@Override
	default double applyAsDouble(T t, Float f) {
		return this.applyAsFloat(t, f);
	}
}
