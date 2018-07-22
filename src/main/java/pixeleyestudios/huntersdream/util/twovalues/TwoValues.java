package pixeleyestudios.huntersdream.util.twovalues;

/**
 * Simple class for saving two values
 */
public class TwoValues<E0, E1> {
	public final E0 FIRST_VALUE;
	public final E1 SECOND_VALUE;

	public TwoValues(E0 firstValue, E1 secondValue) {
		this.FIRST_VALUE = firstValue;
		this.SECOND_VALUE = secondValue;
	}
}
