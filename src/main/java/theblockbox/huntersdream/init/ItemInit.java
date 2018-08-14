package theblockbox.huntersdream.init;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.util.EnumHelper;
import theblockbox.huntersdream.objects.armor.SilverArmorBase;
import theblockbox.huntersdream.objects.items.ItemPureSilver;
import theblockbox.huntersdream.objects.tools.axe.ToolSilverAxe;
import theblockbox.huntersdream.objects.tools.hoe.ToolSilverHoe;
import theblockbox.huntersdream.objects.tools.pickaxe.ToolSilverPickaxe;
import theblockbox.huntersdream.objects.tools.shovel.ToolSilverShovel;
import theblockbox.huntersdream.objects.tools.sword.ToolSilverSword;
import theblockbox.huntersdream.util.Reference;

public class ItemInit {
	public static final List<Item> ITEMS = new ArrayList<>();

	// Items
	public static final Item INGOT_SILVER = new ItemPureSilver("ingot_silver");

	// Materials
	public static final ToolMaterial TOOL_SILVER = EnumHelper.addToolMaterial(Reference.MODID + ":tool_silver", 3, 60,
			6.0F, 0.0F, 14);
	public static final ArmorMaterial ARMOR_SILVER = EnumHelper.addArmorMaterial(Reference.MODID + ":armor_silver",
			Reference.MODID + ":silver", 6, new int[] { 1, 3, 5, 2 }, 14, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.2F);

	// Tools
	public static final Item HOE_SILVER = new ToolSilverHoe("hoe_silver");
	public static final Item PICKAXE_SILVER = new ToolSilverPickaxe("pickaxe_silver");
	public static final Item SHOVEL_SILVER = new ToolSilverShovel("shovel_silver");
	public static final Item SWORD_SILVER = new ToolSilverSword("sword_silver");
	public static final Item AXE_SILVER = new ToolSilverAxe("axe_silver");

	// Armor
	// These are the weirdest values in the whole world
	public static final Item HELMET_SILVER = new SilverArmorBase("helmet_silver", ARMOR_SILVER, 1,
			EntityEquipmentSlot.HEAD, 1.35F, 1.2F);
	public static final Item CHESTPLATE_SILVER = new SilverArmorBase("chestplate_silver", ARMOR_SILVER, 1,
			EntityEquipmentSlot.CHEST, 1.85F, 1.6F);
	public static final Item LEGGINGS_SILVER = new SilverArmorBase("leggings_silver", ARMOR_SILVER, 2,
			EntityEquipmentSlot.LEGS, 1.65F, 1.3F);
	public static final Item BOOTS_SILVER = new SilverArmorBase("boots_silver", ARMOR_SILVER, 1,
			EntityEquipmentSlot.FEET, 1.25F, 1.1F);

	/*
	 * How to make normal item: - create JSON file in models/item - create item
	 * texture in textures/items
	 * 
	 * How to make armor: - create new JSON files in models/item - create item
	 * texture in textures/items - create texture (for armor when it's worn) in
	 * textures/models/armor
	 */
}
