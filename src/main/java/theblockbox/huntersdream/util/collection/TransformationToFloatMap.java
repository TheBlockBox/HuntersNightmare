package theblockbox.huntersdream.util.collection;

import java.util.stream.Stream;

import theblockbox.huntersdream.util.Transformation;

public class TransformationToFloatMap implements Cloneable {
	private final float[] floats;

	public TransformationToFloatMap() {
		this(new float[Transformation.getRegisteredTransformations()]);
	}

	private TransformationToFloatMap(float[] backingFloats) {
		this.floats = backingFloats;
	}

	public float get(Transformation key) {
		int id = key.getTemporaryID();
		return (id >= this.floats.length) ? 0F : this.floats[id];
	}

	public TransformationToFloatMap put(Transformation key, float value) {
		this.floats[key.getTemporaryID()] = value;
		return this;
	}

	public boolean hasKey(Transformation key) {
		return this.get(key) != 0F;
	}

	@Override
	public TransformationToFloatMap clone() {
		return new TransformationToFloatMap(this.floats.clone());
	}

	public Transformation[] toArray() {
		return this.transformationStream().toArray(Transformation[]::new);
	}

	public Stream<Transformation> transformationStream() {
		return Stream.of(Transformation.getAllTransformations()).filter(this::hasKey);
	}
}
