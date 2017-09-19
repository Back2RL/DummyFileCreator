package b_logic;

import c_persistance.PersistanceBoundary;
import c_persistance.SettingsDTO;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

class LogicController {

	private SettingsEntity settingsEntity = new SettingsEntity();

	private static LogicController ourInstance = new LogicController();

	static LogicController getInstance() {
		return ourInstance;
	}

	private LogicController() {
	}

	boolean setOriginalsDir(final File newOriginalsDir) {
		if (isValidDirectory(newOriginalsDir)
				&& (settingsEntity.getDummiesDir() == null
				^!settingsEntity.getDummiesDir().equals(newOriginalsDir))) {
			settingsEntity.setOriginalsDir(newOriginalsDir);
			return true;
		}
		return false;
	}


	boolean setDummiesDir(final File newDummiesDir) {
		if (isValidDirectory(newDummiesDir)
				&& (settingsEntity.getOriginalsDir() == null
				^!settingsEntity.getOriginalsDir().equals(newDummiesDir))) {
			settingsEntity.setDummiesDir(newDummiesDir);
			return true;
		}
		return false;
	}

	private boolean isValidDirectory(final File directory) {
		return directory != null && directory.isDirectory() && directory.canRead();
	}

	boolean createDummies() {

		int created = 0;
		int skipped = 0;
		int failed = 0;

		File originals = settingsEntity.getOriginalsDir();
		File dummies = settingsEntity.getDummiesDir();

		if (!isValidDirectory(originals)) {
			Logger.getGlobal().log(Level.WARNING, "Creation of Dummies failed: " +
					"invalid Originals Directory");
			return false;
		}
		if (!isValidDirectory(dummies)) {
			Logger.getGlobal().log(Level.WARNING, "Creation of Dummies failed: " +
					"invalid Dummies Directory");
			return false;
		}
		if (originals.equals(dummies)) {
			Logger.getGlobal().log(Level.WARNING, "Creation of Dummies failed: " +
					"Dummy and Original Directory are the same");
			return false;
		}

		File[] childFiles = originals.listFiles();
		if (childFiles == null || childFiles.length == 0) {
			Logger.getGlobal().log(Level.WARNING, "Creation of Dummies failed: " +
					"Originals Directory contains no Files");
			return false;
		}
		Logger.getGlobal().log(Level.INFO, "Starting creation of Dummies.\n" +
				"Source: " + originals.getPath() + "\n" +
				"Target: " + dummies.getPath());
		for (File childFile : childFiles) {
			File newDummy = new File(dummies.getPath() + File.separator + childFile.getName());
			Logger.getGlobal().log(Level.INFO, "creating Dummy for " + childFile.getName() + ":\n"
					+ newDummy.getPath());
			try {
				if (!newDummy.exists()) {
					if (newDummy.createNewFile()) {
						Logger.getGlobal().log(Level.INFO, "created Dummy: " + newDummy.getPath());
						++created;
					} else {
						Logger.getGlobal().log(Level.WARNING, "Dummy-File could not be created: " +
								newDummy.getPath());
						++failed;
					}
				} else {
					++skipped;
					Logger.getGlobal().log(Level.INFO, "skipped Dummy: " +
							newDummy.getPath() + "\nFile exists.");
				}
			} catch (IOException e) {
				e.printStackTrace();
				++failed;
			}
		}
		Logger.getGlobal().log(Level.INFO, "Creation of Dummies finished.\n" +
				"  total: " + childFiles.length + "\ncreated: " + created +
				"\nskipped: " + skipped + "\n failed: " + failed);
		return true;
	}

	boolean setLastBrowserDir(File browserDir) {
		if (isValidDirectory(browserDir)) {
			settingsEntity.setLastBrowserDir(browserDir);
			return true;
		}
		return false;
	}

	final SettingsEntity getCurrentSettings() {
		return new SettingsEntity(settingsEntity);
	}

	void saveToXML() throws IOException {
		SettingsDTO settingsDTO = new SettingsDTO();
		settingsDTO.setLastBrowserDir(settingsEntity.getLastBrowserDir());
		settingsDTO.setDummiesDir(settingsEntity.getDummiesDir());
		settingsDTO.setOriginalsDir(settingsEntity.getOriginalsDir());
		PersistanceBoundary.saveToXML(settingsDTO);
	}

	static boolean validateXMLSchema(final File xmlSource) throws Exception {
		return PersistanceBoundary.validateXMLSchema(xmlSource);
	}

	boolean loadFromXML() throws Exception {
		SettingsDTO settingsDTO = PersistanceBoundary.loadFromXML();
		if (settingsDTO != null) {
			settingsEntity.setDummiesDir(settingsDTO.getDummiesDir());
			settingsEntity.setLastBrowserDir(settingsDTO.getLastBrowserDir());
			settingsEntity.setOriginalsDir(settingsDTO.getOriginalsDir());
			return true;
		}
		return false;
	}

}