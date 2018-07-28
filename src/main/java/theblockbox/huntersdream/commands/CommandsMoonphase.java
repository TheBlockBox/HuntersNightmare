package theblockbox.huntersdream.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import theblockbox.huntersdream.util.helpers.CommandHelper;

public class CommandsMoonphase extends CommandBase {

	@Override
	public String getName() {
		return "hdmoonphase";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "command.moonphase.usage";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length >= 1) {
			if (args[0].equals("get")) {
				try {
					// when there's a world parameter
					if (args.length >= 2) {
						sender.sendMessage(new TextComponentTranslation("command.moonphase.getCurrent",
								server.getWorld(Integer.parseInt(args[1])).getCurrentMoonPhaseFactor()));
					} else {
						sender.sendMessage(new TextComponentTranslation("command.moonphase.getCurrent",
								sender.getEntityWorld().getCurrentMoonPhaseFactor()));
					}
				} catch (Exception e) {
					CommandHelper.invalidCommand(sender);
				}
			} else if (args[0].equals("add")) {
				try {
					int moonphasesToSkip = 1;
					World world = sender.getEntityWorld();
					if (args.length >= 2) {
						world = server.getWorld(Integer.parseInt(args[1]));
					}
					if (args.length >= 3) {
						moonphasesToSkip = Integer.parseInt(args[2]);
					}
					world.setWorldTime(world.getWorldTime() + (24000 * moonphasesToSkip));
					// essentially /time add 24000
					sender.sendMessage(new TextComponentTranslation("command.moonphase.getSet",
							world.getCurrentMoonPhaseFactor()));
				} catch (Exception e) {
					CommandHelper.invalidCommand(sender);
				}
			}
		} else {
			CommandHelper.invalidCommand(sender);
		}
	}

}
