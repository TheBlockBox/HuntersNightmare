package theblockbox.huntersdream.util;

import theblockbox.huntersdream.util.handlers.ConfigHandler;

/**
 * This is only for retrieving a line, so if a method requires an ExecutionPath
 * object, you have to make a new one. Don't throw this
 */
public class ExecutionPath extends Exception {
	private ExecutionPath() {
	}

	public static String get(int index) {
		return ConfigHandler.common.showFullStackTrace ? getAll() : (new ExecutionPath()).getFromIndex(index);
	}

	/**
	 * @param beginIndex inclusive
	 * @param endIndex   exclusive
	 */
	public static String get(int beginIndex, int endIndex) {
		if (ConfigHandler.common.showFullStackTrace) {
			return getAll();
		} else {
			ExecutionPath path = new ExecutionPath();
			StringBuilder toReturn = new StringBuilder();
			for (int i = beginIndex; i < endIndex; i++) {
				try {
					toReturn.append("\n\tat ").append(path.getFromIndex(i));
				} catch (ArrayIndexOutOfBoundsException e) {
					break;
				}
			}
			return toReturn.toString();
		}
	}

	public static String getAll() {
		ExecutionPath path = new ExecutionPath();
		StringBuilder toReturn = new StringBuilder();
		String last = "";
		int repetitions = 0;
		for (int i = 0; i < path.getStackTrace().length; i++) {
			String str = "\n\tat " + path.getFromIndex(i);
			if (!str.equals(last)) {
				if (repetitions > 0) {
					toReturn.append("\n\t(times ").append(repetitions).append(")");
					repetitions = 0;
				}
				toReturn.append(str);
			} else {
				repetitions++;
			}
			last = str;
		}
		if (repetitions > 0) {
			toReturn.append("\n\t(times ").append(repetitions).append(")");
		}
		return toReturn.toString();
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
