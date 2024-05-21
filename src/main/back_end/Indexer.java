package back_end;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.math.NumberUtils.isNumber;

public class Indexer {
    private static Document addDoc(IndexWriter w, String[] csv_line_value) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("source_id", csv_line_value[0], Field.Store.YES));
        doc.add(new TextField("full_name", csv_line_value[1], Field.Store.YES));
        doc.add(new TextField("institution", csv_line_value[2], Field.Store.YES));
        doc.add(new TextField("year", csv_line_value[3], Field.Store.YES));
        doc.add(new TextField("title", csv_line_value[4], Field.Store.YES));
        doc.add(new TextField("abstract", csv_line_value[5], Field.Store.YES));
        doc.add(new TextField("full_text", csv_line_value[6], Field.Store.YES));
        w.addDocument(doc);
        return(doc);
    }

    public static List<Document> makeIntexer(IndexWriter w, String csv_file) throws IOException {
        List<Document> documents = new ArrayList<Document>();
        int counter = 0;//

        try (CSVReader reader = new CSVReader(new FileReader(csv_file))) {
            String[] csv_line;
            while ((csv_line = reader.readNext()) != null) {
//                System.out.println("HELOOOOOOO " + csv_line[0]);
//                System.out.println("HELOOOOOOO " + csv_line[1]);
//                System.out.println("HELOOOOOOO " + csv_line[2]);
//                System.out.println("HELOOOOOOO " + csv_line[3]);
//                System.out.println("HELOOOOOOO " + csv_line[4]);
//                System.out.println("HELOOOOOOO " + csv_line[5]);
//                System.out.println("HELOOOOOOO " + csv_line[6]);
                if (csv_line.length == 7 && isNumber(csv_line[0])) {
                    documents.add(addDoc(w, csv_line));
                    counter++;//
                }
            }
            System.out.println("HIIIIIIIIIIIIIII " + counter);//
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

        return documents;
    }

    public static List<Document> searchIntexer(Directory index, String query, String field, Analyzer analyzer) throws ParseException, IOException {
        int hitsPerPage = 10;
        Query q = new QueryParser(field, analyzer).parse(query);

        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs docs = searcher.search(q, hitsPerPage);
        ScoreDoc[] hits = docs.scoreDocs;

        List<Document> docs_result = new ArrayList<Document>();

        // print the hits
        System.out.println("Found " + hits.length + " hits.\n");
        for(int i=0;i<hits.length;++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            docs_result.add(d);
            System.out.println((i + 1) + ". " + d.get("source_id"));
            System.out.println("-> " + d.get("full_name"));
            System.out.println("-> " + d.get("institution"));
            System.out.println("-> " + d.get("year"));
            System.out.println("-> " + d.get("title"));
            System.out.println("-> " + d.get("abstract").substring(0, 60) + " ...");
            System.out.println("-> " + d.get("full_text").substring(0, 60) + " ...");

            System.out.println("__________________________________________________________________");
        }

        return docs_result;
    }

    public String myFunction(String arg1, String arg2) {
        return "Result: " + arg1 + " " + arg2;
    }

    public static void main(String[] args) throws IOException, ParseException {
//        String corpus = "C:\\Users\\user\\IdeaProjects\\Information-Retrieval\\src\\main\\corpus\\dummy.txt";
//        String separator_column = "\\|\\|\\|";
//        String separator_line = ";!.";
//        ArrayList<String[]> rows = new ArrayList<>();
//
//        try (BufferedReader reader = new BufferedReader(new FileReader(corpus))){
//            String line;
//            boolean separatorFound = false;
//
//            while ((line = reader.readLine()) != null && !separatorFound){
//                System.out.println(line);
//                String[] column_values = line.split(separator_column);
//
//                for (String value : column_values){
//                    System.out.println(value);
//                    if(value.equals(separator_line)) {
//                        rows.add(column_values);
//
//                    } else {
//                        rows.add(column_values);
//                    }
//
//                }
//            }
//        }

        String csvFile = "C:\\Users\\user\\IdeaProjects\\Information-Retrieval\\src\\main\\corpus\\corpus.csv";

        StandardAnalyzer analyzer = new StandardAnalyzer();
        Directory index = new ByteBuffersDirectory();

        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        IndexWriter w = new IndexWriter(index, config);

        //Create the Index
        List<Document> documents = makeIntexer(w, csvFile);
        w.close();

        //Search the Index
        String query = "Bregman"; // write your String_Query HERE
        String field = "title"; // write your String_Field HERE
        searchIntexer(index, query, field, analyzer);
    }
}
