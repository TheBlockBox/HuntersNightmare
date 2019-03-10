package theblockbox.huntersdream.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.api.event.TransformationEvent;
import theblockbox.huntersdream.api.helpers.CommandHelper;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.api.helpers.TransformationHelper;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandTransformation extends CommandBase {

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
		if (args.length == 1) {
			List<String> toReturn = Stream.of(Transformation.getAllTransformations()).map(Transformation::toString).collect(Collectors.toList());
			toReturn.add("get");
			return CommandBase.getListOfStringsMatchingLastWord(args, toReturn);
		} else {
			return CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
		}
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
		try {
			EntityPlayerMP player;
			if (args.length >= 2) {
				player = (EntityPlayerMP) sender.getEntityWorld().getPlayerEntityByName(args[1]);
			} else {
				player = (EntityPlayerMP) sender;
			}
			if ("get".equals(args[0])) {
				sender.sendMessage(new TextComponentTranslation("command.huntersdream.transformation.transformationGet",
						player.getName(), TransformationHelper.getTransformation(player).toString()));
			} else {
				String transformation = args[0];
				Transformation t = Transformation
						.fromNameWithoutError(GeneralHelper.newResLoc(transformation).toString());
				TransformationHelper.changeTransformation(player, t, TransformationEvent.TransformationEventReason.COMMAND);
				sender.sendMessage(new TextComponentTranslation("command.huntersdream.transformation.transformationSet",
						player.getName(), t.toString()));
			}
		} catch (Exception e) {
			CommandHelper.invalidCommand(sender, e);
		}
	}

}
