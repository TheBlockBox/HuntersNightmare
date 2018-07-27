package pixeleyestudios.huntersdream.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import pixeleyestudios.huntersdream.util.ExecutionPath;
import pixeleyestudios.huntersdream.util.handlers.PacketHandler.Packets;
import pixeleyestudios.huntersdream.util.helpers.CommandHelper;
import pixeleyestudios.huntersdream.util.helpers.TransformationHelper;
import pixeleyestudios.huntersdream.util.helpers.TransformationHelper.Transformations;

public class CommandsTransformation extends CommandBase {

	@Override
	public String getName() {
		return "hdtransformation";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "command.transformation.usage";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		try {
			EntityPlayerMP player = null;
			if (args.length >= 2) {
				player = (EntityPlayerMP) sender.getEntityWorld().getPlayerEntityByName(args[1]);
			} else {
				player = (EntityPlayerMP) sender;
			}
			int transformation = args[0].equals("get") ? TransformationHelper.getCap(player).getTransformationInt()
					: Integer.parseInt(args[0]);
			TransformationHelper.changeTransformation(player, Transformations.fromID(transformation));
			// sender.sendMessage(new TextComponentString(
			// player.getName() + " is now a " +
			// Transformations.fromID(transformation).toString()));
			sender.sendMessage(new TextComponentTranslation("command.transformation.transformationSet",
					player.getName(), Transformations.fromID(transformation).toString()));
			Packets.TRANSFORMATION.sync(new ExecutionPath(), player);
		} catch (Exception e) {
			CommandHelper.invalidCommand(sender);
		}
	}

}
