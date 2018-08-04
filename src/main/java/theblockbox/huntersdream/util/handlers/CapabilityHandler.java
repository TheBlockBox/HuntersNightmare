package theblockbox.huntersdream.util.handlers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import theblockbox.huntersdream.capabilities.CapabilityProvider;
import theblockbox.huntersdream.init.CapabilitiesInit;
import theblockbox.huntersdream.util.ExecutionPath;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.enums.Transformations;
import theblockbox.huntersdream.util.handlers.PacketHandler.Packets;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.interfaces.IInfectInTicks;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationCreature;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

@Mod.EventBusSubscriber
public class CapabilityHandler {
	public final static ResourceLocation TRANSFORMATION_PLAYER_CAPABILITIY = new ResourceLocation(Reference.MODID,
			"transformationplayer");
	public static final ResourceLocation TRANSFORMATION_CREATURE_CAPABILITY = new ResourceLocation(Reference.MODID,
			"transformationcreature");
	public static final ResourceLocation INFECT_IN_TICKS_CAPABILITY = new ResourceLocation(Reference.MODID,
			"infectinticks");

	@SubscribeEvent
	public static void onCapabilityAttach(AttachCapabilitiesEvent<Entity> event) {
		Entity entity = event.getObject();

		if (entity instanceof EntityPlayer) {
			event.addCapability(TRANSFORMATION_PLAYER_CAPABILITIY,
					new CapabilityProvider<ITransformationPlayer>(CapabilitiesInit.CAPABILITY_TRANSFORMATION_PLAYER));
		} else if (entity instanceof EntityVillager) {
			event.addCapability(TRANSFORMATION_CREATURE_CAPABILITY, new CapabilityProvider<ITransformationCreature>(
					CapabilitiesInit.CAPABILITY_TRANSFORMATION_CREATURE));
		}

		if (entity.hasCapability(CapabilitiesInit.CAPABILITY_TRANSFORMATION_CREATURE, null)
				|| entity instanceof EntityPlayer || entity instanceof ITransformationCreature) {
			event.addCapability(INFECT_IN_TICKS_CAPABILITY,
					new CapabilityProvider<IInfectInTicks>(CapabilitiesInit.CAPABILITY_INFECT_IN_TICKS));
		}
	}

	@SubscribeEvent
	public static void onPlayerClone(PlayerEvent.Clone event) {
		ITransformationPlayer transformationPlayer = TransformationHelper.getCap(event.getEntityPlayer());
		ITransformationPlayer oldTransformationPlayer = TransformationHelper.getCap(event.getOriginal());

		transformationPlayer.setXP(oldTransformationPlayer.getXP());
		transformationPlayer.setTransformed(false);
		transformationPlayer.setTransformationID(oldTransformationPlayer.getTransformationID());
		transformationPlayer.setTextureIndex(oldTransformationPlayer.getTextureIndex());

		IInfectInTicks iit = TransformationHelper.getIInfectInTicks(event.getEntityPlayer());
		iit.setCurrentlyInfected(false);
		iit.setInfectionTransformation(Transformations.HUMAN);
		iit.setTime(-1);

		Packets.TRANSFORMATION.sync(new ExecutionPath(), event.getEntityPlayer());
	}
}