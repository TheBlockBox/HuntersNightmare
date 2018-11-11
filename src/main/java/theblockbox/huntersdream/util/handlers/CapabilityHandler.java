package theblockbox.huntersdream.util.handlers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.capabilities.CapabilityProvider;
import theblockbox.huntersdream.capabilities.TransformationCreatureProvider;
import theblockbox.huntersdream.entity.EntityGoblinTD;
import theblockbox.huntersdream.init.CapabilitiesInit;
import theblockbox.huntersdream.init.TransformationInit;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.helpers.GeneralHelper;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;
import theblockbox.huntersdream.util.interfaces.IInfectInTicks;
import theblockbox.huntersdream.util.interfaces.IInfectOnNextMoon;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationCreature;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class CapabilityHandler {
	public final static ResourceLocation TRANSFORMATION_PLAYER_CAPABILITIY = GeneralHelper
			.newResLoc("transformationplayer");
	public static final ResourceLocation TRANSFORMATION_CREATURE_CAPABILITY = GeneralHelper
			.newResLoc("transformationcreature");
	public static final ResourceLocation INFECT_IN_TICKS_CAPABILITY = GeneralHelper.newResLoc("infectinticks");
	public static final ResourceLocation INFECT_ON_NEXT_MOON = GeneralHelper.newResLoc("infectonnextmoon");
	public static final ResourceLocation WEREWOLF = GeneralHelper.newResLoc("werewolf");
	public static final ResourceLocation VAMPIRE = GeneralHelper.newResLoc("vampire");
	public static final ResourceLocation ITEM_HANDLER = GeneralHelper.newResLoc("itemhandler");

	@SubscribeEvent
	public static void onCapabilityAttach(AttachCapabilitiesEvent<Entity> event) {
		Entity entity = event.getObject();
		if (entity instanceof EntityPlayer) {
			event.addCapability(TRANSFORMATION_PLAYER_CAPABILITIY,
					new CapabilityProvider<>(CapabilitiesInit.CAPABILITY_TRANSFORMATION_PLAYER));
			event.addCapability(WEREWOLF, new CapabilityProvider<>(CapabilitiesInit.CAPABILITY_WEREWOLF));
			event.addCapability(VAMPIRE, new CapabilityProvider<>(CapabilitiesInit.CAPABILITY_VAMPIRE));
		} else if (entity instanceof EntityVillager) {
			event.addCapability(TRANSFORMATION_CREATURE_CAPABILITY,
					new TransformationCreatureProvider(TransformationInit.WEREWOLF, TransformationInit.VAMPIRE));
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

			IInfectInTicks iit = TransformationHelper.getIInfectInTicks(player);
			IInfectInTicks iitOld = TransformationHelper.getIInfectInTicks(originalPlayer);
			iit.setCurrentlyInfected(iitOld.currentlyInfected());
			iit.setInfectionTransformation(iitOld.getInfectionTransformation());
			iit.setTime(iitOld.getTime());

			IInfectOnNextMoon ionm = WerewolfHelper.getIInfectOnNextMoon(player);
			IInfectOnNextMoon ionmOld = WerewolfHelper.getIInfectOnNextMoon(originalPlayer);
			ionm.setInfectionStatus(ionmOld.getInfectionStatus());
			ionm.setInfectionTick(ionmOld.getInfectionTick());
			ionm.setInfectionTransformation(ionmOld.getInfectionTransformation());

			ITransformationPlayer transformationPlayer = TransformationHelper.getCap(player);
			ITransformationPlayer oldTransformationPlayer = TransformationHelper.getCap(originalPlayer);

			transformationPlayer.setTransformation(oldTransformationPlayer.getTransformation());
			transformationPlayer.setTextureIndex(oldTransformationPlayer.getTextureIndex());
			transformationPlayer.setRituals(oldTransformationPlayer.getRituals());

			WerewolfHelper.getIWerewolf(player).setTransformed(false);

			if (transformationPlayer.getTransformation() == TransformationInit.VAMPIRE) {
				VampireEventHandler.onVampireRespawn(player);
			}

			final MinecraftServer server = player.getServer();

			sendPackets(server, player);
			GeneralHelper.executeOnMainThreadIn(() -> {
				sendPackets(server, player);
			}, 100, server, "SyncCapAfterPlayerDeath1");
			GeneralHelper.executeOnMainThreadIn(() -> {
				sendPackets(server, player);
			}, 40000, server, "SyncCapAfterPlayerDeath2");

			new Thread(() -> {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					Main.getLogger().catching(e);
				}
				server.addScheduledTask(() -> {
					sendPackets(server, player);
				});
			}, "SyncCapAfterPlayerDeath1").start();
			new Thread(() -> {
				try {
					Thread.sleep(40000);
				} catch (InterruptedException e) {
					Main.getLogger().catching(e);
				}
				server.addScheduledTask(() -> {
					sendPackets(server, player);
				});
			}, "SyncCapAfterPlayerDeath2").start();
		}
	}

	private static void sendPackets(MinecraftServer server, EntityPlayerMP player) {
		for (EntityPlayerMP p : server.getPlayerList().getPlayers()) {
			PacketHandler.sendTransformationMessageToPlayer(p, player);
		}
		PacketHandler.sendBloodMessage(player);
		PacketHandler.sendWerewolfTransformedMessage(player);
	}
}