package theblockbox.huntersdream.util.exceptions;

import theblockbox.huntersdream.util.Transformation;

public class WrongTransformationException extends RuntimeException {
	private static final long serialVersionUID = 1581449395217223441L;

	public WrongTransformationException(String message, Transformation transformation) {
		super("Wrong transformation: " + transformation + "\n" + message);
	}

	public WrongTransformationException(String message) {
		super("Wrong transformation: Not given" + "\n" + message);
	}
}
