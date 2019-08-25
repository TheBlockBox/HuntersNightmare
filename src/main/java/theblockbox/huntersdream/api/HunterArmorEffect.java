package theblockbox.huntersdream.api;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreIngredient;
import org.apache.commons.lang3.ObjectUtils;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.api.helpers.WerewolfHelper;
import theblockbox.huntersdream.items.ItemHunterArmor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * This class represents effects that can be applied to items that are instances of {@link ItemHunterArmor}.
 */
public class HunterArmorEffect {
    private static final Map<ResourceLocation, HunterArmorEffect> EFFECT_MAP = new HashMap<>();
    /**
     * An unmodifiable collection with all HunterArmorEffects. <br>
     * Newly created effects are automatically added to this collection.
     */
    public static final Collection<HunterArmorEffect> EFFECTS = Collections.unmodifiableCollection(HunterArmorEffect.EFFECT_MAP.values());

    public static final HunterArmorEffect NONE = new HunterArmorEffect(GeneralHelper.newResLoc("none"), Ingredient.EMPTY);
    public static final HunterArmorEffect ACONITE = new HunterArmorEffect(GeneralHelper.newResLoc("aconite"),
            new OreIngredient("aconite"), entity -> WerewolfHelper.applyWolfsbaneEffects(entity, false, false));
    public static final HunterArmorEffect MONKSHOOD = new HunterArmorEffect(GeneralHelper.newResLoc("monkshood"),
            new OreIngredient("monkshood"), entity -> WerewolfHelper.applyWolfsbaneEffects(entity, false, false));
    public static final HunterArmorEffect WOLFSBANE = new HunterArmorEffect(GeneralHelper.newResLoc("wolfsbane"),
            new OreIngredient("wolfsbane"), entity -> WerewolfHelper.applyWolfsbaneEffects(entity, false, false));
    public static final HunterArmorEffect HEALING = new HunterArmorEffect(GeneralHelper.newResLoc("healing"),
            new OreIngredient("herbHealing"), new PotionEffect(MobEffects.REGENERATION, 103));
    public static final HunterArmorEffect GLOW_FERN = new HunterArmorEffect(GeneralHelper.newResLoc("glow_fern"),
            new OreIngredient("glowfern"), new PotionEffect(MobEffects.NIGHT_VISION, 400));
    public static final HunterArmorEffect MAGMA_FLOWER = new HunterArmorEffect(GeneralHelper.newResLoc("magma_flower"),
            new OreIngredient("flowerMagma"), new PotionEffect(MobEffects.FIRE_RESISTANCE, 103));
    public static final HunterArmorEffect POISON_IVY = new HunterArmorEffect(GeneralHelper.newResLoc("poison_ivy"),
            new OreIngredient("ivyPoison"));
    public static final HunterArmorEffect WITHER_MOSS = new HunterArmorEffect(GeneralHelper.newResLoc("wither_moss"),
            new OreIngredient("mossWither"));

    private final ResourceLocation registryName;
    private final Ingredient ingredient;
    private final Consumer<EntityLivingBase> applyEffect;

    /**
     * Constructor used for creating new HunterArmorEffects. When created, they automatically get added to {@link #EFFECTS}.
     * The given {@link Ingredient} will be used to create a recipe for adding the effect. If {@link Ingredient#EMPTY} is
     * given, no recipe will be added. The given {@link Consumer}, if not null, will be called in {@link #applyEffect(EntityLivingBase)}
     * (for more info, see there).
     */
    public HunterArmorEffect(@Nonnull ResourceLocation registryName, @Nonnull Ingredient ingredient, @Nullable Consumer<EntityLivingBase> applyEffect) {
        this.registryName = registryName;
        this.ingredient = ingredient;
        HunterArmorEffect oldEffect = HunterArmorEffect.EFFECT_MAP.get(registryName);
        if (oldEffect == null) {
            HunterArmorEffect.EFFECT_MAP.put(registryName, this);
        } else {
            Main.getLogger().error("Tried to create new HunterArmorEffect with the registry name " + registryName +
                    " but couldn't as the HunterArmorEffect " + oldEffect + " has already been registered with that name.");
        }
        this.applyEffect = applyEffect;
    }

    /**
     * Same as {@link HunterArmorEffect#HunterArmorEffect(ResourceLocation, Ingredient, Consumer)} except that here,
     * a copy of the given {@link PotionEffect} is applied in {@link #applyEffect(EntityLivingBase)} instead of a {@link Consumer}.
     * (The {@link PotionEffect} is only added on the server side, but minecraft will automatically sync it to the clients.)
     */
    public HunterArmorEffect(@Nonnull ResourceLocation registryName, @Nonnull Ingredient ingredient, PotionEffect effectToApply) {
        this(registryName, ingredient, entity -> {
            if (!entity.world.isRemote) {
                entity.addPotionEffect(new PotionEffect(effectToApply));
            }
        });
    }

    /**
     * Same as {@link HunterArmorEffect#HunterArmorEffect(ResourceLocation, Ingredient, Consumer)} except that here,
     * nothing will be done in {@link #applyEffect(EntityLivingBase)}, except if it is overridden.
     */
    public HunterArmorEffect(@Nonnull ResourceLocation registryName, @Nonnull Ingredient ingredient) {
        this(registryName, ingredient, (Consumer<EntityLivingBase>) null);
    }

    /**
     * Returns an effect from the given registry name. If no effect with that registry name could be found,
     * {@link HunterArmorEffect#NONE} will be returned.
     */
    public static HunterArmorEffect getEffectFromRegistryName(ResourceLocation registryName) {
        return ObjectUtils.defaultIfNull(HunterArmorEffect.EFFECT_MAP.get(registryName), HunterArmorEffect.NONE);
    }

    /**
     * Returns an instance of {@link HunterArmorEffect} for the given stack by testing if the effect's {@link Ingredient}
     * applies. If no fitting effect could be found, {@link #NONE} will be returned.
     */
    public static HunterArmorEffect getEffectFromStack(ItemStack stack) {
        for (HunterArmorEffect effect : HunterArmorEffect.EFFECTS) {
            if (!stack.isEmpty() && effect.getIngredient().apply(stack)) {
                return effect;
            }
        }
        return HunterArmorEffect.NONE;
    }

    /**
     * Returns the ingredient associated with this effect to determine which items it can be crafted with.
     */
    public Ingredient getIngredient() {
        return this.ingredient;
    }

    /**
     * Returns a newly allocated stack that will be returned when crafting the given armor stack back.
     * The default implementation returns a copy of the first item stack that the ingredient of this effect allows.
     */
    public ItemStack getReturnStack(ItemStack armor) {
        return this.getIngredient().getMatchingStacks()[0].copy();
    }

    /**
     * Returns the string used to translate this effect. The default implementation returns the registry name with all
     * colons replaced with dots and {@code hunter_armor_effect.}. added before it. (So {@link #ACONITE} with the registry
     * name {@code huntersdream:aconite} would yield {@code hunter_armor_effect.huntersdream.aconite}.)
     */
    public String getTranslationKey() {
        return "hunter_armor_effect." + this.getRegistryName().toString().replace(':', '.');
    }

    /**
     * Gets called every 101 ticks to apply the effect to the given entity on client and server. This won't be called at
     * all when the entity doesn't have the full armor set or only once when they have two armor parts with this effect,
     * so that there wouldn't be any difference between having the effect on one or two parts except if this method would
     * test for that specific case. (Though that last decision is given to the implementor.) The default implementation
     * calls the {@link Consumer} given in the constructor.
     */
    public void applyEffect(EntityLivingBase entity) {
        if (this.applyEffect != null) {
            this.applyEffect.accept(entity);
        }
    }

    /**
     * Returns this effects's unique registry name.
     */
    public ResourceLocation getRegistryName() {
        return this.registryName;
    }

    @Override
    public String toString() {
        return this.getRegistryName().toString();
    }
}
