package de.citec.sc.query;

import de.citec.sc.wordNet.WordNetAnalyzer;
import edu.stanford.nlp.util.ArraySet;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IndexSearch {

    /**
     * <p>
     * get set of DBpedia predicates for given label </p>
     * <p>
     * if useWordNet is set to true, then retrieve derivational words from
     * Wordnet for the given label and combine all results </p>
     *
     * @return List of DBpedia predicates
     * @param label to search the index
     * @param useWordNet
     */
    public Set<String> getPredicates(String label, boolean useWordNet) {

        label = label.toLowerCase();
        QueryProcessor queryProcessor = new PredicateQueryProcessor();
        Set<String> predicates = new ArraySet<String>();

        WordNetAnalyzer analyzer = new WordNetAnalyzer("src/main/resources/WordNet-3.0/dict");
        Set<String> words = new ArraySet<>();

        if (useWordNet) {
            words = analyzer.getDerivationalWords(label, "\t");
        } else {
            words.add(label);
        }

        for (String w : words) {
            List<String> instances = queryProcessor.getMatches(w);

            for (String c : instances) {
                // check if the first letter start with Lowercase character
                String firstLetter = "";

                if (c.contains("http://dbpedia.org/ontology/")) {
                    firstLetter = c.replace("http://dbpedia.org/ontology/", "").substring(0, 1);
                }
                if (c.contains("http://dbpedia.org/property/")) {
                    firstLetter = c.replace("http://dbpedia.org/property/", "").substring(0, 1);
                }

                if (firstLetter.equals(firstLetter.toLowerCase())) {
                    if (!predicates.contains(c)) {
                        predicates.add(c);
                    }
                }
            }
        }

        return predicates;
    }

    /**
     * <p>
     * get list of DBpedia classes for given label combined with Wordnet </p>
     * <p>
     * if useWordNet is set to true, then retrieve derivational words from
     * Wordnet for the given label and combine all results </p>
     *
     * @return List of DBpedia classes
     * @param label to search the index
     * @param useWordNet
     */
    public Set<String> getClasses(String label, boolean useWordNet) {
        label = label.toLowerCase();

        QueryProcessor queryProcessor = new PredicateQueryProcessor();

        WordNetAnalyzer analyzer = new WordNetAnalyzer("src/main/resources/WordNet-3.0/dict");
        Set<String> words = new ArraySet<>();

        if (useWordNet) {
            words = analyzer.getDerivationalWords(label, "\t");
        } else {
            words.add(label);
        }

        Set<String> classes = new ArraySet<String>();

        for (String w : words) {
            List<String> instances = queryProcessor.getMatches(w);

            for (String c : instances) {
                // check if the first letter start with Uppercase character
                String firstLetter = c.replace("http://dbpedia.org/ontology/", "").substring(0, 1);
                if (firstLetter.equals(firstLetter.toUpperCase())) {
                    if (!classes.contains(c)) {
                        classes.add("http://www.w3.org/1999/02/22-rdf-syntax-ns#type###" + c);
                    }
                }
            }
        }

        return classes;
    }

    /**
     * <p>
     * get list of DBpedia resources for given label </p>
     *
     * @return List of DBpedia resources from AnchorText
     * @param label to search the index
     */
    public Set<String> getEntitiesFromAnchorText(String label) {

        label = label.toLowerCase();

        QueryProcessor queryProcessor = new AnchorTextQueryProcessor();

        List<Instance> instances = queryProcessor.getTopMatches(label, 10);

        Set<String> result = new ArraySet<>();
        for (Instance c : instances) {
            // check if the first letter start with Lowercase character
            result.add(c.getUri());
        }

        return result;
    }

    /**
     * <p>
     * get set of DBpedia restriction classes (predicate###resource) for given
     * label </p>
     * E.g. catholic -> dbo:religion###dbr:Catholic_church E.g. female ->
     * dbo:gender###dbr:Female
     *
     * @return List of 10 DBpedia predicates and resources from MATOLL
     * (restriction class)
     * @param label to search the index
     * @param useWordNet
     */
    public Set<String> getRestrictionClassesFromMATOLL(String label, boolean useWordNet) {

        label = label.toLowerCase();

        QueryProcessor queryProcessor = new MATOLLQueryProcessor();

        WordNetAnalyzer analyzer = new WordNetAnalyzer("src/main/resources/WordNet-3.0/dict");
        Set<String> words = new ArraySet<>();

        if (useWordNet) {
            words = analyzer.getDerivationalWords(label, "\t");
        } else {
            words.add(label);
        }

        Set<String> result = new ArraySet<String>();

        for (String w : words) {

            List<Instance> instances = queryProcessor.getTopMatches(w, 10);

            for (Instance c : instances) {
                if (c.getPos().equals("http://www.lexinfo.net/ontology/2.0/lexinfo#adjective")) {
                    if (!c.getOnProperty().equals("")) {
                        result.add(c.getOnProperty() + "###" + c.getUri());
                    }
                }
            }
        }

        return result;
    }

    /**
     * <p>
     * get list of DBpedia predicates for given label </p>
     *
     * @return List of DBpedia predicates from MATOLL
     * @param label to search the index
     */
    public Set<String> getPredicatesFromMATOLL(String label, boolean useWordNet) {

        label = label.toLowerCase();

        QueryProcessor queryProcessor = new MATOLLQueryProcessor();
        Set<String> predicates = new ArraySet<String>();

        WordNetAnalyzer analyzer = new WordNetAnalyzer("src/main/resources/WordNet-3.0/dict");
        Set<String> words = new ArraySet<>();

        if (useWordNet) {
            words = analyzer.getDerivationalWords(label, "\t");
        } else {
            words.add(label);
        }

        Set<Instance> instances = new ArraySet<>();

        for (String w : words) {
            List<Instance> inst = queryProcessor.getTopMatches(label, 10);
            instances.addAll(inst);
        }

        for (Instance i : instances) {
            // check if the first letter start with Lowercase character
            if (i.getOnProperty().equals("")) {
                
                if (i.getUri().contains("http://dbpedia.org/ontology/")) {
                    
                    String c = i.getUri();
                    String firstLetter = c.replace("http://dbpedia.org/ontology/", "").substring(0, 1);
                    if (firstLetter.equals(firstLetter.toLowerCase())) {
                        if (!predicates.contains(c)) {
                            predicates.add(c);
                        }
                    }
                }
                if (i.getUri().contains("http://dbpedia.org/property/")) {
                    
                    String c = i.getUri();
                    predicates.add(c);
                }
            }

        }

        return predicates;
    }

    /**
     * <p>
     * get list of DBpedia predicates for given label combining Wordnet MATOLL
     * and DBpedia Ontology</p>
     *
     * @return List of DBpedia predicates from MATOLL
     * @param label to search the index
     */
    public Set<String> getAllPredicates(String label) {
        Set<String> uris = getPredicates(label, true);

        uris.addAll(getPredicatesFromMATOLL(label, true));

        return uris;
    }
}
