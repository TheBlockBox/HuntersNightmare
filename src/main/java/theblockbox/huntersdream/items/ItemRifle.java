package theblockbox.huntersdream.items;

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
import theblockbox.huntersdream.api.interfaces.IAmmunition;

import java.util.function.Supplier;

public class ItemRifle extends ItemPercussionGun {
    @SideOnly(Side.CLIENT)
    public static TextureAtlasSprite rifleScopeNormal = null;
    @SideOnly(Side.CLIENT)
    public static TextureAtlasSprite rifleScopeTargetted = null;

    public ItemRifle(double damage, int durability, int ticksShotCooldown, Supplier<Item> defaultAmmunition, int maximumAmmunitionStorage) {
        super(damage, durability, ticksShotCooldown, defaultAmmunition, maximumAmmunitionStorage, 0.1F, IAmmunition.AmmunitionType.RIFLE_BULLET);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public TextureAtlasSprite getReticle(EntityLivingBase entity, ItemStack stack) {
        TextureAtlasSprite sprite = super.getReticle(entity, stack);
        if ((sprite != null) && this.isLoaded(stack)) {
            Minecraft mc = Minecraft.getMinecraft();
            // when no entity is targetted, use normal scope, otherwise the targetted one
            TextureAtlasSprite scope = (mc.pointedEntity == null) ? ItemRifle.rifleScopeNormal : ItemRifle.rifleScopeTargetted;
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
        return sprite;
    }
}
