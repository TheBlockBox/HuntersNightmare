package theblockbox.huntersdream.util;

/**
 * This is only for retrieving a line, so if a method requires an ExecutionPath
 * object, you have to make a new one. Don't throw this
 */
public class ExecutionPath extends Exception {
	private static final long serialVersionUID = 1L;

	public ExecutionPath() {
	}

	public String get() {
		StackTraceElement ste = getStackTrace()[0];
		return ste.getClassName() + "." + ste.getMethodName() + ":" + ste.getLineNumber();
	}
}
