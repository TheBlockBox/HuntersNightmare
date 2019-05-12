package theblockbox.huntersdream.entity.renderer;

import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import theblockbox.huntersdream.entity.EntityHunter;

import javax.annotation.Nullable;

public class RenderHunter extends RenderLivingBase<EntityHunter> {
    public RenderHunter(RenderManager manager, boolean smallArms) {
        super(manager, new ModelPlayer(0.0F, smallArms), 0.5F);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityHunter entity) {
        return null;
    }
}
