package b_logic;

import c_persistence.IPersistence;
import c_persistence.SettingsDTO;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogicController {

	private static LogicController ourInstance = new LogicController();
	private SettingsEntity settingsEntity = new SettingsEntity();

	public IPersistence getiPersistence() {
		return iPersistence;
	}

	public void setiPersistence(final IPersistence iPersistence) {
		this.iPersistence = iPersistence;
	}

	private IPersistence iPersistence;

	private LogicController() {
	}

	public static LogicController getInstance() {
		return ourInstance;
	}

	boolean validateXMLSchema(final File xmlSource) throws Exception {
		return iPersistence.validateXMLSchema(xmlSource);
	}

	boolean setOriginalsDir(final File newOriginalsDir) {
		if (isValidDirectory(newOriginalsDir)
				&& (settingsEntity.getDummiesDir() == null
				^ !settingsEntity.getDummiesDir().equals(newOriginalsDir))) {
			settingsEntity.setOriginalsDir(newOriginalsDir);
			return true;
		}
		settingsEntity.setOriginalsDir(null);
		return false;
	}

	private boolean isValidDirectory(final File directory) {
		return directory != null && directory.isDirectory() && directory.canRead();
	}

	boolean setDummiesDir(final File newDummiesDir) {
		if (isValidDirectory(newDummiesDir)
				&& (settingsEntity.getOriginalsDir() == null
				^ !settingsEntity.getOriginalsDir().equals(newDummiesDir))) {
			settingsEntity.setDummiesDir(newDummiesDir);
			settingsEntity.addDummyDirToHistory(newDummiesDir);
			return true;
		}
		settingsEntity.setDummiesDir(null);
		return false;
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
		settingsEntity.setLastBrowserDir(null);
		return false;
	}

	final SettingsEntity getCurrentSettings() {
		return settingsEntity;
	}

	void saveToXML() throws IOException {
		SettingsDTO settingsDTO = new SettingsDTO();
		settingsDTO.setLastBrowserDir(settingsEntity.getLastBrowserDir());
		settingsDTO.setDummiesDir(settingsEntity.getDummiesDir());
		settingsDTO.setOriginalsDir(settingsEntity.getOriginalsDir());
		iPersistence.saveToXML(settingsDTO);
	}

	boolean loadFromXML() throws Exception {
		SettingsDTO settingsDTO = iPersistence.loadFromXML();
		if (settingsDTO != null) {
			settingsEntity.setDummiesDir(settingsDTO.getDummiesDir());
			settingsEntity.setLastBrowserDir(settingsDTO.getLastBrowserDir());
			settingsEntity.setOriginalsDir(settingsDTO.getOriginalsDir());
			return true;
		}
		return false;
	}

}
