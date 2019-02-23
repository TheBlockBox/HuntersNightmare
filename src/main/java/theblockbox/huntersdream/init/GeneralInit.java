package theblockbox.huntersdream.init;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import theblockbox.huntersdream.api.WerewolfTransformationOverlay;
import theblockbox.huntersdream.util.helpers.ChanceHelper;
import theblockbox.huntersdream.util.helpers.GeneralHelper;

/**
 * An init class for different kinds of objects that aren't enough to have their own init class
 */
public class GeneralInit {
    public static final WerewolfTransformationOverlay BLOODSHOT_HEARTBEAT = new WerewolfTransformationOverlay(
            GeneralHelper.newResLoc("gui/werewolf_transformation/bloodshot_heartbeat"))
            .addOverlay(1).addOverlay(2).addOverlay(3).addOverlay(5);
    public static final WerewolfTransformationOverlay HYPERSENSITIVE_VISION = new WerewolfTransformationOverlay(
            GeneralHelper.newResLoc("gui/werewolf_transformation/hypersensitive_vision")).addOverlay(5);
    public static final WerewolfTransformationOverlay BLINKING = new WerewolfTransformationOverlay(
            GeneralHelper.newResLoc("gui/werewolf_transformation/blinking")) {
        @SideOnly(Side.CLIENT)
        @Override
        public void stitchTexture(TextureMap textureMap) {
            this.sprite = new TextureAtlasSprite(this.getPath().toString()) {
                @Override
                public void updateAnimation() {
                    int frame = this.frameCounter;
                    super.updateAnimation();
                    if (this.frameCounter != frame) {
                        this.frameCounter = Minecraft.getMinecraft().player.getRNG().nextInt(this.framesTextureData.size());
                    }
                }
            };
            textureMap.setTextureEntry(this.sprite);
        }
    }.addOverlay(1).addOverlay(2).addOverlay(3).addOverlay(4).addOverlay(5).addOverlay(6);

    public static void preInit() {
    }
}
