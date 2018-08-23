package theblockbox.huntersdream.util.helpers;

import java.util.HashMap;
import java.util.function.Function;

import javax.annotation.Nonnull;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.event.TransformationXPEvent;
import theblockbox.huntersdream.event.TransformationXPEvent.TransformationXPSentReason;
import theblockbox.huntersdream.init.CapabilitiesInit;
import theblockbox.huntersdream.util.enums.Rituals;
import theblockbox.huntersdream.util.enums.Transformations;
import theblockbox.huntersdream.util.exceptions.WrongSideException;
import theblockbox.huntersdream.util.handlers.ConfigHandler;
import theblockbox.huntersdream.util.handlers.PacketHandler.Packets;
import theblockbox.huntersdream.util.interfaces.IInfectInTicks;
import theblockbox.huntersdream.util.interfaces.IInfectOnNextMoon;
import theblockbox.huntersdream.util.interfaces.IInfectOnNextMoon.InfectionStatus;
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
	 * special damage source for things that are effective against specific
	 * transformations
	 */
	public static final DamageSource EFFECTIVE_AGAINST_TRANSFORMATION = new DamageSource(
			"effectiveAgainstTransformation");

	/**
	 * Returns the transformation capability of the given player (just a short-cut
	 * method)
	 */
	public static ITransformationPlayer getCap(EntityPlayer player) {
		if (player != null)
			return player.getCapability(CapabilitiesInit.CAPABILITY_TRANSFORMATION_PLAYER, null);
		else
			throw new NullPointerException("Player is null");
	}

	/**
	 * Changes the player's transformation also resets xp and transformed and sends
	 * the data to the client (this method is only to be called server side!)
	 */
	public static void changeTransformation(EntityPlayerMP player, @Nonnull Transformations transformation) {
		if (transformation == null)
			throw new NullPointerException("Null not allowed here");
		ITransformationPlayer cap = getCap(player);
		cap.setXP(0); // reset xp
		cap.setRituals(new Rituals[0]); // reset rituals
		cap.setLevel(0);
		cap.setTransformed(false); // reset transformed
		cap.setTransformation(transformation);
		cap.setTextureIndex(cap.getTransformation().getRandomTextureIndex());
		if (ConfigHandler.showPacketMessages)
			Main.getLogger()
					.info("Transformation of player " + player.getName() + " changed to " + transformation.toString());
		Packets.TRANSFORMATION.sync(player); // sync data with client
	}

	public static void changeTransformation(@Nonnull EntityLivingBase entity, @Nonnull Transformations transformation) {
		if (entity != null && transformation != null) {
			if (entity instanceof EntityPlayer) {
				if (entity instanceof EntityPlayerMP) {
					changeTransformation((EntityPlayerMP) entity, transformation);
				} else {
					throw new WrongSideException("You can only change transformation on server side", Side.CLIENT);
				}
			} else {
				getITransformation(entity).setTransformation(transformation);
				ITransformationCreature tc = getITransformationCreature(entity);
				if (tc != null) {
					tc.setTextureIndex(tc.getTransformation().getRandomTextureIndex());
				}
			}
		} else {
			throw new NullPointerException("A null argument was passed. Entity null: " + (entity == null)
					+ " Transformation null: " + (transformation == null));
		}
	}

	public static void changeTransformationWhenPossible(@Nonnull EntityLivingBase entity,
			@Nonnull Transformations transformation) {
		if (transformation == null || entity == null) {
			throw new NullPointerException("A null argument was passed. Entity null: " + (entity == null)
					+ " Transformation null: " + (transformation == null));
		}
		if (canChangeTransformation(entity)) {
			changeTransformation(entity, transformation);
		}
	}

	/**
	 * Returns true when the given entity can change transformation without rituals
	 * (e.g. by werewolf infection)
	 */
	public static boolean canChangeTransformation(EntityLivingBase entity) {
		return canChangeTransformationOnInfection(entity) && !isInfected(entity);
	}

	public static boolean canChangeTransformationOnInfection(EntityLivingBase entity) {
		Transformations transformation = getTransformation(entity);
		return ((transformation == Transformations.HUMAN) || (transformation == Transformations.HUNTER)
				|| (INFECTABLE_ENTITES.containsKey(entity.getClass())) && transformation != null);
	}

	public static Transformations getTransformation(EntityLivingBase entity) {
		if (entity == null) {
			return null;
		} else {
			ITransformation transformation = getITransformation(entity);
			if (transformation == null) {
				return null;
			} else {
				return transformation.getTransformation();
			}
		}
	}

	public static ITransformation getITransformation(EntityLivingBase entity) {
		if (entity instanceof EntityPlayer) {
			return getCap((EntityPlayer) entity);
		} else if (entity instanceof ITransformation) {
			return (ITransformation) entity;
		} else {
			return getITransformationCreature(entity);
		}
	}

	public static ITransformationCreature getITransformationCreature(EntityLivingBase entity) {
		if (entity instanceof EntityCreature && entity != null) {
			if (entity instanceof ITransformationCreature) {
				return (ITransformationCreature) entity;
			} else {
				return entity.getCapability(CapabilitiesInit.CAPABILITY_TRANSFORMATION_CREATURE, null);
			}
		} else {
			return null;
		}
	}

	public static void incrementXP(EntityPlayerMP player, TransformationXPSentReason reason) {
		addXP(player, 1, reason);
	}

	public static void addXP(EntityPlayerMP player, int xpToAdd, TransformationXPSentReason reason) {
		ITransformationPlayer cap = getCap(player);
		setXP(player, (cap.getXP() + xpToAdd), reason);
	}

	/**
	 * Sets the players xp to the given xp, sends a message on levelup and an xp
	 * packet
	 */
	public static void setXP(EntityPlayerMP player, int xp, TransformationXPSentReason reason) {
		ITransformationPlayer cap = getCap(player);
		int levelBefore = cap.getLevelFloor();
		TransformationXPEvent event = new TransformationXPEvent(player, xp, reason);

		if (!MinecraftForge.EVENT_BUS.post(event)) {
			cap.setXP(event.getAmount());
			cap.setLevel(cap.getTransformation().getLevel((EntityPlayerMP) player));
			int levelAfter = cap.getLevelFloor();
			if (levelBefore < levelAfter) {
				player.sendMessage(new TextComponentTranslation("transformations.huntersdream.onLevelUp", levelAfter));
				cap.getTransformation().onLevelUp(player, levelAfter);
			}
			Packets.XP.sync(player);
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

	public static boolean transformedTransformation(EntityLivingBase entity, Transformations transformation) {
		if (entity == null)
			return false;
		else
			return (getTransformation(entity) == (transformation)) && getITransformation(entity).transformed();
	}

	public static boolean canBeInfectedWith(Transformations infection, EntityLivingBase entity) {
		if (canChangeTransformation(entity)) {
			IInfectInTicks iit = getIInfectInTicks(entity);
			IInfectOnNextMoon ionm = WerewolfHelper.getIInfectOnNextMoon(entity);
			if (ionm != null) {
				if (infection == Transformations.WEREWOLF) {
					return true;
				}
			}
			if (iit != null) {
				ITransformationCreature tc = getITransformationCreature(entity);
				if (tc != null) {
					return tc.notImmuneToTransformation(infection);
				} else {
					return true;
				}
			}

		}
		return false;
	}

	public static boolean onInfectionCanBeInfectedWith(Transformations infection, EntityLivingBase entity) {
		if (canChangeTransformationOnInfection(entity)) {
			ITransformationCreature tc = getITransformationCreature(entity);
			if (tc != null) {
				return tc.notImmuneToTransformation(infection);
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	public static IInfectInTicks getIInfectInTicks(EntityLivingBase entity) {
		return entity.getCapability(CapabilitiesInit.CAPABILITY_INFECT_IN_TICKS, null);
	}

	public static void infectIn(int ticksUntilInfection, EntityLivingBase entityToBeInfected,
			Transformations infectTo) {
		IInfectInTicks iit = getIInfectInTicks(entityToBeInfected);
		if (iit != null) {
			iit.setTime(ticksUntilInfection);
			iit.setCurrentlyInfected(true);
			iit.setInfectionTransformation(infectTo);
		} else {
			throw new IllegalArgumentException(
					"The given entity does not have the capability IInfectInTicks/infectinticks");
		}
	}

	public static boolean isInfected(EntityLivingBase entity) {
		IInfectInTicks iit = getIInfectInTicks(entity);
		IInfectOnNextMoon ionm = WerewolfHelper.getIInfectOnNextMoon(entity);
		boolean flag = false;

		if (iit != null) {
			flag = iit.currentlyInfected();
		}

		if (ionm != null) {
			if (!flag) {
				flag = !(ionm.getInfectionStatus() == InfectionStatus.NOT_INFECTED);
			}
		}
		return flag;
	}

	public static void refreshLevel(EntityPlayerMP player) {
		ITransformationPlayer cap = getCap(player);
		cap.setLevel(cap.getTransformation().getLevel(player));
	}

	public static void transformCreature(EntityLiving entity,
			Function<EntityLiving, EntityLivingBase> transformCreature) {
		EntityLivingBase returned = transformCreature.apply(entity);
		if (returned != null) {
			World world = returned.world;
			returned.setPosition(entity.posX, entity.posY, entity.posZ);
			returned.setHealth(returned.getMaxHealth() / (entity.getMaxHealth() / entity.getHealth()));
			// 10 / 30 : 15 / 45
			// 45 / ( 30 / 10 ) = 45 / 3 = 15
			world.removeEntity(entity);
			world.spawnEntity(returned);
			returned.setPositionAndUpdate(entity.posX, entity.posY, entity.posZ);
		}
	}
}
