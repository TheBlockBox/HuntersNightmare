package theblockbox.huntersdream.util.collection;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.google.common.collect.Iterators;

import theblockbox.huntersdream.api.Transformation;

/**
 * A set for storing Transformation instances via storing their temporary id
 * obtained from {@link Transformation#getTemporaryID()} in a BitSet. Does not
 * allow null values
 */
public class TransformationSet extends AbstractSet<Transformation> implements Cloneable {
	protected final BoolArray delegate;

	public TransformationSet() {
		this(BoolArray.of(Transformation.getRegisteredTransformations()));
	}

	public TransformationSet(Collection<Transformation> collection) {
		this(collection.stream().mapToInt(Transformation::getTemporaryID).collect(
				() -> BoolArray.of(Transformation.getRegisteredTransformations()), BoolArray::set, BoolArray::or));
	}

	public TransformationSet(BoolArray delegate) {
		this.delegate = delegate;
	}

	public TransformationSet(Transformation... transformations) {
		this(Stream.of(transformations).mapToInt(Transformation::getTemporaryID).collect(
				() -> BoolArray.of(Transformation.getRegisteredTransformations()), BoolArray::set, BoolArray::or));
	}

	public static TransformationSet singletonSet(Transformation transformation) {
		return new SingletonTransformationSet(transformation);
	}

	@Override
	public boolean add(@Nonnull Transformation transformation) {
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

	public boolean remove(@Nonnull Transformation transformation) {
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

	public boolean contains(@Nonnull Transformation transformation) {
		return this.delegate.get(transformation.getTemporaryID());
	}

	@Override
	public void clear() {
		this.delegate.clear();
	}

	@Override
	public Iterator<Transformation> iterator() {
		return new Iterator<Transformation>() {
			private int current = -1;
			private int next = TransformationSet.this.delegate.getNextTrueIndex(0);

			@Override
			public boolean hasNext() {
				return this.next != -1;
			}

			@Override
			public Transformation next() {
				this.current = this.next;
				this.next = TransformationSet.this.delegate.getNextTrueIndex(this.current);
				if (this.current == -1) {
					throw new NoSuchElementException();
				} else {
					return Transformation.fromTemporaryID(this.next);
				}
			}

			@Override
			public void remove() {
				if (!TransformationSet.this.remove(Transformation.fromTemporaryID(this.current)))
					throw new IllegalStateException();
			}
		};
	}

	@Override
	public int size() {
		return this.delegate.getTrueElements();
	}

	@Override
	public Transformation[] toArray() {
		return this.stream().toArray(Transformation[]::new).clone();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] array) {
		if (array instanceof Transformation[]) {
			return (T[]) this.toArray();
		}
		return super.toArray(array);
	}

	@Override
	public TransformationSet clone() {
		return new TransformationSet(this.delegate.clone());
	}

	@Immutable
	private static class SingletonTransformationSet extends TransformationSet {
		private final Transformation transformation;

		public SingletonTransformationSet(Transformation transformation) {
			super((BoolArray) null);
			this.transformation = transformation;
		}

		@Override
		public boolean add(Transformation t) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Transformation t) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int size() {
			return 1;
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean contains(Transformation t) {
			return this.transformation == t;
		}

		@Override
		public Iterator<Transformation> iterator() {
			return Iterators.singletonIterator(this.transformation);
		}

		@Override
		public Stream<Transformation> stream() {
			return Stream.of(this.transformation);
		}

		@Override
		public Transformation[] toArray() {
			return new Transformation[] { this.transformation };
		}

		@Override
		public TransformationSet clone() {
			return new SingletonTransformationSet(this.transformation);
		}
	}
}
