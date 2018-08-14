package theblockbox.huntersdream.util.handlers;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import theblockbox.huntersdream.entity.renderer.RenderLycantropheBiped;
import theblockbox.huntersdream.entity.renderer.RenderLycantropheQuadruped;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.exceptions.UnexpectedBehaviorException;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

/**
 * Handles events which are important for transforming
 */
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = Reference.MODID)
public class TransformationClientEventHandler {
	private static RenderLycantropheBiped renderLycantropheBiped = null;
	private static RenderLycantropheQuadruped renderLycantropheQuadruped = null;
	private static RenderPlayer renderPlayer = null;
	public static final ResourceLocation WEREWOLF_HAND = new ResourceLocation(
			Reference.ENTITY_TEXTURE_PATH + "werewolf_arms.png");

	@SubscribeEvent
	public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
		if (ConfigHandler.customPlayerRender) {
			EntityPlayer player = event.getEntityPlayer();
			// werewolf
			if (WerewolfHelper.transformedWerewolf(player)) {
				event.setCanceled(true);
				if (player.isSprinting()) {
					if (renderLycantropheQuadruped == null)
						renderLycantropheQuadruped = new RenderLycantropheQuadruped(
								Minecraft.getMinecraft().getRenderManager());
					renderLycantropheQuadruped.doRender(player, event.getX(), event.getY(), event.getZ(),
							player.rotationYaw, event.getPartialRenderTick());
				} else {
					if (renderLycantropheBiped == null)
						renderLycantropheBiped = new RenderLycantropheBiped(
								Minecraft.getMinecraft().getRenderManager());
					renderLycantropheBiped.doRender(player, event.getX(), event.getY(), event.getZ(),
							player.rotationYaw, event.getPartialRenderTick());
				}
			}
		}
	}

	@SubscribeEvent
	public static void onRenderGameOverlayPost(RenderGameOverlayEvent.Post event) {
		if (ConfigHandler.renderXPBar) {
			Minecraft mc = Minecraft.getMinecraft();
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

						if (ConfigHandler.xpBarTop) {
							x = ConfigHandler.xpBarLeft ? 1 : event.getResolution().getScaledWidth() - 82;
						}

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
		renderWerewolfHand(event);
	}

	@SubscribeEvent
	public static void onRenderSpecificPlayerHand(RenderSpecificHandEvent event) {
		renderWerewolfHand(event);
	}

	// TODO: Make that this works
	public static void renderWerewolfHand(Event event) {
		if (ConfigHandler.customPlayerRender) {
			Minecraft mc = Minecraft.getMinecraft();
			EntityPlayerSP player = mc.player;
			if (WerewolfHelper.transformedWerewolf(player)) {
				if (renderPlayer == null) {
					String skinType = player.getSkinType();
					if (skinType.equals("default") || skinType.equals("slim")) {
						renderPlayer = new RenderPlayer(mc.getRenderManager(), skinType.equals("slim"));
					} else {
						throw new UnexpectedBehaviorException("Couldn't find skin type " + skinType);
					}
				}
				event.setCanceled(true);
				renderPlayer.bindTexture(WEREWOLF_HAND);
				mc.getTextureManager().bindTexture(WEREWOLF_HAND);
				GlStateManager.pushMatrix();
				EnumHandSide hand = player.getPrimaryHand();
				float f = hand == EnumHandSide.RIGHT ? 1.0F : -1.0F;
				GlStateManager.rotate(92.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(f * -41.0F, 0.0F, 0.0F, 1.0F);
				GlStateManager.translate(f * 0.3F, -1.1F, 0.45F);
				if (hand == EnumHandSide.LEFT)
					renderPlayer.renderLeftArm(player);
				else
					renderPlayer.renderRightArm(player);
				GlStateManager.popMatrix();
			}
		}
	}
}