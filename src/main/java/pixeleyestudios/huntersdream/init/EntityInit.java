package pixeleyestudios.huntersdream.init;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import pixeleyestudios.huntersdream.Main;
import pixeleyestudios.huntersdream.entity.EntityGoblinTD;
import pixeleyestudios.huntersdream.entity.EntityWerewolf;
import pixeleyestudios.huntersdream.entity.EntityWerewolfVillager;
import pixeleyestudios.huntersdream.entity.renderer.RenderGoblinTD;
import pixeleyestudios.huntersdream.entity.renderer.RenderWerewolf;
import pixeleyestudios.huntersdream.entity.renderer.RenderWerewolfVillager;
import pixeleyestudios.huntersdream.util.Reference;
import pixeleyestudios.huntersdream.util.handlers.ConfigHandler;

public class EntityInit {
	public static void registerEntities() {
		registerEntity("goblintd", EntityGoblinTD.class, ConfigHandler.goblinID, 20, 29696, 255);
		registerEntity("werewolfVillager", EntityWerewolfVillager.class, ConfigHandler.werewolfVillagerID, 15, 41414,
				5252);
		registerEntity("werewolf", EntityWerewolf.class, ConfigHandler.werewolfID, 15, 3155156, 166116);
		// TODO: Change egg colour
	}

	public static void registerEntityRenders() {
		RenderingRegistry.registerEntityRenderingHandler(EntityGoblinTD.class, new IRenderFactory<EntityGoblinTD>() {
			@Override
			public Render<? super EntityGoblinTD> createRenderFor(RenderManager manager) {
				return new RenderGoblinTD(manager);
			}
		});
		RenderingRegistry.registerEntityRenderingHandler(EntityWerewolf.class, new IRenderFactory<EntityWerewolf>() {

			@Override
			public Render<? super EntityWerewolf> createRenderFor(RenderManager manager) {
				return new RenderWerewolf(manager);
			}
		});
		RenderingRegistry.registerEntityRenderingHandler(EntityWerewolfVillager.class,
				new IRenderFactory<EntityWerewolfVillager>() {

					@Override
					public Render<? super EntityWerewolfVillager> createRenderFor(RenderManager manager) {
						return new RenderWerewolfVillager(manager);
					}
				});
	}

	/*
	 * How to make entities: - register it here - make a new renderer class - make
	 * new texture in textures/entity
	 */

	private static void registerEntity(String name, Class<? extends Entity> entity, int id, int trackingRange,
			int eggColor1, int eggColor2) {
		EntityRegistry.registerModEntity(new ResourceLocation(Reference.MODID + ":" + name), entity, name, id,
				Main.instance, trackingRange, 1, true, eggColor1, eggColor2);
	}
}
