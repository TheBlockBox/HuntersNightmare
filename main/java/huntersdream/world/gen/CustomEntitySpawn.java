package huntersdream.world.gen;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.Biome;

public class CustomEntitySpawn {
	public final Class<? extends EntityLiving> ENTITY;
	public final int PROBABILITY;
	/** The minium of entities spawned */
	public final int MIN;
	/** The maximum of entities spawned */
	public final int MAX;
	public final EnumCreatureType CREATURE_TYPE;
	/** Biomes in which the entity should spawn */
	public final Biome[] SPAWN_BIOMES;

	public CustomEntitySpawn(Class<? extends EntityLiving> entity, int probability, int min, int max,
			EnumCreatureType creatureType, Biome... biomes) {
		this.ENTITY = entity;
		this.PROBABILITY = probability;
		this.MIN = min;
		this.MAX = max;
		this.CREATURE_TYPE = creatureType;
		this.SPAWN_BIOMES = biomes;
	}
}
