package grash.core;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

// VM Options: --module-path [FX-PATH] --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.media -Djavafx.animation.fullspeed=true
// GitHub Repo: https://github.com/BlackBlocky/Grash

public final class Main extends Application {

    public static final String WORKING_DIRECTORY = System.getProperty("user.dir");

    public static final double SCREEN_WIDTH = 1600;
    public static final double SCREEN_HEIGHT = 900;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        primaryStage.setWidth(SCREEN_WIDTH);
        primaryStage.setHeight(SCREEN_HEIGHT);
        primaryStage.setResizable(false);
        //primaryStage.setScene(new Scene(new VBox(), 300, 200, Color.WHITE));
        primaryStage.initStyle(StageStyle.DECORATED);
        primaryStage.setTitle("Grash");
        //primaryStage.show();

        GameController controller = new GameController(primaryStage);

//        FXMLLoader loader = new FXMLLoader();
//        loader.setLocation(new URL("file:///" + WORKING_DIRECTORY + "\\assets\\fxml\\hello-world.fxml"));
//        VBox vbox = loader.<VBox>load();
    }
}