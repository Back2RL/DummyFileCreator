package b_logic;

import java.io.File;
import java.io.IOException;

public class LogicBoundary {
	public static boolean setOriginalsDir(final File newOriginalsDir) {
		return LogicController.getInstance().setOriginalsDir(newOriginalsDir);
	}

	public static boolean setDummiesDir(final File newDummiesDir) {
		return LogicController.getInstance().setDummiesDir(newDummiesDir);
	}

	public static boolean setLastBrowserDir(final File lastBrowserDir) {
		return LogicController.getInstance().setLastBrowserDir(lastBrowserDir);
	}

	public static SettingsEntity getCurrentSettings(){
		return LogicController.getInstance().getCurrentSettings();
	}

	public static boolean createDummies(){
		return LogicController.getInstance().createDummies();
	}

	public static void saveSettings() throws IOException {
		LogicController.getInstance().saveToXML();
	}

	public static boolean validateXMLSchema(final File xmlSource) throws Exception {
		return LogicController.validateXMLSchema(xmlSource);
	}

	public static boolean loadSettings() throws Exception {
		return LogicController.getInstance().loadFromXML();
	}
}
