package theblockbox.huntersdream.init;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import theblockbox.huntersdream.entity.EntityGoblinTD;
import theblockbox.huntersdream.entity.EntityWerewolf;
import theblockbox.huntersdream.entity.renderer.RenderGoblinTD;
import theblockbox.huntersdream.entity.renderer.RenderWerewolf;
import theblockbox.huntersdream.util.Reference;

public class EntityInit {
	private static int networkID = 0;

	public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {

		// Register with egg
		registerEntity(event, "goblintd", EntityGoblinTD.class, 20, 29696, 255);

		// Register without egg
		registerEntity(event, "werewolf", EntityWerewolf.class, 20);
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
	}

	/*
	 * How to make entities: - register it here - make a new renderer class - make
	 * new texture in textures/entity
	 */

	/** Register with egg */
	private static void registerEntity(RegistryEvent.Register<EntityEntry> event, String name,
			Class<? extends Entity> entity, int trackingRange, int eggColor1, int eggColor2) {
		EntityEntry entry = EntityEntryBuilder.create().entity(entity)
				.id(new ResourceLocation(Reference.MODID, name), networkID++).name(name)
				.tracker(trackingRange, 20, false).egg(eggColor1, eggColor2).build();
		event.getRegistry().register(entry);
	}

	/** Register without egg */
	private static void registerEntity(RegistryEvent.Register<EntityEntry> event, String name,
			Class<? extends Entity> entity, int trackingRange) {
		EntityEntry entry = EntityEntryBuilder.create().entity(entity)
				.id(new ResourceLocation(Reference.MODID, name), networkID++).name(name)
				.tracker(trackingRange, 20, false).build();
		event.getRegistry().register(entry);
	}
}
