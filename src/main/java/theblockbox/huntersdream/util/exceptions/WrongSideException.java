package theblockbox.huntersdream.util.exceptions;

import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import theblockbox.huntersdream.api.helpers.GeneralHelper;

public class WrongSideException extends RuntimeException {
	public WrongSideException(String message, Side side) {
		super("Wrong side: " + side + "\n" + message);
	}

	public WrongSideException(String message, World world) {
		this(message, GeneralHelper.getSideFromWorld(world));
	}
}
