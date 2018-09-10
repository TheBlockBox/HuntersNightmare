package theblockbox.huntersdream.util.interfaces.transformation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import theblockbox.huntersdream.event.ExtraDataEvent;
import theblockbox.huntersdream.event.TransformingEvent;
import theblockbox.huntersdream.event.TransformingEvent.TransformingEventReason;
import theblockbox.huntersdream.util.enums.Transformations;
import theblockbox.huntersdream.util.exceptions.WrongSideException;
import theblockbox.huntersdream.util.helpers.TransformationHelper;

/**
 * The name may seem a bit weird. This interface is for entities that
 * are/represent the transformed form (for example werewolf counts, werewolf
 * villager does not)
 */
public interface ITransformationEntityTransformed extends ITransformation {
	@Override
	default boolean transformed() {
		return true;
	}

	@Override
	default void setTransformed(boolean transformed) {
		throw new UnsupportedOperationException("This entity is always transformed");
	}

	@Override
	default void setTransformation(Transformations transformation) {
		throw new UnsupportedOperationException("This creature's transformation is already determined");
	}

	/** Returns the untransformed entity's extra data */
	public NBTTagCompound getExtraData();

	/** Sets the untransformed entity's extra data */
	public void setExtraData(NBTTagCompound extraData);

	/**
	 * Used to get the entity before the transformation. If the returned string
	 * starts with "player", the entity's untransformed form is a player (Caution:
	 * This may change after the new no control system is done). If the string
	 * starts with $bycap, the extra data (texture index, transformation etc.) will
	 * be written in the entity's TransformationCreature capability, otherwise if
	 * the name doesn't start with $bycap, the information will be passed to the
	 * constructor
	 */
	public String getUntransformedEntityName();

	public static <T extends EntityCreature & ITransformationEntityTransformed> void transformBack(
			T toBeTransformedBack) {
		if (!toBeTransformedBack.world.isRemote) {
			if (!MinecraftForge.EVENT_BUS
					.post(new TransformingEvent(toBeTransformedBack, true, TransformingEventReason.ENVIROMENT))) {
				EntityCreature e = null;
				String entityName = toBeTransformedBack.getUntransformedEntityName();

				// TODO: Remove this when no control is done (only the line below and its
				// bracket)
				if (!entityName.startsWith("player")) {

					if (!entityName.startsWith("$bycap")) {
						try {
							@SuppressWarnings("unchecked")
							Class<? extends Entity> entityClass = (Class<? extends Entity>) Class.forName(entityName);
							Constructor<?> constructor = entityClass.getConstructor(World.class, int.class,
									Transformations.class);
							e = (EntityCreature) constructor.newInstance(toBeTransformedBack.world,
									toBeTransformedBack.getTextureIndex(), toBeTransformedBack.getTransformation());
						} catch (ClassNotFoundException ex) {
							throw new NullPointerException("Can't find class " + entityName);
						} catch (ClassCastException ex) {
							throw new IllegalArgumentException("Given class " + entityName + " is not an entity");
						} catch (NoSuchMethodException | InvocationTargetException | SecurityException
								| IllegalAccessException ex) {
							throw new NullPointerException("Class " + entityName
									+ " does not have an accessible constructor with parameters World, int, Transformations");
						} catch (InstantiationException ex) {
							throw new IllegalArgumentException("Can't instantiate class " + entityName);
						}
					} else {
						String eName = entityName.substring(6);
						try {
							@SuppressWarnings("unchecked")
							Class<? extends Entity> entityClass = (Class<? extends Entity>) Class.forName(eName);
							Constructor<?> constructor = entityClass.getConstructor(World.class);
							e = (EntityCreature) constructor.newInstance(toBeTransformedBack.world);
							// remember: this is only server side and the client doesn't actually need to
							// know about this
							ITransformationCreature transformation = TransformationHelper
									.getITransformationCreature((EntityCreature) e);
							transformation.setTextureIndex(toBeTransformedBack.getTextureIndex());
							transformation.setTransformation(toBeTransformedBack.getTransformation());
						} catch (NullPointerException ex) {
							NullPointerException exception = new NullPointerException(
									"Either the entity's capability or something else was null");
							exception.setStackTrace(ex.getStackTrace());
							throw exception;
						} catch (ClassNotFoundException ex) {
							throw new NullPointerException("Can't find class " + eName);
						} catch (ClassCastException ex) {
							throw new IllegalArgumentException("Given class " + eName + " is not an entity");
						} catch (NoSuchMethodException | SecurityException | IllegalAccessException
								| InstantiationException | InvocationTargetException ex) {
							throw new IllegalArgumentException(
									"This entity does not have an accessible constructor and is therefore not registered");
						}
					}

					applyExtraData(e, toBeTransformedBack.getExtraData(), toBeTransformedBack);
					e.setPosition(toBeTransformedBack.posX, toBeTransformedBack.posY, toBeTransformedBack.posZ);
					toBeTransformedBack.world.spawnEntity(e);
				}

				toBeTransformedBack.world.removeEntity(toBeTransformedBack);
			}
		} else {
			throw new WrongSideException("This method is server side only", toBeTransformedBack.world);
		}
	}

	public static void applyExtraData(EntityCreature entity, NBTTagCompound extraData, EntityCreature otherEntity) {
		if (entity instanceof EntityCreature) {
			extraData.setFloat("Health",
					entity.getMaxHealth() / (otherEntity.getMaxHealth() / otherEntity.getHealth()));
			ExtraDataEvent extraDataEvent = new ExtraDataEvent(entity, extraData, false);
			MinecraftForge.EVENT_BUS.post(extraDataEvent);
			entity.readEntityFromNBT(extraDataEvent.getExtraData());
		}
	}
}
