package theblockbox.huntersdream.util.handlers;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.entity.renderer.RenderWolfmanPlayer;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.helpers.TransformationHelper.Transformations;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;
import theblockbox.huntersdream.util.interfaces.ITransformationPlayer;

/**
 * Handles events which are important for transforming
 */
@Mod.EventBusSubscriber
public class TransformationClientEventHandler {
	@SideOnly(Side.CLIENT)
	private static RenderWolfmanPlayer renderWerewolf = null;

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
		if (ConfigHandler.customPlayerRender) {
			EntityPlayer player = event.getEntityPlayer();
			ITransformationPlayer cap = TransformationHelper.getCap(player);

			// werewolf
			if ((cap.getTransformation() == Transformations.WEREWOLF) && cap.transformed()
					&& WerewolfHelper.hasControl(player)) {
				event.setCanceled(true);
				if (renderWerewolf == null)
					renderWerewolf = new RenderWolfmanPlayer(Minecraft.getMinecraft().getRenderManager());
				renderWerewolf.doRender(player, event.getX(), event.getY(), event.getZ(), player.rotationYaw,
						event.getPartialRenderTick());
			}
		}
	}

	// Transformation xp handler
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onGameOverlayRenderPost(RenderGameOverlayEvent.Post event) {
		if (ConfigHandler.renderXPBar) {
			Minecraft mc = Minecraft.getMinecraft();
			EntityPlayer player = Main.proxy.getPlayer();
			GameType gameType = mc.playerController.getCurrentGameType();
			if (gameType != GameType.CREATIVE && gameType != GameType.SPECTATOR) {
				if ((!event.isCancelable()) && event.getType() == ElementType.EXPERIENCE) {
					Transformations transformation = TransformationHelper.getTransformation(player);
					if (transformation.getLevel(player) > 0) {
						mc.renderEngine.bindTexture(transformation.getXPBarTexture());
						int x = event.getResolution().getScaledWidth() / 2 + 10;
						int y = event.getResolution().getScaledHeight() - 48;

						// when there are bubbles aka the player is under water, move the bar up
						Block block = player.world
								.getBlockState(new BlockPos(player.posX, Math.ceil(player.posY + 1), player.posZ))
								.getBlock();
						if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
							y -= 10;
						}
						mc.ingameGUI.drawTexturedModalRect(x, y, 0, 0, 81, 8);
						int percent = (int) (transformation.getPercentageToNextLevel(player) * 79);
						mc.ingameGUI.drawTexturedModalRect(x + 1, y + 1, 0, 9, percent, 9);
						mc.ingameGUI.drawCenteredString(mc.fontRenderer,
								Integer.toString(transformation.getLevelFloor(player)), x + 40, y - 7, 3138304);
					}
				}
			}
		}
	}

	// TODO: Custom hand render
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onRenderPlayerHand(RenderHandEvent event) {
		if (ConfigHandler.customPlayerRender) {

		}
	}
}