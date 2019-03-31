package theblockbox.huntersdream.potions;

import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.util.Reference;

public abstract class PotionBase extends Potion {

    public PotionBase(boolean isBadEffectIn, int liquidColorIn, int iconIndex, String name) {
        super(isBadEffectIn, liquidColorIn);

        this.setRegistryName(GeneralHelper.newResLoc(name));
        this.setPotionName("effect." + Reference.MODID + "." + name);
        this.setIconIndex(iconIndex % 6, MathHelper.floor(iconIndex / 6.0D));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasStatusIcon() {
        Minecraft.getMinecraft().renderEngine.bindTexture(GeneralHelper.newResLoc("textures/gui/potion_icons.png"));
        return true;
    }
}
