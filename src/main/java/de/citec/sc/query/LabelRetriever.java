package de.citec.sc.query;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.RAMDirectory;

public abstract class LabelRetriever {

    protected final Comparator<Instance> pageRankComparator = new Comparator<Instance>() {

        @Override
        public int compare(Instance s1, Instance s2) {

            if (s1.getPageRank() > s2.getPageRank()) {
                return -1;
            } else if (s1.getPageRank() < s2.getPageRank()) {
                return 1;
            }
            return 0;
        }
    };

    protected final Comparator<Instance> frequencyComparator = new Comparator<Instance>() {

        @Override
        public int compare(Instance s1, Instance s2) {

            if (s1.getFreq() > s2.getFreq()) {
                return -1;
            } else if (s1.getFreq() < s2.getFreq()) {
                return 1;
            }

            return 0;
        }
    };

    protected Comparator<Instance> comparator = frequencyComparator;

    /**
     * returns top k URIs that match the searchTerm directly (not partial match)
     * returns top k URIs in sorted order by frequency if frequency isn't
     * available, sorting is done based on string similarity between searchTerm
     * and retrieved URI
     *
     * @return Set<Instace>
     * @param searchTerm
     * @param queryPart
     * @param retrievalPart
     * @param k
     */
    protected Set<Instance> getDirectMatches(String searchTerm, String searchField, String returnField, int k, Directory indexDirectory) {
        Set<Instance> result = new LinkedHashSet<>();

        try {

            searchTerm = searchTerm.toLowerCase();
            //Query q = new QueryParser("label", analyzer).parse(label);
            Query q = new TermQuery(new Term(searchField, searchTerm));

            // 3. search
            int hitsPerPage = 1000;
            IndexReader reader = DirectoryReader.open(indexDirectory);
            IndexSearcher searcher = new IndexSearcher(reader);
            TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
            searcher.search(q, collector);
            ScoreDoc[] hits = collector.topDocs().scoreDocs;
//            TopDocs topdocs = searcher.search(q, k);
//            ScoreDoc[] hits = topdocs.scoreDocs;

            // 4. display results
            //System.out.println("Found " + hits.length + " hits.");
            for (int i = 0; i < hits.length; ++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                float score = hits[i].score;

                String p = d.get(returnField);
                double freq = 0;
                if (d.get("freq") != null) {
                    freq = Double.parseDouble(d.get("freq"));
                } else {
//                    String pAsString = p
//                            .replace("http://dbpedia.org/resource/", "")
//                            .replace("http://dbpedia.org/ontology/", "")
//                            .replace("http://dbpedia.org/property/", "")
//                            .replace("_", " ");
//
//                    //value between 0 and 1
//                    double similarity = stringSimilarity(searchField, pAsString);

                    freq = score;// * similarity;
                }

                String onProperty = "";
                String pos = "";
                double rank = 0;

                //this part only for MATOLL lexicon
                if (d.get("POS") != null) {
                    pos = d.get("POS");
                }
                if (d.get("onProperty") != null) {
                    onProperty = d.get("onProperty");
                }
                if (d.get("rank") != null) {
                    rank = Double.parseDouble(d.get("rank"));
                }

                Instance i1 = new Instance(p, freq);
                i1.setOnProperty(onProperty);
                i1.setPos(pos);
                i1.setPageRank(rank);

                if (freq > 0) {
                    result.add(i1);
                }

            }

            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Instance> sorted = new ArrayList<>(result);
        Collections.sort(sorted, comparator);

        if (sorted.size() > k) {
            sorted = sorted.subList(0, k);
        }

        result.clear();
        result.addAll(sorted);

        return result;
    }

    /**
     * returns top k URIs that match the searchTerm partially and directly
     * returns top k URIs in sorted order by frequency if frequency isn't
     * available, sorting is done based on string similarity between searchTerm
     * and retrieved URI
     *
     * @return Set<Instace>
     * @param searchTerm
     * @param queryPart
     * @param retrievalPart
     * @param k
     */
    protected Set<Instance> getPartialMatches(String searchTerm, String searchField, String returnField, int k, Directory indexDirectory, StandardAnalyzer analyzer) {
        Set<Instance> result = new LinkedHashSet<>();

        try {

            searchTerm = searchTerm.toLowerCase();
            Query q = new QueryParser(searchField, analyzer).parse(searchTerm);

            // 3. search
            int hitsPerPage = 1000;
            IndexReader reader = DirectoryReader.open(indexDirectory);
            IndexSearcher searcher = new IndexSearcher(reader);
            TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
            searcher.search(q, collector);
            ScoreDoc[] hits = collector.topDocs().scoreDocs;
//            TopDocs topdocs = searcher.search(q, k);
//            ScoreDoc[] hits = topdocs.scoreDocs;

            // 4. display results
            //System.out.println("Found " + hits.length + " hits.");
            for (int i = 0; i < hits.length; ++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                float score = hits[i].score;

                String p = d.get(returnField);
                double freq = 0;
                if (d.get("freq") != null) {
                    freq = Double.parseDouble(d.get("freq"));
                } else {
//                    String pAsString = p
//                            .replace("http://dbpedia.org/resource/", "")
//                            .replace("http://dbpedia.org/ontology/", "")
//                            .replace("http://dbpedia.org/property/", "")
//                            .replace("_", " ");
//
//                    //value between 0 and 1
//                    double similarity = stringSimilarity(searchField, pAsString);

                    freq = score;// * similarity;
                }

                String onProperty = "";
                String pos = "";
                double rank = 0;

                //this part only for MATOLL lexicon
                if (d.get("POS") != null) {
                    pos = d.get("POS");
                }
                if (d.get("onProperty") != null) {
                    onProperty = d.get("onProperty");
                }
                if (d.get("rank") != null) {
                    rank = Double.parseDouble(d.get("rank"));
                }

                Instance i1 = new Instance(p, freq);
                i1.setOnProperty(onProperty);
                i1.setPos(pos);
                i1.setPageRank(rank);

                if (freq > 0) {
                    result.add(i1);
                }
            }

            reader.close();

        } catch (Exception e) {
            //e.printStackTrace();
        }

        List<Instance> sorted = new ArrayList<>(result);
        Collections.sort(sorted, comparator);

        if (sorted.size() > k) {
            sorted = sorted.subList(0, k);
        }

        result.clear();
        result.addAll(sorted);

        return result;
    }

    private int levenshteinDistance(String s, String t) {
        // degenerate cases
        if (s == t) {
            return 0;
        }
        if (s.length() == 0) {
            return t.length();
        }
        if (t.length() == 0) {
            return s.length();
        }

        // create two work vectors of integer distances
        int[] v0 = new int[t.length() + 1];
        int[] v1 = new int[t.length() + 1];

        // initialize v0 (the previous row of distances)
        // this row is A[0][i]: edit distance for an empty s
        // the distance is just the number of characters to delete from t
        for (int i = 0; i < v0.length; i++) {
            v0[i] = i;
        }

        for (int i = 0; i < s.length(); i++) {
            // calculate v1 (current row distances) from the previous row v0

            // first element of v1 is A[i+1][0]
            //   edit distance is delete (i+1) chars from s to match empty t
            v1[0] = i + 1;

            // use formula to fill in the rest of the row
            for (int j = 0; j < t.length(); j++) {
                int cost;
                if (s.charAt(i) == t.charAt(j)) {
                    cost = 0;
                } else {
                    cost = 1;
                }
                int min1 = Math.min(v1[j] + 1, v0[j + 1] + 1);
                v1[j + 1] = Math.min(min1, v0[j] + cost);

            }

            // copy v1 (current row) to v0 (previous row) for next iteration
            for (int j = 0; j < v0.length; j++) {
                v0[j] = v1[j];
            }
        }

        return v1[t.length()];
    }

    private double stringSimilarity(String f, String s) {
        double similarity = 0;

        int lDistance = levenshteinDistance(f, s);

        int max = Math.max(f.length(), s.length());
        double score = (double) (max - lDistance) / (double) max;

        if (Double.isNaN(score)) {
            return similarity;
        }
        if (Double.isInfinite(score)) {
            return similarity;
        }
        if (score < 0) {
            return similarity;
        }

        return score;
    }

    /**
     * sorts the given Set<String> based on Edit distance prefers ontology
     * predicates over property namespaces
     *
     */
    protected Set<String> sortBySimilarity(Set<String> input, String label, int topK) {

        List<Instance> instances = new ArrayList<>();

        for (String s : input) {

            //preprocess the string
            //remove namespaces
            String preprocessed = "";
            double preference = 1.0;
            if (s.contains("http://dbpedia.org/property/")) {
                preprocessed = s.replace("http://dbpedia.org/property/", "");
            }

            //ontology namespaces are preferred 10.5 times more than property ones, for ranking.
            if (s.contains("http://dbpedia.org/ontology/")) {
                preprocessed = s.replace("http://dbpedia.org/ontology/", "");
                preference = 10.5;
            }

            if (s.contains("http://dbpedia.org/resource/")) {
                preprocessed = s.replace("http://dbpedia.org/resource/", "");
            }

            double similarity = stringSimilarity(preprocessed, label);
            //preference for ranking
            int freq = (int) (similarity * preference * 100);

            Instance i1 = new Instance(s, freq);
            instances.add(i1);
        }

        Collections.sort(instances);

        if (instances.size() > topK) {
            instances = instances.subList(0, topK);
        }

        Set<String> result = new LinkedHashSet<>();
        instances.forEach(i1 -> result.add(i1.getUri()));

        return result;
    }
}
