package theblockbox.huntersdream.entity.renderer;

import net.minecraft.client.renderer.entity.RenderManager;
import theblockbox.huntersdream.entity.EntityWerewolf;

public class RenderWerewolf extends RenderLycanthrope<EntityWerewolf> {

    public RenderWerewolf(RenderManager manager) {
        super(manager);
    }

    @Override
    protected boolean canRenderName(EntityWerewolf entity) {
        return false;
    }
}
