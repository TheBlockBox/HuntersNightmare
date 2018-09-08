package theblockbox.huntersdream.event;

import net.minecraft.entity.EntityCreature;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Posted when an entity transforms (back), shortly before the extra data is
 * saved/applied. To see if the data is being saved or applied, look if
 * {@link #isBeingSaved()} is true. This event is useful for adding some extra
 * data that's stored in capabilities and not saved via nbt. The event is not
 * cancelable. Posted on {@link MinecraftForge#EVENT_BUS}
 */
public class ExtraDataEvent extends Event {
	/**
	 * The creature to which the data is being saved/from which the data has been
	 * retrieved (always an untransformed entity, so it can't be a werewolf or a
	 * vampire, but a villager or a goblin)
	 */
	private final EntityCreature creature;
	/**
	 * The extra data that is going to be saved/applied. If {@link #onDataSave} is
	 * true, the data should be the exact same data gotten through the method
	 * {@link EntityCreature#writeEntityToNBT(NBTTagCompound)}
	 */
	private NBTTagCompound extraData;
	/**
	 * True if the data is currently being saved to the transformed entity, false if
	 * the data is being applied to the untransformed entity
	 */
	private boolean onDataSave;

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

	/** @see extraData */
	public NBTTagCompound getExtraData() {
		return this.extraData;
	}

	/** @see extraData */
	public void setExtraData(NBTTagCompound extraData) {
		this.extraData = extraData;
	}

	/**
	 * @return Returns true if the data is currently being saved to the transforming
	 *         entity, returns false if the data is being applied to the
	 *         untransformed entity
	 */
	public boolean isBeingSaved() {
		return this.onDataSave;
	}
}
