package controller.logic;

import a_presentation.model.SettingsModel;

import java.io.File;
import java.io.IOException;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogicController implements controller.logic.LogicInterface {

	public SettingsModel getSettingsModel() {
		return settingsModel;
	}

	private SettingsModel settingsModel = null;



	@Override
	public File getOriginalsDir() {
		return settingsModel.getOriginalsDir();
	}

	@Override
	public File getDummiesDir() {
		return settingsModel.getDummiesDir();
	}


	private static LogicController ourInstance = new LogicController();

	public static LogicController getInstance() {
		return ourInstance;
	}

	private LogicController() {
		// TODO: load/validate from XML File
		settingsModel = new SettingsModel();
	}

	@Override
	public boolean setOriginalsDir(final File newOriginalsDir) {
		if (isValidDirectory(newOriginalsDir)) {
			settingsModel.setOriginalsDir(newOriginalsDir);
			return true;
		}
		settingsModel.setOriginalsDir(null);
		return false;
	}

	@Override
	public boolean setDummiesDir(final File newDummiesDir) {
		if (isValidDirectory(newDummiesDir)) {
			settingsModel.setDummiesDir(newDummiesDir);
			return true;
		}
		settingsModel.setDummiesDir(null);
		return false;
	}

	private boolean isValidDirectory(final File directory) {
		return directory != null && directory.isDirectory() && directory.canRead();
	}

	@Override
	public boolean createDummies(Observer...observers) {


		controller.logic.Job job = new controller.logic.Job(true,observers);
		int created = 0;
		int skipped = 0;
		int failed = 0;

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		File originals = settingsModel.getOriginalsDir();
		File dummies = settingsModel.getDummiesDir();

		if (!isValidDirectory(originals)) {
			Logger.getGlobal().log(Level.WARNING, "Creation of Dummies failed: " +
					"invalid Originals Directory");
			job.setRunning(false);
			return false;
		}
		if (!isValidDirectory(dummies)) {
			Logger.getGlobal().log(Level.WARNING, "Creation of Dummies failed: " +
					"invalid Dummies Directory");
			job.setRunning(false);
			return false;
		}
		if (originals.equals(dummies)) {
			Logger.getGlobal().log(Level.WARNING, "Creation of Dummies failed: " +
					"Dummy and Original Directory are the same");
			job.setRunning(false);
			return false;
		}

		File[] childFiles = originals.listFiles();
		if (childFiles == null || childFiles.length == 0) {
			Logger.getGlobal().log(Level.WARNING, "Creation of Dummies failed: " +
					"Originals Directory contains no Files");
			job.setRunning(false);
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
		job.setRunning(false);
		return true;
	}

	@Override
	public File getLastBrowserDir() {
		return settingsModel.getLastBrowserDir();
	}

	@Override
	public boolean updateLastBrowserDir(File browserDir) {
		if (isValidDirectory(browserDir)) {
			settingsModel.setLastBrowserDir(browserDir);
			return true;
		}
		return false;
	}


}
