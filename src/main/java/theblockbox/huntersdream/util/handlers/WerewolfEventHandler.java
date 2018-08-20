package theblockbox.huntersdream.util.handlers;

import java.util.ArrayList;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemPickupEvent;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.entity.EntityWerewolf;
import theblockbox.huntersdream.event.TransformationXPEvent.TransformationXPSentReason;
import theblockbox.huntersdream.init.CapabilitiesInit;
import theblockbox.huntersdream.init.PotionInit;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.enums.Transformations;
import theblockbox.huntersdream.util.exceptions.UnexpectedBehaviorException;
import theblockbox.huntersdream.util.handlers.PacketHandler.Packets;
import theblockbox.huntersdream.util.helpers.ChanceHelper;
import theblockbox.huntersdream.util.helpers.EffectivenessHelper;
import theblockbox.huntersdream.util.helpers.GeneralHelper;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;
import theblockbox.huntersdream.util.interfaces.IInfectOnNextMoon;
import theblockbox.huntersdream.util.interfaces.IInfectOnNextMoon.InfectionStatus;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformation;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;
import theblockbox.huntersdream.util.interfaces.transformation.IWerewolf;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class WerewolfEventHandler {
	/** @deprecated Don't use. We're going to make a new system */
	@Deprecated
	private static final ArrayList<EntityWerewolf> PLAYER_WEREWOLVES = new ArrayList<>();

	// use LivingDamage only for removing damage and LivingHurt for damage and
	// damaged resources
	@SubscribeEvent
	public static void onWerewolfHurt(LivingHurtEvent event) {
		// what this method should do:
		// if attacker is supernatural, use ability or weapon
		// if attacker isn't supernatural use direct attacker, then weapon, then
		// attacker

		EntityLivingBase hurtWerewolf = event.getEntityLiving();
		ITransformation transformationWerewolf = TransformationHelper.getITransformation(hurtWerewolf);
		// this cast may cause exceptions, but we'll have to test
		EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();
		ITransformation transformationAttacker = TransformationHelper.getITransformation(attacker);

		if (transformationWerewolf != null) {
			if (WerewolfHelper.transformedWerewolf(hurtWerewolf)) {
				// now it is made sure that the attacked entity is a werewolf

				if (!event.getSource().damageType.equals("thorns")) {
					if (attacker != null) {
						ItemStack weaponItemStack = attacker.getHeldItemMainhand();
						Item weapon = weaponItemStack.getItem();
						// when the attacker is supernatural,
						if (transformationAttacker != null
								&& transformationAttacker.getTransformation().isSupernatural()) {
							// has no weapon and is effective against werewolf
							if (weaponItemStack.isEmpty() && WerewolfHelper.effectiveAgainstWerewolf(attacker)) {
								event.setAmount(
										event.getAmount() * WerewolfHelper.getEffectivenessAgainstWerewolf(attacker));
								return;
							} else if (!weaponItemStack.isEmpty() && WerewolfHelper.effectiveAgainstWerewolf(weapon)) {
								event.setAmount(
										event.getAmount() * WerewolfHelper.getEffectivenessAgainstWerewolf(weapon));
								return;
							}
						} else {
							// when attacker is not supernatural

							// first test if immediate source (for example arrow) is effective
							Entity immediateSource = event.getSource().getImmediateSource();
							if (WerewolfHelper.effectiveAgainstWerewolf(immediateSource)) {
								event.setAmount(event.getAmount()
										* WerewolfHelper.getEffectivenessAgainstWerewolf(immediateSource));
								return;
							} else if (!weaponItemStack.isEmpty() && WerewolfHelper.effectiveAgainstWerewolf(weapon)) {
								event.setAmount(
										event.getAmount() * WerewolfHelper.getEffectivenessAgainstWerewolf(weapon));
								return;
							} else if (WerewolfHelper.effectiveAgainstWerewolf(attacker)) {
								event.setAmount(
										event.getAmount() * WerewolfHelper.getEffectivenessAgainstWerewolf(attacker));
								return;
							}
						}
					}
				}

				// when nothing applies (this should also reduce any other damage from other
				// damage sources)
				event.setAmount((event.getAmount() / Transformations.WEREWOLF.getProtection()));
			}
		}

		if (transformationAttacker != null && WerewolfHelper.transformedWerewolf(attacker)) {
			onWerewolfAttack(attacker, transformationAttacker, hurtWerewolf);
		}
	}

	public static void onWerewolfAttack(EntityLivingBase attacker, ITransformation transformationAttacker,
			EntityLivingBase attacked) {
		// handle werewolf infection
		// if the werewolf can infect
		if (WerewolfHelper.canInfect(attacker)) {
			if (ChanceHelper.chanceOf(WerewolfHelper.getInfectionPercentage(attacker))) {
				// and the entity can be infected
				if (TransformationHelper.canChangeTransformation(attacked)
						&& TransformationHelper.canBeInfectedWith(Transformations.WEREWOLF, attacked)
						&& (!TransformationHelper.isInfected(attacked))) {
					// infect the entity
					WerewolfHelper.infectEntityAsWerewolf(attacked);
				}
			}
		}
		if (attacker instanceof EntityPlayer) {
			// fill hunger
			EntityPlayer player = (EntityPlayer) attacker;
			player.getFoodStats().addStats(1, 1);
		}
	}

	@SubscribeEvent
	public static void onEntityDeath(LivingDeathEvent event) {
		if (event.getSource().getTrueSource() instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) event.getSource().getTrueSource();
			if (WerewolfHelper.transformedWerewolf(player) && WerewolfHelper.hasMainlyControl(player)) {
				TransformationHelper.addXP(player, 10, TransformationXPSentReason.WEREWOLF_HAS_KILLED);
			}
		}
	}

	/**
	 * Called in
	 * {@link TransformationEventHandler#onEntityTick(net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent)}
	 */
	public static void handleInfection(EntityLivingBase entity) {
		if (entity.hasCapability(CapabilitiesInit.CAPABILITY_INFECT_ON_NEXT_MOON, null)) {
			IInfectOnNextMoon ionm = WerewolfHelper.getIInfectOnNextMoon(entity);
			if (ionm.getInfectionTransformation() == Transformations.WEREWOLF) {
				if (!WerewolfHelper.isWerewolfTime(entity)) {
					if (ionm.getInfectionStatus() == InfectionStatus.MOON_ON_INFECTION) {
						ionm.setInfectionStatus(InfectionStatus.AFTER_INFECTION);
					}
				} else if (WerewolfHelper.isWerewolfTime(entity)) {
					if (ionm.getInfectionStatus() == InfectionStatus.AFTER_INFECTION) {
						ionm.setInfectionStatus(InfectionStatus.NOT_INFECTED);
						ionm.setInfectionTick(-1);
						ionm.setInfectionTransformation(Transformations.HUMAN);
						// change transformation
						TransformationHelper.changeTransformation(entity, Transformations.WEREWOLF);
					}
				}
			}
		}
	}

	// these methods are here for easier code understanding

	static void werewolfTimeTransformed(EntityPlayerMP player, ITransformationPlayer cap) {
	}

	static void werewolfTimeNotTransformed(EntityPlayerMP player, ITransformationPlayer cap) {
		if (!player.isPotionActive(PotionInit.POTION_WOLFSBANE)) {
			IWerewolf werewolf = WerewolfHelper.getIWerewolf(player);

			if (werewolf.getTransformationStage() <= 0) {
				werewolf.setTimeSinceTransformation(player.ticksExisted);
				onStageChanged(player, werewolf, 1, cap);
			}

			// every five seconds (20 * 5 = 100) one stage up
			int nextStage = MathHelper
					.floor(((double) (player.ticksExisted - werewolf.getTimeSinceTransformation())) / 100.0D);

			if (nextStage > 6 || nextStage < 0) {
				Packets.PLAY_SOUND.sync(player, "howl", 1000000000, 1);
				werewolf.setTimeSinceTransformation(-1);
				werewolf.setTransformationStage(0);
				Main.LOGGER.warn(
						"Has the ingame time been changed, did the player leave the world or did the player use wolfsbane? Player "
								+ player.getName() + "'s transformation stage (" + nextStage + ") is invalid");
				return;
			}
			if (nextStage > werewolf.getTransformationStage()) {
				onStageChanged(player, werewolf, nextStage, cap);
			}
		}
	}

	static void notWerewolfTimeNotTransformed(EntityPlayerMP player, ITransformationPlayer cap) {
		// currently does nothing
	}

	static void notWerewolfTimeTransformed(EntityPlayerMP player, ITransformationPlayer cap) {
		IWerewolf werewolf = WerewolfHelper.getIWerewolf(player);
		if (werewolf.getTransformationStage() <= 0) {
			player.sendMessage(
					new TextComponentTranslation("transformations.huntersdream:werewolf.transformingBack.0"));
			cap.setTransformed(false);
			Packets.TRANSFORMATION.sync(player);
			player.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 1200, 2));
			player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 1200, 1));
			player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 300, 4));
			player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 300, 0));
			// night vision for better blindness effect
			player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 300, 0, false, false));
		}
	}

	/** Called when infection stage changes */
	private static void onStageChanged(EntityPlayer player, IWerewolf werewolf, int nextStage,
			ITransformationPlayer cap) {

		werewolf.setTransformationStage(nextStage);

		switch (werewolf.getTransformationStage()) {
		case 1:
			Packets.PLAY_SOUND.sync(player, "heartbeat", 100, 1);
			player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 550, 1));
			player.addPotionEffect(new PotionEffect(MobEffects.POISON, 550, 0));
			break;
		case 2:
			player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 450, 1));
			break;
		case 3:
			player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 350, 255));
			player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 350, 255));
			player.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 350, 255));
			break;
		case 4:
			player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 120, 0));
			break;
		case 5:
			// nothing happens
			break;
		case 6:
			Packets.PLAY_SOUND.sync(player, "howl", 1000000000, 1);
			werewolf.setTimeSinceTransformation(-1);
			werewolf.setTransformationStage(0);
			cap.setTransformed(true);
			Packets.TRANSFORMATION.sync(player);

// TODO: Remove this after no control is done
//			if (!WerewolfHelper.hasControl(player)) {
//				World world = player.world;
//				EntityWerewolf were = new EntityWerewolf(world, TransformationHelper.getCap(player).getTextureIndex(),
//						"player" + player.getName());
//				were.setPosition(player.posX, player.posY, player.posZ);
//				PLAYER_WEREWOLVES.add(were);
//				world.spawnEntity(were);
//				Packets.NO_CONTROL.sync(player, were);
//			}
			break;
		default:
			throw new UnexpectedBehaviorException(
					"Stage " + werewolf.getTransformationStage() + " is not a valid stage");
		}
		if (werewolf.getTransformationStage() != 0) {
			player.sendMessage(new TextComponentTranslation(
					"transformations.huntersdream:werewolf.transformingInto." + werewolf.getTransformationStage()));
		}

	}

	/**
	 * @deprecated Don't use, we're going to change the no control mechanic
	 */
	@Deprecated
	public static EntityWerewolf[] getPlayerWerewolves() {
		return PLAYER_WEREWOLVES.toArray(new EntityWerewolf[0]);
	}

	@SubscribeEvent
	public static void onEntityHurt(LivingHurtEvent event) {
		if (event.getEntityLiving() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			if (!player.world.isRemote) {
				ITransformationPlayer cap = TransformationHelper.getCap(player);
				IWerewolf werewolf = WerewolfHelper.getIWerewolf(player);
				if (WerewolfHelper.isWerewolfTime(player) && !cap.transformed()
						&& (cap.getTransformation() == Transformations.WEREWOLF)
						&& werewolf.getTransformationStage() > 0) {
					// cancel event if damage source isn't magic (including poison) or event can
					// kill player
					event.setCanceled(
							(event.getSource() != DamageSource.MAGIC) || (event.getAmount() > player.getHealth()));
				}
			}
		}
	}

	@SubscribeEvent
	public static void onEntityItemPickup(EntityItemPickupEvent event) {
		if (WerewolfHelper.transformedWerewolf(event.getEntityPlayer()) && !event.getEntityPlayer().isCreative()) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onItemPickup(ItemPickupEvent event) {
		EntityItem originalEntity = event.getOriginalEntity();
		if (!originalEntity.world.isRemote) {
			String throwerName = originalEntity.getThrower();
			EntityPlayer player = event.player;
			ITransformationPlayer cap = TransformationHelper.getCap(player);
			Item item = event.getStack().getItem();
			if (EffectivenessHelper.effectiveAgainstTransformation(cap.getTransformation(), item)) {
				// now it is ensured that the item is effective against the player
				String msg = "transformations." + cap.getTransformation().toString() + ".";

				EntityPlayer thrower;
				if (!(throwerName == null) && !(throwerName.equals("null"))
						&& !(throwerName.equals(player.getName()))) {
					thrower = originalEntity.world.getPlayerEntityByName(throwerName);
					Packets.TRANSFORMATION_REPLY.sync(player, msg + "fp.touched", player, item);
					Packets.TRANSFORMATION_REPLY.sync(thrower, msg + "tp.touched", player, item);
				} else {
					Packets.TRANSFORMATION_REPLY.sync(player, msg + "fp.picked", player, item);
					thrower = GeneralHelper.getNearestPlayer(player.world, player, 5);
					if (thrower != null) {
						Packets.TRANSFORMATION_REPLY.sync(thrower, msg + "tp.picked", player, item);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onRightClick(PlayerInteractEvent.RightClickItem event) {
		EntityPlayer player = event.getEntityPlayer();
		if (!player.world.isRemote) {
			// Item item = event.getItemStack().getItem();
			// if(TransformationHelper.effectiveAgainstTransformation(effectiveAgainst,
			// object))
		}
	}

	/**
	 * Called from
	 * {@link EventHandler#onPlayerJoin(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent)}
	 */
	public static void resetTransformationStage(EntityPlayerMP player) {
		WerewolfHelper.getIWerewolf(player).setTransformationStage(0);
	}
}
