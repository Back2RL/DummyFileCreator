package gui;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class AutoSelectingTextField extends TextField {
	private boolean justGainedFocus = false;

	public AutoSelectingTextField() {
		super();
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				setEditable(true);
			}
		});
		setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent event) {
				if(justGainedFocus) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							selectAll();
						}
					});
				}
				justGainedFocus = false;
			}
		});

		focusedProperty().addListener(new ChangeListener<Boolean>()
		{
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
			{
				if (newPropertyValue)
				{
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							selectAll();
						}
					});
					justGainedFocus = true;
				}
			}
		});
	}
}
