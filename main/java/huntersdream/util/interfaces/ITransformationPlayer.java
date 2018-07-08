package huntersdream.util.interfaces;

/**
 * ITransform for players (players can have xp)
 */
public interface ITransformationPlayer extends ITransformation {
	public int getXP();

	public void setXP(int xp);

	default public void addXP(int xpToAdd) {
		setXP(getXP() + xpToAdd);
	}

	default public void removeXP(int xpToRemove) {
		setXP(getXP() - xpToRemove);
	}

	default public void incrementXP() {
		addXP(1);
	}

	default public void decrementXP() {
		addXP(-1);
	}

	public int getTextureIndex();

	public void setTextureIndex(int textureIndex);
}
