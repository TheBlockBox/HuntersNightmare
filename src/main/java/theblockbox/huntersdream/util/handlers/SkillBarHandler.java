package theblockbox.huntersdream.util.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Mouse;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.api.helpers.ClientHelper;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.api.helpers.TransformationHelper;
import theblockbox.huntersdream.api.interfaces.transformation.ITransformationPlayer;
import theblockbox.huntersdream.api.skill.Skill;
import theblockbox.huntersdream.util.Reference;

import javax.annotation.Nullable;
import java.util.Optional;

import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.*;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = Reference.MODID)
public class SkillBarHandler {
    public static final ResourceLocation WIDGETS = GeneralHelper.newResLoc("minecraft:textures/gui/widgets.png");
    /**
     * How much pixels the hotbar should be pushed up (always negative)
     */
    public static final float HOTBAR_PUSH_HEIGHT = -26.0F;
    /**
     * The z at which the skill bar should be drawn
     */
    public static final float SKILL_BAR_Z_LEVEL = -90.0F;
    public static final ResourceLocation CROSS = GeneralHelper.newResLoc("gui/cross");
    private static final Skill[] SKILL_BAR = new Skill[9];
    static TextureAtlasSprite crossSprite = null;
    private static int currentSkill = 0;
    private static float skillBarShowStage = 0.0F;
    private static boolean isBarGoingUp = false;
    private static boolean isSkillBarSlotChosen = false;

    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (SkillBarHandler.isSkillBarFullyShown()) {
            for (int i = 0; i < SkillBarHandler.SKILL_BAR.length; ++i) {
                if (mc.gameSettings.keyBindsHotbar[i].isPressed()) {
                    SkillBarHandler.currentSkill = i;
                    break;
                }
            }
        } else if (SkillBarHandler.isSkillBarSlotChosen() && mc.gameSettings.keyBindDrop.isKeyDown()) {
            // TODO: Better approach?
            while (mc.gameSettings.keyBindDrop.isPressed()) ;
        }
    }

    @SubscribeEvent
    public static void onMouse(MouseEvent event) {
        int dWheel = MathHelper.clamp(Mouse.getEventDWheel(), -1, 1);
        int button = event.getButton();
        if (SkillBarHandler.isSkillBarShown()) {
            int newSkill = SkillBarHandler.currentSkill - MathHelper.clamp(dWheel, -1, 1);
            if (newSkill > SkillBarHandler.SKILL_BAR.length) {
                newSkill = 0;
            } else if (newSkill < 0) {
                newSkill = SkillBarHandler.SKILL_BAR.length;
            }

            if (newSkill != SkillBarHandler.currentSkill) {
                SkillBarHandler.currentSkill = newSkill;
                event.setCanceled(true);
            }

            if ((button == 1) && SkillBarHandler.isSkillBarFullyShown()) {
                Skill skill = SkillBarHandler.getCurrentSkill();
                SkillBarHandler.hideSkillBar();
                if (skill != null) {
                    skill.onSkillActivated(Minecraft.getMinecraft().player);
                }
                PacketHandler.sendActivateSkillMessage(Minecraft.getMinecraft().world, skill);
                event.setCanceled(true);
            }
        } else {
            EntityPlayer player = Minecraft.getMinecraft().player;
            if (SkillBarHandler.shouldShowSkillBarSlot()) {
                InventoryPlayer inventory = player.inventory;
                if (SkillBarHandler.isSkillBarSlotChosen()) {
                    if (dWheel == 1) {
                        inventory.currentItem = 0;
                        SkillBarHandler.isSkillBarSlotChosen = false;
                    } else if (dWheel == -1) {
                        inventory.currentItem = 8;
                        SkillBarHandler.isSkillBarSlotChosen = false;
                    }
                    if (button == 1) {
                        SkillBarHandler.showSkillBar();
                        event.setCanceled(true);
                    } else if (button == 0) {
                        event.setCanceled(true);
                    }
                } else if (((inventory.currentItem == 0) && (dWheel == 1)) || ((inventory.currentItem == 8) && (dWheel == -1))) {
                    SkillBarHandler.isSkillBarSlotChosen = true;
                }
            }
            Optional<Skill> activeSkill = TransformationHelper.getITransformationPlayer(player).getActiveSkill();
            if (!SkillBarHandler.isSkillBarSlotChosen() && (button == 1) && player.getActiveItemStack().isEmpty()
                    && (activeSkill.isPresent() && activeSkill.get().onSkillUse(player))) {
                PacketHandler.sendUseSkillMessage(player.world);
            }
        }
    }

    // we'll just hope nobody is crazy enough to cancel
    // RenderGameOverlayEvent.Pre on the lowest priority
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onGameOverlayRenderPreLowest(RenderGameOverlayEvent.Pre event) {
        Minecraft mc = Minecraft.getMinecraft();

        int scaledHeight = event.getResolution().getScaledHeight() + 7;
        int halfScaledWidth = event.getResolution().getScaledWidth() / 2;

        if (mc.getRenderViewEntity() instanceof EntityPlayer) {
            boolean shouldDrawSkillBar = SkillBarHandler.shouldDrawSkillBar(event);
            if (shouldDrawSkillBar) {
                // translate
                GlStateManager.translate(0, SkillBarHandler.skillBarShowStage, 0);

                // draw only when the type is hotbar to prevent texture issues and drawing although it's already been drawn
                if (event.getType() == HOTBAR) {
                    // draw skill bar

                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    mc.getTextureManager().bindTexture(SkillBarHandler.WIDGETS);

                    // draw skill bar and highlight chosen skill
                    ClientHelper.drawTexturedModalRect(halfScaledWidth - 91, scaledHeight - 3, 0,
                            0, 182, 22, SkillBarHandler.SKILL_BAR_Z_LEVEL);
                    ClientHelper.drawTexturedModalRect(halfScaledWidth - 92 + SkillBarHandler.currentSkill * 20,
                            scaledHeight - 4, 0, 22, 24, 23, SkillBarHandler.SKILL_BAR_Z_LEVEL);

                    // draw cross
                    mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                    if (SkillBarHandler.crossSprite != null) {
                        ClientHelper.drawTexturedModalRect(halfScaledWidth + 92, scaledHeight, SkillBarHandler.crossSprite,
                                16, 16, SkillBarHandler.SKILL_BAR_Z_LEVEL);
                    }

                    // draw the skills
                    GuiIngame guiIng = mc.ingameGUI;
                    for (int i = 0; i < SkillBarHandler.SKILL_BAR.length; i++) {
                        int x = halfScaledWidth - 88 + 20 * i;
                        Skill skill = SkillBarHandler.SKILL_BAR[i];
                        if (skill == null) {
                            // if null, break since there will
                            // only be null skills left
                            break;
                        } else {
                            guiIng.drawTexturedModalRect(x, scaledHeight, skill.getIconAsSprite(), 16, 16);
                        }
                    }
                    mc.getTextureManager().bindTexture(SkillBarHandler.WIDGETS);
                }
            }
            Transformation transformation = TransformationHelper.getTransformation(mc.player);
            if ((event.getType() == HOTBAR) && SkillBarHandler.shouldShowSkillBarSlot()) {
                mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                Optional<Skill> activeSkill = TransformationHelper.getITransformationPlayer(mc.player).getActiveSkill();
                TextureAtlasSprite sprite = activeSkill.isPresent() ? activeSkill.get().getIconAsSprite()
                        : transformation.getIconAsSprite();
                if (sprite != null) {
                    mc.ingameGUI.drawTexturedModalRect(event.getResolution().getScaledWidth() / 2
                                    + ((mc.player.getPrimaryHand() == EnumHandSide.LEFT) ? -112 : 96),
                            event.getResolution().getScaledHeight() - 19, sprite, 16, 16);
                }
                if (SkillBarHandler.isSkillBarSlotChosen() && !shouldDrawSkillBar) {
                    mc.getTextureManager().bindTexture(SkillBarHandler.WIDGETS);
                    mc.ingameGUI.drawTexturedModalRect(halfScaledWidth + ((mc.player.getPrimaryHand() == EnumHandSide.LEFT) ?
                            -116 : 92), scaledHeight - 30, 0, 22, 24, 22);
                    mc.player.inventory.currentItem += 30;
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH, receiveCanceled = true)
    public static void onGameOverlayRenderPostHighestReceiveCanceled(RenderGameOverlayEvent.Post event) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (SkillBarHandler.shouldDrawSkillBar(event)) {
            // translate everything back
            GlStateManager.translate(0, -SkillBarHandler.skillBarShowStage, 0);
            // "animate" hotbar
            if (SkillBarHandler.isBarGoingUp) {
                // go up
                SkillBarHandler.skillBarShowStage = Math.max(SkillBarHandler.skillBarShowStage - 0.4F, SkillBarHandler.HOTBAR_PUSH_HEIGHT);
            } else {
                // go down
                SkillBarHandler.skillBarShowStage = Math.min(SkillBarHandler.skillBarShowStage + 0.4F, 0.0F);
            }
        } else if ((event.getType() == HOTBAR) && SkillBarHandler.isSkillBarSlotChosen()) {
            InventoryPlayer inventory = player.inventory;
            inventory.currentItem = MathHelper.clamp(inventory.currentItem - 30, 0, 9);
        }
    }

    /**
     * When called, opens the skill bar for the current player via an animation.
     */
    public static void showSkillBar() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        Transformation transformation = TransformationHelper.getTransformation(player);
        if (!SkillBarHandler.isSkillBarShown() && transformation.hasSkills()) {
            SkillBarHandler.skillBarShowStage = -0.01F;
            SkillBarHandler.isBarGoingUp = true;

            // add skills
            int index = 0;
            for (Skill skill : Skill.getAllSkills()) {
                if (!skill.isAlwaysActive() && skill.isForTransformation(transformation) && skill.shouldShowSkillInSkillBar(player)) {
                    SkillBarHandler.SKILL_BAR[index++] = skill;
                }
            }
            // replace all old skills
            for (int i = index; i < SkillBarHandler.SKILL_BAR.length; i++) {
                SkillBarHandler.SKILL_BAR[i] = null;
            }
        }
    }

    /**
     * If this is called and the skill bar is currently opened, this will close it for the current player via an animation.
     */
    public static void hideSkillBar() {
        SkillBarHandler.isBarGoingUp = false;
        SkillBarHandler.isSkillBarSlotChosen = false;
    }

    /**
     * Returns true if the opened skill bar should be drawn.
     */
    public static boolean shouldDrawSkillBar(RenderGameOverlayEvent event) {
        RenderGameOverlayEvent.ElementType type = event.getType();
        return SkillBarHandler.isSkillBarShown() && ((type == HEALTH) || (type == FOOD) || (type == AIR)
                || (type == ARMOR) || (type == EXPERIENCE) || (type == HEALTHMOUNT)
                || (type == HOTBAR) || (type == JUMPBAR));
    }

    /**
     * Returns true when the skill bar is seeable. (Even if it's still in the animation and you can't interact with it.)
     */
    public static boolean isSkillBarShown() {
        return SkillBarHandler.shouldShowSkillBarSlot() && (SkillBarHandler.skillBarShowStage < 0.0F);
    }

    /**
     * Returns true when the skill bar is fully shown. Used to determine if you can already interact with it
     * (e.g. select a skill or close it).
     */
    public static boolean isSkillBarFullyShown() {
        return SkillBarHandler.isSkillBarShown() && (SkillBarHandler.skillBarShowStage <= SkillBarHandler.HOTBAR_PUSH_HEIGHT);
    }

    /**
     * Returns true if the skill bar slot should be drawn.
     */
    public static boolean shouldShowSkillBarSlot() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        ITransformationPlayer transformation = TransformationHelper.getITransformationPlayer(player);
        return ConfigHandler.client.showSkillBarSlot && !player.isSpectator() && transformation.getTransformation().hasSkills()
                && !transformation.hasNoSkills() && transformation.getSkills().stream().anyMatch(skill ->
                !skill.isAlwaysActive() && skill.shouldShowSkillInSkillBar(player));
    }

    /**
     * Returns true if the skill bar slot is selected. (Doesn't count as selected when {@link #shouldShowSkillBarSlot()}
     * returns false.)
     */
    public static boolean isSkillBarSlotChosen() {
        return SkillBarHandler.isSkillBarSlotChosen && SkillBarHandler.shouldShowSkillBarSlot();
    }

    /**
     * Returns the currently selected skill. May be null if no skill is selected.
     */
    @Nullable
    public static Skill getCurrentSkill() {
        return (SkillBarHandler.currentSkill == SkillBarHandler.SKILL_BAR.length) ? null
                : SkillBarHandler.SKILL_BAR[SkillBarHandler.currentSkill];
    }

    public static float getSkillBarShowStage() {
        return SkillBarHandler.skillBarShowStage;
    }
}
