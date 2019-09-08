package theblockbox.huntersdream.api.helpers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import org.apache.commons.lang3.Validate;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.api.event.CanLivingBeInfectedEvent;
import theblockbox.huntersdream.api.event.IsLivingInfectedEvent;
import theblockbox.huntersdream.api.event.TransformationEvent;
import theblockbox.huntersdream.api.init.CapabilitiesInit;
import theblockbox.huntersdream.api.init.ItemInit;
import theblockbox.huntersdream.api.interfaces.IGun;
import theblockbox.huntersdream.api.interfaces.IInfectInTicks;
import theblockbox.huntersdream.api.interfaces.IInfectOnNextMoon;
import theblockbox.huntersdream.api.interfaces.transformation.ITransformation;
import theblockbox.huntersdream.api.interfaces.transformation.ITransformationCreature;
import theblockbox.huntersdream.api.interfaces.transformation.ITransformationPlayer;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.exceptions.WrongSideException;
import theblockbox.huntersdream.util.handlers.ConfigHandler;
import theblockbox.huntersdream.util.handlers.PacketHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Optional;

public class TransformationHelper {
    public static final Capability<ITransformationPlayer> CAPABILITY_TRANSFORMATION_PLAYER = CapabilitiesInit.CAPABILITY_TRANSFORMATION_PLAYER;
    public static final Capability<ITransformationCreature> CAPABILITY_TRANSFORMATION_CREATURE = CapabilitiesInit.CAPABILITY_TRANSFORMATION_CREATURE;
    public static final Capability<IInfectInTicks> CAPABILITY_INFECT_IN_TICKS = CapabilitiesInit.CAPABILITY_INFECT_IN_TICKS;
    public static final String THORNS_DAMAGE_NAME = Reference.MODID + ":effectiveAgainstTransformationThorns";
    /**
     * Special damage source for things that are effective against specific
     * transformations. If used, the attacked entity's protection won't work
     */
    public static final DamageSource EFFECTIVE_AGAINST_TRANSFORMATION = new DamageSource(
            Reference.MODID + "effectiveAgainstTransformation");

    /**
     * Returns the transformation capability of the given player (just a short-cut
     * method)
     */
    public static ITransformationPlayer getITransformationPlayer(@Nonnull EntityPlayer player) {
        Validate.notNull(player);
        return player.getCapability(TransformationHelper.CAPABILITY_TRANSFORMATION_PLAYER, null);
    }

    /**
     * Changes the player's transformation also resets xp and transformed and sends
     * the data to the client (this method is only to be called server side!)
     */
    private static void changeTransformation(EntityPlayerMP player, Transformation transformation) {
        transformation.validateIsTransformation();
        ITransformationPlayer cap = TransformationHelper.getITransformationPlayer(player);
        // reset skills
        cap.setSkills(Collections.emptySet());
        // reset active skill
        cap.setActiveSkill(null);
        cap.setTransformation(transformation);
        cap.setTextureIndex(player);

        NBTTagCompound transformationData = new NBTTagCompound();
        transformationData.setString("transformation", transformation.toString());
        cap.setTransformationData(transformationData);

        if (ConfigHandler.common.showPacketMessages)
            Main.getLogger()
                    .info("Transformation of player " + player.getName() + " changed to " + transformation);
        PacketHandler.sendTransformationMessage(player); // sync data with client
    }

    public static void changeTransformation(@Nonnull EntityLivingBase entity, @Nonnull Transformation transformation,
                                            TransformationEvent.TransformationEventReason reason) {
        if (entity != null && transformation.isTransformation()) {
            World world = entity.world;
            if (world.isRemote) {
                throw new WrongSideException("Can only change transformation on server side", entity.world);
            } else {
                if (!MinecraftForge.EVENT_BUS.post(new TransformationEvent(entity, transformation, reason))) {
                    if (entity instanceof EntityPlayerMP) {
                        TransformationHelper.changeTransformation((EntityPlayerMP) entity, transformation);
                    } else if (entity instanceof EntityCreature) {
                        ITransformation it = TransformationHelper.getITransformation(entity).get();
                        it.setTransformation(transformation);

                        NBTTagCompound transformationData = new NBTTagCompound();
                        transformationData.setString("transformation", transformation.toString());
                        it.setTransformationData(transformationData);

                        TransformationHelper.getITransformationCreature((EntityCreature) entity).ifPresent(t -> t.setTextureIndex(entity));
                    } else {
                        throw new IllegalArgumentException("Can't transform entity " + entity
                                + " (it is neither a player nor an instance of EntityCreature)");
                    }
                }
            }
        } else {
            throw new NullPointerException("A null argument was passed. Entity null: " + (entity == null)
                    + " Transformation null: " + (transformation == null));
        }
    }

    public static void changeTransformationWhenPossible(EntityCreature entity, Transformation transformation,
                                                        TransformationEvent.TransformationEventReason reason) {
        Validate.notNull(entity, "The entity isn't allowed to be null");
        Validate.notNull(transformation, "The transformation isn't allowed to be null");

        Optional<ITransformationCreature> tc = TransformationHelper.getITransformationCreature(entity);
        boolean flag = TransformationHelper.canChangeTransformation(entity)
                && (!tc.isPresent() || tc.get().notImmuneToTransformation(transformation));
        if (flag) {
            TransformationHelper.changeTransformation(entity, transformation, reason);
        }
    }

    /**
     * Returns true when the given entity can change transformation without rituals
     * (e.g. by werewolf infection)
     */
    public static boolean canChangeTransformation(EntityLivingBase entity) {
        return TransformationHelper.canChangeTransformationOnInfection(entity) && !TransformationHelper.isInfected(entity);
    }

    /**
     * Returns true if the given entity can change it's transformation while not
     * accounting for infection
     */
    public static boolean canChangeTransformationOnInfection(EntityLivingBase entity) {
        Optional<ITransformation> transformation = TransformationHelper.getITransformation(entity);
        return transformation.isPresent() && transformation.get().isTransformationChangeable();
    }

    /**
     * Returns the entity's transformation. If the entity has none or the entity is
     * null, {@link Transformation#NONE} is returned
     */
    @Nonnull
    public static Transformation getTransformation(EntityLivingBase entity) {
        return TransformationHelper.getITransformation(entity).map(ITransformation::getTransformation).orElse(Transformation.NONE);
    }

    /**
     * This is a way to get the {@link ITransformation} interface from every entity
     * that has it in some form (capability or implemented)
     */
    public static Optional<ITransformation> getITransformation(EntityLivingBase entity) {
        if (entity instanceof EntityPlayer) {
            return Optional.ofNullable(TransformationHelper.getITransformationPlayer((EntityPlayer) entity));
        } else if (entity instanceof ITransformation) {
            return Optional.of((ITransformation) entity);
        } else if (entity instanceof EntityCreature) {
            return TransformationHelper.getITransformationCreature((EntityCreature) entity).map(t -> t);
        } else {
            return Optional.empty();
        }
    }

    public static Optional<ITransformationCreature> getITransformationCreature(@Nonnull EntityCreature entity) {
        Validate.notNull(entity);
        return Optional.ofNullable((entity instanceof ITransformationCreature) ? (ITransformationCreature) entity
                : entity.getCapability(TransformationHelper.CAPABILITY_TRANSFORMATION_CREATURE, null));
    }

    /**
     * Returns true when the given entity can be infected with the given
     * infection/transformation
     */
    public static boolean canBeInfectedWith(Transformation infection, EntityLivingBase entity) {
        Validate.notNull(infection, "The transformation isn't allowed to be null");
        if (TransformationHelper.canChangeTransformation(entity)) {
            // can't infect entity that already has the transformation
            if (TransformationHelper.getTransformation(entity) == infection) {
                return false;
            } else if (WerewolfHelper.getIInfectOnNextMoon(entity).isPresent() && infection == Transformation.WEREWOLF) {
                return true;
            } else if (TransformationHelper.getIInfectInTicks(entity).isPresent()) {
                if (entity instanceof EntityCreature) {
                    Optional<ITransformationCreature> otc = TransformationHelper.getITransformationCreature((EntityCreature) entity);
                    if (otc.isPresent()) {
                        return otc.get().notImmuneToTransformation(infection)
                                || MinecraftForge.EVENT_BUS.post(new CanLivingBeInfectedEvent(entity, infection));
                    }
                }
                return true;
            }
        }
        return MinecraftForge.EVENT_BUS.post(new CanLivingBeInfectedEvent(entity, infection));
    }

    /**
     * Returns true when the given entity can be infected with the given
     * infection/transformation without accounting for current infections
     */
    public static boolean onInfectionCanBeInfectedWith(Transformation infection, EntityCreature entity) {
        Validate.notNull(infection, "The transformation isn't allowed to be null");
        if (TransformationHelper.canChangeTransformationOnInfection(entity)) {
            Optional<ITransformationCreature> tc = TransformationHelper.getITransformationCreature(entity);
            return !tc.isPresent() || tc.get().notImmuneToTransformation(infection);
        } else {
            return false;
        }
    }

    /**
     * Returns the {@link IInfectInTicks} capability of the given entity
     */
    public static Optional<IInfectInTicks> getIInfectInTicks(@Nonnull EntityLivingBase entity) {
        Validate.notNull(entity);
        return Optional.ofNullable(entity.getCapability(TransformationHelper.CAPABILITY_INFECT_IN_TICKS, null));
    }

    /**
     * Infects the given entity in the given amount of ticks with the given
     * infection/transformation
     */
    public static void infectIn(int ticksUntilInfection, EntityLivingBase entityToBeInfected, Transformation infectTo) {
        Validate.notNull(infectTo, "The transformation isn't allowed to be null");
        IInfectInTicks iit = TransformationHelper.getIInfectInTicks(entityToBeInfected).orElseThrow(() -> new IllegalArgumentException(
                "The given entity does not have the capability IInfectInTicks/infectinticks"));
        iit.setTime(ticksUntilInfection);
        iit.setCurrentlyInfected(true);
        iit.setInfectionTransformation(infectTo);
    }

    /**
     * Returns true if the given entity is infected
     */
    public static boolean isInfected(EntityLivingBase entity) {
        Optional<IInfectInTicks> iit = TransformationHelper.getIInfectInTicks(entity);
        Optional<IInfectOnNextMoon> ionm = WerewolfHelper.getIInfectOnNextMoon(entity);
        if (((iit.isPresent()) && iit.get().currentlyInfected())
                || (ionm.isPresent() && (ionm.get().getInfectionStatus() != IInfectOnNextMoon.InfectionStatus.NOT_INFECTED))) {
            return true;
        }
        return MinecraftForge.EVENT_BUS.post(new IsLivingInfectedEvent(entity));
    }

    public static NBTTagCompound getTransformationData(EntityLivingBase entity) {
        Transformation transformation = TransformationHelper.getTransformation(entity);
        transformation.validateIsTransformation();
        ITransformation iTransformation = TransformationHelper.getITransformation(entity).get();
        NBTTagCompound compound = iTransformation.getTransformationData();

        String transformationValue = compound.getString("transformation");
        String strTransformation = transformation.toString();

        // support for old versions
        if (transformationValue.isEmpty()) {
            Main.getLogger().warn(
                    "It seems like Hunter's Dream has been updated... (If this was not the case, please report this!)\n"
                            + "Setting transformation data for entity \"" + entity + "\" to a valid one");
            compound = new NBTTagCompound();
            compound.setString("transformation", strTransformation);
            compound.setBoolean("transformed", true);

            iTransformation.setTransformationData(compound);
            if (entity instanceof EntityPlayerMP) {
                PacketHandler.sendTransformationMessage((EntityPlayerMP) entity);
            }
            return compound;
        }

        Validate.isTrue(transformationValue.equals(strTransformation),
                "The NBTTagCompound should have the key \"transformation\" with the value \"%s\" but it was \"%s\"",
                strTransformation, transformationValue);
        return compound;
    }

    public static DamageSource causeEffectivenessThornsDamage(Entity source) {
        return new EntityDamageSource(TransformationHelper.THORNS_DAMAGE_NAME, source).setIsThornsDamage().setMagicDamage();
    }

    public static Item getHunterWeaponForEntity(@Nullable EntityLivingBase attacked, boolean meleeWeapon) {
        boolean isTransformedWerewolf = WerewolfHelper.isTransformed(attacked);
        if (meleeWeapon) {
            return isTransformedWerewolf ? ItemInit.SILVER_SWORD : Items.IRON_SWORD;
        } else {
            return ItemInit.FLINTLOCK_PISTOL;
        }
    }

    public static ItemStack getHunterAmmunitionForEntity(@Nullable EntityLivingBase attacked, IGun gun) {
        return new ItemStack(WerewolfHelper.isTransformed(attacked) ? ItemInit.SILVER_MUSKET_BALL : ItemInit.MUSKET_BALL);
    }
}
