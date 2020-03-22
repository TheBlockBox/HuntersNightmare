package theblockbox.huntersdream.util.handlers;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.api.HunterArmorEffect;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.api.event.CanLivingBeInfectedEvent;
import theblockbox.huntersdream.api.event.TransformationEvent;
import theblockbox.huntersdream.api.event.WerewolfTransformingEvent;
import theblockbox.huntersdream.api.event.effectiveness.ArmorEffectivenessEvent;
import theblockbox.huntersdream.api.event.effectiveness.EffectivenessEvent;
import theblockbox.huntersdream.api.event.effectiveness.EntityEffectivenessEvent;
import theblockbox.huntersdream.api.event.effectiveness.ItemEffectivenessEvent;
import theblockbox.huntersdream.api.helpers.ChanceHelper;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.api.helpers.TransformationHelper;
import theblockbox.huntersdream.api.helpers.WerewolfHelper;
import theblockbox.huntersdream.api.init.CapabilitiesInit;
import theblockbox.huntersdream.api.init.ItemInit;
import theblockbox.huntersdream.api.init.OreDictionaryInit;
import theblockbox.huntersdream.api.interfaces.IAmmunition;
import theblockbox.huntersdream.api.interfaces.IInfectInTicks;
import theblockbox.huntersdream.api.interfaces.IInfectOnNextMoon;
import theblockbox.huntersdream.api.interfaces.transformation.ITransformation;
import theblockbox.huntersdream.api.interfaces.transformation.ITransformationCreature;
import theblockbox.huntersdream.api.interfaces.transformation.ITransformationPlayer;
import theblockbox.huntersdream.entity.EntitySilverArrow;
import theblockbox.huntersdream.entity.EntityWerewolf;
import theblockbox.huntersdream.items.ItemHunterArmor;
import theblockbox.huntersdream.items.gun.ItemRifle;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.exceptions.UnexpectedBehaviorException;

import java.util.List;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class TransformationEventHandler {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {

            EntityPlayer player = event.player;
            ITransformationPlayer cap = TransformationHelper.getITransformationPlayer(player);
            boolean isWerewolf = cap.getTransformation() == Transformation.WEREWOLF;
            boolean isTransformed = WerewolfHelper.isTransformed(player);

            if (isTransformed) {
                if (!player.isCreative() && !player.isSpectator()) {
                    if (!player.inventory.isEmpty()) {
                        WerewolfHelper.dropAllItems(player);
                    }
                }
            }
            if (!player.world.isRemote) {
                boolean isWerewolfTime = WerewolfHelper.isWerewolfTime(player.world);

                if (player.ticksExisted % 20 == 0) {
                    EntityPlayerMP playerMP = (EntityPlayerMP) player;

                    for (EnumHand hand : GeneralHelper.HANDS) {
                        ItemStack stack = player.getHeldItem(hand);
                        if ((stack.getItem() instanceof ItemRifle) && ((ItemRifle) stack.getItem()).isAiming(player, stack)) {
                            player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 22, 1, false, false));
                            break;
                        }
                    }

                    if (isWerewolf) {
                        for (ItemStack stack : player.getEquipmentAndArmor()) {
                            // damage player if item is effective against transformation (also works when
                            // player is not transformed)
                            if (GeneralHelper.itemStackHasOreDicts(stack, OreDictionaryInit.SILVER_NAMES)) {
                                player.attackEntityFrom(TransformationHelper.EFFECTIVE_AGAINST_TRANSFORMATION, 1);
                                break;
                            }
                        }
                        if (isWerewolfTime && (!isTransformed)) {
                            playerMP.getServerWorld().getEntityTracker().sendToTrackingAndSelf(player, new SPacketParticles(
                                    EnumParticleTypes.SMOKE_LARGE, false, (float) player.posX - 0.1F,
                                    (float) player.posY - 0.2F, (float) player.posZ - 0.1F, 0.2F,
                                    1.4F, 0.2F, 0.0F, 50));
                        }
                    }

                    if (player.ticksExisted % 80 == 0) {
                        // call methods in WerewolfEventHandler
                        if (isWerewolf) {
                            if (isWerewolfTime) {
                                if (isTransformed) {
                                    WerewolfHelper.addTransformationEffects(playerMP, cap);
                                } else {
                                    WerewolfHelper.advanceWerewolfTransformation(playerMP, cap, WerewolfTransformingEvent.WerewolfTransformingReason.FULL_MOON_STARTING);
                                }
                            } else {
                                if (isTransformed) {
                                    WerewolfHelper.transformWerewolfBack(playerMP, cap, WerewolfTransformingEvent.WerewolfTransformingReason.FULL_MOON_END);
                                } else {
                                    WerewolfHelper.resetTransformationStageWhenNeeded(playerMP, cap);
                                }
                            }
                            // this piece of code syncs the player data every six minutes, so basically
                            // you don't have to sync the data every time you change something (though it is
                            // recommended)
                            if (player.ticksExisted % 7200 == 0) {
                                PacketHandler.sendTransformationMessage(playerMP);
                            }
                        }
                    }
                    // when a werewolf player is transforming, deal damage
                    if ((player.ticksExisted % 35 == 0) && isWerewolf && !isTransformed && isWerewolfTime
                            && (WerewolfHelper.getTransformationStage(player) != 0)) {
                        if (player.getHealth() > 1) {
                            player.attackEntityFrom(WerewolfHelper.WEREWOLF_TRANSFORMATION_DAMAGE, 1.0F);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityHurt(LivingHurtEvent event) {
        EntityLivingBase hurt = event.getEntityLiving();
        Transformation transformationHurt = TransformationHelper.getTransformation(hurt);
        if (event.getSource().getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();
            Transformation transformationAtt = TransformationHelper.getTransformation(attacker);

            if (transformationAtt.isTransformation()) {
                // make that player deals more damage
                if (attacker instanceof EntityPlayer) {
                    event.setAmount(transformationAtt.getDamage(attacker, event.getAmount()));
                }
                // reset lastAttackBite if this attack isn't bite
                if ((attacker instanceof EntityPlayer) && WerewolfHelper.isTransformed(attacker)
                        && WerewolfHelper.wasLastAttackBite(attacker)
                        && ((attacker.world.getTotalWorldTime() - WerewolfHelper.getBiteTicks(attacker)) != 0)) {
                    WerewolfHelper.setLastAttackBite(attacker, false);
                }
            }

            if (hurt != null) {
                if (attacker != null) {
                    // add effectiveness against undead
                    if (hurt.isEntityUndead() && GeneralHelper.itemStackHasOreDicts(attacker.getHeldItemMainhand(),
                            OreDictionaryInit.SILVER_NAMES))
                        event.setAmount(event.getAmount() + 2.5F);
                    if (transformationHurt.isTransformation()
                            && TransformationEventHandler.addEffectiveAgainst(event, attacker, hurt, transformationHurt))
                        return;
                }
            }
        }

        // add protection
        if (transformationHurt.isTransformation()
                && !(event.getSource().damageType.equals(TransformationHelper.THORNS_DAMAGE_NAME))
                && (event.getSource() != TransformationHelper.EFFECTIVE_AGAINST_TRANSFORMATION)) {
            event.setAmount(transformationHurt.getReducedDamage(hurt, event.getAmount()));
        }
        WerewolfEventHandler.onEntityHurt(event);
    }

    private static boolean addEffectiveAgainst(LivingHurtEvent event, EntityLivingBase attacker, EntityLivingBase
            hurt, Transformation transformationHurt) {
        // don't apply when it was thorns damage
        if (!event.getSource().damageType.equals(TransformationHelper.THORNS_DAMAGE_NAME)) {
            Object[] objects = {event.getSource().getImmediateSource(), attacker.getHeldItemMainhand(), attacker};
            for (Object obj : objects) {
                if (obj != null) {
                    float damage = event.getAmount();
                    EffectivenessEvent ee = (obj instanceof Entity)
                            ? new EntityEffectivenessEvent(hurt, (Entity) obj, damage)
                            : ((obj instanceof ItemStack)
                            ? new ItemEffectivenessEvent(hurt, attacker, damage, (ItemStack) obj)
                            : null);
                    if (MinecraftForge.EVENT_BUS.post(ee)) {
                        event.setAmount(ee.getDamage());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // do this at the end so the real damage can be seen
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void addThorns(LivingHurtEvent event) {
        Entity source = event.getSource().getTrueSource();
        EntityLivingBase hurt = event.getEntityLiving();
        if ((!event.getSource().getDamageType().equals(TransformationHelper.THORNS_DAMAGE_NAME))
                && source instanceof EntityLivingBase && hurt != null) {
            EntityLivingBase attacker = (EntityLivingBase) source;
            Transformation transformationAttacker = TransformationHelper.getTransformation(attacker);
            if (transformationAttacker.isTransformation()) {
                Iterable<ItemStack> stacks = hurt.getArmorInventoryList();

                float thorns = 0;
                float removedDamage = 0;
                // if at least one armor part's event has been canceled (meaning it was
                // effective)
                boolean anyItemEffective = false;

                for (ItemStack stack : stacks) {
                    if (!stack.isEmpty()) {
                        ArmorEffectivenessEvent aee = new ArmorEffectivenessEvent(hurt, attacker, event.getAmount(),
                                stack);
                        if (MinecraftForge.EVENT_BUS.post(aee)) {
                            anyItemEffective = true;
                            // add the thorns value
                            thorns += aee.getThorns();
                            // add the removedDamage value
                            removedDamage += aee.getRemovedDamage();
                            // damage the armor
                            int itemDamage = aee.getArmorDamage();
                            if (itemDamage > 0)
                                stack.damageItem(itemDamage, hurt);
                        }
                    }
                }

                // if one or more armor parts were effective,
                if (anyItemEffective) {
                    // and thorns is bigger than 0,
                    if (thorns > 0)
                        // attack the attacker with thorns
                        attacker.attackEntityFrom(TransformationHelper.causeEffectivenessThornsDamage(hurt), thorns);

                    // set the damage the entity will receive to the average (the Math#max is there
                    // so there won't be negative damage)
                    event.setAmount(Math.max(event.getAmount() - removedDamage, 0));
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEntityJoin(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityCreature) {
            EntityCreature creature = (EntityCreature) event.getEntity();
            Optional<ITransformationCreature> tc = TransformationHelper.getITransformationCreature(creature);

            World world = creature.world;
            if (!world.isRemote) {
                if (creature instanceof EntityGolem) {
                    EntityGolem entity = (EntityGolem) creature;
                    entity.targetTasks.addTask(2,
                            new EntityAINearestAttackableTarget<>(entity, EntityWerewolf.class, true));
                    entity.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(entity, EntityPlayer.class, 10,
                            true, false, WerewolfHelper::isTransformed));
                } else if (creature instanceof EntityVillager) {
                    if (ChanceHelper.chanceOf(creature, 5) && (tc.get().getTransformation() == Transformation.HUMAN)) {
                        TransformationHelper.changeTransformationWhenPossible(creature, Transformation.WEREWOLF,
                                TransformationEvent.TransformationEventReason.SPAWN);
                    }
                } else if (creature instanceof EntityWitch) {
                    if (ChanceHelper.chanceOf(creature, 5) && (tc.get().getTransformation() == Transformation.HUMAN)) {
                        TransformationHelper.changeTransformationWhenPossible(creature, Transformation.WEREWOLF,
                                TransformationEvent.TransformationEventReason.SPAWN);
                    }
                } else if (creature instanceof EntityWerewolf) {
                    ITransformation transformation = TransformationHelper.getITransformation(creature).get();
                    if (!transformation.textureIndexInBounds())
                        transformation.setTextureIndex(creature);
                } else if (creature instanceof EntitySkeleton) {
                    // undead hunter (skeleton with hunter armor and weapons)
                    if (creature.world.rand.nextInt(70) == 0) {
                        creature.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(ItemInit.HUNTER_HAT));
                        creature.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(ItemInit.HUNTER_COAT));
                        creature.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(ItemInit.HUNTER_PANTS));
                        creature.setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(ItemInit.HUNTER_BOOTS));
                        int weapon = creature.world.rand.nextInt(3);
                        creature.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(weapon == 0 ? ItemInit.FLINTLOCK_MUSKET :
                                (weapon == 1 ? ItemInit.FLINTLOCK_BLUNDERBUSS : ItemInit.FLINTLOCK_PISTOL)));
                    }
                }
                tc.ifPresent(t -> {
                    if (!t.textureIndexInBounds())
                        t.setTextureIndex(creature);
                });
            }

            if (creature instanceof EntityAgeable) {
                ((EntityAgeable) creature).tasks.addTask(2, new EntityAIAvoidEntity<>(creature, EntityLivingBase.class,
                        WerewolfHelper::isTransformed, 8.0F, 0.8F, 1.1F));
            }
        }
    }

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving().ticksExisted % 101 == 0) {
            EntityLivingBase entity = event.getEntityLiving();

            // hunter armor effects
            for (HunterArmorEffect effect : ItemHunterArmor.getEffectsFromEntity(entity)) {
                effect.applyEffect(entity);
            }

            if (!entity.world.isRemote) {
                if (entity instanceof EntityCreature) {
                    EntityCreature creature = (EntityCreature) entity;
                    Transformation transformation = TransformationHelper.getTransformation(creature);
                    if (transformation == Transformation.WEREWOLF) {
                        WerewolfHelper.onWerewolfTick(creature);
                    }
                }
                if (TransformationHelper.getTransformation(entity) == Transformation.VAMPIRE) {
                    VampireEventHandler.onVampireTick(entity);
                }

                if (entity.hasCapability(CapabilitiesInit.CAPABILITY_INFECT_IN_TICKS, null)) {
                    IInfectInTicks iit = TransformationHelper.getIInfectInTicks(entity).get();
                    if (iit.getTime() > -1) {
                        if (iit.currentlyInfected()) {
                            if (iit.getTimeUntilInfection() > 0) {
                                iit.setTimeUntilInfection(iit.getTimeUntilInfection() - 80);
                            } else if (iit.getTimeUntilInfection() <= 0) {
                                // when already done,

                                // infect entity
                                iit.getInfectionTransformation().getInfect().accept(entity);

                                // set to standard (-1)
                                iit.setTime(-1);
                            }
                        } else {
                            throw new UnexpectedBehaviorException(
                                    "IInfectInTicks#currentlyInfected returns false although there is definitely an infection going on");
                        }
                    }
                }
                WerewolfEventHandler.handleWerewolfInfection(entity);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityStruckByLightning(EntityStruckByLightningEvent event) {
        if (event.getEntity() instanceof EntityVillager) {
            EntityVillager villager = (EntityVillager) event.getEntity();
            Transformation transformation = TransformationHelper.getTransformation(villager);
            if (transformation.isTransformation()) {
                event.setCanceled(true);
                List<Entity> entities = villager.world.loadedEntityList;
                int entityAmount = entities.size();
                villager.onStruckByLightning(event.getLightning());
                if (entities.size() == (entityAmount + 1)) {
                    Entity witch = entities.get(entityAmount);
                    if (witch instanceof EntityWitch) {
                        TransformationHelper.changeTransformation((EntityWitch) witch, transformation,
                                TransformationEvent.TransformationEventReason.ENTITY_CHANGE);
                    } else {
                        Main.getLogger().warn("Villager " + villager + " was hit by lightning but became " + witch
                                + " instead of a normal witch");
                    }
                } else {
                    Main.getLogger().warn("Villager " + villager + " was hit by lightning but didn't become a witch");
                }
            }
        }
    }

    @SubscribeEvent
    public static void onArmorEffectiveness(ArmorEffectivenessEvent event) {
        if (event.getAttackerTransformation() == Transformation.WEREWOLF) {
            // have to change damage because some mods purposely don't register their
            // damaged armor to the ore dict because of recipes
            ItemStack armor = event.getArmor();
            int damage = armor.getItemDamage();
            armor.setItemDamage(0);
            int[] ids = OreDictionary.getOreIDs(armor);
            armor.setItemDamage(damage);

            float thorns;
            if (ArrayUtils.contains(ids, OreDictionary.getOreID("helmetSilver"))) {
                thorns = 0.075F;
            } else if (ArrayUtils.contains(ids, OreDictionary.getOreID("chestplateSilver"))) {
                thorns = 0.120F;
            } else if (ArrayUtils.contains(ids, OreDictionary.getOreID("leggingsSilver"))) {
                thorns = 0.105F;
            } else if (ArrayUtils.contains(ids, OreDictionary.getOreID("bootsSilver"))) {
                thorns = 0.060F;
            } else {
                return;
            }
            event.setThorns(event.getDamage() * thorns);
            event.setArmorDamage(4);
        }
        if ((event.getHurtTransformation() == Transformation.WEREWOLF) && (event.getAttacker() instanceof IProjectile)) {
            event.setDamage(event.getDamage() / 10F);
        }
    }

    @SubscribeEvent
    public static void onEntityEffectiveness(EntityEffectivenessEvent event) {
        Entity attacker = event.getAttacker();
        if (WerewolfHelper.isTransformed(event.getEntityLiving()) &&
                (((attacker instanceof IAmmunition) && ArrayUtils.contains(((IAmmunition) attacker).getAmmunitionTypes(), IAmmunition.AmmunitionType.SILVER))
                        || (attacker instanceof EntitySilverArrow))) {
            event.setDamage(event.getDamage() * 2.0F);
        }
    }

    @SubscribeEvent
    public static void onItemEffectiveness(ItemEffectivenessEvent event) {
        if ((event.getHurtTransformation() == Transformation.WEREWOLF)
                && GeneralHelper.itemStackHasOreDicts(event.getItemStack(), OreDictionaryInit.SILVER_NAMES)) {
            event.setDamage(event.getDamage() * (WerewolfHelper.isTransformed(event.getEntityLiving()) ? 4.0F : 2.0F));
        }
    }

    @SubscribeEvent
    public static void onLivingInfected(CanLivingBeInfectedEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        Optional<IInfectOnNextMoon> ionm = WerewolfHelper.getIInfectOnNextMoon(entity);
        if (ionm.isPresent() && (event.getInfection() == Transformation.WEREWOLF)) {
            event.setCanceled(true);
        }
    }
}
