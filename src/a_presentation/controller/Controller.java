package a_presentation.controller;

import a_presentation.model.Model;
import a_presentation.view.ErrorDialog;
import a_presentation.view.View;
import b_logic.LogicBoundary;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.stage.DirectoryChooser;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller {
	private static Controller ourInstance = new Controller();
	private View view = null;

	private void setInputsDisabled(final boolean disabled) {
		Platform.runLater(() -> {
			view.getBtnStartDummyCreation().setDisable(disabled);
			view.getBtnChooseOrigDir().setDisable(disabled);
			view.getBtnChooseDummyDir().setDisable(disabled);
			view.getBtnReloadSettings().setDisable(disabled);
			view.getTfOrigDirPathInput().setDisable(disabled);
			view.getTfDummyDirPathInput().setDisable(disabled);
			view.getBtnAbortDummyCreation().setDisable(!disabled);
		});
	}

	EventHandler<ActionEvent> abortHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(final ActionEvent event) {
			if (job != null) {
				job.interrupt();
				Platform.runLater(() -> view.getStatus().setText("Aborted"));
			}
		}
	};
	private Thread job = null;
	EventHandler<ActionEvent> startHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(final ActionEvent event) {
			setInputsDisabled(true);
			job = new Thread(() -> {
				Platform.runLater(() -> view.getStatus().setText("Creating Dummies..."));
				try {
					Thread.sleep(10000); // TODO: remove this again (simulating work)
					if (LogicBoundary.createDummies()) {
						Platform.runLater(() -> view.getStatus().setText("Dummy-Creation finished"));
					} else {
						Platform.runLater(() -> view.getStatus().setText("Dummy-Creation failed!"));
					}

				} catch (InterruptedException e) {
					Logger.getGlobal().log(Level.INFO, "Aborted Creation of Dummies");
				} finally {
					job = null;
					setInputsDisabled(false);
				}
			});
			job.start();
		}
	};
	private Model model = null;
	EventHandler<ActionEvent> orignalsChooseEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(final ActionEvent event) {
			DirectoryChooser directoryChooser = buildDirectoryChooser();
			directoryChooser.setTitle("Choose the Directory that contains the original Files");
			File dir = directoryChooser.showDialog(view.getStage());
			if (dir != null) LogicBoundary.setLastBrowserDir(dir.getParentFile());
			LogicBoundary.setOriginalsDir(dir);
			model.setOriginalsDir(LogicBoundary.getCurrentSettings().getOriginalsDir());
		}
	};
	EventHandler<ActionEvent> dummiesChooseEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(final ActionEvent event) {
			DirectoryChooser directoryChooser = buildDirectoryChooser();
			directoryChooser.setTitle("Choose the Directory that shall be the Target for the Dummy-Files");
			File dir = directoryChooser.showDialog(view.getStage());
			if (dir != null) LogicBoundary.setLastBrowserDir(dir.getParentFile());
			LogicBoundary.setDummiesDir(dir);
			model.setDummiesDir(LogicBoundary.getCurrentSettings().getDummiesDir());
		}
	};
	EventHandler<ActionEvent> reloadHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(final ActionEvent event) {
			new Thread(() -> {
				loadSettings();
				Platform.runLater(() -> view.getStatus().setText("Settings reloaded"));
			}).start();
		}
	};
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
	private ChangeListener<String> originalsStringListener = new ChangeListener<String>() {
		@Override
		public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
			if (LogicBoundary.setOriginalsDir(new File(view.getOrigPath()))) {
				Platform.runLater(() -> view.getStatus().setText("Valid Originals-Directory"));
			} else {
				Platform.runLater(() -> view.getStatus().setText("Invalid Originals-Directory"));
			}
			model.setOriginalsDir(LogicBoundary.getCurrentSettings().getOriginalsDir());
		}
	};
	private ChangeListener<String> dummiesStringListener = new ChangeListener<String>() {
		@Override
		public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
			if (LogicBoundary.setDummiesDir(new File(view.getDummyPath()))) {
				Platform.runLater(() -> view.getStatus().setText("Valid Dummies-Directory"));
			} else {
				Platform.runLater(() -> view.getStatus().setText("Invalid Dummies-Directory"));
			}
			model.setDummiesDir(LogicBoundary.getCurrentSettings().getDummiesDir());
		}
	};

	private Controller() {
	}

	public static Controller getInstance() {
		return ourInstance;
	}

	private Observer jobObserver = new Observer() {
		@Override
		public void update(final Observable o, final Object arg) {

		}
	};

	private Observer modelObserver = new Observer() {
		@Override
		public void update(final Observable o, final Object arg) {
			Platform.runLater(() -> {
				boolean bCanCreateDummies = true;
				if (model.getDummiesDir() == null) {
					bCanCreateDummies = false;
					view.getTfDummyDirPathInput().setId("invaliddir");
				} else {
					view.getTfDummyDirPathInput().setId("validdir");
					view.getTfDummyDirPathInput().setText(model.getDummiesDir().getPath());
				}
				if (model.getOriginalsDir() == null) {
					bCanCreateDummies = false;
					view.getTfOrigDirPathInput().setId("invaliddir");
				} else {
					view.getTfOrigDirPathInput().setId("validdir");
					view.getTfOrigDirPathInput().setText(model.getOriginalsDir().getPath());
				}
				view.getBtnStartDummyCreation().setDisable(!bCanCreateDummies);
			});
		}
	};


	DirectoryChooser buildDirectoryChooser() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		File lastBrowserDir = LogicBoundary.getCurrentSettings().getLastBrowserDir();
		if (lastBrowserDir != null && lastBrowserDir.exists() && lastBrowserDir.isDirectory()) {
			directoryChooser.setInitialDirectory(lastBrowserDir);
		}
		return directoryChooser;
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
		Platform.runLater(() -> {
			view.getStage().setOnCloseRequest(windowCloseHandler);
			view.getTfOrigDirPathInput().textProperty().addListener(originalsStringListener);
			view.getTfDummyDirPathInput().textProperty().addListener(dummiesStringListener);

			view.getBtnChooseOrigDir().setOnAction(orignalsChooseEventHandler);
			view.getBtnChooseDummyDir().setOnAction(dummiesChooseEventHandler);
			view.getBtnStartDummyCreation().setOnAction(startHandler);
			view.getBtnAbortDummyCreation().setOnAction(abortHandler);
			view.getBtnReloadSettings().setOnAction(reloadHandler);

		});
		model.addObserver(modelObserver);
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
