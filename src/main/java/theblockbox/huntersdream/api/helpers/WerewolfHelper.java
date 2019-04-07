package theblockbox.huntersdream.api.helpers;

import com.google.common.base.Preconditions;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
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
import theblockbox.huntersdream.entity.EntityWerewolf;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.exceptions.UnexpectedBehaviorException;
import theblockbox.huntersdream.util.exceptions.WrongSideException;
import theblockbox.huntersdream.util.exceptions.WrongTransformationException;
import theblockbox.huntersdream.util.handlers.PacketHandler;
import theblockbox.huntersdream.util.handlers.TransformationEventHandler;
import theblockbox.huntersdream.util.interfaces.IInfectOnNextMoon;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformation;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;

import static net.minecraft.init.SoundEvents.ENTITY_WOLF_GROWL;

public class WerewolfHelper {
    public static final Capability<IInfectOnNextMoon> CAPABILITY_INFECT_ON_NEXT_MOON = CapabilitiesInit.CAPABILITY_INFECT_ON_NEXT_MOON;
    public static final DamageSource WEREWOLF_TRANSFORMATION_DAMAGE = new DamageSource("huntersdream:werewolfTransformationDamage");

    /**
     * Returns true when a werewolf can transform in this world
     */
    public static boolean isWerewolfTime(World world) {
        if (world.isRemote)
            throw new WrongSideException("Can only test if it is werewolf time on server side", world);
        long worldTime = world.getWorldTime();
        // 23459 and 12540 are the exact times between which it is day (according to World#isDaytime)
        return WerewolfHelper.isFullmoon(world) && ((worldTime > 12540L) && (worldTime < 23459L));
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
            if (transformed) {
                // default: 6 lvl 0: 7 lvl 1: 8 lvl 2: 9
                if (entity.getHeldItemMainhand().isEmpty()) {
                    ITransformationPlayer transformation = TransformationHelper.getITransformationPlayer((EntityPlayer) entity);
                    return transformation.getSkillLevel(SkillInit.UNARMED_0) + ((WerewolfHelper.wasLastAttackBite(entity)
                            && transformation.getSkillLevel(SkillInit.BITE_0) > 0) ? 13.0F : 7.0F);
                } else {
                    return initialDamage;
                }
            } else {
                // "Their [untransformed] melee damage is normal damage +2"
                return initialDamage + 2;
            }
        } else {
            if (transformed) {
                // "Their attack is 4 hearts per slash and 5 hearts per bite"
                return WerewolfHelper.wasLastAttackBite(entity) ? 5.0F : 4.0F;
            } else {
                // no damage change for untransformed werewolves
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
            if (entityToBeInfected instanceof EntityPlayer) {
                entityToBeInfected.sendMessage(
                        new TextComponentTranslation("transformations." + Reference.MODID + ".infected.werewolf"));
            }
            entityToBeInfected.addPotionEffect(new PotionEffect(MobEffects.POISON, 100, 0, false, true));
            entityToBeInfected.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 100, 1, false, true));
            entityToBeInfected.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 100, 0, false, false));
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
        NBTTagCompound compound = TransformationHelper.getTransformationData(player);
        compound.setInteger("transformationStage", stage);
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
        NBTTagCompound compound = TransformationHelper.getTransformationData(player);
        compound.setInteger("timeSinceTransformation", time);
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
        NBTTagCompound compound = TransformationHelper.getTransformationData(player);
        compound.setInteger("soundTicks", ticks);
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
        NBTTagCompound compound = TransformationHelper.getTransformationData(entity);
        compound.setBoolean("lastAttackBite", wasBite);
    }

    public static long getBiteTicks(EntityLivingBase entity) {
        WerewolfHelper.validateIsWerewolf(entity);
        return TransformationHelper.getTransformationData(entity).getLong("biteTicks");
    }

    /**
     * Sets the the world tick at which the given entity used bite the last time. 0 means that it hasn't used it yet.
     * No packet is being sent to the client
     */
    public static void setBiteTicks(EntityLivingBase entity, long biteTicks) {
        WerewolfHelper.validateIsWerewolf(entity);
        NBTTagCompound compound = TransformationHelper.getTransformationData(entity);
        compound.setLong("biteTicks", biteTicks);
    }

    /**
     * Returns the amount of ticks the given werewolf player has been transformed. If the returned value is 0, they
     * haven't wilfully transformed yet. If the value is positive, they are currently wilfully transformed and the long
     * is the tick (gotten from {@link World#getTotalWorldTime()}) at which they started to transform. If the value is
     * negative, they have already been transformed and the long is the tick at which they transformed back with a minus
     * sign in front of it.
     * <br>
     * Try to always prefer {@link #isPlayerWilfullyTransformed(EntityPlayer)} and {@link #hasPlayerReachedWilfulTransformationLimit(EntityPlayer)}
     * over this method since they make the code easier to understand and make bugs because of small things (like having
     * written the wrong number) less probable.
     */
    public static long getWilfulTransformationTicks(EntityPlayer player) {
        WerewolfHelper.validateIsWerewolf(player);
        return TransformationHelper.getTransformationData(player).getLong("wilfulTransformationTicks");
    }

    /**
     * Sets a werewolf player's wilful transformation ticks (used for the wilful transformation).
     * No packet is being sent to the client.
     */
    public static void setWilfulTransformationTicks(EntityPlayer player, long ticks) {
        WerewolfHelper.validateIsWerewolf(player);
        NBTTagCompound compound = TransformationHelper.getTransformationData(player);
        compound.setLong("wilfulTransformationTicks", ticks);
    }

    /**
     * Returns true when the given player can wilfully transform, but does NOT check if it is the correct moon phase.
     * (The reason it is not checked is that the method only works server side, while this method is supposed to be both
     * client and server side.)
     */
    public static boolean canPlayerWilfullyTransform(EntityPlayer werewolf) {
        ITransformationPlayer transformation = TransformationHelper.getITransformationPlayer(werewolf);
        if (transformation.getTransformation() == Transformation.WEREWOLF) {
            long ticks = WerewolfHelper.getWilfulTransformationTicks(werewolf);
            // 18000 ticks = 5 minutes cooldown
            // if the player hasn't wilfully transformed, they won't have any cooldown
            return (werewolf.getActiveItemStack().isEmpty()
                    && (transformation.getActiveSkill().orElse(null) == SkillInit.WILFUL_TRANSFORMATION)
                    && !WerewolfHelper.isTransformed(werewolf) && !WerewolfHelper.isPlayerWilfullyTransformed(werewolf)
                    && ((ticks == 0) || ((werewolf.world.getTotalWorldTime() + ticks) >= 18000)));
        } else {
            return false;
        }
    }

    public static boolean canPlayerWilfullyTransformBack(EntityPlayer werewolf) {
        ITransformationPlayer transformation = TransformationHelper.getITransformationPlayer(werewolf);
        return (transformation.getTransformation() == Transformation.WEREWOLF) && werewolf.getActiveItemStack().isEmpty()
                && (transformation.getActiveSkill().orElse(null) == SkillInit.WILFUL_TRANSFORMATION)
                && WerewolfHelper.isPlayerWilfullyTransformed(werewolf);
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
     * If the given entity is a werewolf, all the aconite effects will be
     * applied and a howl sound will be played
     */
    public static boolean applyAconiteEffects(EntityLivingBase entity, boolean playSound) {
        if (TransformationHelper.getTransformation(entity) == Transformation.WEREWOLF) {
            if (playSound) {
                entity.world.playSound(null, entity.posX, entity.posY, entity.posZ,
                        SoundInit.WEREWOLF_HOWLING, entity.getSoundCategory(), 100, 1);
            }
            entity.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 200));
            entity.addPotionEffect(new PotionEffect(MobEffects.POISON, 200));
            entity.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 200, 2));
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
            TransformationHelper.changeTransformation(toCure, Transformation.HUMAN, TransformationEvent.TransformationEventReason.ACONITE);
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
                player.world.playSound(null, player.getPosition(), SoundInit.WEREWOLF_HOWLING, SoundCategory.PLAYERS, 100,
                        1);
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
        if ((WerewolfHelper.getTransformationStage(werewolf) != 0) && !WerewolfHelper.isPlayerWilfullyTransformed(werewolf)) {
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
            // reset wilful transformation ticks if necessary
            long wilfulTransformationTicks = WerewolfHelper.getWilfulTransformationTicks(werewolf);
            if (wilfulTransformationTicks > 0) {
                WerewolfHelper.setWilfulTransformationTicks(werewolf, -wilfulTransformationTicks);
            }
        }
    }

    /**
     * Returns true if the given player is wilfully transformed. Does also return true when they're not actually transformed
     * yet. (Meaning they've started transforming but are still in a transformation stage.) This method always checks if
     * the given player is a werewolf.
     */
    public static boolean isPlayerWilfullyTransformed(EntityPlayer werewolf) {
        return WerewolfHelper.getWilfulTransformationTicks(werewolf) > 0;
    }

    /**
     * Returns true if the given player has reached their wilful transformation limit, meaning that they've been
     * wilfully transformed for more than 6000 ticks/5 minutes. This method always checks if the given player is a
     * werewolf and will throw an exception if the player is over the wilful transformation limit but isn't transformed.
     */
    public static boolean hasPlayerReachedWilfulTransformationLimit(EntityPlayer werewolf) {
        long ticks = WerewolfHelper.getWilfulTransformationTicks(werewolf);
        long totalWorldTime = werewolf.world.getTotalWorldTime();
        // 6000 ticks = 5 minutes limit for being transformed wilfully
        if (WerewolfHelper.isPlayerWilfullyTransformed(werewolf) && (totalWorldTime > (ticks + 6000))) {
            // check if the player is transformed to ensure nothing is going wrong
            Preconditions.checkArgument(WerewolfHelper.isTransformed(werewolf), "Player " + werewolf +
                    " was wilfully transformed and has reached their wilful transformation limit with " + ticks +
                    " ticks (current tick: " + totalWorldTime + ") but wasn't transformed. Please report this!");
            return true;
        } else {
            return false;
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
}
