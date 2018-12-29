package theblockbox.huntersdream.init;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import theblockbox.huntersdream.items.ItemBestiary;
import theblockbox.huntersdream.items.ItemHuntersJournal;
import theblockbox.huntersdream.items.ItemHuntersJournalPage;
import theblockbox.huntersdream.items.ItemTent;
import theblockbox.huntersdream.items.ItemWolfsbane;
import theblockbox.huntersdream.items.tools.ToolAxe;
import theblockbox.huntersdream.items.tools.ToolPickaxe;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.helpers.GeneralHelper;

public class ItemInit {
	public static final List<Item> ITEMS = new ArrayList<>();

	@ObjectHolder("huntersdream:ingot_silver")
	public static final Item INGOT_SILVER = null;

	@ObjectHolder("huntersdream:sword_silver")
	public static final Item SWORD_SILVER = null;

	@ObjectHolder("huntersdream:wolfsbane")
	public static final Item WOLFSBANE_FLOWER = null;

	@ObjectHolder("huntersdream:hunters_journal")
	public static final Item HUNTERS_JOURNAL = null;
	
	@ObjectHolder("huntersdream:tent")
	public static final Item TENT = null;

	// Materials
	public static final ToolMaterial TOOL_SILVER = EnumHelper.addToolMaterial(Reference.MODID + ":tool_silver", 3, 60,
			6.0F, 0.0F, 14);
	public static final ArmorMaterial ARMOR_SILVER = EnumHelper.addArmorMaterial(Reference.MODID + ":armor_silver",
			Reference.MODID + ":silver", 6, new int[] { 1, 3, 5, 2 }, 14, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.2F);

	public static void onItemRegister(RegistryEvent.Register<Item> event) {
		ITEMS.forEach(item -> registerItemBlock(item, event));
		// TODO: Is there a better way to do this?
		Item silverIngot = registerItem(new Item(), "ingot_silver", CreativeTabInit.HUNTERSDREAM_MISC, event);
		TOOL_SILVER.setRepairItem(new ItemStack(silverIngot));
		ARMOR_SILVER.setRepairItem(new ItemStack(silverIngot));
		registerItem(new ItemWolfsbane(), "wolfsbane", CreativeTabInit.HUNTERSDREAM_MISC, event);
		registerItem(new ItemHuntersJournal(), "hunters_journal", event);
		registerItem(new ItemHuntersJournalPage(), "hunters_journal_page", CreativeTabInit.HUNTERSDREAM_MISC, event);
		registerItem(new ItemBestiary(), "bestiary", CreativeTabInit.HUNTERSDREAM_MISC, event);
		registerItem(new ItemTent(), "tent", CreativeTabInit.HUNTERSDREAM_MISC, event);
		registerToolSet("silver", TOOL_SILVER, event);
		registerArmorSet("silver", ARMOR_SILVER, event);
	}

	private static Item registerItem(Item item, String name, RegistryEvent.Register<Item> event) {
		return registerItem(item, name,
				item.getCreativeTab() == null ? CreativeTabInit.HUNTERSDREAM_MISC : item.getCreativeTab(), event);
	}

	private static Item registerItem(Item item, String name, CreativeTabs tab, RegistryEvent.Register<Item> event) {
		event.getRegistry().register(item.setRegistryName(GeneralHelper.newResLoc(name))
				.setTranslationKey(Reference.MODID + "." + name).setCreativeTab(tab));
		ITEMS.add(item);
		return item;
	}

	private static Item registerItemBlock(Item itemBlock, RegistryEvent.Register<Item> event) {
		event.getRegistry()
				.register(itemBlock.setTranslationKey(itemBlock.getRegistryName().toString().replace(':', '.')));
		return itemBlock;
	}

	private static void registerToolSet(String name, ToolMaterial toolMaterial, RegistryEvent.Register<Item> event) {
		registerItem(new ItemHoe(toolMaterial), "hoe_" + name, CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS, event);
		registerItem(new ToolPickaxe(toolMaterial), "pickaxe_" + name, CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS,
				event);
		registerItem(new ItemSpade(toolMaterial), "shovel_" + name, CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS,
				event);
		registerItem(new ItemSword(toolMaterial), "sword_" + name, CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS,
				event);
		registerItem(new ToolAxe(toolMaterial), "axe_" + name, CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS, event);
	}

	private static void registerArmorSet(String name, ArmorMaterial material, RegistryEvent.Register<Item> event) {
		registerItem(new ItemArmor(material, 1, EntityEquipmentSlot.HEAD), "helmet_" + name,
				CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS, event);
		registerItem(new ItemArmor(material, 1, EntityEquipmentSlot.CHEST), "chestplate_" + name,
				CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS, event);
		registerItem(new ItemArmor(material, 2, EntityEquipmentSlot.LEGS), "leggings_" + name,
				CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS, event);
		registerItem(new ItemArmor(material, 1, EntityEquipmentSlot.FEET), "boots_" + name,
				CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS, event);
	}
}
