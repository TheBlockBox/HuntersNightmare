package theblockbox.huntersdream.blocks.custommodel;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import theblockbox.huntersdream.init.CreativeTabInit;
import theblockbox.huntersdream.util.enums.Rituals;
import theblockbox.huntersdream.util.enums.Transformations;
import theblockbox.huntersdream.util.handlers.PacketHandler.Packets;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

public class BlockWerewolfEnchantingStone extends BlockBaseCustomModelWithDirection {
	private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(sixteenth(1), 0, sixteenth(1), sixteenth(15),
			sixteenth(10), sixteenth(15));
	public static final Material WEREWOLF_ENCHANTING_STONE = new Material(MapColor.STONE) {
		@Override
		public boolean isSolid() {
			return false;
		}

		@Override
		public boolean blocksMovement() {
			return false;
		}

		@Override
		public boolean getCanBurn() {
			return false;
		}

		@Override
		public boolean isReplaceable() {
			return false;
		}

		@Override
		public boolean isOpaque() {
			return false;
		}

		@Override
		public boolean isToolNotRequired() {
			return false;
		};
	};

	public BlockWerewolfEnchantingStone(String name) {
		super(name, WEREWOLF_ENCHANTING_STONE, 3.5F);
		setHarvestLevel("pickaxe", 2);
		setCreativeTab(CreativeTabInit.HUNTERSDREAM_MISC);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return BOUNDING_BOX;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			if (TransformationHelper.getTransformation(playerIn) == Transformations.WEREWOLF) {
				ITransformationPlayer cap = TransformationHelper.getCap(playerIn);
				if (!cap.hasRitual(Rituals.LUPUS_ADVOCABIT)) {
					cap.addRitual(Rituals.LUPUS_ADVOCABIT);
				} else if (!cap.hasRitual(Rituals.WEREWOLF_SECOND_RITE) && cap.getLevelFloor() == 5) {
					cap.addRitual(Rituals.WEREWOLF_SECOND_RITE);
				} else {
					return false;
				}

				Packets.TRANSFORMATION.sync(playerIn);
				playerIn.sendMessage(
						new TextComponentTranslation("huntersdream.werewolf_enchanting_stone.onClick.werewolf"));
			} else {
				playerIn.sendMessage(
						new TextComponentTranslation("huntersdream.werewolf_enchanting_stone.onClick.notWerewolf"));
			}
		}
		return true;
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return worldIn.getBlockState(pos.down()).getMaterial().isSolid();
	}
}
