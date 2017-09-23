package a_presentation.view;

import a_presentation.controller.Controller;
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

public class View {

	private Stage stage = null;

	public Controller getController() {
		return controller;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}

	private Controller controller = null;

	private static View ourInstance = new View();

	public static View getInstance() {
		return ourInstance;
	}

	private View() {
	}

	VBox root = null;
	ScrollPane scrollPane = null;
	HBox statusBar = null;
	Label status = null;
	ProgressBar progressBar = null;
	Scene scene = null;

	public void start(Stage primaryStage) throws Exception {

		controller.init();

		stage = primaryStage;
		root = new VBox();

		scrollPane = new ScrollPane();
		scrollPane.setFitToHeight(true);
		scrollPane.setFitToWidth(true);

		statusBar = new HBox();
		statusBar.setMinWidth(512);
		statusBar.setAlignment(Pos.BOTTOM_LEFT);

		status = new Label();
		status.setText("Idle");
		status.setAlignment(Pos.BOTTOM_LEFT);

		progressBar = new ProgressBar(0.5);
		progressBar.setVisible(false);

		statusBar.getChildren().addAll(status, progressBar);
		root.getChildren().addAll(scrollPane, statusBar);
		VBox.setVgrow(scrollPane, Priority.ALWAYS);

		scene = new Scene(root);
		stage.setTitle("DummyFileCreator");
		stage.setScene(scene);
		stage.setMinHeight(128);
		stage.setMinWidth(512);
		//primaryStage.setAlwaysOnTop(true);
		stage.show();

		buildContent();
	}

	public void addOnWindowCloseEventHandler(EventHandler<WindowEvent> windowEventEventHandler) {
		Platform.runLater(() -> stage.setOnCloseRequest(windowEventEventHandler));
	}

	private void buildContent() {
		Platform.runLater(() -> {
// TODO: add button to reload settings

					GridPane gridpane = new GridPane();
					gridpane.setAlignment(Pos.TOP_LEFT);
					ColumnConstraints column1 = new ColumnConstraints();
					column1.setPercentWidth(80);
					//ColumnConstraints column2 = new ColumnConstraints();
					//column2.setPercentWidth(20);
					gridpane.getColumnConstraints().add(column1);

					StartButton btnStartDummyCreation = new StartButton();
					btnStartDummyCreation.setText("Create Dummies");
					btnStartDummyCreation.setMinWidth(100);

					AutoSelectingTextField tfOrigDirPathInput = new AutoSelectingTextField();
					if (LogicBoundary.getCurrentSettings().getOriginalsDir() != null) {
						tfOrigDirPathInput.setText(LogicBoundary.getCurrentSettings().getOriginalsDir().getPath());
					}
					tfOrigDirPathInput.textProperty().addListener(new ChangeListener<String>() {
						@Override
						public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
							if (LogicBoundary.setOriginalsDir(new File(tfOrigDirPathInput.getText()))) {
								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										status.setText("Valid Originals-Directory");
									}
								});
							} else {
								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										status.setText("Invalid Originals-Directory");
									}
								});
							}
						}
					});
					tfOrigDirPathInput.setTooltip(new Tooltip("path to the directory of Original Files (Files with a real size)"));

					AutoSelectingTextField tfDummyDirPathInput = new AutoSelectingTextField();
					if (LogicBoundary.getCurrentSettings().getDummiesDir() != null) {
						tfDummyDirPathInput.setText(LogicBoundary.getCurrentSettings().getDummiesDir().getPath());
					}
					tfDummyDirPathInput.textProperty().addListener(new ChangeListener<String>() {
						@Override
						public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
							if (LogicBoundary.setDummiesDir(new File(tfDummyDirPathInput.getText()))) {
								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										status.setText("Valid Dummies-Directory");
									}
								});
							} else {
								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										status.setText("Invalid Dummies-Directory");
									}
								});
							}
						}

					});
					tfDummyDirPathInput.setTooltip(new Tooltip("path to the directory where the created Dummy-Files will be placed (empty Files)"));

					Button btnChooseOrigDir = new Button("Select");
					btnChooseOrigDir.setMinWidth(100);
					btnChooseOrigDir.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(final ActionEvent event) {
							DirectoryChooser directoryChooser = new DirectoryChooser();
							File lastBrowserDir = LogicBoundary.getCurrentSettings().getLastBrowserDir();
							if (lastBrowserDir != null && lastBrowserDir.exists() && lastBrowserDir.isDirectory()) {
								directoryChooser.setInitialDirectory(lastBrowserDir);
							}
							directoryChooser.setTitle("Choose the Directory that contains the original Files");
							File dir = directoryChooser.showDialog(stage);
							if (dir != null) {
								LogicBoundary.setOriginalsDir(dir);
								LogicBoundary.setLastBrowserDir(dir.getParentFile());
								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										tfOrigDirPathInput.setText(dir.getAbsolutePath());
									}
								});
							}
						}
					});

					Button btnChooseDummyDir = new Button("Select");
					btnChooseDummyDir.setMinWidth(100);
					btnChooseDummyDir.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(final ActionEvent event) {
							DirectoryChooser directoryChooser = new DirectoryChooser();
							File lastBrowserDir = LogicBoundary.getCurrentSettings().getLastBrowserDir();
							if (lastBrowserDir != null && lastBrowserDir.exists() && lastBrowserDir.isDirectory()) {
								directoryChooser.setInitialDirectory(lastBrowserDir);
							}
							directoryChooser.setTitle("Choose the Directory that shall be the Target for the Dummy-Files");
							File dir = directoryChooser.showDialog(stage);
							if (dir != null) {
								LogicBoundary.setDummiesDir(dir);
								LogicBoundary.setLastBrowserDir(dir.getParentFile());
								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										tfDummyDirPathInput.setText(dir.getAbsolutePath());
									}
								});
							}
						}
					});


					btnStartDummyCreation.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(final ActionEvent event) {
							new Thread(new Runnable() {
								@Override
								public void run() {
									Platform.runLater(new Runnable() {
										@Override
										public void run() {
											status.setText("creating Dummies...");
										}
									});
									if (LogicBoundary.createDummies()) {
										Platform.runLater(new Runnable() {
											@Override
											public void run() {
												status.setText("Dummy-Creation finished");
											}
										});
									} else {
										Platform.runLater(new Runnable() {
											@Override
											public void run() {
												status.setText("Dummy-Creation failed");
											}
										});
									}
								}
							}).start();
						}
					});

					gridpane.add(tfOrigDirPathInput, 0, 0);
					gridpane.add(tfDummyDirPathInput, 0, 1);
					gridpane.add(btnChooseOrigDir, 1, 0);
					gridpane.add(btnChooseDummyDir, 1, 1);
					gridpane.add(btnStartDummyCreation, 0, 2);
					scrollPane.setContent(gridpane);
				}
		);
	}
}
