package theblockbox.huntersdream.commands;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
		List<String> toReturn = new ArrayList<>();
		if (args.length == 1) {
			return CommandBase.getListOfStringsMatchingLastWord(args, CommandSkill.OPERATIONS);
		} else if (args.length == 2) {
			return CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
		} else if (args.length == 3) {
			return CommandBase.getListOfStringsMatchingLastWord(args, Skill.getAllSkills());
		}
		return toReturn;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
		try {
			EntityPlayerMP player = (EntityPlayerMP)
					(args.length >= 2 ? sender.getEntityWorld().getPlayerEntityByName(args[1]) : sender);
			ITransformationPlayer cap = TransformationHelper.getITransformationPlayer(player);

			Skill skill = ("clear".equals(args[0]) || "list".equals(args[0])) ? null
					: Preconditions.checkNotNull(Skill.fromName(args[2]));
			switch (args[0]) {
			case "clear":
				cap.removeAllSkills();
				sender.sendMessage(new TextComponentTranslation("command.huntersdream.skill.clear", player.getName()));
				break;
			case "list":
				sender.sendMessage(new TextComponentTranslation("command.huntersdream.skill.list", player.getName(),
						CommandSkill.SKILL_JOINER.join(cap.getSkills())));
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