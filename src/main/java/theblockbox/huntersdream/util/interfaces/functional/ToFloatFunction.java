package theblockbox.huntersdream.util.interfaces.functional;

@FunctionalInterface
public interface ToFloatFunction<T> {
	public float applyAsFloat(T value);
}
