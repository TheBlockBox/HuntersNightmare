package huntersdream.init;

import java.util.ArrayList;
import java.util.List;

import huntersdream.objects.armor.ArmorBase;
import huntersdream.objects.items.ItemBase;
import huntersdream.objects.tools.ToolHoe;
import huntersdream.objects.tools.ToolPickaxe;
import huntersdream.objects.tools.ToolShovel;
import huntersdream.objects.tools.ToolSword;
import huntersdream.util.Reference;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.util.EnumHelper;

public class ItemInit {
	public static final List<Item> ITEMS = new ArrayList<>();

	// Items
	public static final Item INGOT_SILVER = new ItemBase("ingot_silver", CreativeTabs.MATERIALS);

	// Materials
	public static final ToolMaterial TOOL_SILVER = EnumHelper.addToolMaterial("tool_silver", 2, 400, 6.0F, 2.5F, 14);
	public static final ArmorMaterial ARMOR_SILVER = EnumHelper.addArmorMaterial("armor_silver",
			Reference.MODID + ":silver", 1000, new int[] { 2, 5, 6, 2 }, 9, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 1);

	// Tools
	// public static final Item AXE_SILVER = new ToolAxe("axe_silver", TOOL_SILVER);
	public static final Item HOE_SILVER = new ToolHoe("hoe_silver", TOOL_SILVER);
	public static final Item PICKAXE_SILVER = new ToolPickaxe("pickaxe_silver", TOOL_SILVER);
	public static final Item SHOVEL_SILVER = new ToolShovel("shovel_silver", TOOL_SILVER);
	public static final Item SWORD_SILVER = new ToolSword("sword_silver", TOOL_SILVER);

	// Armor
	public static final Item HELMET_SILVER = new ArmorBase("helmet_silver", ARMOR_SILVER, 1, EntityEquipmentSlot.HEAD);
	public static final Item CHESTPLATE_SILVER = new ArmorBase("chestplate_silver", ARMOR_SILVER, 1,
			EntityEquipmentSlot.CHEST);
	public static final Item LEGGINGS_SILVER = new ArmorBase("leggings_silver", ARMOR_SILVER, 2,
			EntityEquipmentSlot.LEGS);
	public static final Item BOOTS_SILVER = new ArmorBase("boots_silver", ARMOR_SILVER, 1, EntityEquipmentSlot.FEET);

	/*
	 * How to make normal item: - create JSON file in models/item - create item
	 * texture in textures/items
	 * 
	 * How to make armor: - create new JSON files in models/item - create item
	 * texture in textures/items - create texture (for armor when it's worn) in
	 * textures/models/armor
	 */
}
