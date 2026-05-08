package dev.maajid.stacksage;

import dev.maajid.stacksage.ui.StackSageView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        StackSageView view = new StackSageView();
        Scene scene = new Scene(view.create(), 1120, 760);
        scene.getStylesheets().add(App.class.getResource("/styles/app.css").toExternalForm());

        stage.setTitle("StackSage JVM");
        stage.setMinWidth(880);
        stage.setMinHeight(620);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
