package pixeleyestudios.huntersdream.commands;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import pixeleyestudios.huntersdream.util.helpers.CommandHelper;
import pixeleyestudios.huntersdream.util.helpers.PacketHelper;
import pixeleyestudios.huntersdream.util.helpers.TransformationHelper;
import pixeleyestudios.huntersdream.util.interfaces.ITransformationPlayer;

public class CommandsTransformationLevel extends CommandBase {

	@Override
	public String getName() {
		return "transformationlevel";
	}

	@Override
	public List<String> getAliases() {
		return Lists.newArrayList("tlevel", "tlvl");
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "transformationlevel <set|add|remove|get> <value> <player>";
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
			sender.sendMessage(new TextComponentString(player.getName() + "'s transformation xp was set to " + value));
			PacketHelper.syncPlayerTransformationData(player);
		} catch (Exception e) {
			CommandHelper.invalidCommand(sender);
		}
	}

}
