package theblockbox.huntersdream.entity.renderer;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.entity.EntityHunter;
import theblockbox.huntersdream.entity.model.ModelHunter;
import theblockbox.huntersdream.util.Reference;

import javax.annotation.Nullable;

public class RenderHunter extends RenderLivingBase<EntityHunter> {
    public static final ResourceLocation HUNTER_TEXTURE = GeneralHelper.newResLoc(Reference.ENTITY_TEXTURE_PATH + "hunter.png");

    public RenderHunter(RenderManager manager) {
        super(manager, new ModelHunter(), 0.5F);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityHunter entity) {
        return RenderHunter.HUNTER_TEXTURE;
    }

    @Override
    protected boolean canRenderName(EntityHunter entity) {
        return false;
    }
}
