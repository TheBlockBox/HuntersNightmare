package theblockbox.huntersdream.items;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import theblockbox.huntersdream.api.HunterArmorEffect;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.api.init.ItemInit;
import theblockbox.huntersdream.entity.model.ModelHunterArmor;

import javax.annotation.Nullable;

// TODO: Finish
public class ItemHunterArmor extends ItemArmor {
    public ItemHunterArmor(int renderIndex, EntityEquipmentSlot equipmentSlot) {
        super(ItemInit.ARMOR_HUNTER, renderIndex, equipmentSlot);
    }

    /**
     * Returns true if the given item stack's item is an instance of ItemHunterArmor and could possibly have an effect
     * (meaning that it either already has an effect or that you could add one). Since it is only possible to add effects
     * to the helmet and chestplate, this will only return true for those and not for the leggings or boots.
     */
    public static boolean acceptsEffects(ItemStack stack) {
        if (stack.getItem() instanceof ItemHunterArmor) {
            ItemHunterArmor armor = (ItemHunterArmor) stack.getItem();
            return (armor.armorType == EntityEquipmentSlot.HEAD) || (armor.armorType == EntityEquipmentSlot.CHEST);
        }
        return false;
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

    @Nullable
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return super.getArmorTexture(stack, entity, slot, type);
    }

    @Nullable
    @Override
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped defaultModel) {
        return new ModelHunterArmor();
    }

    @Override
    public boolean hasOverlay(ItemStack stack) {
        return super.hasOverlay(stack);
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
        return 0;
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
}
