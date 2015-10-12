package de.citec.sc.query;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class PredicateQueryProcessor implements QueryProcessor {

    private String entityIndexDirectory = "/predicatesindex/";
    private StandardAnalyzer analyzer;
    private Directory entityIndex;

    public PredicateQueryProcessor(String indexDirectory) {
        initIndexDirectory(indexDirectory);
    }

    public PredicateQueryProcessor() {
        initIndexDirectory("luceneIndex");
    }

    @Override
    public List<String> getMatches(String s) {
        // TODO Auto-generated method stub

        List<String> result = getInstances(s, "label");
//        List<String> resultSyn = getInstances(s, "synonyms");
//        List<String> resultHyp = getInstances(s, "hypernyms");

//        for (String r : resultSyn) {
//            if (!result.contains(r)) {
//                result.add(r);
//            }
//        }
//        for (String r : resultHyp) {
//            if (!result.contains(r)) {
//                result.add(r);
//            }
//        }

        return result;
    }

    private List<String> getInstances(String queryString, String fieldName) {
        List<String> result = new ArrayList<>();
        try {

            Query q = new QueryParser(fieldName, analyzer).parse(queryString);

            // 3. search
            int hitsPerPage = 1000;
            IndexReader reader = DirectoryReader.open(entityIndex);
            IndexSearcher searcher = new IndexSearcher(reader);
            TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
            searcher.search(q, collector);
            ScoreDoc[] hits = collector.topDocs().scoreDocs;

			// 4. display results
            //System.out.println("Found " + hits.length + " hits.");
            for (int i = 0; i < hits.length; ++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);

                if (result == null) {
                    result = new ArrayList<String>();
                }
                if (!result.contains(d.get("URI"))) {
                    result.add(d.get("URI"));
                }
            }

            reader.close();

        } catch (Exception e) {

        }

        return result;
    }

    

    @Override
    public List<Instance> getTopMatches(String label, int k) {
        List<Instance> result = new ArrayList<>();
        
        try {

            //Query q = new QueryParser("label", analyzer).parse(label);
            Query q = new TermQuery(new Term("label", label));

            // 3. search
            int hitsPerPage = 1000;
            IndexReader reader = DirectoryReader.open(entityIndex);
            IndexSearcher searcher = new IndexSearcher(reader);
            TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
            searcher.search(q, collector);
            ScoreDoc[] hits = collector.topDocs().scoreDocs;

			// 4. display results
            //System.out.println("Found " + hits.length + " hits.");
            for (int i = 0; i < hits.length; ++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                
                Instance i1 = new Instance(d.get("URI"), 0);
                
                if (!result.contains(i1)) {
                    result.add(i1);
                }
            }

            reader.close();

        } catch (Exception e) {

        }
        
        Collections.sort(result);
        
        if(result.size()>k){
            result = result.subList(0, k);
        }
        return result;
    }

    @Override
    public void initIndexDirectory(String indexDirectory) {
        try {
            entityIndexDirectory = indexDirectory + "/predicatesindex/";

            analyzer = new StandardAnalyzer();
            entityIndex = FSDirectory.open(Paths.get(entityIndexDirectory));
        } catch (Exception e) {
            System.err.println("Problem with initializing InstanceQueryProcessor\n" + e.getMessage());
        }
    }

}
