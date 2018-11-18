package theblockbox.huntersdream.init;

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
import theblockbox.huntersdream.capabilities.CapabilityProvider;
import theblockbox.huntersdream.capabilities.TransformationCreatureProvider;
import theblockbox.huntersdream.entity.EntityGoblinTD;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.Transformation;
import theblockbox.huntersdream.util.handlers.PacketHandler;
import theblockbox.huntersdream.util.handlers.VampireEventHandler;
import theblockbox.huntersdream.util.helpers.GeneralHelper;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;
import theblockbox.huntersdream.util.interfaces.IInfectInTicks;
import theblockbox.huntersdream.util.interfaces.IInfectInTicks.InfectInTicks;
import theblockbox.huntersdream.util.interfaces.IInfectInTicks.InfectInTicksStorage;
import theblockbox.huntersdream.util.interfaces.IInfectOnNextMoon;
import theblockbox.huntersdream.util.interfaces.IInfectOnNextMoon.InfectOnNextMoon;
import theblockbox.huntersdream.util.interfaces.IInfectOnNextMoon.InfectOnNextMoonStorage;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationCreature;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationCreature.TransformationCreature;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationCreature.TransformationCreatureStorage;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer.TransformationPlayer;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer.TransformationPlayerStorage;
import theblockbox.huntersdream.util.interfaces.transformation.IVampirePlayer;
import theblockbox.huntersdream.util.interfaces.transformation.IVampirePlayer.Vampire;
import theblockbox.huntersdream.util.interfaces.transformation.IVampirePlayer.VampireStorage;

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
		CapabilityManager.INSTANCE.register(ITransformationPlayer.class, new TransformationPlayerStorage(),
				TransformationPlayer::new);
		CapabilityManager.INSTANCE.register(ITransformationCreature.class, new TransformationCreatureStorage(),
				TransformationCreature::new);
		CapabilityManager.INSTANCE.register(IInfectInTicks.class, new InfectInTicksStorage(), InfectInTicks::new);
		CapabilityManager.INSTANCE.register(IInfectOnNextMoon.class, new InfectOnNextMoonStorage(),
				InfectOnNextMoon::new);
		CapabilityManager.INSTANCE.register(IVampirePlayer.class, new VampireStorage(), Vampire::new);
	}

	public final static ResourceLocation TRANSFORMATION_PLAYER_CAPABILITIY = GeneralHelper
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
			event.addCapability(TRANSFORMATION_PLAYER_CAPABILITIY,
					new CapabilityProvider<>(CapabilitiesInit.CAPABILITY_TRANSFORMATION_PLAYER));
			event.addCapability(VAMPIRE, new CapabilityProvider<>(CapabilitiesInit.CAPABILITY_VAMPIRE));
		} else if (entity instanceof EntityVillager) {
			event.addCapability(TRANSFORMATION_CREATURE_CAPABILITY,
					new TransformationCreatureProvider(Transformation.WEREWOLF, Transformation.VAMPIRE));
		}

		if (entity instanceof EntityVillager || entity instanceof EntityGoblinTD || entity instanceof EntityPlayer
				|| entity instanceof ITransformationCreature) {
			event.addCapability(INFECT_IN_TICKS_CAPABILITY,
					new CapabilityProvider<>(CapabilitiesInit.CAPABILITY_INFECT_IN_TICKS));
			event.addCapability(INFECT_ON_NEXT_MOON,
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
			transformationPlayer.setRituals(oldTransformationPlayer.getRituals());
			transformationPlayer.setTransformationData(oldTransformationPlayer.getTransformationData());

			if (transformationPlayer.getTransformation() == Transformation.WEREWOLF) {
				WerewolfHelper.setTransformed(player, false);
			}

			if (transformationPlayer.getTransformation() == Transformation.VAMPIRE) {
				VampireEventHandler.onVampireRespawn(player);
			}

			MinecraftServer server = player.getServer();
			GeneralHelper.executeOnMainThreadIn(() -> {
				sendPackets(server, player);
			}, 100, server, "SyncCapAfterPlayerDeath1");
			GeneralHelper.executeOnMainThreadIn(() -> {
				sendPackets(server, player);
			}, 40000, server, "SyncCapAfterPlayerDeath2");
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
