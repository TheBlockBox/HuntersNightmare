package pixeleyestudios.huntersdream.util.helpers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import pixeleyestudios.huntersdream.init.CapabilitiesInit;
import pixeleyestudios.huntersdream.util.Reference;
import pixeleyestudios.huntersdream.util.handlers.PacketHandler.Packets;
import pixeleyestudios.huntersdream.util.interfaces.ICalculateLevel;
import pixeleyestudios.huntersdream.util.interfaces.ITransformation;
import pixeleyestudios.huntersdream.util.interfaces.ITransformationPlayer;

public interface TransformationHelper {
	public enum Transformations {
		// TODO: Add levelling system for HUMAN and VAMPIRE

		HUMAN(0, p -> {
			return 0;
		}),

		WEREWOLF(1, WerewolfHelper::getWerewolfLevel, "werewolf_beta_black", "werewolf_beta_white",
				"werewolf_beta_brown"), // I've always wanted to use the :: operator

		VAMPIRE(2, p -> {
			return 0;
		});

		// when an entity has no transformation, use null
		// (no transformation meaning not infectable)

		public final int ID;
		private final ICalculateLevel CALCULATE_LEVEL;
		public final ResourceLocation[] TEXTURES;

		private Transformations(int id, ICalculateLevel calculateLevel, String... textures) {
			this.ID = id;
			this.CALCULATE_LEVEL = calculateLevel;
			TEXTURES = new ResourceLocation[textures.length];
			for (int i = 0; i < textures.length; i++) {
				TEXTURES[i] = new ResourceLocation(Reference.MODID, "textures/entity/" + textures[i] + ".png");
			}

		}

		public static Transformations fromID(int id) {
			for (Transformations transformations : values()) {
				if (transformations.ID == id) {
					return transformations;
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
	}

	/**
	 * Returns the transformation capability of the given player (just a short-cut
	 * method)
	 */
	public static ITransformationPlayer getCap(EntityPlayer player) {
		return player.getCapability(CapabilitiesInit.CAPABILITY_TRANSFORMATION_PLAYER, null);
	}

	/**
	 * Changes the player's transformation also resets xp and transformed and sends
	 * the data to the client (this method is only server side!)
	 */
	public static void changeTransformation(EntityPlayerMP player, Transformations transformation) {
		ITransformationPlayer cap = TransformationHelper.getCap(player);
		cap.setXP(0); // reset xp
		cap.setTransformed(false); // reset transformed
		cap.setTransformation(transformation);
		cap.setTextureIndex(0); // reset texture index (to avoid ArrayIndexOutOfBoundsExceptions)
		Packets.TRANSFORMATION.sync(player); // sync data with client
	}

	public static void changeTransformationWhenPossible(EntityPlayerMP player, Transformations transformation) {
		if (canChangeTransformation(player)) {
			changeTransformation(player, transformation);
		}
	}

	/**
	 * Returns true when the given entity can change transformation without rituals
	 * (e.g. by werewolf infection)
	 */
	public static boolean canChangeTransformation(EntityLivingBase entity) {
		return (getTransformation(entity) == Transformations.HUMAN);
	}

	// TODO: Add handling of transformation change
	public static void changeTransformation(EntityLivingBase entity, Transformations transformation) {

	}

	public static Transformations getTransformation(EntityLivingBase entity) {
		Transformations transformation = null;

		if (entity instanceof EntityPlayer) {
			transformation = getCap((EntityPlayer) entity).getTransformation();
		} else if (entity instanceof ITransformation) {
			transformation = ((ITransformation) entity).getTransformation();
		}
		return transformation;
	}

	public static ITransformation getITransformation(EntityLivingBase entity) {
		if (entity instanceof EntityPlayer) {
			return getCap((EntityPlayer) entity);
		} else if (entity instanceof ITransformation) {
			return (ITransformation) entity;
		}

		return null;
	}

	/**
	 * Increments the player's xp, sends a message on levelup and sends an xp packet
	 */
	public static void incrementXP(EntityPlayerMP player) {
		addXP(player, 1);
	}

	/**
	 * Adds the given xp to the player's xp, sends a message on levelup and sends an
	 * xp packet
	 */
	public static void addXP(EntityPlayerMP player, int xpToAdd) {
		ITransformationPlayer cap = getCap(player);
		int levelBefore = cap.getTransformation().getLevelFloor(player);
		cap.addXP(xpToAdd);
		int levelAfter = cap.getTransformation().getLevelFloor(player);
		if (levelBefore < levelAfter) {
			player.sendMessage(new TextComponentTranslation("transformations.onLevelUp", levelAfter));
		}
		Packets.XP.sync(player);
	}
}
