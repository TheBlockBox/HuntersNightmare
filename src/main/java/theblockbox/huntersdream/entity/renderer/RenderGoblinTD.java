package theblockbox.huntersdream.entity.renderer;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.entity.EntityGoblinTD;
import theblockbox.huntersdream.entity.model.ModelGoblinTD;
import theblockbox.huntersdream.util.Reference;

public class RenderGoblinTD extends RenderLiving<EntityGoblinTD> {
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[EntityGoblinTD.TEXTURES];

    static {
        for (int i = 0; i < EntityGoblinTD.TEXTURES; i++) {
            RenderGoblinTD.TEXTURES[i] = GeneralHelper.newResLoc(Reference.ENTITY_TEXTURE_PATH + "goblin" + i + ".png");
        }
    }

    public RenderGoblinTD(RenderManager manager) {
        super(manager, new ModelGoblinTD(), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityGoblinTD entity) {
        return RenderGoblinTD.TEXTURES[entity.getTexture()];
    }
}