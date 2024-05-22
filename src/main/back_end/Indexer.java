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
            System.out.println("Number Of Documents That I Have: " + counter);//
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

        w.close();

        return documents;
    }

    public static List<String[]> searchIntexer(int numberOfHits, Directory index, String query, String field, Analyzer analyzer) throws ParseException, IOException {
        int hitsPerPage = numberOfHits;//909;
        Query q = new QueryParser(field, analyzer).parse(query);
//        if(field.equals("all")){
//            searchIntexer(numberOfHits, index, query, "full_name", analyzer);
//            searchIntexer(numberOfHits, index, query, "institution", analyzer);
//            searchIntexer(numberOfHits, index, query, "year", analyzer);
//            searchIntexer(numberOfHits, index, query, "title", analyzer);
//            searchIntexer(numberOfHits, index, query, "abstract", analyzer);
//            searchIntexer(numberOfHits, index, query, "full_text", analyzer);
//        }

        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs docs = searcher.search(q, hitsPerPage);
        ScoreDoc[] hits = docs.scoreDocs;

        List<String[]> docs_result = new ArrayList<>();

        // print the hits
        System.out.println("Found " + hits.length + " hits.\n");
        for(int i=0;i<hits.length;++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            //docs_result.add(d);

            // String hit_num
            String author = d.get("full_name");
            String institution = d.get("institution");
            String year = d.get("year");
            String title = d.get("title");
            String abstr = d.get("abstract");
            String papper = d.get("full_text");
            String[] doc_result = {Integer.toString(i + 1), author, institution, year, title, abstr, papper};
            docs_result.add(doc_result);

            System.out.println((i + 1) + ". " + d.get("source_id"));
            System.out.println("-> " + author);
            System.out.println("-> " + institution);
            System.out.println("-> " + year);
            System.out.println("-> " + title);
            System.out.println("-> " + abstr.substring(0, 60) + " ...");
            System.out.println("-> " + papper.substring(0, 60) + " ...");
            System.out.println("__________________________________________________________________");
        }

        return docs_result;
    }

    public List<String> myFunction(String arg1, String arg2) {
        List<String> results = new ArrayList<>();
        for (int i = 0; i < 12; i++) { // Just an example to return multiple results
            results.add("Result " + (i+1) + ": " + arg1 + arg2);
        }
        return results;
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
        //w.close();

        //Search the Index
        String query = "Bregman"; // write your String_Query HERE
        String field = "title"; // write your String_Field HERE
        searchIntexer(documents.size(), index, query, field, analyzer);
    }
}
