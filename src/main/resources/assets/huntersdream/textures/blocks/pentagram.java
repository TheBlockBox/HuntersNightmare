package Pentagram;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * pentagram - RebelT
 * Created using Tabula 7.1.0
 */
public class pentagram extends ModelBase {
    public ModelRenderer pentagram;

    public pentagram() {
        this.textureWidth = 96;
        this.textureHeight = 64;
        this.pentagram = new ModelRenderer(this, -48, 0);
        this.pentagram.setRotationPoint(0.0F, 23.5F, 0.0F);
        this.pentagram.addBox(-24.0F, 0.0F, -24.0F, 48, 0, 48, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.pentagram.render(f5);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
