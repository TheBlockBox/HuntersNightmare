package theblockbox.huntersdream.api.init;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.api.event.WerewolfTransformingEvent;
import theblockbox.huntersdream.api.helpers.TransformationHelper;
import theblockbox.huntersdream.api.helpers.WerewolfHelper;
import theblockbox.huntersdream.api.skill.ChildSkill;
import theblockbox.huntersdream.api.skill.ParentSkill;
import theblockbox.huntersdream.util.collection.TransformationSet;
import theblockbox.huntersdream.util.handlers.PacketHandler;

import static theblockbox.huntersdream.api.helpers.GeneralHelper.newResLoc;

/**
 * A simple class for storing all skill instances.
 */
public class SkillInit {
    private static final TransformationSet WEREWOLF_SET = TransformationSet.singletonSet(Transformation.WEREWOLF);

    // Hunter's Dream Skills
    public static final ParentSkill SPEED_0 = new ParentSkill(newResLoc("speed"), 40, SkillInit.WEREWOLF_SET, true);
    public static final ChildSkill SPEED_1 = new ChildSkill(SkillInit.SPEED_0, 80, 1);
    public static final ChildSkill SPEED_2 = new ChildSkill(SkillInit.SPEED_0, 120, 2);

    public static final ParentSkill JUMP_0 = new ParentSkill(newResLoc("jump"), 40, SkillInit.WEREWOLF_SET, true);
    public static final ChildSkill JUMP_1 = new ChildSkill(SkillInit.JUMP_0, 80, 1);
    public static final ChildSkill JUMP_2 = new ChildSkill(SkillInit.JUMP_0, 120, 2);

    public static final ParentSkill UNARMED_0 = new ParentSkill(newResLoc("unarmed"), 40, SkillInit.WEREWOLF_SET, true);
    public static final ChildSkill UNARMED_1 = new ChildSkill(SkillInit.UNARMED_0, 80, 1);
    public static final ChildSkill UNARMED_2 = new ChildSkill(SkillInit.UNARMED_0, 120, 2);

    public static final ParentSkill ARMOR_0 = new ParentSkill(newResLoc("natural_armor"), 40, SkillInit.WEREWOLF_SET, true);
    public static final ChildSkill ARMOR_1 = new ChildSkill(SkillInit.ARMOR_0, 80, 1);
    public static final ChildSkill ARMOR_2 = new ChildSkill(SkillInit.ARMOR_0, 120, 2);

    public static final ParentSkill BITE_0 = new ParentSkill(newResLoc("bite"), 40, SkillInit.WEREWOLF_SET, false) {
        @Override
        public boolean onSkillUse(EntityPlayer player) {
            if (player.world.isRemote && WerewolfHelper.canPlayerBiteAgain(player)) {
                Minecraft mc = Minecraft.getMinecraft();
                if ((mc.objectMouseOver != null) && (mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY)) {
                    // TODO: do animation here
                    player.swingArm(EnumHand.MAIN_HAND); // TODO: Remove once animation is done
                    PacketHandler.sendUseBiteMessage(player.world, mc.objectMouseOver.entityHit);
                }
            }
            return false;
        }
    };
    public static final ChildSkill BITE_1 = new ChildSkill(SkillInit.BITE_0, 80, 1);
    public static final ChildSkill BITE_2 = new ChildSkill(SkillInit.BITE_0, 120, 2);

    public static final ParentSkill WILFUL_TRANSFORMATION = new ParentSkill(newResLoc("wilful_transformation"), -1, SkillInit.WEREWOLF_SET, false) {
        @Override
        public boolean shouldShowSkillInSkillBar(EntityPlayer player) {
            return true;
        }

        @Override
        public boolean onSkillUse(EntityPlayer player) {
            if (player.world.isRemote) {
                return WerewolfHelper.canPlayerWilfullyTransform(player) || WerewolfHelper.canPlayerWilfullyTransformBack(player);
            } else {
                EntityPlayerMP playerMP = (EntityPlayerMP) player;
                if (WerewolfHelper.isTransformed(playerMP)) {
                    if (WerewolfHelper.canPlayerWilfullyTransformBack(playerMP)) {
                        WerewolfHelper.transformWerewolfBack(playerMP, TransformationHelper.getITransformationPlayer(playerMP),
                                WerewolfTransformingEvent.WerewolfTransformingReason.WILFUL_TRANSFORMATION_ENDING);
                        PacketHandler.sendTransformationMessage(playerMP);
                        return true;
                    } else {
                        Main.getLogger().error("The player " + playerMP + " tried to transform back via deactivating " +
                                "wilful transformation but wasn't allowed to");
                    }
                } else {
                    if (WerewolfHelper.canPlayerWilfullyTransform(playerMP)) {
                        // TODO: Not hardcode this?
                        if (playerMP.world.provider.getCurrentMoonPhaseFactor() != 1.0F) {
                            WerewolfHelper.setWilfulTransformationTicks(playerMP, playerMP.world.getTotalWorldTime());
                            PacketHandler.sendTransformationMessage(playerMP);
                            return true;
                        }
                    } else {
                        Main.getLogger().error("The player " + playerMP + " tried to activate wilful transformation but " +
                                "wasn't allowed to");
                    }
                }
                return false;
            }
        }
    };
}
