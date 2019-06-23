package theblockbox.huntersdream.api.init;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import theblockbox.huntersdream.api.Transformation;
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
        private long lastSuccessfulUse = 0;

        @Override
        public boolean onSkillUse(EntityPlayer player) {
            if (player.world.isRemote && WerewolfHelper.canPlayerBiteAgain(player)) {
                RayTraceResult mouseOver = Minecraft.getMinecraft().objectMouseOver;
                long totalWorldTime = player.world.getTotalWorldTime();
                if ((mouseOver != null) && (mouseOver.typeOfHit == RayTraceResult.Type.ENTITY)
                        && (this.lastSuccessfulUse < (totalWorldTime - 60))) {
                    // set last successful use to prevent double clicking
                    this.lastSuccessfulUse = totalWorldTime;
                    // send message to server
                    PacketHandler.sendUseBiteMessage(player.world, mouseOver.entityHit);
                }
            }
            return false;
        }
    };
    public static final ChildSkill BITE_1 = new ChildSkill(SkillInit.BITE_0, 80, 1);
    public static final ChildSkill BITE_2 = new ChildSkill(SkillInit.BITE_0, 120, 2);
}
