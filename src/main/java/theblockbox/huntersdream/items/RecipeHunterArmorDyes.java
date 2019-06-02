package theblockbox.huntersdream.items;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipesArmorDyes;
import net.minecraft.world.World;

public class RecipeHunterArmorDyes extends RecipesArmorDyes {
    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        ItemHunterArmor.manipulatedMaterial = ItemArmor.ArmorMaterial.LEATHER;
        boolean toReturn = super.matches(inv, worldIn);
        ItemHunterArmor.manipulatedMaterial = null;
        return toReturn;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemHunterArmor.manipulatedMaterial = ItemArmor.ArmorMaterial.LEATHER;
        ItemStack toReturn = super.getCraftingResult(inv);
        ItemHunterArmor.manipulatedMaterial = null;
        return toReturn;
    }
}
