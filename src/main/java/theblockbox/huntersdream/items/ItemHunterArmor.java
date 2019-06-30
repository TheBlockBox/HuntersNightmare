package theblockbox.huntersdream.items;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ObjectUtils;
import theblockbox.huntersdream.api.HunterArmorEffect;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.api.init.ItemInit;
import theblockbox.huntersdream.entity.model.ModelHunterArmorHat;
import theblockbox.huntersdream.util.Reference;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

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
     * Tries to get a {@link HunterArmorEffect} from the given stack. The item of the stack should be an instance of
     * {@link ItemHunterArmor}. Otherwise, this will return {@link HunterArmorEffect#NONE}. If the armor has no effect,
     * this will also return {@link HunterArmorEffect#NONE}.
     */
    public static HunterArmorEffect getEffectFromStack(ItemStack stack) {
        if (ItemHunterArmor.acceptsEffects(stack)) {
            NBTTagCompound compound = stack.getTagCompound();
            if (compound != null) {
                return HunterArmorEffect.getEffectFromRegistryName(GeneralHelper.newResLoc(compound.getString("huntersdream:hunter_armor_effect")));
            }
        }
        return HunterArmorEffect.NONE;
    }

    /**
     * Tries to set the given item stack's hunter armor effect to the passed one. If this succeeds, true will be returned.
     * This will only work if {@link #acceptsEffects(ItemStack)} returns true. To reset the effect, pass
     * {@link HunterArmorEffect#NONE} for "effect" here.
     */
    public static boolean setHunterArmorEffect(ItemStack stack, HunterArmorEffect effect) {
        if (ItemHunterArmor.acceptsEffects(stack)) {
            GeneralHelper.getTagCompoundFromItemStack(stack).setString("huntersdream:hunter_armor_effect", effect.getRegistryName().toString());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ItemArmor.ArmorMaterial getArmorMaterial() {
        return ObjectUtils.defaultIfNull(ItemHunterArmor.manipulatedMaterial, super.getArmorMaterial());
    }

    @SideOnly(Side.CLIENT)
    @Nullable
    @Override
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped defaultModel) {
        return (armorSlot == EntityEquipmentSlot.HEAD) ? ModelHunterArmorHat.INSTANCE : null;
    }

    @Nullable
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        if (slot == EntityEquipmentSlot.HEAD) {
            String material = this.getArmorMaterial().getName();
            StringBuilder builder = new StringBuilder().append(Reference.MODID).append(":textures/models/armor/")
                    .append(material.substring(material.indexOf(':') + 1)).append("_layer_3");
            if ("overlay".equals(type)) {
                builder.append("_overlay");
            }
            return builder.append(".png").toString();
        } else {
            return super.getArmorTexture(stack, entity, slot, type);
        }
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

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        HunterArmorEffect effect = ItemHunterArmor.getEffectFromStack(stack);
        if (effect != HunterArmorEffect.NONE) {
            tooltip.add(I18n.format("huntersdream.hunter_armor_effect", I18n.format(effect.getTranslationKey())));
        }
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
