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
		return get(0);
	}

	public String get(int index) {
		StackTraceElement ste = getStackTrace()[index];
		if (ste.isNativeMethod()) {
			return "Native method: " + ste.getFileName() + ":" + ste.getClassName() + "." + ste.getMethodName() + ":"
					+ ste.getLineNumber();
		} else {
			return ste.getClassName() + "." + ste.getMethodName() + ":" + ste.getLineNumber();
		}
	}

	public String getAll() {
		String toReturn = "";
		for (int i = 0; i < getStackTrace().length; i++) {
			toReturn += "\n" + "	at " + get(i);
		}
		return toReturn;
	}
}
