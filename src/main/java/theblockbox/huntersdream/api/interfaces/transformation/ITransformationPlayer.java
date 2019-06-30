package theblockbox.huntersdream.api.interfaces.transformation;

import com.google.common.collect.Sets;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.api.init.CapabilitiesInit;
import theblockbox.huntersdream.api.skill.ChildSkill;
import theblockbox.huntersdream.api.skill.ParentSkill;
import theblockbox.huntersdream.api.skill.Skill;
import theblockbox.huntersdream.inventory.ItemHandlerClothingTab;
import theblockbox.huntersdream.util.annotations.CapabilityInterface;

import javax.annotation.Nullable;
import java.util.*;

/**
 * ITransform for players (players can have xp)
 */
@CapabilityInterface
public interface ITransformationPlayer extends ITransformation {
    /**
     * Returns an optional of the skill the player is currently using. Returns an
     * empty optional if no skill is in use.
     */
    public Optional<Skill> getActiveSkill();

    /**
     * Sets the skill the player is currently using to the given skill. May be null.
     */
    public void setActiveSkill(@Nullable Skill skill);

    /**
     * Returns a mutable set of all the skills the player this capability
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
     */
    public boolean addSkill(Skill skill);

    /**
     * Removes the given skill. Returns true if the skill was removed, false if it
     * wasn't because the player didn't have it.
     */
    public boolean removeSkill(Skill skill);

    /**
     * Returns true if the player this capability belongs to has the given skill.
     */
    public boolean hasSkill(Skill skill);

    /**
     * Removes all skills by setting them to an empty set. (A new mutable set is
     * created in the implementation if the set is the same as
     * {@link Collections#EMPTY_SET}.)
     */
    public default void removeAllSkills() {
        this.setSkills(Collections.emptySet());
    }

    /**
     * Returns the level of the given skill the player has.
     * If the player doesn't have the skill, -1 is returned.
     */
    public default int getSkillLevel(ParentSkill skill) {
        int maxLevel = this.hasSkill(skill) ? skill.getLevel() : -1;
        for (ChildSkill childSkill : skill.getChildSkills()) {
            if (this.hasSkill(childSkill)) {
                maxLevel = Math.max(maxLevel, childSkill.getLevel());
            }
        }
        return maxLevel;
    }

    /**
     * Returns true when the player hasn't unlocked any skill at all.
     * Convenience method for {@link #getSkills()} and {@link Set#isEmpty()}.
     */
    public boolean hasNoSkills();

    public ItemHandlerClothingTab getClothingTab();

    public void setClothingTab(ItemHandlerClothingTab clothingTab);

    public static class TransformationPlayer implements ITransformationPlayer {
        private Transformation transformation = Transformation.HUMAN;
        private int textureIndex = 0;
        /**
         * Rituals that the player has done
         */
        private Set<Skill> skills = new HashSet<>();
        private ItemHandlerClothingTab clothingTab = new ItemHandlerClothingTab();
        private NBTTagCompound transformationData = new NBTTagCompound();
        private Skill activeSkill = null;

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
        public Optional<Skill> getActiveSkill() {
            return Optional.ofNullable(this.activeSkill);
        }

        @Override
        public void setActiveSkill(Skill skill) {
            this.activeSkill = skill;
        }

        @Override
        public Set<Skill> getSkills() {
            return new HashSet<>(this.skills);
        }

        @Override
        public void setSkills(Collection<Skill> skills) {
            this.skills = new HashSet<>(skills);
        }

        @Override
        public boolean addSkill(Skill skill) {
            return this.skills.add(Objects.requireNonNull(skill, "Cannot add null skill"));
        }

        @Override
        public boolean removeSkill(Skill skill) {
            return this.skills.remove(skill);
        }

        @Override
        public boolean hasSkill(Skill skill) {
            return this.skills.contains(skill);
        }

        @Override
        public boolean hasNoSkills() {
            return this.skills.isEmpty();
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

    public static class TransformationPlayerStorage implements Capability.IStorage<ITransformationPlayer> {
        public static final String TRANSFORMATION = "transformation";
        public static final String TEXTURE_INDEX = "textureindex";
        public static final String SKILLS = "skillsnbt";
        public static final String CLOTHING_TAB = "clothingtab";
        public static final String TRANSFORMATION_DATA = "transformationdata";
        public static final String ACTIVE_SKILL = "activeskill";

        @Override
        public NBTBase writeNBT(Capability<ITransformationPlayer> capability, ITransformationPlayer instance,
                                EnumFacing side) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setInteger(ITransformationPlayer.TransformationPlayerStorage.TEXTURE_INDEX, instance.getTextureIndex());
            Set<Skill> skills = instance.getSkills();
            GeneralHelper.writeArrayToNBT(compound, skills.toArray(new Skill[0]), ITransformationPlayer.TransformationPlayerStorage.SKILLS, Skill::toString);
            compound.setString(ITransformationPlayer.TransformationPlayerStorage.TRANSFORMATION, instance.getTransformation().toString());
            compound.setTag(ITransformationPlayer.TransformationPlayerStorage.CLOTHING_TAB,
                    CapabilitiesInit.CAPABILITY_ITEM_HANDLER.writeNBT(instance.getClothingTab(), null));
            compound.setTag(ITransformationPlayer.TransformationPlayerStorage.TRANSFORMATION_DATA, instance.getTransformationData());
            compound.setString(ITransformationPlayer.TransformationPlayerStorage.ACTIVE_SKILL, Objects.toString(instance.getActiveSkill(), ""));
            return compound;
        }

        @Override
        public void readNBT(Capability<ITransformationPlayer> capability, ITransformationPlayer instance,
                            EnumFacing side, NBTBase nbt) {
            NBTTagCompound compound = (NBTTagCompound) nbt;
            instance.setTextureIndex(compound.getInteger(ITransformationPlayer.TransformationPlayerStorage.TEXTURE_INDEX));
            instance.setTransformation(Transformation.fromName(compound.getString(ITransformationPlayer.TransformationPlayerStorage.TRANSFORMATION)));
            instance.setSkills(Sets.newHashSet(GeneralHelper.readArrayFromNBT(compound, ITransformationPlayer.TransformationPlayerStorage.SKILLS,
                    s -> Objects.requireNonNull(Skill.fromName(s)), Skill[]::new)));
            if (compound.hasKey(ITransformationPlayer.TransformationPlayerStorage.CLOTHING_TAB))
                CapabilitiesInit.CAPABILITY_ITEM_HANDLER.readNBT(instance.getClothingTab(), null,
                        compound.getTag(ITransformationPlayer.TransformationPlayerStorage.CLOTHING_TAB));
            instance.setTransformationData((NBTTagCompound) compound.getTag(ITransformationPlayer.TransformationPlayerStorage.TRANSFORMATION_DATA));
            instance.setActiveSkill(Skill.fromName(compound.getString(ITransformationPlayer.TransformationPlayerStorage.ACTIVE_SKILL)));
        }
    }
}
