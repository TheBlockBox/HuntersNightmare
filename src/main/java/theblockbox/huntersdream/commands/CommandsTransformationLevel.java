package theblockbox.huntersdream.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import theblockbox.huntersdream.util.ExecutionPath;
import theblockbox.huntersdream.util.handlers.PacketHandler.Packets;
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
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		try {
			EntityPlayer player;
			if (args.length >= 3) {
				player = sender.getEntityWorld().getPlayerEntityByName(args[2]);
			} else {
				player = (EntityPlayer) sender;
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

			cap.setXP(value);
			sender.sendMessage(new TextComponentTranslation("command.transformationXP.set", player.getName(), value));
			Packets.TRANSFORMATION.sync(new ExecutionPath(), player);
		} catch (Exception e) {
			CommandHelper.invalidCommand(sender);
		}
	}

}
