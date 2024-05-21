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
import javafx.scene.web.WebView;
import javafx.stage.Modality;
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

    private Indexer myFunctionClass = new Indexer();
    private List<String> results = new ArrayList<>();
    private int currentPage = 0;
    private static final int PAGE_SIZE = 10;
    private WebView webView;
    private Button prevPageButton;
    private Button nextPageButton;

    @Override
    public void start(Stage primaryStage) {
        // Create UI elements
        TextField arg1Field = new TextField();
        arg1Field.setPromptText("Enter first argument");

        TextField arg2Field = new TextField();
        arg2Field.setPromptText("Enter second argument");

        Button executeButton = new Button("Execute");
        prevPageButton = new Button("⬅️ Previous");
        nextPageButton = new Button("Next ➡️");

        webView = new WebView();

        // Set button action for executing the function
        executeButton.setOnAction(event -> {
            String arg1 = arg1Field.getText();
            String arg2 = arg2Field.getText();
            String result = myFunctionClass.myFunction(arg1, arg2);

            // Highlight "ab" in arg2 if present
            String highlightedArg2 = arg2.replace("ab", "<span style='background-color: yellow;'>ab</span>");

            // Prepare the result with links
            results.clear();
            for (int i = 0; i < 15; i++) {
                String arg2Content = "<div style='height: 200px; overflow-y: auto;'>" + highlightedArg2 + "</div>";
                results.add(result.replace(arg2, "") + arg2Content);
            }
            currentPage = 0;
            displayResults();
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

        // Arrange elements in a layout
        HBox paginationBox = new HBox(10, prevPageButton, nextPageButton);
        VBox layout = new VBox(10, arg1Field, arg2Field, executeButton, webView, paginationBox);
        layout.setPadding(new javafx.geometry.Insets(10));

        // Create and set the scene
        Scene scene = new Scene(layout, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("My JavaFX App");
        primaryStage.show();
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
        nextPageButton.setDisable((currentPage + 1) * PAGE_SIZE >= results.size());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
