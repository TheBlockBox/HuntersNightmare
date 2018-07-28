package theblockbox.huntersdream.init;

import static net.minecraft.init.Biomes.BIRCH_FOREST;
import static net.minecraft.init.Biomes.BIRCH_FOREST_HILLS;
import static net.minecraft.init.Biomes.FOREST;
import static net.minecraft.init.Biomes.FOREST_HILLS;
import static net.minecraft.init.Biomes.MUTATED_BIRCH_FOREST;
import static net.minecraft.init.Biomes.MUTATED_BIRCH_FOREST_HILLS;
import static net.minecraft.init.Biomes.MUTATED_FOREST;
import static net.minecraft.init.Biomes.MUTATED_ROOFED_FOREST;
import static net.minecraft.init.Biomes.ROOFED_FOREST;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import theblockbox.huntersdream.entity.EntityGoblinTD;
import theblockbox.huntersdream.entity.EntityWerewolf;
import theblockbox.huntersdream.entity.EntityWerewolfVillager;
import theblockbox.huntersdream.entity.renderer.RenderGoblinTD;
import theblockbox.huntersdream.entity.renderer.RenderWerewolf;
import theblockbox.huntersdream.entity.renderer.RenderWerewolfVillager;
import theblockbox.huntersdream.util.Reference;

public class EntityInit {
	private static int networkID = 0;

	public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {

		// Register with egg
		registerEntity(event, "goblintd", EntityGoblinTD.class, 20, 29696, 255);

		// Register with egg and spawn
		registerEntity(event, "werewolfvillager", EntityWerewolfVillager.class, 15, 41414, 5252,
				EnumCreatureType.CREATURE, 6, 10, 100, FOREST, FOREST_HILLS, BIRCH_FOREST, BIRCH_FOREST_HILLS,
				MUTATED_BIRCH_FOREST, MUTATED_BIRCH_FOREST_HILLS, MUTATED_ROOFED_FOREST, MUTATED_FOREST, ROOFED_FOREST);

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

	/** Register with egg and spawn biomes */
	private static void registerEntity(RegistryEvent.Register<EntityEntry> event, String name,
			Class<? extends Entity> entity, int trackingRange, int eggColor1, int eggColor2, EnumCreatureType type,
			int weight, int min, int max, Biome... spawnBiomes) {
		EntityEntry entry = EntityEntryBuilder.create().entity(entity)
				.id(new ResourceLocation(Reference.MODID, name), networkID++).name(name)
				.tracker(trackingRange, 20, false).egg(eggColor1, eggColor2).spawn(type, weight, min, max, spawnBiomes)
				.build();
		event.getRegistry().register(entry);
	}
}
