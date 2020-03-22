package theblockbox.huntersdream.api.init;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.api.interfaces.IAmmunition;
import theblockbox.huntersdream.entity.EntitySilverArrow;
import theblockbox.huntersdream.items.*;
import theblockbox.huntersdream.items.gun.*;
import theblockbox.huntersdream.items.tools.ToolAxe;
import theblockbox.huntersdream.items.tools.ToolPickaxe;
import theblockbox.huntersdream.util.Reference;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ItemInit {
    public static final List<Item> ITEMS = new ArrayList<>();
    // this items is used as a placeholder for uninitialized items
    // it's annotated with non null to make ides not complain about final fields that equal null
    @Nonnull
    private static final Item NULL_ITEM = null;

    @GameRegistry.ObjectHolder("huntersdream:silver_ingot")
    public static final Item SILVER_INGOT = ItemInit.NULL_ITEM;

    @GameRegistry.ObjectHolder("huntersdream:lycanthropy_book")
    public static final Item LYCANTHROPY_BOOK = ItemInit.NULL_ITEM;

    @GameRegistry.ObjectHolder("huntersdream:silver_sword")
    public static final Item SILVER_SWORD = ItemInit.NULL_ITEM;

    @GameRegistry.ObjectHolder("huntersdream:tent")
    public static final Item TENT = ItemInit.NULL_ITEM;

    @GameRegistry.ObjectHolder("huntersdream:fabric")
    public static final Item FABRIC = ItemInit.NULL_ITEM;

    @GameRegistry.ObjectHolder("huntersdream:healing_herb")
    public static final Item HEALING_HERB = ItemInit.NULL_ITEM;

    @GameRegistry.ObjectHolder("huntersdream:flintlock_musket")
    public static final Item FLINTLOCK_MUSKET = ItemInit.NULL_ITEM;

    @GameRegistry.ObjectHolder("huntersdream:flintlock_pistol")
    public static final Item FLINTLOCK_PISTOL = ItemInit.NULL_ITEM;

    @GameRegistry.ObjectHolder("huntersdream:flintlock_blunderbuss")
    public static final Item FLINTLOCK_BLUNDERBUSS = ItemInit.NULL_ITEM;

    @GameRegistry.ObjectHolder("huntersdream:musket_ball")
    public static final Item MUSKET_BALL = ItemInit.NULL_ITEM;

    @GameRegistry.ObjectHolder("huntersdream:silver_musket_ball")
    public static final Item SILVER_MUSKET_BALL = ItemInit.NULL_ITEM;

    @GameRegistry.ObjectHolder("huntersdream:revolver_bullet")
    public static final Item REVOLVER_BULLET = ItemInit.NULL_ITEM;

    @GameRegistry.ObjectHolder("huntersdream:rifle_bullet")
    public static final Item RIFLE_BULLET = ItemInit.NULL_ITEM;

    @GameRegistry.ObjectHolder("huntersdream:shotgun_shell")
    public static final Item SHOTGUN_SHELL = ItemInit.NULL_ITEM;

    @GameRegistry.ObjectHolder("huntersdream:hunter_hat")
    public static final Item HUNTER_HAT = ItemInit.NULL_ITEM;

    @GameRegistry.ObjectHolder("huntersdream:hunter_coat")
    public static final Item HUNTER_COAT = ItemInit.NULL_ITEM;

    @GameRegistry.ObjectHolder("huntersdream:hunter_pants")
    public static final Item HUNTER_PANTS = ItemInit.NULL_ITEM;

    @GameRegistry.ObjectHolder("huntersdream:hunter_boots")
    public static final Item HUNTER_BOOTS = ItemInit.NULL_ITEM;

    @GameRegistry.ObjectHolder("huntersdream:silver_arrow")
    public static final Item SILVER_ARROW = ItemInit.NULL_ITEM;

    // Materials
    public static final Item.ToolMaterial TOOL_SILVER = EnumHelper.addToolMaterial(Reference.MODID + ":tool_silver", 3, 60,
            6.0F, 0.0F, 14);
    public static final ItemArmor.ArmorMaterial ARMOR_SILVER = EnumHelper.addArmorMaterial(Reference.MODID + ":armor_silver",
            Reference.MODID + ":silver", 6, new int[]{1, 3, 5, 2}, 14, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.2F);
    public static final ItemArmor.ArmorMaterial ARMOR_HUNTER = EnumHelper.addArmorMaterial(Reference.MODID + ":armor_hunter",
            Reference.MODID + ":hunter", 15, new int[]{1, 4, 5, 2}, 12, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0F);

    public static void onItemRegister(RegistryEvent.Register<Item> event) {
        ItemInit.ITEMS.forEach(item -> ItemInit.registerItemBlock(item, event));
        Item silverIngot = ItemInit.registerItem(new Item(), "silver_ingot", CreativeTabInit.HUNTERSDREAM_MISC, event);
        ItemInit.TOOL_SILVER.setRepairItem(new ItemStack(silverIngot));
        ItemInit.ARMOR_SILVER.setRepairItem(new ItemStack(silverIngot));
        ItemInit.ARMOR_HUNTER.setRepairItem(new ItemStack(Items.LEATHER));
        ItemInit.registerItem(new ItemBestiary(), "bestiary", CreativeTabInit.HUNTERSDREAM_MISC, event);
        ItemInit.registerItem(new ItemLycanthropyBook(), "lycanthropy_book", CreativeTabInit.HUNTERSDREAM_MISC, event);
        ItemInit.registerItem(new ItemTent(), "tent", CreativeTabInit.HUNTERSDREAM_MISC, event);
        ItemInit.registerItem(new Item(), "fabric", CreativeTabInit.HUNTERSDREAM_MISC, event);
        ItemInit.registerItem(new ItemHealingHerb(), "healing_herb", CreativeTabInit.HUNTERSDREAM_MISC, event);
        ItemInit.registerToolSet("silver", ItemInit.TOOL_SILVER, event);
        ItemInit.registerArmorSet("silver", ItemInit.ARMOR_SILVER, event);
        ItemInit.registerItem(new ItemHunterArmor(1, EntityEquipmentSlot.HEAD), "hunter_hat",
                CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS, event);
        ItemInit.registerItem(new ItemHunterArmor(1, EntityEquipmentSlot.CHEST), "hunter_coat",
                CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS, event);
        ItemInit.registerItem(new ItemHunterArmor(2, EntityEquipmentSlot.LEGS), "hunter_pants",
                CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS, event);
        ItemInit.registerItem(new ItemHunterArmor(1, EntityEquipmentSlot.FEET), "hunter_boots",
                CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS, event);
        ItemInit.registerItem(new ItemHerbalWolfsbaneWater(), "herbal_wolfsbane_water", event);
        ItemInit.registerItem(new ItemFlintlockGun(10, 3451, 1, 2),
                "flintlock_musket", CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS, event);
        ItemInit.registerItem(new ItemFlintlockGun(6, 3255, 1, 1),
                "flintlock_pistol", CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS, event);
        ItemInit.registerItem(new ItemFlintlockGunBlunderBuss(6, 3465, 5, 6),
                "flintlock_blunderbuss", CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS, event);
        ItemInit.registerItem(new ItemPercussionGun(12, 4781, 10,
                        () -> ItemInit.REVOLVER_BULLET, 6, 1.0F, IAmmunition.AmmunitionType.REVOLVER_BULLET),
                "revolver", CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS, event);
        ItemInit.registerItem(new ItemRifle(25, 5721, 40, () -> ItemInit.RIFLE_BULLET, 5),
                "hunting_rifle", CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS, event);
        ItemInit.registerItem(new ItemShotgun(16, 5751, 20, () -> ItemInit.SHOTGUN_SHELL, 5),
                "pump_shotgun", CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS, event);
        ItemInit.registerItem(new ItemAmmunition(IAmmunition.AmmunitionType.MUSKET_BALL), "musket_ball",
                CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS, event);
        ItemInit.registerItem(new ItemAmmunition(IAmmunition.AmmunitionType.MUSKET_BALL, IAmmunition.AmmunitionType.SILVER),
                "silver_musket_ball", CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS, event);
        ItemInit.registerItem(new ItemAmmunition(IAmmunition.AmmunitionType.REVOLVER_BULLET), "revolver_bullet",
                CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS, event);
        ItemInit.registerItem(new ItemAmmunition(IAmmunition.AmmunitionType.REVOLVER_BULLET, IAmmunition.AmmunitionType.SILVER),
                "silver_revolver_bullet", CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS, event);
        ItemInit.registerItem(new ItemAmmunition(IAmmunition.AmmunitionType.RIFLE_BULLET), "rifle_bullet",
                CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS, event);
        ItemInit.registerItem(new ItemAmmunition(IAmmunition.AmmunitionType.RIFLE_BULLET, IAmmunition.AmmunitionType.SILVER),
                "silver_rifle_bullet", CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS, event);
        ItemInit.registerItem(new ItemAmmunition(IAmmunition.AmmunitionType.SHOTGUN_SHELL), "shotgun_shell",
                CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS, event);
        ItemInit.registerItem(new ItemAmmunition(IAmmunition.AmmunitionType.SHOTGUN_SHELL, IAmmunition.AmmunitionType.SILVER),
                "silver_shotgun_shell", CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS, event);
        ItemInit.registerItem(new ItemArrow() {
            @Override
            public EntityArrow createArrow(World worldIn, ItemStack stack, EntityLivingBase shooter) {
                return new EntitySilverArrow(worldIn, shooter);
            }
        }, "silver_arrow", CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS, event);
        GameRegistry.addSmelting(BlockInit.SILVER_ORE, new ItemStack(silverIngot), 0.9F);
    }

    private static Item registerItem(Item item, String name, RegistryEvent.Register<Item> event) {
        return ItemInit.registerItem(item, name, (item.getCreativeTab() == null) ? CreativeTabInit.HUNTERSDREAM_MISC
                : item.getCreativeTab(), event);
    }

    private static Item registerItem(Item item, String name, CreativeTabs tab, RegistryEvent.Register<Item> event) {
        event.getRegistry().register(item.setRegistryName(GeneralHelper.newResLoc(name))
                .setTranslationKey(Reference.MODID + "." + name).setCreativeTab(tab));
        ItemInit.ITEMS.add(item);
        return item;
    }

    private static Item registerItemBlock(Item itemBlock, RegistryEvent.Register<Item> event) {
        event.getRegistry()
                .register(itemBlock.setTranslationKey(itemBlock.getRegistryName().toString().replace(':', '.')));
        return itemBlock;
    }

    private static void registerToolSet(String name, Item.ToolMaterial toolMaterial, RegistryEvent.Register<Item> event) {
        ItemInit.registerItem(new ItemHoe(toolMaterial), name + "_hoe", CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS, event);
        ItemInit.registerItem(new ToolPickaxe(toolMaterial), name + "_pickaxe", CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS,
                event);
        ItemInit.registerItem(new ItemSpade(toolMaterial), name + "_shovel", CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS,
                event);
        ItemInit.registerItem(new ItemSword(toolMaterial), name + "_sword", CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS,
                event);
        ItemInit.registerItem(new ToolAxe(toolMaterial), name + "_axe", CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS, event);
    }

    private static void registerArmorSet(String name, ItemArmor.ArmorMaterial material, RegistryEvent.Register<Item> event) {
        ItemInit.registerItem(new ItemArmor(material, 1, EntityEquipmentSlot.HEAD), name + "_helmet",
                CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS, event);
        ItemInit.registerItem(new ItemArmor(material, 1, EntityEquipmentSlot.CHEST), name + "_chestplate",
                CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS, event);
        ItemInit.registerItem(new ItemArmor(material, 2, EntityEquipmentSlot.LEGS), name + "_leggings",
                CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS, event);
        ItemInit.registerItem(new ItemArmor(material, 1, EntityEquipmentSlot.FEET), name + "_boots",
                CreativeTabInit.HUNTERSDREAM_TOOLS_AND_WEAPONS, event);
    }
}
