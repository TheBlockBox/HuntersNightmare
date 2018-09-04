package theblockbox.huntersdream.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import theblockbox.huntersdream.util.enums.Rituals;
import theblockbox.huntersdream.util.handlers.PacketHandler;
import theblockbox.huntersdream.util.helpers.CommandHelper;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

public class CommandsRitual extends CommandBase {

	@Override
	public String getName() {
		return "hdritual";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "command.huntersdream.ritual.usage";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
			BlockPos targetPos) {
		ArrayList<String> toReturn = new ArrayList<>();
		if (args.length == 1) {
			toReturn.add("add");
			toReturn.add("remove");
			toReturn.add("clear");
		} else if (args.length == 2) {
			return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
		} else if (args.length == 3) {
			return getListOfStringsMatchingLastWord(args,
					Stream.of(Rituals.values()).map(Rituals::toString).collect(Collectors.toList()));
		}
		return toReturn;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		try {
			EntityPlayerMP player;
			if (args.length >= 2) {
				player = (EntityPlayerMP) sender.getEntityWorld().getPlayerEntityByName(args[1]);
			} else {
				player = (EntityPlayerMP) sender;
			}

			ITransformationPlayer cap = TransformationHelper.getCap(player);

			Rituals ritual = (!args[0].equals("clear")) ? Rituals.fromNameWithException(args[2]) : null;

			switch (args[0]) {
			case "clear":
				cap.setRituals(new Rituals[0]);
				sender.sendMessage(new TextComponentTranslation("command.huntersdream.ritual.clear", player.getName()));
				break;
			case "add":
				if (!cap.hasRitual(ritual)) {
					cap.addRitual(ritual);
					sender.sendMessage(
							new TextComponentTranslation("command.huntersdream.ritual.add", ritual, player.getName()));
				}
				break;
			case "remove":
				if (cap.hasRitual(ritual)) {
					cap.removeRitual(ritual);
					sender.sendMessage(new TextComponentTranslation("command.huntersdream.ritual.remove", ritual,
							player.getName()));
				}
				break;
			default:
				throw new IllegalArgumentException("Invalid argument index one");
			}
			PacketHandler.sendTransformationMessage(player);
		} catch (Exception e) {
			CommandHelper.invalidCommand(sender);
		}
	}
}
