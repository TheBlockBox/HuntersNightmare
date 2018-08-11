package theblockbox.huntersdream.commands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import theblockbox.huntersdream.event.TransformationXPEvent.TransformationXPSentReason;
import theblockbox.huntersdream.util.helpers.CommandHelper;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

public class CommandsTransformationLevel extends CommandBase {

	@Override
	public String getName() {
		return "hdtransformationlevel";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "command.transformationXP.usage";
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
			toReturn.add("set");
			toReturn.add("add");
			toReturn.add("remove");
		} else if (args.length == 2) {
			try {
				toReturn.add(String.valueOf(Integer.parseInt(args[1]) + 1));
			} catch (NumberFormatException e) {
			}
		} else if (args.length == 3) {
			return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
		}
		return toReturn;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		try {
			EntityPlayerMP player;
			if (args.length >= 3) {
				player = (EntityPlayerMP) sender.getEntityWorld().getPlayerEntityByName(args[2]);
			} else {
				player = (EntityPlayerMP) sender;
			}

			ITransformationPlayer cap = TransformationHelper.getCap(player);
			Integer value = null;

			switch (args[0]) {
			case "set":
				value = Integer.parseInt(args[1]);
				break;

			case "add":
				value = Integer.parseInt(args[1]) + cap.getXP();
				break;

			case "remove":
				value = cap.getXP() - Integer.parseInt(args[1]);
				break;
			default:
				throw new IllegalArgumentException("Invalid argument index one");
			}

			if (value < 0) {
				throw new IllegalArgumentException("Minus xp");
			}

			TransformationHelper.setXP(player, value, TransformationXPSentReason.COMMAND);
			sender.sendMessage(new TextComponentTranslation("command.transformationXP.set", player.getName(), value));

		} catch (Exception e) {
			CommandHelper.invalidCommand(sender);
		}
	}

}
