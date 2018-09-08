package theblockbox.huntersdream.util;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import theblockbox.huntersdream.util.exceptions.UnexpectedBehaviorException;

public class HuntersJournalPage {
	public static final List<HuntersJournalPage> PAGES = new ArrayList<>();
	// Don't use the normal constructor, it's only used here because it's a test
	// page that you shouldn't be able to get
	public static final HuntersJournalPage TEST_PAGE = new HuntersJournalPage(
			Reference.MODID + ".huntersjournal.test.title", null, Reference.MODID + ".huntersjournal.test.description");
	public static final HuntersJournalPage WEREWOLF = HuntersJournalPage.of("werewolf", null);
	private String title;
	private ResourceLocation imagePath;
	private String description;
	private boolean hasImage;

	private HuntersJournalPage(String keyTitle, ResourceLocation imagePath, String keyDescription) {
		this.title = I18n.format(keyTitle);
		this.imagePath = imagePath;
		this.description = I18n.format(keyDescription);
		this.hasImage = imagePath != null;
	}

	public static HuntersJournalPage of(String keyTitle, @Nullable ResourceLocation imagePath, String keyDescription) {
		HuntersJournalPage page = new HuntersJournalPage(keyTitle, imagePath, keyDescription);
		PAGES.add(page);
		return page;
	}

	public static HuntersJournalPage of(String name, @Nullable ResourceLocation imagePath) {
		return of(Reference.MODID + ".hunters_journal." + name + ".title", imagePath,
				Reference.MODID + ".hunters_journal." + name + ".description");
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

	public boolean hasImage() {
		return hasImage;
	}

	@Override
	public String toString() {
		int index = PAGES.indexOf(this);
		if (index == -1) {
			throw new UnexpectedBehaviorException(
					"The page " + this.getTitle() + " couldn't be found in the HuntersJournalPage.PAGES list");
		}
		return this.getTitle() + "?" + index;
	}

	public static HuntersJournalPage fromName(String name) {
		HuntersJournalPage page = PAGES.get(Integer.parseInt(name.substring(name.lastIndexOf('?') + 1, name.length())));
		if (name.equals(page.toString())) {
			return page;
		} else {
			throw new UnexpectedBehaviorException("Can't find hunter's journal page \"" + name + "\"");
		}
	}
}
