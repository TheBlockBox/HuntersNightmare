package theblockbox.huntersdream.util.interfaces.transformation;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.util.annotations.CapabilityInterface;
import theblockbox.huntersdream.util.enums.Rituals;
import theblockbox.huntersdream.util.enums.Transformations;
import theblockbox.huntersdream.util.exceptions.UnexpectedBehaviorException;
import theblockbox.huntersdream.util.helpers.TransformationHelper;;

/**
 * ITransform for players (players can have xp)
 */
@CapabilityInterface
public interface ITransformationPlayer extends ITransformation {
	public int getXP();

	/**
	 * Use {@link TransformationHelper#setXP(EntityPlayerMP, int)} for automatic
	 * packets and level up messages. When you use this, always remember to also do
	 * {@link #setLevel(double)}
	 */
	public void setXP(int xp);

	// getTextureIndex() already defined in ITransformation

	public void setTextureIndex(int textureIndex);

	public double getLevel();

	public void setLevel(double level);

	default public int getLevelFloor() {
		return MathHelper.floor(getLevel());
	}

	default public double getPercentageToNextLevel() {
		return (getLevel() - getLevelFloor());
	}

	public Rituals[] getRituals();

	public void setRituals(Rituals[] rituals);

	default public void addRitual(Rituals ritual) {
		Rituals[] currentRituals = getRituals();
		Rituals[] rituals = new Rituals[currentRituals.length + 1];
		for (int i = 0; i < currentRituals.length; i++) {
			rituals[i] = currentRituals[i];
			if (rituals[i] == ritual)
				throw new IllegalArgumentException(
						"The ritual " + ritual.toString() + " is already in the array at index " + i);
		}
		rituals[currentRituals.length] = ritual;
		setRituals(rituals);
	}

	default public void removeRitual(Rituals ritual) {
		if (hasRitual(ritual)) {
			Rituals[] currentRituals = getRituals();
			ArrayList<Rituals> rituals = new ArrayList<>();
			for (Rituals r : currentRituals) {
				if (r != ritual)
					rituals.add(r);
			}
			Rituals[] newRituals = rituals.toArray(new Rituals[0]);
			if (newRituals.length != (currentRituals.length - 1))
				throw new UnexpectedBehaviorException(
						"Should remove one ritual, but array size is different then expected (old array size: "
								+ currentRituals.length + " expected array size: " + (currentRituals.length - 1)
								+ " gotten array size: " + newRituals.length);
			setRituals(newRituals);
		} else {
			throw new IllegalArgumentException(
					"The ritual " + ritual.toString() + " couldn't be found in the array and therefore not be removed");
		}
	}

	default public boolean hasRitual(Rituals ritual) {
		for (Rituals r : getRituals()) {
			if (r == ritual)
				return true;
		}
		return false;
	}

	public static class TransformationPlayer implements ITransformationPlayer {
		private boolean transformed = false;
		private Transformations transformation = Transformations.HUMAN;
		private int xp = 0;
		private int textureIndex = 0;
		private double level = 0;
		/** Rituals that the player has done */
		private Rituals[] rituals = new Rituals[0];

		@Override
		public boolean transformed() {
			return transformed;
		}

		@Override
		public void setTransformed(boolean transformed) {
			this.transformed = transformed;
		}

		@Override
		public Transformations getTransformation() {
			return this.transformation;
		}

		@Override
		public void setTransformation(Transformations transformation) {
			this.transformation = transformation;
		}

		@Override
		public int getXP() {
			return this.xp;
		}

		@Override
		public void setXP(int xp) {
			this.xp = xp;
		}

		@Override
		public int getTextureIndex() {
			return this.textureIndex;
		}

		@Override
		public void setTextureIndex(int textureIndex) {
			this.textureIndex = textureIndex;
		}

		@Override
		public double getLevel() {
			return this.level;
		}

		@Override
		public void setLevel(double level) {
			this.level = level;
		}

		@Override
		public Rituals[] getRituals() {
			return this.rituals;
		}

		@Override
		public void setRituals(Rituals[] rituals) {
			this.rituals = rituals;
		}
	}

	public static class TransformationPlayerStorage implements IStorage<ITransformationPlayer> {
		public static final String TRANSFORMED = "transformed";
		public static final String XP = "xp";
		public static final String TRANSFORMATION = "transformation";
		public static final String TEXTURE_INDEX = "textureindex";
		public static final String LEVEL = "level";
		public static final String LENGTH = "length";
		public static final String RITUALS = "rituals";
		public static final String RITUAL_CHAR = "r";

		@Override
		public NBTBase writeNBT(Capability<ITransformationPlayer> capability, ITransformationPlayer instance,
				EnumFacing side) {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setBoolean(TRANSFORMED, instance.transformed());
			compound.setInteger(XP, instance.getXP());
			compound.setInteger(TEXTURE_INDEX, instance.getTextureIndex());
			compound.setDouble(LEVEL, instance.getLevel());
			Rituals[] rituals = instance.getRituals();
			NBTTagCompound ritualStorage = new NBTTagCompound();
			ritualStorage.setInteger(LENGTH, rituals.length);
			for (int i = 0; i < rituals.length; i++) {
				ritualStorage.setString(RITUAL_CHAR + i, rituals[i].toString());
			}
			compound.setTag(RITUALS, ritualStorage);
			compound.setString(TRANSFORMATION, instance.getTransformation().toString());
			return compound;
		}

		@Override
		public void readNBT(Capability<ITransformationPlayer> capability, ITransformationPlayer instance,
				EnumFacing side, NBTBase nbt) {
			NBTTagCompound compound = (NBTTagCompound) nbt;
			instance.setTransformed(compound.getBoolean(TRANSFORMED));
			instance.setXP(compound.getInteger(XP));
			instance.setTextureIndex(compound.getInteger(TEXTURE_INDEX));
			instance.setLevel(compound.getDouble(LEVEL));
			NBTTagCompound ritualStorage = (NBTTagCompound) compound.getTag(RITUALS);
			int length = ritualStorage.getInteger(LENGTH);
			Rituals[] rituals = new Rituals[length];
			for (int i = 0; i < length; i++) {
				rituals[i] = Rituals.fromName(ritualStorage.getString(RITUAL_CHAR + i));
			}
			instance.setRituals(rituals);
			instance.setTransformation(Transformations.fromName(compound.getString(TRANSFORMATION)));

			// pre-0.2.0 support
			if (compound.hasKey("transformationID")) {
				Main.LOGGER.warn("Seems like the mod has been upgraded... Loading transformation from old format");
				@SuppressWarnings("deprecation")
				Transformations transformation = Transformations.fromID(compound.getInteger("transformationID"));
				instance.setTransformation(transformation);
			}
		}
	}
}
