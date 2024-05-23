package back_end;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

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

        try
        {
            Reader reader = new FileReader(csv_file);
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());

            for (CSVRecord csvRecord : csvParser) {
                int numberOfColumns = csvRecord.size();
                String[] csv_line = new String[numberOfColumns];

                csv_line[0] = csvRecord.get(0);
                csv_line[1] = csvRecord.get(1);
                csv_line[2] = csvRecord.get(2);
                csv_line[3] = csvRecord.get(3);
                csv_line[4] = csvRecord.get(4);
                csv_line[5] = csvRecord.get(5);
                csv_line[6] = csvRecord.get(6);

                documents.add(addDoc(w, csv_line));
                // for debugging purposes
//                System.out.println("HELOOOOOOO " + csv_line[0]);
//                System.out.println("HELOOOOOOO " + csv_line[1]);
//                System.out.println("HELOOOOOOO " + csv_line[2]);
//                System.out.println("HELOOOOOOO " + csv_line[3]);
//                System.out.println("HELOOOOOOO " + csv_line[4]);
//                System.out.println("HELOOOOOOO " + csv_line[5]);
//                System.out.println("HELOOOOOOO " + csv_line[6]);

                counter++;
            }
            csvParser.close();

            System.out.println("Number Of Documents That I Have: " + counter);//
        } catch (IOException e) {
            e.printStackTrace();
        }

        w.close();

        return documents;
    }

    public static List<String[]> searchIntexer(int numberOfHits, Directory index, String query, String field, Analyzer analyzer) throws ParseException, IOException {
        Query q;
        if (field.equals("keywords")) {
            String[] fields = {"full_name", "institution", "year", "title", "abstract", "full_text"};
            // initialize searching in the index by only a keyword (it is searching in all fields)
            q = new MultiFieldQueryParser(fields, analyzer).parse(query);
        } else {
            // initialize searching in the index by specific field and query
            q = new QueryParser(field, analyzer).parse(query);
        }

        List<String[]> docs_result = printDocs(numberOfHits, index, q);

        return docs_result;
    }

    private static List<String[]> printDocs(int numberOfHits, Directory index, Query q) throws IOException {
        int hitsPerPage = numberOfHits;//2029;

        // create the indexReader (features: the object that is responsible for reading documents to the index)
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);

        // actual searching
        TopDocs docs = searcher.search(q, hitsPerPage);
        ScoreDoc[] hits = docs.scoreDocs;

        List<String[]> docs_result = new ArrayList<>();

        // print the hits
        System.out.println("Found " + hits.length + " hits.\n");
        for(int i=0;i<hits.length;++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);

            String source_id = d.get("source_id");//
            String author = d.get("full_name");
            String institution = d.get("institution");
            String year = d.get("year");
            String title = d.get("title");
            String abstr = StringEscapeUtils.escapeHtml4(d.get("abstract"));
            String papper = StringEscapeUtils.escapeHtml4(d.get("full_text"));
            String[] doc_result = {Integer.toString(i + 1), author, institution, year, title, abstr, papper, source_id};
            docs_result.add(doc_result);

            // for debugging purposes
            System.out.println((i + 1) + ". " + d.get("source_id"));
            System.out.println("-> " + author);
            System.out.println("-> " + institution);
            System.out.println("-> " + year);
            System.out.println("-> " + title);
            if (abstr.length() >= 60) {
                System.out.println("-> " + abstr.substring(0, 60) + " ...");
            } else {
                System.out.println("-> " + abstr);
            }
            if (papper.length() >= 60) {
                System.out.println("-> " + papper.substring(0, 60) + " ...");
            } else {
                System.out.println("-> " + papper);
            }
            System.out.println("__________________________________________________________________");
        }
        return docs_result;
    }

    public static void main(String[] args) throws IOException, ParseException {

        // main testing example (without GUI)

        String csvFile = "C:\\Users\\user\\IdeaProjects\\Information-Retrieval\\src\\main\\corpus\\corpus.csv";

        // create the analyzer (features: tokenization, Lowercasing, Stop Words, Stemming)
        StandardAnalyzer analyzer = new StandardAnalyzer();

        // initialize the index (features: where to save it: in RAM)
        Directory index = new ByteBuffersDirectory();

        // create the config (features: special configurations for the indexWriter in the next step)
        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        // initialize the indexWriter (features: the object that is responsible for writing documents to the index)
        IndexWriter w = new IndexWriter(index, config);

        // adding documents to the indexer
        List<Document> documents = makeIntexer(w, csvFile);

        // testing data
        String query = "2018"; // write your String_Query HERE
        String field = "keywords"; // write your String_Field HERE

        // searching
        searchIntexer(documents.size(), index, query, field, analyzer);
    }
}
