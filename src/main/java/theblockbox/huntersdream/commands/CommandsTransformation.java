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
import theblockbox.huntersdream.event.TransformationEvent.TransformationEventReason;
import theblockbox.huntersdream.util.Transformation;
import theblockbox.huntersdream.util.helpers.CommandHelper;
import theblockbox.huntersdream.util.helpers.GeneralHelper;
import theblockbox.huntersdream.util.helpers.TransformationHelper;

public class CommandsTransformation extends CommandBase {

	@Override
	public String getName() {
		return "hdtransformation";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "command.huntersdream.transformation.usage";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
			BlockPos targetPos) {
		List<String> toReturn = new ArrayList<>();
		if (args.length == 1) {
			toReturn.addAll(Stream.of(Transformation.getAllTransformations()).map(Transformation::toString)
					.collect(Collectors.toList()));
			toReturn.add("get");
			return getListOfStringsMatchingLastWord(args, toReturn);
		} else {
			return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
		}
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
			if (args[0].equals("get")) {
				sender.sendMessage(new TextComponentTranslation("command.huntersdream.transformation.transformationGet",
						player.getName(), TransformationHelper.getTransformation(player).toString()));
			} else {
				String transformation = args[0];
				Transformation t = Transformation
						.fromNameWithoutError(GeneralHelper.newResLoc(transformation).toString());
				TransformationHelper.changeTransformation(player, t, TransformationEventReason.COMMAND);
				sender.sendMessage(new TextComponentTranslation("command.huntersdream.transformation.transformationSet",
						player.getName(), t.toString()));
			}
		} catch (Exception e) {
			CommandHelper.invalidCommand(sender);
		}
	}

}
