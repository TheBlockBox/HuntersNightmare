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

public class CommandsTransformationTexture extends CommandBase {

	@Override
	public String getName() {
		return "hdtransformationtexture";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "command.transformationtexture.usage";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		try {
			EntityPlayer player;
			if (args.length >= 2) {
				player = server.getEntityWorld().getPlayerEntityByName(args[1]);
			} else {
				player = (EntityPlayer) sender;
			}
			ITransformationPlayer cap = TransformationHelper.getCap(player);

			if (args[0].equals("get")) {
				sender.sendMessage(new TextComponentTranslation("command.transformationtexture.get", player.getName(),
						cap.getTextureIndex()));
			} else {
				int index = Integer.parseInt(args[0]);
				if (index >= cap.getTransformation().TEXTURES.length) {
					throw new IllegalArgumentException("Wrong texture length");
				}
				cap.setTextureIndex(index);
				sender.sendMessage(new TextComponentTranslation("command.transformationtexture.set", player.getName(),
						cap.getTextureIndex()));
				Packets.TRANSFORMATION.sync(new ExecutionPath(), player);
			}
		} catch (Exception e) {
			CommandHelper.invalidCommand(sender);
		}
	}

}
