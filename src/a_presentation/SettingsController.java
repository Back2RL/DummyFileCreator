package a_presentation;

public class SettingsController {
	private static SettingsController ourInstance = new SettingsController();

	public static SettingsController getInstance() {
		return ourInstance;
	}

	private SettingsController() {
	init();
	}

	public void init(){

	}
}
