package theblockbox.huntersdream.util.interfaces.transformation;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.collect.Sets;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import theblockbox.huntersdream.api.Skill;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.init.CapabilitiesInit;
import theblockbox.huntersdream.inventory.ItemHandlerClothingTab;
import theblockbox.huntersdream.util.HuntersJournalPage;
import theblockbox.huntersdream.util.annotations.CapabilityInterface;
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

	/**
	 * Returns an unmodifiable set of all the skills the player this capability
	 * belongs to has.
	 */
	public Set<Skill> getSkills();

	/**
	 * Sets the skills to the given collection. (The collection is cloned, so if you
	 * change anything with the collection you passed in this method it won't change
	 * for the player.)
	 */
	public void setSkills(Collection<Skill> skills);

	/**
	 * Adds a skill. Returns true if the skill was added and false if the skill has
	 * already been added.
	 * 
	 * @throws IllegalArgumentException When the skill requires skills that the
	 *                                  player doesn't have.
	 */
	public boolean addSkill(Skill skill) throws IllegalArgumentException;

	/**
	 * Removes the given skill. Returns true if the skill was removed, false if it
	 * wasn't because the player didn't have it.
	 * 
	 * @throws IllegalArgumentException When other skills require the skill that
	 *                                  should be removed. (For preventing bugs.)
	 */
	public boolean removeSkill(Skill skill) throws IllegalArgumentException;

	/**
	 * Returns true if the player this capability belongs to has the given skill.
	 */
	public boolean hasSkill(Skill skill);

	/**
	 * Removes all skills by setting them to an empty set. (A new mutable set is
	 * created in the implementation if the set is the same as
	 * {@link Collections#EMPTY_SET}.)
	 */
	default void removeAllSkills() {
		this.setSkills(Collections.emptySet());
	}

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
		private Set<Skill> skills = new HashSet<>();
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
		public Set<Skill> getSkills() {
			return Collections.unmodifiableSet(this.skills);
		}

		@Override
		public void setSkills(Collection<Skill> skills) {
			this.skills = new HashSet<>(skills);
		}

		@Override
		public boolean addSkill(Skill skill) throws IllegalArgumentException {
			if (!GeneralHelper.containsAll(this.skills, skill.getRequiredSkills()))
				throw new IllegalArgumentException("Can't add skill " + skill + " because it requires the skills "
						+ Arrays.toString(skill.getRequiredSkills()) + ", but only " + this.skills.toString()
						+ " are present");
			return this.skills.add(skill);
		}

		@Override
		public boolean removeSkill(Skill skill) throws IllegalArgumentException {
			for (Skill s : this.skills)
				if (s.requiresSkill(skill))
					throw new IllegalArgumentException(
							"The skill " + s + " still requires the skill " + skill + " that should be removed");
			return this.skills.remove(skill);
		}

		@Override
		public boolean hasSkill(Skill skill) {
			return this.skills.contains(skill);
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
		public static final String SKILLS = "skillsnbt";
		public static final String PAGES = "pages";
		public static final String CLOTHING_TAB = "clothingtab";
		public static final String TRANSFORMATION_DATA = "transformationdata";

		@Override
		public NBTBase writeNBT(Capability<ITransformationPlayer> capability, ITransformationPlayer instance,
				EnumFacing side) {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setInteger(TEXTURE_INDEX, instance.getTextureIndex());
			Set<Skill> skills = instance.getSkills();
			GeneralHelper.writeArrayToNBT(compound, skills.toArray(new Skill[skills.size()]), SKILLS, Skill::toString);
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
			instance.setSkills(
					Sets.newHashSet(GeneralHelper.readArrayFromNBT(compound, SKILLS, Skill::fromName, Skill[]::new)));
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
