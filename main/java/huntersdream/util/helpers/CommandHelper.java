package huntersdream.util.helpers;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class CommandHelper {
	private static TextComponentTranslation commandInvalid = new TextComponentTranslation("commandinvalid",
			TextFormatting.RED);

	public static void invalidCommand(ICommandSender sender) {
		sender.sendMessage(commandInvalid);
	}
}
