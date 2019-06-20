package theblockbox.huntersdream.api;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreIngredient;
import org.apache.commons.lang3.ObjectUtils;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.items.ItemHunterArmor;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * This class represents effects that can be applied to items that are instances of {@link ItemHunterArmor}.
 */
public class HunterArmorEffect {
    private static final Map<ResourceLocation, HunterArmorEffect> EFFECT_MAP = new HashMap<>();
    /**
     * An unmodifiable collection with all HunterArmorEffects except for {@link #NONE}. <br>
     * Newly created effects are automatically added to this collection.
     */
    public static final Collection<HunterArmorEffect> EFFECTS = Collections.unmodifiableCollection(HunterArmorEffect.EFFECT_MAP.values());

    public static final HunterArmorEffect NONE = new HunterArmorEffect(GeneralHelper.newResLoc("none"),
            Ingredient.EMPTY);
    public static final HunterArmorEffect SILVER = new HunterArmorEffect(GeneralHelper.newResLoc("silver"),
            new OreIngredient("ingotSilver"));
    public static final HunterArmorEffect ACONITE = new HunterArmorEffect(GeneralHelper.newResLoc("aconite"),
            new OreIngredient("aconite"));

    private final ResourceLocation registryName;
    private final Ingredient ingredient;

    /**
     * Constructor used for creating new HunterArmorEffects. When created, they automatically get added to {@link #EFFECTS}
     * (except if the ingredient is null or {@link Ingredient#EMPTY}).
     */
    public HunterArmorEffect(@Nonnull ResourceLocation registryName, Ingredient ingredient) {
        this.registryName = registryName;
        this.ingredient = ingredient;
        if ((ingredient != null) && (ingredient != Ingredient.EMPTY)) {
            HunterArmorEffect oldEffect = HunterArmorEffect.EFFECT_MAP.get(registryName);
            if (oldEffect == null) {
                HunterArmorEffect.EFFECT_MAP.put(registryName, this);
            } else {
                Main.getLogger().error("Tried to create new HunterArmorEffect with the registry name " + registryName +
                        " but couldn't as the HunterArmorEffect " + oldEffect + " has already been registered with that name.");
            }
        }
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
            if (effect.getIngredient().apply(stack)) {
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
     * colons replaced with dots and {@code hunter_armor_effect.}. added before it. (So {@link #SILVER} with the registry
     * name {@code huntersdream:silver} would yield {@code hunter_armor_effect.huntersdream.silver}.)
     */
    public String getTranslationKey() {
        return "hunter_armor_effect." + this.getRegistryName().toString().replace(':', '.');
    }

    /**
     * Gets called every 101 ticks to apply the effect to the given entity on client and server. This won't be called at
     * all when the entity doesn't have the full armor set or only once when they have two armor parts with this effect,
     * so that there wouldn't be any difference between having the effect on one or two parts except if this method would
     * test for that specific case. (Though that last decision is given to the implementor.)
     */
    public void applyEffect(EntityLivingBase entity) {
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
