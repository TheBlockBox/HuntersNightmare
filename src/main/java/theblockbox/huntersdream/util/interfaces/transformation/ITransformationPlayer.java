package theblockbox.huntersdream.util.interfaces.transformation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.util.HuntersJournalPage;
import theblockbox.huntersdream.util.annotations.CapabilityInterface;
import theblockbox.huntersdream.util.enums.Rituals;
import theblockbox.huntersdream.util.enums.Transformations;
import theblockbox.huntersdream.util.helpers.ChanceHelper;
import theblockbox.huntersdream.util.helpers.GeneralHelper;
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

	public double getLevel();

	public void setLevel(double level);

	default public int getLevelFloor() {
		return MathHelper.floor(getLevel());
	}

	default public double getPercentageToNextLevel() {
		return (getLevel() - getLevelFloor());
	}

	public void setUnlockedPages(HuntersJournalPage[] pages);

	public HuntersJournalPage[] getUnlockedPages();

	public void unlockPage(HuntersJournalPage page);

	public void lockPage(HuntersJournalPage page);

	public boolean hasUnlockedPage(HuntersJournalPage page);

	public HuntersJournalPage getRandomNotUnlockedPage();

	public Rituals[] getRituals();

	public void setRituals(Rituals[] rituals);

	public void addRitual(Rituals ritual);

	public void removeRitual(Rituals ritual);

	public boolean hasRitual(Rituals ritual);

	public static class TransformationPlayer implements ITransformationPlayer {
		private boolean transformed = false;
		private Transformations transformation = Transformations.HUMAN;
		private int xp = 0;
		private int textureIndex = 0;
		private double level = 0;
		/** Rituals that the player has done */
		// private Rituals[] rituals = new Rituals[0];
		// private HuntersJournalPage[] unlockedPages;
		private Set<Rituals> rituals = new HashSet<>();
		private Set<HuntersJournalPage> unlockedPages = new HashSet<>();

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
			return this.rituals.toArray(new Rituals[0]);
		}

		@Override
		public void setRituals(Rituals[] rituals) {
			this.rituals = Arrays.stream(rituals).collect(Collectors.toCollection(HashSet::new));
		}

		@Override
		public void setUnlockedPages(HuntersJournalPage[] pages) {
			this.unlockedPages = Arrays.stream(pages).collect(Collectors.toCollection(HashSet::new));
		}

		@Override
		public HuntersJournalPage[] getUnlockedPages() {
			return this.unlockedPages.toArray(new HuntersJournalPage[0]);
		}

		@Override
		public void unlockPage(HuntersJournalPage page) {
			if (!this.unlockedPages.add(page)) {
				throw new IllegalArgumentException("The player has already unlocked this page");
			}
		}

		@Override
		public void lockPage(HuntersJournalPage page) {
			if (!this.unlockedPages.remove(page)) {
				throw new IllegalArgumentException("The player hasn't unlocked this page yet");
			}
		}

		@Override
		public boolean hasUnlockedPage(HuntersJournalPage page) {
			return this.unlockedPages.contains(page);
		}

		@Override
		public HuntersJournalPage getRandomNotUnlockedPage() {
			int size = HuntersJournalPage.PAGES.size();
			if (this.unlockedPages.size() >= size) {
				return null;
			} else {
				HuntersJournalPage random = HuntersJournalPage.PAGES.get(ChanceHelper.randomInt(size));
				return this.hasUnlockedPage(random) ? this.getRandomNotUnlockedPage() : random;
			}
		}

		@Override
		public void addRitual(Rituals ritual) {
			if (!this.rituals.add(ritual)) {
				throw new IllegalArgumentException("The player already has this ritual");
			}
		}

		@Override
		public void removeRitual(Rituals ritual) {
			if (!this.rituals.remove(ritual)) {
				throw new IllegalArgumentException("The player didn't have the ritual that should be removed");
			}
		}

		@Override
		public boolean hasRitual(Rituals ritual) {
			return this.rituals.contains(ritual);
		}
	}

	public static class TransformationPlayerStorage implements IStorage<ITransformationPlayer> {
		public static final String TRANSFORMED = "transformed";
		public static final String XP = "xp";
		public static final String TRANSFORMATION = "transformation";
		public static final String TEXTURE_INDEX = "textureindex";
		public static final String LEVEL = "level";
		public static final String RITUALS = "rituals";
		public static final String PAGES = "pages";

		@Override
		public NBTBase writeNBT(Capability<ITransformationPlayer> capability, ITransformationPlayer instance,
				EnumFacing side) {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setBoolean(TRANSFORMED, instance.transformed());
			compound.setInteger(XP, instance.getXP());
			compound.setInteger(TEXTURE_INDEX, instance.getTextureIndex());
			compound.setDouble(LEVEL, instance.getLevel());
			GeneralHelper.writeArrayToNBT(compound, instance.getRituals(), RITUALS, Rituals::toString);
			compound.setString(TRANSFORMATION, instance.getTransformation().toString());
			GeneralHelper.writeArrayToNBT(compound, instance.getUnlockedPages(), PAGES, HuntersJournalPage::toString);
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
			instance.setRituals(GeneralHelper.readArrayFromNBT(compound, RITUALS, Rituals::fromName, Rituals[]::new));
			instance.setTransformation(Transformations.fromName(compound.getString(TRANSFORMATION)));
			instance.setUnlockedPages(GeneralHelper.readArrayFromNBT(compound, PAGES, HuntersJournalPage::fromName,
					HuntersJournalPage[]::new));

			// pre-0.2.0 support
			if (compound.hasKey("transformationID")) {
				Main.getLogger().warn(
						"Seems like you're using a new version of the mod... Loading transformation from old format");
				@SuppressWarnings("deprecation")
				Transformations transformation = Transformations.fromID(compound.getInteger("transformationID"));
				instance.setTransformation(transformation);
			}
		}
	}
}
