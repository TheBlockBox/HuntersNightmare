package theblockbox.huntersdream.entity.renderer;

import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderTippedArrow;
import net.minecraft.util.ResourceLocation;
import theblockbox.huntersdream.entity.EntityBullet;

import javax.annotation.Nullable;

public class RenderBullet extends RenderArrow<EntityBullet> {
    public RenderBullet(RenderManager manager) {
        super(manager);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityBullet entity) {
        return RenderTippedArrow.RES_TIPPED_ARROW;
    }
}
