package theblockbox.huntersdream.commands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import theblockbox.huntersdream.util.ExecutionPath;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.enums.Transformations;
import theblockbox.huntersdream.util.helpers.CommandHelper;
import theblockbox.huntersdream.util.helpers.TransformationHelper;

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
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
			BlockPos targetPos) {
		ArrayList<String> toReturn = new ArrayList<>();
		if (args.length == 1) {
			for (Transformations transformation : Transformations.values()) {
				toReturn.add(transformation.toString());
			}
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
				sender.sendMessage(new TextComponentTranslation("command.transformation.transformationGet",
						player.getName(), TransformationHelper.getTransformation(player).toString()));
			} else {
				String transformation = args[0];
				ResourceLocation name;
				if (transformation.contains(":")) {
					name = new ResourceLocation(transformation);
				} else {
					name = new ResourceLocation(Reference.MODID, transformation);
				}
				Transformations transformations = Transformations.fromResourceLocation(name);
				TransformationHelper.changeTransformation(player, transformations, new ExecutionPath());
				sender.sendMessage(new TextComponentTranslation("command.transformation.transformationSet",
						player.getName(), Transformations.fromResourceLocation(name).toString()));
			}
		} catch (Exception e) {
			CommandHelper.invalidCommand(sender);
		}
	}

}
