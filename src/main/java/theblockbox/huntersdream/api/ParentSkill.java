package theblockbox.huntersdream.api;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import theblockbox.huntersdream.util.collection.TransformationSet;
import theblockbox.huntersdream.util.helpers.GeneralHelper;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Represents a skill that always has the level 0 and may have children of type {@link ChildSkill} (a parent skill can
 * still exist without child skills).
 *
 * @see ChildSkill
 * @see Skill
 */
public class ParentSkill extends Skill {
    private final boolean isAlwaysActive;
    private final TransformationSet forTransformations;
    private final String translationKeyName;
    private final String translationKeyDescription;
    private final ResourceLocation icon;
    @SideOnly(Side.CLIENT)
    private TextureAtlasSprite sprite;
    private final List<ChildSkill> childSkills = new ArrayList<>();

    /**
     * Constructs a new ParentSkill instance from the given arguments.
     *
     * @param registryName              A unique {@link ResourceLocation} that is used to identify this skill.
     * @param neededExperienceLevels    The amount of experience levels that are needed to unlock this skill. If this
     *                                  skill is not obtainable via experience levels, pass {@code -1} here.
     * @param forTransformations        A set that contains all transformations that can unlock this skill. The set will
     *                                  be copied, so if you change something in the passed set, it won't change in this
     *                                  skill's private set.
     * @param isAlwaysActive            Decides if you have to activate this skill so that it works (false) or if it
     *                                  works passively all the time (true).
     * @param icon                      A {@link ResourceLocation} that is used to get the icon that is shown for this
     *                                  skill. It is only used to get the {@link TextureAtlasSprite}, so in order to be
     *                                  loaded correctly, it has to be in the correct format. The needed format is
     *                                  exactly the same like normal, except that you don't need to add {@code textures}
     *                                  and {@code .png}. For example if a resource location in this format was
     *                                  {@code huntersdream:gui/skills/bite}, it would actually point to
     *                                  {@code huntersdream:textures/gui/skills/bite.png}.
     * @param translationKeyName        A key for getting this skill's translated name.
     * @param translationKeyDescription A key for getting this skill's translated description.
     */
    public ParentSkill(ResourceLocation registryName, int neededExperienceLevels,
                       Collection<Transformation> forTransformations, boolean isAlwaysActive, ResourceLocation icon,
                       String translationKeyName, String translationKeyDescription) {
        super(registryName, 0, neededExperienceLevels);
        this.forTransformations = new TransformationSet(forTransformations);
        this.isAlwaysActive = isAlwaysActive;
        this.icon = icon;
        this.translationKeyName = translationKeyName;
        this.translationKeyDescription = translationKeyDescription;
    }

    /**
     * Constructs a new ParentSkill instance from the given arguments by delegating to
     * {@link #ParentSkill(ResourceLocation, int, Collection, boolean, ResourceLocation, String, String)}.
     * <br>
     * The icon path will automatically point to {@code modid:textures/gui/skills/skillname.png} (the format would
     * actually be {@code modid:gui/skills/skillname}, where {@code modid} is the mod id used in the registry name and
     * {@code skillname} is the path used in the registry name for more info look at
     * {@link #ParentSkill(ResourceLocation, int, Collection, boolean, ResourceLocation, String, String)}).
     * <br>
     * The translation key for the name will be generated in the format {@code modid.skillname.name} and the one for the
     * description will be generated in the format {@code modid.skillname.description}, where {@code modid} is the mod
     * id used in the registry name and {@code skillname} is the path used in the registry name. For example if the mod
     * id was {@code huntersdream} and the skill name was {@code bite}, the key for the name would be
     * {@code huntersdream.bite.name} and the one for the description would be {@code huntersdream.bite.description}.
     *
     * @param registryName           A unique {@link ResourceLocation} that is used to identify this skill.
     * @param neededExperienceLevels The amount of experience levels that are needed to unlock this skill. If this skill
     *                               is not obtainable via experience levels, pass {@code -1} here.
     * @param forTransformations     A set that contains all transformations that can unlock this skill. The set will
     *                               be copied, so if you change something in the passed set, it won't change in this
     *                               skill's private set.
     * @param isAlwaysActive         Decides if you have to activate this skill so that it works (false) or if it
     *                               works passively all the time (true).
     */
    public ParentSkill(ResourceLocation registryName, int neededExperienceLevels,
                       Collection<Transformation> forTransformations, boolean isAlwaysActive) {
        this(registryName, neededExperienceLevels, forTransformations, isAlwaysActive,
                new ResourceLocation(registryName.getNamespace(), "gui/skills/" + registryName.getPath()),
                registryName.getNamespace() + "." + registryName.getPath() + ".name",
                registryName.getNamespace() + "." + registryName.getPath() + ".description");
    }

    @Override
    public boolean isForTransformation(Transformation transformation) {
        return this.forTransformations.contains(transformation);
    }

    @Override
    public Set<Transformation> getTransformations() {
        return new TransformationSet(this.forTransformations);
    }

    @Override
    public Transformation[] getTransformationsAsArray() {
        return this.forTransformations.toArray();
    }

    @Override
    public boolean isAlwaysActive() {
        return this.isAlwaysActive;
    }

    @Override
    public boolean canPlayerUnlockSkill(EntityPlayer player) {
        ITransformationPlayer tp = TransformationHelper.getITransformationPlayer(player);
        return (player.experienceLevel >= this.getNeededExperienceLevels())
                && this.isForTransformation(tp.getTransformation()) && !tp.hasSkill(this);
    }

    @Override
    public ParentSkill getGroupParent() {
        return this;
    }

    @Override
    public List<Skill> getRequiredSkills() {
        return new ArrayList<>();
    }

    @Override
    public int getMaximumLevel() {
        return this.childSkills.size();
    }

    @Override
    public String getTranslationKeyName() {
        return this.translationKeyName;
    }

    @Override
    public String getTranslationKeyDescription() {
        return this.translationKeyDescription;
    }

    /**
     * Returns a newly allocated modifiable list of this skill's child skills.
     * Therefore, you are allowed to modify the returned list without modifying
     * the "real" list.
     */
    public List<ChildSkill> getChildSkills() {
        return new ArrayList<>(this.childSkills);
    }

    @Override
    public Skill getSkillWithLevel(int level) throws IndexOutOfBoundsException {
        if (level == 0)
            return this;
        else
            return this.getChildSkillWithLevel(level);
    }

    @Override
    public boolean isLevelOf(ParentSkill parentSkill) {
        return this == parentSkill;
    }

    @Override
    public ResourceLocation getIcon() {
        return this.icon;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public TextureAtlasSprite getIconAsSprite() {
        return this.sprite;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void setIconSprite(TextureAtlasSprite sprite) {
        this.sprite = sprite;
    }

    ChildSkill getChildSkillWithLevel(int level) {
        return this.childSkills.get(level - 1);
    }

    void addChildSkill(ChildSkill childSkill) {
        int index = childSkill.getLevel() - 1;
        Skill atIndex = GeneralHelper.safeGet(this.childSkills, index);
        if (atIndex == null) {
            GeneralHelper.safeSet(this.childSkills, index, childSkill);
        } else {
            throw new IllegalArgumentException("Tried to add child skill " + childSkill + " to parent skill " + this
                    + ", but found skill " + atIndex + " that had the same level.");
        }
    }
}