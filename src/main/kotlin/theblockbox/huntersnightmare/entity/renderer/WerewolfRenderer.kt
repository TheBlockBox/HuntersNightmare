package theblockbox.huntersnightmare.entity.renderer

import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.util.ResourceLocation
import theblockbox.huntersnightmare.HuntersNightmare
import theblockbox.huntersnightmare.entity.WerewolfEntity
import theblockbox.huntersnightmare.entity.model.WerewolfModel

class WerewolfRenderer(manager: EntityRendererManager) : MobRenderer<WerewolfEntity, WerewolfModel>(manager, WerewolfModel(), 0.5F) {
    companion object {
        private val TEXTURE = ResourceLocation(HuntersNightmare.MODID, "textures/entity/werewolf.png")
    }

    override fun getEntityTexture(entity: WerewolfEntity): ResourceLocation {
        return TEXTURE
    }
}