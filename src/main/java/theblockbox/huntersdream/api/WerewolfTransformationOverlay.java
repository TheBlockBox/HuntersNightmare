package theblockbox.huntersdream.api;

import com.google.common.collect.Iterators;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.api.helpers.WerewolfHelper;

import javax.annotation.Nullable;
import java.util.*;

public class WerewolfTransformationOverlay {
    private static final List<WerewolfTransformationOverlay.InternalCollection> OVERLAYS =
            new ArrayList<>(WerewolfHelper.getAmountOfTransformationStages());
    private final ResourceLocation path;
    @SideOnly(Side.CLIENT)
    protected TextureAtlasSprite sprite;

    public WerewolfTransformationOverlay(ResourceLocation path) {
        this.path = path;
    }

    /**
     * Returns an unmodifiable collection that contains all overlays that should be shown at the given stage.
     * All changes will be visible in the returned collection, so if a new overlay is added, it'll also be
     * added to the returned collection.
     */
    @Nullable
    public static Collection<WerewolfTransformationOverlay> getOverlaysForTransformationStage(int transformationStage) {
        return GeneralHelper.safeGet(WerewolfTransformationOverlay.OVERLAYS, transformationStage);
    }

    /**
     * Adds this WerewolfTransformationOverlay to be rendered at the given transformation stage.
     * It's possible to add one overlay to multiple transformation stages.
     */
    public WerewolfTransformationOverlay addOverlay(int transformationStage) {
        WerewolfTransformationOverlay.InternalCollection collection =
                GeneralHelper.safeGet(WerewolfTransformationOverlay.OVERLAYS, transformationStage);
        if (collection == null) {
            WerewolfTransformationOverlay.InternalCollection newCollection =
                    new WerewolfTransformationOverlay.InternalCollection(new ArrayDeque<>());
            newCollection.delegate.add(this);
            GeneralHelper.safeSet(WerewolfTransformationOverlay.OVERLAYS, transformationStage, newCollection);
        } else if (!collection.contains(this)) {
            collection.delegate.add(this);
        }
        return this;
    }

    public ResourceLocation getPath() {
        return this.path;
    }

    /**
     * Is called when this overlay should be drawn between x and y 0 and the given width and height.
     * Blend and alpha should always be enabled when this method is called, so they don't need to be
     * activated in this method. Additionally, {@link TextureMap#LOCATION_BLOCKS_TEXTURE} is also bound
     * before this is called, so you don't have to bind that, too. Be aware that, if you bind something
     * else, you need to rebind {@link TextureMap#LOCATION_BLOCKS_TEXTURE} in order for the other
     * overlays to work.
     */
    @SideOnly(Side.CLIENT)
    public void draw(double width, double height) {
        GlStateManager.pushMatrix();
        int overlayWidth = this.sprite.getIconWidth();
        int overlayHeight = this.sprite.getIconHeight();
        GlStateManager.scale(width / (double) overlayWidth, height / (double) overlayHeight, 1.0D);
        Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(0, 0, this.sprite, overlayWidth, overlayHeight);
        GlStateManager.popMatrix();
    }

    /**
     * Is called when {@link net.minecraftforge.client.event.TextureStitchEvent.Pre} is being fired to
     * stitch this overlay's texture onto the atlas.
     */
    @SideOnly(Side.CLIENT)
    public void stitchTexture(TextureMap textureMap) {
        this.sprite = textureMap.registerSprite(this.path);
    }

    private static class InternalCollection extends AbstractCollection<WerewolfTransformationOverlay> {
        private final Collection<WerewolfTransformationOverlay> delegate;

        private InternalCollection(Collection<WerewolfTransformationOverlay> delegate) {
            this.delegate = delegate;
        }

        @Override
        public Iterator<WerewolfTransformationOverlay> iterator() {
            return Iterators.unmodifiableIterator(this.delegate.iterator());
        }

        @Override
        public int size() {
            return this.delegate.size();
        }
    }
}
