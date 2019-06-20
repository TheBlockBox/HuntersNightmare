package theblockbox.huntersdream.items;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;
import theblockbox.huntersdream.api.HunterArmorEffect;

public class RecipeAddHunterArmorEffects extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        ItemStack armor = ItemStack.EMPTY;
        boolean foundEffect = false;
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof ItemHunterArmor) {
                    if (ItemHunterArmor.acceptsEffects(stack) && (ItemHunterArmor.getEffectFromStack(stack) == HunterArmorEffect.NONE) && armor.isEmpty()) {
                        // if the stack has no effect but could have one and no other armor
                        // has already matched that criteria, set "armor" equal to the stack
                        armor = stack;
                    } else {
                        // if the armor doesn't match the criteria, return false (as there can only be
                        // one armor part in the crafting table and that has to match the criteria)
                        return false;
                    }
                } else {
                    HunterArmorEffect effect = HunterArmorEffect.getEffectFromStack(stack);
                    if ((effect != HunterArmorEffect.NONE) && !foundEffect) {
                        // if the effect does not equal HunterArmorEffect#NONE and no other effect
                        // has already matched that criteria, set "newEffect" equal to the stack
                        foundEffect = true;
                    } else {
                        // return false if the item doesn't match the criteria or if there's another item that matches it
                        // (there can only be one item that isn't an instance of ItemHunterArmor in the recipe)
                        return false;
                    }
                }
            }
        }
        return !armor.isEmpty() && foundEffect;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack armor = ItemStack.EMPTY;
        HunterArmorEffect armorEffect = null;
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof ItemArmor) {
                    armor = stack;
                    if (!(stack.isEmpty() || ItemHunterArmor.acceptsEffects(stack))) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    HunterArmorEffect effect = HunterArmorEffect.getEffectFromStack(stack);
                    if (effect == HunterArmorEffect.NONE) {
                        return ItemStack.EMPTY;
                    } else {
                        armorEffect = effect;
                    }
                }
            }
        }
        armor = armor.copy();
        return ItemHunterArmor.setHunterArmorEffect(armor, armorEffect) ? armor : ItemStack.EMPTY;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Override
    public boolean canFit(int width, int height) {
        return (width * height) >= 2;
    }
}
