package theblockbox.huntersdream.util;

import net.minecraft.entity.player.EntityPlayer;

public class TimedRunnable {
	public final Runnable RUNNABLE;
	public final int ON_TICK;
	public final EntityPlayer PLAYER;

	public TimedRunnable(Runnable runnable, int onTick, EntityPlayer player) {
		this.RUNNABLE = runnable;
		this.ON_TICK = onTick;
		this.PLAYER = player;
	}
}
