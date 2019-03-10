package theblockbox.huntersdream.api.skill;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.api.event.SkillRegistryEvent;
import theblockbox.huntersdream.api.helpers.TransformationHelper;
import theblockbox.huntersdream.util.handlers.PacketHandler;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Represents a skill (like speed, jump etc.) for one or more transformations
 * that can be unlocked by players. This class is only extended by the classes
 * {@link ChildSkill} and {@link ParentSkill}.
 * @see ChildSkill
 * @see ParentSkill
 */
public abstract class Skill {
    private static Map<String, Skill> skills = null;
    private final ResourceLocation registryName;
    private final int skillLevel;
    private final int neededExperienceLevels;

    // protected constructor so that only ParentSkill and ChildSkill can extend this class
    Skill(ResourceLocation registryName, int level, int neededExperienceLevels) {
        this.registryName = registryName;
        this.skillLevel = level;
        this.neededExperienceLevels = neededExperienceLevels;
        if (!((this instanceof ParentSkill) || (this instanceof ChildSkill))) {
            throw new IllegalStateException("The skill " + this +
                    " is neither an instance of ParentSkill, nor an instance of ChildSkill");
        }
        if (level < 0) {
            throw new IllegalArgumentException("The level of the skill " + this +
                    " isn't allowed to be less than 0 but is " + level);
        } else if ((level == 0) && (this instanceof ChildSkill)) {
            throw new IllegalArgumentException("The level of the child skill " + this +
                    " isn't allowed to be less than 1 but is " + level);
        }
    }

    /**
     * Returns an unmodifiable collection of all skills.
     */
    public static Collection<Skill> getAllSkills() {
        return Skill.skills == null ? Collections.emptySet() : Collections.unmodifiableCollection(Skill.skills.values());
    }

    /**
     * Tries to get a skill with the given string. Returns null if no skill was
     * found.
     *
     * @see #fromRegistryName(ResourceLocation)
     * @see #getRegistryName()
     */
    @Nullable
    public static Skill fromName(String name) {
        return Skill.skills == null ? null : Skill.skills.get(name);
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
        return Skill.fromName(registryName.toString());
    }

    /**
     * Called in the preInit phase to register all skills. Should <b>not</b> be
     * called outside of Hunter's Dream.
     */
    public static void preInit() {
        SkillRegistryEvent event = new SkillRegistryEvent();
        MinecraftForge.EVENT_BUS.post(event);
        Skill.skills = event.getSkills();
    }

    /**
     * Returns this skill's registry name (gotten from
     * {@link #getRegistryName()}) in its string
     * representation.
     */
    @Override
    public final String toString() {
        return this.registryName.toString();
    }

    /**
     * Returns a unique {@link ResourceLocation} that
     * is used to identify this skill.
     */
    public final ResourceLocation getRegistryName() {
        return this.registryName;
    }

    /**
     * Returns the experience levels that are
     * required to unlock this skill. If this
     * skill is not buyable via experience levels,
     * this will return -1
     */
    public final int getNeededExperienceLevels() {
        return this.neededExperienceLevels;
    }

    /**
     * Returns true when this skill can bought with
     * experience in the skill tab. The return value
     * of this method is always the same as
     * {@code skill.getNeededExperienceLevels() != -1},
     * where {@code skill} is this skill.
     */
    public final boolean canBeBoughtWithExperience() {
        return this.getNeededExperienceLevels() != -1;
    }

    /**
     * Returns this skill's level. For parent
     * skills, this always returns 0.
     */
    public final int getLevel() {
        return this.skillLevel;
    }

    /**
     * Returns true if this skill is a parent skill.
     * (Doesn't necessarily need to have children.)
     */
    public final boolean isParentSkill() {
        return this instanceof ParentSkill;
    }

    /**
     * Returns an empty optional if this skill is a {@link ParentSkill}
     * or an optional with this skill in it if this skill is an instance
     * of {@link ChildSkill}.
     */
    public final Optional<ChildSkill> getAsChildSkill() {
        return (this instanceof ChildSkill) ? Optional.of((ChildSkill) this) : Optional.empty();
    }

    /**
     * Returns an empty optional if this skill is a {@link ChildSkill}
     * or an optional with this skill in it if this skill is an instance
     * of {@link ParentSkill}.
     */
    public final Optional<ParentSkill> getAsParentSkill() {
        return (this instanceof ParentSkill) ? Optional.of((ParentSkill) this) : Optional.empty();
    }

    /**
     * Tries to unlock this skill for the given player.
     * Returns true, removes experience levels and adds
     * the skill if this methods succeeds, returns
     * false if it fails.
     */
    public boolean unlockSkillForPlayer(EntityPlayerMP player) {
        if (this.canPlayerUnlockSkill(player)) {
            player.addExperienceLevel(-this.getNeededExperienceLevels());
            TransformationHelper.getITransformationPlayer(player).addSkill(this);
            PacketHandler.sendTransformationMessage(player);
            return true;
        }
        return false;
    }

    /**
     * Is called on client and server side when this skill has been activated in the skill bar by the given player.
     */
    public void onSkillActivated(EntityPlayer player) {
    }

    /**
     * Returns true if this skill should be shown in the skill bar of the given player. The standard implementation
     * tests if the given player has unlocked this skill with this skill's specific level, though it is not necessary to
     * have unlocked the skill in order to show it in the skill bar. Therefore, you can also add skills to the skill bar
     * that the player hasn't even unlocked. It is also possible to add multiple skills of the same type but different
     * level to the skill bar, although this is not recommended. If {@link #isAlwaysActive()} returns false, this method
     * will neither have any effect nor even be called.
     */
    public boolean shouldShowSkillInSkillBar(EntityPlayer player) {
        // TODO: Make this more efficient by passing the ITransformationPlayer capability of the player, too?
        return TransformationHelper.getITransformationPlayer(player).getSkillLevel(this.getGroupParent()) == this.getLevel();
    }

    /**
     * Returns true if the given transformation can unlock
     * this skill.
     */
    public abstract boolean isForTransformation(Transformation transformation);

    /**
     * Returns a set of all {@link Transformation}s that can
     * have this skill.
     *
     * @see #getTransformationsAsArray()
     */
    public abstract Set<Transformation> getTransformations();

    /**
     * Does the same as {@link #getTransformations()}, except
     * that it returns an array instead of a set.
     *
     * @see #getTransformations()
     */
    public abstract Transformation[] getTransformationsAsArray();

    /**
     * Returns true if this skill is always active, meaning
     * that it works passively and that you therefore don't
     * have to activate it in the skill bar.
     */
    public abstract boolean isAlwaysActive();

    /**
     * Returns true if the given player can unlock this skill.
     */
    public abstract boolean canPlayerUnlockSkill(EntityPlayer player);

    /**
     * Returns the {@link ParentSkill} of this skill's group.
     * If this skill is a ParentSkill, this skill is returned.
     * If this skill is a ChildSkill, this skill's parent is returned.
     */
    public abstract ParentSkill getGroupParent();

    /**
     * Allocates a new modifiable list with all skills
     * that are required to unlock this skill. For parent
     * skills, this will simply return a new empty list,
     * for child skills, this will return this skill's
     * parent skill and all skills in this skill's skill
     * group with a level lower than this skill's level.
     */
    public abstract List<Skill> getRequiredSkills();

    /**
     * Returns the level of the skill with the
     * highest level in this skill's skill group.
     */
    public abstract int getMaximumLevel();

    /**
     * Returns a string that is used for translating
     * this skill's name.
     */
    public abstract String getTranslationKeyName();

    /**
     * Returns a string that is used for translating
     * this skill's description in the skill tab.
     */
    public abstract String getTranslationKeyDescription();

    /**
     * Returns a skill belonging to this skill's group
     * with the given level. Returns null if there's no
     * skill with the given level in this skill's group.
     */
    public abstract Skill getSkillWithLevel(int level);

    /**
     * Returns true if this skill belongs to the group of the
     * given {@link ParentSkill}.
     */
    public abstract boolean isLevelOf(ParentSkill parentSkill);

    /**
     * Returns the icon for this skill as a {@link ResourceLocation}.
     * The ResourceLocation is in the same format that is used
     * for stitching textures to the atlas. So if a ResourceLocation
     * {@code huntersdream:gui/skills/bite} would be returned, it would
     * actually point to {@code huntersdream:textures/gui/skills/bite.png}
     */
    public abstract ResourceLocation getIcon();

    /**
     * Returns the {@link TextureAtlasSprite} that was loaded from
     * the {@link ResourceLocation} returned by {@link #getIcon()}.
     * <br>
     * Only available on the client.
     */
    @SideOnly(Side.CLIENT)
    public abstract TextureAtlasSprite getIconAsSprite();

    /**
     * Sets the {@link TextureAtlasSprite} that will be returned when
     * calling {@link #getIconAsSprite()}.
     * <br>
     * Only available on the client.
     * <br>
     * This method is internal, so it could change, could be
     * completely removed or could even throw exceptions!
     */
    @SideOnly(Side.CLIENT)
    public abstract void setIconSprite(TextureAtlasSprite sprite);
}