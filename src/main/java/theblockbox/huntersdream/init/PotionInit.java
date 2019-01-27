package theblockbox.huntersdream.init;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraftforge.event.RegistryEvent;
import theblockbox.huntersdream.potions.PotionAconite;
import theblockbox.huntersdream.potions.PotionFear;
import theblockbox.huntersdream.potions.PotionSunscreen;
import theblockbox.huntersdream.util.helpers.GeneralHelper;

public class PotionInit {

    public static final Potion POTION_FEAR = new PotionFear();
    public static final Potion POTION_ACONITE = new PotionAconite();
    public static final Potion POTION_SUNSCREEN = new PotionSunscreen();

    public static final PotionType ACONITE = new PotionType("aconite", new PotionEffect(PotionInit.POTION_ACONITE, 6000))
            .setRegistryName(GeneralHelper.newResLoc("aconite"));

    public static void registerPotions(RegistryEvent.Register<Potion> event) {
        event.getRegistry().registerAll(PotionInit.POTION_FEAR, PotionInit.POTION_ACONITE, PotionInit.POTION_SUNSCREEN);
    }

    public static void registerPotionTypes(RegistryEvent.Register<PotionType> event) {
        event.getRegistry().register(PotionInit.ACONITE);
    }
}
