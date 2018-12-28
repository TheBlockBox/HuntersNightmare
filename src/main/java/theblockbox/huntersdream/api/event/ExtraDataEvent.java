package theblockbox.huntersdream.api.event;

import net.minecraft.entity.EntityCreature;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import theblockbox.huntersdream.entity.EntityWerewolf;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;

/**
 * ExtraDataEvent is fired when an entity that is not a player transforms (back)
 * and tries to save/apply the extra data that is stored in the transformed
 * werewolf. (Extra data is used to store everything entity related so that the
 * entity behaves exactly the same after the transformation as before.) This
 * event should mostly be used for adding some extra data that's stored in
 * capabilities and not saved via nbt.<br>
 * This event is fired in {@link EntityWerewolf#onLivingUpdate()} and
 * {@link WerewolfHelper#toWerewolfWhenNight(EntityCreature)} <br>
 * This event is not {@link Cancelable}.<br>
 * <br>
 * This event does not have a result.
 * {@link net.minecraftforge.fml.common.eventhandler.Event.HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class ExtraDataEvent extends Event {
	private final EntityCreature creature;
	private NBTTagCompound extraData;
	private final boolean onDataSave;

	public ExtraDataEvent(EntityCreature creature, NBTTagCompound extraData, boolean onDataSave) {
		this.creature = creature;
		this.extraData = extraData;
		this.onDataSave = onDataSave;
	}

	/**
	 * The creature to which the data is being saved/from which the data has been
	 * retrieved (always an untransformed entity, so it can't be a werewolf or a
	 * vampire, but a villager or a goblin)
	 */
	public EntityCreature getEntityCreature() {
		return this.creature;
	}

	/**
	 * Gets the extra data that is going to be saved/applied. If
	 * {@link #isBeingSaved()} returns true, the data should be the exact same data
	 * gotten through the method
	 * {@link EntityCreature#writeEntityToNBT(NBTTagCompound)} except for the health
	 * (the untransformed entity has the same percentage of health as the
	 * transformed one to make things a bit more realistic) and other parameters
	 * that have been changed by an event handler that received the event before the
	 * current event handler
	 */
	public NBTTagCompound getExtraData() {
		return this.extraData;
	}

	/**
	 * Sets the extra data that is going to be saved/applied. If
	 * {@link #isBeingSaved()} returns true, the data should be the exact same data
	 * gotten through the method
	 * {@link EntityCreature#writeEntityToNBT(NBTTagCompound)} except for the health
	 * (the untransformed entity has the same percentage of health as the
	 * transformed one to make things a bit more realistic) and other parameters
	 * that have been changed by an event handler that received the event before the
	 * current event handler
	 */
	public void setExtraData(NBTTagCompound extraData) {
		this.extraData = extraData;
	}

	/**
	 * Returns true if the data is currently being saved to the transforming entity,
	 * returns false if the data is being applied to the untransformed entity
	 */
	public boolean isBeingSaved() {
		return this.onDataSave;
	}
}
