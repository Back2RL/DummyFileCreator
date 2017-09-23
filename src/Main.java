import a_presentation.controller.Controller;
import a_presentation.model.Model;
import a_presentation.view.View;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * TODO:
 * -   Buttons Observer-based
 * -   change Color of invalid paths
 * -   allow dragging of Directories onto text-field in order to get the path
 * -
 */

public class Main extends Application {


	public static void main(String[] args) {

		final Controller controller = Controller.getInstance();
		final View view = View.getInstance();

		controller.setView(view);
		controller.setModel(new Model());
		view.setController(controller);

		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		View.getInstance().start(primaryStage);
	}
}
