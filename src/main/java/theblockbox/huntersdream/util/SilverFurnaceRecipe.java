package theblockbox.huntersdream.util;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.JsonUtils;
import net.minecraftforge.oredict.OreDictionary;
import theblockbox.huntersdream.Main;

public class SilverFurnaceRecipe {
	public static final Pair<ItemStack, String> EMPTY_PAIR = new ImmutablePair<>(ItemStack.EMPTY, null);
	private static final Set<SilverFurnaceRecipe> RECIPES = new HashSet<>();
	private static int currentID = 0;

	private final ItemStack in1;
	private final ItemStack in2;
	private final String oreDictName1;
	private final String oreDictName2;
	private final ItemStack out1;
	private final ItemStack out2;
	private final float xp;
	private final int smeltingTime;
	private final int id;

	protected SilverFurnaceRecipe(ItemStack input1, ItemStack input2, @Nullable String oreDictName1,
			@Nullable String oreDictName2, ItemStack output1, ItemStack output2, float experience, int smeltingTime) {
		this.in1 = input1;
		this.in2 = input2;
		this.oreDictName1 = oreDictName1;
		this.oreDictName2 = oreDictName2;
		this.out1 = output1;
		this.out2 = output2;
		this.xp = experience;
		this.smeltingTime = smeltingTime;
		this.id = currentID++;
	}

	public static void addRecipe(ItemStack input1, ItemStack input2, @Nullable String oreDictName1,
			@Nullable String oreDictName2, ItemStack output1, ItemStack output2, float experience, int smeltingTime) {
		RECIPES.add(new SilverFurnaceRecipe(input1, input2, oreDictName1, oreDictName2, output1, output2, experience,
				smeltingTime));
	}

	public static void addRecipe(JsonObject json) {
		try {
			if (!json.get("type").getAsString().equals(Reference.MODID + ":silver_furnace_recipe"))
				throw new JsonParseException(
						String.format("The type of a silver furnace recipe was \"%s\" but should have been \"%s\"",
								json.get("type").getAsString(), Reference.MODID + ":silver_furnace_recipe"));

			int smeltingTime = json.has("smeltingTime") ? json.get("smeltingTime").getAsInt() : 200;
			float experience = json.get("experience").getAsFloat();
			Pair<ItemStack, String> input1 = readInputStack(json.get("input1").getAsJsonObject());
			Pair<ItemStack, String> input2 = json.has("input2") ? readInputStack(json.get("input2").getAsJsonObject())
					: EMPTY_PAIR;
			ItemStack output1 = json.has("output1") ? readOutputStack(json.get("output1").getAsJsonObject())
					: ItemStack.EMPTY;
			ItemStack output2 = json.has("output2") ? readOutputStack(json.get("output2").getAsJsonObject())
					: ItemStack.EMPTY;

			addRecipe(input1.getLeft(), input2.getLeft(), input1.getRight(), input2.getRight(), output1, output2,
					experience, smeltingTime);
			if (json.has("generateMirror") ? json.get("generateMirror").getAsBoolean() : false)
				addRecipe(input2.getLeft(), input1.getLeft(), input2.getRight(), input1.getRight(), output1, output2,
						experience, smeltingTime);
		} catch (Exception e) {
			Main.getLogger().error("An exception occured while trying to parse from json to silver furnace recipe\n"
					+ "Exception: " + e.toString() + " Stacktrace:\n" + ExceptionUtils.getStackTrace(e));
		}
	}

	private static Pair<ItemStack, String> readInputStack(JsonObject json) {
		boolean usesOreDict = json.has("useOreDict") ? json.get("useOreDict").getAsBoolean() : false;
		int meta = json.has("meta") ? json.get("meta").getAsInt() : OreDictionary.WILDCARD_VALUE;
		int count = json.has("count") ? json.get("count").getAsInt() : 1;
		String item = json.get("item").getAsString();
		if (usesOreDict) {
			return new ImmutablePair<>(new ItemStack((Item) null, count, meta), item);
		} else {
			return new ImmutablePair<>(new ItemStack(Item.getByNameOrId(item), count, meta), null);
		}
	}

	private static ItemStack readOutputStack(JsonObject json) {
		Item item = Item.getByNameOrId(json.get("item").getAsString());
		int count = json.get("count").getAsInt();
		int meta = json.has("meta") ? json.get("meta").getAsInt() : OreDictionary.WILDCARD_VALUE;
		return json.has("nbt") ? new ItemStack(item, count, meta, JsonUtils.readNBT(json, "nbt"))
				: new ItemStack(item, count, meta);
	}

	public static Optional<SilverFurnaceRecipe> getFromInput(ItemStack input1, ItemStack input2) {
		return RECIPES.stream().filter(recipe -> recipe.matches(input1, input2)).findFirst();
	}

	/** Returns the count of the itemstack that is in input 1 */
	public int getAmount1() {
		return this.in1.isEmpty() ? 0 : this.in1.getCount();
	}

	/** Returns the count of the itemstack that is in input 2 */
	public int getAmount2() {
		return this.in2.isEmpty() ? 0 : this.in2.getCount();
	}

	/** Returns a copy of the first itemstack that should be outputted */
	public ItemStack getOutput1() {
		return this.out1.copy();
	}

	/** Returns a copy of the second itemstack that should be outputted */
	public ItemStack getOutput2() {
		return this.out2.copy();
	}

	/**
	 * Returns the experience gotten when the player takes the result out of the
	 * furnace
	 */
	public float getExperience() {
		return this.xp;
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

	private static boolean doItemStacksMatch(ItemStack stack1, ItemStack stack2, @Nullable String oreDictName) {
		if (stack1.isEmpty() && stack2.isEmpty())
			return true;
		if (stack1.getMetadata() == OreDictionary.WILDCARD_VALUE || stack1.getMetadata() == stack2.getMetadata()
				&& stack2.getCount() >= stack1.getCount() && stack1.areCapsCompatible(stack2)) {
			if (oreDictName == null) {
				final int[] oreDictIDs = OreDictionary.getOreIDs(stack2);
				final int oreDictID = OreDictionary.getOreID(oreDictName);
				for (int i = 0; i < oreDictIDs.length; i++)
					if (oreDictID == oreDictIDs[i])
						return true;
			} else {
				return stack1.getItem() == stack2.getItem();
			}
		}
		return false;
	}

	public boolean matches(ItemStack input1, ItemStack input2) {
		return doItemStacksMatch(this.in1, input1, this.oreDictName1)
				&& doItemStacksMatch(this.in2, input2, this.oreDictName2);
	}
}
