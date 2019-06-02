package theblockbox.huntersdream.items;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;
import theblockbox.huntersdream.api.HunterArmorEffect;

public class RecipeHunterArmorEffects extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
//    public RecipeHunterArmorEffects(ResourceLocation registryName) {
//        super(registryName.toString(), ItemStack.EMPTY, NonNullList.from(Ingredient.EMPTY, new CompoundIngredient(Arrays.asList(
//                new OreIngredient("hunterHat"), new OreIngredient("hunterTrenchcoat"), new OreIngredient("hunterPants"),
//                new OreIngredient("hunterBoots"))) {}, Ingredient.fromStacks(HunterArmorEffect.getAllItems().stream()
//                .map(item -> new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE)).toArray(ItemStack[]::new))));
//        this.setRegistryName(registryName);
//        for (Ingredient ingredient : this.getIngredients()) {
//            System.out.println(Arrays.toString(ingredient.getMatchingStacks()));
//        }
//    }
//
//    @Override
//    public ItemStack getRecipeOutput() {
//        return new ItemStack(ItemInit.HUNTER_HAT);
//    }
//
//    @Override
//    public boolean matches(InventoryCrafting inv, World worldIn) {
//        return super.matches(inv, worldIn) && this.getCraftingResult(inv).isEmpty();
//    }
//
//    @Override
//    public ItemStack getCraftingResult(InventoryCrafting inv) {
//        Item effect = null;
//        ItemStack armor = ItemStack.EMPTY;
//        for (int i = 0; i < inv.getHeight(); ++i) {
//            for (int j = 0; j < inv.getWidth(); ++j) {
//                ItemStack stack = inv.getStackInRowAndColumn(j, i);
//                if (!stack.isEmpty()) {
//                    Item item = stack.getItem();
//                    if (item instanceof ItemHunterArmor) {
//                        armor = stack.copy();
//                    } else {
//                        effect = item;
//                    }
//                }
//            }
//        }
//        //return ItemHunterArmor.setHunterArmorEffect(armor, HunterArmorEffect.getEffectFromItem(effect)) ? armor : ItemStack.EMPTY;
//        return new ItemStack(Items.IRON_SWORD);
//    }
//
//    @Override
//    public boolean isDynamic() {
//        return true;
//    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        ItemStack armor = ItemStack.EMPTY;
        boolean isEffectPresent = false;
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof ItemHunterArmor) {
                    if (armor.isEmpty() && ItemHunterArmor.acceptsEffects(stack)) {
                        armor = stack;
                    } else {
                        return false;
                    }
                } else {
                    if (HunterArmorEffect.getEffectFromItem(stack.getItem()) == HunterArmorEffect.NONE) {
                        return false;
                    } else {
                        isEffectPresent = true;
                    }
                }
            }
        }
        return !armor.isEmpty() && isEffectPresent;
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
                    HunterArmorEffect effect = HunterArmorEffect.getEffectFromItem(stack.getItem());
                    if (effect == HunterArmorEffect.NONE) {
                        return ItemStack.EMPTY;
                    } else {
                        armorEffect = effect;
                    }
                }
            }
        }
        return ItemHunterArmor.setHunterArmorEffect(armor, armorEffect) ? armor : ItemStack.EMPTY;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        NonNullList<ItemStack> list = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        for (int i = 0; i < list.size(); ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);
            list.set(i, net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack));
        }
        return list;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }
}
