package theblockbox.huntersdream.util.interfaces;

/**
 * An interface that should be implemented by all items and bullets serving as ammunition for some weapon.
 */
public interface IAmmunition {
    /**
     * Returns this ammunition's {@link IAmmunition.AmmunitionType}. For items, the types are used for determining which
     * weapons should be able to utilize this as ammunition. For entities (bullets), t
     */
    public IAmmunition.AmmunitionType[] getAmmunitionTypes();

    /**
     * Types of ammunition for specific weapons and abilities (like being effective against werewolves).
     */
    public enum AmmunitionType {
        /**
         * For flintlock weapons.
         */
        MUSKET_BALL,
        /**
         * Does twice as much damage to transformed werewolves and undead. (Goes through the natural armor of the former.)
         */
        SILVER
    }
}
