package front_end;

import back_end.Indexer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class GuiApplication extends Application {

    private static final String HISTORY_FILE = "history.txt";
    private List<String> history = new ArrayList<>();
    private ObservableList<String> filteredHistory = FXCollections.observableArrayList();
    private ProgressBar progressBar;

    private ProgressIndicator loadingIndicator;


    //private Indexer indexer = new Indexer();

    @Override
    public void start(Stage primaryStage) {
        // Load history from file
        history = loadHistory();
        filteredHistory.addAll(history);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // Create a loading indicator
        loadingIndicator = new ProgressIndicator();
        loadingIndicator.setPrefSize(50, 50);
        loadingIndicator.setVisible(false);

        // Fetch additional data from the backend
        new Thread(() -> {
            loadingIndicator.setVisible(true);
            List<String> backendData = fetchDataFromBackend();
            history.addAll(backendData);

            // Remove duplicates and sort if needed
            history = history.stream().distinct().collect(Collectors.toList());

            filteredHistory.setAll(history);
            loadingIndicator.setVisible(false);
        }).start();

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // Label to display selected option
        Label selectedLabel = new Label("Select an option:");

        // Create a TextField
        TextField textField = new TextField();
        textField.setPromptText("Write something here...");
        //VBox.setVgrow(textField, Priority.ALWAYS);

        // Create ToggleButtons
        ToggleButton toggleButton1 = new ToggleButton("full_name");
        ToggleButton toggleButton2 = new ToggleButton("institution");
        ToggleButton toggleButton3 = new ToggleButton("year");
        ToggleButton toggleButton4 = new ToggleButton("title");
        ToggleButton toggleButton5 = new ToggleButton("abstract");
        ToggleButton toggleButton6 = new ToggleButton("full_text");

        HBox hbox_toggledButtons = new HBox(10, toggleButton1, toggleButton2, toggleButton3, toggleButton4, toggleButton5, toggleButton6);

        toggleButton1.setSelected(true);

        // Set action on toggle buttons
        toggleButton1.setOnAction(e -> {
            if (toggleButton1.isSelected()) {
                selectedLabel.setText("Selected: full_name");
                toggleButton2.setSelected(false);
                toggleButton3.setSelected(false);
                toggleButton4.setSelected(false);
                toggleButton5.setSelected(false);
                toggleButton6.setSelected(false);
                //List<String> docs_result = indexer.searchIntexer("full_name", textField.getText());
            } else {
                selectedLabel.setText("Select an option:");
            }
        });

        toggleButton2.setOnAction(e -> {
            if (toggleButton2.isSelected()) {
                selectedLabel.setText("Selected: institution");
                toggleButton1.setSelected(false);
                toggleButton3.setSelected(false);
                toggleButton4.setSelected(false);
                toggleButton5.setSelected(false);
                toggleButton6.setSelected(false);
            } else {
                selectedLabel.setText("Select an option:");
            }
        });

        toggleButton3.setOnAction(e -> {
            if (toggleButton3.isSelected()) {
                selectedLabel.setText("Selected: year");
                toggleButton1.setSelected(false);
                toggleButton2.setSelected(false);
                toggleButton4.setSelected(false);
                toggleButton5.setSelected(false);
                toggleButton6.setSelected(false);
            } else {
                selectedLabel.setText("Select an option:");
            }
        });

        toggleButton4.setOnAction(e -> {
            if (toggleButton4.isSelected()) {
                selectedLabel.setText("Selected: title");
                toggleButton1.setSelected(false);
                toggleButton2.setSelected(false);
                toggleButton3.setSelected(false);
                toggleButton5.setSelected(false);
                toggleButton6.setSelected(false);
            } else {
                selectedLabel.setText("Select an option:");
            }
        });

        toggleButton5.setOnAction(e -> {
            if (toggleButton5.isSelected()) {
                selectedLabel.setText("Selected: abstract");
                toggleButton1.setSelected(false);
                toggleButton2.setSelected(false);
                toggleButton3.setSelected(false);
                toggleButton4.setSelected(false);
                toggleButton6.setSelected(false);
            } else {
                selectedLabel.setText("Select an option:");
            }
        });

        toggleButton6.setOnAction(e -> {
            if (toggleButton6.isSelected()) {
                selectedLabel.setText("Selected: full_text");
                toggleButton1.setSelected(false);
                toggleButton2.setSelected(false);
                toggleButton3.setSelected(false);
                toggleButton4.setSelected(false);
                toggleButton5.setSelected(false);
            } else {
                selectedLabel.setText("Select an option:");
            }
        });

        Label historyLabel = new Label("HISTORY - you can press any value you want");

        // ListView to display history
        ListView<String> historyListView = new ListView<>(filteredHistory);
        historyListView.setPrefHeight(120);

        // Create a Button
        Button actionButton = new Button("Search");
        actionButton.setStyle("-fx-background-color: blue; -fx-text-fill: white;");

        // Create a Label to display the greeting
        Label greetingLabel = new Label();

        // Set Button action
        actionButton.setOnAction(e -> {
            String name = textField.getText();
            if (!name.isEmpty()) {
                greetingLabel.setText("Hello, " + name + "!");
                System.out.println("Confirmed: " + selectedLabel.getText());
                if (!history.contains(name)){
                    history.add(name);
                    saveHistory(name);
                    history = loadHistory();
                }
                textField.clear();
                updateFilteredHistory("");
            } else {
                greetingLabel.setText("Hello, World!");
                System.out.println("Confirmed: " + selectedLabel.getText());
            }
        });

        // HBox for the text placeholder and button
        HBox hbox = new HBox(10,textField, actionButton);
        hbox.setStyle("-fx-alignment: center-left; -fx-padding: 10;");
        HBox.setHgrow(textField, Priority.ALWAYS);

        // Handle click on history item
        historyListView.setOnMouseClicked(event -> {
            String selectedItem = historyListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                textField.setText(selectedItem);
                saveHistory(selectedItem);
                history = loadHistory();
            }
        });

        // Filter history based on current text input
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateFilteredHistory(newValue);
        });

        SplitPane splitPane = new SplitPane();
        splitPane.setPadding(new Insets(3, 0, 3, 0));

        // Create a VBox layout and add the controls
        VBox vbox = new VBox(10, selectedLabel, hbox_toggledButtons, hbox, historyLabel, historyListView, splitPane, greetingLabel, loadingIndicator);
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setStyle("-fx-padding: 20;");
        //VBox.setVgrow(hbox, Priority.ALWAYS);
        vbox.setSpacing(10);

        // Create a Scene
        Scene scene = new Scene(vbox, 600, 700);

        // Set the Stage
        primaryStage.setTitle("Search Engine App");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png"))); // Load the icon image
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Backend method to get data
    public List<String> fetchDataFromBackend() {
        // Example backend data fetching logic
        List<String> data = new ArrayList<>();
        // Simulate data fetching with sleep
        try {
            Thread.sleep(2000); // Simulate network delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //data.add("Backend Item 1");
        //data.add("Backend Item 2");

        // Add actual backend data fetching logic here
        return data;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Load history from file
    private List<String> loadHistory() {
        List<String> historyItems = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(HISTORY_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                historyItems.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return historyItems;
    }

    // Save history to file
    private void saveHistory(String text) {
        List<String> existingHistory = loadHistory();

        // Remove the existing entry if it already exists
        existingHistory.removeIf(existingText -> existingText.equals(text));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HISTORY_FILE))) {
            writer.write(text);
            writer.newLine();

            // Write existing history after the new text
            for (String line : existingHistory) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Update filtered history based on current text input
    private void updateFilteredHistory(String filter) {
        filteredHistory.setAll(
                history.stream()
                        .filter(item -> item.contains(filter))
                        .collect(Collectors.toList())
        );
    }

    public static void main(String[] args) {
        launch(args);
    }
}

