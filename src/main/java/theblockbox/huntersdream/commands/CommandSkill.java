package theblockbox.huntersdream.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import theblockbox.huntersdream.api.Skill;
import theblockbox.huntersdream.util.handlers.PacketHandler;
import theblockbox.huntersdream.util.helpers.CommandHelper;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

public class CommandSkill extends CommandBase {
	public static final Joiner SKILL_JOINER = Joiner.on(", ").skipNulls();
	private static final Collection<String> OPERATIONS = Arrays
			.asList("add", "remove", "clear", "list");

	@Override
	public String getName() {
		return "hdskill";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "command.huntersdream.skill.usage";
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
			return getListOfStringsMatchingLastWord(args, OPERATIONS);
		} else if (args.length == 2) {
			return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
		} else if (args.length == 3) {
			return getListOfStringsMatchingLastWord(args, Skill.getAllSkills());
		}
		return toReturn;
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

			ITransformationPlayer cap = TransformationHelper.getITransformationPlayer(player);

			Skill skill = (args[0].equals("clear") || args[0].equals("list")) ? null
					: Preconditions.checkNotNull(Skill.fromName(args[2]));
			switch (args[0]) {
			case "clear":
				cap.setSkills(Collections.emptySet());
				sender.sendMessage(new TextComponentTranslation("command.huntersdream.skill.clear", player.getName()));
				break;
			case "list":
				sender.sendMessage(new TextComponentTranslation("command.huntersdream.skill.list", player.getName(),
						SKILL_JOINER.join(cap.getSkills())));
				break;
			case "add":
				if (!cap.hasSkill(skill)) {
					cap.addSkill(skill);
					sender.sendMessage(
							new TextComponentTranslation("command.huntersdream.skill.add", skill, player.getName()));
				}
				break;
			case "remove":

				if (cap.hasSkill(skill)) {
					cap.removeSkill(skill);
					sender.sendMessage(
							new TextComponentTranslation("command.huntersdream.skill.remove", skill, player.getName()));
				}
				break;
			default:
				throw new IllegalArgumentException("Invalid argument index one");
			}
			PacketHandler.sendTransformationMessage(player);
		} catch (Exception e) {
			CommandHelper.invalidCommand(sender, e);
		}
	}
}
