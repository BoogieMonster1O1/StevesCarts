package stevesvehicles.client.localization.entry.info;

import stevesvehicles.api.modules.data.ILocalizedText;
import stevesvehicles.client.localization.LocalizedTextSimple;

public final class LocalizationMessage {
	public static final ILocalizedText THUNDER_PIG = createSimple("thunder_pig");
	public static final ILocalizedText OCEAN = createSimple("ocean");
	public static final ILocalizedText YEAR = createSimple("year");
	public static final ILocalizedText EMPTY_STORAGE = createSimple("empty_storage");
	public static final ILocalizedText FULL_STORAGE = createSimple("full_storage");
	public static final ILocalizedText GIFT = createSimple("gift");
	public static final ILocalizedText EGG = createSimple("egg");
	private static final String HEADER = "steves_vehicles:gui.info.message:";

	private static ILocalizedText createSimple(String code) {
		return new LocalizedTextSimple(HEADER + code);
	}

	private LocalizationMessage() {
	}
}
