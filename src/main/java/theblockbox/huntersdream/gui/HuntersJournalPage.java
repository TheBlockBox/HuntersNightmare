package theblockbox.huntersdream.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class HuntersJournalPage {
	private String title;
	private ResourceLocation imagePath;
	private String description;

	public HuntersJournalPage(String keyTitle, ResourceLocation imagePath, String keyDescription) {
		this.title = I18n.format(keyTitle);
		this.imagePath = imagePath;
		this.description = I18n.format(keyDescription);
	}

	public String getTitle() {
		return title;
	}

	public ResourceLocation getImagePath() {
		return imagePath;
	}

	public String getDescription() {
		return description;
	}
}
