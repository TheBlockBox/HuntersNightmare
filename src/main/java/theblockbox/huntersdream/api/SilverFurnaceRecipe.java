package theblockbox.huntersdream.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.util.Reference;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

public class SilverFurnaceRecipe {
    public static final int DEFAULT_SMELTING_TIME = 200;
    private static final Set<SilverFurnaceRecipe> RECIPES = new HashSet<>();
    private static int currentID = 0;
    private final Ingredient in1;
    private final Ingredient in2;
    private final int amount1;
    private final int amount2;
    private final ItemStack out1;
    private final ItemStack out2;
    private final int smeltingTime;
    private final int id;

    public SilverFurnaceRecipe(Ingredient input1, Ingredient input2, int amount1, int amount2, ItemStack output1,
                               ItemStack output2, int smeltingTime) {
        this.in1 = input1;
        this.in2 = input2;
        this.amount1 = amount1;
        this.amount2 = amount2;
        this.out1 = output1;
        this.out2 = output2;
        this.smeltingTime = smeltingTime;
        this.id = SilverFurnaceRecipe.currentID++;
    }

    public SilverFurnaceRecipe(Ingredient input1, Ingredient input2, int amount1, int amount2, ItemStack output1,
                               ItemStack output2) {
        this(input1, input2, amount1, amount2, output1, output2, SilverFurnaceRecipe.DEFAULT_SMELTING_TIME);
    }

    public static void addRecipe(SilverFurnaceRecipe recipe) {
        SilverFurnaceRecipe.RECIPES.add(recipe);
    }

    public static void addRecipe(JsonObject json) {
        try {
            if (!json.get("type").getAsString().equals(Reference.MODID + ":silver_furnace_recipe"))
                throw new JsonParseException(
                        String.format("The type of a silver furnace recipe was \"%s\" but should have been \"%s\"",
                                json.get("type").getAsString(), Reference.MODID + ":silver_furnace_recipe"));
            int smeltingTime = JsonUtils.getInt(json, "smeltingTime", SilverFurnaceRecipe.DEFAULT_SMELTING_TIME);
            JsonObject input1 = json.getAsJsonObject("input1");
            JsonObject input2 = json.getAsJsonObject("input2");
            Ingredient inputIngredient1 = CraftingHelper.getIngredient(input1, new JsonContext("minecraft"));
            Ingredient inputIngredient2 = input2 == null ? Ingredient.EMPTY : CraftingHelper.getIngredient(input2,
                    new JsonContext("minecraft"));
            ItemStack output1 = json.has("output1") ? CraftingHelper.getItemStack(json.get("output1")
                    .getAsJsonObject(), new JsonContext("minecraft")) : ItemStack.EMPTY;
            ItemStack output2 = json.has("output2") ? CraftingHelper.getItemStack(json.get("output2")
                    .getAsJsonObject(), new JsonContext("minecraft")) : ItemStack.EMPTY;
            SilverFurnaceRecipe recipe = new SilverFurnaceRecipe(inputIngredient1, inputIngredient2,
                    JsonUtils.getInt(input1, "amount", 1), (input2 == null) ? 0 :
                    JsonUtils.getInt(input2, "amount", 1), output1, output2, smeltingTime);
            SilverFurnaceRecipe.addRecipe(recipe);
            if (JsonUtils.getBoolean(json, "generateMirror", false))
                SilverFurnaceRecipe.addRecipe(recipe.getMirror());
        } catch (Exception e) {
            Main.getLogger().error("An exception occured while trying to parse from json to silver furnace recipe\n"
                    + "Exception: " + e + " Stacktrace:\n" + ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * Creates new files and directories for the silver furnace recipes if they
     * haven't already been created and parses all the files in the directory to
     * silver furnace recipes
     */
    public static void setAndLoadFiles(File directory) {
        File sfrLocation = new File(directory, "huntersdream/silver_furnace_recipes");
        // if directory doesn't exist, make it and add default files
        if (!sfrLocation.exists()) {
            sfrLocation.mkdirs();
            // add files here
        }
        JsonParser parser = new JsonParser();
        FileUtils.listFiles(sfrLocation, new String[]{"json"}, true).stream().map(file -> {
            JsonObject json = null;
            try (Reader reader = new InputStreamReader(FileUtils.openInputStream(file))) {
                json = parser.parse(reader).getAsJsonObject();
            } catch (JsonParseException e) {
                Main.getLogger().error(String.format(
                        "An exception occured while trying to parse the silver furnace recipe \"%s\" (full path: %s)\nException: %s Stacktrace:\n%s",
                        file.getName(), file.getAbsolutePath(), e.toString(), ExceptionUtils.getStackTrace(e)));
            } catch (IOException e) {
                Main.getLogger().error(String.format(
                        "An exception occured while trying to get/load the silver furnace recipe \"%s\" (full path: %s)\nException: %s Stacktrace:\n%s",
                        file.getName(), file.getAbsolutePath(), e.toString(), ExceptionUtils.getStackTrace(e)));
            }
            return json;
        }).filter(Objects::nonNull).forEach(SilverFurnaceRecipe::addRecipe);
    }

    public static Optional<SilverFurnaceRecipe> getFromInput(ItemStack input1, ItemStack input2) {
        return SilverFurnaceRecipe.RECIPES.stream().filter(recipe -> recipe.matches(input1, input2)).findFirst();
    }

    /**
     * Returns the count of the itemstack that is in input 1
     */
    public int getAmount1() {
        return this.amount1;
    }

    /**
     * Returns the count of the itemstack that is in input 2
     */
    public int getAmount2() {
        return this.amount2;
    }

    /**
     * Returns a copy of the first itemstack that should be outputted
     */
    public ItemStack getOutput1(Random random) {
        return this.out1.copy();
    }

    /**
     * Returns a copy of the second itemstack that should be outputted
     */
    public ItemStack getOutput2(Random random) {
        return this.out2.copy();
    }

    /**
     * Returns the time that is needed to smelt this recipe in ticks. Default is 200
     */
    public int getSmeltingTime() {
        return this.smeltingTime;
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof SilverFurnaceRecipe) && (obj.hashCode() == this.hashCode());
    }

    public boolean matches(ItemStack input1, ItemStack input2) {
        return this.in1.test(input1) && this.in2.test(input2);
    }

    public SilverFurnaceRecipe getMirror() {
        return new SilverFurnaceRecipe(this.in2, this.in1, this.amount2, this.amount1, this.out1, this.out2, this.smeltingTime);
    }
}
