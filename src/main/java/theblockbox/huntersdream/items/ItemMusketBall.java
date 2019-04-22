package theblockbox.huntersdream.items;

import net.minecraft.item.Item;
import theblockbox.huntersdream.api.interfaces.IAmmunition;

public class ItemMusketBall extends Item implements IAmmunition {
    public static final IAmmunition.AmmunitionType[] AMMUNITION_TYPES = {IAmmunition.AmmunitionType.MUSKET_BALL};
    private final IAmmunition.AmmunitionType[] ammunitionTypes;

    public ItemMusketBall() {
        this(ItemMusketBall.AMMUNITION_TYPES);
    }

    public ItemMusketBall(IAmmunition.AmmunitionType[] ammunitionTypes) {
        this.ammunitionTypes = ammunitionTypes;
    }

    @Override
    public IAmmunition.AmmunitionType[] getAmmunitionTypes() {
        return this.ammunitionTypes;
    }
}
