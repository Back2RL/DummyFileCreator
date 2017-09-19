package a_presentation;

import java.util.Observable;
import java.util.Observer;

public class Job extends Observable {
	synchronized public boolean isRunning() {
		return isRunning;
	}

	synchronized public void setRunning(boolean running) {
		isRunning = running;
		setChanged();
		notifyObservers();
	}

	private boolean isRunning;

	public Job(boolean isRunning, Observer...observers) {
		for(Observer observer:observers){
			addObserver(observer);
		}
		setRunning(isRunning);
	}
}
