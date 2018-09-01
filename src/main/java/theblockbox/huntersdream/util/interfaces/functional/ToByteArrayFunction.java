package theblockbox.huntersdream.util.interfaces.functional;

import java.util.function.Function;

@FunctionalInterface
public interface ToByteArrayFunction<T> extends Function<T, byte[]> {
}
