package theblockbox.huntersdream.commands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import theblockbox.huntersdream.util.helpers.CommandHelper;

public class CommandMoonphase extends CommandBase {
	private static final Exception AIOOBE = new ArrayIndexOutOfBoundsException(0);

	@Override
	public String getName() {
		return "hdmoonphase";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "command.huntersdream.moonphase.usage";
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
			toReturn.add("get");
			toReturn.add("add");
		} else if (args.length == 2) {
			toReturn.add("0");
		} else if (args.length == 3) {
			for (int i = 1; i < 20; i++) {
				toReturn.add(String.valueOf(i));
			}
		}
		return toReturn;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
		if (args.length >= 1) {
			if (args[0].equals("get")) {
				try {
					// when there's a world parameter
					if (args.length >= 2) {
						sender.sendMessage(new TextComponentTranslation("command.huntersdream.moonphase.getCurrent",
								server.getWorld(Integer.parseInt(args[1])).getCurrentMoonPhaseFactor()));
					} else {
						sender.sendMessage(new TextComponentTranslation("command.huntersdream.moonphase.getCurrent",
								sender.getEntityWorld().getCurrentMoonPhaseFactor()));
					}
				} catch (Exception e) {
					CommandHelper.invalidCommand(sender, e);
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
					sender.sendMessage(new TextComponentTranslation("command.huntersdream.moonphase.getSet",
							world.getCurrentMoonPhaseFactor()));
				} catch (Exception e) {
					CommandHelper.invalidCommand(sender, e);
				}
			}
		} else {
			CommandHelper.invalidCommand(sender, AIOOBE);
		}
	}
}
