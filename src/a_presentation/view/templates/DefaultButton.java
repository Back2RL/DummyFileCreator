package a_presentation.view.templates;

import javafx.scene.control.Button;

public class DefaultButton extends Button {
	public DefaultButton(final String text) {
		super(text);
		setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
	}
}
