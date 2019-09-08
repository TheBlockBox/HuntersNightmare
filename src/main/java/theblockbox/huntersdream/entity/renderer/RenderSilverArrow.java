package theblockbox.huntersdream.entity.renderer;

import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.entity.EntitySilverArrow;
import theblockbox.huntersdream.util.Reference;

import javax.annotation.Nullable;

public class RenderSilverArrow extends RenderArrow<EntitySilverArrow> {
    public static final ResourceLocation SILVER_ARROW = GeneralHelper.newResLoc(Reference.ENTITY_TEXTURE_PATH + "silver_arrow.png");

    public RenderSilverArrow(RenderManager manager) {
        super(manager);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntitySilverArrow entity) {
        return RenderSilverArrow.SILVER_ARROW;
    }
}
