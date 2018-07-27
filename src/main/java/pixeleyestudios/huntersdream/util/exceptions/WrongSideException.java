package pixeleyestudios.huntersdream.util.exceptions;

import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

public class WrongSideException extends RuntimeException {
	private static final long serialVersionUID = 7281095896269746349L;

	public WrongSideException(String message, Side side) {
		super("Wrong side: " + side.toString() + "\n" + message);
	}

	public WrongSideException(String message, World world) {
		this(message, (world.isRemote ? Side.CLIENT : Side.SERVER));
	}
}
