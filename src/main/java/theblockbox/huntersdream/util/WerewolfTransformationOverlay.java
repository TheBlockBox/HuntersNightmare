package theblockbox.huntersdream.util;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import theblockbox.huntersdream.util.helpers.GeneralHelper;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class WerewolfTransformationOverlay {
    public static final List<Queue<WerewolfTransformationOverlay>> OVERLAYS = new ArrayList<>(6);
    /** Internal, don't use outside of this mod */
    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite sprite;
    private final ResourceLocation path;
    private final int width;
    private final int height;

    public WerewolfTransformationOverlay(ResourceLocation path, int width, int height) {
        this.path = path;
        this.width = width;
        this.height = height;
    }

    /**
     * Adds this WerewolfTransformationOverlay to be rendered at the given transformation stage.
     * It's possible to add one overlay to multiple transformation stages.
     */
    public WerewolfTransformationOverlay addOverlay(int transformationStage) {
        Queue<WerewolfTransformationOverlay> queue = GeneralHelper.safeGet(WerewolfTransformationOverlay.OVERLAYS, transformationStage);
        if(queue == null) {
            queue = new ArrayDeque<>();
            queue.add(this);
            GeneralHelper.safeSet(WerewolfTransformationOverlay.OVERLAYS, transformationStage, queue);
        } else if (!queue.contains(this)) {
            queue.add(this);
        }
        return this;
    }

    public ResourceLocation getPath() {
        return this.path;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}
