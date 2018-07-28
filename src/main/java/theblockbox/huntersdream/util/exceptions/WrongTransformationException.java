package theblockbox.huntersdream.util.exceptions;

import theblockbox.huntersdream.util.helpers.TransformationHelper.Transformations;

public class WrongTransformationException extends RuntimeException {
	private static final long serialVersionUID = 1581449395217223441L;

	public WrongTransformationException(String message, Transformations transformation) {
		super("Wrong transformation: " + transformation.toString() + "\n" + message);
	}

	public WrongTransformationException(String message) {
		super("Wrong transformation: Not given" + "\n" + message);
	}
}
