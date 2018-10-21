package theblockbox.huntersdream.util.interfaces.transformation;

import java.util.Arrays;
import java.util.EnumSet;
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
import theblockbox.huntersdream.init.CapabilitiesInit;
import theblockbox.huntersdream.init.TransformationInit;
import theblockbox.huntersdream.inventory.ItemHandlerClothingTab;
import theblockbox.huntersdream.util.HuntersJournalPage;
import theblockbox.huntersdream.util.Transformation;
import theblockbox.huntersdream.util.annotations.CapabilityInterface;
import theblockbox.huntersdream.util.enums.Rituals;
import theblockbox.huntersdream.util.helpers.ChanceHelper;
import theblockbox.huntersdream.util.helpers.GeneralHelper;
import theblockbox.huntersdream.util.helpers.TransformationHelper;

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

	public ItemHandlerClothingTab getClothingTab();

	public void setClothingTab(ItemHandlerClothingTab clothingTab);

	public static class TransformationPlayer implements ITransformationPlayer {
		private boolean transformed = false;
		private Transformation transformation = TransformationInit.HUMAN;
		private int xp = 0;
		private int textureIndex = 0;
		private double level = 0;
		/** Rituals that the player has done */
		private Set<Rituals> rituals = EnumSet.noneOf(Rituals.class);
		private Set<HuntersJournalPage> unlockedPages = new HashSet<>();
		private ItemHandlerClothingTab clothingTab = new ItemHandlerClothingTab();

		@Override
		public boolean transformed() {
			return this.transformed;
		}

		@Override
		public void setTransformed(boolean transformed) {
			this.transformed = transformed;
		}

		@Override
		public Transformation getTransformation() {
			return this.transformation;
		}

		@Override
		public void setTransformation(Transformation transformation) {
			transformation.validateIsTransformation();
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

		@Override
		public ItemHandlerClothingTab getClothingTab() {
			return this.clothingTab;
		}

		@Override
		public void setClothingTab(ItemHandlerClothingTab clothingTab) {
			this.clothingTab = clothingTab;
		}
	}

	public static class TransformationPlayerStorage implements IStorage<ITransformationPlayer> {
		public static final String TRANSFORMED = "transformed";
		public static final String XP = "xp";
		public static final String TRANSFORMATION = "transformation";
		public static final String TEXTURE_INDEX = "textureindex";
		public static final String LEVEL = "level";
		public static final String RITUALS = "ritualsnbt";
		public static final String PAGES = "pages";
		public static final String CLOTHING_TAB = "clothingtab";

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
			compound.setTag(CLOTHING_TAB,
					CapabilitiesInit.CAPABILITY_ITEM_HANDLER.writeNBT(instance.getClothingTab(), null));
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
			instance.setTransformation(Transformation.fromName(compound.getString(TRANSFORMATION)));
			instance.setUnlockedPages(GeneralHelper.readArrayFromNBT(compound, PAGES, HuntersJournalPage::fromName,
					HuntersJournalPage[]::new));
			instance.setRituals(GeneralHelper.readArrayFromNBT(compound, RITUALS, Rituals::fromName, Rituals[]::new));
			if (compound.hasKey(CLOTHING_TAB))
				CapabilitiesInit.CAPABILITY_ITEM_HANDLER.readNBT(instance.getClothingTab(), null,
						compound.getTag(CLOTHING_TAB));
		}
	}
}
