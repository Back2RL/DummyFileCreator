package b_logic;

import java.io.File;

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
}
