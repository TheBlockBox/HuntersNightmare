package theblockbox.huntersdream.util.helpers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import org.apache.commons.lang3.Validate;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.api.event.ExtraDataEvent;
import theblockbox.huntersdream.api.event.TransformationEvent;
import theblockbox.huntersdream.api.event.WerewolfTransformingEvent;
import theblockbox.huntersdream.entity.EntityWerewolf;
import theblockbox.huntersdream.init.CapabilitiesInit;
import theblockbox.huntersdream.init.SkillInit;
import theblockbox.huntersdream.init.SoundInit;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.exceptions.WrongSideException;
import theblockbox.huntersdream.util.exceptions.WrongTransformationException;
import theblockbox.huntersdream.util.handlers.PacketHandler;
import theblockbox.huntersdream.util.handlers.TransformationEventHandler;
import theblockbox.huntersdream.util.interfaces.IInfectOnNextMoon;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformation;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

import javax.annotation.Nonnull;
import java.util.Optional;

public class WerewolfHelper {
    public static final Capability<IInfectOnNextMoon> CAPABILITY_INFECT_ON_NEXT_MOON = CapabilitiesInit.CAPABILITY_INFECT_ON_NEXT_MOON;
    public static final DamageSource WEREWOLF_TRANSFORMATION_DAMAGE = new DamageSource(
            "huntersdream:werewolfTransformationDamage");
    public static final float WEREWOLF_HEIGHT = 2.4F;

    /**
     * Returns true when a werewolf can transform in this world
     */
    public static boolean isWerewolfTime(World world) {
        if (world.isRemote)
            throw new WrongSideException("Can only test if it is werewolf time on server side", world);
        return (world.getCurrentMoonPhaseFactor() == 1.0F) && !world.isDaytime();
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
                    return TransformationHelper.getITransformationPlayer((EntityPlayer) entity).getSkillLevel(SkillInit.UNARMED_0) + 7.0F;
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
     * /** Returns how much damage the given entity should get if the entity and the
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
     * @return Returns the chance of infection (100% = always, 0% = never)
     */
    public static int getInfectionPercentage(EntityLivingBase entity) {
        if (WerewolfHelper.canInfect(entity)) {
            // TODO: Return different infection percantage if entity is player
            return 25;
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
     * Transforms a werewolf. No packet is being sent to the client.
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
     * to use, Transformations = transformation that the entity should have) When
     * the werewolf transforms back, this constructor will be called and World will
     * be {@link EntityWerewolf#getEntityWorld()}, int will be
     * {@link EntityWerewolf#getTextureIndex()} and Transformations will be
     * {@link Transformation#WEREWOLF}
     *
     * @param entity The entity to be transformed
     */
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
                                        "Entity does not implement interface \"ITransformation\" or has TransformationCreature capability"))
                                .getTextureIndex(), "$bycap" + entity.getClass().getName(),
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

    public static int getTransformationStage(EntityPlayerMP player) {
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

    // add potion effects to werewolves
    // TODO: Make that it practically would also work when it's day, as long as the
    // werewolf is transformed
    public static void applyLevelBuffs(EntityPlayerMP werewolf) {
        int duration = 101;
        ITransformationPlayer tp = TransformationHelper.getITransformationPlayer(werewolf);
        werewolf.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 401, 0, false, false));
        werewolf.addPotionEffect(new PotionEffect(MobEffects.SPEED, duration, tp.getSkillLevel(SkillInit.SPEED_0) + 1, false, false));
        werewolf.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, duration, tp.getSkillLevel(SkillInit.JUMP_0) + 1, false, false));
        werewolf.addPotionEffect(new PotionEffect(MobEffects.HUNGER, duration, 2, false, false));
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

    public static float getWerewolfHeight(Entity werewolf) {
        // TODO: Change eye height if it's possible to change it when sneaking
        return WerewolfHelper.shouldUseSneakingModel(werewolf) ? 2.0F : WerewolfHelper.WEREWOLF_HEIGHT;
    }

    public static float getWerewolfEyeHeight(Entity werewolf) {
        return WerewolfHelper.shouldUseSneakingModel(werewolf) ? 1.85F : 2.1F;
    }

    // TODO: Change this?
    public static boolean shouldUseSneakingModel(Entity werewolf) {
        // if player was sneaking before, don't use sneaking model
        return werewolf.isSneaking() || !GeneralHelper.canEntityExpandHeight(werewolf, WerewolfHelper.WEREWOLF_HEIGHT);
    }

    public static void validateIsWerewolf(EntityLivingBase entity) {
        TransformationHelper.getTransformation(entity).validateEquals(Transformation.WEREWOLF);
    }

    /**
     * Returns a werewolf texture index depending on where the werewolf currently
     * is.
     */
    public static int getTextureIndexForWerewolf(EntityLivingBase werewolf) {
        // 0 is brown, 1 is black, 2 is white and 3 is yellow
        BlockPos pos = werewolf.getPosition();
        World world = werewolf.world;
        if (world.isBlockLoaded(pos)) {
            Biome biome = world.getBiome(pos);
            if (biome.getTempCategory() == Biome.TempCategory.WARM)
                return 3;
                // if the biome is snowy or there can be snow where the werewolf is, use the
                // white skin (also works on mountains with snow)
            else if (biome.isSnowyBiome() || (biome.getTemperature(werewolf.getPosition()) < 0.15F))
                return 2;
                // TODO: Rethink when the werewolf skin should be brown
            else
                return werewolf.getRNG().nextBoolean() ? 0 : 1;
        }
        return 0;
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
}
