package theblockbox.huntersdream.util.helpers;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import theblockbox.huntersdream.event.TransformationXPEvent;
import theblockbox.huntersdream.init.CapabilitiesInit;
import theblockbox.huntersdream.util.ExecutionPath;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.handlers.PacketHandler.Packets;
import theblockbox.huntersdream.util.interfaces.ICalculateLevel;
import theblockbox.huntersdream.util.interfaces.ITransformation;
import theblockbox.huntersdream.util.interfaces.ITransformationPlayer;

public class TransformationHelper {
	public enum Transformations {
		// TODO: Add levelling system for HUMAN, VAMPIRE and WITCH

		HUMAN(0, p -> 0),

		WEREWOLF(1, 8, 17.5F, WerewolfHelper::getWerewolfLevel, "werewolf_beta_black", "werewolf_beta_white",
				"werewolf_beta_brown"), // I've always wanted to use the :: operator

		VAMPIRE(2, p -> 0),

		WITCH(3, p -> 0);

		// when an entity has no transformation, use null
		// (no transformation meaning not infectable)

		public final int ID;
		/**
		 * the damage that the entites should deal in half hearts or for player's the
		 * damage multiplier
		 */
		public final int GENERAL_DAMAGE;
		private final ICalculateLevel CALCULATE_LEVEL;
		public final ResourceLocation[] TEXTURES;
		public final float PROTECTION;

		private Transformations(int id, int generalDamage, float protection, ICalculateLevel calculateLevel,
				String... textures) {
			this.ID = id;
			this.CALCULATE_LEVEL = calculateLevel;
			TEXTURES = new ResourceLocation[textures.length];
			this.GENERAL_DAMAGE = generalDamage;
			this.PROTECTION = protection;
			for (int i = 0; i < textures.length; i++) {
				TEXTURES[i] = new ResourceLocation(Reference.MODID, "textures/entity/" + textures[i] + ".png");
			}
			TRANSFORMATIONS.add(this);
		}

		private Transformations(int id, ICalculateLevel calculateLevel, String... textures) {
			this(id, 1, 1F, calculateLevel, textures);
		}

		public static Transformations fromID(int id) {
			for (Transformations transformations : TRANSFORMATIONS) {
				if (transformations.ID == id) {
					return transformations;
				}
			}
			return null;
		}

		public static Transformations fromName(String name) {
			for (Transformations transformation : TRANSFORMATIONS) {
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
			return !(this == Transformations.HUMAN || this == Transformations.WITCH);
		}

		public String toStringLowerCase() {
			return toString().toLowerCase();
		}

		public ResourceLocation getXPBarTexture() {
			return new ResourceLocation(Reference.MODID,
					"textures/gui/transformation_xp_bar_" + toStringLowerCase() + ".png");
		}
	}

	public enum TransformationXPSentReason {
		WEREWOLF_HAS_KILLED(Transformations.WEREWOLF), WEREWOLF_UNDER_MOON(Transformations.WEREWOLF);

		/** The transformations that can receive xp through this cause */
		public final Transformations[] TRANSFORMATIONS;

		private TransformationXPSentReason(Transformations... transformations) {
			this.TRANSFORMATIONS = transformations;
		}

		public static boolean validReason(TransformationXPSentReason reason, Transformations transformation) {
			for (Transformations t : reason.TRANSFORMATIONS) {
				if (t == transformation) {
					return true;
				}
			}
			return false;
		}
	}

	public static final ArrayList<Transformations> TRANSFORMATIONS = new ArrayList<>();

	/**
	 * Contains entities that can be infected. The class is the entity's class and
	 * the Transformation array contains all the transformations into which the
	 * entity can transform
	 */
	public final static HashMap<Class<? extends EntityLivingBase>, Transformations[]> INFECTABLE_ENTITES = new HashMap<>();

	/**
	 * Returns the transformation capability of the given player (just a short-cut
	 * method)
	 */
	public static ITransformationPlayer getCap(EntityPlayer player) {
		return player.getCapability(CapabilitiesInit.CAPABILITY_TRANSFORMATION_PLAYER, null);
	}

	/**
	 * Changes the player's transformation also resets xp and transformed and sends
	 * the data to the client (this method is only to be called server side!)
	 */
	public static void changeTransformation(EntityPlayerMP player, Transformations transformation, ExecutionPath path) {
		ITransformationPlayer cap = getCap(player);
		cap.setXP(0); // reset xp
		cap.setTransformed(false); // reset transformed
		cap.setTransformation(transformation);
		cap.setTextureIndex(0); // reset texture index (to avoid ArrayIndexOutOfBoundsExceptions)
		Packets.TRANSFORMATION.sync(path, player); // sync data with client
	}

	// TODO: Add handling of transformation change
	public static void changeTransformation(EntityLivingBase entity, Transformations transformation,
			ExecutionPath path) {
		if (entity instanceof EntityPlayerMP) {
			changeTransformation((EntityPlayerMP) entity, transformation, path);
		} else {

		}
	}

	public static void changeTransformationWhenPossible(EntityLivingBase entity, Transformations transformation,
			ExecutionPath path) {
		if (canChangeTransformation(entity)) {
			changeTransformation(entity, transformation, path);
		}
	}

	/**
	 * Returns true when the given entity can change transformation without rituals
	 * (e.g. by werewolf infection)
	 */
	public static boolean canChangeTransformation(EntityLivingBase entity) {
		return ((getTransformation(entity) == Transformations.HUMAN)
				|| INFECTABLE_ENTITES.containsKey(entity.getClass()));
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

	public static void incrementXP(EntityPlayerMP player, TransformationXPSentReason reason, ExecutionPath path) {
		addXP(player, 1, reason, path);
	}

	public static void addXP(EntityPlayerMP player, int xpToAdd, TransformationXPSentReason reason,
			ExecutionPath path) {
		ITransformationPlayer cap = getCap(player);
		setXP(player, (cap.getXP() + xpToAdd), reason, path);
	}

	/**
	 * Sets the players xp to the given xp, sends a message on levelup and an xp
	 * packet
	 */
	public static void setXP(EntityPlayerMP player, int xp, TransformationXPSentReason reason, ExecutionPath path) {
		ITransformationPlayer cap = getCap(player);
		int levelBefore = cap.getTransformation().getLevelFloor(player);
		TransformationXPEvent event = new TransformationXPEvent(player, xp, reason);

		if (!MinecraftForge.EVENT_BUS.post(event)) {
			cap.setXP(event.getAmount());
			int levelAfter = cap.getTransformation().getLevelFloor(player);
			if (levelBefore < levelAfter) {
				player.sendMessage(new TextComponentTranslation("transformations.onLevelUp", levelAfter));
			}
			Packets.XP.sync(path, player);
		}
	}

	/**
	 * Add an entity that can be infected
	 * 
	 * @param transformations
	 *            The transformations in that the entity can transform
	 */
	public static void addInfectableEntity(Class<? extends EntityLivingBase> entity,
			Transformations... transformations) {
		if (!INFECTABLE_ENTITES.containsKey(entity)) {
			INFECTABLE_ENTITES.put(entity, transformations);
		}
	}
}
