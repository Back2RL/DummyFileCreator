package a_presentation.controller;

import a_presentation.view.ErrorDialog;
import b_logic.LogicBoundary;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class Controller {
	private static Controller ourInstance = new Controller();

	public static Controller getInstance() {
		return ourInstance;
	}

	private Controller() {
	}

	public void init(){
		try {
			if(!LogicBoundary.loadSettings()){
				Platform.runLater(() -> new ErrorDialog(Alert.AlertType.INFORMATION,null,"Information","No Settings were loaded"));
			}
		} catch (Exception e) {
			Platform.runLater(() -> new ErrorDialog(Alert.AlertType.ERROR,e,"ERROR!","Settings could not be loaded!"));
		}
	}
}
