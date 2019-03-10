package theblockbox.huntersdream.api.init;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.IItemHandler;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.api.helpers.TransformationHelper;
import theblockbox.huntersdream.api.helpers.WerewolfHelper;
import theblockbox.huntersdream.capabilities.CapabilityProvider;
import theblockbox.huntersdream.capabilities.TransformationCreatureProvider;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.handlers.PacketHandler;
import theblockbox.huntersdream.util.handlers.VampireEventHandler;
import theblockbox.huntersdream.util.interfaces.IInfectInTicks;
import theblockbox.huntersdream.util.interfaces.IInfectOnNextMoon;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationCreature;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;
import theblockbox.huntersdream.util.interfaces.transformation.IVampirePlayer;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class CapabilitiesInit {
	@CapabilityInject(ITransformationPlayer.class)
	public static final Capability<ITransformationPlayer> CAPABILITY_TRANSFORMATION_PLAYER = null;
	@CapabilityInject(ITransformationCreature.class)
	public static final Capability<ITransformationCreature> CAPABILITY_TRANSFORMATION_CREATURE = null;
	@CapabilityInject(IInfectInTicks.class)
	public static final Capability<IInfectInTicks> CAPABILITY_INFECT_IN_TICKS = null;
	@CapabilityInject(IInfectOnNextMoon.class)
	public static final Capability<IInfectOnNextMoon> CAPABILITY_INFECT_ON_NEXT_MOON = null;
	@CapabilityInject(IVampirePlayer.class)
	public static final Capability<IVampirePlayer> CAPABILITY_VAMPIRE = null;
	@CapabilityInject(IItemHandler.class)
	public static final Capability<IItemHandler> CAPABILITY_ITEM_HANDLER = null;

	public static void registerCapabilities() {
		CapabilityManager.INSTANCE.register(ITransformationPlayer.class, new ITransformationPlayer.TransformationPlayerStorage(),
				ITransformationPlayer.TransformationPlayer::new);
		CapabilityManager.INSTANCE.register(ITransformationCreature.class, new ITransformationCreature.TransformationCreatureStorage(),
				ITransformationCreature.TransformationCreature::new);
		CapabilityManager.INSTANCE.register(IInfectInTicks.class, new IInfectInTicks.InfectInTicksStorage(), IInfectInTicks.InfectInTicks::new);
		CapabilityManager.INSTANCE.register(IInfectOnNextMoon.class, new IInfectOnNextMoon.InfectOnNextMoonStorage(),
				IInfectOnNextMoon.InfectOnNextMoon::new);
		CapabilityManager.INSTANCE.register(IVampirePlayer.class, new IVampirePlayer.VampireStorage(), IVampirePlayer.Vampire::new);
	}

	public static final ResourceLocation TRANSFORMATION_PLAYER_CAPABILITIY = GeneralHelper
			.newResLoc("transformationplayer");
	public static final ResourceLocation TRANSFORMATION_CREATURE_CAPABILITY = GeneralHelper
			.newResLoc("transformationcreature");
	public static final ResourceLocation INFECT_IN_TICKS_CAPABILITY = GeneralHelper.newResLoc("infectinticks");
	public static final ResourceLocation INFECT_ON_NEXT_MOON = GeneralHelper.newResLoc("infectonnextmoon");
	public static final ResourceLocation VAMPIRE = GeneralHelper.newResLoc("vampire");
	public static final ResourceLocation ITEM_HANDLER = GeneralHelper.newResLoc("itemhandler");

	@SubscribeEvent
	public static void onCapabilityAttach(AttachCapabilitiesEvent<Entity> event) {
		Entity entity = event.getObject();
		if (entity instanceof EntityPlayer) {
			event.addCapability(CapabilitiesInit.TRANSFORMATION_PLAYER_CAPABILITIY,
					new CapabilityProvider<>(CapabilitiesInit.CAPABILITY_TRANSFORMATION_PLAYER));
			event.addCapability(CapabilitiesInit.VAMPIRE, new CapabilityProvider<>(CapabilitiesInit.CAPABILITY_VAMPIRE));
		} else if (entity instanceof EntityVillager) {
			event.addCapability(CapabilitiesInit.TRANSFORMATION_CREATURE_CAPABILITY,
					new TransformationCreatureProvider(Transformation.WEREWOLF, Transformation.VAMPIRE));
		}

		if (entity instanceof EntityVillager || entity instanceof EntityPlayer
				|| entity instanceof ITransformationCreature) {
			event.addCapability(CapabilitiesInit.INFECT_IN_TICKS_CAPABILITY,
					new CapabilityProvider<>(CapabilitiesInit.CAPABILITY_INFECT_IN_TICKS));
			event.addCapability(CapabilitiesInit.INFECT_ON_NEXT_MOON,
					new CapabilityProvider<>(CapabilitiesInit.CAPABILITY_INFECT_ON_NEXT_MOON));
		}
	}

	@SubscribeEvent
	public static void onPlayerClone(PlayerEvent.Clone event) {
		if (event.isWasDeath()) {
			EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
			EntityPlayer originalPlayer = event.getOriginal();

			IInfectInTicks iit = TransformationHelper.getIInfectInTicks(player).get();
			IInfectInTicks iitOld = TransformationHelper.getIInfectInTicks(originalPlayer).get();
			iit.setCurrentlyInfected(iitOld.currentlyInfected());
			iit.setInfectionTransformation(iitOld.getInfectionTransformation());
			iit.setTime(iitOld.getTime());

			IInfectOnNextMoon ionm = WerewolfHelper.getIInfectOnNextMoon(player).get();
			IInfectOnNextMoon ionmOld = WerewolfHelper.getIInfectOnNextMoon(originalPlayer).get();
			ionm.setInfectionStatus(ionmOld.getInfectionStatus());
			ionm.setInfectionTick(ionmOld.getInfectionTick());
			ionm.setInfectionTransformation(ionmOld.getInfectionTransformation());

			ITransformationPlayer transformationPlayer = TransformationHelper.getITransformationPlayer(player);
			ITransformationPlayer oldTransformationPlayer = TransformationHelper
					.getITransformationPlayer(originalPlayer);

			transformationPlayer.setTransformation(oldTransformationPlayer.getTransformation());
			transformationPlayer.setTextureIndex(oldTransformationPlayer.getTextureIndex());
			transformationPlayer.setSkills(oldTransformationPlayer.getSkills());
			transformationPlayer.setTransformationData(oldTransformationPlayer.getTransformationData());
			transformationPlayer.setActiveSkill(oldTransformationPlayer.getActiveSkill().orElse(null));

			if (transformationPlayer.getTransformation() == Transformation.WEREWOLF) {
				WerewolfHelper.setTransformed(player, false);
			}

			if (transformationPlayer.getTransformation() == Transformation.VAMPIRE) {
				VampireEventHandler.onVampireRespawn(player);
			}

			MinecraftServer server = player.getServer();
			GeneralHelper.executeOnMainThreadIn(() -> CapabilitiesInit.sendPackets(server, player), 100, server, Reference.MODID + ":syncCapAfterPlayerDeath1");
			GeneralHelper.executeOnMainThreadIn(() -> CapabilitiesInit.sendPackets(server, player), 40000, server, Reference.MODID + ":syncCapAfterPlayerDeath2");
		}
	}

	private static void sendPackets(MinecraftServer server, EntityPlayerMP player) {
		for (EntityPlayerMP p : server.getPlayerList().getPlayers()) {
			PacketHandler.sendTransformationMessageToPlayer(p, player);
		}
		PacketHandler.sendBloodMessage(player);
		PacketHandler.sendTransformationMessage(player);
	}
}
