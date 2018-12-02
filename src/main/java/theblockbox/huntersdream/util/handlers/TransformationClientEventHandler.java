package theblockbox.huntersdream.util.handlers;

import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import theblockbox.huntersdream.entity.renderer.RenderLycantrophePlayer;
import theblockbox.huntersdream.gui.GuiButtonSurvivalTab;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.Transformation;
import theblockbox.huntersdream.util.helpers.GeneralHelper;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.helpers.VampireHelper;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;
import theblockbox.huntersdream.util.interfaces.transformation.IVampirePlayer;

/**
 * Handles events which are important for transforming
 */
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = Reference.MODID)
public class TransformationClientEventHandler {
	private static RenderLycantrophePlayer renderLycantrophePlayer = null;
	private static RenderPlayer renderPlayerHand = null;
	public static final ResourceLocation BLOOD_BAR = GeneralHelper.newResLoc("textures/gui/blood_bar.png");
	public static final ResourceLocation WEREWOLF_HEALTH = GeneralHelper.newResLoc("textures/gui/werewolf_health.png");
	public static final ResourceLocation[] WEREWOLF_HANDS = { getHandTexture("brown", false),
			getHandTexture("black", false), getHandTexture("white", false), getHandTexture("brown", true),
			getHandTexture("black", true), getHandTexture("white", true) };

	private static ResourceLocation getHandTexture(String variant, boolean slim) {
		return GeneralHelper.newResLoc(
				Reference.ENTITY_TEXTURE_PATH + "werewolf_arms_" + (slim ? "slim" : "normal") + "_" + variant + ".png");
	}

	@SubscribeEvent
	public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
		Minecraft mc = Minecraft.getMinecraft();
		if (ConfigHandler.client.customPlayerRender && !mc.playerController.isSpectator()) {
			EntityPlayer player = event.getEntityPlayer();
			// werewolf
			if (WerewolfHelper.isTransformed(player)) {
				event.setCanceled(true);
				if (renderLycantrophePlayer == null)
					renderLycantrophePlayer = new RenderLycantrophePlayer(Minecraft.getMinecraft().getRenderManager());
				renderLycantrophePlayer.doRender(player, event.getX(), event.getY(), event.getZ(), player.rotationYaw,
						event.getPartialRenderTick());
			}
		}
	}

	@SubscribeEvent
	public static void onRenderPlayerHand(RenderHandEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		if (ConfigHandler.client.customPlayerRender && !mc.gameSettings.hideGUI && !mc.playerController.isSpectator()) {
			EntityPlayerSP player = mc.player;
			String skinType = player.getSkinType();
			if (WerewolfHelper.isTransformed(player)) {
				if (renderPlayerHand == null) {
					renderPlayerHand = new RenderPlayer(mc.getRenderManager(), skinType.equals("slim")) {
						@Override
						public void renderLeftArm(AbstractClientPlayer clientPlayer) {
							this.bindTextures(clientPlayer);
							super.renderLeftArm(clientPlayer);
						}

						@Override
						public void renderRightArm(AbstractClientPlayer clientPlayer) {
							this.bindTextures(clientPlayer);
							super.renderRightArm(clientPlayer);
						}

						public void bindTextures(AbstractClientPlayer clientPlayer) {
							ResourceLocation werewolfHand = WEREWOLF_HANDS[((clientPlayer.getSkinType().equals("slim")
									? 3
									: 0)
									+ TransformationHelper.getITransformationPlayer(clientPlayer).getTextureIndex())];
							this.bindTexture(werewolfHand);
							Minecraft.getMinecraft().getTextureManager().bindTexture(werewolfHand);
						}
					};
				}
				event.setCanceled(true);
				if (mc.gameSettings.thirdPersonView == 0) {
					Map<String, RenderPlayer> skinMap = Minecraft.getMinecraft().getRenderManager().skinMap;
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

	@SubscribeEvent
	public static void onGuiDraw(GuiScreenEvent.InitGuiEvent.Post event) {
		if (event.getGui() instanceof GuiInventory) {
			GuiContainer gui = (GuiContainer) event.getGui();
			GuiButton button = new GuiButtonSurvivalTab(event.getButtonList().size(), gui.getGuiLeft() + 2,
					gui.getGuiTop() - 18);
			event.getButtonList().add(button);
		}
	}

	@SubscribeEvent
	public static void onGameOverlayRenderPre(RenderGameOverlayEvent.Pre event) {
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayerSP player = mc.player;
		if (event.getType() == ElementType.FOOD) {
			onHungerBarRendered(event, player, mc);
		} else if (event.getType() == ElementType.HEALTH) {
			// render werewolf hearts
			if (WerewolfHelper.isTransformed(player)) {
				mc.getTextureManager().bindTexture(WEREWOLF_HEALTH);
			}
		}
	}

	@SubscribeEvent
	public static void onGameOverlayRenderPost(RenderGameOverlayEvent.Post event) {
		Minecraft mc = Minecraft.getMinecraft();
		if (event.getType() == ElementType.HEALTH && WerewolfHelper.isTransformed(mc.player)) {
			// binding icon texture so that food bar still gets shown
			mc.getTextureManager().bindTexture(Gui.ICONS);
		}
	}

	public static void onHungerBarRendered(RenderGameOverlayEvent.Pre event, EntityPlayerSP player, Minecraft mc) {
		if (TransformationHelper.getTransformation(player) == Transformation.VAMPIRE) {
			mc.profiler.startSection("huntersdream:hunger_werewolf");
			event.setCanceled(true);
			GuiIngameForge ingGUI = (GuiIngameForge) mc.ingameGUI;
			IVampirePlayer vampire = VampireHelper.getIVampire(player);
			GlStateManager.enableBlend();
			{
				mc.renderEngine.bindTexture(BLOOD_BAR);
				int left = event.getResolution().getScaledWidth() / 2 + 91;
				int top = event.getResolution().getScaledHeight() - GuiIngameForge.right_height;
				GuiIngameForge.right_height += 10;
				int blood = vampire.getBlood();

				for (int i = 0; i < 10; ++i) {
					int idx = i * 2 + 1;
					int x = left - i * 8 - 9;
					int y = top - 3;
					int icon = 16;
					byte background = 0;

					if (player.isPotionActive(MobEffects.HUNGER)) {
						icon += 36;
						background = 13;
					}

					// none
					ingGUI.drawTexturedModalRect(x, y, 16 + background * 9, 24, 9, 12);

					if (idx < blood) {
						// half
						ingGUI.drawTexturedModalRect(x, y, icon + 36, 24, 9, 12);
					} else if (idx == blood) {
						// full
						ingGUI.drawTexturedModalRect(x, y, icon + 45, 24, 9, 12);
					}
				}
			}
			GlStateManager.disableBlend();
			mc.profiler.endSection();

			// post new post event so gui rendering from other mods doesn't get canceled
			MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(event, event.getType()));
		}
	}
}