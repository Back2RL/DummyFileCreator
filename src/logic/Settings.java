package logic;

import java.io.File;
import java.util.Observable;

public class Settings extends Observable {
	private File lastBrowserDir = null;
	private File originalsDir = null;
	private File dummiesDir = null;

	public File getOriginalsDir() {
		return originalsDir;
	}

	public void setOriginalsDir(final File originalsDir) {
		this.originalsDir = originalsDir;
		setChanged();
	}

	public File getDummiesDir() {
		return dummiesDir;
	}

	public void setDummiesDir(final File dummiesDir) {
		this.dummiesDir = dummiesDir;
		setChanged();
	}

	public File getLastBrowserDir() {
		return lastBrowserDir;
	}

	public void setLastBrowserDir(File lastBrowserDir) {
		this.lastBrowserDir = lastBrowserDir;
		setChanged();
	}

	public void save(){
		notifyObservers();
	}
}
