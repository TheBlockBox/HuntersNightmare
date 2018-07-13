package pixeleyestudios.huntersdream.init;

import static net.minecraft.init.Biomes.BIRCH_FOREST;
import static net.minecraft.init.Biomes.BIRCH_FOREST_HILLS;
import static net.minecraft.init.Biomes.FOREST;
import static net.minecraft.init.Biomes.FOREST_HILLS;
import static net.minecraft.init.Biomes.MUTATED_BIRCH_FOREST;
import static net.minecraft.init.Biomes.MUTATED_BIRCH_FOREST_HILLS;
import static net.minecraft.init.Biomes.MUTATED_FOREST;
import static net.minecraft.init.Biomes.MUTATED_ROOFED_FOREST;
import static net.minecraft.init.Biomes.ROOFED_FOREST;

import java.util.ArrayList;

import net.minecraft.entity.EnumCreatureType;
import pixeleyestudios.huntersdream.entity.EntityWerewolf;
import pixeleyestudios.huntersdream.world.gen.CustomEntitySpawn;

public class EntitySpawnInit {
	public static final ArrayList<CustomEntitySpawn> CUSTOM_ENTITY_SPAWNS = new ArrayList<>();

	public static final CustomEntitySpawn SPAWN_VILLAGER_WEREWOLF = new CustomEntitySpawn(EntityWerewolf.class,
			1, 10, 50, EnumCreatureType.CREATURE, FOREST, FOREST_HILLS, BIRCH_FOREST, BIRCH_FOREST_HILLS,
			MUTATED_BIRCH_FOREST, MUTATED_BIRCH_FOREST_HILLS, MUTATED_ROOFED_FOREST, MUTATED_FOREST, ROOFED_FOREST);
	// TODO: Add better probability and min max
}
