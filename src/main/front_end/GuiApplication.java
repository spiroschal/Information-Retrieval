package front_end;

import back_end.Indexer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
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
    List<String> results = new ArrayList<>();
    List<String[]> docs_result = new ArrayList<>();

    private int currentPage = 0;
    private static final int PAGE_SIZE = 10;
    private WebView webView;
    private Button prevPageButton;
    private Button nextPageButton;

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
        //ToggleButton toggleButton0 = new ToggleButton("keywords");
        ToggleButton toggleButton1 = new ToggleButton("full_name");
        ToggleButton toggleButton2 = new ToggleButton("institution");
        ToggleButton toggleButton3 = new ToggleButton("year");
        ToggleButton toggleButton4 = new ToggleButton("title");
        ToggleButton toggleButton5 = new ToggleButton("abstract");
        ToggleButton toggleButton6 = new ToggleButton("full_text");

        HBox hbox_toggledButtons = new HBox(10, toggleButton1, toggleButton2, toggleButton3, toggleButton4, toggleButton5, toggleButton6);

        // Initial pressed button
        //toggleButton0.setSelected(true);
        toggleButton1.setSelected(true);
        toggleButton2.setSelected(false);
        toggleButton3.setSelected(false);
        toggleButton4.setSelected(false);
        toggleButton5.setSelected(false);
        toggleButton6.setSelected(false);
        //selectedLabel.setText("Selected: keywords");
        selectedLabel.setText("Selected: full_name");
        //field = "keywords";
        field = "full_name";

        // Set action on toggle buttons
        //toggleButton0.setOnAction(e -> {
        //field = "keywords";

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
        Label greetingLabel = new Label();
        greetingLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        prevPageButton = new Button("⬅️ Previous");
        nextPageButton = new Button("Next ➡️");

        webView = new WebView();

        // Set Button action
        actionButton.setOnAction(e -> {
            query = textField.getText();
            if (!query.isEmpty()) {
                greetingLabel.setText("RESULTS:");
                System.out.println("Confirmed: Your Query Word(s): " + query);
                System.out.println("Confirmed: Your Selected Field: " + selectedLabel.getText());
                if (!history.contains(query)){
                    history.add(query);
                    saveHistory(query);
                    history = loadHistory();
                }
                textField.clear();
                updateFilteredHistory("");

                //if (field == "keywords") {
                // indexer.searchIntexer(documents.size(), index, query, "full_name", analyzer);
                // for (String[] result : docs_result) ????????
                // indexer.searchIntexer(documents.size(), index, query, "institution", analyzer);
                // indexer.searchIntexer(documents.size(), index, query, "year", analyzer);
                // indexer.searchIntexer(documents.size(), index, query, "title", analyzer);
                // indexer.searchIntexer(documents.size(), index, query, "abstract", analyzer);
                // indexer.searchIntexer(documents.size(), index, query, "full_text", analyzer);
                // }

                results.clear();
                // Call 'searchIntexer' function
                try {
                    docs_result = indexer.searchIntexer(documents.size(), index, query, field, analyzer);
                } catch (ParseException | IOException ex) {
                    throw new RuntimeException(ex);
                }
                for (String[] result : docs_result) {
                    // Extract each part and assign it to its respective variable
                    String hit_num = result[0];
                    String author = result[1];
                    String institution = result[2];
                    String year = result[3];
                    String title = result[4];
                    String abstr = result[5];
                    String abstrContent = "<div style='height: 120px; overflow-y: auto;'>" + abstr + "</div>";
                    String papper = result[6];
                    String papperContent = "<div style='height: 120px; overflow-y: auto;'>" + papper + "</div>";

//                    System.out.println("--"+result.length);
//                    System.out.println(".."+hit_num);
//                    System.out.println(".."+author);
//                    System.out.println(".."+institution);
//                    System.out.println(".."+year);
//                    System.out.println(".."+title);
//                    System.out.println(".."+abstr);
//                    System.out.println(".."+papper);
//                    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

                    // Highlight "query"
                    if (field.equals("full_name")) {
                        String highlightedAuthorQuery = author.replace(query, "<span style='background-color: yellow;'>"+query+"</span>");
                        results.add("<center><u><b>Result: "+hit_num+"</b></u></center>"+"<br><b>Author: </b>"+highlightedAuthorQuery+"<br><b>Institution: </b>"+institution+"<br><b>Year: </b>"+year+"<br><b>Title: </b>"+title+"<br><br><b>Abstract: </b>"+abstrContent+"<br><b>Papper: </b>"+papperContent);
                    }
                    else if (field.equals("institution")) {
                        String highlightedQuery = institution.replace(query, "<span style='background-color: yellow;'>"+query+"</span>");
                    }
                    else if (field.equals("year")) {
                        String highlightedQuery = year.replace(query, "<span style='background-color: yellow;'>"+query+"</span>");
                    }
                    else if (field.equals("title")) {
                        String highlightedQuery = title.replace(query, "<span style='background-color: yellow;'>"+query+"</span>");
                    }
                    else if (field.equals("abstract")) {
                        String highlightedQuery = abstr.replace(query, "<span style='background-color: yellow;'>"+query+"</span>");
                    }
                    else if (field.equals("full_text")) {
                        String highlightedQuery = papper.replace(query, "<span style='background-color: yellow;'>"+query+"</span>");
                    }
                    //else if (field.equals("keywords"))
                }
                currentPage = 0;
                displayResults();
            } else {
                greetingLabel.setText("You didn't write any Query Word(s) !!!");
                System.out.println("Confirmed: You don't write any Query Word(s) !!!" + query);
                System.out.println("Confirmed: " + selectedLabel.getText());
            }
            System.out.println();
        });

        // Set button actions for pagination
        prevPageButton.setOnAction(event -> {
            if (currentPage > 0) {
                currentPage--;
                displayResults();
            }
        });

        nextPageButton.setOnAction(event -> {
            if ((currentPage + 1) * PAGE_SIZE < results.size()) {
                currentPage++;
                displayResults();
            }
        });

        HBox paginationBox = new HBox(10, prevPageButton, nextPageButton);
        // Create a VBox layout to hold the WebView
        VBox webViewBox = new VBox();
        webViewBox.setAlignment(Pos.CENTER);
        webViewBox.getChildren().addAll(webView, paginationBox);

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

        // Create a VBox layout and add the controls
        VBox vbox = new VBox(10, selectedLabel, hbox_toggledButtons, hbox, historyLabel, historyListView, loadingIndicator, greetingLabel);
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setStyle("-fx-padding: 20;");
        //VBox.setVgrow(hbox, Priority.ALWAYS);
        vbox.setSpacing(10);

        // Create a SplitPane to arrange the content
        SplitPane splitPane = new SplitPane(vbox, webViewBox);
        splitPane.setPadding(new Insets(3, 0, 3, 0));
        splitPane.setOrientation(Orientation.VERTICAL);

        // Create a Scene
        Scene scene = new Scene(splitPane, 900, 700);

        // Set the Stage
        primaryStage.setTitle("Search Engine App");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png"))); // Load the icon image
        primaryStage.setScene(scene);
        primaryStage.show();

        // Create a Task to run 'makeIntexer' asynchronously
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws IOException {
                actionButton.setDisable(true);

                // Call 'makeIntexer' function
                documents = indexer.makeIntexer(w, csvFile);
                return null;
            }

            @Override
            protected void succeeded() {
                loadingIndicator.setVisible(false); // Hide the loading indicator when done

                // Enable the action button
                actionButton.setDisable(false);

                System.out.println("Confirmed: Successful run of the \'makeIntexer\' function");
            }

            @Override
            protected void failed() {
                loadingIndicator.setVisible(false); // Hide the loading indicator if failed

                // Enable the action button
                actionButton.setDisable(false);

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

    private void displayResults() {
        StringBuilder content = new StringBuilder();
        int start = currentPage * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, results.size());
        for (int i = start; i < end; i++) {
            content.append(results.get(i)).append("<br/><hr/>");
        }
        webView.getEngine().loadContent("<html><body>" + content.toString() + "</body></html>");

        // Enable or disable buttons based on the current page
        prevPageButton.setDisable(currentPage == 0);
        //nextPageButton.setDisable((currentPage + 1) * PAGE_SIZE >= results.size());
        nextPageButton.setDisable(end >= results.size());
    }

    public static void main(String[] args) {
        launch(args);
    }
}

