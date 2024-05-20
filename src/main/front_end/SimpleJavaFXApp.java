package front_end;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SimpleJavaFXApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create a Label
        Label label = new Label("Enter your name:");

        // Create a TextField
        TextField textField = new TextField();

        // Create a Button
        Button button = new Button("Say Hello");

        // Create a Label to display the greeting
        Label greetingLabel = new Label();

        // Set Button action
        button.setOnAction(e -> {
            String name = textField.getText();
            if (!name.isEmpty()) {
                greetingLabel.setText("Hello, " + name + "!");
            } else {
                greetingLabel.setText("Hello, World!");
            }
        });

        // Create a VBox layout and add the controls
        VBox vbox = new VBox(10, label, textField, button, greetingLabel);

        // Create a Scene
        Scene scene = new Scene(vbox, 300, 200);

        // Set the Stage
        primaryStage.setTitle("Simple JavaFX App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

