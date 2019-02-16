package theblockbox.huntersdream.util.handlers;

import static net.minecraft.init.SoundEvents.ENTITY_PLAYER_BREATH;
import static net.minecraft.init.SoundEvents.ENTITY_PLAYER_DEATH;
import static net.minecraft.init.SoundEvents.ENTITY_PLAYER_HURT;
import static net.minecraft.init.SoundEvents.ENTITY_PLAYER_HURT_DROWN;
import static net.minecraft.init.SoundEvents.ENTITY_PLAYER_HURT_ON_FIRE;
import static net.minecraft.init.SoundEvents.ENTITY_WOLF_DEATH;
import static net.minecraft.init.SoundEvents.ENTITY_WOLF_GROWL;
import static net.minecraft.init.SoundEvents.ENTITY_WOLF_HURT;
import static net.minecraft.init.SoundEvents.ENTITY_WOLF_PANT;

import java.util.Optional;
import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.api.event.TransformationEvent;
import theblockbox.huntersdream.api.event.WerewolfTransformingEvent;
import theblockbox.huntersdream.init.*;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.exceptions.UnexpectedBehaviorException;
import theblockbox.huntersdream.util.helpers.ChanceHelper;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;
import theblockbox.huntersdream.util.interfaces.IInfectOnNextMoon;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformation;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class WerewolfEventHandler {
	// use LivingDamage only for removing damage and LivingHurt for damage and
	// damaged resources
	@SubscribeEvent
	public static void onEntityHurt(LivingHurtEvent event) {
		EntityLivingBase attacked = event.getEntityLiving();
		if (event.getSource().getTrueSource() instanceof EntityLivingBase) {
			EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();
			Optional<ITransformation> transformationAttacker = TransformationHelper.getITransformation(attacker);

			if (transformationAttacker.isPresent() && WerewolfHelper.isTransformed(attacker)) {
				if (attacker instanceof EntityPlayer) {
					// TODO: Cooldown
					EntityPlayer player = (EntityPlayer) attacker;
					int biteLevel = TransformationHelper.getITransformationPlayer(player).getSkillLevel(SkillInit.BITE_0);
					if (biteLevel != -1) {
						WerewolfHelper.setLastAttackBite(attacker, ChanceHelper.chanceOf(attacker, 25));
						if(biteLevel > 1) {
							// TODO: Ingore armor
							event.setAmount(event.getAmount() + 5.0F);
							if((biteLevel == 2) && ChanceHelper.chanceOf(attacker, 25)
									&& attacked.isEntityUndead()) {
								attacked.addPotionEffect(new PotionEffect(MobEffects.WITHER, 100));
							}
						}
					}
				}

				// handle werewolf infection
				// if the werewolf can infect
				if (WerewolfHelper.canInfect(attacker)) {
					if (ChanceHelper.chanceOf(attacker, WerewolfHelper.getInfectionPercentage(attacker))) {
						// and the entity can be infected
						if (TransformationHelper.canChangeTransformation(attacked)
								&& TransformationHelper.canBeInfectedWith(Transformation.WEREWOLF, attacked)
								&& (!TransformationHelper.isInfected(attacked))) {
							// infect the entity
							WerewolfHelper.infectEntityAsWerewolf(attacked);
						}
					}
				}
			}
		}

		if (attacked instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) attacked;
			if (!player.world.isRemote) {
				ITransformationPlayer cap = TransformationHelper.getITransformationPlayer(player);
				if (WerewolfHelper.isWerewolfTime(player.world) && !WerewolfHelper.isTransformed(player)
						&& (cap.getTransformation() == Transformation.WEREWOLF)
						&& WerewolfHelper.getTransformationStage(player) > 0) {
					// cancel event if damage source isn't magic (including poison) or event can
					// kill player
					event.setCanceled((event.getSource() != WerewolfHelper.WEREWOLF_TRANSFORMATION_DAMAGE)
							|| (event.getAmount() >= player.getHealth()));
				}
			}
		}
	}

	@SubscribeEvent
	public static void onEntityDeath(LivingDeathEvent event) {
		EntityLivingBase killed = event.getEntityLiving();
		DamageSource source = event.getSource();
		if ("player".equals(source.getDamageType())) {
			// just hope that all this casting won't cause any problems
			EntityPlayer player = (EntityPlayer) source.getTrueSource();
			if (WerewolfHelper.isTransformed(player)) {
				// every full heart of the entity's max health gives the player half a hunger
				player.getFoodStats().addStats((int) (killed.getMaxHealth() / 2), 1);
			}
		}
	}

	// heal werewolf players twice as fast as normal
	@SubscribeEvent
	public static void onWerewolfPlayerHeal(LivingHealEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		// TODO: Should this work for human AND werewolf form or only for human form?
		// TODO: Find better way for regenerating twice as fast
		if ((entity instanceof EntityPlayerMP)
				&& (TransformationHelper.getTransformation(entity) == Transformation.WEREWOLF)) {
			event.setAmount(event.getAmount() * 2);
		}
	}

	/**
	 * Called in
	 * {@link TransformationEventHandler#onEntityTick(net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent)}
	 */
	public static void handleWerewolfInfection(EntityLivingBase entity) {
		if (entity.hasCapability(CapabilitiesInit.CAPABILITY_INFECT_ON_NEXT_MOON, null)) {
			IInfectOnNextMoon ionm = WerewolfHelper.getIInfectOnNextMoon(entity).get();
			if (ionm.getInfectionTransformation() == Transformation.WEREWOLF) {
				if (!WerewolfHelper.isWerewolfTime(entity.world)) {
					if (ionm.getInfectionStatus() == IInfectOnNextMoon.InfectionStatus.MOON_ON_INFECTION) {
						ionm.setInfectionStatus(IInfectOnNextMoon.InfectionStatus.AFTER_INFECTION);
					}
				} else if (WerewolfHelper.isWerewolfTime(entity.world)) {
					if (ionm.getInfectionStatus() == IInfectOnNextMoon.InfectionStatus.AFTER_INFECTION) {
						ionm.setInfectionStatus(IInfectOnNextMoon.InfectionStatus.NOT_INFECTED);
						ionm.setInfectionTick(-1);
						ionm.setInfectionTransformation(Transformation.HUMAN);
						// change transformation
						TransformationHelper.changeTransformation(entity, Transformation.WEREWOLF,
								TransformationEvent.TransformationEventReason.INFECTION);
					}
				}
			}
		}
	}

	// these methods are here for easier code understanding

	static void werewolfTimeTransformed(EntityPlayerMP player, ITransformationPlayer cap) {
		WerewolfHelper.applyLevelBuffs(player);
		Random random = player.getRNG();
		int soundTicksBefore = WerewolfHelper.getSoundTicks(player);
		WerewolfHelper.setSoundTicks(player, soundTicksBefore + 1);
		if (random.nextInt(13) < soundTicksBefore) {
			WerewolfHelper.setSoundTicks(player, -80);
			player.world.playSound(null, player.posX, player.posY, player.posZ, ENTITY_WOLF_GROWL,
					player.getSoundCategory(), 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F);
		}
	}

	static void werewolfTimeNotTransformed(EntityPlayerMP player, ITransformationPlayer cap) {
		if (WerewolfHelper.canWerewolfTransform(player)) {
			if (WerewolfHelper.getTransformationStage(player) <= 0) {
				WerewolfHelper.setTimeSinceTransformation(player, player.ticksExisted);
				WerewolfEventHandler.onStageChanged(player, 1);
			}

			// every five seconds (20 * 5 = 100) one stage up
			int nextStage = MathHelper
					.floor(((player.ticksExisted - WerewolfHelper.getTimeSinceTransformation(player))) / 100.0D);

			if (nextStage > 6 || nextStage < 0) {
				WerewolfHelper.setTimeSinceTransformation(player, -1);
				WerewolfHelper.setTransformationStage(player, 0);
				Main.getLogger().warn(
						"Has the ingame time been changed, did the player leave the world or did the player use wolfsbane? Player "
								+ player.getName() + "'s transformation stage (" + nextStage + ") is invalid");
				return;
			}
			if (nextStage > WerewolfHelper.getTransformationStage(player)) {
				WerewolfEventHandler.onStageChanged(player, nextStage);
			}
		}
	}

	static void notWerewolfTimeNotTransformed(EntityPlayerMP player, ITransformationPlayer cap) {
		// currently does nothing
	}

	static void notWerewolfTimeTransformed(EntityPlayerMP player, ITransformationPlayer cap) {
		if (WerewolfHelper.getTransformationStage(player) <= 0) {
			ITextComponent message = new TextComponentTranslation(
					"transformations.huntersdream:werewolf.transformingBack.0");
			message.getStyle().setItalic(Boolean.TRUE).setColor(TextFormatting.BLUE);
			player.sendMessage(message);
			WerewolfHelper.transformPlayer(player, false, WerewolfTransformingEvent.WerewolfTransformingReason.FULL_MOON_END);
			PacketHandler.sendTransformationMessage(player);
			player.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 1200, 2));
			player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 1200, 1));
			player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 300, 4));
			player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 300, 0));
			// night vision for better blindness effect
			player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 300, 0, false, false));
		}
	}

	/** Called when infection stage changes */
	private static void onStageChanged(EntityPlayerMP player, int nextStage) {
		WerewolfHelper.setTransformationStage(player, nextStage);
		int transformationStage = WerewolfHelper.getTransformationStage(player);

		// send chat messages
		if (transformationStage != 0) {
			ITextComponent message = new TextComponentTranslation(
					"transformations.huntersdream:werewolf.transformingInto."
							+ WerewolfHelper.getTransformationStage(player));
			message.getStyle().setItalic(Boolean.TRUE).setColor(TextFormatting.RED);
			player.sendMessage(message);
		}

		switch (transformationStage) {
		case 1:
			player.world.playSound(null, player.posX, player.posY, player.posZ, SoundInit.HEART_BEAT,
					SoundCategory.PLAYERS, 100, 1);
			player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 550, 1));
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
			player.world.playSound(null, player.getPosition(), SoundInit.WEREWOLF_HOWLING, SoundCategory.PLAYERS, 100,
					1);
			WerewolfHelper.setTimeSinceTransformation(player, -1);
			WerewolfHelper.setTransformationStage(player, 0);
			WerewolfHelper.transformPlayer(player, true, WerewolfTransformingEvent.WerewolfTransformingReason.FULL_MOON);
			// completely heal player
			player.setHealth(player.getMaxHealth());
			break;
		default:
			throw new UnexpectedBehaviorException(
					"Stage " + WerewolfHelper.getTransformationStage(player) + " is not a valid stage");
		}
		// sync transformation extra data with player (for transformation overlays)
		PacketHandler.sendTransformationMessageToPlayer(player, player);
	}

	@SubscribeEvent
	public static void onEntityItemPickup(EntityItemPickupEvent event) {
		if (WerewolfHelper.isTransformed(event.getEntityPlayer()) && !event.getEntityPlayer().isCreative()) {
			event.setCanceled(true);
		}
	}

	// handle werewolves clicked with wolfsbane
	@SubscribeEvent
	public static void onRightClick(PlayerInteractEvent.EntityInteract event) {
		World world = event.getWorld();
		if (event.getTarget() instanceof EntityLivingBase) {
			EntityLivingBase interactedWith = (EntityLivingBase) event.getTarget();
			ItemStack stack = event.getItemStack();
			if ((TransformationHelper.getTransformation(interactedWith) == Transformation.WEREWOLF)
					&& (stack.getItem() == Item.getItemFromBlock(BlockInit.ACONITE_FLOWER))) {
				// we don't want to open any gui, so we say that this
				// interaction was a success and cancel the event
				event.setCanceled(true);
				event.setCancellationResult(EnumActionResult.SUCCESS);
				if (!world.isRemote) {
					WerewolfHelper.applyAconiteEffects(interactedWith, true);
					if(!event.getEntityPlayer().isCreative()) {
						stack.shrink(1);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerSleep(PlayerSleepInBedEvent event) {
		EntityPlayer player = event.getEntityPlayer();
		if ((TransformationHelper.getTransformation(player) == Transformation.WEREWOLF)
				&& WerewolfHelper.isTransformed(player)) {
			// TODO: set to EntityPlayer.SleepResult#NOT_POSSIBLE_NOW ?
			event.setResult(EntityPlayer.SleepResult.OTHER_PROBLEM);
			if (!player.world.isRemote) {
				player.sendStatusMessage(new TextComponentTranslation(Reference.MODID + ".werewolfNotAllowedToSleep"),
						true);
			}
		}
	}

	// removes hunger effect from werewolves when eating food that would normally
	// cause hunger
	// TODO: Find better way?
	@SubscribeEvent
	public static void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
		EntityLivingBase entity = event.getEntityLiving();
		if (!entity.world.isRemote && (TransformationHelper.getTransformation(entity) == Transformation.WEREWOLF)
				&& (event.getItem().getItem() instanceof ItemFood)) {
			// using AT to access ItemFood#potionId
			PotionEffect foodEffect = ((ItemFood) event.getItem().getItem()).potionId;
			if ((foodEffect != null) && (foodEffect.getPotion() == MobEffects.HUNGER)) {
				PotionEffect activeEffect = entity.getActivePotionEffect(MobEffects.HUNGER);
				if ((activeEffect != null) && (activeEffect.getAmplifier() >= foodEffect.getAmplifier())
						&& (activeEffect.doesShowParticles() == foodEffect.doesShowParticles())) {
					entity.removePotionEffect(MobEffects.HUNGER);
				}
			}
		}
	}

	// play wolf sounds instead of normal player sounds
	@SubscribeEvent
	public static void onSoundPlayedAtEntity(PlaySoundAtEntityEvent event) {
		if (event.getEntity() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntity();
			if (WerewolfHelper.isTransformed(player)) {
				SoundEvent sound = event.getSound();
				if (sound == ENTITY_PLAYER_BREATH) {
					event.setSound(ENTITY_WOLF_PANT);
				} else if (sound == ENTITY_PLAYER_DEATH) {
					event.setSound(ENTITY_WOLF_DEATH);
				} else if (sound == ENTITY_PLAYER_HURT || sound == ENTITY_PLAYER_HURT_DROWN
						|| sound == ENTITY_PLAYER_HURT_ON_FIRE) {
					event.setSound(ENTITY_WOLF_HURT);
				} else {
					return;
				}
				Random random = player.getRNG();
				event.setPitch((random.nextFloat() - random.nextFloat()) * 0.2F);
			}
		}
	}

	public static void addHeartsToPlayer(EntityPlayer player, double extraHalfHearts) {
		IAttributeInstance attribute = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
		attribute.setBaseValue(attribute.getBaseValue() + extraHalfHearts);
	}
}
