package a_presentation.model;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

public class Model extends Observable {
	private File lastBrowserDir = null;
	private File originalsDir = null;
	private File dummiesDir = null;

	public File getLastBrowserDir() {
		return lastBrowserDir;
	}

	public void setLastBrowserDir(final File lastBrowserDir) {
		this.lastBrowserDir = lastBrowserDir;
		setChanged();
		notifyObservers();
	}

	public File getOriginalsDir() {
		return originalsDir;
	}

	public void setOriginalsDir(final File originalsDir) {
		this.originalsDir = originalsDir;
		setChanged();
		notifyObservers();
	}

	public File getDummiesDir() {
		return dummiesDir;
	}

	public void setDummiesDir(final File dummiesDir) {
		this.dummiesDir = dummiesDir;
		setChanged();
		notifyObservers();
	}

	@Override
	public synchronized void addObserver(Observer o) {
		super.addObserver(o);
		setChanged();
		notifyObservers();
	}
}
