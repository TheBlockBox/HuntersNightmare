package theblockbox.huntersdream.util.interfaces.functional;

/**
 * A functional interface that has one method
 * {@link #applyAsFloat(Object, float)} that takes the arguments T and float and
 * returns a float
 */
@FunctionalInterface
public interface ToFloatObjFloatFunction<T> {
	public float applyAsFloat(T value, float f);
}
