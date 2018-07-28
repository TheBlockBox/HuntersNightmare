package theblockbox.huntersdream.util.helpers;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class CommandHelper {
	public static final TextComponentTranslation COMMAND_INVALID = new TextComponentTranslation("command.invalid",
			TextFormatting.RED);

	static {
		COMMAND_INVALID.getStyle().setColor(TextFormatting.RED);
	}

	public static void invalidCommand(ICommandSender sender) {
		sender.sendMessage(COMMAND_INVALID);
	}
}
