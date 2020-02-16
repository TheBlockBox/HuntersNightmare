package theblockbox.huntersdream.api.helpers;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class CommandHelper {
    public static final TextComponentTranslation COMMAND_INVALID = new TextComponentTranslation(
            "command.huntersdream.invalid");

    static {
        CommandHelper.COMMAND_INVALID.getStyle().setColor(TextFormatting.RED);
    }

    /**
     * Sends the given command sender a message that the executed command had
     * invalid parameters
     */
    public static void invalidCommand(ICommandSender sender, Exception e) {
        sender.sendMessage(CommandHelper.COMMAND_INVALID);
    }
}