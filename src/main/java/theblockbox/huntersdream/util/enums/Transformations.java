package theblockbox.huntersdream.util.enums;

import java.util.ArrayList;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;
import theblockbox.huntersdream.util.interfaces.ICalculateLevel;
import theblockbox.huntersdream.util.interfaces.ITransformCreature;

public enum Transformations {

	// TODO: Add levelling system for HUMAN, VAMPIRE and WITCH

	HUMAN(0, 1, 1F, Helper.DEFAULT_CALCULATE_LEVEL, false, null),

	WEREWOLF(1, 8, 17.5F, WerewolfHelper::getWerewolfLevel, WerewolfHelper::toWerewolfWhenNight, "werewolf_beta_white",
			"werewolf_beta_brown", "werewolf_beta_black"),
	// I've always wanted to use the :: operator

	VAMPIRE(2, Helper.DEFAULT_CALCULATE_LEVEL, null),

	WITCH(3, 1, 1F, Helper.DEFAULT_CALCULATE_LEVEL, false, null),

	CLOCKWORKANDROID(4, Helper.DEFAULT_CALCULATE_LEVEL, null),

	HYBRID(5, Helper.DEFAULT_CALCULATE_LEVEL, null);

	// when an entity has no transformation, use null
	// (no transformation meaning not infectable)

	private static class Helper {
		private static final ArrayList<Transformations> TRANSFORMATIONS = new ArrayList<>();
		private static final ICalculateLevel DEFAULT_CALCULATE_LEVEL = player -> {
			return TransformationHelper.getCap(player).getXP() / 500;
		};
		private static int currentTransformationID = 0;
	}

	public final int ID;
	/**
	 * the damage that the entites should deal in half hearts or for player's the
	 * damage multiplier
	 */
	public final int GENERAL_DAMAGE;
	private final ICalculateLevel CALCULATE_LEVEL;
	public final ResourceLocation[] TEXTURES;
	public final float PROTECTION;
	private final boolean SUPER_NATURAL;
	private final ITransformCreature TRANSFORM_CREATURE;

	private Transformations(int id, int generalDamage, float protection, ICalculateLevel calculateLevel,
			boolean supernatural, ITransformCreature transformCreature, String... textures) {
		this.ID = id;
		this.CALCULATE_LEVEL = calculateLevel;
		TEXTURES = new ResourceLocation[textures.length];
		this.GENERAL_DAMAGE = generalDamage;
		this.PROTECTION = protection;
		this.SUPER_NATURAL = supernatural;
		for (int i = 0; i < textures.length; i++) {
			TEXTURES[i] = new ResourceLocation(Reference.MODID, "textures/entity/" + textures[i] + ".png");
		}
		this.TRANSFORM_CREATURE = transformCreature;
		Helper.TRANSFORMATIONS.add(this);
		getNewTransformationID();
	}

	public static int getNewTransformationID() {
		return Helper.currentTransformationID++;
	}

	private Transformations(int id, int generalDamage, float protection, ICalculateLevel calculateLevel,
			ITransformCreature transformCreature, String... textures) {
		this(id, generalDamage, protection, calculateLevel, true, transformCreature, textures);
	}

	private Transformations(int id, ICalculateLevel calculateLevel, ITransformCreature transformCreature,
			String... textures) {
		this(id, 1, 1F, calculateLevel, true, transformCreature, textures);
	}

	public static Transformations fromID(int id) {
		for (Transformations transformations : Helper.TRANSFORMATIONS) {
			if (transformations.ID == id) {
				return transformations;
			}
		}
		return null;
	}

	public static Transformations fromName(String name) {
		for (Transformations transformation : Helper.TRANSFORMATIONS) {
			if (transformation.toString().equalsIgnoreCase(name)) {
				return transformation;
			}
		}
		return null;
	}

	public double getLevel(EntityPlayer player) {
		return CALCULATE_LEVEL.getLevel(player);
	}

	public int getLevelFloor(EntityPlayer player) {
		return MathHelper.floor(getLevel(player));
	}

	public double getPercentageToNextLevel(EntityPlayer player) {
		return getLevel(player) - getLevelFloor(player);
	}

	public boolean isSupernatural() {
		return SUPER_NATURAL;
	}

	public String toStringLowerCase() {
		return toString().toLowerCase();
	}

	public ResourceLocation getXPBarTexture() {
		return new ResourceLocation(Reference.MODID,
				"textures/gui/transformation_xp_bar_" + toStringLowerCase() + ".png");
	}

	public void transformCreatureWhenPossible(EntityCreature creature) {
		if (TRANSFORM_CREATURE != null) {
			this.TRANSFORM_CREATURE.transformCreature(creature);
		}
	}
}
