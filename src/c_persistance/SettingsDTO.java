package c_persistance;

import java.io.File;

public class SettingsDTO {
	public File getLastBrowserDir() {
		return lastBrowserDir;
	}

	public void setLastBrowserDir(final File lastBrowserDir) {
		this.lastBrowserDir = lastBrowserDir;
	}

	public File getOriginalsDir() {
		return originalsDir;
	}

	public void setOriginalsDir(final File originalsDir) {
		this.originalsDir = originalsDir;
	}

	public File getDummiesDir() {
		return dummiesDir;
	}

	public void setDummiesDir(final File dummiesDir) {
		this.dummiesDir = dummiesDir;
	}

	private File lastBrowserDir = null;
	private File originalsDir = null;
	private File dummiesDir = null;
}
