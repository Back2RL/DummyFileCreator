package logic;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogicController implements LogicInterface {

	public Settings getSettings() {
		return settings;
	}

	private Settings settings = null;

	synchronized public boolean isRunning() {
		return isRunning;
	}

	synchronized public void setRunning(boolean running) {
		isRunning = running;
	}

	private boolean isRunning = false;

	@Override
	public File getOriginalsDir() {
		return settings.getOriginalsDir();
	}

	@Override
	public File getDummiesDir() {
		return settings.getDummiesDir();
	}

	@Override
	public boolean getRunStatus() {
		return isRunning();
	}

	private static LogicController ourInstance = new LogicController();

	public static LogicController getInstance() {
		return ourInstance;
	}

	private LogicController() {
		// TODO: load/validate from XML File
		settings = new Settings();
		settings.addObserver(SettingsSaver.getInstance());
	}

	@Override
	public boolean setOriginalsDir(final File newOriginalsDir) {
		if (isValidDirectory(newOriginalsDir)) {
			settings.setOriginalsDir(newOriginalsDir);
			return true;
		}
		settings.setOriginalsDir(null);
		return false;
	}

	@Override
	public boolean setDummiesDir(final File newDummiesDir) {
		if (isValidDirectory(newDummiesDir)) {
			settings.setDummiesDir(newDummiesDir);
			return true;
		}
		settings.setDummiesDir(null);
		return false;
	}

	private boolean isValidDirectory(final File directory) {
		return directory != null && directory.isDirectory() && directory.canRead();
	}

	@Override
	public boolean createDummies() {

//		try {
//			Thread.sleep(5000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}

		setRunning(true);
		int created = 0;
		int skipped = 0;
		int failed = 0;

		File originals = settings.getOriginalsDir();
		File dummies = settings.getDummiesDir();

		if (!isValidDirectory(originals)) {
			Logger.getGlobal().log(Level.WARNING, "Creation of Dummies failed: " +
					"invalid Originals Directory");
			setRunning(false);
			return false;
		}
		if (!isValidDirectory(dummies)) {
			Logger.getGlobal().log(Level.WARNING, "Creation of Dummies failed: " +
					"invalid Dummies Directory");
			setRunning(false);
			return false;
		}
		if (originals.equals(dummies)) {
			Logger.getGlobal().log(Level.WARNING, "Creation of Dummies failed: " +
					"Dummy and Original Directory are the same");
			setRunning(false);
			return false;
		}

		File[] childFiles = originals.listFiles();
		if (childFiles == null || childFiles.length == 0) {
			Logger.getGlobal().log(Level.WARNING, "Creation of Dummies failed: " +
					"Originals Directory contains no Files");
			setRunning(false);
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
		setRunning(false);
		return true;
	}

	@Override
	public File getLastBrowserDir() {
		return settings.getLastBrowserDir();
	}

	@Override
	public boolean updateLastBrowserDir(File browserDir) {
		if (isValidDirectory(browserDir)) {
			settings.setLastBrowserDir(browserDir);
			return true;
		}
		return false;
	}


}
