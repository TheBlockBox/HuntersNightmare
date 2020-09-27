package theblockbox.huntersnightmare.entity.model

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.vertex.IVertexBuilder
import net.minecraft.client.renderer.entity.model.EntityModel
import net.minecraft.client.renderer.model.ModelRenderer
import theblockbox.huntersnightmare.entity.WerewolfEntity

// Made with Blockbench 3.5.2
// Exported for Minecraft version 1.15
/**
 * @author RebelT
 */
class WerewolfModel : EntityModel<WerewolfEntity>() {
    private val body: ModelRenderer
    private val head: ModelRenderer
    private val armLeft: ModelRenderer
    private val armRight: ModelRenderer
    private val legLeft: ModelRenderer
    private val legRight: ModelRenderer
    private val tail: ModelRenderer

    override fun setRotationAngles(entity: WerewolfEntity, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float, netHeadYaw: Float, headPitch: Float) {
        //previously the render function, render code was moved to a method below
    }

    override fun render(matrixStack: MatrixStack, buffer: IVertexBuilder, packedLight: Int, packedOverlay: Int, red: Float, green: Float, blue: Float, alpha: Float) {
        body.render(matrixStack, buffer, packedLight, packedOverlay)
    }

    fun setRotationAngle(modelRenderer: ModelRenderer, x: Float, y: Float, z: Float) {
        modelRenderer.rotateAngleX = x
        modelRenderer.rotateAngleY = y
        modelRenderer.rotateAngleZ = z
    }

    init {
        textureWidth = 128
        textureHeight = 64
        body = ModelRenderer(this)
        body.setRotationPoint(0.0f, 6.0f, -12.0f)
        body.setTextureOffset(42, 0).addBox(-8.0f, -8.0f, 0.0f, 16.0f, 14.0f, 12.0f, 0.0f, false)
        body.setTextureOffset(56, 26).addBox(-6.0f, -6.0f, 12.0f, 12.0f, 12.0f, 18.0f, 0.0f, false)
        head = ModelRenderer(this)
        head.setRotationPoint(-1.0f, -1.0f, 0.0f)
        body.addChild(head)
        head.setTextureOffset(0, 0).addBox(-5.5f, -6.0f, -7.0f, 13.0f, 12.0f, 8.0f, 0.0f, false)
        head.setTextureOffset(40, 26).addBox(-2.0f, 0.0f, -13.0f, 6.0f, 6.0f, 6.0f, 0.0f, false)
        head.setTextureOffset(40, 38).addBox(3.0f, -10.0f, -2.0f, 4.0f, 4.0f, 2.0f, 0.0f, true)
        head.setTextureOffset(40, 38).addBox(-5.0f, -10.0f, -2.0f, 4.0f, 4.0f, 2.0f, 0.0f, false)
        armLeft = ModelRenderer(this)
        armLeft.setRotationPoint(3.0f, 2.0f, 4.0f)
        body.addChild(armLeft)
        armLeft.setTextureOffset(16, 20).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 16.0f, 4.0f, 0.0f, false)
        armRight = ModelRenderer(this)
        armRight.setRotationPoint(-3.0f, 2.0f, 4.0f)
        body.addChild(armRight)
        armRight.setTextureOffset(16, 20).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 16.0f, 4.0f, 0.0f, true)
        legLeft = ModelRenderer(this)
        legLeft.setRotationPoint(-3.0f, 2.0f, 26.0f)
        body.addChild(legLeft)
        legLeft.setTextureOffset(16, 20).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 16.0f, 4.0f, 0.0f, true)
        legRight = ModelRenderer(this)
        legRight.setRotationPoint(3.0f, 2.0f, 26.0f)
        body.addChild(legRight)
        legRight.setTextureOffset(16, 20).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 16.0f, 4.0f, 0.0f, false)
        tail = ModelRenderer(this)
        tail.setRotationPoint(0.0f, -4.0f, 29.0f)
        body.addChild(tail)
        setRotationAngle(tail, 0.6981f, 0.0f, 0.0f)
        tail.setTextureOffset(0, 20).addBox(-2.0f, -1.0f, -3.0f, 4.0f, 16.0f, 4.0f, 0.0f, false)
    }

    override fun setLivingAnimations(entityIn: WerewolfEntity, limbSwing: Float, limbSwingAmount: Float, partialTick: Float) {
        // Add actual animations at some point
//        // TODO: Tail swinging?
////        if (entityIn.isAngry()) {
////            tail.rotateAngleY = 0.0f
////        } else {
////            tail.rotateAngleY = MathHelper.cos(limbSwing * 0.6662f) * 1.4f * limbSwingAmount
////        }
//        body.setRotationPoint(0.0f, 14.0f, 2.0f)
//        body.rotateAngleX = Math.PI.toFloat() / 2f
////        this.mane.setRotationPoint(-1.0f, 14.0f, -3.0f)
////        this.mane.rotateAngleX = body.rotateAngleX
//        tail.setRotationPoint(-1.0f, 12.0f, 8.0f)
//        this.legRight.setRotationPoint(-2.5f, 16.0f, 7.0f)
//        this.legLeft.setRotationPoint(0.5f, 16.0f, 7.0f)
//        this.armRight.setRotationPoint(-2.5f, 16.0f, -4.0f)
//        this.armLeft.setRotationPoint(0.5f, 16.0f, -4.0f)
//        this.legRight.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f) * 1.4f * limbSwingAmount
//        this.legLeft.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f + Math.PI.toFloat()) * 1.4f * limbSwingAmount
//        this.armRight.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f + Math.PI.toFloat()) * 1.4f * limbSwingAmount
//        this.armLeft.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f) * 1.4f * limbSwingAmount
    }
}