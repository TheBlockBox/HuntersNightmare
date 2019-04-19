package theblockbox.huntersdream.util.interfaces;

public interface IGun {
    // TODO: Write java doc for this
    public IAmmunition.AmmunitionType[] getAllowedAmmunitionTypes();

    /**
     * Returns how inaccurate this gun is. Vanilla arrows, potions, ender pearls, snowballs and eggs use 1.0,
     * droppers 6.0, witches 8.0, llamas 10.0 and snowmen 12.0.
     */
    public float getInaccuracy();
}
