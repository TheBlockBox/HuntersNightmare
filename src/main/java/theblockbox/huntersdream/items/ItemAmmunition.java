package theblockbox.huntersdream.items;

import net.minecraft.item.Item;
import theblockbox.huntersdream.api.interfaces.IAmmunition;

public class ItemAmmunition extends Item implements IAmmunition {
    private final IAmmunition.AmmunitionType[] ammunitionTypes;

    public ItemAmmunition() {
        this(IAmmunition.AmmunitionType.MUSKET_BALL);
    }

    public ItemAmmunition(IAmmunition.AmmunitionType... ammunitionTypes) {
        this.ammunitionTypes = ammunitionTypes;
    }

    @Override
    public IAmmunition.AmmunitionType[] getAmmunitionTypes() {
        return this.ammunitionTypes;
    }
}
