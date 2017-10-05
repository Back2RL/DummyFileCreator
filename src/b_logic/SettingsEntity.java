package b_logic;

import java.io.File;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SettingsEntity {
	public File getLastBrowserDir() {
		return lastBrowserDir;
	}

	SettingsEntity() {
	}

	SettingsEntity(SettingsEntity other) {
		lastBrowserDir = other.lastBrowserDir;
		originalsDir = other.originalsDir;
		dummiesDir = other.dummiesDir;
	}

	void setLastBrowserDir(final File lastBrowserDir) {
		this.lastBrowserDir = lastBrowserDir;
	}

	public File getOriginalsDir() {
		return originalsDir;
	}

	void setOriginalsDir(final File originalsDir) {
		this.originalsDir = originalsDir;
	}

	public File getDummiesDir() {
		return dummiesDir;
	}

	void setDummiesDir(final File dummiesDir) {
		this.dummiesDir = dummiesDir;
	}

	private File lastBrowserDir = null;
	private File originalsDir = null;
	private File dummiesDir = null;

	public HashSet<File> getDummyHistory() {
		return dummyHistory;
	}



	public HashSet<File> getOriginalHistory() {
		return originalHistory;
	}



	void addDummyDirToHistory(File dummyDir) {
		if (!dummyHistory.contains(dummyDir)) {
			dummyHistory.add(dummyDir);
//			if (dummyHistory.size() > 10) {
//				dummyHistory.remove(0);
//			}
			Logger.getGlobal().log(Level.INFO,"added Dummy Entry to history: "+dummyHistory.size());
		}
	}

	private HashSet<File> dummyHistory = new HashSet<>();
	private HashSet<File> originalHistory = new HashSet<>();
}
