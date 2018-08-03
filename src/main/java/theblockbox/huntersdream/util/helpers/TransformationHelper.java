package theblockbox.huntersdream.util.helpers;

import java.util.HashMap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import theblockbox.huntersdream.event.TransformationXPEvent;
import theblockbox.huntersdream.event.TransformationXPEvent.TransformationXPSentReason;
import theblockbox.huntersdream.init.CapabilitiesInit;
import theblockbox.huntersdream.util.ExecutionPath;
import theblockbox.huntersdream.util.enums.Transformations;
import theblockbox.huntersdream.util.handlers.PacketHandler.Packets;
import theblockbox.huntersdream.util.interfaces.effective.ArmorEffectiveAgainstTransformation;
import theblockbox.huntersdream.util.interfaces.effective.EffectiveAgainstTransformation;
import theblockbox.huntersdream.util.interfaces.effective.EffectiveAgainstTransformation.EntityEffectiveAgainstTransformation;
import theblockbox.huntersdream.util.interfaces.effective.EffectiveAgainstTransformation.ItemEffectiveAgainstTransformation;
import theblockbox.huntersdream.util.interfaces.effective.IArmorEffectiveAgainstTransformation;
import theblockbox.huntersdream.util.interfaces.effective.IEffectiveAgainstTransformation;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformation;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationCreature;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

public class TransformationHelper {

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
	 * Returns true when the object is either an instance of
	 * {@link IEffectiveAgainstTransformation} or registered through
	 * {@link #addEffectiveAgainst(Object, float, Transformations...)}
	 */
	public static boolean effectiveAgainstTransformation(Transformations effectiveAgainst, Object object) {
		notItemstack(object);
		if (object instanceof IEffectiveAgainstTransformation) {
			return ((IEffectiveAgainstTransformation) object).effectiveAgainst(effectiveAgainst);
		} else {
			EffectiveAgainstTransformation<?> eat = EffectiveAgainstTransformation.getFromObject(object);
			if (eat == null) {
				return false;
			} else {
				return eat.effectiveAgainst(effectiveAgainst);
			}
		}
	}

	/**
	 * Returns true when the object is either an instance of
	 * {@link IArmorEffectiveAgainstTransformation} or registered through
	 * {@link #addArmorEffectiveAgainst(Item, float, float, Transformations...)}
	 */
	public static boolean armorEffectiveAgainstTransformation(Transformations effectiveAgainst, Item armorPart) {
		if (armorPart instanceof IArmorEffectiveAgainstTransformation) {
			return ((IArmorEffectiveAgainstTransformation) armorPart).effectiveAgainst(effectiveAgainst);
		} else {
			ArmorEffectiveAgainstTransformation aeat = ArmorEffectiveAgainstTransformation.getFromArmor(armorPart);
			if (aeat == null) {
				return false;
			} else {
				return aeat.effectiveAgainst(effectiveAgainst);
			}
		}
	}

	public static IArmorEffectiveAgainstTransformation getAEAT(Item armorPart) {
		if (armorPart instanceof IArmorEffectiveAgainstTransformation) {
			return ((IArmorEffectiveAgainstTransformation) armorPart);
		} else {
			IArmorEffectiveAgainstTransformation aeat = ArmorEffectiveAgainstTransformation.getFromArmor(armorPart);
			if (aeat == null) {
				throw new IllegalArgumentException("Given armor is not effective against any transformation");
			} else {
				return aeat;
			}
		}
	}

	public static float armorGetProtectionAgainst(Transformations against, Item armorPart) {
		if (armorEffectiveAgainstTransformation(against, armorPart)) {
			return getAEAT(armorPart).getProtection();
		} else {
			throw new IllegalArgumentException("Given armor is not effective against the given transformation");
		}
	}

	/**
	 * Effectiveness = thorns (more info here:
	 * {@link ArmorEffectiveAgainstTransformation#ArmorEffectiveAgainstTransformation(Item, float, float, Transformations...)})
	 */
	public static float armorGetEffectivenessAgainst(Transformations against, Item armorPart) {
		if (armorEffectiveAgainstTransformation(against, armorPart)) {
			return getAEAT(armorPart).getArmorEffectiveness();
		} else {
			throw new IllegalArgumentException("Given armor is not effective against the given transformation");
		}
	}

	/**
	 * (For armor parts, see
	 */
	public static float getEffectivenessAgainst(Transformations effectiveAgainst, Object object) {
		notItemstack(object);
		if (effectiveAgainstTransformation(effectiveAgainst, object)) {
			if (object instanceof IEffectiveAgainstTransformation) {
				return ((IEffectiveAgainstTransformation) object).getEffectiveness();
			} else {
				// this should NOT cause a NullPointerException
				return EffectiveAgainstTransformation.getFromObject(object).getEffectiveness();
			}
		} else {
			throw new IllegalArgumentException("The given object is not effective against the given transformation ("
					+ effectiveAgainst.toString() + ")");
		}
	}

	public static <T> void addEffectiveAgainst(T object, float effectiveness, Transformations... effectiveAgainst) {
		if (object instanceof Entity) {
			new EntityEffectiveAgainstTransformation((Entity) object, effectiveness, effectiveAgainst);
		} else if (object instanceof Item) {
			new ItemEffectiveAgainstTransformation((Item) object, effectiveness, effectiveAgainst);
		} else {
			throw new UnsupportedOperationException("The given object is not of type item or entity");
		}
	}

	/**
	 * For more info about the parameters see
	 * {@link ArmorEffectiveAgainstTransformation#ArmorEffectiveAgainstTransformation(Item, float, float, Transformations...)}
	 */
	public static void addArmorEffectiveAgainst(Item armorPart, float protection, float effectiveness,
			Transformations... effectiveAgainst) {
		new ArmorEffectiveAgainstTransformation(armorPart, effectiveness, protection, effectiveAgainst);
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

	public static ITransformationCreature getITransformationCreature(EntityCreature entity) {
		if (entity instanceof ITransformationCreature) {
			return (ITransformationCreature) entity;
		} else {
			return entity.getCapability(CapabilitiesInit.CAPABILITY_TRANSFORMATION_CREATURE, null);
		}
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
	 * @param transformations The transformations in that the entity can transform
	 */
	public static void addInfectableEntity(Class<? extends EntityLivingBase> entity,
			Transformations... transformations) {
		if (!INFECTABLE_ENTITES.containsKey(entity)) {
			INFECTABLE_ENTITES.put(entity, transformations);
		}
	}

	public static void notItemstack(Object object) {
		if (object instanceof ItemStack) {
			throw new IllegalArgumentException("Use ItemStack#getItem, instead of ItemStack");
		}
	}

	public static Transformations getTransformedTransformation(EntityLivingBase entity) {
		if (entity instanceof EntityPlayer) {
			return getCap((EntityPlayer) entity).getTransformation();
		} else if (entity instanceof ITransformationCreature) {
			return ((ITransformationCreature) entity).getCurrentTransformation();
		} else if (entity instanceof ITransformation) {
			return ((ITransformation) entity).getTransformation();
		} else {
			return null;
		}
	}
}
