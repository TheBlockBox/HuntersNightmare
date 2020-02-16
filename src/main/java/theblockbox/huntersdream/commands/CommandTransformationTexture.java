package theblockbox.huntersdream.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import theblockbox.huntersdream.api.helpers.CommandHelper;
import theblockbox.huntersdream.api.helpers.TransformationHelper;
import theblockbox.huntersdream.api.interfaces.transformation.ITransformationPlayer;
import theblockbox.huntersdream.util.handlers.PacketHandler;

import java.util.ArrayList;
import java.util.List;

public class CommandTransformationTexture extends CommandBase {

    @Override
    public String getName() {
        return "hdtransformationtexture";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "command.huntersdream.transformationtexture.usage";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
                                          BlockPos targetPos) {
        List<String> toReturn = new ArrayList<>();
        if (args.length == 1) {
            if (sender instanceof EntityPlayer) {
                toReturn.add("get");
                ITransformationPlayer cap = TransformationHelper.getITransformationPlayer((EntityPlayer) sender);
                for (int i = 0; i < cap.getTransformation().getTextures().length; i++) {
                    toReturn.add(String.valueOf(i));
                }
            }
        } else if (args.length == 2) {
            return CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }
        return toReturn;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        try {
            EntityPlayer player;
            if (args.length >= 2) {
                player = server.getEntityWorld().getPlayerEntityByName(args[1]);
            } else {
                player = (EntityPlayer) sender;
            }
            ITransformationPlayer cap = TransformationHelper.getITransformationPlayer(player);

            if ("get".equals(args[0])) {
                sender.sendMessage(new TextComponentTranslation("command.huntersdream.transformationtexture.get",
                        player.getName(), cap.getTextureIndex()));
            } else {
                int index = Integer.parseInt(args[0]);
                if (index >= cap.getTransformation().getTextures().length) {
                    throw new IllegalArgumentException("Wrong texture length");
                }
                cap.setTextureIndex(index);
                sender.sendMessage(new TextComponentTranslation("command.huntersdream.transformationtexture.set",
                        player.getName(), cap.getTextureIndex()));
                PacketHandler.sendTransformationMessage((EntityPlayerMP) player);
            }
        } catch (Exception e) {
            CommandHelper.invalidCommand(sender, e);
        }
    }

}