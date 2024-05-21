module information.retrieval.project {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.apache.lucene.core; // Use the inferred module name
    requires org.apache.lucene.analysis.common;
    requires org.apache.lucene.queryparser;
    requires org.apache.lucene.highlighter;
    requires org.apache.lucene.grouping;
    requires com.opencsv;
    requires org.apache.commons.csv;
    requires org.apache.commons.lang3;
    requires javafx.web;

    opens front_end to javafx.fxml;
    exports front_end;
    exports back_end;
    opens back_end to javafx.fxml;
}
