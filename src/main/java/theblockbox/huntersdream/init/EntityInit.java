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
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import theblockbox.huntersdream.entity.EntityChair;
import theblockbox.huntersdream.entity.EntityGoblinTD;
import theblockbox.huntersdream.entity.EntityWerewolf;
import theblockbox.huntersdream.entity.renderer.RenderGoblinTD;
import theblockbox.huntersdream.entity.renderer.RenderWerewolf;
import theblockbox.huntersdream.util.helpers.GeneralHelper;

public class EntityInit {
	private static int networkID = 0;

	public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
		event.getRegistry().registerAll(
				getEntityEntryBuilder("goblintd", EntityGoblinTD.class).egg(29696, 255).tracker(20, 20, false).build(),
				getEntityEntryBuilder("werewolf", EntityWerewolf.class).tracker(20, 20, false)
						.spawn(EnumCreatureType.CREATURE, 6, 20, 150, FOREST, FOREST_HILLS, BIRCH_FOREST,
								BIRCH_FOREST_HILLS, MUTATED_BIRCH_FOREST, MUTATED_BIRCH_FOREST_HILLS,
								MUTATED_ROOFED_FOREST, MUTATED_FOREST, ROOFED_FOREST)
						.build(),
				getEntityEntryBuilder("chair", EntityChair.class).build());
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

	private static EntityEntryBuilder<Entity> getEntityEntryBuilder(String name, Class<? extends Entity> clazz) {
		return EntityEntryBuilder.create().entity(clazz).id(GeneralHelper.newResLoc(name), networkID++).name(name);
	}
}
