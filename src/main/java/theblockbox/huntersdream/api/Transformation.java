package theblockbox.huntersdream.api;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.api.event.TransformationRegistryEvent;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.api.helpers.VampireHelper;
import theblockbox.huntersdream.api.helpers.WerewolfHelper;
import theblockbox.huntersdream.util.ExecutionPath;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.exceptions.WrongTransformationException;
import theblockbox.huntersdream.util.interfaces.functional.ToFloatObjFloatFunction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.ObjDoubleConsumer;
import java.util.function.ToIntFunction;

import static theblockbox.huntersdream.api.Transformation.TransformationEntry.of;
import static theblockbox.huntersdream.api.Transformation.TransformationType.NORMAL;
import static theblockbox.huntersdream.api.Transformation.TransformationType.PHYSICAL_SUPERNATURAL;

/**
 * A class to represent transformations and their properties. To register new
 * ones subscribe to
 * {@link theblockbox.huntersdream.api.event.TransformationRegistryEvent} and
 * add your transformation. Create a new one with
 * {@link Transformation.TransformationEntry#create()}.
 */
public class Transformation {
    // Transformation constants
    /**
     * Used to indicate that no transformation is present and it won't change
     */
    public static final Transformation NONE = of("none").setTransformationType(NORMAL).create();
    /**
     * Used to indicate that no transformation is currently present but it could
     * change
     */
    public static final Transformation HUMAN = of("human").setTransformationType(NORMAL).create();
    /**
     * The transformation for werewolves
     */
    public static final Transformation WEREWOLF = of("werewolf")
            .setCalculateDamage(WerewolfHelper::calculateUnarmedDamage)
            .setCalculateReducedDamage(WerewolfHelper::calculateReducedDamage)
            .setTexturesHD("werewolf/werewolf_white").create();
    /**
     * The transformation for vampires
     */
    public static final Transformation VAMPIRE = of("vampire").setCalculateDamage(VampireHelper::calculateDamage)
            .setCalculateReducedDamage(VampireHelper::calculateReducedDamage).create();

    private static Transformation[] transformations = null;
    private final Transformation.TransformationEntry entry;
    private final ResourceLocation registryName;
    /**
     * The id of this transformation for this game session. Gets set when
     * transformation gets registered
     */
    private int temporaryID = -1;
    private final TextComponentTranslation translation;
    private final String registryNameString;
    private Object iconSprite = null;

    /**
     * Returns the transformation that has the same name. If no transformation has
     * that name, {@link Transformation#NONE} will be returned
     *
     * @param name The name of the resourcelocation (obtained through
     *             {@link ResourceLocation#toString()}, something like
     *             "huntersdream:werewolf"
     * @see #fromNameWithoutError(String)
     */
    public static Transformation fromName(String name) {
        Transformation transformation = Transformation.fromNameWithoutError(name);
        if (transformation == Transformation.NONE) {
            Main.getLogger()
                    .error("The given string \"" + name
                            + "\" does not have a corresponding transformation. Please report this\nStacktrace: "
                            + ExecutionPath.getAll());
        }
        return transformation;
    }

    /**
     * Returns the number of registered transformations
     */
    public static int getRegisteredTransformations() {
        // if not initialized, return 5 (default transformations)
        return (Transformation.transformations != null) ? Transformation.transformations.length : 5;
    }

    /**
     * Does exactly the same as {@link #fromName(String)} but without logging an
     * error
     *
     * @see #fromName(String)
     */
    public static Transformation fromNameWithoutError(String name) {
        for (Transformation transformation : Transformation.transformations)
            if (transformation.getRegistryName().toString().equals(name))
                return transformation;
        return Transformation.NONE;
    }

    /**
     * Does exactly the same as {@link #fromName(String)} except that it accepts a
     * {@link ResourceLocation}.
     *
     * @see #fromName(String)
     */
    public static Transformation fromResourceLocation(ResourceLocation resourceLocation) {
        return Transformation.fromName(resourceLocation.toString());
    }

    /**
     * Tries to get a transformation from its temporary id. For a version that works
     * without temporary ids over game restarts, use {@link #fromName(String)}.
     *
     * @see #fromName(String)
     */
    public static Transformation fromTemporaryID(int temporaryID) throws ArrayIndexOutOfBoundsException {
        return Transformation.transformations[temporaryID];
    }

    /**
     * Gets the transformation with the temporary id of the given transformation + 1
     * or 1 if a higher one doesn't exist (because 0 would be
     * {@link theblockbox.huntersdream.api.Transformation#NONE}). Doesn't allow
     * {@link Transformation#NONE}
     */
    public static Transformation cycle(Transformation transformation) {
        transformation.validateIsTransformation();
        int id = transformation.getTemporaryID();
        return Transformation.fromTemporaryID((Transformation.getRegisteredTransformations() > id) ? id + 1 : 1);
    }

    /**
     * Returns an array of all currently registered transformations
     */
    public static Transformation[] getAllTransformations() {
        return Transformation.transformations.clone();
    }

    /**
     * Called in the preInit phase to register all transformations. Should
     * <b>not</b> be called outside of Hunter's Dream.
     */
    public static void preInit() {
        TransformationRegistryEvent event = new TransformationRegistryEvent();
        MinecraftForge.EVENT_BUS.post(event);
        Transformation.transformations = event.getTransformations();
        for (int i = 0; i < Transformation.transformations.length; i++) {
            Transformation.transformations[i].temporaryID = i;
        }
    }

    /**
     * A protected constructor for everyone who needs to extend this class.
     *
     * @see Transformation.TransformationEntry#create()
     */
    protected Transformation(Transformation.TransformationEntry entry) {
        this.entry = entry;
        this.registryName = entry.registryName;
        this.registryNameString = this.registryName.toString();
        this.translation = new TextComponentTranslation(this.registryNameString);
    }

    /**
     * Gets the transformation with the temporary id of this transformation + 1 or 1
     * if this temporary id is the highest one (0 is always
     * {@link Transformation#NONE}, so that won't be used)
     */
    public Transformation cycle() {
        int newID = this.temporaryID + 1;
        return Transformation.fromTemporaryID((Transformation.getRegisteredTransformations() > newID) ? newID : 1);
    }

    /**
     * Returns true if this transformation has been registered
     */
    public boolean hasBeenRegistered() {
        return this.temporaryID >= 0;
    }

    /**
     * Returns this transformation's registry name
     */
    public ResourceLocation getRegistryName() {
        return this.registryName;
    }

    /**
     * Returns the {@link TextComponentTranslation} for this transformation
     */
    public TextComponentTranslation getTranslation() {
        return this.translation;
    }

    /**
     * This method returns true if the transformation is a transformation (meaning
     * it's not {@link Transformation#NONE})
     */
    public boolean isTransformation() {
        return this != Transformation.NONE;
    }

    /**
     * Throws an exception if {@link #isTransformation()} returns false
     */
    public void validateIsTransformation() {
        if (!this.isTransformation())
            throw new WrongTransformationException("No transformation not allowed here", this);
    }

    /**
     * Throws an exception if this transformation does not equal the given
     * transformation
     */
    public void validateEquals(Transformation other) {
        if (this != other)
            throw new WrongTransformationException(
                    String.format("The transformation should be \"%s\" but is \"%s\"\n", this, other), other);
    }

    /**
     * This method returns the resource location obtained through
     * {@link #getRegistryName()} in a string representation. So for example for
     * werewolf with a resource location of new ResourceLocation("huntersdream",
     * "werewolf") this method here would return "huntersdream:werewolf"
     */
    @Override
    public String toString() {
        return this.registryNameString;
    }

    public Transformation.TransformationType getTransformationType() {
        return this.entry.transformationType;
    }

    /**
     * Returns the resource locations of the textures this transformation has (used
     * for texture index)
     */
    public ResourceLocation[] getTextures() {
        return this.entry.textures.clone();
    }

    /**
     * Used to get the dealt damage from an entity and the initial damage. Currently
     * only used for players.
     */
    public float getDamage(EntityLivingBase entity, float initialDamage) {
        return this.entry.calculateDamage.applyAsFloat(entity, initialDamage);
    }

    /**
     * Used to get the damage an entity gets by passing the entity and the initial
     * damage the entity got
     */
    public float getReducedDamage(EntityLivingBase entity, float initialDamage) {
        return this.entry.calculateReducedDamage.applyAsFloat(entity, initialDamage);
    }

    /**
     * Returns the texture index an entity should get when it just spawned/changed
     * transformation.
     */
    public int getTextureIndexForEntity(EntityLivingBase entity) {
        return this.entry.getTextureIndex.applyAsInt(entity);
    }

    public Consumer<EntityLivingBase> getInfect() {
        return this.entry.infect;
    }

    /**
     * Returns the resource location to the icon of this transformation. The icon is
     * used in the skill survival tab. This is currently only used to get the
     * {@link TextureAtlasSprite} for this transformation. The returned resource
     * location doesn't include textures/ and .png because they're not required to
     * get a {@link TextureAtlasSprite}. So a resource location
     * {@code huntersdream:gui/icon_werewolf} would point to
     * {@code huntersdream:textures/gui/icon_werewolf.png}.
     */
    public ResourceLocation getIcon() {
        return this.entry.icon;
    }

    /**
     * Returns the {@link TextureAtlasSprite} for the icon of this transformation.
     * If this transformation is {@link Transformation#NONE} or this method is
     * called before {@link net.minecraftforge.client.event.TextureStitchEvent.Pre}
     * has been fired for the first time, null will be returned. This method is
     * client side only to prevent crashes.
     */
    @SideOnly(Side.CLIENT)
    @Nullable
    public TextureAtlasSprite getIconAsSprite() {
        return (TextureAtlasSprite) this.iconSprite;
    }

    /**
     * Sets the icon sprite for this transformation. Should only be used internally
     * and on the client side. Don't call this outside of Hunter's Dream!
     */
    @SideOnly(Side.CLIENT)
    public void setIconSprite(@Nonnull TextureAtlasSprite sprite) {
        if (sprite == null)
            throw new NullPointerException("The icon sprite is not allowed to be null");
        this.iconSprite = sprite;
    }

    /**
     * Returns a temporary id for the transformation. Don't use the id for writing
     * to or reading from nbt because it may change the next time the game is
     * started
     */
    public int getTemporaryID() {
        return this.temporaryID;
    }

    /**
     * {@inheritDoc}<br>
     * <br>
     * The transformation version always returns the temporary id
     * ({@link #getTemporaryID()})
     *
     * @see #getTemporaryID()
     */
    @Override
    public int hashCode() {
        return this.getTemporaryID();
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }

    public enum TransformationType {
        NORMAL(false), PHYSICAL_SUPERNATURAL(true), UNPHYSICAL_SUPERNATURAL(true);
        private boolean supernatural;

        TransformationType(boolean supernatural) {
            this.supernatural = supernatural;
        }

        public boolean isSupernatural() {
            return this.supernatural;
        }
    }

    /**
     * Used to register new transformations. All set methods are optional.
     */
    public static class TransformationEntry {
        private final ResourceLocation registryName;
        private Transformation.TransformationType transformationType = PHYSICAL_SUPERNATURAL;
        private ResourceLocation[] textures = new ResourceLocation[0];
        private Consumer<EntityLivingBase> infect = null;
        /**
         * An {@link ObjDoubleConsumer} that is executed when the player levels up
         */
        private ToFloatObjFloatFunction<EntityLivingBase> calculateDamage = (e, f) -> f;
        private ToFloatObjFloatFunction<EntityLivingBase> calculateReducedDamage = (e, f) -> f;
        private ToIntFunction<EntityLivingBase> getTextureIndex = e -> 0;
        private ResourceLocation icon;

        private TransformationEntry(ResourceLocation registryName) {
            this.registryName = registryName;
            this.icon = new ResourceLocation(registryName.getNamespace(), "gui/icon_" + registryName.getPath());
        }

        /**
         * Returns a new instance of type TransformationEntry from the given
         * ResourceLocation.
         *
         * @param registryName A ResourceLocation that should be used as the registry
         *                     name for the Transformation that is going to be created.
         */
        public static Transformation.TransformationEntry of(ResourceLocation registryName) {
            return new Transformation.TransformationEntry(registryName);
        }

        /**
         * Returns a new instance of type TransformationEntry from the given
         * ResourceLocation. Exists for Hunter's Dream only!
         */
        static Transformation.TransformationEntry of(String name) {
            return new Transformation.TransformationEntry(GeneralHelper.newResLoc(name));
        }

        /**
         * Sets the {@link Transformation.TransformationType}. The default value is
         * {@link Transformation.TransformationType#PHYSICAL_SUPERNATURAL}.
         */
        public Transformation.TransformationEntry setTransformationType(Transformation.TransformationType type) {
            this.transformationType = type;
            return this;
        }

        /**
         * Sets a {@link ToFloatObjFloatFunction} to calculate the damage an entity,
         * that has this transformation, deals
         */
        public Transformation.TransformationEntry setCalculateDamage(ToFloatObjFloatFunction<EntityLivingBase> calculateDamage) {
            this.calculateDamage = calculateDamage;
            return this;
        }

        /**
         * Sets a {@link ToFloatObjFloatFunction} to calculate the damage an entity,
         * that has this transformation, gets
         */
        public Transformation.TransformationEntry setCalculateReducedDamage(
                ToFloatObjFloatFunction<EntityLivingBase> calculateReducedDamage) {
            this.calculateReducedDamage = calculateReducedDamage;
            return this;
        }

        /**
         * Sets the {@link ResourceLocation}s of the textures for this transformation.
         */
        public Transformation.TransformationEntry setTextures(ResourceLocation... textures) {
            this.textures = textures;
            return this;
        }

        private Transformation.TransformationEntry setTexturesHD(String... textures) {
            ResourceLocation[] resourceLocations = new ResourceLocation[textures.length];
            for (int i = 0; i < textures.length; i++) {
                resourceLocations[i] = GeneralHelper.newResLoc(Reference.ENTITY_TEXTURE_PATH + textures[i] + ".png");
            }
            this.textures = resourceLocations;
            return this;
        }

        /**
         * Sets the given {@link Consumer} to the function that is called when an entity
         * is infected with this tranformation
         *
         * @param infect Gets called when an entity is infected with this tranformation.
         *               Is allowed to be null.
         */
        public Transformation.TransformationEntry setInfect(@Nullable Consumer<EntityLivingBase> infect) {
            this.infect = infect;
            return this;
        }

        /**
         * Sets the getTextureIndex method to the given {@link ToIntFunction} that will
         * be called to determine the texture index of an entity.
         */
        public Transformation.TransformationEntry setGetTextureIndex(ToIntFunction<EntityLivingBase> getTextureIndex) {
            this.getTextureIndex = getTextureIndex;
            return this;
        }

        /**
         * Sets the icon that is used in {@link Transformation#getIcon()} to the given
         * ResourceLocation. If this method is not called, the icon will be an auto
         * generated ResourceLocation in the format
         * {@code modid:textures/gui/icon_transformation_name.png}.
         */
        public void setIcon(ResourceLocation icon) {
            this.icon = icon;
        }

        /**
         * Creates a new Transformation from this TransformationEntry
         */
        public Transformation create() {
            return new Transformation(this);
        }
    }
}
