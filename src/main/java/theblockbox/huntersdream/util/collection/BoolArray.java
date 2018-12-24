package theblockbox.huntersdream.util.collection;

import java.util.Arrays;
import java.util.function.LongBinaryOperator;

/**
 * An array of booleans that is hopefully more efficient than traditional
 * boolean arrays
 */
public class BoolArray implements Cloneable {
	private long[] backingLongs;

	private BoolArray(long[] backingLongs) {
		this.backingLongs = backingLongs;
	}

	public static BoolArray of(int initialCapacity) {
		if (initialCapacity < 0)
			throw new NegativeArraySizeException(String.valueOf(initialCapacity));
		return of(new long[(initialCapacity / 64) + 1]);
	}

	public static BoolArray of(long[] backingLongs) {
		return new BoolArray(backingLongs);
	}

	public static BoolArray ofAllTrue(int initialCapacity) {
		if (initialCapacity < 0)
			throw new NegativeArraySizeException(String.valueOf(initialCapacity));
		long[] longs = new long[(initialCapacity / 64) + 1];
		Arrays.fill(longs, Long.MAX_VALUE);
		return of(longs);
	}

	public static BoolArray of(boolean[] booleans) {
		BoolArray array = of(booleans.length);
		for (int i = 0; i < booleans.length; i++)
			if (booleans[i])
				array.set(i);
		return array;
	}

	public static BoolArray reduce(BoolArray firstArray, BoolArray secondArray, LongBinaryOperator reducer) {
		long[] firstLongs = firstArray.getBackingLongs();
		long[] secondLongs = secondArray.getBackingLongs();
		int maxLength = Math.max(firstLongs.length, secondLongs.length);
		long[] newLongs = new long[maxLength];
		for (int i = 0; i < newLongs.length; i++) {
			long l1 = firstLongs.length > i ? firstLongs[i] : 0;
			long l2 = secondLongs.length > i ? secondLongs[i] : 0;
			newLongs[i] = reducer.applyAsLong(l1, l2);
		}
		return new BoolArray(newLongs);
	}

	public static BoolArray and(BoolArray firstArray, BoolArray secondArray) {
		return reduce(firstArray, secondArray, (l1, l2) -> l1 & l2);
	}

	public static BoolArray or(BoolArray firstArray, BoolArray secondArray) {
		return reduce(firstArray, secondArray, (l1, l2) -> l1 | l2);
	}

	public static BoolArray xor(BoolArray firstArray, BoolArray secondArray) {
		return reduce(firstArray, secondArray, (l1, l2) -> l1 ^ l2);
	}

	public boolean get(int index) throws ArrayIndexOutOfBoundsException {
		int arrayIndex = index / 64;
		int boolIndex = index % 64;
		try {
			return (this.backingLongs[arrayIndex] & (1L << boolIndex)) != 0;
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException(index);
		}
	}

	public boolean getAndWiden(int index) {
		int arrayIndex = index / 64;
		int boolIndex = index % 64;
		return (this.getAndWidenIfNeeded(arrayIndex) & (1L << boolIndex)) != 0;
	}

	public boolean set(int index, boolean value) {
		int arrayIndex = index / 64;
		int boolIndex = index % 64;
		boolean bool = (getAndWidenIfNeeded(arrayIndex) & (1L << boolIndex)) != 0;
		if (bool ^ value)
			this.backingLongs[arrayIndex] |= (1L << boolIndex);
		return bool;
	}

	public boolean set(int index) {
		return this.set(index, true);
	}

	/**
	 * Returns the next true index starting from the position (position inclusive)
	 * or -1 if no true index could be found
	 */
	public int getNextTrueIndex(int startIndex) {
		int length = this.length();
		for (int i = startIndex; i < length; i++)
			if (this.get(i))
				return i;
		return -1;
	}

	/**
	 * Returns the next false index starting from the position (position inclusive)
	 * or -1 if no false index could be found
	 */
	public int getNextFalseIndex(int startIndex) {
		int length = this.length();
		for (int i = startIndex; i < length; i++)
			if (!this.get(i))
				return i;
		return -1;
	}

	public long[] getBackingLongs() {
		return this.backingLongs;
	}

	public long[] getLongs() {
		return this.backingLongs.clone();
	}

	public int length() {
		return this.backingLongs.length * 64;
	}

	public int getTrueElements() {
		int elements = 0;
		for (int i = 0; i < this.backingLongs.length; i++)
			if (this.backingLongs[i] != 0)
				elements += Long.bitCount(this.backingLongs[i]);
		return elements;
	}

	public void clear() {
		for (int i = 0; i < this.backingLongs.length; i++) {
			this.backingLongs[i] = 0L;
		}
	}

	@Override
	public BoolArray clone() {
		return new BoolArray(this.getLongs());
	}

	private long getAndWidenIfNeeded(int index) {
		if (this.backingLongs.length <= index) {
			this.backingLongs = Arrays.copyOf(this.backingLongs, index + 1);
			return 0L;
		} else {
			return this.backingLongs[index];
		}
	}
}
