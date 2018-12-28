package theblockbox.huntersdream.util.exceptions;

import theblockbox.huntersdream.api.Transformation;

public class WrongTransformationException extends RuntimeException {

	public WrongTransformationException(String message, Transformation transformation) {
		super("Wrong transformation: " + transformation + "\n" + message);
	}

	public WrongTransformationException(String message) {
		super("Wrong transformation: Not given" + "\n" + message);
	}

	public static void ifNotTransformationThrow(Transformation transformation, Transformation expectedTransformation) {
		if (transformation != expectedTransformation) {
			throw new WrongTransformationException(
					String.format("The transformation should be %s but is %s", expectedTransformation, transformation),
					transformation);
		}
	}
}
