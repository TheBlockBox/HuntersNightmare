package theblockbox.huntersdream.util;

import theblockbox.huntersdream.util.handlers.ConfigHandler;

/**
 * This is only for retrieving a line, so if a method requires an ExecutionPath
 * object, you have to make a new one. Don't throw this
 */
public class ExecutionPath extends Exception {
	private static final long serialVersionUID = 1L;

	public ExecutionPath() {
	}

	public String get(int index) {
		return ConfigHandler.common.showFullStackTrace ? getAll() : getFromIndex(index);
	}

	/**
	 * @param beginIndex inclusive
	 * @param endIndex   exclusive
	 */
	public String get(int beginIndex, int endIndex) {
		if (ConfigHandler.common.showFullStackTrace) {
			return getAll();
		} else {
			String toReturn = "";
			for (int i = beginIndex; i < endIndex; i++) {
				toReturn += "\n" + "	at " + get(i);
			}
			return toReturn;
		}
	}

	public String getAll() {
		String toReturn = "";
		for (int i = 0; i < getStackTrace().length; i++) {
			toReturn += "\n" + "	at " + getFromIndex(i);
		}
		return toReturn;
	}

	private String getFromIndex(int index) {
		StackTraceElement ste = getStackTrace()[index];
		if (ste.isNativeMethod()) {
			return "Native method: " + ste.getFileName() + ":" + ste.getClassName() + "." + ste.getMethodName() + ":"
					+ ste.getLineNumber();
		} else {
			return ste.getClassName() + "." + ste.getMethodName() + ":" + ste.getLineNumber();
		}
	}
}
