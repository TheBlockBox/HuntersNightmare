package theblockbox.huntersdream.api.init;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraftforge.event.RegistryEvent;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.potions.PotionSunscreen;
import theblockbox.huntersdream.potions.PotionWolfsbane;

public class PotionInit {

    public static final Potion POTION_WOLFSBANE = new PotionWolfsbane();
    public static final Potion POTION_SUNSCREEN = new PotionSunscreen();

    public static final PotionType WOLFSBANE = new PotionType("wolfsbane", new PotionEffect(PotionInit.POTION_WOLFSBANE, 6000))
            .setRegistryName(GeneralHelper.newResLoc("wolfsbane"));

    public static void registerPotions(RegistryEvent.Register<Potion> event) {
        event.getRegistry().registerAll(PotionInit.POTION_WOLFSBANE, PotionInit.POTION_SUNSCREEN);
    }

    public static void registerPotionTypes(RegistryEvent.Register<PotionType> event) {
        event.getRegistry().register(PotionInit.WOLFSBANE);
    }
}
