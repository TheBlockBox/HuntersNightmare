package theblockbox.huntersdream.util.handlers;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import theblockbox.huntersdream.entity.renderer.RenderLycantrophePlayer;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.helpers.GeneralHelper;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

/**
 * Handles events which are important for transforming
 */
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = Reference.MODID)
public class TransformationClientEventHandler {
	private static RenderLycantrophePlayer renderLycantrophePlayer = null;
	private static RenderPlayer renderPlayerHand = null;
	public static HashMap<String, RenderPlayer> skinMap;
	public static final ResourceLocation[] WEREWOLF_HANDS = { getHandTexture("black", false),
			getHandTexture("brown", false), getHandTexture("white", false), getHandTexture("black", true),
			getHandTexture("brown", true), getHandTexture("white", true) };

	private static ResourceLocation getHandTexture(String variant, boolean slim) {
		return GeneralHelper.newResLoc(
				Reference.ENTITY_TEXTURE_PATH + "werewolf_arms_" + (slim ? "slim" : "normal") + "_" + variant + ".png");
	}

	@SubscribeEvent
	public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
		Minecraft mc = Minecraft.getMinecraft();
		if (ConfigHandler.customPlayerRender && !mc.playerController.isSpectator()) {
			EntityPlayer player = event.getEntityPlayer();
			// werewolf
			if (WerewolfHelper.transformedWerewolf(player)) {
				event.setCanceled(true);
				if (renderLycantrophePlayer == null)
					renderLycantrophePlayer = new RenderLycantrophePlayer(Minecraft.getMinecraft().getRenderManager());
				renderLycantrophePlayer.doRender(player, event.getX(), event.getY(), event.getZ(), player.rotationYaw,
						event.getPartialRenderTick());
			}
		}
	}

	@SubscribeEvent
	public static void onRenderGameOverlayPost(RenderGameOverlayEvent.Post event) {
		Minecraft mc = Minecraft.getMinecraft();
		if (ConfigHandler.renderXPBar && !mc.gameSettings.hideGUI && !mc.playerController.isSpectator()) {
			EntityPlayerSP player = mc.player;
			GameType gameType = mc.playerController.getCurrentGameType();
			if (gameType != GameType.CREATIVE && gameType != GameType.SPECTATOR) {
				if ((!event.isCancelable()) && event.getType() == ElementType.EXPERIENCE) {
					ITransformationPlayer cap = TransformationHelper.getCap(player);
					if (cap.getLevel() > 0.0D) {
						mc.renderEngine.bindTexture(cap.getTransformation().getXPBarTexture());

						int x = ConfigHandler.xpBarLeft ? event.getResolution().getScaledWidth() / 2 - 90
								: event.getResolution().getScaledWidth() / 2 + 10;
						int y = configureXPBarHeight(player, event.getResolution());

						if (ConfigHandler.xpBarTop)
							x = ConfigHandler.xpBarLeft ? 1 : event.getResolution().getScaledWidth() - 82;

						// make support for absorbtion and extra hearts

						mc.ingameGUI.drawTexturedModalRect(x, y, 0, 0, 81, 8);
						int percent = (int) (cap.getPercentageToNextLevel() * 79);
						mc.ingameGUI.drawTexturedModalRect(x + 1, y + 1, 0, 9, percent, 6);
						mc.ingameGUI.drawCenteredString(mc.fontRenderer, Integer.toString(cap.getLevelFloor()), x + 40,
								y - 7, 3138304);
					}
				}
			}
		}
	}

	public static int configureXPBarHeight(EntityPlayerSP player, ScaledResolution resolution) {
		int y = ConfigHandler.xpBarTop ? 8 : resolution.getScaledHeight() - 48;
		int moveUp = 0;

		// when there are bubbles aka the player is under water, move the bar up
		if (!ConfigHandler.xpBarLeft && !ConfigHandler.xpBarTop) {
			Block block = player.world.getBlockState(new BlockPos(player.posX, Math.ceil(player.posY + 1), player.posZ))
					.getBlock();
			if (block == Blocks.WATER || block == Blocks.FLOWING_WATER)
				moveUp++;

		}
		if (ConfigHandler.xpBarLeft && !ConfigHandler.xpBarTop) {
			for (ItemStack stack : player.getArmorInventoryList())
				if (!stack.isEmpty()) {
					moveUp++;
					break;
				}

			// TODO: Make that the xp bar moves up when you have an absorbtion or health
			// boost effect
			// moveUp += MathHelper.ceil(player.getAbsorptionAmount() / 20);
			// y -= (MathHelper.ceil(player.getMaxHealth() / 20) - 1) * 8;
		}
		return (y - (moveUp * 10));
	}

	@SubscribeEvent
	public static void onRenderPlayerHand(RenderHandEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		if (ConfigHandler.customPlayerRender && !mc.gameSettings.hideGUI && !mc.playerController.isSpectator()) {
			EntityPlayerSP player = mc.player;
			String skinType = player.getSkinType();
			if (WerewolfHelper.transformedWerewolf(player)) {
				if (skinMap == null)
					skinMap = ObfuscationReflectionHelper.getPrivateValue(RenderManager.class,
							Minecraft.getMinecraft().getRenderManager(), 1);
				final ResourceLocation WEREWOLF_HAND = WEREWOLF_HANDS[(skinType.equals("slim") ? 3 : 0)
						+ TransformationHelper.getCap(player).getTextureIndex()];
				if (renderPlayerHand == null) {
					renderPlayerHand = new RenderPlayer(mc.getRenderManager(), skinType.equals("slim")) {
						@Override
						public void renderLeftArm(AbstractClientPlayer clientPlayer) {
							bindTexture(WEREWOLF_HAND);
							mc.getTextureManager().bindTexture(WEREWOLF_HAND);
							super.renderLeftArm(clientPlayer);
						}

						@Override
						public void renderRightArm(AbstractClientPlayer clientPlayer) {
							bindTexture(WEREWOLF_HAND);
							mc.getTextureManager().bindTexture(WEREWOLF_HAND);
							super.renderRightArm(clientPlayer);
						}
					};
				}
				event.setCanceled(true);
				if (mc.gameSettings.thirdPersonView == 0) {
					RenderPlayer normalRender = skinMap.get(skinType);
					skinMap.put(skinType, renderPlayerHand);

					mc.entityRenderer.enableLightmap();
					mc.getItemRenderer().renderItemInFirstPerson(event.getPartialTicks());
					mc.entityRenderer.disableLightmap();

					skinMap.put(skinType, normalRender);
				}
			}
		}
	}
}