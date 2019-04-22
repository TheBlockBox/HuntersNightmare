package theblockbox.huntersdream.util.handlers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.api.event.TransformationEvent;
import theblockbox.huntersdream.api.event.WerewolfTransformingEvent;
import theblockbox.huntersdream.api.helpers.ChanceHelper;
import theblockbox.huntersdream.api.helpers.TransformationHelper;
import theblockbox.huntersdream.api.helpers.WerewolfHelper;
import theblockbox.huntersdream.api.init.BlockInit;
import theblockbox.huntersdream.api.init.CapabilitiesInit;
import theblockbox.huntersdream.api.init.SkillInit;
import theblockbox.huntersdream.api.interfaces.IInfectOnNextMoon;
import theblockbox.huntersdream.api.interfaces.transformation.ITransformationPlayer;
import theblockbox.huntersdream.util.Reference;

import java.util.Random;

import static net.minecraft.init.SoundEvents.*;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class WerewolfEventHandler {
    private static EntityLivingBase lastBittenEntity = null;
    private static float lastBiteDamage = 0.0F;

    // TODO: Better way to make damage not go through armor?
    @SubscribeEvent
    public static void onEntityDamaged(LivingDamageEvent event) {
        EntityLivingBase attacked = event.getEntityLiving();
        if ((WerewolfEventHandler.lastBittenEntity == attacked) && (event.getSource().getTrueSource() instanceof EntityLivingBase)) {
            EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();
            if (TransformationHelper.getITransformation(attacker).isPresent() && WerewolfHelper.isTransformed(attacker)
                    && (attacker instanceof EntityPlayer) && WerewolfHelper.wasLastAttackBite(attacker)) {
                event.setAmount(Math.max(WerewolfEventHandler.lastBiteDamage, event.getAmount()));
                WerewolfEventHandler.lastBittenEntity = null;
            }
        }
    }

    // use LivingDamage only for removing damage and LivingHurt for damage and
    // damaged resources

    /**
     * Called from {@link TransformationEventHandler#onEntityHurt(LivingHurtEvent)}
     */
    public static void onEntityHurt(LivingHurtEvent event) {
        // reset last bitten entity
        WerewolfEventHandler.lastBittenEntity = null;
        EntityLivingBase attacked = event.getEntityLiving();
        if (event.getSource().getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();
            if (TransformationHelper.getITransformation(attacker).isPresent() && WerewolfHelper.isTransformed(attacker)) {
                if ((attacker instanceof EntityPlayer) && WerewolfHelper.wasLastAttackBite(attacker)) {
                    if ((TransformationHelper.getITransformationPlayer((EntityPlayer) attacker).getSkillLevel(SkillInit.BITE_0) >= 2)
                            && (attacked.isEntityUndead()) || (TransformationHelper.getTransformation(attacked).isUndead())) {
                        attacked.addPotionEffect(new PotionEffect(MobEffects.WITHER, 100));
                    }
                    // set last bitten entity and bite damage
                    WerewolfEventHandler.lastBittenEntity = attacked;
                    WerewolfEventHandler.lastBiteDamage = event.getAmount();
                }

                // handle werewolf infection
                // if the werewolf can infect
                if (WerewolfHelper.canInfect(attacker)) {
                    if (ChanceHelper.chanceOf(attacker, WerewolfHelper.getInfectionPercentage(attacker))) {
                        // and the entity can be infected
                        if (TransformationHelper.canBeInfectedWith(Transformation.WEREWOLF, attacked)
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
        DamageSource source = event.getSource();
        if ("player".equals(source.getDamageType())) {
            // just hope that all this casting won't cause any problems
            EntityPlayer player = (EntityPlayer) source.getTrueSource();
            if (WerewolfHelper.isTransformed(player)) {
                // every kill gives one full hunger
                player.getFoodStats().addStats(2, 1);
            }
        }
    }

    // heal transformed werewolf players twice as fast as normal
    @SubscribeEvent
    public static void onWerewolfPlayerHeal(LivingHealEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if ((entity instanceof EntityPlayerMP) && (TransformationHelper.getTransformation(entity) == Transformation.WEREWOLF)
                && WerewolfHelper.isTransformed(entity)) {
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
                // we don't want to open any gui, so we say that the
                // interaction was a success and cancel the event
                event.setCanceled(true);
                event.setCancellationResult(EnumActionResult.SUCCESS);
                if (!world.isRemote) {
                    WerewolfHelper.applyAconiteEffects(interactedWith, true);
                    if (!event.getEntityPlayer().isCreative()) {
                        stack.shrink(1);
                    }
                }
            }
            EntityPlayer interacting = event.getEntityPlayer();
            if (WerewolfHelper.isTransformed(interacting)) {
                // we don't want the werewolf to interact with anything when transformed,
                // so we'll say that the interaction was a success and cancel the event
                event.setCanceled(true);
                event.setCancellationResult(EnumActionResult.SUCCESS);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerSleep(PlayerSleepInBedEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        if ((TransformationHelper.getTransformation(player) == Transformation.WEREWOLF)
                && WerewolfHelper.isTransformed(player)) {
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

    @SubscribeEvent
    public static void onPlayerTransforming(WerewolfTransformingEvent.PlayerWerewolfTransformingEvent event) {
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
        WerewolfTransformingEvent.WerewolfTransformingReason reason = event.getTransformingEventReason();
        // if the ticks are higher than 0 (meaning that the player is transformed because of wilful transformation),
        // the player still has time until they'll get turned back, the fired event is trying to transform them back and
        // the reason is that the full moon has "ended", cancel the event and don't turn them back
        if (WerewolfHelper.isPlayerWilfullyTransformed(player) && !WerewolfHelper.hasPlayerReachedWilfulTransformationLimit(player)
                && event.transformingBack() && (reason == WerewolfTransformingEvent.WerewolfTransformingReason.FULL_MOON_END)) {
            event.setCanceled(true);
            // else if the player transformed back because they either were forced or did it themselves,
        } else if ((reason == WerewolfTransformingEvent.WerewolfTransformingReason.WILFUL_TRANSFORMATION_ENDING)
                || (reason == WerewolfTransformingEvent.WerewolfTransformingReason.WILFUL_TRANSFORMATION_FORCED_ENDING)) {
            // reset the ticks
            WerewolfHelper.setWilfulTransformationTicks(player, -player.world.getTotalWorldTime());
        }
    }
}
