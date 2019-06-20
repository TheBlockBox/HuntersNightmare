package theblockbox.huntersdream.items;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;
import theblockbox.huntersdream.api.HunterArmorEffect;

public class RecipeRemoveHunterArmorEffects extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        boolean foundArmorWithEffect = false;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if ((stack.getItem() instanceof ItemHunterArmor) && (ItemHunterArmor.getEffectFromStack(stack) != HunterArmorEffect.NONE) && !foundArmorWithEffect) {
                // if the item is an instance of ItemHunterArmor, has an effect and no other
                // armor matching the criteria has been found, set foundArmorWithEffect to true
                foundArmorWithEffect = true;
            } else if (!stack.isEmpty()) {
                // if the stack doesn't match the criteria and isn't empty,
                // return false as this recipe only allows one item
                return false;
            }
        }
        return foundArmorWithEffect;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty() && (stack.getItem() instanceof ItemHunterArmor)) {
                return ItemHunterArmor.getEffectFromStack(stack).getReturnStack(stack);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        NonNullList<ItemStack> list = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        for (int i = 0; i < list.size(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty() && (stack.getItem() instanceof ItemHunterArmor)) {
                // copy the stack, reset HunterArmorEffect and let it stay in the inventory
                stack = stack.copy();
                ItemHunterArmor.setHunterArmorEffect(stack, HunterArmorEffect.NONE);
            }
            list.set(i, stack);
        }
        return list;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Override
    public boolean canFit(int width, int height) {
        return (width * height) >= 1;
    }
}
