package theblockbox.huntersdream.util.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.api.WerewolfTransformationOverlay;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.api.helpers.TransformationHelper;
import theblockbox.huntersdream.api.helpers.VampireHelper;
import theblockbox.huntersdream.api.helpers.WerewolfHelper;
import theblockbox.huntersdream.api.init.ItemInit;
import theblockbox.huntersdream.api.init.ParticleClientInit;
import theblockbox.huntersdream.api.init.ParticleCommonInit;
import theblockbox.huntersdream.api.interfaces.IGun;
import theblockbox.huntersdream.api.skill.Skill;
import theblockbox.huntersdream.entity.renderer.RenderLycanthropePlayer;
import theblockbox.huntersdream.items.ItemGun;
import theblockbox.huntersdream.items.ItemRifle;
import theblockbox.huntersdream.items.ItemShotgun;
import theblockbox.huntersdream.util.Reference;

import java.util.Collection;
import java.util.Map;

/**
 * Handles events which are important for transforming
 */
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = Reference.MODID)
public class TransformationClientEventHandler {
    public static final ResourceLocation BLOOD_BAR = GeneralHelper.newResLoc("textures/gui/blood_bar.png");
    public static final ResourceLocation WEREWOLF_HEALTH = GeneralHelper.newResLoc("textures/gui/werewolf_health.png");
    public static TextureAtlasSprite transparent16x16Texture = null;
    private static final ResourceLocation WEREWOLF_HAND = GeneralHelper.newResLoc(Reference.ENTITY_TEXTURE_PATH + "werewolf/werewolf_arms.png");
    private static RenderLycanthropePlayer renderLycantrophePlayer = null;
    private static RenderPlayer renderPlayerHand = null;
    private static int oldActiveStackUseCount = 0;

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        EntityPlayer player = event.getEntityPlayer();
        if (ConfigHandler.client.customPlayerRender && !player.isSpectator()) {
            // werewolf
            if (WerewolfHelper.isTransformed(player)) {
                event.setCanceled(true);
                if (TransformationClientEventHandler.renderLycantrophePlayer == null)
                    TransformationClientEventHandler.renderLycantrophePlayer = new RenderLycanthropePlayer(Minecraft.getMinecraft().getRenderManager());
                TransformationClientEventHandler.renderLycantrophePlayer.doRender(player, event.getX(), event.getY(),
                        event.getZ(), player.rotationYaw, event.getPartialRenderTick());
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onRenderPlayerPreLow(RenderPlayerEvent.Pre event) {
        EntityPlayer player = event.getEntityPlayer();
        for (EnumHand hand : GeneralHelper.HANDS) {
            ItemStack stack = player.getHeldItem(hand);
            if ((stack.getItem() instanceof IGun) && ((IGun) stack.getItem()).shouldRenderDifferently(player, stack)) {
                TransformationClientEventHandler.oldActiveStackUseCount = player.activeItemStackUseCount;
                player.activeItemStackUseCount = 1;
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onRenderPlayerPost(RenderPlayerEvent.Post event) {
        EntityPlayer player = event.getEntityPlayer();
        for (EnumHand hand : GeneralHelper.HANDS) {
            ItemStack stack = player.getHeldItem(hand);
            if ((stack.getItem() instanceof IGun) && ((IGun) stack.getItem()).shouldRenderDifferently(player, stack)
                    && (player.activeItemStackUseCount == 1)) {
                player.activeItemStackUseCount = TransformationClientEventHandler.oldActiveStackUseCount;
                TransformationClientEventHandler.oldActiveStackUseCount = 0;
            }
        }
    }

    @SubscribeEvent
    public static void onRenderPlayerHand(RenderHandEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (ConfigHandler.client.customPlayerRender && !mc.gameSettings.hideGUI && !mc.playerController.isSpectator()) {
            EntityPlayerSP player = mc.player;
            if (WerewolfHelper.isTransformed(player)) {
                if (TransformationClientEventHandler.renderPlayerHand == null) {
                    TransformationClientEventHandler.renderPlayerHand = new TransformationClientEventHandler.RenderLycanthropeArm();
                }
                event.setCanceled(true);
                if (mc.gameSettings.thirdPersonView == 0) {
                    Map<String, RenderPlayer> skinMap = Minecraft.getMinecraft().getRenderManager().skinMap;
                    String skinType = player.getSkinType();
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

    // Add transformation icons to atlas
    @SubscribeEvent
    public static void onTextureStichPre(TextureStitchEvent.Pre event) {
        TextureMap map = event.getMap();
        for (Transformation transformation : Transformation.getAllTransformations()) {
            if (transformation.isTransformation() && transformation.hasSkills()) {
                transformation.setIconSprite(map.registerSprite(transformation.getIcon()));
            }
        }
        for (Skill skill : Skill.getAllSkills()) {
            if (skill.isParentSkill()) {
                skill.setIconSprite(map.registerSprite(skill.getIcon()));
            }
        }
        for (int i = 1; i <= WerewolfHelper.getAmountOfTransformationStages(); i++) {
            Collection<WerewolfTransformationOverlay> collection = WerewolfTransformationOverlay.getOverlaysForTransformationStage(i);
            if (collection != null) {
                collection.forEach(overlay -> overlay.stitchTexture(map));
            }
        }
        TransformationClientEventHandler.transparent16x16Texture = map.registerSprite(GeneralHelper.newResLoc("nothing"));
        SkillBarHandler.crossSprite = map.registerSprite(SkillBarHandler.CROSS);
        ParticleClientInit.bloodParticleTexture = map.registerSprite(GeneralHelper.newResLoc("particles/"
                + ParticleCommonInit.BLOOD_PARTICLE.getParticleName().split(":", 2)[1]));
        ItemGun.reticleNormal = map.registerSprite(GeneralHelper.newResLoc("gui/gun/reticle_normal"));
        ItemGun.reticleReload = map.registerSprite(GeneralHelper.newResLoc("gui/gun/reticle_reload"));
        ItemShotgun.reticleNormalShotgun = map.registerSprite(GeneralHelper.newResLoc("gui/gun/reticle_normal_shotgun"));
        ItemRifle.rifleScope = map.registerSprite(GeneralHelper.newResLoc("gui/gun/rifle_scope"));
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
            if (TransformationHelper.getTransformation(player) == Transformation.WEREWOLF
                    && ((WerewolfHelper.getTransformationStage(player) >= 5) || WerewolfHelper.isTransformed(player))) {
                mc.getTextureManager().bindTexture(TransformationClientEventHandler.WEREWOLF_HEALTH);
            }
        } else if (type == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
            TransformationClientEventHandler.onCrosshairsRendered(event, player, mc);
        }
    }

    @SubscribeEvent
    public static void onGameOverlayRenderPost(RenderGameOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getMinecraft();
        RenderGameOverlayEvent.ElementType type = event.getType();
        EntityPlayer player = mc.player;
        if (type == RenderGameOverlayEvent.ElementType.HEALTH) {
            if (TransformationHelper.getTransformation(player) == Transformation.WEREWOLF
                    && ((WerewolfHelper.getTransformationStage(player) >= 5) || WerewolfHelper.isTransformed(player))) {
                // binding icon texture so that food bar still gets shown
                mc.getTextureManager().bindTexture(Gui.ICONS);
            }
        } else if ((type == RenderGameOverlayEvent.ElementType.ALL) && (TransformationHelper.getTransformation(player) == Transformation.WEREWOLF)) {
            // draw overlays
            int width = event.getResolution().getScaledWidth();
            int height = event.getResolution().getScaledHeight();
            int transformationStage = WerewolfHelper.getTransformationStage(player);
            if (transformationStage != 0) {
                mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                GlStateManager.enableAlpha();
                GlStateManager.enableBlend();
                Collection<WerewolfTransformationOverlay> collection = WerewolfTransformationOverlay
                        .getOverlaysForTransformationStage(transformationStage);
                if (collection != null) {
                    for (WerewolfTransformationOverlay overlay : collection) {
                        overlay.draw(width, height);
                    }
                }
            }
        }
    }

    public static void onHungerBarRendered(RenderGameOverlayEvent.Pre event, EntityPlayerSP player, Minecraft mc) {
        Transformation transformation = TransformationHelper.getTransformation(player);
        if (transformation == Transformation.VAMPIRE) {
            mc.profiler.startSection("huntersdream:hunger_vampire");
            event.setCanceled(true);
            GuiIngameForge ingGUI = (GuiIngameForge) mc.ingameGUI;
            GlStateManager.enableBlend();
            {
                mc.getTextureManager().bindTexture(TransformationClientEventHandler.BLOOD_BAR);
                int left = event.getResolution().getScaledWidth() / 2 + 91;
                int top = event.getResolution().getScaledHeight() - GuiIngameForge.right_height;
                GuiIngameForge.right_height += 10;
                int blood = VampireHelper.getBlood(player);

                for (int i = 0; i < 10; ++i) {
                    int idx = i * 2 + 1;
                    int x = left - i * 8 - 9;
                    int y = top - 3;
                    int icon = 16;
                    int background = 0;

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

    public static void onCrosshairsRendered(RenderGameOverlayEvent.Pre event, EntityPlayerSP player, Minecraft mc) {
        for (ItemStack stack : new ItemStack[]{player.getHeldItemMainhand(), player.getHeldItemOffhand()}) {
            if (stack.getItem() instanceof IGun) {
                TextureAtlasSprite reticle = ((IGun) stack.getItem()).getReticle(player, stack);
                if (reticle != null) {
                    // cancel event
                    event.setCanceled(true);
                    // bind texture, enable alpha and blend and draw reticle
                    GlStateManager.enableAlpha();
                    GlStateManager.enableBlend();
                    mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                    mc.ingameGUI.drawTexturedModalRect(event.getResolution().getScaledWidth() / 2 - 7,
                            event.getResolution().getScaledHeight() / 2 - 7, reticle, 16, 16);
                    // return so that no two reticles will be drawn
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onColorHandlerItem(ColorHandlerEvent.Item event) {
        event.getItemColors().registerItemColorHandler((stack, tintIndex) -> (tintIndex > 0) ? -1 : ((ItemArmor) stack.getItem()).getColor(stack),
                ItemInit.HUNTER_HAT, ItemInit.HUNTER_COAT, ItemInit.HUNTER_PANTS, ItemInit.HUNTER_BOOTS);
    }

    public static class RenderLycanthropeArm extends RenderPlayer {

        public RenderLycanthropeArm() {
            super(Minecraft.getMinecraft().getRenderManager(), false);
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
            this.bindTexture(TransformationClientEventHandler.WEREWOLF_HAND);
        }
    }
}