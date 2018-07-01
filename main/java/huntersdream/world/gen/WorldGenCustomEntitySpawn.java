package huntersdream.world.gen;

import huntersdream.init.EntitySpawnInit;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class WorldGenCustomEntitySpawn {
	public static void registerEntitySpawns() {
		for (CustomEntitySpawn spawn : EntitySpawnInit.CUSTOM_ENTITY_SPAWNS) {
			EntityRegistry.addSpawn(spawn.ENTITY, spawn.PROBABILITY, spawn.MIN, spawn.MAX, spawn.CREATURE_TYPE,
					spawn.SPAWN_BIOMES);
		}
	}
}
