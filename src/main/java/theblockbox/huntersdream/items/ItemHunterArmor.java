package theblockbox.huntersdream.items;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.ObjectUtils;
import theblockbox.huntersdream.api.HunterArmorEffect;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.api.init.ItemInit;
import theblockbox.huntersdream.entity.model.ModelHunterArmor;
import theblockbox.huntersdream.util.Reference;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// TODO: Finish
public class ItemHunterArmor extends ItemArmor {
    /**
     * If not null, the value of this package-private field will be returned in {@link #getArmorMaterial()}.
     * Used in {@link RecipeHunterArmorDyes}.
     */
    static ItemArmor.ArmorMaterial manipulatedMaterial = null;

    public ItemHunterArmor(int renderIndex, EntityEquipmentSlot equipmentSlot) {
        super(ItemInit.ARMOR_HUNTER, renderIndex, equipmentSlot);
    }

    public static HunterArmorEffect[] getEffectsFromEntity(EntityLivingBase entity) {
        List<HunterArmorEffect> effects = new ArrayList<>(4);
        for (ItemStack armor : entity.getArmorInventoryList()) {
            if (armor.getItem() instanceof ItemHunterArmor) {
                // add all effects
                effects.add(ItemHunterArmor.getEffectFromStack(armor));
            } else {
                // if one armor piece is not present, don't activate any effects
                return new HunterArmorEffect[0];
            }
        }
        // if less than four armor pieces are present,
        if (effects.size() < 4) {
            // don't activate any effects
            return new HunterArmorEffect[0];
        }
        return effects.stream().distinct().filter(effect -> effect != HunterArmorEffect.NONE).toArray(HunterArmorEffect[]::new);
    }

    /**
     * Returns true if the given item stack's item is an instance of ItemHunterArmor and could possibly have an effect
     * (meaning that it either already has an effect or that you could add one). By default, this will only return true
     * when the item's equipment slot is either {@link EntityEquipmentSlot#HEAD} or {@link EntityEquipmentSlot#CHEST},
     * although this can be different if the item overrides {@link #doesItemAcceptEffects(ItemStack)}.
     */
    public static boolean acceptsEffects(ItemStack stack) {
        return (stack.getItem() instanceof ItemHunterArmor) && ((ItemHunterArmor) stack.getItem()).doesItemAcceptEffects(stack);
    }

    /**
     * Tries to get a {@link HunterArmorEffect} from the given stack. If none could be gotten,
     * {@link HunterArmorEffect#NONE} will be returned. This is also true for items that can't have any
     * effect in the first place.
     */
    public static HunterArmorEffect getEffectFromStack(ItemStack stack) {
        if (ItemHunterArmor.acceptsEffects(stack)) {
            NBTTagCompound compound = stack.getTagCompound();
            if (compound != null) {
                return HunterArmorEffect.getEffectFromItem(Item.getByNameOrId(compound.getString("huntersdream:effectItem")));
            }
        }
        return HunterArmorEffect.NONE;
    }

    /**
     * Tries to set the given item stack's hunter armor effect to the passed one. If this succeeds, true will be returned.
     * This will only work if {@link #acceptsEffects(ItemStack)} returns true.
     */
    public static boolean setHunterArmorEffect(ItemStack stack, HunterArmorEffect effect) {
        if (ItemHunterArmor.acceptsEffects(stack)) {
            GeneralHelper.getTagCompoundFromItemStack(stack).setString("huntersdream:effectItem", effect.getEffectItem().getRegistryName().toString());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ItemArmor.ArmorMaterial getArmorMaterial() {
        return ObjectUtils.defaultIfNull(ItemHunterArmor.manipulatedMaterial, super.getArmorMaterial());
    }

    @Nullable
    @Override
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped defaultModel) {
        return ((armorSlot == EntityEquipmentSlot.LEGS) || (armorSlot == EntityEquipmentSlot.FEET)) ? null : new ModelHunterArmor();
    }

    @Nullable
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        String material = this.getArmorMaterial().getName();
        return String.format("%s:textures/models/armor/%s_layer_%s.png", Reference.MODID, material.substring(material.indexOf(':') + 1),
                Objects.equals(type, "overlay") ? "2" : "1");
    }

    @Override
    public boolean hasOverlay(ItemStack stack) {
        return true;
    }

    @Override
    public boolean hasColor(ItemStack stack) {
        NBTTagCompound compound = stack.getTagCompound();
        return (compound != null) && compound.hasKey("display", 10) && compound.getCompoundTag("display").hasKey("color", 3);
    }

    @Override
    public int getColor(ItemStack stack) {
        NBTTagCompound compound = stack.getTagCompound();
        if (compound != null) {
            NBTTagCompound display = compound.getCompoundTag("display");
            if (display.hasKey("color", 3)) {
                return display.getInteger("color");
            }
        }
        return 7427149;
    }

    @Override
    public void removeColor(ItemStack stack) {
        NBTTagCompound compound = stack.getTagCompound();
        if (compound != null) {
            NBTTagCompound display = compound.getCompoundTag("display");
            if (display.hasKey("color")) {
                display.removeTag("color");
            }
        }
    }

    @Override
    public void setColor(ItemStack stack, int color) {
        NBTTagCompound compound = GeneralHelper.getTagCompoundFromItemStack(stack);
        NBTTagCompound display = compound.getCompoundTag("display");
        if (!compound.hasKey("display", 10)) {
            compound.setTag("display", display);
        }
        display.setInteger("color", color);
    }

    /**
     * Returns true if effects can be put on the given item stack. The item stack's item should always be the same as this
     * class, so it is allowed to immediately cast the item. Currently only called in {@link #acceptsEffects(ItemStack)}.
     * The default version only returns true if the item's equipment slot is either {@link EntityEquipmentSlot#HEAD} or
     * {@link EntityEquipmentSlot#CHEST}, though this method can be overridden for custom handling.
     */
    protected boolean doesItemAcceptEffects(ItemStack stack) {
        EntityEquipmentSlot armorType = ((ItemHunterArmor) stack.getItem()).armorType;
        return (armorType == EntityEquipmentSlot.HEAD) || (armorType == EntityEquipmentSlot.CHEST);
    }
}
