package theblockbox.huntersdream.util.handlers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.enums.Transformations;
import theblockbox.huntersdream.util.helpers.GeneralHelper;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;
import theblockbox.huntersdream.util.interfaces.IInfectInTicks;
import theblockbox.huntersdream.util.interfaces.IInfectOnNextMoon;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationCreature;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;
import theblockbox.huntersdream.util.interfaces.transformation.IVampire;
import theblockbox.huntersdream.util.interfaces.transformation.IWerewolf;

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

	@SubscribeEvent
	public static void onCapabilityAttach(AttachCapabilitiesEvent<Entity> event) {
		Entity entity = event.getObject();
		if (entity instanceof EntityPlayer) {
			event.addCapability(TRANSFORMATION_PLAYER_CAPABILITIY,
					new CapabilityProvider<ITransformationPlayer>(CapabilitiesInit.CAPABILITY_TRANSFORMATION_PLAYER));
			event.addCapability(WEREWOLF, new CapabilityProvider<IWerewolf>(CapabilitiesInit.CAPABILITY_WEREWOLF));
			event.addCapability(VAMPIRE, new CapabilityProvider<IVampire>(CapabilitiesInit.CAPABILITY_VAMPIRE));
		} else if (entity instanceof EntityVillager) {
			event.addCapability(TRANSFORMATION_CREATURE_CAPABILITY,
					new TransformationCreatureProvider(Transformations.WEREWOLF, Transformations.VAMPIRE));
		}

		if (entity instanceof EntityVillager || entity instanceof EntityGoblinTD || entity instanceof EntityPlayer
				|| entity instanceof ITransformationCreature) {
			event.addCapability(INFECT_IN_TICKS_CAPABILITY,
					new CapabilityProvider<IInfectInTicks>(CapabilitiesInit.CAPABILITY_INFECT_IN_TICKS));
			event.addCapability(INFECT_ON_NEXT_MOON,
					new CapabilityProvider<IInfectOnNextMoon>(CapabilitiesInit.CAPABILITY_INFECT_ON_NEXT_MOON));
		}
	}

	@SubscribeEvent
	public static void onPlayerClone(PlayerEvent.Clone event) throws InterruptedException {
		if (event.isWasDeath()) {
			EntityPlayer player = event.getEntityPlayer();
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

			transformationPlayer.setLevel(oldTransformationPlayer.getLevel());
			transformationPlayer.setTransformed(false);
			transformationPlayer.setXP(oldTransformationPlayer.getXP());
			transformationPlayer.setTransformation(oldTransformationPlayer.getTransformation());
			transformationPlayer.setTextureIndex(oldTransformationPlayer.getTextureIndex());
			transformationPlayer.setRituals(oldTransformationPlayer.getRituals());

			new Thread(() -> {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					Main.getLogger().catching(e);
				}
				player.getServer().addScheduledTask(() -> {
					for (EntityPlayerMP p : player.getServer().getPlayerList().getPlayers())
						PacketHandler.sendTransformationMessageToPlayer(p, (EntityPlayerMP) player);
				});
			}, "SyncCapAfterPlayerDeath1").start();
			new Thread(() -> {
				try {
					Thread.sleep(40000);
				} catch (InterruptedException e) {
					Main.getLogger().catching(e);
				}
				player.getServer().addScheduledTask(() -> {
					for (EntityPlayerMP p : player.getServer().getPlayerList().getPlayers())
						PacketHandler.sendTransformationMessageToPlayer(p, (EntityPlayerMP) player);
				});
			}, "SyncCapAfterPlayerDeath2").start();
		}
	}
}