package theblockbox.huntersdream.util;

import com.google.common.collect.Iterators;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import theblockbox.huntersdream.util.helpers.GeneralHelper;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;

import javax.annotation.Nullable;
import java.util.*;

public class WerewolfTransformationOverlay {
    private static final List<WerewolfTransformationOverlay.InternalCollection> OVERLAYS =
            new ArrayList<>(WerewolfHelper.getAmountOfTransformationStages());
    /**
     * Internal, don't use outside of this mod
     */
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

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
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
