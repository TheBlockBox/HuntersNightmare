package theblockbox.huntersdream.api.helpers;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import org.apache.commons.lang3.Validate;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.api.event.ExtraDataEvent;
import theblockbox.huntersdream.api.event.TransformationEvent;
import theblockbox.huntersdream.api.event.WerewolfTransformingEvent;
import theblockbox.huntersdream.api.init.CapabilitiesInit;
import theblockbox.huntersdream.api.init.ParticleCommonInit;
import theblockbox.huntersdream.api.init.SkillInit;
import theblockbox.huntersdream.api.init.SoundInit;
import theblockbox.huntersdream.api.interfaces.IInfectOnNextMoon;
import theblockbox.huntersdream.api.interfaces.transformation.ITransformation;
import theblockbox.huntersdream.api.interfaces.transformation.ITransformationPlayer;
import theblockbox.huntersdream.entity.EntityWerewolf;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.exceptions.UnexpectedBehaviorException;
import theblockbox.huntersdream.util.exceptions.WrongSideException;
import theblockbox.huntersdream.util.exceptions.WrongTransformationException;
import theblockbox.huntersdream.util.handlers.PacketHandler;
import theblockbox.huntersdream.util.handlers.TransformationEventHandler;
import theblockbox.huntersdream.util.handlers.ConfigHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;

import static net.minecraft.init.SoundEvents.ENTITY_WOLF_GROWL;

public class WerewolfHelper {
    public static final Capability<IInfectOnNextMoon> CAPABILITY_INFECT_ON_NEXT_MOON = CapabilitiesInit.CAPABILITY_INFECT_ON_NEXT_MOON;
    public static final DamageSource WEREWOLF_TRANSFORMATION_DAMAGE = new DamageSource("huntersdream:werewolfTransformationDamage");

    public static final int LONG_DESPAWN = 18000; // 15 minutes

    /**
     * Returns true when a werewolf can transform in this world
     */
    public static boolean isWerewolfTime(World world) {
        if (world.isRemote)
            throw new WrongSideException("Can only test if it is werewolf time on server side", world);
        return WerewolfHelper.isFullmoon(world) && GeneralHelper.isNight(world);
    }

    /**
     * Returns true if it is full moon in the given world
     */
    public static boolean isFullmoon(World world) {
        if (world.isRemote)
            throw new WrongSideException("Can only test if it is full moon and night on server side", world);
        return (world.getCurrentMoonPhaseFactor() == 1.0F);
    }

    /**
     * Returns how much damage the given entity does unarmed (this method can also
     * be called when the entity is not transformed, as long as it's still a
     * werewolf)
     */
    public static float calculateUnarmedDamage(EntityLivingBase entity, float initialDamage) {
        WerewolfHelper.validateIsWerewolf(entity);
        // if the werewolf is either not transformed or has something in its hands
        // (shouldn't happen for players)
        boolean transformed = WerewolfHelper.isTransformed(entity);

        if (entity instanceof EntityPlayer) {
            // No werewolf bonus with items/weapons
            if (!entity.getHeldItemMainhand().isEmpty()) { return initialDamage; }

            // Player werewolves get unarmed bonus whenever their hand is empty
            ITransformationPlayer transformation = TransformationHelper.getITransformationPlayer((EntityPlayer) entity);
            float dmg = initialDamage + (transformation.getSkillLevel(SkillInit.UNARMED_0)+1) * ConfigHandler.balance.playerWerewolfBonusDamagePerLevel;

            if (transformed) {
                return dmg + ((WerewolfHelper.wasLastAttackBite(entity) && transformation.getSkillLevel(SkillInit.BITE_0) > 0) ?
                    ConfigHandler.balance.playerWerewolfBiteDamage : ConfigHandler.balance.playerWerewolfClawDamage);
            } else {
                return dmg;
            }
        } else {
            // npc werewolf
            if (transformed) {
                return WerewolfHelper.wasLastAttackBite(entity) ? ConfigHandler.balance.npcWerewolfBiteDamage : ConfigHandler.balance.npcWerewolfClawDamage;
            } else {
                return initialDamage;
            }
        }
    }

    /**
     * Returns how much damage the given entity should get if the entity and the
     * initial damage are the passed arguments (this method can also be called when
     * the entity is not transformed, as long as it's still a werewolf)
     */
    public static float calculateReducedDamage(EntityLivingBase entity, float initialDamage) {
        WerewolfHelper.validateIsWerewolf(entity);
        if (WerewolfHelper.isTransformed(entity)) {
            if (entity instanceof EntityPlayer) {
                // default: 1.28 lvl 0: 1.72 lvl 1: 2.17 lvl 2: 4.35
                int level = TransformationHelper.getITransformationPlayer((EntityPlayer) entity).getSkillLevel(SkillInit.ARMOR_0);
                float damageReduction = 1.28F;
                switch (level) {
                    case 0:
                        damageReduction = 1.72F;
                        break;
                    case 1:
                        damageReduction = 2.17F;
                        break;
                    case 2:
                        damageReduction = 4.35F;
                        break;
                }
                return initialDamage / damageReduction;
            } else {
                // same as diamond armor
                return initialDamage / 4.35F;
            }
        } else {
            // no protection when not transformed
            return initialDamage;
        }
    }

    /**
     * Returns true if the werewolf can transform
     */
    public static boolean canWerewolfTransform(EntityLivingBase werewolf) {
        WerewolfHelper.validateIsWerewolf(werewolf);
        return true;
    }

    /**
     * Infects the given entity with lycanthropy
     */
    public static void infectEntityAsWerewolf(EntityLivingBase entityToBeInfected) {
        if (TransformationHelper.canChangeTransformation(entityToBeInfected)
                && TransformationHelper.canBeInfectedWith(Transformation.WEREWOLF, entityToBeInfected)) {
            IInfectOnNextMoon ionm = WerewolfHelper.getIInfectOnNextMoon(entityToBeInfected).get();
            ionm.setInfectionStatus(IInfectOnNextMoon.InfectionStatus.MOON_ON_INFECTION);
            ionm.setInfectionTick(entityToBeInfected.ticksExisted);
            ionm.setInfectionTransformation(Transformation.WEREWOLF);
        }
    }

    public static Optional<IInfectOnNextMoon> getIInfectOnNextMoon(@Nonnull EntityLivingBase entity) {
        Validate.notNull(entity);
        return Optional.ofNullable(entity.getCapability(WerewolfHelper.CAPABILITY_INFECT_ON_NEXT_MOON, null));
    }

    /**
     * Returns true if the given entity can infect other entities (it also checks if
     * the entity is a werewolf and transformed!)
     */
    public static boolean canInfect(EntityLivingBase entity) {
        // all werewolves (players included) can always infect (given they're
        // transformed)
        return WerewolfHelper.isTransformed(entity) && WerewolfHelper.wasLastAttackBite(entity);
    }

    /**
     * Returns the chance of a werewolf infecting a mob/player
     *
     * @param entity The entity whose chance to return
     * @return Returns the chance of infection (100 = always, 0 = never)
     */
    public static int getInfectionPercentage(EntityLivingBase entity) {
        if (WerewolfHelper.canInfect(entity)) {
            return (entity instanceof EntityPlayer) ? 25 : 100;
        } else {
            throw new WrongTransformationException("The given entity is not a werewolf",
                    TransformationHelper.getTransformation(entity));
        }
    }

    /**
     * Returns true when the given player has mainly control in the werewolf form
     */
    public static boolean hasMainlyControl(EntityPlayer player) {
        WerewolfHelper.validateIsWerewolf(player);
        return true;
    }

    /**
     * Returns true when the given entity is transformed and also a werewolf
     */
    public static boolean isTransformed(EntityLivingBase entity) {
        return (TransformationHelper.getTransformation(entity) == Transformation.WEREWOLF)
                && TransformationHelper.getTransformationData(entity).getBoolean("transformed");
    }

    /**
     * Transforms a werewolf. No packet is being sent to the client. Prefer to use
     * {@link #transformPlayer(EntityPlayerMP, boolean, WerewolfTransformingEvent.WerewolfTransformingReason)}
     */
    public static void setTransformed(EntityLivingBase werewolf, boolean transformed) {
        WerewolfHelper.validateIsWerewolf(werewolf);
        NBTTagCompound compound = TransformationHelper.getTransformationData(werewolf);
        compound.setBoolean("transformed", transformed);
    }

    /**
     * Tries to transform a player. Returns true if successful and false if not
     * (also returns true if the player was already transformed). Also sends a
     * packet if something changed.
     */
    public static boolean transformPlayer(@Nonnull EntityPlayerMP player, boolean transformed,
                                          WerewolfTransformingEvent.WerewolfTransformingReason reason) {
        if (WerewolfHelper.isTransformed(player) != transformed) {
            if (MinecraftForge.EVENT_BUS.post(new WerewolfTransformingEvent(player, !transformed, reason))) {
                return false;
            } else {
                WerewolfHelper.setTransformed(player, transformed);
                PacketHandler.sendTransformationMessage(player);
                // get off what you're currently riding
                player.dismountRidingEntity();
                return true;
            }
        }
        return true;
    }

    /**
     * Caution! Your entity has to extend EntityLiving or a subclass and needs a
     * constructor World, int, Transformations (World = spawn world, int = texture
     * to use, Transformation = transformation that the entity should have) When
     * the werewolf transforms back, this constructor will be called and World will
     * be {@link EntityWerewolf#getEntityWorld()}, int will be
     * {@link EntityWerewolf#getTextureIndex()} and Transformation will be
     * {@link Transformation#WEREWOLF}
     *
     * @param entity The entity to be transformed
     */
    @Nullable
    public static EntityWerewolf toWerewolfWhenNight(EntityCreature entity) {
        World world = entity.world;
        if (world.isRemote) {
            throw new WrongSideException("Can't transform entity to werewolf on client side", world);
        } else {
            if (WerewolfHelper.isWerewolfTime(entity.world)) {
                if (WerewolfHelper.canWerewolfTransform(entity)) {
                    if (entity instanceof ITransformation) {
                        ITransformation transformation = (ITransformation) entity;
                        WerewolfHelper.validateIsWerewolf(entity);
                        return new EntityWerewolf(world, transformation.getTextureIndex(),
                                entity.getClass().getName(), WerewolfHelper.postExtraDataEvent(entity, true));
                    } else {
                        WerewolfHelper.validateIsWerewolf(entity);
                        return new EntityWerewolf(world, TransformationHelper
                                .getITransformationCreature(entity)
                                .orElseThrow(() -> new IllegalArgumentException(
                                        "Entity does not implement interface \"ITransformation\" or does not have the TransformationCreature capability"))
                                .getTextureIndex(), "$useCap" + entity.getClass().getName(),
                                WerewolfHelper.postExtraDataEvent(entity, true));
                    }
                }
            }
        }
        return null;
    }

    private static NBTTagCompound postExtraDataEvent(EntityCreature creature, boolean onDataSave) {
        ExtraDataEvent event = new ExtraDataEvent(creature, GeneralHelper.writeEntityToNBT(creature), onDataSave);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getExtraData();
    }

    public static int getTransformationStage(EntityPlayer player) {
        WerewolfHelper.validateIsWerewolf(player);
        return TransformationHelper.getTransformationData(player).getInteger("transformationStage");
    }

    public static void setTransformationStage(EntityPlayerMP player, int stage) {
        WerewolfHelper.validateIsWerewolf(player);
        TransformationHelper.getTransformationData(player).setInteger("transformationStage", stage);
    }

    /**
     * Returns the player.ticksExisted when the transformation has started
     */
    public static int getTimeSinceTransformation(EntityPlayerMP player) {
        WerewolfHelper.validateIsWerewolf(player);
        return TransformationHelper.getTransformationData(player).getInteger("timeSinceTransformation");
    }

    /**
     * Sets a werewolf player's time since the last transformation. No packet is
     * being sent to the client
     */
    public static void setTimeSinceTransformation(EntityPlayerMP player, int time) {
        WerewolfHelper.validateIsWerewolf(player);
        TransformationHelper.getTransformationData(player).setInteger("timeSinceTransformation", time);
    }

    public static int getSoundTicks(EntityPlayerMP player) {
        WerewolfHelper.validateIsWerewolf(player);
        return TransformationHelper.getTransformationData(player).getInteger("soundTicks");
    }

    /**
     * Sets a werewolf player's sound ticks (used for the passive werewolf sounds).
     * No packet is being sent to the client
     */
    public static void setSoundTicks(EntityPlayerMP player, int ticks) {
        WerewolfHelper.validateIsWerewolf(player);
        TransformationHelper.getTransformationData(player).setInteger("soundTicks", ticks);
    }

    public static boolean wasLastAttackBite(EntityLivingBase entity) {
        WerewolfHelper.validateIsWerewolf(entity);
        return TransformationHelper.getTransformationData(entity).getBoolean("lastAttackBite");
    }

    /**
     * Sets the werewolf's last attack to bite. No packet is being sent to the
     * client
     */
    public static void setLastAttackBite(EntityLivingBase entity, boolean wasBite) {
        WerewolfHelper.validateIsWerewolf(entity);
        TransformationHelper.getTransformationData(entity).setBoolean("lastAttackBite", wasBite);
    }

    public static long getBiteTicks(EntityLivingBase entity) {
        WerewolfHelper.validateIsWerewolf(entity);
        return TransformationHelper.getTransformationData(entity).getLong("biteTicks");
    }

    /**
     * Sets the world tick at which the given entity used bite the last time. 0 means that it hasn't used it yet.
     * No packet is being sent to the client
     */
    public static void setBiteTicks(EntityLivingBase entity, long biteTicks) {
        WerewolfHelper.validateIsWerewolf(entity);
        TransformationHelper.getTransformationData(entity).setLong("biteTicks", biteTicks);
    }

    /**
     * Called in
     * {@link TransformationEventHandler#onEntityTick(net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent)}
     */
    public static void onWerewolfTick(EntityCreature werewolf) {
        if (!WerewolfHelper.isTransformed(werewolf)) {
            WerewolfHelper.transformWerewolfWhenPossible(werewolf, WerewolfTransformingEvent.WerewolfTransformingReason.FULL_MOON);
        }
    }

    public static void transformWerewolfWhenPossible(@Nonnull EntityCreature werewolf,
                                                     WerewolfTransformingEvent.WerewolfTransformingReason reason) {
        Validate.notNull(werewolf, "The argument werewolf is not allowed to be null");
        WerewolfHelper.validateIsWerewolf(werewolf);
        if (WerewolfHelper.isTransformed(werewolf)) {
            throw new IllegalArgumentException(
                    "The given entity " + werewolf + " is not allowed to be transformed");
        }
        if (!MinecraftForge.EVENT_BUS.post(new WerewolfTransformingEvent(werewolf, false, reason))) {
            EntityLivingBase returned = WerewolfHelper.toWerewolfWhenNight(werewolf);
            if (returned != null) {
                World world = returned.world;
                returned.setPosition(werewolf.posX, werewolf.posY, werewolf.posZ);
                returned.setHealth(returned.getMaxHealth() / (werewolf.getMaxHealth() / werewolf.getHealth()));
                world.removeEntity(werewolf);
                world.spawnEntity(returned);
                returned.setPositionAndUpdate(werewolf.posX, werewolf.posY, werewolf.posZ);
            }
        }
    }

    public static void validateIsWerewolf(EntityLivingBase entity) {
        TransformationHelper.getTransformation(entity).validateEquals(Transformation.WEREWOLF);
    }

    /**
     * If the given entity is a werewolf, all the wolfsbane effects will be
     * applied and a howl sound will be played
     */
    public static boolean applyWolfsbaneEffects(EntityLivingBase entity, boolean playSound, boolean affectWolves) {
        if ((TransformationHelper.getTransformation(entity) == Transformation.WEREWOLF)
                || (affectWolves && (entity instanceof EntityWolf))) {
            if (!entity.world.isRemote) {
                if (playSound) {
                    WerewolfHelper.playHowlSound(entity);
                }
                entity.addPotionEffect(new PotionEffect(WerewolfHelper.isTransformed(entity) ? MobEffects.WITHER : MobEffects.POISON, 200));
            }
            return true;
        } else {
            return false;
        }
    }

    public static boolean cureLycanthropy(EntityLivingBase toCure, boolean alsoCureAlreadyTransformed) {
        IInfectOnNextMoon ionm = WerewolfHelper.getIInfectOnNextMoon(toCure).orElse(null);
        if ((ionm != null) && ionm.isInfected()) {
            ionm.setInfectionStatus(IInfectOnNextMoon.InfectionStatus.NOT_INFECTED);
            ionm.setInfectionTick(-1);
            ionm.setInfectionTransformation(Transformation.HUMAN);
            return true;
        } else if (alsoCureAlreadyTransformed && (TransformationHelper.getTransformation(toCure) == Transformation.WEREWOLF)) {
            if(toCure instanceof EntityWerewolf) {
                toCure = ((EntityWerewolf) toCure).forceTransformBack(WerewolfTransformingEvent.WerewolfTransformingReason.WOLFSBANE);
                if (toCure == null) {
                    return false;
                }
            }
            TransformationHelper.changeTransformation(toCure, Transformation.HUMAN, TransformationEvent.TransformationEventReason.WOLFSBANE);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns how many stages there are until a werewolf
     * player has transformed into a werewolf.
     */
    public static int getAmountOfTransformationStages() {
        return 6;
    }

    /**
     * Adds the effects a werewolf should get when they're transformed to the given player. The applied effects are the
     * werewolf sounds, night vision, speed, jump boost and hunger. It is NOT checked if the given player is a werewolf.
     */
    public static void addTransformationEffects(EntityPlayerMP werewolf, ITransformationPlayer cap) {
        ITransformationPlayer tp = TransformationHelper.getITransformationPlayer(werewolf);
        werewolf.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 401, 0, false, false));
        werewolf.addPotionEffect(new PotionEffect(MobEffects.SPEED, 101, tp.getSkillLevel(SkillInit.SPEED_0) + 1, false, false));
        werewolf.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 101, tp.getSkillLevel(SkillInit.JUMP_0) + 1, false, false));
        werewolf.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 101, 2, false, false));
        Random random = werewolf.getRNG();
        int soundTicksBefore = WerewolfHelper.getSoundTicks(werewolf);
        WerewolfHelper.setSoundTicks(werewolf, soundTicksBefore + 1);
        if (random.nextInt(13) < soundTicksBefore) {
            WerewolfHelper.setSoundTicks(werewolf, -80);
            werewolf.world.playSound(null, werewolf.posX, werewolf.posY, werewolf.posZ, ENTITY_WOLF_GROWL,
                    werewolf.getSoundCategory(), 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F);
        }
    }

    /**
     * Advances the werewolf transformation via changing the transformation stage and applying some effects. Should be
     * called until the werewolf is transformed to make sure the transformation is fully done. Always checks if the
     * given player is a werewolf.
     */
    public static void advanceWerewolfTransformation(EntityPlayerMP werewolf, ITransformationPlayer cap,
                                                     WerewolfTransformingEvent.WerewolfTransformingReason reason) {
        if (WerewolfHelper.canWerewolfTransform(werewolf)) {
            if (MinecraftForge.EVENT_BUS.post(new WerewolfTransformingEvent(werewolf, false, reason)))
                return;
            if (WerewolfHelper.getTransformationStage(werewolf) <= 0) {
                WerewolfHelper.setTimeSinceTransformation(werewolf, werewolf.ticksExisted);
                WerewolfHelper.onStageChanged(werewolf, 1, reason.getActualTransformingReason());
                return;
            }

            // every five seconds (20 * 5 = 100) one stage up
            int nextStage = MathHelper
                    .floor(((werewolf.ticksExisted - WerewolfHelper.getTimeSinceTransformation(werewolf))) / 100.0D);

            if (nextStage > 6 || nextStage < 0) {
                WerewolfHelper.setTimeSinceTransformation(werewolf, -1);
                WerewolfHelper.setTransformationStage(werewolf, 0);
                Main.getLogger().warn(
                        "Has the ingame time been changed or has the player left the world? Player " + werewolf.getName()
                                + "'s transformation stage (" + nextStage + ") is invalid");
                PacketHandler.sendTransformationMessage(werewolf);
                return;
            }

            if (nextStage > WerewolfHelper.getTransformationStage(werewolf)) {
                WerewolfHelper.onStageChanged(werewolf, nextStage, reason.getActualTransformingReason());
            }
        }
    }

    /**
     * Called when transformation stage changes
     */
    private static void onStageChanged(EntityPlayerMP player, int nextStage, WerewolfTransformingEvent.WerewolfTransformingReason reasonForEvent) {
        WerewolfHelper.setTransformationStage(player, nextStage);
        int transformationStage = WerewolfHelper.getTransformationStage(player);

        // send chat messages
        if (transformationStage != 0) {
            ITextComponent message = new TextComponentTranslation(
                    "transformations.huntersdream:werewolf.transformingInto."
                            + WerewolfHelper.getTransformationStage(player));
            message.getStyle().setItalic(Boolean.TRUE).setColor(TextFormatting.RED);
            player.sendStatusMessage(message, true);
            player.getServerWorld().getEntityTracker().sendToTrackingAndSelf(player, new SPacketParticles(
                    ParticleCommonInit.BLOOD_PARTICLE, false, (float) player.posX, (float) player.posY,
                    (float) player.posZ, 0.0F, 0.0F, 0.0F, 0.0F, 30));
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
                player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 110, 0));
                break;
            case 5:
                player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 400, 0));
                break;
            case 6:
                WerewolfHelper.playHowlSound(player);
                WerewolfHelper.setTimeSinceTransformation(player, -1);
                WerewolfHelper.setTransformationStage(player, 0);
                WerewolfHelper.transformPlayer(player, true, reasonForEvent);
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

    /**
     * Should be called when the given player is NOT transformed but it should be ensured that their transformation stage
     * and time are reset. This method always checks if the given player is a werewolf but does NOT check if they're
     * untransformed.
     */
    public static void resetTransformationStageWhenNeeded(EntityPlayerMP werewolf, ITransformationPlayer cap) {
        // test if player has no transformation stage
        if ((WerewolfHelper.getTransformationStage(werewolf) != 0)) {
            WerewolfHelper.setTimeSinceTransformation(werewolf, -1);
            WerewolfHelper.setTransformationStage(werewolf, 0);
            Main.getLogger().warn(
                    "Has the ingame time been changed or has the player left the world? Player " + werewolf.getName()
                            + "'s transformation stage wasn't 0 although the player wasn't transformed");
            PacketHandler.sendTransformationMessage(werewolf);
        }
    }

    /**
     * Does the opposite of {@link #advanceWerewolfTransformation(EntityPlayerMP, ITransformationPlayer, WerewolfTransformingEvent.WerewolfTransformingReason)}
     * but does not to but called multiple times but only once. This method always checks if the player is a werewolf and
     * can be canceled by {@link WerewolfTransformingEvent}. The effects that are applied to the player are hunger,
     * weakness, slowness, blindness and night vision (for a better blindness effect). In addition, a message is also sent.
     * Does NOT execute when the werewolf is currently transforming.
     */
    public static void transformWerewolfBack(EntityPlayerMP werewolf, ITransformationPlayer cap, WerewolfTransformingEvent.WerewolfTransformingReason reason) {
        if ((WerewolfHelper.getTransformationStage(werewolf) <= 0) && WerewolfHelper.transformPlayer(werewolf,
                false, reason)) {
            ITextComponent message = new TextComponentTranslation(
                    "transformations.huntersdream:werewolf.transformingBack.0");
            message.getStyle().setItalic(Boolean.TRUE).setColor(TextFormatting.BLUE);
            werewolf.sendStatusMessage(message, true);
            PacketHandler.sendTransformationMessage(werewolf);
            werewolf.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 1200, 2));
            werewolf.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 1200, 1));
            werewolf.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 300, 4));
            werewolf.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 300, 0));
            // night vision for better blindness effect
            werewolf.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 300, 0, false, false));
        }
    }

    /**
     * Returns true if the given player's bite cooldown has ended (cooldown is 5 seconds/100 ticks). Does not check if
     * the player can actually use bite again. For a method that does this, please look at {@link #canPlayerBiteAgain(EntityPlayer)}
     */
    public static boolean hasBiteCooldownEnded(EntityPlayer player) {
        long biteTicks = WerewolfHelper.getBiteTicks(player);
        // 5 seconds (= 100 ticks) cooldown
        // return true if either the cooldown has ended or if the player hasn't used bite yet
        return ((player.world.getTotalWorldTime() - biteTicks) > 100) || (biteTicks == 0);
    }

    /**
     * Returns true if the given player can bite again, meaning that the cooldown has ended and the player is transformed.
     */
    public static boolean canPlayerBiteAgain(EntityPlayer player) {
        return WerewolfHelper.hasBiteCooldownEnded(player) && WerewolfHelper.isTransformed(player);
    }

    public static void playHowlSound(EntityLivingBase entity) {
        entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_WOLF_HOWL,
                entity.getSoundCategory(), 100.0F, (entity.getRNG().nextFloat() - entity.getRNG().nextFloat()) * 0.2F);
    }

    // Drop all items from player, with extended despawn timer
    public static void dropAllItems(EntityPlayer player) {

        net.minecraft.entity.player.InventoryPlayer inv = player.inventory;

        //main
        for (int i = 0; i < inv.mainInventory.size(); ++i)  {
            ItemStack itemstack = inv.mainInventory.get(i);

            if (!itemstack.isEmpty()) {
                EntityItem ei = player.dropItem(itemstack, true, false);
                inv.mainInventory.set(i, ItemStack.EMPTY);
                ei.lifespan = LONG_DESPAWN;
            }
        }
        // armor
        for (int i = 0; i < inv.armorInventory.size(); ++i) {
            ItemStack itemstack = inv.armorInventory.get(i);

            if (!itemstack.isEmpty()) {
                EntityItem ei = player.dropItem(itemstack, true, false);
                inv.armorInventory.set(i, ItemStack.EMPTY);
                ei.lifespan = LONG_DESPAWN;
            }
        }
        // offhand
        for (int i = 0; i < inv.offHandInventory.size(); ++i) {
            ItemStack itemstack = inv.offHandInventory.get(i);
            if (!itemstack.isEmpty()) {
                EntityItem ei = player.dropItem(itemstack, true, false);
                inv.offHandInventory.set(i, ItemStack.EMPTY);
                ei.lifespan = LONG_DESPAWN;
            }
        }
    }

    private void extendItemDespawn(EntityItem item) {
        net.minecraft.nbt.NBTTagCompound nbt = new net.minecraft.nbt.NBTTagCompound();
        item.writeEntityToNBT(nbt);
        int age = nbt.getShort("Age");

        item.lifespan = age + LONG_DESPAWN;
    }
}