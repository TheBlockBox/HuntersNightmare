package theblockbox.huntersdream.util.collection;

import java.util.stream.Stream;

import theblockbox.huntersdream.util.Transformation;

public class TransformationToFloatFloatMap implements Cloneable {
	private final float[] floats1;
	private final float[] floats2;

	public TransformationToFloatFloatMap() {
		this(new float[Transformation.getRegisteredTransformations()],
				new float[Transformation.getRegisteredTransformations()]);
	}

	private TransformationToFloatFloatMap(float[] backingFloats1, float[] backingFloats2) {
		this.floats1 = backingFloats1;
		this.floats2 = backingFloats2;
	}

	public float getFloat1(Transformation key) {
		int id = key.getTemporaryID();
		return (id >= this.floats1.length) ? 0F : this.floats1[id];
	}

	public float getFloat2(Transformation key) {
		int id = key.getTemporaryID();
		return (id >= this.floats2.length) ? 0F : this.floats2[id];
	}

	public TransformationToFloatFloatMap put(Transformation key, float value1, float value2) {
		int id = key.getTemporaryID();
		this.floats1[id] = value1;
		this.floats2[id] = value2;
		return this;
	}

	public boolean hasKey(Transformation key) {
		return (this.getFloat1(key) != 0F) && (this.getFloat2(key) != 0F);
	}

	@Override
	public TransformationToFloatFloatMap clone() {
		return new TransformationToFloatFloatMap(this.floats1.clone(), this.floats2.clone());
	}

	public Transformation[] toArray() {
		return this.transformationStream().toArray(Transformation[]::new);
	}

	public Stream<Transformation> transformationStream() {
		return Stream.of(Transformation.getAllTransformations()).filter(this::hasKey);
	}
}
