package theblockbox.huntersdream.entity.renderer;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.api.helpers.TransformationHelper;
import theblockbox.huntersdream.api.interfaces.transformation.ITransformation;
import theblockbox.huntersdream.entity.EntityWerewolf;
import theblockbox.huntersdream.entity.model.ModelWerewolf;

import java.util.Optional;

public abstract class RenderLycanthrope<T extends EntityLivingBase> extends RenderLivingBase<T> {
    public RenderLycanthrope(RenderManager manager) {
        super(manager, new ModelWerewolf(), 0.5F);
    }

    public static boolean isAlex(Entity entity) {
        if (entity instanceof AbstractClientPlayer) {
            return "slim".equals(((AbstractClientPlayer) entity).getSkinType());
        } else if (entity instanceof EntityWerewolf) {
            return ((EntityWerewolf) entity).usesAlexSkin();
        } else {
            return false;
        }
    }

    @Override
    public ResourceLocation getEntityTexture(T entity) {
        Optional<ITransformation> transformation = TransformationHelper.getITransformation(entity);
        return Transformation.WEREWOLF.getTextures()[(transformation.isPresent() ? transformation.get().getTextureIndex()
                : 0)];
    }
}