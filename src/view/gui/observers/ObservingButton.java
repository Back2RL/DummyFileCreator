package view.gui.observers;

import view.gui.ErrorDialog;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import java.util.*;

public class ObservingButton extends Button implements Observer {

	protected Set<Observable> observingObjects = new HashSet<>();

	public ObservingButton(Observable... observable) {
		observeObject(observable);
	}



	@Override
	public void update(Observable o, Object arg) {
		observeObject(o);
	}

	public void observeObject(Observable... observable) {
		try {
			for (Observable o : observable) {
				o.addObserver(this);
				observingObjects.add(o);
			}
		} catch (Exception e) {
			new ErrorDialog(Alert.AlertType.WARNING, e, "Error", "An Exception occurred!");
		}
	}
}
