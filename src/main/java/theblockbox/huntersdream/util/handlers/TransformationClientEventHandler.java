package theblockbox.huntersdream.util.handlers;

import java.util.List;
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
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import theblockbox.huntersdream.api.Skill;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.entity.renderer.RenderLycanthropePlayer;
import theblockbox.huntersdream.gui.GuiButtonClickable;
import theblockbox.huntersdream.gui.GuiButtonSurvivalTab;
import theblockbox.huntersdream.gui.GuiSkillTab;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.helpers.GeneralHelper;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.helpers.VampireHelper;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;
import theblockbox.huntersdream.util.interfaces.transformation.IVampirePlayer;

/**
 * Handles events which are important for transforming
 */
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = Reference.MODID)
public class TransformationClientEventHandler {
	private static RenderLycanthropePlayer renderLycantrophePlayer = null;
	private static RenderPlayer renderPlayerHand = null;
	public static final ResourceLocation BLOOD_BAR = GeneralHelper.newResLoc("textures/gui/blood_bar.png");
	public static final ResourceLocation WEREWOLF_HEALTH = GeneralHelper.newResLoc("textures/gui/werewolf_health.png");
	private static final ResourceLocation[] WEREWOLF_HANDS = {TransformationClientEventHandler.getHandTexture("brown", false),
			TransformationClientEventHandler.getHandTexture("black", false), TransformationClientEventHandler.getHandTexture("white", false), TransformationClientEventHandler.getHandTexture("yellow", false),
			TransformationClientEventHandler.getHandTexture("brown", true), TransformationClientEventHandler.getHandTexture("black", true), TransformationClientEventHandler.getHandTexture("white", true),
			TransformationClientEventHandler.getHandTexture("yellow", true) };

	private static ResourceLocation getHandTexture(String variant, boolean slim) {
		return GeneralHelper.newResLoc(Reference.ENTITY_TEXTURE_PATH + "werewolf/werewolf_arms_"
				+ (slim ? "slim" : "normal") + "_" + variant + ".png");
	}

	@SubscribeEvent
	public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
		Minecraft mc = Minecraft.getMinecraft();
		if (ConfigHandler.client.customPlayerRender && !mc.playerController.isSpectator()) {
			EntityPlayer player = event.getEntityPlayer();
			// werewolf
			if (WerewolfHelper.isTransformed(player)) {
				event.setCanceled(true);
				if (TransformationClientEventHandler.renderLycantrophePlayer == null)
					TransformationClientEventHandler.renderLycantrophePlayer = new RenderLycanthropePlayer(Minecraft.getMinecraft().getRenderManager());
				TransformationClientEventHandler.renderLycantrophePlayer.doRender(player, event.getX(), event.getY(), event.getZ(), player.rotationYaw,
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
				if (TransformationClientEventHandler.renderPlayerHand == null) {
					TransformationClientEventHandler.renderPlayerHand = new TransformationClientEventHandler.RenderLycanthropeArm(skinType, player);
				}
				event.setCanceled(true);
				if (mc.gameSettings.thirdPersonView == 0) {
					Map<String, RenderPlayer> skinMap = Minecraft.getMinecraft().getRenderManager().skinMap;
					RenderPlayer normalRender = skinMap.get(skinType);
					skinMap.put(skinType, TransformationClientEventHandler.renderPlayerHand);

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
			List<GuiButton> buttonList = event.getButtonList();
			GuiContainer gui = (GuiContainer) event.getGui();
			int x = gui.getGuiLeft() + 2;
			int y = gui.getGuiTop() - 18;

			buttonList.add(new GuiButtonSurvivalTab(buttonList.size(), x, y, new GuiSkillTab(),
					TransformationHelper.getTransformation(Minecraft.getMinecraft().player).getIconAsSprite()));
			buttonList.add(new GuiButtonClickable(buttonList.size(), x + 19, y, Skill.BITE_0.getIconAsSprite()){
				@Override
				public void onClicked(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
					mc.displayGuiScreen(null);
					SkillBarHandler.showSkillBar();
				}
			});
		}
	}

	// Add transformation icons to atlas
	@SubscribeEvent
	public static void onTextureStichPre(TextureStitchEvent.Pre event) {
		TextureMap map = event.getMap();
		for (Transformation transformation : Transformation.getAllTransformations()) {
			if (transformation.isTransformation()) {
				transformation.setIconSprite(map.registerSprite(transformation.getIcon()));
			}
		}
		for (Skill skill : Skill.getAllSkills()) {
			skill.setIconSprite(map.registerSprite(skill.getIcon()));
		}
	}

	@SubscribeEvent
	public static void onGameOverlayRenderPre(RenderGameOverlayEvent.Pre event) {
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayerSP player = mc.player;
		RenderGameOverlayEvent.ElementType type = event.getType();
		if (type == RenderGameOverlayEvent.ElementType.FOOD) {
			TransformationClientEventHandler.onHungerBarRendered(event, player, mc);
		} else if (type == RenderGameOverlayEvent.ElementType.HEALTH) {
			// render werewolf hearts
			if (WerewolfHelper.isTransformed(player)) {
				mc.getTextureManager().bindTexture(TransformationClientEventHandler.WEREWOLF_HEALTH);
			}
		} else {
			SkillBarHandler.onGameOverlayRenderPre(event);
		}
	}

	@SubscribeEvent
	public static void onGameOverlayRenderPost(RenderGameOverlayEvent.Post event) {
		Minecraft mc = Minecraft.getMinecraft();
		RenderGameOverlayEvent.ElementType type = event.getType();
		if (type == RenderGameOverlayEvent.ElementType.HEALTH && WerewolfHelper.isTransformed(mc.player)) {
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
				mc.getTextureManager().bindTexture(TransformationClientEventHandler.BLOOD_BAR);
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

	public static class RenderLycanthropeArm extends RenderPlayer {
		private final EntityPlayer player;

		public RenderLycanthropeArm(String skinType, EntityPlayer player) {
			super(Minecraft.getMinecraft().getRenderManager(), "slim".equals(skinType));
			this.player = player;
		}
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
			ITransformationPlayer transformation = TransformationHelper
					.getITransformationPlayer(this.player);
			ResourceLocation werewolfHand = TransformationClientEventHandler.WEREWOLF_HANDS[(("slim".equals(clientPlayer.getSkinType())
					? transformation.getTransformation().getTextures().length
					: 0)
					+ TransformationHelper.getITransformationPlayer(clientPlayer).getTextureIndex())];
			this.bindTexture(werewolfHand);
			Minecraft.getMinecraft().getTextureManager().bindTexture(werewolfHand);
		}
	}
}