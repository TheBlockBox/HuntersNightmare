package theblockbox.huntersdream.util.exceptions;

import net.minecraft.entity.EntityLivingBase;
import theblockbox.huntersdream.util.Transformation;
import theblockbox.huntersdream.util.helpers.TransformationHelper;

public class WrongTransformationException extends RuntimeException {
	private static final long serialVersionUID = 1581449395217223441L;

	public WrongTransformationException(String message, Transformation transformation) {
		super("Wrong transformation: " + transformation + "\n" + message);
	}

	public WrongTransformationException(String message) {
		super("Wrong transformation: Not given" + "\n" + message);
	}

	public static void ifNotTransformationThrow(EntityLivingBase entity, Transformation expectedTransformation) {
		Transformation transformation = TransformationHelper.getTransformation(entity);
		if (transformation != expectedTransformation) {
			throw new WrongTransformationException(
					String.format("The transformation of the entity \"%s\" should be %s but is %s", entity,
							expectedTransformation, transformation),
					transformation);
		}
	}

	public static void ifNotTransformationThrow(Transformation transformation, Transformation expectedTransformation) {
		if (transformation != expectedTransformation) {
			throw new WrongTransformationException(
					String.format("The transformation should be %s but is %s", expectedTransformation, transformation),
					transformation);
		}
	}
}
