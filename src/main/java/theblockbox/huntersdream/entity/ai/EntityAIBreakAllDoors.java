package theblockbox.huntersdream.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBreakDoor;
import net.minecraft.entity.ai.EntityAIDoorInteract;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;

/**
 * Class that extends EntityAIBreakDoor so that you can also break iron doors.
 * Made because {@link EntityAIDoorInteract#getBlockDoor(BlockPos)} is private.
 */
public class EntityAIBreakAllDoors extends EntityAIBreakDoor {

	public EntityAIBreakAllDoors(EntityLiving entityIn) {
		super(entityIn);
	}

	@Override
	public boolean shouldExecute() {
		if (!this.superShouldExecute()) {
			return false;
		} else if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.entity.world, this.entity)
				|| !this.entity.world.getBlockState(this.doorPosition).getBlock().canEntityDestroy(
						this.entity.world.getBlockState(this.doorPosition), this.entity.world, this.doorPosition,
						this.entity)
				|| !net.minecraftforge.event.ForgeEventFactory.onEntityDestroyBlock(this.entity, this.doorPosition,
						this.entity.world.getBlockState(this.doorPosition))) {
			return false;
		} else {
			return !BlockDoor.isOpen(this.entity.world, this.doorPosition);
		}
	}

	protected boolean superShouldExecute() {
		if (!this.entity.collidedHorizontally) {
			return false;
		} else {
			PathNavigateGround pathNavigateGround = (PathNavigateGround) this.entity.getNavigator();
			Path path = pathNavigateGround.getPath();

			if (path != null && !path.isFinished() && pathNavigateGround.getEnterDoors()) {
				for (int i = 0; i < Math.min(path.getCurrentPathIndex() + 2, path.getCurrentPathLength()); ++i) {
					PathPoint pathPoint = path.getPathPointFromIndex(i);
					this.doorPosition = new BlockPos(pathPoint.x, pathPoint.y + 1, pathPoint.z);

					if (this.entity.getDistanceSq(this.doorPosition.getX(), this.entity.posY,
							this.doorPosition.getZ()) <= 2.25D) {
						this.doorBlock = this.getBlockDoorAt(this.doorPosition);

						if (this.doorBlock != null) {
							return true;
						}
					}
				}

				this.doorPosition = (new BlockPos(this.entity)).up();
				this.doorBlock = this.getBlockDoorAt(this.doorPosition);
				return this.doorBlock != null;
			} else {
				return false;
			}
		}
	}

	protected BlockDoor getBlockDoorAt(BlockPos pos) {
		Block block = this.entity.world.getBlockState(pos).getBlock();
		return block instanceof BlockDoor ? (BlockDoor) block : null;
	}
}
