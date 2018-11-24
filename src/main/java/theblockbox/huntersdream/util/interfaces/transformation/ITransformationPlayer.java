package theblockbox.huntersdream.util.interfaces.transformation;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import theblockbox.huntersdream.init.CapabilitiesInit;
import theblockbox.huntersdream.inventory.ItemHandlerClothingTab;
import theblockbox.huntersdream.util.HuntersJournalPage;
import theblockbox.huntersdream.util.Transformation;
import theblockbox.huntersdream.util.annotations.CapabilityInterface;
import theblockbox.huntersdream.util.enums.Rituals;
import theblockbox.huntersdream.util.helpers.GeneralHelper;

/**
 * ITransform for players (players can have xp)
 */
@CapabilityInterface
public interface ITransformationPlayer extends ITransformation {
	public void setUnlockedPages(HuntersJournalPage[] pages);

	public HuntersJournalPage[] getUnlockedPages();

	public void unlockPage(HuntersJournalPage page);

	public void lockPage(HuntersJournalPage page);

	public boolean hasUnlockedPage(HuntersJournalPage page);

	/** Returns a random not unlocked page or null if all pages are unlocked */
	@Nullable
	public HuntersJournalPage getRandomNotUnlockedPage(Random random);

	public Rituals[] getRituals();

	public void setRituals(Rituals[] rituals);

	public void addRitual(Rituals ritual);

	public void removeRitual(Rituals ritual);

	public boolean hasRitual(Rituals ritual);

	public ItemHandlerClothingTab getClothingTab();

	public void setClothingTab(ItemHandlerClothingTab clothingTab);

	public static class TransformationPlayer implements ITransformationPlayer {
		private static final NBTTagCompound DEFAULT_TRANSFORMATION_DATA = new NBTTagCompound();
		static {
			DEFAULT_TRANSFORMATION_DATA.setString("transformation", Transformation.WEREWOLF.toString());
		}

		private Transformation transformation = Transformation.HUMAN;
		private int textureIndex = 0;
		/** Rituals that the player has done */
		private Set<Rituals> rituals = EnumSet.noneOf(Rituals.class);
		private Set<HuntersJournalPage> unlockedPages = new HashSet<>();
		private ItemHandlerClothingTab clothingTab = new ItemHandlerClothingTab();
		private NBTTagCompound transformationData = DEFAULT_TRANSFORMATION_DATA;

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
		public int getTextureIndex() {
			return this.textureIndex;
		}

		@Override
		public void setTextureIndex(int textureIndex) {
			this.textureIndex = textureIndex;
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
		@Nullable
		public HuntersJournalPage getRandomNotUnlockedPage(Random random) {
			int size = HuntersJournalPage.PAGES.size();
			if (this.unlockedPages.size() >= size) {
				return null;
			} else {
				HuntersJournalPage page = HuntersJournalPage.PAGES.get(random.nextInt(size));
				return this.hasUnlockedPage(page) ? this.getRandomNotUnlockedPage(random) : page;
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

		@Override
		public NBTTagCompound getTransformationData() {
			return this.transformationData;
		}

		@Override
		public void setTransformationData(NBTTagCompound transformationData) {
			this.transformationData = transformationData;
		}
	}

	public static class TransformationPlayerStorage implements IStorage<ITransformationPlayer> {
		public static final String TRANSFORMATION = "transformation";
		public static final String TEXTURE_INDEX = "textureindex";
		public static final String RITUALS = "ritualsnbt";
		public static final String PAGES = "pages";
		public static final String CLOTHING_TAB = "clothingtab";
		public static final String TRANSFORMATION_DATA = "transformationdata";

		@Override
		public NBTBase writeNBT(Capability<ITransformationPlayer> capability, ITransformationPlayer instance,
				EnumFacing side) {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setInteger(TEXTURE_INDEX, instance.getTextureIndex());
			GeneralHelper.writeArrayToNBT(compound, instance.getRituals(), RITUALS, Rituals::toString);
			compound.setString(TRANSFORMATION, instance.getTransformation().toString());
			GeneralHelper.writeArrayToNBT(compound, instance.getUnlockedPages(), PAGES, HuntersJournalPage::toString);
			compound.setTag(CLOTHING_TAB,
					CapabilitiesInit.CAPABILITY_ITEM_HANDLER.writeNBT(instance.getClothingTab(), null));
			compound.setTag(TRANSFORMATION_DATA, instance.getTransformationData());
			return compound;
		}

		@Override
		public void readNBT(Capability<ITransformationPlayer> capability, ITransformationPlayer instance,
				EnumFacing side, NBTBase nbt) {
			NBTTagCompound compound = (NBTTagCompound) nbt;
			instance.setTextureIndex(compound.getInteger(TEXTURE_INDEX));
			instance.setTransformation(Transformation.fromName(compound.getString(TRANSFORMATION)));
			instance.setUnlockedPages(GeneralHelper.readArrayFromNBT(compound, PAGES, HuntersJournalPage::fromName,
					HuntersJournalPage[]::new));
			instance.setRituals(GeneralHelper.readArrayFromNBT(compound, RITUALS, Rituals::fromName, Rituals[]::new));
			if (compound.hasKey(CLOTHING_TAB))
				CapabilitiesInit.CAPABILITY_ITEM_HANDLER.readNBT(instance.getClothingTab(), null,
						compound.getTag(CLOTHING_TAB));
			instance.setTransformationData((NBTTagCompound) compound.getTag(TRANSFORMATION_DATA));
		}
	}

	default public int getLevelFloor() {
		return 0;
	}
}
