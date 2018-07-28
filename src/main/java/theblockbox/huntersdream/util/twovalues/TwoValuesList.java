package theblockbox.huntersdream.util.twovalues;

import java.util.ArrayList;

public class TwoValuesList<E0, E1> extends ArrayList<TwoValues<E0, E1>> {
	private static final long serialVersionUID = 3120515013545416059L;

	public TwoValuesList() {
	}

	public void add(E0 firstValue, E1 secondValue) {
		this.add(new TwoValues<E0, E1>(firstValue, secondValue));
	}

	/** tests if any of the TwoValues objects contains the given value */
	public boolean has(Object object) {
		for (TwoValues<E0, E1> tv : this) {
			if (tv.FIRST_VALUE == object || tv.SECOND_VALUE == object) {
				return true;
			}
		}
		return false;
	}
}
