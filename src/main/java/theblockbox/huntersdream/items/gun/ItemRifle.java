package theblockbox.huntersdream.items.gun;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import theblockbox.huntersdream.api.init.SoundInit;
import theblockbox.huntersdream.api.interfaces.IAmmunition;
import theblockbox.huntersdream.util.handlers.TransformationClientEventHandler;

import java.util.function.Supplier;

public class ItemRifle extends ItemPercussionGun {
    @SideOnly(Side.CLIENT)
    public static TextureAtlasSprite rifleScope = null;

    public ItemRifle(double damage, int durability, int ticksShotCooldown, Supplier<Item> defaultAmmunition, int maximumAmmunitionStorage) {
        super(damage, durability, ticksShotCooldown, defaultAmmunition, maximumAmmunitionStorage, 0.1F, IAmmunition.AmmunitionType.RIFLE_BULLET);
        this.fireSound = SoundInit.RIFLE_FIRE;
        this.reloadSound = SoundInit.RIFLE_RELOAD;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public TextureAtlasSprite getReticle(EntityLivingBase entity, ItemStack stack) {
        TextureAtlasSprite sprite = super.getReticle(entity, stack);
        boolean showScope = this.isAiming(entity, stack);
        if ((sprite != null) && showScope) {
            Minecraft mc = Minecraft.getMinecraft();
            TextureAtlasSprite scope = ItemRifle.rifleScope;
            ScaledResolution res = ((GuiIngameForge) mc.ingameGUI).getResolution();
            // draw rifle scope
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.pushMatrix();
            int overlayWidth = scope.getIconWidth();
            int overlayHeight = scope.getIconHeight();
            mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            GlStateManager.scale(res.getScaledWidth() / (double) overlayWidth, res.getScaledHeight() / (double) overlayHeight, 1.0D);
            mc.ingameGUI.drawTexturedModalRect(0, 0, scope, overlayWidth, overlayHeight);
            GlStateManager.popMatrix();
        }
        return this.isLoaded(stack) ? TransformationClientEventHandler.transparent16x16Texture : sprite;
    }

    public boolean isAiming(EntityLivingBase entity, ItemStack stack) {
        return this.isLoaded(stack) && entity.isSneaking();
    }
}