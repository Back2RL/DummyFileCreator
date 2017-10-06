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
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Controller {
	private static Controller ourInstance = new Controller();
	private View view;
	private Thread job;
	private Model model;

	private EventHandler<ActionEvent> abortHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(final ActionEvent event) {
			if (job != null) {
				job.interrupt();
				Platform.runLater(() -> view.getStatus().setText("Aborted"));
			}
		}
	};
	private EventHandler<ActionEvent> startHandler = new EventHandler<ActionEvent>() {
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
	private EventHandler<ActionEvent> orignalsChooseEventHandler = new EventHandler<ActionEvent>() {
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
	private EventHandler<ActionEvent> dummiesChooseEventHandler = new EventHandler<ActionEvent>() {
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
	private EventHandler<ActionEvent> reloadHandler = new EventHandler<ActionEvent>() {
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
			Platform.runLater(() -> view.getComboBoxOriginal().show());
		}
	};
	private ChangeListener<String> dummiesStringListener = new ChangeListener<String>() {
		@Override
		public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
			Platform.runLater(() -> view.getComboBoxDummy().show());
		}
	};
	private Observer jobObserver = new Observer() {
		@Override
		public void update(final Observable o, final Object arg) {

		}
	};
	private Observer modelObserver = new Observer() {
		@Override
		public void update(final Observable o, final Object arg) {
			Platform.runLater(() -> {
				boolean OK = true;
				if (model.getDummiesDir() == null || !model.getDummiesDir().exists()) {
					OK = false;
				} else {
					//view.getComboBoxDummy().getEditor().setText(model.getDummiesDir().getPath());
					view.getComboBoxDummy().getItems().clear();
					for (File file : LogicBoundary.getCurrentSettings().getDummyHistory()) {
						view.getComboBoxDummy().getItems().add(file.getPath());
						Logger.getGlobal().log(Level.INFO, "added " + file.getPath() + " to Dropdown");
					}
					Logger.getGlobal().log(Level.INFO, "added Dummy History to Dropdown: "
							+ LogicBoundary.getCurrentSettings().getDummyHistory().size());
				}
				if (model.getOriginalsDir() == null || !model.getOriginalsDir().exists()) {
					OK = false;
				} else {
					view.getComboBoxOriginal().getEditor().setText(model.getOriginalsDir().getPath());
				}
				view.getBtnStartDummyCreation().setDisable(!OK);
			});
		}
	};
	private ChangeListener<Boolean> originalFocusListener = new ChangeListener<Boolean>() {
		@Override
		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
			Logger.getGlobal().log(Level.INFO, "Original " + (newValue ? "focused" : "lost focus"));
			if (newValue)
				Platform.runLater(() -> {
					view.getComboBoxOriginal().show();
					view.getComboBoxOriginal().getEditor().textProperty().addListener(originalsStringListener);
				});

			else {
				Platform.runLater(() -> {
					view.getComboBoxOriginal().hide();
					view.getComboBoxOriginal().getEditor().textProperty().removeListener(originalsStringListener);
				});
				if (LogicBoundary.setOriginalsDir(new File(view.getComboBoxOriginal().getEditor().getText()))) {
					Platform.runLater(() -> view.getStatus().setText("Valid Originals-Directory"));
				} else {
					Platform.runLater(() -> view.getStatus().setText("Invalid Originals-Directory"));
				}
				model.setOriginalsDir(LogicBoundary.getCurrentSettings().getOriginalsDir());
			}
		}
	};
	private ChangeListener<Boolean> dummyFocusListener = new ChangeListener<Boolean>() {
		@Override
		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
			Logger.getGlobal().log(Level.INFO, "Dummy " + (newValue ? "focused" : "lost focus"));
			if (newValue)
				Platform.runLater(() -> view.getComboBoxDummy().show());
			else {
				Platform.runLater(() -> view.getComboBoxDummy().hide());
				if (LogicBoundary.setDummiesDir(new File(view.getComboBoxDummy().getEditor().getText()))) {
					Platform.runLater(() -> view.getStatus().setText("Valid Dummy-Directory"));
				} else {
					Platform.runLater(() -> view.getStatus().setText("Invalid Dummy-Directory"));
				}
				model.setDummiesDir(LogicBoundary.getCurrentSettings().getDummiesDir());
			}
		}
	};

	private Controller() {
		Logger.getGlobal().addHandler(new Handler() {
			@Override
			public void publish(final LogRecord record) {
				appendLog(LocalDateTime.now() + " : "
						+ record.getLevel().getLocalizedName() + " : "
						+ record.getMessage() + "\n");
			}

			@Override
			public void flush() {
			}

			@Override
			public void close() throws SecurityException {
			}
		});
	}

	public void init() {
		model.addObserver(modelObserver);
		Platform.runLater(() -> {
			view.getStage().setOnCloseRequest(windowCloseHandler);
			view.getComboBoxOriginal().focusedProperty().addListener(originalFocusListener);
			view.getComboBoxDummy().focusedProperty().addListener(dummyFocusListener);

			view.getBtnChooseOrigDir().setOnAction(orignalsChooseEventHandler);
			view.getBtnChooseDummyDir().setOnAction(dummiesChooseEventHandler);
			view.getBtnStartDummyCreation().setOnAction(startHandler);
			view.getBtnAbortDummyCreation().setOnAction(abortHandler);
			view.getBtnReloadSettings().setOnAction(reloadHandler);
			loadSettings();
		});
	}

	public static Controller getInstance() {
		return ourInstance;
	}

	private void setInputsDisabled(final boolean disabled) {
		Platform.runLater(() -> {
			view.getBtnStartDummyCreation().setDisable(disabled);
			view.getBtnChooseOrigDir().setDisable(disabled);
			view.getBtnChooseDummyDir().setDisable(disabled);
			view.getBtnReloadSettings().setDisable(disabled);
			view.getComboBoxOriginal().setDisable(disabled);
			view.getComboBoxDummy().setDisable(disabled);
			view.getBtnAbortDummyCreation().setDisable(!disabled);
		});
	}

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

	public void appendLog(final String log) {
		Platform.runLater(() -> {
			view.getLogTextArea().appendText(log);
			view.getLogTextArea().setScrollTop(1);
		});
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
