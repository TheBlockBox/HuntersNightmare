package theblockbox.huntersdream.api;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Represents a skill with a level higher than 0 that always has one {@link ParentSkill} with the level 0.
 *
 * @see ParentSkill
 * @see Skill
 */
public class ChildSkill extends Skill {
    private final ParentSkill parent;
    private final String translationKeyName;
    private final String translationKeyDescription;

    /**
     * Constructs a new ChildSkill instance from the given arguments.
     * <br>
     * The registry name will be exactly the same as this parent's registry name except that an underscore and the level
     * are added. For example if the parent's registry name was {@code huntersdream:bite} and the child skill had a
     * level of 2, the child skill's registry name would be {@code huntersdream:bite_2}.
     *
     * @param parent                    The parent of this skill.
     * @param neededExperienceLevels    The experience levels that are needed to unlock this skill.
     * @param level                     The level of this skill (must be higher than 0)
     * @param translationKeyName        A key for getting this skill's translated name.
     * @param translationKeyDescription A key for getting this skill's translated description.
     */
    public ChildSkill(ParentSkill parent, int neededExperienceLevels, int level, String translationKeyName,
                      String translationKeyDescription) {
        super(new ResourceLocation(parent + "_" + level), level, neededExperienceLevels);
        this.parent = parent;
        this.translationKeyName = translationKeyName;
        this.translationKeyDescription = translationKeyDescription;
        this.parent.addChildSkill(this);
    }

    /**
     * Constructs a new ChildSkill instance from the given arguments.
     * <br>
     * The translation key for the name will be in the
     * format {@code modid.skillname.name}, the one for the description will be in the format
     * {@code modid.skillname.description}, like it is created for the parent skills. For more info see
     * {@link ParentSkill#ParentSkill(ResourceLocation, int, Collection, boolean)}.
     *
     * @param parent                 The parent of this skill.
     * @param neededExperienceLevels The experience levels that are needed to unlock this skill.
     * @param level                  The level of this skill (must be higher than 0)
     * @see #ChildSkill(ParentSkill, int, int, String, String)
     * @see ParentSkill#ParentSkill(ResourceLocation, int, Collection, boolean)
     */
    public ChildSkill(ParentSkill parent, int neededExperienceLevels, int level) {
        super(new ResourceLocation(parent + "_" + level), level, neededExperienceLevels);
        this.parent = parent;
        String translationKey = this.getRegistryName().getNamespace() + "." + this.getRegistryName().getPath();
        this.translationKeyName = translationKey + ".name";
        this.translationKeyDescription = translationKey + ".description";
        this.parent.addChildSkill(this);
    }

    @Override
    public boolean isForTransformation(Transformation transformation) {
        return this.parent.isForTransformation(transformation);
    }

    @Override
    public Set<Transformation> getTransformations() {
        return this.parent.getTransformations();
    }

    @Override
    public Transformation[] getTransformationsAsArray() {
        return this.parent.getTransformationsAsArray();
    }

    @Override
    public boolean isAlwaysActive() {
        return this.parent.isAlwaysActive();
    }

    @Override
    public boolean canPlayerUnlockSkill(EntityPlayer player) {
        ITransformationPlayer tp = TransformationHelper.getITransformationPlayer(player);
        if ((player.experienceLevel >= this.getNeededExperienceLevels())
                && this.isForTransformation(tp.getTransformation()) && !tp.hasSkill(this)) {
            for (Skill s : this.getRequiredSkills()) {
                if (!tp.hasSkill(s)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public ParentSkill getGroupParent() {
        return this.parent;
    }

    @Override
    public List<Skill> getRequiredSkills() {
        List<Skill> toReturn = new ArrayList<>(this.getLevel());
        for (int i = 0; i < this.getLevel(); i++) {
            toReturn.add(this.getSkillWithLevel(i));
        }
        return toReturn;
    }

    @Override
    public int getMaximumLevel() {
        return this.parent.getMaximumLevel();
    }

    @Override
    public String getTranslationKeyName() {
        return this.translationKeyName;
    }

    @Override
    public String getTranslationKeyDescription() {
        return this.translationKeyDescription;
    }

    @Override
    public Skill getSkillWithLevel(int level) {
        return this.parent.getSkillWithLevel(level);
    }

    @Override
    public boolean isLevelOf(ParentSkill parentSkill) {
        return this.parent == parentSkill;
    }

    @Override
    public ResourceLocation getIcon() {
        return this.parent.getIcon();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public TextureAtlasSprite getIconAsSprite() {
        return this.parent.getIconAsSprite();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void setIconSprite(TextureAtlasSprite sprite) {
        throw new IllegalStateException("Can't set sprite for child skill!");
    }
}