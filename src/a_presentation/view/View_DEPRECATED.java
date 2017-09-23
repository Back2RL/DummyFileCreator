package a_presentation.view;

import a_presentation.view.templates.AutoSelectingTextField;
import b_logic.LogicBoundary;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class View_DEPRECATED {
	private static View_DEPRECATED ourInstance = new View_DEPRECATED();

	public static View_DEPRECATED getInstance() {
		return ourInstance;
	}

	private View_DEPRECATED() 	{
	}

	public void start(Stage primaryStage) throws Exception {
		VBox root = new VBox();

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setFitToHeight(true);
		scrollPane.setFitToWidth(true);

		HBox statusBar = new HBox();
		statusBar.setMinWidth(512);
		statusBar.setAlignment(Pos.BOTTOM_LEFT);

		Label status = new Label();
		status.setText("Idle");
		status.setAlignment(Pos.BOTTOM_LEFT);

		ProgressBar progressBar = new ProgressBar(0.5);
		progressBar.setVisible(false);

		statusBar.getChildren().addAll(status, progressBar);
		root.getChildren().addAll(scrollPane, statusBar);
		VBox.setVgrow(scrollPane, Priority.ALWAYS);

		Scene scene = new Scene(root);
		primaryStage.setTitle("DummyFileCreator");
		primaryStage.setScene(scene);
		primaryStage.setMinHeight(128);
		primaryStage.setMinWidth(512);
		//primaryStage.setAlwaysOnTop(true);
		primaryStage.show();
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				try {
					LogicBoundary.saveSettings();
				} catch (IOException e) {
					new ErrorDialog(Alert.AlertType.ERROR, e, "ERROR!", "Settings could not be saved!");
				}
				Logger.getGlobal().log(Level.INFO, "Closing the Program");
			}
		});

	}

}
