package theblockbox.huntersdream.init;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;
import theblockbox.huntersdream.objects.armor.PureSilverArmorBase;
import theblockbox.huntersdream.objects.items.ItemPureSilver;
import theblockbox.huntersdream.objects.tools.axe.ToolPureSilverAxe;
import theblockbox.huntersdream.objects.tools.hoe.ToolPureSilverHoe;
import theblockbox.huntersdream.objects.tools.pickaxe.ToolPureSilverPickaxe;
import theblockbox.huntersdream.objects.tools.shovel.ToolPureSilverShovel;
import theblockbox.huntersdream.objects.tools.sword.ToolPureSilverSword;
import theblockbox.huntersdream.util.Reference;

public class ItemInit {
	public static final List<Item> ITEMS = new ArrayList<>();

	// Items
	public static final Item PURE_SILVER = new ItemPureSilver("pure_silver");

	// Materials
	public static final ToolMaterial TOOL_PURE_SILVER = EnumHelper.addToolMaterial("tool_pure_silver", 3, 60, 6.0F,
			0.0F, 14);
	public static final ArmorMaterial ARMOR_PURE_SILVER = EnumHelper.addArmorMaterial("armor_pure_silver",
			Reference.MODID + ":pure_silver", 6, new int[] { 1, 3, 5, 2 }, 14, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.2F);

	// Tools
	public static final Item HOE_PURE_SILVER = new ToolPureSilverHoe("hoe_pure_silver");
	public static final Item PICKAXE_PURE_SILVER = new ToolPureSilverPickaxe("pickaxe_pure_silver");
	public static final Item SHOVEL_PURE_SILVER = new ToolPureSilverShovel("shovel_pure_silver");
	public static final Item SWORD_PURE_SILVER = new ToolPureSilverSword("sword_pure_silver");
	public static final Item AXE_PURE_SILVER = new ToolPureSilverAxe("axe_pure_silver");

	// Armor
	// These are the weirdest values in the whole world
	public static final Item HELMET_PURE_SILVER = new PureSilverArmorBase("helmet_pure_silver", ARMOR_PURE_SILVER, 1,
			EntityEquipmentSlot.HEAD, 1.2F, 1.03F);
	public static final Item CHESTPLATE_PURE_SILVER = new PureSilverArmorBase("chestplate_pure_silver",
			ARMOR_PURE_SILVER, 1, EntityEquipmentSlot.CHEST, 2.05F, 1.15F);
	public static final Item LEGGINGS_PURE_SILVER = new PureSilverArmorBase("leggings_pure_silver", ARMOR_PURE_SILVER,
			2, EntityEquipmentSlot.LEGS, 1.7F, 1.08F);
	public static final Item BOOTS_PURE_SILVER = new PureSilverArmorBase("boots_pure_silver", ARMOR_PURE_SILVER, 1,
			EntityEquipmentSlot.FEET, 1.2F, 1.03F);

	/*
	 * How to make normal item: - create JSON file in models/item - create item
	 * texture in textures/items
	 * 
	 * How to make armor: - create new JSON files in models/item - create item
	 * texture in textures/items - create texture (for armor when it's worn) in
	 * textures/models/armor
	 */

	static {
		TOOL_PURE_SILVER.setRepairItem(new ItemStack(PURE_SILVER));
		ARMOR_PURE_SILVER.setRepairItem(new ItemStack(PURE_SILVER));
	}
}
