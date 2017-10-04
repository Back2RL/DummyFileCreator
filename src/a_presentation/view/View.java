package a_presentation.view;

import a_presentation.controller.Controller;
import a_presentation.view.templates.DefaultButton;
import a_presentation.view.templates.ObservingTextField;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class View {

	private static View ourInstance = new View();
	private VBox root;
	private ScrollPane scrollPane;
	private HBox statusBar;
	private Label status;
	private ProgressBar progressBar;
	private Scene scene;
	private ObservingTextField tfOrigDirPathInput;
	private ObservingTextField tfDummyDirPathInput;
	private DefaultButton btnChooseOrigDir;
	private DefaultButton btnChooseDummyDir;
	private DefaultButton btnReloadSettings;
	private StartButton btnStartDummyCreation;
	private StartButton btnAbortDummyCreation;
	private TextArea logTextArea;
	private Stage stage;
	private Controller controller;

	private View() {
	}

	public static View getInstance() {
		return ourInstance;
	}

	public Label getStatus() {
		return status;
	}

	public ProgressBar getProgressBar() {
		return progressBar;
	}

	public DefaultButton getBtnChooseOrigDir() {
		return btnChooseOrigDir;
	}

	public DefaultButton getBtnChooseDummyDir() {
		return btnChooseDummyDir;
	}

	public DefaultButton getBtnReloadSettings() {
		return btnReloadSettings;
	}

	public StartButton getBtnStartDummyCreation() {
		return btnStartDummyCreation;
	}

	public Controller getController() {
		return controller;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}

	public ObservingTextField getTfOrigDirPathInput() {
		return tfOrigDirPathInput;
	}

	public ObservingTextField getTfDummyDirPathInput() {
		return tfDummyDirPathInput;
	}

	public String getOrigPath() {
		return tfOrigDirPathInput.getText();
	}

	public String getDummyPath() {
		return tfDummyDirPathInput.getText();
	}

	public void start(Stage primaryStage) throws Exception {

		controller.init();

		stage = primaryStage;
		root = new VBox();

		scrollPane = new ScrollPane();
		scrollPane.setFitToHeight(true);
		scrollPane.setFitToWidth(true);
		scrollPane.setPrefSize(640, 200);

		statusBar = new HBox();
		statusBar.setPrefHeight(20);
		statusBar.setAlignment(Pos.CENTER_LEFT);


		status = new Label();
		status.setText("Idle");
		status.setAlignment(Pos.CENTER_LEFT);
		status.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

		Separator separator = new Separator(Orientation.VERTICAL);
		separator.setPrefWidth(20);

		progressBar = new ProgressBar(0.5);
		progressBar.setVisible(true);
		progressBar.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		HBox.setHgrow(progressBar, Priority.SOMETIMES);


		statusBar.getChildren().addAll(status, separator, progressBar);
		root.getChildren().addAll(scrollPane, statusBar);
		VBox.setVgrow(scrollPane, Priority.ALWAYS);
		VBox.setVgrow(statusBar, Priority.NEVER);

		scene = new Scene(root);
		scene.getStylesheets().add("/skin.css");
		stage.setTitle("DummyFileCreator");
		stage.setScene(scene);
		//stage.setMinHeight(128);
		//stage.setMinWidth(512);
		primaryStage.setAlwaysOnTop(true);
		stage.show();

		buildContent();
	}

	public StartButton getBtnAbortDummyCreation() {
		return btnAbortDummyCreation;
	}

	private void buildContent() {
// TODO: add button to reload settings

		GridPane gridpane = new GridPane();
		gridpane.setAlignment(Pos.TOP_LEFT);
		ColumnConstraints column1 = new ColumnConstraints();
		//column1.setFillWidth(true);
		column1.setHgrow(Priority.ALWAYS);
		//column1.setPercentWidth(80);
		gridpane.getColumnConstraints().add(column1);

		btnStartDummyCreation = new StartButton();
		btnStartDummyCreation.setDisable(true);
		btnStartDummyCreation.setText("Create Dummies");
		btnStartDummyCreation.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

		btnAbortDummyCreation = new StartButton();
		btnAbortDummyCreation.setText("Abort Creation");
		btnAbortDummyCreation.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		btnAbortDummyCreation.setDisable(true);

		tfOrigDirPathInput = new ObservingTextField();
		tfOrigDirPathInput.setTooltip(new Tooltip("path to the directory of Original Files (Files with a real size)"));

		tfDummyDirPathInput = new ObservingTextField();
		tfDummyDirPathInput.setTooltip(new Tooltip("path to the directory where the created Dummy-Files will be placed (empty Files)"));

		btnChooseOrigDir = new DefaultButton("Select Source");
		btnChooseOrigDir.setMinWidth(10);

		btnChooseDummyDir = new DefaultButton("Select Target");
		btnChooseDummyDir.setMinWidth(10);

		btnReloadSettings = new DefaultButton("Reload Settings");
		btnChooseDummyDir.setMinWidth(10);

		logTextArea = new TextArea();
		logTextArea.setEditable(false);
		logTextArea.setWrapText(true);
		logTextArea.setMaxWidth(Double.MAX_VALUE);
		logTextArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(logTextArea, Priority.ALWAYS);
		GridPane.setHgrow(logTextArea, Priority.ALWAYS);

		gridpane.add(tfOrigDirPathInput, 0, 0);
		gridpane.add(btnChooseOrigDir, 1, 0);
		gridpane.add(tfDummyDirPathInput, 0, 1);
		gridpane.add(btnChooseDummyDir, 1, 1);
		gridpane.add(btnReloadSettings, 0, 2);
		gridpane.add(btnStartDummyCreation, 0, 3);
		gridpane.add(btnAbortDummyCreation, 0, 4);
		gridpane.add(logTextArea, 0, 5, 2, 1);

		scrollPane.setContent(gridpane);
	}

	public Stage getStage() {
		return stage;
	}

	public void appendLog(final String log) {
		Platform.runLater(() -> {
			logTextArea.appendText(log);
			logTextArea.setScrollLeft(0);
			logTextArea.setScrollTop(1);
		});
	}
}
