package theblockbox.huntersdream.api;

import static theblockbox.huntersdream.util.helpers.GeneralHelper.newResLoc;

import java.util.*;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import org.apache.commons.lang3.Validate;

import com.google.common.base.Preconditions;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import theblockbox.huntersdream.api.event.SkillRegistryEvent;
import theblockbox.huntersdream.util.collection.TransformationSet;
import theblockbox.huntersdream.util.handlers.PacketHandler;
import theblockbox.huntersdream.util.helpers.GeneralHelper;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

/**
 * Represents a skill (like speed, jump etc.) for one or more transformations
 * that can be unlocked by players.
 */
public class Skill {
    private static Map<String, Skill> skills = null;
    private final ResourceLocation registryName;
    private final int neededExperienceLevels;
    private final TransformationSet forTransformations;
    private final ResourceLocation icon;
    private final int level;
    private final ArrayList<Skill> childSkills = new ArrayList<>();
    private Skill parent = null;
    private Object iconSprite = null;

    private static final TransformationSet WEREWOLF_SET = TransformationSet.singletonSet(Transformation.WEREWOLF);

    // Hunter's Dream Skills
    public static final Skill BITE_0 = new Skill(newResLoc("bite"), 40, WEREWOLF_SET);
    public static final Skill BITE_1 = new Skill(80, BITE_0, 1);
    public static final Skill BITE_2 = new Skill(120, BITE_0, 2);

    public static final Skill SPEED_0 = new Skill(newResLoc("speed"), 40, WEREWOLF_SET);
    public static final Skill SPEED_1 = new Skill(80, SPEED_0, 1);
    public static final Skill SPEED_2 = new Skill(120, SPEED_0, 2);

    public static final Skill JUMP_0 = new Skill(newResLoc("jump"), 40, WEREWOLF_SET);
    public static final Skill JUMP_1 = new Skill(80, JUMP_0, 1);
    public static final Skill JUMP_2 = new Skill(120, JUMP_0, 2);

    public static final Skill UNARMED_0 = new Skill(newResLoc("unarmed"), 40, WEREWOLF_SET);
    public static final Skill UNARMED_1 = new Skill(80, UNARMED_0, 1);
    public static final Skill UNARMED_2 = new Skill(120, UNARMED_0, 2);

    public static final Skill ARMOR_0 = new Skill(newResLoc("natural_armor"), 40, WEREWOLF_SET);
    public static final Skill ARMOR_1 = new Skill(80, ARMOR_0, 1);
    public static final Skill ARMOR_2 = new Skill(120, ARMOR_0, 2);

    public static final Skill WILFUL_TRANSFORMATION = new Skill(newResLoc("wilful_transformation"), 200, WEREWOLF_SET);

    /**
     * Protected constructor that has a level parameter. All other constructors call
     * this one.
     */
    protected Skill(ResourceLocation registryName, @Nonnegative int neededExperienceLevels,
                    Collection<Transformation> forTransformations, ResourceLocation icon, int level) {
        this.registryName = registryName;
        Preconditions.checkArgument(neededExperienceLevels >= 0,
                "The argument neededExperienceLevels should be positive but had the value %s", neededExperienceLevels);
        this.neededExperienceLevels = neededExperienceLevels;
        String registryNameString = registryName.toString();
        Validate.isTrue(!forTransformations.isEmpty(), "The skill \"" + registryNameString
                + "\" should have at least one Transformation in the TransformationSet");
        this.forTransformations = new TransformationSet(forTransformations);
        this.icon = icon;
        Preconditions.checkArgument(level >= 0, "The argument level should be positive but had the value %s", level);
        this.level = level;
    }

    /**
     * Creates a new Skill instance with the given arguments.
     *
     * @param registryName           A unique {@link ResourceLocation} for this
     *                               skill.
     * @param neededExperienceLevels The levels that are removed from the player
     *                               when the skill is unlocked. Is not allowed to
     *                               be negative.
     * @param forTransformations     A {@link TransformationSet} that contains all
     *                               Transformations that should be able to unlock
     *                               this skill. Mustn't be null or empty.
     * @param icon                   The icon that should be shown in the skill tab
     *                               for this Skill. You don't have to add textures/
     *                               and .png as they're automatically being added
     *                               (so if the resource location is
     *                               {@code huntersdream:gui/skills/jump} it'll
     *                               automatically be converted to
     *                               {@code huntersdream:textures/gui/skills/jump.png}.
     * @throws IllegalArgumentException If the forTransformations argument is null
     *                                  or empty, a Skill with the same registry
     *                                  name already exists or the
     *                                  neededExperienceLevels parameter is
     *                                  negative.
     */
    public Skill(ResourceLocation registryName, @Nonnegative int neededExperienceLevels,
                 TransformationSet forTransformations, ResourceLocation icon) throws IllegalArgumentException {
        this(registryName, neededExperienceLevels, forTransformations, icon, 0);
    }

    /**
     * Creates a new Skill instance with the given arguments, the icon resource
     * location will be generated automatically in the format
     * {@code modid:textures/gui/skills/skill_name.png}
     *
     * @param registryName           A unique {@link ResourceLocation} for this
     *                               skill.
     * @param neededExperienceLevels The levels that are removed from the player
     *                               when the skill is unlocked. Is not allowed to
     *                               be negative.
     * @param forTransformations     A {@link TransformationSet} that contains all
     *                               Transformations that should be able to unlock
     *                               this skill. Mustn't be null or empty.
     * @throws IllegalArgumentException If the forTransformations argument is null
     *                                  or empty, a Skill with the same registry
     *                                  name already exists or the
     *                                  neededExperienceLevels parameter is
     *                                  negative.
     */
    public Skill(ResourceLocation registryName, @Nonnegative int neededExperienceLevels,
                 TransformationSet forTransformations) throws IllegalArgumentException {
        this(registryName, neededExperienceLevels, forTransformations,
                new ResourceLocation(registryName.getNamespace(), "gui/skills/" + registryName.getPath()));
    }

    /**
     * Creates a new Skill from a parent Skill.
     *
     * @param neededExperienceLevels The levels that are removed from the player
     *                               when the skill is unlocked. Is not allowed to
     *                               be negative.
     * @param parent                 The parent for this skill from which the
     *                               registry name (an underscore and the level are
     *                               added so that there aren't two skills with the
     *                               same registry name that would crash the game),
     *                               the icon and the transformations are inherited.
     * @param level                  The level for this Skill. Is not allowed to be
     *                               less than 1.
     * @throws IllegalArgumentException If the forTransformations argument is null
     *                                  or empty, a Skill with the same registry
     *                                  name already exists, the
     *                                  neededExperienceLevels parameter is
     *                                  negative, the level parameter is less than
     *                                  one or the parent skill isn't allowed to
     *                                  have child Skills.
     */
    public Skill(@Nonnegative int neededExperienceLevels, Skill parent, @Nonnegative int level)
            throws IllegalArgumentException {
        this(new ResourceLocation(parent.toString() + "_" + level), neededExperienceLevels, parent.forTransformations,
                parent.getIcon(), level);
        Preconditions.checkArgument(level >= 1, "The argument level should be at least one but was %s", level);
        int parentLevel = parent.getLevel();
        Preconditions.checkArgument(parentLevel == 0, "The parent's level should be 0 but was %s", parentLevel);
        this.parent = parent;
        GeneralHelper.safeSet(parent.childSkills, level - 1, this);
    }

    /**
     * Returns an unmodifiable collection of all skills.
     */
    public static Collection<Skill> getAllSkills() {
        return skills == null ? Collections.emptySet() : Collections.unmodifiableCollection(skills.values());
    }

    /**
     * Tries to get a Skill with the given string. Returns null if no Skill was
     * found.
     *
     * @see #fromRegistryName(ResourceLocation)
     * @see #getRegistryName()
     */
    @Nullable
    public static Skill fromName(String name) {
        return skills == null ? null : skills.get(name);
    }

    /**
     * Does exactly the same as {@link #fromName(String)} except that it accepts a
     * ResourceLocation.
     *
     * @see #fromName(String)
     * @see #toString()
     */
    @Nullable
    public static Skill fromRegistryName(ResourceLocation registryName) {
        return fromName(registryName.toString());
    }

    /**
     * Called in the preInit phase to register all skills. Should <b>not</b> be
     * called outside of Hunter's Dream.
     */
    public static void preInit() {
        SkillRegistryEvent event = new SkillRegistryEvent();
        MinecraftForge.EVENT_BUS.post(event);
        skills = event.getSkills();
    }

    /**
     * Returns the icon this Skill should have in the skill tab. Currently only used
     * for getting the {@link TextureAtlasSprite} from it.
     */
    public ResourceLocation getIcon() {
        return this.icon;
    }

    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getIconAsSprite() {
        return (TextureAtlasSprite) this.iconSprite;
    }

    /**
     * Sets the icon sprite for this Skill. Should only be used internally and on
     * the client side. Don't call this outside of Hunter's Dream!
     */
    @SideOnly(Side.CLIENT)
    public void setIconSprite(@Nonnull TextureAtlasSprite sprite) {
        if (sprite == null)
            throw new NullPointerException("The icon sprite is not allowed to be null");
        this.iconSprite = sprite;
    }

    /**
     * Returns the parent Skill of this Skill in an Optional. The parent Skill's
     * level should always be 0. If this Skill doesn't have a parent Skill, an empty
     * Optional is returned.
     */
    public Optional<Skill> getParent() {
        return Optional.ofNullable(this.parent);
    }

    /**
     * Returns true if the given Skill is this Skill's parent and therefore this
     * Skill is "the same" but with a different level.
     */
    public boolean isLevelOf(Skill skill) {
        Optional<Skill> optional = this.getParent();
        return optional.isPresent() && (optional.get() == skill);
    }

    /**
     * Returns the transformations that can unlock this Skill as a set.
     *
     * @see #getTransformationsAsArray()
     * @see #isForTransformation(Transformation)
     */
    public Set<Transformation> getTransformations() {
        return Collections.unmodifiableSet(this.forTransformations);
    }

    /**
     * Returns the transformations that can unlock this Skill as an array.
     *
     * @see #getTransformations()
     * @see #isForTransformation(Transformation)
     */
    public Transformation[] getTransformationsAsArray() {
        return this.forTransformations.toArray();
    }

    /**
     * Returns the xp levels that will be removed from the player when this Skill is
     * unlocked.
     */
    public int getNeededExperienceLevels() {
        return this.neededExperienceLevels;
    }

    /**
     * Returns the level this skill has. Should never be negative. Default and
     * minimum value is 0.
     */
    public int getLevel() {
        return this.level;
    }

    /**
     * Returns true if this Skill is a parent Skill, meaning that it doesn't have a
     * parent.
     */
    public boolean isParentSkill() {
        return this.parent == null;
    }

    /**
     * Returns an unmodifiable list of all Skills whose parent is this Skill.
     * Every Skill's index is the same as its level - 1 in that list, so if
     * you searched for a child Skill with a level of 10, you'd access the
     * list at index 9.
     */
    public List<Skill> getChildSkills() {
        return Collections.unmodifiableList(this.childSkills);
    }

    /**
     * Returns a modifiable list of all Skills this Skill requires in order to be
     * unlocked.<br>
     * Note: Parent Skills (with or without children) don't have required Skills. If
     * this is called on a child Skill, the list will always contain the parent Skill
     * and all child Skills of the parent Skill that have a level that is less than
     * that of the child Skill the method was called on.
     */
    public List<Skill> getRequiredSkills() {
        if (this.parent == null) {
            return Collections.emptyList();
        } else {
            List<Skill> toReturn  = Lists.newArrayListWithExpectedSize(this.getLevel() + 1);
            toReturn.add(this.parent);
            toReturn.addAll(this.parent.childSkills.subList(0, this.getLevel() - 1));
            return toReturn;
        }
    }

    /**
     * Returns true if the given transformation can unlock this Skill.
     *
     * @see #getTransformations()
     * @see #getTransformationsAsArray()
     */
    public boolean isForTransformation(Transformation transformation) {
        return this.forTransformations.contains(transformation);
    }

    /**
     * Returns the Skill's registry name that is used to save and load it with
     * {@link #fromRegistryName(ResourceLocation)}.
     *
     * @see #fromRegistryName(ResourceLocation)
     */
    public ResourceLocation getRegistryName() {
        return this.registryName;
    }

    /**
     * Returns the translation key for this Skill's name. The key is in the
     * format modid.skill.name where modid is the modid gotten from
     * {@link #getRegistryName()} and skill is the name space, also gotten from
     * {@link #getRegistryName()}.
     *
     * @see #getTranslationKeyDescription()
     */
    public String getTranslationKeyName() {
        ResourceLocation name = this.getRegistryName();
        return name.getNamespace() + "." + name.getPath() + ".name";
    }

    /**
     * Returns true if the given player can unlock this Skill. Returns false if the player can't unlock the skill or
     * has already unlocked it.
     */
    public boolean canPlayerUnlockSkill(EntityPlayer player) {
        ITransformationPlayer tp = TransformationHelper.getITransformationPlayer(player);
        if(player.experienceLevel >= this.getNeededExperienceLevels()
                && this.forTransformations.contains(tp.getTransformation()) && !tp.hasSkill(this)) {
            for(Skill s : this.getRequiredSkills()) {
                if(!tp.hasSkill(s)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Tries to unlock this Skill for the given player. If the player unlocked the Skill with this method, true will be
     * returned. If nothing changed because the player couldn't unlock the given Skill, false will be returned.
     * @throws  IllegalStateException If this Skill has already been unlocked
     */
    public boolean unlockSkillForPlayer(EntityPlayerMP player) throws  IllegalStateException {
        ITransformationPlayer tp = TransformationHelper.getITransformationPlayer(player);
        if(tp.hasSkill(this))
            throw new IllegalStateException("The player " + player + " has already unlocked the skill " + this +
                    " and therefore can't unlock it again.");

        int requiredXPLevels = this.getNeededExperienceLevels();
        if(player.experienceLevel >= requiredXPLevels && this.forTransformations.contains(tp.getTransformation())) {
            for(Skill s : this.getRequiredSkills()) {
                if(!tp.hasSkill(s)) {
                    return false;
                }
            }
            // if player can unlock skill,
            // remove xp
            player.addExperienceLevel(-requiredXPLevels);
            // add skill
            tp.addSkill(this);
            // sync with client
            PacketHandler.sendTransformationMessage(player);
            // and return true
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns the translation key for this Skill's description. The key is in
     * the format modid.skill.description where modid is the modid gotten from
     * {@link #getRegistryName()} and skill is the name space, also gotten from
     * {@link #getRegistryName()}.
     *
     * @see #getTranslationKeyName()
     */
    public String getTranslationKeyDescription(){
        ResourceLocation name = this.getRegistryName();
        return name.getNamespace() + "." + name.getPath() + ".description";
    }

    /**
     * Returns a String representation of the Skill's registry name.
     *
     * @see #getRegistryName()
     */
    @Override
    public String toString() {
        return this.registryName.toString();
    }
}