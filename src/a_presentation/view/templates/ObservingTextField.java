package a_presentation.view.templates;

import a_presentation.view.ErrorDialog;
import a_presentation.view.templates.AutoSelectingTextField;
import javafx.scene.control.Alert;

import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

public class ObservingTextField extends AutoSelectingTextField implements Observer {
	protected Set<Observable> observingObjects = new HashSet<>();

	public ObservingTextField(Observable... observable) {
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
