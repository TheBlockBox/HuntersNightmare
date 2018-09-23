package theblockbox.huntersdream.util;

import java.util.AbstractSet;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * A set for storing Transformation instances via storing their temporary id
 * obtained from {@link Transformation#getTemporaryID()} in a BitSet
 */
public class TransformationSet extends AbstractSet<Transformation> implements Cloneable {
	private final BitSet delegate;

	public TransformationSet() {
		this(new BitSet(Transformation.getTransformationLength()));
	}

	public TransformationSet(Collection<Transformation> collection) {
		this(collection.stream().mapToInt(Transformation::getTemporaryID)
				.collect(() -> new BitSet(Transformation.getTransformationLength()), BitSet::set, BitSet::or));
	}

	public TransformationSet(BitSet delegate) {
		this.delegate = delegate;
	}

	public TransformationSet(Transformation... transformations) {
		this(Stream.of(transformations).mapToInt(Transformation::getTemporaryID)
				.collect(() -> new BitSet(Transformation.getTransformationLength()), BitSet::set, BitSet::or));
	}

	@Override
	public boolean add(Transformation transformation) {
		int id = transformation.getTemporaryID();
		if (!this.delegate.get(id)) {
			this.delegate.set(id);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean remove(Object o) {
		if (o instanceof Transformation) {
			return remove((Transformation) o);
		} else {
			return false;
		}
	}

	public boolean remove(Transformation transformation) {
		int id = transformation.getTemporaryID();
		if (this.delegate.get(id)) {
			this.delegate.set(id, false);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean contains(Object o) {
		if (o instanceof Transformation) {
			return this.contains((Transformation) o);
		} else {
			return false;
		}
	}

	public boolean contains(Transformation transformation) {
		return this.delegate.get(transformation.getTemporaryID());
	}

	@Override
	public void clear() {
		this.delegate.clear();
	}

	@Override
	public Iterator<Transformation> iterator() {
		return this.stream().iterator();
	}

	@Override
	public int size() {
		return this.delegate.cardinality();
	}

	@Override
	public Transformation[] toArray() {
		return this.stream().toArray(Transformation[]::new).clone();
	}

	@Override
	public Stream<Transformation> stream() {
		return this.delegate.stream().mapToObj(Transformation::fromTemporaryID);
	}

	@Override
	public TransformationSet clone() {
		return new TransformationSet((BitSet) this.delegate.clone());
	}
}
