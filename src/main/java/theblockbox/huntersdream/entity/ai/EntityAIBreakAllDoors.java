package theblockbox.huntersdream.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBreakDoor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;

/**
 * Class that extends EntityAIBreakDoor and adds the ability to destroy all types of doors. (Including iron ones.)
 * Also works on all difficulties. (The default one only works on hard.)
 */
// TODO: Make this work for iron doors
public class EntityAIBreakAllDoors extends EntityAIBreakDoor {

    public EntityAIBreakAllDoors(EntityLiving entityIn) {
        super(entityIn);
    }

    @Override
    public BlockDoor getBlockDoor(BlockPos pos) {
        Block block = this.entity.world.getBlockState(pos).getBlock();
        System.out.println(block);
        return block instanceof BlockDoor ? (BlockDoor) block : null;
    }

    @Override
    public void updateTask() {
        super.updateTask();
        // if the door should be destroyed but it couldn't be since the difficulty was not hard, destroy it
        if (this.breakingTime == 240 && (this.entity.world.getDifficulty() != EnumDifficulty.HARD)) {
            this.entity.world.setBlockToAir(this.doorPosition);
            this.entity.world.playEvent(1021, this.doorPosition, 0);
            this.entity.world.playEvent(2001, this.doorPosition, Block.getIdFromBlock(this.doorBlock));
        }
    }
}
