package a_presentation.view;

import a_presentation.view.templates.ObservingButton;
import javafx.application.Platform;
import a_presentation.controller.Job;

import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StartButton extends ObservingButton {

	private boolean invalidDirectories = false;
	private boolean isRunning = true;

	@Override
	public void update(Observable o, Object arg) {
		super.update(o, arg);

		for (Observable observable : observingObjects) {

//			if (observable instanceof SettingsModel) {
//				SettingsModel settingsModel = (SettingsModel) observable;
//				invalidDirectories = settingsModel.getDummiesDir() == null || settingsModel.getOriginalsDir() == null;
//				continue;
//			}
			if (observable instanceof Job) {
				Job job = (Job) observable;
				isRunning = job.isRunning();
			}
		}

		updateStatus();
		Logger.getGlobal().log(Level.INFO, "Updated Start-Button: " + isRunning);
	}

	private void updateStatus(){
		Platform.runLater(() -> setDisable(isRunning || invalidDirectories));
	}


}
