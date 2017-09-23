package a_presentation.controller;

import a_presentation.model.Model;
import a_presentation.view.ErrorDialog;
import a_presentation.view.View;
import b_logic.LogicBoundary;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller {

	private static Controller ourInstance = new Controller();
	private View view = null;
	private Model model = null;
	private EventHandler<WindowEvent> windowCloseHandler = new EventHandler<WindowEvent>() {
		public void handle(WindowEvent we) {
			try {
				LogicBoundary.saveSettings();
			} catch (IOException e) {
				new ErrorDialog(Alert.AlertType.ERROR, e, "ERROR!", "Settings could not be saved!");
			}
			Logger.getGlobal().log(Level.INFO, "Closing the Program");
		}
	};

	private Controller() {
	}

	public static Controller getInstance() {
		return ourInstance;
	}

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public void init() {
		view.addOnWindowCloseEventHandler(windowCloseHandler);
		loadSettings();
	}

	private void loadSettings() {
		new Thread(() -> {
			try {
				if (!LogicBoundary.loadSettings()) {
					Platform.runLater(() -> new ErrorDialog(Alert.AlertType.INFORMATION, null, "Information", "No Settings were loaded"));
				}
				model.setDummiesDir(LogicBoundary.getCurrentSettings().getDummiesDir());
				model.setOriginalsDir(LogicBoundary.getCurrentSettings().getOriginalsDir());
				model.setLastBrowserDir(LogicBoundary.getCurrentSettings().getLastBrowserDir());
			} catch (Exception e) {
				Platform.runLater(() -> new ErrorDialog(Alert.AlertType.ERROR, e, "ERROR!", "Settings could not be loaded!"));
			}
		}).start();
	}


}
