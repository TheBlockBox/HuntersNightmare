package theblockbox.huntersdream.event;

import java.util.stream.Stream;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import theblockbox.huntersdream.init.TransformationInit;
import theblockbox.huntersdream.util.Transformation;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

/**
 * This event is fired shortly before the player's transformation xp is changed
 * (through the method {@link TransformationHelper#setXP(EntityPlayerMP, int)}
 * or methods that call this method). When this event is canceled, the player
 * won't get any xp added, no packet and no level up message will be sent. The
 * amount is the xp the player will have after the post of this event (given
 * that the event is not canceled). Posted on {@link MinecraftForge#EVENT_BUS}
 */
@Cancelable
public class TransformationXPEvent extends PlayerEvent {
	public final ITransformationPlayer TRANSFORMATION_PLAYER;
	/** The player's current xp (before applying it) */
	private int xpBefore;
	private int xpAfter;
	private double level;
	private TransformationXPSentReason reason;

	public TransformationXPEvent(EntityPlayer player, int xpAfter, TransformationXPSentReason reason) {
		super(player);
		ITransformationPlayer cap = TransformationHelper.getCap(player);
		this.TRANSFORMATION_PLAYER = cap;
		this.xpBefore = cap.getXP();
		this.xpAfter = xpAfter;
		this.reason = reason;
		this.level = TransformationHelper.getCap(player).getLevel();
	}

	/** Returns the xp the player should have AFTER this event is fired */
	public int getAmount() {
		return this.xpAfter;
	}

	/** Set the player's xp */
	public void setAmount(int amount) {
		this.xpAfter = amount;
	}

	public TransformationXPSentReason getTransformationXPSentReason() {
		return reason;
	}

	public int getXPBefore() {
		return xpBefore;
	}

	/** The out of the xp before the event calculated level */
	public double getLevel() {
		return level;
	}

	/** Returns true if the player levelled up. Server side only! */
	public boolean leveledUp() {
		return MathHelper.floor(getNewLevel()) > MathHelper.floor(getLevel());
	}

	/** Returns the player's level after the */
	public double getNewLevel() {
		return TransformationHelper.getTransformation(getEntityPlayer()).getLevel((EntityPlayerMP) getEntityPlayer());
	}

	public enum TransformationXPSentReason {
		WEREWOLF_HAS_KILLED(TransformationInit.WEREWOLF), WEREWOLF_UNDER_MOON(TransformationInit.WEREWOLF),
		VAMPIRE_DRANK_BLOOD(TransformationInit.VAMPIRE), COMMAND, RESPAWN;

		/** The transformations that can receive xp through this cause */
		public final Transformation[] TRANSFORMATIONS;

		private TransformationXPSentReason(Transformation... transformations) {
			this.TRANSFORMATIONS = transformations;
		}

		public static boolean validReason(@Nonnull TransformationXPSentReason reason,
				@Nonnull Transformation transformation) {
			return Stream.of(reason.TRANSFORMATIONS).anyMatch(transformation::equals);
		}
	}
}
