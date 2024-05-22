package front_end;

import back_end.Indexer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class GuiApplication extends Application {

    private static final String HISTORY_FILE = "history.txt";
    private List<String> history = new ArrayList<>();
    private ObservableList<String> filteredHistory = FXCollections.observableArrayList();
    private ProgressIndicator loadingIndicator;

    private String csvFile = "C:\\Users\\user\\IdeaProjects\\Information-Retrieval\\src\\main\\corpus\\corpus.csv";
    private StandardAnalyzer analyzer = new StandardAnalyzer();
    private Directory index = new ByteBuffersDirectory();
    private IndexWriterConfig config = new IndexWriterConfig(analyzer);
    private IndexWriter w;
    {
        try {
            w = new IndexWriter(index, config);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String field = "";
    private String query = "";

    private Indexer indexer = new Indexer();
    List<Document> documents = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Load history from file
        history = loadHistory();
        filteredHistory.addAll(history);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // Create a loading indicator
        loadingIndicator = new ProgressIndicator();
        loadingIndicator.setPrefSize(50, 50);
        loadingIndicator.setVisible(true);

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

        // Initial pressed button
        toggleButton1.setSelected(true);
        toggleButton2.setSelected(false);
        toggleButton3.setSelected(false);
        toggleButton4.setSelected(false);
        toggleButton5.setSelected(false);
        toggleButton6.setSelected(false);
        selectedLabel.setText("Selected: full_name");
        field = "full_name";

        // Set action on toggle buttons
        toggleButton1.setOnAction(e -> {
            if (toggleButton1.isSelected()) {
                selectedLabel.setText("Selected: full_name");
                toggleButton2.setSelected(false);
                toggleButton3.setSelected(false);
                toggleButton4.setSelected(false);
                toggleButton5.setSelected(false);
                toggleButton6.setSelected(false);
                field = "full_name";
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
                field = "institution";
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
                field = "year";
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
                field = "title";
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
                field = "abstract";
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
                field = "full_text";
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
        Label greetingLabel = new Label();/////////////////////////////////////////
        greetingLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Set Button action
        actionButton.setOnAction(e -> {
            query = textField.getText();
            if (!query.isEmpty()) {
                greetingLabel.setText("RESULTS:");/////////////////////////////////////////
                System.out.println("Confirmed: Your Query Word(s): " + query);
                System.out.println("Confirmed: Your Selected Field: " + selectedLabel.getText());
                if (!history.contains(query)){
                    history.add(query);
                    saveHistory(query);
                    history = loadHistory();
                }
                textField.clear();
                updateFilteredHistory("");

                // Call 'searchIntexer' function
                indexer.searchIntexer(documents.size(), index, query, field, analyzer);
            } else {
                greetingLabel.setText("You don't write any Query Word(s) !!!");/////////////////////////////////////////
                System.out.println("Confirmed: You don't write any Query Word(s) !!!" + query);
                System.out.println("Confirmed: " + selectedLabel.getText());
            }
            System.out.println();
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

        // Create a Task to run 'makeIntexer' asynchronously
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws IOException {
                // Call 'makeIntexer' function
                documents = indexer.makeIntexer(w, csvFile);
                return null;
            }

            @Override
            protected void succeeded() {
                loadingIndicator.setVisible(false); // Hide the loading indicator when done
                System.out.println("Confirmed: Successful run of the \'makeIntexer\' function");
            }

            @Override
            protected void failed() {
                loadingIndicator.setVisible(false); // Hide the loading indicator if failed
                System.out.println("Confirmed: Failed to run the \'makeIntexer\' function");
            }
        };

        // Start the Task in a new Thread
        new Thread(task).start();
    }

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

