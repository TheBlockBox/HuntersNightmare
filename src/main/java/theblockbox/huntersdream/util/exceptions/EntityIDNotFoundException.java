package theblockbox.huntersdream.util.exceptions;

public class EntityIDNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 7281095896269746349L;

	public EntityIDNotFoundException(String message, int id, int worldID) {
		super("Couldn't find entity with id " + id + " in world " + worldID + "\n" + message);
	}

	public EntityIDNotFoundException(String message, int id) {
		super("Couldn't find entity with id " + id + "\n" + message);
	}
}
